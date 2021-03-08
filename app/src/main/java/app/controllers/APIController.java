package app.controllers;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import app.App;
import app.DbConnection;
import app.Validator;
import app.objects.*;
import app.util.ViewUtil;
import app.util.emailController;
import spark.*;

public class APIController {

    // SEE https://sparkjava.com/documentation#path-groups FOR HELP

    // thread safe - no DB interaction
    static Validator v = App.getInstance().getValidator();
    static emailController e = new emailController();

    // form sent from front-end to back-end to create host
    public static Route createHost = (Request request, Response response) -> {
        System.out.println("createHost API endpoint recognized request \n");
        DbConnection db = App.getInstance().getDbConnection();

        // collect form attributes, validate attributes and ensure email address is unique
        String f_name = request.queryParams("hostFName");
        String l_name = request.queryParams("hostLName");
        String e_address = request.queryParams("hostEmail");
        String ip_address = request.ip();
        if (!v.nameIsValid(f_name) ||
            !v.nameIsValid(l_name) ||
            !v.eAddressIsValid(e_address) ||
            db.emailExists(e_address)) {
            return ViewUtil.notFound;
        }
        System.out.println("Notice: createHost fields collected and validated");
        Host host = db.createHost(f_name, l_name, ip_address, e_address);
        if (v.isHostValid(host)) {
            request.session(true);
            request.session().attribute("host", host);
            Map<String, Object> model = new HashMap<>();
            model.put("hostCode", host.getHostCode());
            return ViewUtil.render(request, model, "/velocity/get-code.vm");
        } else {
            System.out.println("Error: Host creation failed");
            return ViewUtil.notFound;
        }
        // (broken) send email containing host-code to new host
        //e.sendEmail(Email, "Resmodus: Here's your host code", hostCode);    

    };

    // form sent by host to create an event
    public static Route createEvent = (Request request, Response response) -> {
        System.out.println("createEvent API endpoint recognized request \n");
        DbConnection db = App.getInstance().getDbConnection();
        //start session
        request.session(true);
        //return not found if session is new
        if (request.session().isNew()) {
            return ViewUtil.notFound;
        }
        //initialise event and input 
        Event event = null;
        Host host = request.session().attribute("host");
        String title = request.queryParams("eventName");
        String description = request.queryParams("eventDescription");
        String type = request.queryParams("eventType");
        String templateCode = request.queryParams("eventTemplate");
        // timestamp in format yyyy-[m]m-[d]d hh:mm:ss[.f...]
        Timestamp startTime = Timestamp.valueOf(request.queryParams("startTime"));
        Timestamp endTime = Timestamp.valueOf(request.queryParams("endTime"));
        Timestamp current = new Timestamp(System.currentTimeMillis());

        //validate input before interact with database
        if (v.eventTitleIsValid(title) &&
            v.eventDescriptionIsValid(description) &&
            v.eventTypeIsValid(type) &&
            v.templateCodeIsValid(templateCode) &&
            startTime.compareTo(endTime) < 0 &&
            endTime.compareTo(current) < 0
            ) {
            Template template = db.getTemplateByCode(templateCode);
            event = db.createEvent(host.getHostID(), template.getTemplateID(), title, description, type, startTime, endTime);
        }
        //return host event page if event is created
        if (v.isEventValid(event)) {
            request.session().attribute("event", event);
            Map<String, Object> model = new HashMap<>();
            model.put("eventTitle", event.getTitle());
            model.put("eventDescription", event.getDescription());
            model.put("eventCode", event.getEventCode());
            return ViewUtil.render(request, model, "/velocity/host-event.vm");
        }
        //return not found if event is not created or input is not valid
        return ViewUtil.notFound;
    };

    // form sent by participant to join an event
    public static Route joinEvent = (Request request, Response response) -> {
        System.out.println("joinEvent API endpoint recognized request \n"); 
        DbConnection db = App.getInstance().getDbConnection();
        // Get form attributes, ensure attributes are valid
        String FName = request.queryParams("participantFName");
        String LName = request.queryParams("participantLName");
        String eventCode = request.queryParams("eventCode");
        if (!v.nameIsValid(FName) ||
            !v.nameIsValid(LName) ||
            !v.eventCodeIsValid(eventCode)
            ) {
                return ViewUtil.notFound;
        }
        // create Participant object in DB -> link to event
        Participant participant = db.createParticipant(request.ip(),FName,LName);
        Event event = db.getEventByCode(request.queryParams("participantCode"));
        db.addParticipantToEvent(participant.getParticipantID(), event.getEventID());
        request.session(true);
        request.session().attribute("participant", participant);
        // redirect participant to event
        if (v.isEventValid(event)) {
            request.session().attribute("event", event);
            Map<String, Object> model = new HashMap<>();
            model.put("eventTitle", event.getTitle());
            model.put("eventDescription", event.getDescription());
            return ViewUtil.render(request, model, "/velocity/participant-event.vm");
        }
        //return notfound if event is not found
        return ViewUtil.notFound;
    };

    // form sent by participant (in event) to create an instance of feedback
    public static Route createFeedback = (Request request, Response response) -> {
        System.out.println("createFeedback API endpoint recognized request \n");

        return null;
    };

    /** Login host to host homepage */
    public static Route hostLogin = (Request request, Response response) -> {
        System.out.println("hostLogin API endpoint recognized request \n");
        DbConnection db = App.getInstance().getDbConnection();
        //initialise host
        Host host = null;
        //start session
        request.session(true);
        if (request.session().isNew()) {
            String hostCode = request.queryParams("hostCode");
            //validate input before interact with database
            if (v.hostCodeIsValid(hostCode)) {
                host = db.getHostByCode(hostCode);
            }
            request.session().attribute("host", host);
        } else {
            host = request.session().attribute("host");
        }
        //return host homepage if host is found
        if (v.isHostValid(host)) {
            Map<String, Object> model = new HashMap<>();
            model.put("fName", host.getFName());
            model.put("lName", host.getLName());
            return ViewUtil.render(request, model, "/velocity/host-home.vm");
        }
        //return notfound if host is not found or hostcode is not valid
        System.out.println("Host not found!");
        return ViewUtil.notFound;
    };
}
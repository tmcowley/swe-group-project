package app.controllers;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import app.App;
import app.DbConnection;
import app.Validator;
import app.objects.*;
import app.sentimentanalysis.SentimentAnalyser;
import app.util.ViewUtil;
import app.util.emailController;
import spark.*;

public class APIController {

    // SEE https://sparkjava.com/documentation#path-groups FOR HELP

    // thread safe - no DB interaction
    static Validator v = App.getInstance().getValidator();
    static emailController e = new emailController();

    // form sent from front-end to back-end to create host\
    /**
     * This method creates a new host when a new user signs up as a host
     */
    public static Route createHost = (Request request, Response response) -> {
        System.out.println("\nNotice: createHost API endpoint recognized request \n");
        DbConnection db = App.getInstance().getDbConnection();

        // collect form attributes, validate attributes
        // ensure email address is unique
        String f_name = request.queryParams("hostFName");
        String l_name = request.queryParams("hostLName");
        String e_address = request.queryParams("hostEmail");
        String ip_address = null; // request.ip();
        if (!v.nameIsValid(f_name) || !v.nameIsValid(l_name) || !v.eAddressIsValid(e_address)
                || db.emailExists(e_address)) {
            // return ViewUtil.notFound; -> TODO
            System.out.println("Error: field invalid or email exists");

            Map<String, Object> model = new HashMap<>();
            // model.put("errorMessage", "Error: field invalid or email exists");

            return ViewUtil.render(request, model, "/velocity/host-event.vm");
        }
        // System.out.println("Notice: createHost fields collected and validated");

        Host host = db.createHost(f_name, l_name, ip_address, e_address);
        if (v.isHostValid(host)) {
            // host is valid
            request.session(true);
            request.session().attribute("host", host);
            Map<String, Object> model = new HashMap<>();
            model.put("hostCode", host.getHostCode());
            return ViewUtil.render(request, model, "/velocity/get-code.vm");
        } else {
            System.out.println("Error: Created host considered invalid");
            return "Error: Created host considered invalid";
            // return ViewUtil.notFound; -> TODO
        }
        // (broken) send email containing host-code to new host
        // e.sendEmail(Email, "Resmodus: Here's your host code", hostCode);

    };

    /**
     * This method creates and event when a host user requests a new event to be made
     * form sent by host to create an event
     */
    public static Route createEvent = (Request request, Response response) -> {
        System.out.println("\nNotice: createEvent API endpoint recognized request");
        DbConnection db = App.getInstance().getDbConnection();
        // start session
        request.session(true);
        // return not found if session is new
        if (request.session().isNew()) {
            // return ViewUtil.notFound;
            return "Error: Session not found";
        }

        // initialise event and input
        Event event = null;
        Host host = request.session().attribute("host");
        String title = request.queryParams("eventTitle");
        String description = request.queryParams("eventDescription");
        String type = request.queryParams("eventType");
        String templateCode = request.queryParams("eventTemplate");
        // timestamp in format yyyy-[m]m-[d]d hh:mm:ss[.f...]
        Timestamp current = new Timestamp(System.currentTimeMillis());
        Timestamp startTime, endTime;

        // Temporary fix for broken timestamp input
        try {
            startTime = Timestamp.valueOf(request.queryParams("startTime"));
            endTime = Timestamp.valueOf(request.queryParams("endTime"));
        } catch (java.lang.IllegalArgumentException iae) {
            startTime = current;
            endTime = current;
        }

        // validate input before database interaction
        if (!v.eventTitleIsValid(title))
            return "Error: Event title is not valid";
        if (!v.eventDescriptionIsValid(description))
            return "Error: Event description is invalid";
        if (!v.eventTypeIsValid(type))
            return "Error: Event type is invalid";
        // if (startTime.compareTo(endTime) < 0 && endTime.compareTo(current) > 0)
        // return "Error: start and end time not in order";

        if (templateCode.equals("noTemplate")) {
            // create an event without a template
            System.out.println("Notice: no template has been provided");
            event = db.createEvent(host.getHostID(), title, description, type, startTime, endTime);
        } else if (v.templateCodeIsValid(templateCode)) {
            // create an event with a template
            System.out.println("Notice: a template has been provided");
            Template template = db.getTemplateByCode(templateCode);
            event = db.createEvent(host.getHostID(), template.getTemplateID(), title, description, type, startTime,
                    endTime);
        }

        // return host event page if event is created and valid
        if (v.isEventValid(event)) {
            // event is valid
            request.session().attribute("event", event);
            Map<String, Object> model = new HashMap<>();
            model.put("eventTitle", event.getTitle());
            model.put("eventDescription", event.getDescription());
            model.put("eventCode", event.getEventCode());
            return ViewUtil.render(request, model, "/velocity/host-event.vm");
        }
        // return not found if event is not created or input is not valid
        return "Error: event not created or considered invalid - check inputs";
    };

    // form sent by participant to join an event
    /**
     * This method allows a participant to join an ongoing event
     */
    public static Route joinEvent = (Request request, Response response) -> {
        System.out.println("\nNotice: joinEvent API endpoint recognized request \n");
        DbConnection db = App.getInstance().getDbConnection();

        // Get form attributes, ensure attributes are valid
        String f_name = request.queryParams("participantFName");
        String l_name = request.queryParams("participantLName");
        String eventCode = request.queryParams("eventCode");
        if (!v.nameIsValid(f_name) || !v.nameIsValid(l_name) || !v.eventCodeIsValid(eventCode)) {
            return "Error: a given name or event code is invalid";
        }

        if (!db.eventCodeExists(eventCode)) {
            return "Event code does not exist";
        }

        // create Participant object in DB -> link to event
        Participant participant = db.createParticipant(null, f_name, l_name);
        Event event = db.getEventByCode(eventCode);
        db.addParticipantToEvent(participant.getParticipantID(), event.getEventID());
        request.session(true);
        request.session().attribute("participant", participant);

        // redirect participant to event
        if (v.isEventValid(event)) {
            // event is valid
            request.session().attribute("event", event);
            Map<String, Object> model = new HashMap<>();
            model.put("eventTitle", event.getTitle());
            model.put("eventDescription", event.getDescription());
            return ViewUtil.render(request, model, "/velocity/participant-event.vm");
        }

        return "Error: the event was invalid";
    };

    /**
     * this method allows a participant in an event to create an instance of feedback
     * form sent by participant (in event) to create an instance of feedback
     */
    public static Route createFeedback = (Request request, Response response) -> {
        System.out.println("\nNotice: createFeedback API endpoint recognized request \n");
        DbConnection db = App.getInstance().getDbConnection();
        Session session = request.session();

        // start session
        request.session(true);
        // return not found if session is new
        if (request.session().isNew()) {
            // return ViewUtil.notFound;
            return "Error: Session not found";
        }

        // initialise event and input
        Event event = session.attribute("event");
        Participant participant = session.attribute("participant");
        String[] results = { request.queryParams("feedbackData") };
        Float[] weights = { 4f };
        byte[] types = { 0 };
        Boolean[] keys = { false };
        byte[][] sub_weights = new byte[0][0];
        Timestamp current = new Timestamp(System.currentTimeMillis());
        Boolean anonymous = false;
        if (request.queryParams("anon") != null && request.queryParams("anon").equals("Submit Anonymously")) {
            anonymous = true;
        }

        System.out.println("Notice: generating feedback instance");
        Feedback feedback = new Feedback(participant.getParticipantID(), event.getEventID(), results, weights, types,
                keys, sub_weights, anonymous, current);
        SentimentAnalyser.main(feedback);
        if (feedback.getCompound() != null) {
            System.out.println("Notice: SA on feedback successful");
        }

        // return to event page if feedback is created
        if (v.isFeedbackValid(feedback)) {
            System.out.println("Notice: feedback considered valid");
            String[] arrs = new String[feedback.getKey_Results().size()];
            String[] keyResults = (String[]) feedback.getKey_Results().toArray(arrs);
            db.createFeedback(feedback.getParticipantID(), feedback.getEventID(), feedback.getAnonymous(),
                    feedback.getTimestamp(), feedback.getResults(), feedback.getWeights(), feedback.getTypes(),
                    feedback.getKeys(), feedback.getSub_Weights(), feedback.getCompound(), keyResults);

            // TODO: DISPLAY THAT FEEDBACK WAS RECORDED
            request.session().attribute("event", event);
            Map<String, Object> model = new HashMap<>();
            model.put("eventTitle", event.getTitle());
            model.put("eventDescription", event.getDescription());
            return ViewUtil.render(request, model, "/velocity/participant-event.vm");
        }

        // return not found if feedback is not created
        // return ViewUtil.notFound;
        return "Error: feedback considered invalid";
    };

    /**
     * this method Logs in host to host homepage
     */

    public static Route hostLogin = (Request request, Response response) -> {
        System.out.println("\nNotice: hostLogin API endpoint recognized request \n");
        DbConnection db = App.getInstance().getDbConnection();
        // initialise host
        Host host = null;
        // start session
        request.session(true);
        if (request.session().isNew()) {
            String hostCode = request.queryParams("hostCode");
            // validate input before interact with database
            if (v.hostCodeIsValid(hostCode)) {
                host = db.getHostByCode(hostCode);
            }
            request.session().attribute("host", host);
        } else {
            host = request.session().attribute("host");
        }

        // return host homepage if host is found
        if (v.isHostValid(host)) {
            // host is valid
            Map<String, Object> model = new HashMap<>();
            model.put("fName", host.getFName());
            model.put("lName", host.getLName());
            return ViewUtil.render(request, model, "/velocity/host-home.vm");
        }

        System.out.println("Error: Host is invalid");
        // return ViewUtil.notFound;
        return "Error: Host is invalid";
    };
}
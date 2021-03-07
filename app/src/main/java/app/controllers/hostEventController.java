package app.controllers;

import java.util.*;
import java.net.*;
import java.io.*;
import java.sql.Timestamp;
import spark.*;
import app.App;
import app.objects.Event;
import app.objects.Host;
import app.objects.Template;
import app.util.*;

public class hostEventController {
    /** Serve the index page (GET request) */
    public static Route servePage = (Request request, Response response) -> {
        //start session
        request.session(true);
        //return not found if session is new
        if (request.session().isNew()) {
            return ViewUtil.notFound;
        }
        //initialise event and input 
        Event event = null;
        Host host = request.session().attribute("host").getHostID();
        String title = request.queryParams("eventName");
        String description = request.queryParams("eventDescription");
        String type = request.queryParams("eventType");
        String templateCode = request.queryParams("eventTemplate");
        // timestamp in format yyyy-[m]m-[d]d hh:mm:ss[.f...]
        Timestamp startTime = Timestamp.valueOf(request.queryParams("startTime"));
        Timestamp endTime = Timestamp.valueOf(request.queryParams("endTime"));


        //validate input before interact with database
        if (App.getInstance().getValidator().eventTitleIsValid(title)&&
            App.getInstance().getValidator().eventDescriptionIsValid(description)&&
            App.getInstance().getValidator().eventTypeIsValid(type)&&
            App.getInstance().getValidator().templateCodeIsValid(templateCode)&&
            startTime.compareTo(endTime) < 0
            ) {
            Template template = App.getInstance().getDbConnection().getTemplateByCode(templateCode);
            event = App.getInstance().getDbConnection().createEvent(host.getHostID(), template.getTemplateID(), title, description, type, startTime, endTime);
        }
        //return host event page if event is created
        if (App.getInstance().getValidator().isEventValid(event)) {
            request.session().attribute("event", event);
            Map<String, Object> model = new HashMap<>();
            model.put("eventTitle", event.getTitle());
            model.put("eventDescription", event.getDescription());
            model.put("eventCode", event.getEventCode());
            return ViewUtil.render(request, model, "/velocity/host-event.vm");
        }
        //return notfound if event is not created or input is not valid
        return ViewUtil.notFound;
    };

}

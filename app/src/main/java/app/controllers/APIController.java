package app.controllers;

import app.App;
import app.DbConnection;
import app.Validator;
import app.objects.*;
import app.sentimentanalysis.SentimentAnalyser;
import app.util.ViewUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spark.*;
import spark.utils.StringUtils;

public class APIController {

    // thread safe (no DB interaction)
    static Validator v = App.getInstance().getValidator();

    /**
     * creates a new host when a new user signs-up as a host
     * form sent from front-end to back-end to create host
     */
    public static Route createHost = (Request request, Response response) -> {
        System.out.println("\nNotice: createHost API endpoint recognized request");
        DbConnection db = App.getInstance().getDbConnection();

        // collect attributes from API call-point (request)
        String f_name = request.queryParams("hostFName");
        String l_name = request.queryParams("hostLName");
        String e_address = request.queryParams("hostEmail");

        // attribute validation
        if (!v.nameIsValid(f_name) || !v.nameIsValid(l_name)){
            System.out.println("Error:  the name provided is considered invalid");
            request.session().attribute("errorMessageCreate", "Error: name is invalid");
            response.redirect("/host/login");
            return null;
        }
        if (!v.eAddressIsValid(e_address)){
            System.out.println("Error:  the email provided is considered invalid");
            request.session().attribute("errorMessageCreate", "Error: email is invalid");
            response.redirect("/host/login");
            return null;
        }

        // ensure email uniqueness
        if (db.emailExists(e_address)){
            System.out.println("Error:  the email provided is non-unique");
            request.session().attribute("errorMessageCreate", "Error: email is already in use");
            response.redirect("/host/login");
            return null;
        }

        // create host in system
        Host host = db.createHost(f_name, l_name, e_address);

        // ensure host is valid
        if (!v.isHostValid(host)){
            System.out.println("Error:  created host considered invalid");
            request.session().attribute("errorMessageCreate", "Error: created host invalid; please try again");
            response.redirect("/host/login");
            return null;
        }

        // host is valid; store host and host-code in session
        request.session(true);
        request.session().attribute("hostCode", host.getHostCode());
        request.session().attribute("host", host);

        // return host-code
        // links to (GetCodeController.servePage)
        response.redirect("/host/get-code");
        return null;
    };

    /**
     * Creates and event when a host user requests a new event to be made
     * form sent by host to create an event
     */
    public static Route createEvent = (Request request, Response response) -> {
        System.out.println("\nNotice: createEvent API endpoint recognized request");
        DbConnection db = App.getInstance().getDbConnection();

        // get current session; ensure session is live
        request.session(true);
        Session session = request.session();
        if (session.isNew()) {
            System.out.println("Error:  APIController:createEvent session not found");
            response.redirect("/error/401");
            return null;
        }

        // initialise event and input
        Event event = null;
        Host host = session.attribute("host");
        String title = request.queryParams("eventTitle");
        String description = request.queryParams("eventDescription");
        String type = request.queryParams("eventType");
        String templateCode = request.queryParams("eventTemplate");
        // timestamp in format yyyy-[m]m-[d]d hh:mm:ss[.f...]
        Timestamp current = new Timestamp(System.currentTimeMillis());
        Timestamp startTime, endTime;

        // TODO ((Temporary fix for broken timestamp input))
        try {
            startTime = Timestamp.valueOf(request.queryParams("startTime"));
            endTime = Timestamp.valueOf(request.queryParams("endTime"));
        } catch (java.lang.IllegalArgumentException iae) {
            startTime = current;
            endTime = current;
        }

        // validate input before database interaction
        if (!v.eventTitleIsValid(title)) {
            request.session().attribute("errorMessageCreateEvent", "Error: Event title is not valid");
            response.redirect("/host/create-event");
            return null;
        }
        if (!v.eventDescriptionIsValid(description)) {
            request.session().attribute("errorMessageCreateEvent", "Error: Event description is invalid");
            response.redirect("/host/create-event");
            return null;
        }
        if (!v.eventTypeIsValid(type)) {
            request.session().attribute("errorMessageCreateEvent", "Error: Event type is invalid");
            response.redirect("/host/create-event");
            return null;
        }

        /*
        if (startTime.compareTo(endTime) < 0 && endTime.compareTo(current) > 0)
        return "Error: start and end time not in order";
        */

        if (templateCode.equals("noTemplate")) {
            // create an event without a template
            System.out.println("Notice: no template has been provided");
            event = db.createEvent(host.getHostID(), title, description, type, startTime, endTime);
        } else if (v.templateCodeIsValid(templateCode)) {
            // create an event with a template
            System.out.println("Notice: a template has been provided");
            Template template = db.getTemplateByCode(templateCode);
            event = db.createEvent(host.getHostID(), template.getTemplateID(), title, description, type, startTime, endTime);
        }

        if (!v.isEventValid(event)){
            // return not found if event is not created or input is not valid
            request.session().attribute("errorMessageCreateEvent", "Error: event not created or considered invalid - check inputs");
            response.redirect("/host/create-event");
            return null;
        }

        // store event in session for host event page
        session.attribute("event", event);

        // event is valid; return host event page
        // (linking to HostEventController.servePage)
        response.redirect("/event/host/code");
        return null;
    };

    /**
     * joins a participant to an ongoing event
     */
    public static Route joinEvent = (Request request, Response response) -> {
        System.out.println("\nNotice: joinEvent API endpoint recognized request");
        DbConnection db = App.getInstance().getDbConnection();

        // collect form attributes
        String f_name = request.queryParams("participantFName");
        String l_name = request.queryParams("participantLName");
        String eventCode = request.queryParams("eventCode");

        // ensure attribute validity
        if (!v.nameIsValid(f_name) || !v.nameIsValid(l_name) || !v.eventCodeIsValid(eventCode)) {
            System.out.println("Error:  a given name or event code is invalid");
            request.session().attribute("errorMessageJoinEvent", "Error: a given name or event code is invalid");
            response.redirect("/");
            return null;
        }

        // ensure event-code exists in system
        if (!db.eventCodeExists(eventCode)) {
            System.out.println("Error:  event-code does not exist");
            request.session().attribute("errorMessageJoinEvent", "Error: event-code does not exist");
            response.redirect("/");
            return null;
        }

        // create Participant object in DB -> link to event
        Participant participant = db.createParticipant(f_name, l_name);
        Event event = db.getEventByCode(eventCode);
        db.addParticipantToEvent(participant.getParticipantID(), event.getEventID());

        // ensure event is valid
        if (!v.isEventValid(event)){
            System.out.println("Error:  the event was invalid");
            request.session().attribute("errorMessageJoinEvent", "Error: the event was invalid");
            response.redirect("/");
            return null;
        }

        // startup session; add participant and event to session
        request.session(true);
        request.session().attribute("participant", participant);
        request.session().attribute("event", event);

        // event is valid; redirect participant to event page
        // (links to ParticipantEventController.servePage)
        response.redirect("/event/participant/feedback");
        return null;
    };

    /**
     * allows a participant in an event to create an instance of feedback
     * collects form sent by participant (in event) to create an instance of feedback
     */
    public static Route createFeedback = (Request request, Response response) -> {
        System.out.println("\nNotice: createFeedback API endpoint recognized request");
        DbConnection db = App.getInstance().getDbConnection();

        // get current session; ensure session is live
        request.session(true);
        Session session = request.session();
        if (session.isNew()) {
            System.out.println("Error:  APIController:createFeedback session not found");
            response.redirect("/error/401");
            return null;
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
        Feedback feedback = new Feedback(participant.getParticipantID(), event.getEventID(), results, weights, types, keys, sub_weights, anonymous, current);
        
        // run sentiment analysis on feedback
        SentimentAnalyser.main(feedback);
        if (feedback.getCompound() != null) System.out.println("Notice: SA on feedback successful");

        // ensure feedback is valid
        if (!v.isFeedbackValid(feedback)){
            System.out.println("Error: APIController:createFeedback: feedback considered invalid");
            return "Error: feedback considered invalid";
        }

        // store (valid) feedback in system
        db.createFeedback(feedback);

        // redirect to participant event page
        // (links to ParticipantEventController.servePage)
        response.redirect("/event/participant/feedback");
        return null;
    };

    /**
     * host API end-point: generate an empty (but named) template
     */
    public static Route createEmptyTemplate = (Request request, Response response) -> {
        System.out.println("\nNotice: createEmptyTemplate API endpoint recognized request");
        DbConnection db = App.getInstance().getDbConnection();

        // get current session; ensure session is live
        request.session(true);
        Session session = request.session();
        if (session.isNew()) {
            System.out.println("Error:  APIController:createFeedback session not found");
            response.redirect("/error/401");
            return null;
        }

        // get host from session
        Host host = session.attribute("host");
        int hostID = host.getHostID();

        // collect template name from form
        String template_name = request.queryParams("templateName");
        System.out.println("Notice: template name collected: " + template_name);

        if (StringUtils.isBlank(template_name)){
            System.out.println("Error:  APIController:createEmptyTemplate template name is blank");
            session.attribute("errorMessageCreateEmptyTemplate", "Error: template name is blank");
            response.redirect("/host/templates/new");
            return null;
        }

        // template name valid; generate empty template in DB
        db.createEmptyTemplate(hostID, template_name, new Timestamp(System.currentTimeMillis()));

        // redirect to host templates page
        // (links to MyTemplatesController.servePage)
        response.redirect("/host/templates");
        return null;
    };

    /**
     * host API end-point: generate a populated template
     */
    public static Route createTemplate = (Request request, Response response) -> {
        System.out.println("\nNotice: createTemplate API endpoint recognized request");
        DbConnection db = App.getInstance().getDbConnection();

        // get current session; ensure session is live
        request.session(true);
        Session session = request.session();
        if (session.isNew()) {
            System.out.println("Error:  APIController:createFeedback session not found");
            response.redirect("/error/401");
            return null;
        }

        // get host from session
        Host host = session.attribute("host");

        // NOTE PLACE ERRORS IN: session.attribute("errorMessageCreateTemplate", "value");

        //TODO

        // get template hostCode (stored in form) 
        String hostCode = request.queryParams("hostCode");

        // collect form data
        // form data -> components
        // components -> template
        
        // ensure template is valid

        // store template in DB

        // return to "/host/templates/edit/code"
        // (links to TemplateEditController.servePage)
        response.redirect("/host/templates/edit/code" + "?hostCode=" + hostCode);
        return null;
    };

    public static Route createTemplateComponent = (Request request, Response response) -> {
        return null;
    };

    public static Route deleteTemplate = (Request request, Response response) -> {
        return null;
    };

    public static Route deleteTemplateComponent = (Request request, Response response) -> {
        return null;
    };


}
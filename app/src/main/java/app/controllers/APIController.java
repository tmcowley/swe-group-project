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

import java.util.Date;
import java.util.Calendar;

import spark.*;
import spark.utils.StringUtils;

public class APIController {

    // get validator from singleton App instance; thread safe (no DB interaction)
    static Validator v = App.getInstance().getValidator();

    /**
     * creates a new host when a new user signs-up as a host
     * form sent from front-end to back-end to create host
     */
    public static Route createHost = (Request request, Response response) -> {
        System.out.println("\nNotice: createHost API endpoint recognized request");

        // get db conn from singleton App instance
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

        // get db conn from singleton App instance
        DbConnection db = App.getInstance().getDbConnection();

        // get current session; ensure session is live
        Session session = request.session(true);
        if (session.isNew()) {
            System.out.println("Error:  APIController:createEvent session not found");
            response.redirect("/error/401");
            return null;
        }

        // ensure host exists in current session
        if (session.attribute("host") == null){
            System.out.println("Error:  APIController:createEvent session found, host not in session");
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
        String[] startTimes = request.queryParams("startTime").split(":");
        String[] endTimes = request.queryParams("endTime").split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTimes[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(startTimes[1]));
        calendar.set(Calendar.SECOND, 0);
        Date sTime =(Date) calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTimes[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(endTimes[1]));
        calendar.set(Calendar.SECOND, 0);
        Date eTime =(Date) calendar.getTime();       
        Timestamp startTime = new Timestamp(sTime.getTime());
        Timestamp endTime = new Timestamp(eTime.getTime());    
        Timestamp current = new Timestamp(System.currentTimeMillis());

        // validate input before database interaction
        if (!v.eventTitleIsValid(title)) {
            session.attribute("errorMessageCreateEvent", "Error: Event title is not valid");
            response.redirect("/host/create-event");
            return null;
        }
        if (!v.eventDescriptionIsValid(description)) {
            session.attribute("errorMessageCreateEvent", "Error: Event description is invalid");
            response.redirect("/host/create-event");
            return null;
        }
        if (!v.eventTypeIsValid(type)) {
            session.attribute("errorMessageCreateEvent", "Error: Event type is invalid");
            response.redirect("/host/create-event");
            return null;
        }
        if (startTime.compareTo(endTime) > 0 || endTime.compareTo(current) < 0){
            session.attribute("errorMessageCreateEvent", "Error: start and end time not in order");
            response.redirect("/host/create-event");
            return null;
        }

        // create an event object
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

        // ensure event created is valid
        if (!v.isEventValid(event)){
            // return not found if event is not created or input is not valid
            session.attribute("errorMessageCreateEvent", "Error: event not created or considered invalid - check inputs");
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

        // get db conn from singleton App instance
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

        // get db conn from singleton App instance
        DbConnection db = App.getInstance().getDbConnection();

        // get current session; ensure session is live
        Session session = request.session(true);
        if (session.isNew()) {
            System.out.println("Error:  APIController:createFeedback session not found");
            response.redirect("/error/401");
            return null;
        }

        // ensure participant and event exist in current session
        if (session.attribute("event") == null || session.attribute("participant") == null){
            System.out.println("Error:  APIController:createFeedback session found, event or participant not in session");
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

        // get db conn from singleton App instance
        DbConnection db = App.getInstance().getDbConnection();

        // get current session; ensure session is live
        Session session = request.session(true);
        if (session.isNew()) {
            System.out.println("Error:  APIController:createFeedback session not found");
            response.redirect("/error/401");
            return null;
        }

        // ensure host exists in current session
        if (session.attribute("host") == null){
            System.out.println("Error:  APIController:createEmptyTemplate session found, host not in session");
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
     * host API POST end-point: generate a populated template
     */
    public static Route createTemplate = (Request request, Response response) -> {
        System.out.println("\nNotice: createTemplate API endpoint recognized request");

        // get db conn from singleton App instance
        DbConnection db = App.getInstance().getDbConnection();

        // get current session; ensure session is live
        Session session = request.session(true);
        if (session.isNew()) {
            System.out.println("Error:  APIController:createFeedback session not found");
            response.redirect("/error/401");
            return null;
        }

        // ensure host exists in current session
        if (session.attribute("host") == null){
            System.out.println("Error:  APIController:createTemplate session found, host not in session");
            response.redirect("/error/401");
            return null;
        }

        // ensure host code sent in POST request
        if (request.queryParams("hostCode") == null){
            System.out.println("Error:  APIController:createTemplate host code not in POST request");
            session.attribute("errorMessageCreateTemplate", "Error: host code not in form attributes");
            response.redirect("/host/templates");
            return null;
        }

        // get host from session
        Host host = session.attribute("host");

        // NOTE PLACE ERRORS IN: session.attribute("errorMessageCreateTemplate", "value");

        //TODO

        // get template code (stored in form) 
        String templateCode = request.queryParams("templateCode");

        // collect form data
        // form data -> components
        // components -> template
        
        // ensure template is valid

        // store template in DB

        // return to "/host/templates/edit/code"
        // (links to TemplateEditController.servePage)
        response.redirect("/host/templates/edit/code" + "?templateCode=" + templateCode);
        return null;
    };

    public static Route createTemplateComponent = (Request request, Response response) -> {
        System.out.println("\nNotice: createTemplateComponent API endpoint recognized request");

        // get db conn from singleton App instance
        DbConnection db = App.getInstance().getDbConnection();

        // get current session; ensure session is live
        Session session = request.session(true);
        if (session.isNew()) {
            System.out.println("Error:  APIController:createTemplateComponent session not found");
            response.redirect("/error/401");
            return null;
        }

        // ensure host exists in current session
        if (session.attribute("host") == null){
            System.out.println("Error:  APIController:createTemplateComponent session found, host not in session");
            response.redirect("/error/401");
            return null;
        }

        String name = request.queryParams("tc_name");
        String type = request.queryParams("tc_type");
        String prompt = request.queryParams("tc_prompt");
        String[] options = request.queryParamsValues("tc_options");
        String[] optionsAnsStrings = request.queryParamsValues("tc_optionsAns");
        Boolean[] optionsAns = new Boolean[optionsAnsStrings.length];
        for (int i=0; i < optionsAnsStrings.length;i++) {
            optionsAns[i] = Boolean.parseBoolean(optionsAnsStrings[i]);
        }
        String textResponse;
        if (type.equals("radio") || type.equals("checkbox")) {
            textResponse = null;
        } else {
            textResponse = request.queryParams("tc_textResponse");
        }

        TemplateComponent templateComponent = new TemplateComponent(name, type, prompt, options, optionsAns, textResponse);
        
        if (!v.isComponentValid(templateComponent)){
            System.out.println("Error: APIController:createTemplateComponent: TemplateComponent considered invalid");
            return "Error: TemplateComponent considered invalid";
        }

        db.createTemplateComponent(templateComponent);
        return null;
    };

    public static Route createEmptyComponent = (Request request, Response response) -> {
        return null;
    };

    public static Route deleteTemplate = (Request request, Response response) -> {
        System.out.println("\nNotice: deleteTemplate API endpoint recognized request");

        // get db conn from singleton App instance
        DbConnection db = App.getInstance().getDbConnection();

        // get current session; ensure session is live
        Session session = request.session(true);
        if (session.isNew()) {
            System.out.println("Error:  APIController:deleteTemplate session not found");
            response.redirect("/error/401");
            return null;
        }

        // ensure host exists in current session
        if (session.attribute("host") == null){
            System.out.println("Error:  APIController:deleteTemplate session found, host not in session");
            response.redirect("/error/401");
            return null;
        }

        // ensure host code sent in POST request
        if (request.queryParams("templateCode") == null){
            System.out.println("Error:  APIController:deleteTemplate template code not in POST request");
            session.attribute("errorMessageDeleteTemplate", "Error: template code not in form attributes");
            response.redirect("/host/templates");
            return null;
        }

        String templateCode = request.queryParams("templateCode");

        // get template from templateCode; delete corresponding template
        Template template = db.getTemplateByCode(templateCode);
        db.deleteTemplate(template.getTemplateID());

        // redirect host to templates page
        response.redirect("/host/templates");
        return null;
    };

    public static Route deleteTemplateComponent = (Request request, Response response) -> {
        System.out.println("\nNotice: deleteTemplateComponent API endpoint recognized request");

        // get db conn from singleton App instance
        DbConnection db = App.getInstance().getDbConnection();

        // get current session; ensure session is live
        Session session = request.session(true);
        if (session.isNew()) {
            System.out.println("Error:  APIController:deleteTemplateComponent session not found");
            response.redirect("/error/401");
            return null;
        }

        // ensure host exists in current session
        if (session.attribute("host") == null){
            System.out.println("Error:  APIController:deleteTemplateComponent session found, host not in session");
            response.redirect("/error/401");
            return null;
        }

        // ensure component ID sent in POST request
        if (request.queryParams("component_id") == null){
            System.out.println("Error:  APIController:deleteTemplateComponent TemplateComponent ID not in POST request");
            session.attribute("errorMessageDeleteTemplateComponent", "Error: TemplateComponent ID not in form attributes");
            response.redirect("/host/templates");
            return null;
        }

        // collect host from session
        Host host = session.attribute("host");

        // parse component_id from form input
        int component_id = Integer.parseInt(request.queryParams("component_id"));

        // get template code (stored in form) 
        String templateCode = request.queryParams("templateCode");

        // delete template component by ID
        db.deleteTemplateComponent(component_id);

        // return to "/host/templates/edit/code"
        // (links to TemplateEditController.servePage)
        response.redirect("/host/templates/edit/code" + "?templateCode=" + templateCode);
        return null;
    };


}
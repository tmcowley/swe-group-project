package app.controllers;

import app.App;
import app.DbConnection;
import app.Validator;
import app.objects.*;
import app.sentimentanalysis.SentimentAnalyser;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import spark.*;

// for data validation
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang.ObjectUtils.Null;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;

public class APIController {

    // get validator from singleton App instance; thread safe (no DB interaction)
    static Validator v = App.getInstance().getValidator();

    /**
     * creates a new host following host sign-up
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
        if (!v.nameIsValid(f_name) || !v.nameIsValid(l_name)) {
            System.out.println("Error:  the name provided is considered invalid");
            request.session().attribute("errorMessageCreate", "Error: name is invalid");
            response.redirect("/host/login");
            return null;
        }
        if (!v.eAddressIsValid(e_address)) {
            System.out.println("Error:  the email provided is considered invalid");
            request.session().attribute("errorMessageCreate", "Error: email is invalid");
            response.redirect("/host/login");
            return null;
        }

        // ensure email uniqueness
        if (db.emailExists(e_address)) {
            System.out.println("Error:  the email provided is non-unique");
            request.session().attribute("errorMessageCreate", "Error: email is already in use");
            response.redirect("/host/login");
            return null;
        }

        // create host in system
        Host host = db.createHost(f_name, l_name, e_address);

        // ensure host is valid
        if (!v.isHostValid(host)) {
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
     * create new event
     */
    public static Route createEvent = (Request request, Response response) -> {
        System.out.println("\nNotice: createEvent API endpoint recognized request");

        // get db conn from singleton App instance
        DbConnection db = App.getInstance().getDbConnection();

        // get current session; ensure session is live
        Session session = request.session(true);
        if (session.isNew()) {
            System.out.println("Error:  session not found");
            response.redirect("/error/401");
            return null;
        }

        // ensure host exists in current session
        if (session.attribute("host") == null) {
            System.out.println("Error:  session found, host not in session");
            response.redirect("/error/401");
            return null;
        }

        // initialise event and input
        Event event = null;
        Host host = session.attribute("host");

        // collect POST request parameters
        String title = request.queryParams("eventTitle");
        String description = request.queryParams("eventDescription");
        String type = request.queryParams("eventType");
        String templateCode = request.queryParams("eventTemplate");

        // ensure all request parameters were collected
        if (title == null || description == null || type == null || templateCode == null) {
            session.attribute("errorMessageCreateEvent", "Error: not all form inputs not collected");
            response.redirect("/host/create-event");
            return null;
        }

        // generate a timestamp for now
        Timestamp current = new Timestamp(System.currentTimeMillis());

        /*
         * // parse start and end times from event creation String[] start_time_string =
         * request.queryParams("startTime").split(":"); String[] end_time_string =
         * request.queryParams("endTime").split(":"); Calendar calendar =
         * Calendar.getInstance(); calendar.set(Calendar.HOUR_OF_DAY,
         * Integer.parseInt(start_time_string[0])); calendar.set(Calendar.MINUTE,
         * Integer.parseInt(start_time_string[1])); calendar.set(Calendar.SECOND, 0);
         * Date sTime =(Date) calendar.getTime(); calendar.set(Calendar.HOUR_OF_DAY,
         * Integer.parseInt(end_time_string[0])); calendar.set(Calendar.MINUTE,
         * Integer.parseInt(end_time_string[1])); calendar.set(Calendar.SECOND, 0); Date
         * eTime =(Date) calendar.getTime(); Timestamp startTime = new
         * Timestamp(sTime.getTime()); Timestamp endTime = new
         * Timestamp(eTime.getTime()); Timestamp current = new
         * Timestamp(System.currentTimeMillis()); if (startTime.compareTo(endTime) > 0
         * || endTime.compareTo(current) < 0){ // note: testing discovered errors in
         * this session.attribute("errorMessageCreateEvent",
         * "Error: start and end time not in order");
         * response.redirect("/host/create-event"); return null; }
         */

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

        // create an event object
        if (templateCode.equals("noTemplate")) {
            // create an event without a template
            System.out.println("Notice: no template has been provided");
            event = db.createEvent(host.getHostID(), title, description, type, current, current);
        } else if (v.templateCodeIsValid(templateCode)) {
            // create an event with a template
            System.out.println("Notice: a template has been provided");
            Template template = db.getTemplateByCode(templateCode);
            event = db.createEvent(host.getHostID(), template.getTemplateID(), title, description, type, current,
                    current);
        }

        // ensure event created is valid
        if (!v.isEventValid(event)) {
            // return not found if event is not created or input is not valid
            session.attribute("errorMessageCreateEvent", "Error: event not created or invalid - check inputs");
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
        // System.out.println("Notice: form attributes collected, are valid");

        // ensure event-code exists in system
        if (!db.eventCodeExists(eventCode)) {
            System.out.println("Error:  event-code does not exist");
            request.session().attribute("errorMessageJoinEvent", "Error: event-code does not exist");
            response.redirect("/");
            return null;
        }
        // System.out.println("Notice: event code exists in system");

        // create Participant object in DB -> link to event
        Participant participant = db.createParticipant(f_name, l_name);
        Event event = db.getEventByCode(eventCode);

        // ensure event and participant creations were succeeded
        if (!v.isParticipantValid(participant) || !v.isEventValid(event)) {
            System.out.println("Error:  participant/ event creation failed");
            request.session().attribute("errorMessageJoinEvent", "Error: participant or event creation failed");
            response.redirect("/");
            return null;
        }

        // add participant to event; ensure success
        Boolean added = db.addParticipantToEvent(participant.getParticipantID(), event.getEventID());
        if (BooleanUtils.isNotTrue(added)) {
            System.out.println("Error:  adding participant to event failed");
            request.session().attribute("errorMessageJoinEvent", "Error: adding participant to event failed");
            response.redirect("/");
            return null;
        }

        // start session; add participant and event to session
        request.session(true);
        request.session().attribute("participant", participant);
        request.session().attribute("event", event);

        // event is valid; redirect participant to event page
        // (links to ParticipantEventController.servePage)
        response.redirect("/event/participant/feedback");
        return null;
    };

    /**
     * allow a participant in an event to create an instance of feedback
     */
    public static Route createFeedback = (Request request, Response response) -> {
        System.out.println("\nNotice: createFeedback API endpoint recognized request");

        // get db conn from singleton App instance
        DbConnection db = App.getInstance().getDbConnection();

        // get current session; ensure session is live
        Session session = request.session(true);
        if (session.isNew()) {
            System.out.println("Error:  session not found");
            response.redirect("/error/401");
            return null;
        }

        // ensure participant and event exist in current session
        if (session.attribute("event") == null || session.attribute("participant") == null) {
            System.out.println("Error:  session found, event or participant not in session");
            response.redirect("/error/401");
            return null;
        }

        // store event and participant from session; get IDs and codes
        Event event = session.attribute("event");
        Participant participant = session.attribute("participant");
        int event_id = event.getEventID();
        String event_code = event.getEventCode();
        int participant_id = participant.getParticipantID();

        // ensure event exists
        if (!db.eventCodeExists(event_code)) {
            System.out.println("Error:  session found, event does not exist");
            response.redirect("/error/401");
            return null;
        }

        // ensure participant exists, and is in event
        if (!db.participantInEvent(participant_id, event_id)) {
            System.out.println("Error:  session found, participant not in event");
            response.redirect("/error/401");
            return null;
        }

        // collect fields from form
        String[] results = request.queryParamsValues("feedbackData");
        // ensure form inputs collected
        if (results == null || request.queryParams("feedbackData") == null) {
            System.out.println("Error:  results array not set");
            session.attribute("errorMessageInParticipantEvent", "Error: form inputs not collected (or empty)");
            response.redirect("/event/participant/feedback");
            return null;
        }
        int results_length = results.length;
        String anonymous_string = request.queryParams("anon");

        Float[] weights = new Float[results_length];
        byte[] types = new byte[results_length];
        Boolean[] keys = new Boolean[results_length];
        byte[][] sub_weights = new byte[results_length][5];
        Timestamp current = new Timestamp(System.currentTimeMillis());
        for (int i = 0; i < results_length; i++) {
            weights[i] = 4f;
            types[i] = 0;
            keys[i] = false;
        }
        // get feedback anonymity state
        Boolean anonymous = false;
        if (StringUtils.equals(anonymous_string, "Submit Anonymously")) {
            anonymous = true;
        }

        // System.out.println("Notice: generating new feedback instance");
        Feedback feedback = new Feedback(participant_id, event_id, results, weights, types, keys, sub_weights,
                anonymous, current);

        // ensure feedback is valid
        if (!v.isFeedbackValid(feedback)) {
            System.out.println("Error: feedback considered invalid (before SA)");
            session.attribute("errorMessageInParticipantEvent", "Error: feedback invalid");
            response.redirect("/event/participant/feedback");
            return null;
        }

        // run sentiment analysis on feedback; ensure SA was successful
        SentimentAnalyser.main(feedback);
        if (feedback.getCompound() == null) {
            System.out.println("Notice: SA on feedback failed");
            session.attribute("errorMessageInParticipantEvent", "Error: sentiment analysis failed");
            response.redirect("/event/participant/feedback");
            return null;
        }
        // System.out.println("Notice: SA on feedback successful");

        // ensure feedback is valid
        if (!v.isFeedbackValid(feedback)) {
            System.out.println("Error: feedback considered invalid (after SA)");
            session.attribute("errorMessageInParticipantEvent", "Error: feedback invalid");
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
            System.out.println("Error:  session not found");
            response.redirect("/error/401");
            return null;
        }

        // ensure host exists in current session
        if (session.attribute("host") == null) {
            System.out.println("Error:  session found, host not in session");
            response.redirect("/error/401");
            return null;
        }

        // get host from session; ensure is valid and exists
        Host host = session.attribute("host");
        if (!v.isHostValid(host) || !db.hostCodeExists(host.getHostCode())){
            System.out.println("Error:  host in session invalid or non-existent in database");
            response.redirect("/error/401");
            return null;
        }
        int host_id = host.getHostID();

        // collect template name from form; ensure it's not blank
        String template_name = request.queryParams("templateName");
        System.out.println("Notice: template name collected: " + template_name);
        if (StringUtils.isBlank(template_name)) {
            System.out.println("Error:  template name is blank");
            session.attribute("errorMessageCreateEmptyTemplate", "Error: template name is blank");
            response.redirect("/host/templates/new");
            return null;
        }

        Timestamp timestamp_now = new Timestamp(System.currentTimeMillis());

        // template name valid; generate empty template in DB
        Template emptyTemplate = db.createEmptyTemplate(host_id, template_name, timestamp_now);
        if (!v.isTemplateValid(emptyTemplate) || !db.templateCodeExists(emptyTemplate.getTemplateCode())){
            System.out.println("Error:  template creation failed");
            session.attribute("errorMessageCreateEmptyTemplate", "Error: template creation failed");
            response.redirect("/host/templates/new");
            return null;
        } 
        //System.out.println("Notice: created template code: " + emptyTemplate.getTemplateCode());

        // redirect to host templates page
        // (links to MyTemplatesController.servePage)
        response.redirect("/host/templates");
        return null;
    };

    /**
     * host API POST end-point: generate a populated template
     */
    public static Route fillTemplate = (Request request, Response response) -> {
        System.out.println("\nNotice: createTemplate API endpoint recognized request");

        // get db conn from singleton App instance
        DbConnection db = App.getInstance().getDbConnection();

        // get current session; ensure session is live
        Session session = request.session(true);
        if (session.isNew()) {
            System.out.println("Error:  session not found");
            response.redirect("/error/401");
            return null;
        }
        //System.out.println("session valid");

        // ensure host exists in current session
        if (session.attribute("host") == null) {
            System.out.println("Error:  session found, host not in session");
            response.redirect("/error/401");
            return null;
        }
        //System.out.println("host in session");

        // get host from session
        Host host = session.attribute("host");
        if (!v.isHostValid(host) || !db.hostCodeExists(host.getHostCode())){
            System.out.println("Error:  host in session invalid or non-existent in database");
            response.redirect("/error/401");
            return null;
        }
        int host_id = host.getHostID();
        //System.out.println("host in session valid, exists");

        //session.attribute("errorMessageCreateTemplate", "value");

        // get template code (stored in form); ensure exists in system
        String templateCode = request.queryParams("templateCode");
        if (!db.templateCodeExists(templateCode)) {
            System.out.println("Error:  template code invalid or null");
            response.redirect("/host/templates");
            return null;
        }
        Template template = db.getTemplateByCode(templateCode);
        int template_id = template.getTemplateID();

        // for each component in the template
        for (TemplateComponent component : template.getComponents()){

            // collect component fields
            String prompt = request.queryParams("component["+component.getID()+"][prompt]");
            String name = request.queryParams("component["+component.getID()+"][name]");
            String type = request.queryParams("component["+component.getID()+"][type]");
            Integer old_component_id = null;
            try{
                old_component_id = Integer.valueOf(request.queryParams("component["+component.getID()+"][component_id]"));
            } catch (Exception e){
                old_component_id = component.getID();
            } 
            String consider_string = request.queryParams("component["+component.getID()+"][consider]");
            Boolean considered;

            // collect checkbox input
            if (consider_string == null){
                considered = false;
            } else {
                considered = true;
            }

            Integer weight = null;
            String[] options = null;
            Boolean[] optionsAns = null;
            Integer[] optionPos = null;

            if (component.getType().equals("question")){

                String weight_string = request.queryParams("component["+component.getID()+"][weight]");
                weight = Integer.parseInt(weight_string);

            }
            else if (component.getType().equals("radio") || component.getType().equals("checkbox")) {

                System.out.println("we have an option type");

                options = request.queryParamsValues("options-"+component.getID()+"[]");
                if (options == null){
                    System.out.println("Notice:  form inputs for option type not collected");
                }

                String[] options_pos_as_string = request.queryParamsValues("options-pos-"+component.getID()+"[]");
                int array_len = options_pos_as_string.length;

                optionPos = new Integer[array_len];
                for (int i = 0; i < array_len; i++) {
                    System.out.println("OPTION POS SCORE: " + options_pos_as_string[i]);
                    optionPos[i] = Integer.parseInt(options_pos_as_string[i]) + 4;
                    System.out.println("OPTION POS SCORE (processed): " + optionPos[i]);
                }
            }

            TemplateComponent new_component = new TemplateComponent(name, type, prompt, considered, weight, options, optionPos, optionsAns, "");
            // ensure new component is valid
            if (!v.isComponentValid(new_component)){
                System.out.println("Error:  filled component is invalid");
                session.attribute("errorMessageCreateTemplate", "a component is invalid");
                
                // return to "/host/templates/edit/code"
                // (links to TemplateEditController.servePage)
                response.redirect("/host/templates/edit/code" + "?templateCode=" + templateCode);
            }

            try{
                Boolean filled = fillComponent(db, template_id, old_component_id, new_component);
                if (BooleanUtils.isNotTrue(filled)){
                    System.out.println("Error:  filled did not work");
                }
                System.out.println("Error:  filled did work");
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        // return to "/host/templates/edit/code"
        // (links to TemplateEditController.servePage)
        response.redirect("/host/templates/edit/code" + "?templateCode=" + templateCode);
        return null;
    };


    /**
     * create a template component, derivative method of API
     */
    public static Boolean fillComponent (DbConnection db, int template_id, int old_component_id, TemplateComponent component) {
        System.out.println("\nNotice: fillComponent API derivative function called");

        // delete empty component in system
        Boolean empty_component_removed = db.deleteTemplateComponent(old_component_id);
        if (BooleanUtils.isNotTrue(empty_component_removed)){
            System.out.println("Error:  empty component not removed");
            return false;
        }

        // add new, filled component; ensure valid
        component = db.createTemplateComponent(component);
        if (!v.isComponentValid(component)){
            System.out.println("Error:  filled component (after db insertion) is invalid");
            return false;
        }
        int component_id = component.getId();

        // add component to template; ensure addition successful
        Boolean component_added_to_template = db.addComponentToTemplate(component_id, template_id);
        if (BooleanUtils.isNotTrue(component_added_to_template)){
            System.out.println("Error:  new component not added to template");
            return false;
        }

        return true;
    };

    /**
     * GET request for adding an empty component by type
     */
    public static Route createEmptyComponent = (Request request, Response response) -> {
        System.out.println("\nNotice: createEmptyComponent API endpoint recognized request");

        // get db conn from singleton App instance
        DbConnection db = App.getInstance().getDbConnection();

        // get current session; ensure session is live
        Session session = request.session(true);
        if (session.isNew()) {
            System.out.println("Error:  session not found");
            response.redirect("/error/401");
            return null;
        }
        // ensure host exists in current session
        if (session.attribute("host") == null) {
            System.out.println("Error:  session found, host not in session");
            response.redirect("/error/401");
            return null;
        }

        // collect POST request parameters
        String templateCode = request.queryParams("templateCode");
        String componentType = request.queryParams("componentType");

        // ensure GET request params are correct
        if (templateCode == null || componentType == null) {
            System.out.println("Error:  incorrect parameters to GET request");
            response.redirect("/host/templates");
            return null;
        }
        System.out.println("Notice: templateCode:" + templateCode + ", componentType:" + componentType);

        // ensure template code exists in system
        if (!db.templateCodeExists(templateCode)) {
            System.out.println("Error:  template code invalid");
            response.redirect("/host/templates");
            return null;
        }

        // ensure component type is valid
        if (!v.componentTypeIsValid(componentType)) {
            System.out.println("Error:  component type invalid");
            // return to "/host/templates/edit/code"
            // (links to TemplateEditController.servePage)
            response.redirect("/host/templates/edit/code" + "?templateCode=" + templateCode);
            return null;
        }

        // get template by code
        Template template = db.getTemplateByCode(templateCode);

        TemplateComponent component = null;

        // set default number of options to three
        int default_option_count = 3;

        if (componentType.equals("question")) {
            component = new TemplateComponent("component_name", "question", "", null, null, null, null, null, "");
        } else if (componentType.equals("checkbox")) {
            component = new TemplateComponent("component_name", "checkbox", "", null, null, new String[default_option_count], new Integer[default_option_count], new Boolean[default_option_count], null);
        } else if (componentType.equals("radio")) {
            component = new TemplateComponent("component_name", "radio", "", null, null, new String[default_option_count], new Integer[default_option_count], new Boolean[default_option_count], null);
        }

        // ensure component created is valid
        if (!v.isComponentValid(component)) {
            System.out.println("Error:  invalid component generated");
            response.redirect("/host/templates");
            return null;
        }

        // store created (valid) component
        component = db.createTemplateComponent(component);

        Boolean added = db.addComponentToTemplate(component.getId(), template.getTemplateID());

        if (BooleanUtils.isNotTrue(added)) {
            System.out.println("Error:  component not added to template correctly");
            response.redirect("/host/templates");
            return null;
        }

        // redirect user to template edit page
        response.redirect("/host/templates/edit/code" + "?templateCode=" + templateCode);
        return null;
    };

    /**
     * delete a template within the database (POST API endpoint)
     */
    public static Route deleteTemplate = (Request request, Response response) -> {
        System.out.println("\nNotice: deleteTemplate API endpoint recognized request");

        // get db conn from singleton App instance
        DbConnection db = App.getInstance().getDbConnection();

        // get current session; ensure session is live
        Session session = request.session(true);
        if (session.isNew()) {
            System.out.println("Error:  session not found");
            response.redirect("/error/401");
            return null;
        }

        // ensure host exists in current session
        if (session.attribute("host") == null) {
            System.out.println("Error:  session found, host not in session");
            response.redirect("/error/401");
            return null;
        }

        // ensure host code sent in POST request
        if (request.queryParams("templateCode") == null) {
            System.out.println("Error:  template code not in POST request");
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

    /**
     * delete a template component within the database (POST API endpoint)
     */
    public static Route deleteTemplateComponent = (Request request, Response response) -> {
        System.out.println("\nNotice: deleteTemplateComponent API endpoint recognized request");

        // get db conn from singleton App instance
        DbConnection db = App.getInstance().getDbConnection();

        // get current session; ensure session is live
        Session session = request.session(true);
        if (session.isNew()) {
            System.out.println("Error:  session not found");
            response.redirect("/error/401");
            return null;
        }

        // ensure host exists in current session
        if (session.attribute("host") == null) {
            System.out.println("Error:  session found, host not in session");
            response.redirect("/error/401");
            return null;
        }

        // ensure component ID sent in POST request
        if (request.queryParams("component_id") == null) {
            System.out.println("Error:  TemplateComponent ID not in POST request");
            session.attribute("errorMessageDeleteTemplateComponent",
                    "Error: TemplateComponent ID not in form attributes");
            response.redirect("/host/templates");
            return null;
        }

        // collect host from session
        Host host = session.attribute("host");

        // parse component_id from form input
        int component_id = Integer.parseInt(request.queryParams("component_id"));

        // get template code (stored in form)
        String templateCode = request.queryParams("templateCode");

        // get template by its code (used for ensuring host is author)
        Template template = db.getTemplateByCode(templateCode);
        if (template == null) {
            System.out.println("Error:  template (by code) is null");
            session.attribute("errorMessageDeleteTemplateComponent",
                    "Error: no template corresponding to template code");
            response.redirect("/host/templates");
            return null;
        }

        // ensure template is authored by host in session
        if (host.getHostID() != template.getHostID()) {
            System.out.println("Error:  host does not own template by code");
            response.redirect("/error/401");
            return null;
        }

        // delete template component by ID
        db.deleteTemplateComponent(component_id);

        // return to template's edit page
        // (links to TemplateEditController.servePage)
        response.redirect("/host/templates/edit/code" + "?templateCode=" + templateCode);
        return null;
    };

}
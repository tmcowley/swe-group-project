package app.controllers;

import app.App;
import app.DbConnection;
import app.Validator;
import app.objects.Event;
import app.objects.Feedback;
import app.objects.Host;
import app.objects.Participant;
import app.objects.Template;
import app.objects.TemplateComponent;
import app.util.*;

import java.util.ArrayList;
import java.util.List;
// for ViewUtil velocity models
import java.util.HashMap;
import java.util.Map;

import spark.*;

public class HostEventController {

    // serve host event page (following GET request)
    public static Route servePage = (Request request, Response response) -> {

        System.out.println("\nNotice: HostEventController:servePage recognized request");

        // get db conn, validator from singleton App instance
        Validator v = App.getInstance().getValidator();
        DbConnection db = App.getInstance().getDbConnection();

        // get current session; ensure session is live
        Session session = request.session(true);
        if (session.isNew()) {
            System.out.println("Error:  session not found");
            response.redirect("/error/401");
            return null;
        }

        // ensure host and event exist in current session
        if (session.attribute("event") == null || session.attribute("host") == null){
            System.out.println("Error:  session found, event or host not in session");
            response.redirect("/error/401");
            return null;
        }

        // collect event from session; ensure valid and exists
        Event event = session.attribute("event");
        if (!v.isEventValid(event) || !db.eventCodeExists(event.getEventCode())){
            System.out.println("Error:  event invalid or does not exist in database");
            response.redirect("/error/401");
            return null;
        }
        int event_id = event.getEventID();
        int event_host_id = event.getHostID();

        // collect host from session; ensure valid and exists
        Host host = session.attribute("host");
        if (!v.isHostValid(host) || !db.hostCodeExists(host.getHostCode())){
            System.out.println("Error:  host invalid or does not exist in database");
            response.redirect("/error/401");
            return null;
        }
        int host_id = host.getHostID();

        // ensure host authors event
        if (event_host_id != host_id){
            System.out.println("Error:  event not authored by host");
            return "Error:  event not authored by host";
        }

        // set empty components lists
        ArrayList<TemplateComponent> questionComponents = new ArrayList<TemplateComponent>();
        ArrayList<TemplateComponent> components = new ArrayList<TemplateComponent>();
        List<Integer> componentCounts = new ArrayList<Integer>();

        // case event has template
        if (event.hasTemplate()){
            int template_id = event.getTemplateID();
            Template template = db.getTemplate(template_id);
            components = template.getComponents();
            int componentCount = 0;
            for (TemplateComponent component: components) {
                if (component.getType().equals("question")) {
                    questionComponents.add(component);
                    componentCounts.add(componentCount);
                    componentCount++;
                }
            }
        }

        // collect feedback objects from the event
        Map<String, Object> model = new HashMap<>();
        Feedback[] feedbacks = db.getFeedbacksByEventID(event_id);
        int feedbackCount = 0;
        if (feedbacks.length != 0) {

            List<String> participantFName = new ArrayList<String>();
            List<String> participantLName = new ArrayList<String>();
            List<String[]> feedbackData = new ArrayList<String[]>();
            List<String> sentiment = new ArrayList<String>();
            List<String> time = new ArrayList<String>();
            for (Feedback feedback : feedbacks) {
                if (!feedback.getAnonymous()) {
                    Participant participant = db.getParticipant(feedback.getParticipantID());
                    participantFName.add(participant.getFName());
                    participantLName.add(participant.getLName());
                } else {
                    participantFName.add("Anonymous");
                    participantLName.add("");
                }
                feedbackData.add(feedback.getResults());
                sentiment.add(feedback.assessSentiment());
                time.add(feedback.getTimestamp().toString());
                feedbackCount++;
            }
            model.put("participantFName", participantFName);
            model.put("participantLName", participantLName);
            model.put("feedbackData", feedbackData);
            model.put("sentiment", sentiment);
            model.put("time", time);

        }
        List<Integer> feedbackCounts = new ArrayList<Integer>();
        for (int i = 0; i < feedbackCount; i++) {
            feedbackCounts.add(i);
        }

        // return host event page if event is created
        model.put("questionComponents", questionComponents);
        model.put("components", components);
        model.put("questionComponents", questionComponents);
        model.put("componentCounts", componentCounts);
        model.put("feedbackCounts", feedbackCounts);
        model.put("eventTitle", event.getTitle());
        model.put("eventDescription", event.getDescription());
        model.put("eventCode", event.getEventCode());
        return ViewUtil.render(request, model, "/velocity/host-event.vm");
    };

}

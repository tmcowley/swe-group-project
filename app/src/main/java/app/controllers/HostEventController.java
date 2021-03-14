package app.controllers;

import java.util.ArrayList;
import java.util.List;
// for ViewUtil velocity models
import java.util.HashMap;
import java.util.Map;

import spark.*;
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

        // get event, host from session
        Event event = session.attribute("event");
        Host host = session.attribute("host");
        Template template = db.getTemplate(event.getTemplateID());
        ArrayList<TemplateComponent> components = template.getComponents();
        ArrayList<TemplateComponent> questionComponents = new ArrayList<TemplateComponent>();
        int componentCount = 0;
        List<Integer> componentCounts = new ArrayList<Integer>();
        for (TemplateComponent component: components) {
            if (component.getType().equals("question")) {
                questionComponents.add(component);
                componentCounts.add(componentCount);
                componentCount++;
            }
        }

        // ensure host is valid
        if (!v.isHostValid(host)){
            System.out.println("Error:  host is invalid");
            return "Error:  host is invalid";
        }
        int host_id = host.getHostID();

        // ensure event is valid
        if (!v.isEventValid(event)){
            System.out.println("Error:  event is invalid");
            return "Error:  event is invalid";
        }
        int event_id = event.getEventID();
        int event_host_id = event.getHostID();

        // ensure host authors event
        if (event_host_id != host_id){
            System.out.println("Error:  event not authored by host");
            return "Error:  event not authored by host";
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

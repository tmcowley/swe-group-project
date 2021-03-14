package app.controllers;

import app.App;
import app.DbConnection;
import app.Validator;
import app.objects.*;
import app.util.*;

// for ViewUtil velocity models
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.runtime.directive.Foreach;

import spark.*;

public class ParticipantEventController {

    // serve the participant event page (in response to GET request)
    public static Route servePage = (Request request, Response response) -> {

        System.out.println("\nNotice: ParticipantEventController:servePage recognized request");

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

        // ensure host exists in current session
        if (session.attribute("event") == null || session.attribute("participant") == null){
            System.out.println("Error:  session found, participant or event not in session");
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

        // collect participant from session; ensure valid and exists
        Participant participant = session.attribute("participant");
        if (!v.isParticipantValid(participant) || !db.participantInEvent(participant.getParticipantID(), event_id)){
            System.out.println("Error:  participant invalid or does not exist within event");
            response.redirect("/error/401");
            return null;
        }
        int participant_id = participant.getParticipantID();

        // set empty components lists (case event has no templates)
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

        Map<String, Object> model = new HashMap<>();
        Feedback[] feedbacks = db.getFeedbacksInEventByParticipantID(event.getEventID(), participant.getParticipantID());

        int feedbackCount = 0;
        if (feedbacks.length != 0) {
            List<String> participantFName = new ArrayList<String>();
            List<String> participantLName = new ArrayList<String>();
            List<String> feedbackData = new ArrayList<String>();
            List<String> sentiment = new ArrayList<String>();
            List<String> time = new ArrayList<String>();
            for (Feedback feedback : feedbacks) {
                feedbackData.add(feedback.getResults()[0]);
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

        // generate front-page model (place variables in front-end page)
        model.put("feedbacks", feedbacks);
        model.put("eventTitle", event.getTitle());
        model.put("eventDescription", event.getDescription());
        model.put("components", components);
        model.put("questionComponents", questionComponents);
        model.put("componentCounts", componentCounts);
        model.put("errorMessageInParticipantEvent", session.attribute("errorMessageInParticipantEvent"));

        // unset session stored error message
        session.removeAttribute("errorMessageJoinEvent");
        
        // render the participant event page
        return ViewUtil.render(request, model, "/velocity/participant-event.vm");
    };

}

/* FROM OLD POST API endpoint: createFeedback
if (v.isFeedbackValid(feedback)) {
    System.out.println("Notice: feedback considered valid");
    String[] arrs = new String[feedback.getKey_Results().size()];
    String[] keyResults = (String[]) feedback.getKey_Results().toArray(arrs);
    db.createFeedback(feedback.getParticipantID(), feedback.getEventID(), feedback.getAnonymous(),
            feedback.getTimestamp(), feedback.getResults(), feedback.getWeights(), feedback.getTypes(),
            feedback.getKeys(), feedback.getSub_Weights(), feedback.getCompound(), keyResults);

    //Display Feedback was recorded
    Map<String, Object> model = new HashMap<>();

    Feedback[] feedbacks = db.getFeedbacksInEventByParticipantID(event.getEventID(), participant.getParticipantID());
    int feedbackCount = 0;
    if (feedbacks.length != 0) {

        List<String> participantFName = new ArrayList<String>();
        List<String> participantLName = new ArrayList<String>();
        List<String> feedbackData = new ArrayList<String>();
        List<String> sentiment = new ArrayList<String>();
        List<String> time = new ArrayList<String>();
        for (Feedback feedbackOfParticipant : feedbacks) {
            feedbackData.add(feedbackOfParticipant.getResults()[0]);
            if (feedbackOfParticipant.getCompound() > 0.15) {
                if (feedbackOfParticipant.getCompound() < 0.45) {
                    sentiment.add("slightly positive");
                } else if (feedbackOfParticipant.getCompound() < 0.75) {
                    sentiment.add("positive");
                } else {
                    sentiment.add("very positive");
                }
            } else if (feedbackOfParticipant.getCompound() < -0.15) {
                if (feedbackOfParticipant.getCompound() > -0.45) {
                    sentiment.add("slightly negative");
                } else if (feedbackOfParticipant.getCompound() > -0.75) {
                    sentiment.add("negative");
                } else {
                    sentiment.add("very negative");
                }
            } else {
                sentiment.add("neutral");
            }
            time.add(feedbackOfParticipant.getTimestamp().toString());
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

    model.put("feedbackCounts", feedbackCounts);
    model.put("eventTitle", event.getTitle());
    model.put("eventDescription", event.getDescription());
    return ViewUtil.render(request, model, "/velocity/participant-event.vm");
}
*/

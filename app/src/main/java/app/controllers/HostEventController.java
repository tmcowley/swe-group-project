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
import app.util.*;

public class HostEventController {

    // serve host event page (following GET request)
    public static Route servePage = (Request request, Response response) -> {

        System.out.println("\nNotice: HostEventController:servePage recognized request");

        Validator v = App.getInstance().getValidator();
        DbConnection db = App.getInstance().getDbConnection();

        // start session
        request.session(true);
        // return not found if session is new
        if (request.session().isNew()) {
            // return ViewUtil.notFound; TODO
            return "Error: Session not found";
        }
        // initialise event
        Event event = request.session().attribute("event");
        Host host = request.session().attribute("host");

        if (!v.isEventValid(event))
            return "Error: event is invalid";

        // return host event page if event is created
        request.session().attribute("event", event);
        Map<String, Object> model = new HashMap<>();
        Feedback[] feedbacks = db.getFeedbacksByEventID(event.getEventID());
        int feedbackCount = 0;
        if (feedbacks.length != 0) {

            List<String> participantFName = new ArrayList<String>();
            List<String> participantLName = new ArrayList<String>();
            List<String> feedbackData = new ArrayList<String>();
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
                feedbackData.add(feedback.getResults()[0]);
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
        model.put("feedbackCounts", feedbackCounts);
        model.put("eventTitle", event.getTitle());
        model.put("eventDescription", event.getDescription());
        model.put("eventCode", event.getEventCode());
        return ViewUtil.render(request, model, "/velocity/host-event.vm");
    };

}

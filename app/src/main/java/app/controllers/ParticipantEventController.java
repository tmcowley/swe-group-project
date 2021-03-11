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

import spark.*;

public class ParticipantEventController {

    // serve the participant event page (in response to GET request)
    public static Route servePage = (Request request, Response response) -> {

        System.out.println("\nNotice: participantEventController:servePage recognized request");

        Validator v = App.getInstance().getValidator();
        DbConnection db = App.getInstance().getDbConnection();

        // start session
        request.session(true);
        // return not found if session is new
        if (request.session().isNew()) {
            // return ViewUtil.notFound; TODO
            return "Error: Session not found";
        }

        Event event = request.session().attribute("event");
        Participant participant = request.session().attribute("participant");
        // get all feedbacks of the participant in the current event - inject into front-end
        Map<String, Object> model = new HashMap<>();

            Feedback[] feedbacks = db.getFeedbacksByEventID(event.getEventID());
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
        return ViewUtil.render(request, model, "/velocity/landing.vm");
    };

}

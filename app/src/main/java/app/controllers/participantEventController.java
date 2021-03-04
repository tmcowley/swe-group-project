package app.controllers;

import java.util.*;
import java.net.*;
import java.io.*;
import spark.*;
import app.Validator;
import app.DbConnection;
import app.util.*;
import app.objects.*;

import java.sql.SQLException;


public class participantEventController {
    private static DbConnection db;

    private static Validator validator;

    /** Serve the participant-event page (GET request) */
    public static Route servePage = (Request request, Response response) -> {

        validator = new Validator();
        Event event = null;

        if (validator.eventCodeIsValid(request.queryParams("participantCode"))) {
            String[] name = request.queryParams("participantName").split(" ");
            
            try {
                // instantiate DB connection
                db = new DbConnection();
                
    
                Participant participant = db.createParticipant(request.ip(),name[0],name[1]);
                event = db.getEventByCode(request.queryParams("participantCode"));
                db.addParticipantToEvent(participant.getParticipantID(), event.getEventID());
            } catch (SQLException e){
                System.out.println(e.getMessage());
                //throw e;
            }
        }

        if (validator.isEventValid(event)) {
            Map<String, Object> model = new HashMap<>();
            model.put("eventTitle", event.getTitle());
            model.put("eventDescription", event.getDescription());
            return ViewUtil.render(request, model, "/velocity/participant-event.vm");
        }
        
        return ViewUtil.notFound;
    };

    // public static Route eventEntryPage = (Request request, Response response) -> {
    //     Map<String, Object> model = new HashMap<>();
    //     model.put("test1", "this is a test variable!");
    //     return ViewUtil.render(request, model, "/velocity/joinEvent.vm");
    // };

}

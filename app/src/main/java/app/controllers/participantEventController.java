package app.controllers;

import java.util.*;
import spark.*;
import app.App;
import app.util.*;
import app.objects.*;


public class participantEventController {

    /** Serve the participant-event page (GET request) */
    public static Route servePage = (Request request, Response response) -> {
        //initialise event and input first name and last name
        Event event = null;
        String[] name = request.queryParams("participantName").split(" ");
        //validate input before interact with database
        if (App.getInstance().getValidator().eventCodeIsValid(request.queryParams("participantCode"))&&
            App.getInstance().getValidator().nameIsValid(name[0])&&
            App.getInstance().getValidator().nameIsValid(name[1])
            ) {
            Participant participant = App.getInstance().getDbConnection().createParticipant(request.ip(),name[0],name[1]);
            event = App.getInstance().getDbConnection().getEventByCode(request.queryParams("participantCode"));
            App.getInstance().getDbConnection().addParticipantToEvent(participant.getParticipantID(), event.getEventID());
        }
        //return participant event page if event is found
        if (App.getInstance().getValidator().isEventValid(event)) {
            Map<String, Object> model = new HashMap<>();
            model.put("eventTitle", event.getTitle());
            model.put("eventDescription", event.getDescription());
            return ViewUtil.render(request, model, "/velocity/participant-event.vm");
        }
        //return notfound if event is not found or input is not valid
        return ViewUtil.notFound;
    };

    // public static Route eventEntryPage = (Request request, Response response) -> {
    //     Map<String, Object> model = new HashMap<>();
    //     model.put("test1", "this is a test variable!");
    //     return ViewUtil.render(request, model, "/velocity/joinEvent.vm");
    // };

}

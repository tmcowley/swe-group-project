package app.controllers;

import app.App;
import app.DbConnection;
import app.Validator;
import app.util.*;

// for ViewUtil velocity models
import java.util.HashMap;
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

        // get all feedbacks of the participant in the current event - inject into front-end

        Map<String, Object> model = new HashMap<>();
        return ViewUtil.render(request, model, "/velocity/landing.vm");
    };

}

package app.controllers;

import app.App;
import app.DbConnection;
import app.util.*;

// for ViewUtil velocity models
import java.util.HashMap;
import java.util.Map;

import spark.*;

public class GetCodeController {

    // serve the host-code displaying page (following GET request from host sign-up)
    public static Route servePage = (Request request, Response response) -> {

        System.out.println("\nNotice: GetCodeController:servePage recognized request");

        // get db conn, validator from singleton App instance
        DbConnection db = App.getInstance().getDbConnection();

        // get current session; ensure session is live
        Session session = request.session(true);
        if (session.isNew()) {
            System.out.println("Error:  session not found");
            response.redirect("/error/401");
            return null;
        }

        // ensure host code is set; return user to sign-up page if not
        if (session.attribute("hostCode") == null) {
            System.out.println("Error:  host code not set");
            response.redirect("/host/login");
            return null;
        }

        // collect host-code (attribute already valid)
        String hostCode = session.attribute("hostCode");

        // double-check host code exists
        if (!db.hostCodeExists(hostCode)) {
            System.out.println("Error:  host code invalid or inexistent");
            response.redirect("/host/login"); // response.redirect("/error/401");
            return null;
        }

        Map<String, Object> model = new HashMap<>();
        model.put("hostCode", hostCode);
        return ViewUtil.render(request, model, "/velocity/get-code.vm");
    };

}

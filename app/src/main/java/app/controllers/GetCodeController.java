package app.controllers;

import app.App;
import app.DbConnection;
import app.Validator;
import app.util.*;

// for ViewUtil velocity models
import java.util.HashMap;
import java.util.Map;

import spark.*;


public class GetCodeController {
    
    // serve the host-code displaying page (following GET request from host sign-up)
    public static Route servePage = (Request request, Response response) -> {

        System.out.println("\nNotice: GetCodeController:servePage recognized request");

        DbConnection db = App.getInstance().getDbConnection();
        Validator v = App.getInstance().getValidator();

        Session session = request.session();
        if (session.isNew()) {
            System.out.println("Error:  GetCodeController:servePage session not found");
            response.redirect("/");
            return "Error: Session not found";
        }

        // collect host-code (attribute already valid)
        String hostCode = session.attribute("hostCode");
        
        // hostCode not set -> return user to sign-up page
        if (hostCode == null){
            System.out.println("Error:  GetCodeController:servePage hostCode not set");
            response.redirect("/host/login");
            return null;
        }

        Map<String, Object> model = new HashMap<>();
        model.put("hostCode", hostCode);
        return ViewUtil.render(request, model, "/velocity/get-code.vm");
    };

}

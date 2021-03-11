package app.controllers;

import app.App;
import app.DbConnection;
import app.Validator;
import app.objects.Host;
import app.util.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import spark.*;
import spark.utils.StringUtils;


public class GetCodeController {
    
    // TODO comment
    public static Route servePage = (Request request, Response response) -> {

        System.out.println("\nNotice: GetCodeController:servePage recognized request");

        request.session(true);
        if (request.session().isNew()) {
            System.out.println("Error: GetCodeController:servePage session not found");
            response.redirect("/");
            return "Error: Session not found";
        }
        
        // hostCode not set -> return user to sign-up page
        if (request.session().attribute("hostCode") == null){
            // Error: please ensure you have access to this page
            System.out.println("Error: GetCodeController:servePage hostCode not set");
            response.redirect("/host/login");
            return null;
        }

        // session hostCode attribute already valid
        String hostCode = request.session().attribute("hostCode");

        Map<String, Object> model = new HashMap<>();
        model.put("hostCode", hostCode);
        return ViewUtil.render(request, model, "/velocity/get-code.vm");
    };

}

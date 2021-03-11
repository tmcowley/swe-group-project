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
            return "Error: Session not found";
        }

        if (request.session().attribute("errorMessageGetHostCode") == null)
            request.session().attribute("errorMessageGetHostCode", "");
        
        // host not set -> return user to sign-up page
        if (request.session().attribute("host") == null){
            // Error: please ensure you have access to this page
            System.out.println("Error: GetCodeController:servePage please ensure you have access to this page");
            //request.session().attribute("errorMessageGetHostCode", "Error: please ensure you have access to this page");
            response.redirect("/host/login");
            return null;
        }

        // session host attribute validity pre-established
        Host host = request.session().attribute("host");

        Map<String, Object> model = new HashMap<>();
        model.put("hostCode", host.getHostCode());
        model.put("errorMessageGetHostCode", request.session().attribute("errorMessageGetHostCode"));
        return ViewUtil.render(request, model, "/velocity/get-code.vm");

    };

}

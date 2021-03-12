package app.controllers;

// for ViewUtil velocity models
import java.util.HashMap;
import java.util.Map;

import spark.*;
import spark.utils.StringUtils;
import app.util.*;

public class TemplateEditController {
    
    // serve the Template editor page (in response to GET request)
    public static Route servePage = (Request request, Response response) -> {

        System.out.println("\nNotice: TemplateEditController:servePage recognized request");

        // get current session; ensure session is live
        Session session = request.session(false);
        if (session == null) {
            System.out.println("Error:  TemplateEditController:servePage session not found");
            response.redirect("/error/401");
            return null;
        }

        // collect templateCode from URL-encoded GET parameter 
        String templateCode = request.queryParams("templateCode");
        System.out.println("Notice: templateCode collected: " + templateCode);

        // ensure hostCode is collected
        if (StringUtils.isBlank(templateCode)){
            System.out.println("Error:  TemplateEditController:servePage url encoded hostCode not found");
            response.redirect("/host/templates");
            return null;
        }

        

        Map<String, Object> model = new HashMap<>();
        return ViewUtil.render(request, model, "/velocity/edit-template.vm");
    };

}

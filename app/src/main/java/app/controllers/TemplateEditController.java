package app.controllers;

// for ViewUtil velocity models
import java.util.HashMap;
import java.util.Map;

import spark.*;
import spark.utils.StringUtils;
import app.App;
import app.DbConnection;
import app.Validator;
import app.objects.Template;
import app.util.*;

public class TemplateEditController {
    
    // serve the Template editor page (in response to GET request)
    public static Route servePage = (Request request, Response response) -> {

        System.out.println("\nNotice: TemplateEditController:servePage recognized request");
        DbConnection db = App.getInstance().getDbConnection();
        Validator v = App.getInstance().getValidator();

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

        // ensure host code is collected
        if (StringUtils.isBlank(templateCode)){
            System.out.println("Error:  TemplateEditController:servePage url encoded template code not found");
            response.redirect("/host/templates");
            return null;
        }

        // ensure host code exists in system
        if (!db.templateCodeExists(templateCode)){
            System.out.println("Error:  TemplateEditController:servePage template code does not exist in the system");
            response.redirect("/host/templates");
            return null;
        }

        // get template from template code
        Template template = db.getTemplateByCode(templateCode);

        // ensure template is valid
        if (!v.isTemplateValid(template)){
            System.out.println("Error:  TemplateEditController:servePage template by template code is invalid");
            response.redirect("/host/templates");
            return null;
        }



        // display page
        Map<String, Object> model = new HashMap<>();
        return ViewUtil.render(request, model, "/velocity/edit-template.vm");
    };

}

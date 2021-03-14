package app.controllers;

// for ViewUtil velocity models
import java.util.HashMap;
import java.util.Map;

import spark.*;
import spark.utils.StringUtils;
import app.App;
import app.DbConnection;
import app.Validator;
import app.objects.Host;
import app.objects.Template;
import app.util.*;

public class TemplateEditController {
    
    // serve the template editor page (in response to GET request)
    public static Route servePage = (Request request, Response response) -> {

        System.out.println("\nNotice: TemplateEditController:servePage recognized request");

        // get db conn, validator from singleton App instance
        DbConnection db = App.getInstance().getDbConnection();
        Validator v = App.getInstance().getValidator();

        // get current session; ensure session is live
        Session session = request.session(true);
        if (request.session().isNew()) {
            System.out.println("Error:  session not found");
            response.redirect("/error/401");
            return null;
        }

        // ensure host exists in current session
        if (session.attribute("host") == null){
            System.out.println("Error:  session found, host not in session");
            response.redirect("/error/401");
            return null;
        }

        // // set error messages to empty if unset
        // if (session.attribute("errorMessageCreateTemplate") == null){
        //     session.attribute("errorMessageCreateTemplate", "");
        // }

        // get host from session
        Host host = session.attribute("host");
        int hostID = host.getHostID();

        // collect template code from URL-encoded GET parameter 
        String templateCode = request.queryParams("templateCode");
        //System.out.println("Notice: templateCode collected: " + templateCode);

        // ensure template code is collected
        if (StringUtils.isBlank(templateCode)){
            System.out.println("Error:  url encoded template code not found");
            response.redirect("/host/templates");
            return null;
        }
        //System.out.println("Notice: template code is not blank");

        // ensure template code exists in system
        if (!db.templateCodeExists(templateCode)){
            System.out.println("Error:  template code does not exist in the system");
            response.redirect("/host/templates");
            return null;
        }
        //System.out.println("Notice: template code exists");

        // get template from template code
        Template template = db.getTemplateByCode(templateCode);

        // ensure template is valid
        if (!v.isTemplateValid(template)){
            System.out.println("Error:  template by template code is invalid");
            response.redirect("/host/templates");
            return null;
        }
        //System.out.println("Notice: template code's template is valid");

        // ensure template belongs to the host
        if (template.getHostID() != hostID){
            System.out.println("Error:  template does not belong to the host");
            response.redirect("/host/templates");
            return null;
        }
        //System.out.println("Notice: template belongs to host");

        // generate front-end velocity page
        Map<String, Object> model = new HashMap<>();
        model.put("template", template);
        model.put("errorMessageCreateTemplate", session.attribute("errorMessageCreateTemplate"));

        // unset session error variables
        session.removeAttribute("errorMessageCreateTemplate");

        // render template editor
        return ViewUtil.render(request, model, "/velocity/edit-template.vm");
    };

}

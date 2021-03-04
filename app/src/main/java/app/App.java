package app;

// Spark-Java packages
import static spark.Spark.*;
import spark.Spark;

// Internal packages
import app.controllers.*;
import app.objects.*;
import app.util.*;

// 
import java.sql.SQLException;

public class App{
    public static void main (String[] args) throws SQLException {
        // tell the Spark framework where to find static files
        staticFiles.location("/static");
        Spark.port(4567);

        try{
            // instantiate DB connection
            DbConnection db = new DbConnection();
        } catch (SQLException e){
            System.out.println(e.getMessage());
            //throw e;
        }

        // for each currently running event, generate /event/join/<code>

        //paths
        //get("/", participantEventController.servePage);
        get("/", IndexController.servePage);
        get("/event/join/code", participantEventController.servePage);
        get("/host/login", HostLoginController.servePage);
        get("/host/get-code", null);
        get("/host/home", null);
        get("/host/create-event", EventCreateController.servePage);
        get("/event/host/code", hostEventController.servePage);
        get("/host/templates", null);
        get("/host/templates/new", TemplateCreateController.servePage);
        get("/host/templates/edit/code", null);
        get("/", null);
        
        awaitInitialization();
        System.out.printf("\nRunning at http://localhost:%d\n", Spark.port());
    }
}

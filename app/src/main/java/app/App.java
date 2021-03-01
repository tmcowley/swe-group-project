package app;

// Spark-Java packages
import static spark.Spark.*;
import spark.Spark;

// Internal packages
import app.controllers.*;
import app.objects.*;
import app.util.*;

public class App {
    public static void main(String[] args) {
        // tell the Spark framework where to find static files
        staticFiles.location("/static");
        Spark.port(4567);

        // create a db connection
        try{        
            DbConnection db = new DbConnection();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        
        //paths
        // get("/hello", (req, res) -> "Hello World");
        get("/", indexController.serveIndexPage);
        get("/event/join/code", joinController.serveJoinPage);
        get("/host/login", null);
        get("/host/get-code", null);
        get("/host/home", null);
        get("/host/create-event", null);
        get("/event/host/code", null);
        get("/host/templates", null);
        get("/host/templates/new", null);
        get("/host/templates/edit/code", null);
        get("/", null);
        
        awaitInitialization();
        System.out.printf("\nRunning at http://localhost:%d\n", Spark.port());
    }
}

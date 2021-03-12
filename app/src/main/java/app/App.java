package app;

// Spark-Java packages
import static spark.Spark.*;
import spark.Spark;

// Internal packages
import app.controllers.*;

//SQLException package
import java.sql.SQLException;

public class App {

    // Stores the singleton instance of this class.
    private static App app;

    // Stores the database connection.
    private DbConnection db;

    // Stores the validator class.
    private Validator validator;

    // Gets the singleton instance of this class.
    public static App getInstance() {
        return app;
    }

    // Gets the database connection for this program.
    public DbConnection getDbConnection() {
        return this.db;
    }

    // Gets the validator for this program.
    public Validator getValidator() {
        return this.validator;
    }

    /**
     * Explicitly mark constructor as private so no instances of this class can be
     * created elsewhere.
     */
    private App() {

    }

    /**
     * The main entry point for the application.
     * 
     * @param args The command-line arguments supplied by the OS.
     */
    public static void main(String[] args) throws SQLException {
        // initialize and run the program
        app = new App();
        app.run();
    }

    // Runs the program.
    private void run() throws SQLException {

        // tell the Spark framework where to find static files
        staticFiles.location("/static");
        Spark.port(4567);

        try {
            // instantiate DB connection
            db = new DbConnection();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            //throw e;
        }

        // instantiate validator
        validator = new Validator();

        // TODO: for each currently running event, generate /event/join/<code>

        // API endpoint mappings
        path("/", () -> {
            // landing page GET and POST API endpoint mappings
            get("", IndexController.servePage);
            post("", APIController.joinEvent);
        });
        path("/host", () -> {
            // host GET-API endpoint mappings
            get("/login", HostLoginController.servePage);
            get("/get-code", GetCodeController.servePage);
            get("/home", HostHomeController.servePage);
            get("/create-event", EventCreateController.servePage);
            get("/templates", MyTemplatesController.servePage);
            get("/templates/new", TemplateCreateController.servePage);
            get("/templates/edit/code", TemplateEditController.servePage);
            
            // host POST-API endpoint mappings
            post("/get-code", APIController.createHost);
            post("/home", AuthController.authHost);
            post("/templates/new", APIController.createEmptyTemplate);
        });
        path("/event", () -> {
            // event GET-API endpoint mappings
            get("/host/code", HostEventController.servePage);
            // get("/join/code", participantEventController.servePage);
            get("/participant/feedback", ParticipantEventController.servePage);

            // event POST-API endpoint mappings
            post("/join/code", APIController.joinEvent);
            post("/host/code", APIController.createEvent);
            post("/participant/feedback", APIController.createFeedback);
        });
        path("/error", () -> {
            get("/401", UnauthAccessController.servePage);
        });

        awaitInitialization();
        System.out.printf("\nRunning at http://localhost:%d\n", Spark.port());
    }
}
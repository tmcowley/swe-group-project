package app;

// Spark-Java packages
import static spark.Spark.*;
import spark.Spark;

// Internal packages
import app.controllers.*;
import app.util.ViewUtil;

//SQLException package
import java.sql.SQLException;

public class App {

    // stores the singleton instance of this class
    private static App app;

    // stores the database connection.
    private DbConnection db;

    // stores the validator class.
    private Validator validator;

    // gets the singleton instance of this class
    public static App getInstance() {
        return app;
    }

    // gets the database connection for this program
    public DbConnection getDbConnection() {
        return this.db;
    }

    // gets the validator for this program
    public Validator getValidator() {
        return this.validator;
    }

    // mark constructor as private to prevent external instances
    private App() {
    }

    /**
     * main entry point for the application
     * 
     * @param args command-line arguments supplied by the OS
     */
    public static void main(String[] args) throws SQLException {
        // initialize and run the program
        app = new App();
        app.run();
    }

    // Runs the program.
    private void run() throws SQLException {

        // state location to static files
        staticFiles.location("/static");
        Spark.port(4567);

        try {
            // instantiate DB connection
            db = new DbConnection();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            // throw e;
        }

        // instantiate validator
        validator = new Validator();

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
            // host POST-API endpoint mappings
            post("/get-code", APIController.createHost);
            post("/home", AuthController.authHost);
            // template API endpoint mappings
            path("/templates", () -> {
                // template GET-API endpoint mappings
                get("", MyTemplatesController.servePage);
                get("/new", TemplateCreateController.servePage);
                get("/edit/code", TemplateEditController.servePage);
                // template POST-API endpoint mappings
                post("/new", APIController.createEmptyTemplate);
                post("/edit/code/save", APIController.fillTemplate);
                post("/edit/code/create-empty-component", APIController.createEmptyComponent);
                post("/edit/code/delete-template", APIController.deleteTemplate);
                post("/edit/code/delete-template-component", APIController.deleteTemplateComponent);
            });
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
            get("/401", ViewUtil.unauthAccess);
            get("/404", ViewUtil.notFound);
            get("/406", ViewUtil.notAcceptable);
        });

        // map page not found error to page
        notFound(ViewUtil.notFound);

        awaitInitialization();
        System.out.printf("\nRunning at http://localhost:%d\n", Spark.port());
    }
}
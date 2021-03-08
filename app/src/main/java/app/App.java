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
            throw e;
        }

        // instantiate validator
        validator = new Validator();

        // for each currently running event, generate /event/join/<code>

        // paths
        // get("/", participantEventController.servePage);
        get("/", IndexController.servePage);
        get("/event/join/code", participantEventController.servePage);
        get("/host/login", HostLoginController.servePage);
        get("/host/get-code", GetCodeController.servePage);
        get("/host/home", HostHomeController.servePage);
        get("/host/create-event", EventCreateController.servePage);
        get("/event/host/code", hostEventController.servePage);
        get("/host/templates", null);
        get("/host/templates/new", TemplateCreateController.servePage);
        get("/host/templates/edit/code", null);
        // get("/", null);

        // API endpoints
        post("/host/get-code", APIController.createHost);
        post("/", APIController.joinEvent);

        awaitInitialization();
        System.out.printf("\nRunning at http://localhost:%d\n", Spark.port());
    }
}
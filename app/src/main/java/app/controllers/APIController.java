package app.controllers;

 import java.util.*;
 import java.sql.SQLException;
 import spark.*;
 import static spark.Spark.*;
import java.net.*;
import java.io.*;

public class APIController {
    // /** Serve the index page (GET request) */
    // public static Route serveIndexPage = (Request request, Response response) -> {
    //     Map<String, Object> model = new HashMap<>();
    //     model.put("test1", "this is a test variable!");
    //     return ViewUtil.render(request, model, "/velocity/index.vm");
    // };

    public static Route createHost = (Request request, Response response) -> {
    };

    public static Route createEvent = (Request request, Response response) -> {
    };

    public static Route joinEvent = (Request request, Response response) -> {
    };

    public static Route createParticipant = (Request request, Response response) -> {
    };

    public static Route createFeedback = (Request request, Response response) -> {
    };
}
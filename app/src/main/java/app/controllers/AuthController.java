package app.controllers;

 import java.util.*;
 import java.sql.SQLException;
 import spark.*;
 import static spark.Spark.*;
import java.net.*;
import java.io.*;

public class AuthController {
    // /** Serve the index page (GET request) */
    // public static Route serveIndexPage = (Request request, Response response) -> {
    //     Map<String, Object> model = new HashMap<>();
    //     model.put("test1", "this is a test variable!");
    //     return ViewUtil.render(request, model, "/velocity/index.vm");
    // };

    public static Route authHost = (Request request, Response response) -> {
        return null;
    };

    public static Route authParticipant = (Request request, Response response) -> {
        return null;
    };

}

package app.controllers;

import java.util.*;
import java.net.*;
import java.io.*;
import spark.*;
import app.util.*;

public class joinController {
    /** Serve the join page (GET request) */
    public static Route serveJoinPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        model.put("test1", "this is a test variable!");
        return ViewUtil.render(request, model, "/velocity/index.vm");
    };

}
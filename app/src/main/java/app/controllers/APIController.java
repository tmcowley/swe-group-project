package app.controllers;

import java.util.*;

import app.util.ViewUtil;

import java.sql.SQLException;
import spark.*;
import static spark.Spark.*;
import java.net.*;
import java.io.*;

public class APIController {

    // form sent from front-end to back-end to create host
    public static Route createHost = (Request request, Response response) -> {
        // 1. ensure validity of form (for each input field)
        // 2. create Host, get Host, get Host code
        // 3. send host-code back somehow
        return null;
    };

    // form sent by host to create an event
    public static Route createEvent = (Request request, Response response) -> {
        return null;
    };

    // form sent by participant to join an event
    public static Route joinEvent = (Request request, Response response) -> {
        return null;
    };

    // form sent by user to create participant
    public static Route createParticipant = (Request request, Response response) -> {
        return null;
    };

    // form sent by participant (in event) to create an instance of feedback
    public static Route createFeedback = (Request request, Response response) -> {
        return null;
    };
}
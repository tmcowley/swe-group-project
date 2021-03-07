package app.controllers;

import java.util.*;

import app.App;
import app.DbConnection;
import app.Validator;
import app.objects.*;
import app.util.ViewUtil;

import java.sql.SQLException;
import spark.*;
import static spark.Spark.*;
import java.net.*;
import java.io.*;

public class APIController {

    // thread safe - no DB interaction
    static Validator v = new Validator();

    // form sent from front-end to back-end to create host
    public static Route createHost = (Request request, Response response) -> {
        DbConnection db = new DbConnection();
        System.out.println("This worked??? \n\n");
        System.out.println(request.queryParams("hostFName"));
        String FName = request.queryParams("hostFName");
        String LName = request.queryParams("hostLName");
        String Email = request.queryParams("hostEmail");
        if(v.nameIsValid(FName) && v.nameIsValid(LName) && v.eAddressIsValid(Email)){
            Host host = App.getInstance().getDbConnection().createHost(FName,LName,"192.168.1.1",Email);
            String HostCode = host.getHostCode();
        }
        //TODO: get IP
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
        // String FName = request.queryParams("hostFName");
        // String LName = request.queryParams("hostLName");
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
package app;

import static spark.Spark.*;

import org.apache.velocity.app.*;
import org.apache.velocity.runtime.*;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.VelocityContext;
import java.io.StringWriter;
import java.util.*;
import spark.*;
import spark.template.velocity.*;

import org.eclipse.jetty.http.*;

import java.net.*;
import app.*;

import spark.Spark;
import static spark.Spark.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.nio.file.Paths;

import org.eclipse.jetty.http.*;

import app.objects.*;
import app.controllers.*;
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

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

import app.Event;
import app.controllers.*;
import app.util.*;

public class App {
    public static void main(String[] args) {
        // tell the Spark framework where to find static files
        staticFiles.location("/static");
        Spark.port(4567);

        try{        
            DbConnection db = new DbConnection();
        } catch (Exception e){
            ;
        }
        
        //paths
        get("/hello", (req, res) -> "Hello World");
        get("/", IndexController.serveIndexPage);
        
        awaitInitialization();
        System.out.printf("\nRunning at http://localhost:%d\n", Spark.port());
    }
}

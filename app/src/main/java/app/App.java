package app;

// /**
//  * Hello world!
//  *
//  */
// public class App 
// {
//     public static void main( String[] args )
//     {
//         System.out.println( "Hello World!" );
//     }
// }

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

public class App {
    public static void main(String[] args) {
        get("/", (req, res) -> "Hello World");

        // Validator v = new Validator();

    }
}

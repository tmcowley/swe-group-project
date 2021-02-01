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

public class App {
    public static void main(String[] args) {
        get("/hello", (req, res) -> "Hello World");
        get("/yoitworkd", (req, res) -> "hello there");
        get("/hola", (req, res) -> "holla");
    }
}

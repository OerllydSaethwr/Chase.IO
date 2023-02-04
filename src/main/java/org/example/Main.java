package org.example;

import spark.Spark;
import spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        Spark.get("/hello", (req, res) -> "Hello World");
        Spark.get("/bye", (req, res) -> "bye World");
    }
}
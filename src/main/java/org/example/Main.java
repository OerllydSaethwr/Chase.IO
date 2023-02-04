package org.example;

import spark.Spark;
import spark.Spark.*;


public class Main {
    public static void main(String[] args) {
        Spark.post("/start_game", (req, res) -> {
             System.out.println(req.attributes());
        });
    }
}
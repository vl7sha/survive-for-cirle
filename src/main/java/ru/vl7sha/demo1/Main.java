package ru.vl7sha.demo1;


import ru.vl7sha.demo1.core.Game;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        Game game = new Game(primaryStage);
        game.start();
    }
    
    public static void main(String[] args) {
        // Check for network mode
        boolean networkMode = false;
        String serverIp = "localhost";
        
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--connect") && i + 1 < args.length) {
                networkMode = true;
                serverIp = args[i + 1];
                break;
            }
        }
        
        // Start the JavaFX application
        launch(args);
    }
}
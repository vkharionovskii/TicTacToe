package src.server;
import java.io.*;
import java.net.*;

import src.game.Game;
import src.constants.Constants;


public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(Constants.SERVER_PORT);
            System.out.println("Server started...");
            
            Socket playerX = serverSocket.accept();
            System.out.println("Player " + Constants.PALYER_1_SYMBOL + " connected...");
            Socket playerO = serverSocket.accept();
            System.out.println("Player " + Constants.PALYER_2_SYMBOL + " connected...");

            Game game = new Game(playerX, playerO);
            game.start();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

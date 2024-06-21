package src.client;
import java.io.*;
import java.net.*;

import src.constants.Constants;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket(Constants.SERVER_HOST, Constants.SERVER_PORT);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

            String response = input.readLine();
            if (response.startsWith("START")) {
                char player = response.charAt(6);
                System.out.println("You are player " + player);
            }
            
            while (true) {
                response = input.readLine();
                if (response.startsWith("YOUR_MOVE")) {
                    System.out.println("Enter row and column (0-" + (Constants.BOARD_SIZE - 1) + ") separated by comma:");
                    String move = consoleInput.readLine();
                    output.println(move);
                } else if (response.startsWith("OPPONENT_MOVE")) {
                    String[] moveParts = response.split(" ");
                    String[] coordinates = moveParts[1].split(",");
                    int row = Integer.parseInt(coordinates[0]);
                    int col = Integer.parseInt(coordinates[1]);
                    System.out.println("Opponent's move: row=" + row + ", col=" + col);
                } else if (response.startsWith("GAME_OVER")) {
                    char winner = response.charAt(10);
                    if (winner == Constants.EMPTY_SYMBOL) {
                        System.out.println("Board is filled");
                    } else {
                        System.out.println("Player " + winner + " wins!");
                    }
                    break;
                } else {
                    System.out.println(response);
                }
            }
            
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

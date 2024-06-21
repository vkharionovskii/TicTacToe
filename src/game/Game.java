package src.game;
import java.io.*;
import java.net.*;
import java.util.Random;

import src.constants.Constants;

public class Game {
    private Socket playerX;
    private Socket playerO;
    private BufferedReader inputX;
    private PrintWriter outputX;
    private BufferedReader inputO;
    private PrintWriter outputO;
    
    private char[][] board;
    private char currentPlayer;
    
    public Game(Socket playerX, Socket playerO) {
        this.playerX = playerX;
        this.playerO = playerO;
        
        try {
            inputX = new BufferedReader(new InputStreamReader(playerX.getInputStream()));
            outputX = new PrintWriter(playerX.getOutputStream(), true);

            inputO = new BufferedReader(new InputStreamReader(playerO.getInputStream()));
            outputO = new PrintWriter(playerO.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        board = new char[Constants.BOARD_SIZE][Constants.BOARD_SIZE];

        Random random = new Random();
        currentPlayer = Constants.PALYER_1_SYMBOL;
        if (random.nextBoolean()) {
            currentPlayer = Constants.PALYER_2_SYMBOL;
        }

        initializeBoard();
    }
    
    private void initializeBoard() {
        for (int i = 0; i < Constants.BOARD_SIZE; ++i) {
            for (int j = 0; j < Constants.BOARD_SIZE; ++j) {
                board[i][j] = Constants.EMPTY_SYMBOL;
            }
        }
    }

    private void printBoard() {
        String str_board = "\n";

        for (int i = 0; i < Constants.BOARD_SIZE; ++i) {
            
            for (int j = 0; j < Constants.BOARD_SIZE; ++j) {
                str_board += board[i][j] + " ";
            }
            str_board += "\n";
        }

        outputX.println(str_board);
        outputO.println(str_board);
    }

    public void start() {
        try {
            outputX.println("START " + Constants.PALYER_1_SYMBOL);
            outputO.println("START " + Constants.PALYER_2_SYMBOL);
            printBoard();

            Integer row = 0, col = 0;
            while (!isGameOver()) {
                if (currentPlayer == Constants.PALYER_1_SYMBOL) {
                    if (!moveHandler(inputX, outputX, outputO, row, col)) {
                        printBoard();
                        continue;
                    }
                    currentPlayer = Constants.PALYER_2_SYMBOL;
                } else {
                    if (!moveHandler(inputO, outputO, outputX, row, col)) {
                        printBoard();
                        continue;
                    }
                    currentPlayer = Constants.PALYER_1_SYMBOL;
                }
                printBoard();
            }
            
            outputX.println("GAME_OVER " + checkWinner());
            outputO.println("GAME_OVER " + checkWinner());
            
            playerX.close();
            playerO.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean moveHandler(BufferedReader input_current_player, PrintWriter output_current_player, PrintWriter output_opponent_player, Integer row, Integer col) {
        try {
            output_current_player.println("YOUR_MOVE");
            String move = input_current_player.readLine();
            if (!move.matches("\\d+,\\d+")) {
                output_current_player.println("Invalid input! Try again: format {row},{col}");
                return false;
            }
            row = Integer.parseInt(move.split(",")[0]);
            col = Integer.parseInt(move.split(",")[1]);
            if (!makeMove(row, col)) {
                output_current_player.println("Cell already is busy, select another cell!");
                printBoard();
                return false;
            }
            output_opponent_player.println("OPPONENT_MOVE " + row + "," + col);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isGameOver() {
        return checkWinner() != Constants.EMPTY_SYMBOL || isBoardFull();
    }
    
    private boolean isBoardFull() {
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                if (board[i][j] == Constants.EMPTY_SYMBOL) {
                    return false;
                }
            }
        }
        return true;
    }

    private char checkWinner() {
        int player_1 = 0;
        int player_2 = 0;

        // Проверка строк
        for (int i = 0; i < Constants.BOARD_SIZE; ++i) {
            player_1 = player_2 = 0;
            for (int j = 0; j < Constants.BOARD_SIZE; ++j) {
                if (board[i][j] == Constants.PALYER_1_SYMBOL) {
                    ++player_1;
                } else if (board[i][j] == Constants.PALYER_2_SYMBOL) {
                    ++player_2;
                }
            }
            if (player_1 == Constants.BOARD_SIZE || player_2 == Constants.BOARD_SIZE) {
                return board[i][0];
            }
        }
        
        // Проверка столбцов
        for (int i = 0; i < Constants.BOARD_SIZE; ++i) {
            player_1 = player_2 = 0;
            for (int j = 0; j < Constants.BOARD_SIZE; ++j) {
                if (board[j][i] == Constants.PALYER_1_SYMBOL) {
                    ++player_1;
                } else if (board[j][i] == Constants.PALYER_2_SYMBOL) {
                    ++player_2;
                }
            }
            if (player_1 == Constants.BOARD_SIZE || player_2 == Constants.BOARD_SIZE) {
                return board[0][i];
            }
        }
        
        // Проверка диагоналей
        player_1 = player_2 = 0;
        int max_index = Constants.BOARD_SIZE - 1;
        for (int i = 0; i < Constants.BOARD_SIZE; ++i) {
            for (int j = 0; j < Constants.BOARD_SIZE; ++j) {
                if ((i == j || i + j == max_index)) {
                    if (board[i][j] == Constants.PALYER_1_SYMBOL) {
                        ++player_1;
                    } else if (board[i][j] == Constants.PALYER_2_SYMBOL) {
                        ++player_2;
                    }
                }
            }
            if (player_1 == Constants.BOARD_SIZE || player_2 == Constants.BOARD_SIZE) {
                return board[Constants.BOARD_SIZE / 2][Constants.BOARD_SIZE / 2];
            }
        }

        return Constants.EMPTY_SYMBOL;
    }
    
    private boolean makeMove(int row, int col) {
        if (row >= Constants.BOARD_SIZE || col >= Constants.BOARD_SIZE) {
            return false;
        }
        if (board[row][col] == Constants.EMPTY_SYMBOL) {
            board[row][col] = currentPlayer;
            return true;
        }
        return false;
    }
}

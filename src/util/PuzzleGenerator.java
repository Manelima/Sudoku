package util;

import java.util.*;

public class PuzzleGenerator {

    private int[][] board;
    private static final int BOARD_SIZE = 9;

    public PuzzleGenerator() {
        this.board = new int[BOARD_SIZE][BOARD_SIZE];
    }

    public static Map<String, String> generate(int difficulty) {
        PuzzleGenerator generator = new PuzzleGenerator();
        generator.fillBoard();
        generator.removeNumbers(difficulty);

        Map<String, String> gameConfig = new HashMap<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (generator.board[row][col] != 0) {
                    String key = String.format("%d,%d", row, col);
                    String value = String.format("%d,true", generator.board[row][col]);
                    gameConfig.put(key, value);
                }
            }
        }
        return gameConfig;
    }

    private boolean fillBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == 0) {
                    List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
                    Collections.shuffle(numbers);
                    for (int num : numbers) {
                        if (isValidPlacement(row, col, num)) {
                            board[row][col] = num;
                            if (fillBoard()) {
                                return true;
                            } else {
                                board[row][col] = 0;
                            }
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private void removeNumbers(int count) {
        Random rand = new Random();
        int removed = 0;
        while (removed < count) {
            int row = rand.nextInt(BOARD_SIZE);
            int col = rand.nextInt(BOARD_SIZE);

            if (board[row][col] != 0) {
                board[row][col] = 0;
                removed++;
            }
        }
    }

    private boolean isValidPlacement(int row, int col, int num) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[row][i] == num || board[i][col] == num) {
                return false;
            }
        }

        int sectorRowStart = row - row % 3;
        int sectorColStart = col - col % 3;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board[sectorRowStart + r][sectorColStart + c] == num) {
                    return false;
                }
            }
        }
        return true;
    }
}
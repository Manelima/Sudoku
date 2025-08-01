import model.Board;
import model.Space;
import util.Difficulty;
import util.PuzzleGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static util.BoardTemplate.BOARD_TEMPLATE;
import static java.util.Objects.isNull;

public class Main {

    private final static Scanner scanner = new Scanner(System.in);
    private static Board board;
    private final static int BOARD_LIMIT = 9;

    public static void main(String[] args) {
        var option = -1;
        while (true) {
            System.out.println("Selecione uma das opções a seguir");
            System.out.println("1 - Iniciar um novo Jogo");
            System.out.println("2 - Colocar um novo número");
            System.out.println("3 - Remover um número");
            System.out.println("4 - Visualizar jogo atual");
            System.out.println("5 - Verificar status do jogo");
            System.out.println("6 - limpar jogo");
            System.out.println("7 - Finalizar jogo");
            System.out.println("8 - Sair");

            option = scanner.nextInt();

            switch (option) {
                case 1:
                    Difficulty difficulty = selectDifficulty();
                    var positions = PuzzleGenerator.generate(difficulty.getCellsToRemove());
                    startGame(positions);
                    break;
                case 2:
                    inputNumber();
                    break;
                case 3:
                    removeNumber();
                    break;
                case 4:
                    showCurrentGame();
                    break;
                case 5:
                    showGameStatus();
                    break;
                case 6:
                    clearGame();
                    break;
                case 7:
                    finishGame();
                    break;
                case 8:
                    System.exit(0);
                default:
                    System.out.println("Opção inválida, selecione uma das opções do menu");
            }
        }
    }

    private static Difficulty selectDifficulty() {
        System.out.println("Selecione o nível de dificuldade:");
        System.out.println("1 - Fácil");
        System.out.println("2 - Médio");
        System.out.println("3 - Difícil");
        int choice = runUntilGetValidNumber(1, 3);
        return switch (choice) {
            case 1 -> Difficulty.FACIL;
            case 3 -> Difficulty.DIFICIL;
            default -> Difficulty.MEDIO;
        };
    }

    private static void startGame(final Map<String, String> positions) {
        List<List<Space>> spaces = new ArrayList<>();
        for (int i = 0; i < BOARD_LIMIT; i++) {
            spaces.add(new ArrayList<>());
            for (int j = 0; j < BOARD_LIMIT; j++) {
                String positionKey = String.format("%d,%d", i, j);
                String positionConfig = positions.get(positionKey);

                if (positionConfig != null) {
                    var configParts = positionConfig.split(",");
                    var expected = Integer.parseInt(configParts[0]);
                    var currentSpace = new Space(expected, true);
                    spaces.get(i).add(currentSpace);
                } else {
                    var emptySpace = new Space(0, false);
                    spaces.get(i).add(emptySpace);
                }
            }
        }

        board = new Board(spaces);
        System.out.println("Novo jogo gerado! O jogo está pronto para começar.");
    }

    private static void inputNumber() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        System.out.println("Informe a linha em que o número será inserido (0-8)");
        var row = runUntilGetValidNumber(0, 8);
        System.out.println("Informe a coluna em que o número será inserido (0-8)");
        var col = runUntilGetValidNumber(0, 8);
        System.out.printf("Informe o número que vai entrar na posição [%s,%s]\n", row, col);
        var value = runUntilGetValidNumber(1, 9);
        if (!board.changeValue(row, col, value)) {
            System.out.printf("A posição [%s,%s] tem um valor fixo\n", row, col);
        }
    }

    private static void removeNumber() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        System.out.println("Informe a linha do número a ser removido (0-8)");
        var row = runUntilGetValidNumber(0, 8);
        System.out.println("Informe a coluna do número a ser removido (0-8)");
        var col = runUntilGetValidNumber(0, 8);
        if (!board.clearValue(row, col)) {
            System.out.printf("A posição [%s,%s] tem um valor fixo\n", row, col);
        }
    }

    private static void showCurrentGame() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        var args = new Object[81];
        var argPos = 0;
        for (int i = 0; i < BOARD_LIMIT; i++) {
            for (int j = 0; j < BOARD_LIMIT; j++) {
                Integer actual = board.getSpaces().get(i).get(j).getActual();
                args[argPos++] = " " + (isNull(actual) || actual == 0 ? " " : actual);
            }
        }
        System.out.println("Seu jogo se encontra da seguinte forma");
        System.out.printf((BOARD_TEMPLATE) + "\n", args);
    }

    private static void showGameStatus() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        System.out.printf("O jogo atualmente se encontra no status %s\n", board.getStatus().getLabel());
        if (board.hasErrors()) {
            System.out.println("O jogo contém erros");
        } else {
            System.out.println("O jogo não contém erros");
        }
    }

    private static void clearGame() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        System.out.println("Tem certeza que deseja limpar seu jogo e perder todo seu progresso?");
        var confirm = scanner.next();
        while (!confirm.equalsIgnoreCase("sim") && !confirm.equalsIgnoreCase("não")) {
            System.out.println("Informe 'sim' ou 'não'");
            confirm = scanner.next();
        }

        if (confirm.equalsIgnoreCase("sim")) {
            board.reset();
        }
    }

    private static void finishGame() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        if (board.gameIsFinished()) {
            System.out.println("Parabéns você concluiu o jogo");
            showCurrentGame();
            board = null;
        } else if (board.hasErrors()) {
            System.out.println("Seu jogo contém erros, verifique seu board e ajuste-o");
        } else {
            System.out.println("Você ainda precisa preencher algum espaço");
        }
    }

    private static int runUntilGetValidNumber(final int min, final int max) {
        var current = scanner.nextInt();
        while (current < min || current > max) {
            System.out.printf("Informe um número entre %s e %s\n", min, max);
            current = scanner.nextInt();
        }
        return current;
    }
}
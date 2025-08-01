import ui_custom.screen.MainScreen;
import util.Difficulty;
import util.PuzzleGenerator;
import javax.swing.JOptionPane;
import java.util.Map;

public class UIMain {

    public static void main(String[] args) {
        Difficulty difficulty = selectDifficulty();
        if (difficulty == null) {
            System.exit(0);
        }

        final Map<String, String> gameConfig = PuzzleGenerator.generate(difficulty.getCellsToRemove());
        MainScreen mainsScreen = new MainScreen(gameConfig);
        mainsScreen.buildMainScreen();
    }

    private static Difficulty selectDifficulty() {
        Object[] options = { "Fácil", "Médio", "Difícil" };
        int choice = JOptionPane.showOptionDialog(null,
                "Selecione o nível de dificuldade para começar.",
                "Dificuldade do Sudoku",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

        return switch (choice) {
            case 0 -> Difficulty.FACIL;
            case 1 -> Difficulty.MEDIO;
            case 2 -> Difficulty.DIFICIL;
            default -> null;
        };
    }
}
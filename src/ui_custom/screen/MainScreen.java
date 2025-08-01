package ui_custom.screen;

import model.Space;
import service.BoardService;
import service.NotifierService;
import ui_custom.button.CheckGameStatusButton;
import ui_custom.button.FinishGameButton;
import ui_custom.button.ResetButton;
import ui_custom.frame.MainFrame;
import ui_custom.input.NumberText;
import ui_custom.panel.MainPanel;
import ui_custom.panel.SudokuSector;
import util.PuzzleGenerator;

import javax.swing.*;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static service.EventEnum.CLEAR_SPACE;
import static javax.swing.JOptionPane.*;

public class MainScreen {

    private final static Dimension dimension = new Dimension(600, 600);

    private BoardService boardService;
    private final NotifierService notifierService;

    private JButton checkGameStatusButton;
    private JButton finishGameButton;
    private JButton resetButton;
    private MainFrame mainFrame;

    public MainScreen(final Map<String, String> gameConfig) {
        this.boardService = new BoardService(gameConfig);
        this.notifierService = new NotifierService();
    }

    public void buildMainScreen() {
        JPanel mainPanel = new MainPanel(dimension);
        this.mainFrame = new MainFrame(dimension, mainPanel);

        for (int r = 0; r < 9; r += 3) {
            for (int c = 0; c < 9; c += 3) {
                var spaces = getSpacesFromSector(boardService.getSpaces(), c, r);
                JPanel sector = generateSection(spaces);
                mainPanel.add(sector);
            }
        }
        addResetButton(mainPanel);
        addCheckGameStatusButton(mainPanel);
        addFinishGameButton(mainPanel);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private List<Space> getSpacesFromSector(final List<List<Space>> spaces, final int startCol, final int startRow) {
        List<Space> spaceSector = new ArrayList<>();
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                spaceSector.add(spaces.get(r).get(c));
            }
        }
        return spaceSector;
    }

    private JPanel generateSection(final List<Space> spaces) {
        List<NumberText> fields = new ArrayList<>(spaces.stream().map(NumberText::new).toList());
        fields.forEach(t -> notifierService.subscribe(CLEAR_SPACE, t));
        return new SudokuSector(fields);
    }

    private void restartGame() {
        mainFrame.dispose();

        Map<String, String> newGameConfig = PuzzleGenerator.generate(40);
        MainScreen newScreen = new MainScreen(newGameConfig);
        newScreen.buildMainScreen();
    }

    private void addFinishGameButton(final JPanel mainPanel) {
        finishGameButton = new FinishGameButton(e -> {
            if (boardService.gameIsFinished()) {
                Object[] options = {"Reiniciar Jogo", "Fechar"};
                int choice = JOptionPane.showOptionDialog(null,
                        "Parabéns, você concluiu o jogo!",
                        "Fim de Jogo",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        options[0]);

                if (choice == 0) { // 0 é "Reiniciar Jogo"
                    restartGame();
                } else {
                    System.exit(0);
                }
            } else {
                var message = "Seu jogo tem alguma inconsistência ou está incompleto. Ajuste e tente novamente.";
                showMessageDialog(null, message);
            }
        });
        mainPanel.add(finishGameButton);
    }

    private void addCheckGameStatusButton(final JPanel mainPanel) {
        checkGameStatusButton = new CheckGameStatusButton(e -> {
            var hasErrors = boardService.hasErrors();
            var gameStatus = boardService.getStatus();
            var message = switch (gameStatus) {
                case NON_STARTED -> "O jogo não foi iniciado";
                case INCOMPLETE -> "O jogo está incompleto";
                case COMPLETE -> "O jogo está completo";
            };
            message += hasErrors ? " e contém erros" : " mas não contém erros";
            showMessageDialog(null, message);
        });
        mainPanel.add(this.checkGameStatusButton);
    }

    private void addResetButton(final JPanel mainPanel) {
        resetButton = new ResetButton(e -> {
            var dialogResult = showConfirmDialog(
                    null,
                    "Deseja realmente reiniciar o jogo? Todo o progresso será perdido.",
                    "Limpar o jogo",
                    YES_NO_OPTION,
                    QUESTION_MESSAGE
            );
            if (dialogResult == 0) {
                boardService.reset();
                notifierService.notify(CLEAR_SPACE);
            }
        });
        mainPanel.add(resetButton);
    }
}
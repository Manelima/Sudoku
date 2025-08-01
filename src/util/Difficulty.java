package util;

public enum Difficulty {
    FACIL(10),
    MEDIO(30),
    DIFICIL(40);

    private final int cellsToRemove;

    Difficulty(int cellsToRemove) {
        this.cellsToRemove = cellsToRemove;
    }

    public int getCellsToRemove() {
        return cellsToRemove;
    }
}
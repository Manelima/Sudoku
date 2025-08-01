package model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static model.GameStatusEnum.COMPLETE;
import static model.GameStatusEnum.INCOMPLETE;
import static model.GameStatusEnum.NON_STARTED;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Board {

    private final List<List<Space>> spaces;

    public Board(final List<List<Space>> spaces) {
        this.spaces = spaces;
    }

    public List<List<Space>> getSpaces() {
        return spaces;
    }

    public GameStatusEnum getStatus(){
        if (spaces.stream().flatMap(Collection::stream).noneMatch(s -> !s.isFixed() && nonNull(s.getActual()))){
            return NON_STARTED;
        }

        return spaces.stream().flatMap(Collection::stream)
                .anyMatch(s -> isNull(s.getActual()) || s.getActual() == 0) ? INCOMPLETE : COMPLETE;
    }

    public boolean hasErrors(){
        if(getStatus() == NON_STARTED){
            return false;
        }

        for (int i = 0; i < 9; i++) {
            if (!isRowValid(i) || !isColumnValid(i)) {
                return true;
            }
        }

        for (int row = 0; row < 9; row += 3) {
            for (int col = 0; col < 9; col += 3) {
                if (!isSectorValid(row, col)) {
                    return true;
                }
            }
        }

        return false;
    }


    private boolean isRowValid(int rowIndex) {
        List<Space> row = spaces.get(rowIndex);
        return !hasDuplicates(row);
    }

    private boolean isColumnValid(int colIndex) {
        List<Space> column = spaces.stream()
                .map(row -> row.get(colIndex))
                .collect(Collectors.toList());
        return !hasDuplicates(column);
    }

    private boolean isSectorValid(int startRow, int startCol) {
        List<Space> sector = new java.util.ArrayList<>();
        for (int row = startRow; row < startRow + 3; row++) {
            for (int col = startCol; col < startCol + 3; col++) {
                sector.add(spaces.get(row).get(col));
            }
        }
        return !hasDuplicates(sector);
    }

    private boolean hasDuplicates(List<Space> cells) {
        Set<Integer> seenNumbers = new HashSet<>();
        List<Integer> numbersInCells = cells.stream()
                .map(Space::getActual)
                .filter(Objects::nonNull)
                .filter(num -> num != 0)
                .collect(Collectors.toList());

        for (Integer number : numbersInCells) {
            if (seenNumbers.contains(number)) {
                return true;
            }
            seenNumbers.add(number);
        }
        return false;
    }



    public boolean changeValue(final int col, final int row, final int value){

        var space = spaces.get(col).get(row);
        if (space.isFixed()){
            return false;
        }

        space.setActual(value);
        return true;
    }

    public boolean clearValue(final int col, final int row){
        var space = spaces.get(col).get(row);
        if (space.isFixed()){
            return false;
        }

        space.clearSpace();
        return true;
    }

    public void reset(){
        spaces.forEach(c -> c.forEach(Space::clearSpace));
    }

    public boolean gameIsFinished(){
        return !hasErrors() && getStatus().equals(COMPLETE);
    }
}
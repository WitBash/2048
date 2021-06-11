package com.javarush.task.task35.task3513;

public class MoveEfficiency implements Comparable<MoveEfficiency> {
    private int numberOfEmptyTiles;
    private int score;
    private Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public int compareTo(MoveEfficiency moveEfficiency) {
        if (numberOfEmptyTiles == moveEfficiency.numberOfEmptyTiles && score == moveEfficiency.score) return 0;
        else if (numberOfEmptyTiles > moveEfficiency.numberOfEmptyTiles | score > moveEfficiency.score) return 1;
        else return  -1;
    }
}

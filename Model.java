package com.javarush.task.task35.task3513;

import java.util.*;

public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles;
    int score;
    int maxTile;

    private Stack<Tile[][]> previousStates = new Stack();
    private Stack<Integer> previousScores = new Stack();

    private boolean isSaveNeeded = true;

    public Model() {
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        score = 0;
        maxTile = 0;
        resetGameTiles();
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    void resetGameTiles() {
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    private List<Tile> getEmptyTiles() {
        List<Tile> tileList = new ArrayList<>();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].value == 0) tileList.add(gameTiles[i][j]);
            }
        }
        return tileList;
    }

    private void addTile() {
        if (!getEmptyTiles().isEmpty()) {
            int index = (int) (getEmptyTiles().size() * Math.random());
            getEmptyTiles().get(index).value = Math.random() < 0.9 ? 2 : 4;
        }
    }

    public boolean canMove() {
        if (getEmptyTiles().size() > 0) return true;
        for (int i = 0; i < FIELD_WIDTH - 1; i++) {
            for (int j = 0; j < FIELD_WIDTH - 1; j++) {
                if (gameTiles[i][j].value == gameTiles[i][j + 1].value || gameTiles[i][j].value == gameTiles[i + 1][j].value)
                    return true;
            }
        }
        return false;
    }

    public void left() {
        if (isSaveNeeded) saveState(gameTiles);
        boolean moveFlag = false;
        for (int i = 0; i < gameTiles.length; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) moveFlag = true;
        }
        if (moveFlag) addTile();
        isSaveNeeded = true;
    }

    public void down() {
        saveState(gameTiles);
        rotate(gameTiles);
        left();
        rotate(gameTiles);
        rotate(gameTiles);
        rotate(gameTiles);
    }

    public void right() {
        saveState(gameTiles);
        rotate(gameTiles);
        rotate(gameTiles);
        left();
        rotate(gameTiles);
        rotate(gameTiles);
    }

    public void up() {
        saveState(gameTiles);
        rotate(gameTiles);
        rotate(gameTiles);
        rotate(gameTiles);
        left();
        rotate(gameTiles);
    }


    private void rotate(Tile[][] gameTiles) {
        Tile[][] tempGameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                tempGameTiles[i][FIELD_WIDTH - 1 - j] = gameTiles[j][i];
            }
        }
        this.gameTiles = tempGameTiles;
    }


    private boolean compressTiles(Tile[] tiles) {
        int count = 0;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (tiles[i].value == 0) count++;
        }
        if (count == 0 | count == FIELD_WIDTH) return false;
        else {
            int hash = Arrays.hashCode(tiles);
            Arrays.sort(tiles, Comparator.comparing(Tile::isEmpty));
            return hash != Arrays.hashCode(tiles);
//            List<Tile> tileFull = new ArrayList<>();
//            List<Tile> tileEmpty = new ArrayList<>();
//            for (int i = 0; i < FIELD_WIDTH; i++) {
//                if (tiles[i].value == 0) tileEmpty.add(tiles[i]);
//                else tileFull.add(tiles[i]);
//            }
//            for (int i = 0; i < tileFull.size(); i++) {
//                tiles[i] = tileFull.get(i);
//            }
//            for (int i = 0; i < tileEmpty.size(); i++) {
//                tiles[i + tileFull.size()] = tileEmpty.get(i);
//            }
//            return true;
        }
    }

    private boolean mergeTiles(Tile[] tiles) {
        int count = 0;
        for (int i = 0; i < FIELD_WIDTH - 1; i++) {
            if (tiles[i].value == tiles[i + 1].value && (tiles[i].value != 0 && tiles[i + 1].value != 0)) count++;
        }
        if (count == 0) return false;
        else {
            for (int i = 0; i < FIELD_WIDTH - 1; i++) {
                if (tiles[i].value == tiles[i + 1].value) {
                    tiles[i].value *= 2;
                    score += tiles[i].value;
                    if (tiles[i].value > maxTile) maxTile = tiles[i].value;
                    for (int j = i + 1; j < FIELD_WIDTH - 1; j++) {
                        tiles[j].value = tiles[j + 1].value;
                    }
                    tiles[FIELD_WIDTH - 1].value = 0;
                }
            }
            return true;
        }
    }

    public void rollback() {
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            this.gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    private void saveState(Tile[][] gameTiles) {
        Tile[][] stackGameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                stackGameTiles[i][j] = new Tile(gameTiles[i][j].value);
            }
        }
        previousStates.push(stackGameTiles);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    public void randomMove() {
        switch (((int) (Math.random() * 100)) % 4) {
            case 0:
                left();
            case 1:
                right();
            case 2:
                up();
            case 3:
                down();

        }
    }

    public void autoMove(){
        PriorityQueue<MoveEfficiency> priorityQueue = new PriorityQueue<>(4,Collections.reverseOrder());
        priorityQueue.add(getMoveEfficiency(this::left));
        priorityQueue.add(getMoveEfficiency(this::right));
        priorityQueue.add(getMoveEfficiency(this::up));
        priorityQueue.add(getMoveEfficiency(this::down));
        priorityQueue.peek().getMove().move();
    }

    public boolean hasBoardChanged() {
        int valueGameTiles = 0;
        int valueStackGameTiles = 0;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                valueGameTiles += gameTiles[i][j].value;
            }
        }
        Tile[][] stackGameTiles = previousStates.peek();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                valueStackGameTiles += stackGameTiles[i][j].value;
            }
        }

        if (valueGameTiles != valueStackGameTiles) return true;
        else return false;
    }

    public MoveEfficiency getMoveEfficiency(Move move) {
        move.move();
        if (!hasBoardChanged()) return new MoveEfficiency(-1, 0, move);
        else {
            MoveEfficiency moveEfficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);
            rollback();
            return moveEfficiency;
        }
    }
}

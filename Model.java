package com.javarush.task.task35.task3513;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles;
    int score;
    int maxTile;

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
        boolean moveFlag = false;
        for (int i = 0; i < gameTiles.length; i++) {
            if (compressTiles(gameTiles[i]) || mergeTiles(gameTiles[i])) moveFlag = true;
        }
        if (moveFlag) addTile();
    }

    public void down() {
        rotate(gameTiles);
        left();
        rotate(gameTiles);
        rotate(gameTiles);
        rotate(gameTiles);
    }

    public void right() {
        rotate(gameTiles);
        rotate(gameTiles);
        left();
        rotate(gameTiles);
        rotate(gameTiles);
    }

    public void up() {
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
//            int hash = Arrays.hashCode(tiles);
//            Arrays.sort(tiles, Comparator.comparing(Tile::isEmpty));
//            return hash != Arrays.hashCode(tiles);
            List<Tile> tileFull = new ArrayList<>();
            List<Tile> tileEmpty = new ArrayList<>();
            for (int i = 0; i < FIELD_WIDTH; i++) {
                if (tiles[i].value == 0) tileEmpty.add(tiles[i]);
                else tileFull.add(tiles[i]);
            }
            for (int i = 0; i < tileFull.size(); i++) {
                tiles[i] = tileFull.get(i);
            }
            for (int i = 0; i < tileEmpty.size(); i++) {
                tiles[i + tileFull.size()] = tileEmpty.get(i);
            }
            return true;
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
}

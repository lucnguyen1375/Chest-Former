package com.ChessFormer.model.map;

import com.ChessFormer.model.chess.Chess;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Map {
    private ArrayList<Chess> pieces = new ArrayList<Chess>();
    private Vector2 goalPosition; // luu vi tri cua goal
    private boolean[][] blockedTiles; // luu trang thai cua o co bi chan chua
    private int mapWight, mapHeight;

    public Map(TiledMap map) {
        loadBlockMap(map);
        loadChessMap(map);
        goalPosition = findGoal(map);
    }

    public Vector2 getGoalPosition() {
        return goalPosition;
    }

    public void setGoalPosition(Vector2 goalPosition) {
        this.goalPosition = goalPosition;
    }

    public boolean[][] getBlockedTiles() {
        return blockedTiles;
    }


    public void loadBlockMap(TiledMap map) {
        TiledMapTileLayer ground = (TiledMapTileLayer) map.getLayers().get("Ground"); // lay nen
        mapWight = ground.getWidth();
        mapHeight = ground.getHeight();

        blockedTiles = new boolean[mapWight][mapHeight];

        // duyet qua toan bo map
        for (int x = 0; x < mapWight; x++) {
            for (int y = 0; y < mapHeight; y++) {
                TiledMapTileLayer.Cell cell = ground.getCell(x, y);
                if (cell != null && cell.getTile() != null) {
                    MapProperties propers = cell.getTile().getProperties();
                    if (propers.containsKey("block") && Boolean.TRUE.equals(propers.get("block"))) { // neu cell duoc set thuoc tinh block = true thi o nay bi chan
                        blockedTiles[x][y] = true;
                    }
                }
            }
        }
    }

    public void loadChessMap(TiledMap map) {
        MapLayer objects = map.getLayers().get("Objects"); // lay map objects chua toan bo objects chess
        for (MapObject obj : objects.getObjects()) { // duyet tung object
            if (obj.getName() != null && obj instanceof RectangleMapObject) {
                Rectangle rec = ((RectangleMapObject) obj).getRectangle();
                Vector2 position = new Vector2(rec.x, rec.y);
                switch (obj.getName()) {
                    case "knight":
                        pieces.add(new Knight(position));
                        break;
                    case "king":
                        pieces.add(new King(position));
                        break;
                    case "bishop":
                        pieces.add(new Bishop(position));
                        break;
                    case "pawn":
                        pieces.add(new Pawn(position));
                        break;
                    case "queen":
                        pieces.add(new Queen(position));
                        break;
                    case "rook":
                        pieces.add(new Rook(position));
                        break;
                }
            }
        }
    }

    private Vector2 findGoal(TiledMap map) {
        MapLayer objects = map.getLayers().get("Objects");
        for (MapObject obj : objects.getObjects()) {
            if ("goal".equals(obj.getName()) && obj instanceof RectangleMapObject) {
                Rectangle rec = ((RectangleMapObject) obj).getRectangle();
                return new Vector2(rec.x, rec.y);
            }
        }
        return new Vector2(0, 0);
    }

    // kiem tra o co block khong
    public boolean isBlocked(int x, int y){
        if(x < 0 || y < 0 || x >= mapWight || y >= mapHeight)
                return true;
        return blockedTiles[x][y];
    }

    // kiem tra o co quan co khong

    public boolean isChessPlace(int x, int y){
        for(Chess p : pieces){
            if((int)p.getPosion().x == x && (int)p.getPosion().y == y)
                    return true;
        }
        return false;
    }

    // lay quan co tai mot o cu the

    public Chess getChess(int x, int y){
        for(Chess p: pieces){
            if((int)p.getPosition().x == x && (int)p.getPosition().y == y)
                    return p;
        }
        return null;
    }

    // kiem tra xem goal co bi quan co nao da chua

    public boolean hackGoal(Chess piece){
        if((int)piece.getPosition().x == (int)goalPosition.x && (int)piece.getPosition().y == (int)goalPosition.y)
            return true;
        return false;
    }
}


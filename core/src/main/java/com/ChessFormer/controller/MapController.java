package com.ChessFormer.controller;

import com.ChessFormer.FileLogger;
import com.ChessFormer.model.chess.Chess;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

import static com.ChessFormer.Game_Utilz.UNIT_SCALE;

public class MapController {

    private TiledMap map;
    private List<Polygon> blockPolys;
    private List<Chess> chessList;
    private Chess targetChess;
    private Chess selectedChess;
    private FileLogger LOGGER;
    private int mapLevel;

    public MapController(int mapLevel) {
        LOGGER = new FileLogger(MapController.class.getName());
        this.mapLevel = mapLevel;
        String mapPath = "Map_Assets/Map_Level_" + mapLevel + ".tmx";
        loadMap(mapPath);
        blockPolys = new ArrayList<>();
        chessList = new ArrayList<>();
        selectedChess = null;
        LOGGER.info("MapController initialized with map: " + mapPath);
    }

    public void show() {
        loadBlockPolys();
        loadChess();
    }

    public void update(float delta) {
        checkIfHitTargetChess();
        for (Chess chess : chessList) {
            chess.update(delta, blockPolys, chessList);
        }
        targetChess.update(delta, blockPolys, chessList);
    }

    public TiledMap getMap() {
        return map;
    }

    public void loadMap(String mapPath) {
        try {
            this.map = new TmxMapLoader().load(mapPath);
            LOGGER.info("Map loaded successfully: " + mapPath);
        }   catch (Exception e) {
            LOGGER.error("Error loading map: " + e.getMessage());
        }
    }

    public void loadBlockPolys() {
        blockPolys.clear();
        MapLayer blockLayer = map.getLayers().get("Block");
        if (blockLayer != null) {
            for (MapObject obj : blockLayer.getObjects()) {
                if (obj instanceof PolygonMapObject) {
                    blockPolys.add(((PolygonMapObject) obj).getPolygon());
                }
            }
        }
        else{
            LOGGER.info("Block layer not found in the map.");
        }
        LOGGER.info("Block polygon loaded: " + blockPolys.size());
    }

    public void loadChess() {
        chessList.clear();
        MapLayer playerChessLayer = map.getLayers().get("PlayerChess");

        if (playerChessLayer != null) {
            for(MapObject obj : playerChessLayer.getObjects()) {
                float x = obj.getProperties().get("x", Float.class);
                float y = obj.getProperties().get("y", Float.class) - 32;
                String name = obj.getName();

                String texturePath = "Chess_Assets/" + "w_" + name + ".png"; // Đường dẫn đến hình ảnh quân cờ

                float tileX = x * UNIT_SCALE;
                float tileY = y * UNIT_SCALE;
                Chess chess = new Chess(name, new Vector2(tileX, tileY), texturePath);
                chessList.add(chess);
            }
        }

        MapLayer targetChessLayer = map.getLayers().get("TargetChess");
        if (targetChessLayer != null) {
            for(MapObject obj : targetChessLayer.getObjects()){
                float x = obj.getProperties().get("x", Float.class);
                float y = obj.getProperties().get("y", Float.class) - 32;

                targetChess = new Chess("TargetChess", new Vector2(x * UNIT_SCALE, y * UNIT_SCALE), "Chess_Assets/b_Bishop.png");
            }
        }
    }
    
    public void draw(SpriteBatch batch) {
        for (Chess chess : chessList) {
            chess.draw(batch);
        }
        targetChess.draw(batch);
    }

    public void touchDown(float x, float y) {
        for(Chess chess : chessList) {
            if(chess.getTileBounds().contains(x, y)) {
                selectedChess = chess;
                LOGGER.info("Selected chess: " + chess.getName() + " at position: " + chess.getPosition());
                LOGGER.info("Selected chess tile bounds: " + chess.getTileBounds());
            }
        }
        if (selectedChess != null) {
            selectedChess.moveTo((int)x ,(int)y);
        }
    }

    public int getMapLevel() {  return mapLevel;}

    public void checkIfHitTargetChess(){
        if (targetChess.IsRotating() == true) return;
        for(Chess chess : chessList) {
            if (chess.getPosition().equals(targetChess.getPosition())) {
                targetChess.setRotation(true);
                return;
            }
        }
    }

    public boolean ifChangeMap() {
        return targetChess.getPosition().y < -1;
    }

    public void dispose() {
        if (map != null) {
            map.dispose();
        }
    }

}

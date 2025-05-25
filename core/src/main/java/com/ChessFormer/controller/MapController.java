package com.ChessFormer.controller;

import com.ChessFormer.FileLogger;
import com.ChessFormer.model.chess.Chess;
import com.ChessFormer.screen.Button;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.awt.*;
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

    private int[][] table;
    private List<Sprite> greenBarSprites = new ArrayList<>();
    private List<Sprite> dotSprites = new ArrayList<>();
    private List<ButtonData> buttonList = new ArrayList<>();

    public MapController(int mapLevel) {
        LOGGER = new FileLogger(MapController.class.getName());
        this.mapLevel = mapLevel;
        String mapPath = "Map_Assets/Map_Level_" + mapLevel + ".tmx";
        loadMap(mapPath);

        table = new int[20][12];
        blockPolys = new ArrayList<>();
        chessList = new ArrayList<>();
        selectedChess = null;
        LOGGER.info("MapController initialized with map: " + mapPath);
    }

    public void printTable() {
        for(int i = 11; i >= 0; i--) {
            for(int j = 0; j < 20; j++) {
                System.out.printf("%d ",table[j][i]);
            }
            System.out.println();
        }
    }
    public void show() {
        loadBlockPolys();
        loadChess();
        loadGreenBar();

        printTable();
        for(Sprite sprite : greenBarSprites)
            System.out.println(sprite.getX() + " " + sprite.getY());
    }

    public void update(float delta) {
        checkIfHitTargetChess();
        for (Chess chess : chessList) {
            chess.update(delta, blockPolys, chessList, greenBarSprites);
        }
        targetChess.updateTargetChess(delta);
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
                    System.out.println("Found polygon object: " + obj.getName() + " at position: " + obj.getProperties().get("x") + ", " + obj.getProperties().get("y") + " with bounds: " + ((PolygonMapObject) obj).getPolygon().getBoundingRectangle());
                    float x = ((PolygonMapObject) obj).getPolygon().getBoundingRectangle().getX() / 32;
                    float y = ((PolygonMapObject) obj).getPolygon().getBoundingRectangle().getY() / 32;
                    float blockWidth = ((PolygonMapObject) obj).getPolygon().getBoundingRectangle().getWidth() / 32;
                    float blockHeight = ((PolygonMapObject) obj).getPolygon().getBoundingRectangle().getHeight() / 32;
                    System.out.println("Block polygon position: " + x + ", " + y + " with size: " + blockWidth + "x" + blockHeight);
                    for(int i = 0; i < blockWidth; i++) {
                        for(int j = 0; j < blockHeight; j++) {
                            table[(int)(x + i)][(int)(y + j)] = 1;
                        }
                    }
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
                table[(int)x / 32][(int)y / 32] = 3;
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
                table[(int)x / 32][(int)y / 32] = 2; // Đánh dấu ô có quân cờ mục tiêu
                targetChess = new Chess("TargetChess", new Vector2(x * UNIT_SCALE, y * UNIT_SCALE), "Chess_Assets/b_Bishop.png");
            }
        }
    }

    public void loadDotSprite(SpriteBatch batch) {
        for(int i = 0; i < 20; i++)
        {
            for(int j = 0; j < 12; j++)
            {
                if (table[i][j] == 5) {
                    String texturePath = "Dot_Assets/blue_body_circle.png";
                    Texture texture = new Texture(Gdx.files.internal(texturePath));
                    texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                    Sprite sprite = new Sprite(texture);
                    sprite.setPosition(i + 0.1f, j + 0.1f);
                    sprite.setSize(0.8f, 0.8f);
                    sprite.setAlpha(0.9f);
                    sprite.setOriginCenter();
                    sprite.draw(batch);
                }
            }
        }
    }
    public boolean checkPositionInBoard(int x, int y) {
        if (x < 0 || x >= 20 || y < 0 || y >= 12) {
            LOGGER.info("Position out of bounds: " + x + ", " + y);
            return false;
        }
        LOGGER.info("Position within bounds: " + x + ", " + y);
        return true;
    }

    public void loadChessCanMovePosition() {
        if (selectedChess == null)
        {
            LOGGER.info("No chess selected to load can move positions.");
            return;
        }
        List<Point> direct = ChessDirect.getDirectByType(selectedChess.getName());
        if (selectedChess.getName().equals("Pawn") || selectedChess.getName().equals("King") || selectedChess.getName().equals("Knight")) {
            loadChess_Pawn_King_Knight(direct);
        } else {
            loadChess_Rook_Bishop_Queen(direct);
        }
        printTable();
    }
    public boolean ifHittedAnotherChess(int x, int y) {
        for(Chess chess : chessList) {
            if (chess == selectedChess) continue; // Bỏ qua quân cờ đang được chọn
            if ((int)chess.getPosition().x == x && (int)chess.getPosition().y == y) {
//                LOGGER.info("Hitted another chess at position: " + x + ", " + y);
                return true;
            }
        }
//        LOGGER.info("No chess hit at position: " + x + ", " + y);
        return false;
    }
    public void loadChess_Pawn_King_Knight(List<Point> direct)
    {
        for(Point point : direct) {
            System.out.println("Direct point: " + point.x + ", " + point.y);
            int newX = (int) selectedChess.getPosition().x + point.x;
            int newY = (int) selectedChess.getPosition().y + point.y;
            if (checkPositionInBoard(newX, newY)) {
                if (table[newX][newY] == 1) continue;
                if (ifHittedAnotherChess(newX, newY)) continue; // Kiểm tra nếu có quân cờ khác ở vị trí này
                table[newX][newY] = 5; // Đánh dấu ô có thể di chuyển
            }
        }
    }

    public void loadChess_Rook_Bishop_Queen(List<Point> direct)
    {
        for(Point point : direct) {
            int cnt = 1;
            while(true) {
                System.out.println("Direct point: " + point.x + ", " + point.y);
                int newX = (int) selectedChess.getPosition().x + point.x*cnt;
                int newY = (int) selectedChess.getPosition().y + point.y*cnt;
                cnt++;
                if (checkPositionInBoard(newX, newY)) {
                    if (table[newX][newY] == 1) break;
                    if (ifHittedAnotherChess(newX, newY)) break; // Kiểm tra nếu có quân cờ khác ở vị trí này
                    table[newX][newY] = 5; // Đánh dấu ô có thể di chuyển
                }
                else break;
            }
        }
    }

    public void removeChessCanMovePosition() {
        for(int i = 0; i < table.length; i++) {
            for(int j = 0; j < table[i].length; j++) {
                if (table[i][j] == 5) {
                    table[i][j] = 0; // Đặt lại ô về trạng thái có thể đi được
                }
            }
        }
    }

    public void loadGreenBar() {
        MapLayer greenBar = map.getLayers().get("GreenBar");

        if (greenBar != null) {
            for (MapObject obj : greenBar.getObjects()) {
                float x = obj.getProperties().get("x", Float.class);
                float y = obj.getProperties().get("y", Float.class) - 32;
                String name = obj.getName();

                String texturePath = "Map_Assets/" + name + "_32x32_top.png";

                Texture texture = new Texture(Gdx.files.internal(texturePath));
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

                Sprite sprite = new Sprite(texture);
                sprite.setPosition(x * UNIT_SCALE, y * UNIT_SCALE);
                sprite.setSize(32 * UNIT_SCALE, 32 * UNIT_SCALE);

                greenBarSprites.add(sprite);
            }
        }
    }


    public void draw(SpriteBatch batch) {
        for (Chess chess : chessList) {
            chess.draw(batch);
        }
        targetChess.draw(batch);

        for (Sprite sprite : greenBarSprites) {
            sprite.draw(batch);
        }
        loadDotSprite(batch);
    }

    public void touchDown(float x, float y) {
        for(Chess chess : chessList) {
            if(chess.getTileBounds().contains(x, y)) {
                selectedChess = chess;
                LOGGER.info("Selected chess: " + chess.getName() + " at position: " + chess.getPosition());
                LOGGER.info("Selected chess tile bounds: " + chess.getTileBounds());
                removeChessCanMovePosition();
                loadChessCanMovePosition();
                return;
            }
        }
        if (selectedChess != null) {
            if (table[(int)x][(int)y] == 5) {
                selectedChess.moveTo((int) x, (int) y);
                removeChessCanMovePosition();
            }
            else
            {
                LOGGER.info("Cannot move chess to position: " + x + ", " + y + " - Invalid tile type: " + table[(int)x][(int)y]);
                selectedChess = null; // Deselect if move is invalid
                removeChessCanMovePosition();
            }
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

    public class ButtonData {
        public String name;
        public float x, y;

        public ButtonData(String name, float x, float y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }
    }
    public List<ButtonData> getButtonData() {
        return buttonList;
    }



}


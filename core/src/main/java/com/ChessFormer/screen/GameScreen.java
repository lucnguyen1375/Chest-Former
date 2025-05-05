package com.ChessFormer.screen;

import com.ChessFormer.ChessFormer;
import com.ChessFormer.FileLogger;
import com.ChessFormer.model.chess.Chess;
import com.ChessFormer.model.chess.Dot;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

import static com.ChessFormer.Game_Utilz.TILE_SIZE;
import static com.ChessFormer.Game_Utilz.UNIT_SCALE;

public class GameScreen extends InputAdapter implements Screen {
    private FileLogger LOGGER;
    private final ChessFormer game;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Vector2 lastClickedPosition;

    private int WINDOW_WIDTH;
    private int WINDOW_HEIGHT;
    private List<Polygon> blockRects;
    private List<Chess> playChessList;
    private List<Dot> dotList;

    private Chess targetChess;
    private Chess selectedChess;

    public GameScreen(ChessFormer game) {
        this.game = game; // kết nối tới Game
        WINDOW_WIDTH = Gdx.graphics.getWidth();
        WINDOW_HEIGHT = Gdx.graphics.getHeight();
        LOGGER = new FileLogger(GameScreen.class.getName());
        LOGGER.info("GameScreen initialized with window size: " + WINDOW_WIDTH + "x" + WINDOW_HEIGHT);

        blockRects = new ArrayList<>();
        playChessList = new ArrayList<>();
        dotList = new ArrayList<>();
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();

        // Tải bản đồ
        try{
            map = new TmxMapLoader().load("Map_Assets/Map_Level_2.tmx");
            LOGGER.info("Map loaded: " + map);
        } catch (Exception e) {
            LOGGER.error("Error loading map: " + e.getMessage());
        }


        mapRenderer = new OrthogonalTiledMapRenderer(map, UNIT_SCALE);
        loadBlockRects();
        loadChess();

        // Thiết lập camera
        camera.setToOrtho(false, WINDOW_WIDTH / TILE_SIZE, WINDOW_HEIGHT / TILE_SIZE);

        Gdx.input.setInputProcessor(this);
        lastClickedPosition = new Vector2();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        checkIfHitTargetChess();

        targetChess.update(delta, blockRects, playChessList);
        // Cập nhật camera

        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for(Chess chess : playChessList) {
            chess.update(delta, blockRects, playChessList);
            chess.draw(batch);
        }
        targetChess.draw(batch);
        batch.end();

    }

    public void checkIfHitTargetChess(){
        if (targetChess.IsRotating() == true) return;
        for(Chess chess : playChessList) {
            if (chess.getPosition().equals(targetChess.getPosition())) {
                targetChess.setRotation(true);
                return;
            }
        }
    }

    public void changeMap(String mapFile) {
        if (map != null) {  map.dispose();  }
        map = new TmxMapLoader().load(mapFile);
        mapRenderer.setMap(map);
    }

    public void loadBlockRects() {
        blockRects.clear();
        MapLayer blockLayer = map.getLayers().get("Block");
        if (blockLayer != null) {
            for (MapObject obj : blockLayer.getObjects()) {
                if (obj instanceof PolygonMapObject) {
                    blockRects.add(((PolygonMapObject) obj).getPolygon());
                }
            }
        }
        else{
            LOGGER.info("Block layer not found in the map.");
        }
        LOGGER.info("Block rectangles loaded: " + blockRects.size());
    }

    public void loadChess() {
        playChessList.clear();
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
                playChessList.add(chess);
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

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Chuyển đổi tọa độ màn hình thành tọa độ world
        Vector3 worldCoordinates = camera.unproject(new Vector3(screenX, screenY, 0));
        lastClickedPosition.set(worldCoordinates.x, worldCoordinates.y);

        // Tính toán ô được click (dựa vào TILE_SIZE)
        int tileX = (int) (worldCoordinates.x / UNIT_SCALE);
        int tileY = (int) (worldCoordinates.y / UNIT_SCALE);

        for(Chess chess : playChessList) {
            if(chess.getTileBounds().contains(worldCoordinates.x, worldCoordinates.y)) {
                selectedChess = chess;
                LOGGER.info("Selected chess: " + chess.getName() + " at position: " + chess.getPosition());
                LOGGER.info("Selected chess tile bounds: " + chess.getTileBounds());
                return false;
            }
        }
        if (selectedChess == null) {
            return false;
        }
        if (canMove(new Vector2(tileX, tileY))) {
            selectedChess.moveTo((int)worldCoordinates.x, (int)worldCoordinates.y);
            selectedChess = null;
            return true;
        }
        else {
            LOGGER.info("Can't move to: (" + tileX + ", " + tileY + ")");
            return false;
        }
    }

    public boolean canMove(Vector2 newPosition) {
        for (Polygon block : blockRects) {
            if (block.contains(newPosition)) {
                return false; // Va chạm với block
            }
        }
        return true; // Không va chạm với block
    }

    @Override public void resize(int i, int i1) {
        camera.update();
        LOGGER.info("Window resized to: " + i + "x" + i1);
    }

    @Override public void pause() {}

    @Override public void resume() {}

    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        map.dispose();
        mapRenderer.dispose();
    }
}

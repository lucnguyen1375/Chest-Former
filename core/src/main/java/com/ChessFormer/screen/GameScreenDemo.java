package com.ChessFormer.screen;

import com.ChessFormer.ChessFormer;
import com.ChessFormer.FileLogger;
import com.ChessFormer.controller.MapController;
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
import static com.ChessFormer.Game_Utilz.MAP_LEVEL_MAX;


public class GameScreenDemo extends InputAdapter implements Screen {
    private final FileLogger LOGGER;
    private final ChessFormer game;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Vector2 lastClickedPosition;

    private int WINDOW_WIDTH;
    private int WINDOW_HEIGHT;

    MapController mapController;

    public GameScreenDemo(ChessFormer game) {
        this.game = game; // kết nối tới Game
        LOGGER = new FileLogger(GameScreenDemo.class.getName());
        LOGGER.info("GameScreen initialized with window size: " + Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight());
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        mapController = new MapController(1);
        mapRenderer = new OrthogonalTiledMapRenderer(mapController.getMap(), UNIT_SCALE);
        // Khởi tạo lastClickedPosition
        lastClickedPosition = new Vector2();

        // Đăng ký InputProcessor
        Gdx.input.setInputProcessor(this);

        mapController.show();
        camera.setToOrtho(false, 20, 12);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        if (mapController.ifChangeMap())
            changeMap("Map_Assets/Map_Level_2.tmx");

        // Cập nhật camera
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();
        batch.setProjectionMatrix(camera.combined);
        mapController.update(delta);
        batch.begin();
        mapController.draw(batch);
        batch.end();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Chuyển đổi tọa độ màn hình thành tọa độ world
        Vector3 worldCoordinates = camera.unproject(new Vector3(screenX, screenY, 0));
        lastClickedPosition.set(worldCoordinates.x, worldCoordinates.y);

        System.out.println("CLicked");
        mapController.touchDown(worldCoordinates.x, worldCoordinates.y);
        return true;
    }

    @Override public void resize(int i, int i1) {
        camera.update();
        LOGGER.info("Window resized to: " + i + "x" + i1);
    }

    public void changeMap(String mapPath) {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        int nextMapLevel = mapController.getMapLevel() + 1;
        if (nextMapLevel > MAP_LEVEL_MAX) return;
        mapController = new MapController(nextMapLevel);
        mapRenderer = new OrthogonalTiledMapRenderer(mapController.getMap(), UNIT_SCALE);
        // Khởi tạo lastClickedPosition
        lastClickedPosition = new Vector2();

        // Đăng ký InputProcessor
        Gdx.input.setInputProcessor(this);

        mapController.show();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / TILE_SIZE, Gdx.graphics.getHeight() / TILE_SIZE);
    }

    @Override public void pause() {}

    @Override public void resume() {}

    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        mapController.dispose();
        mapRenderer.dispose();
    }
}

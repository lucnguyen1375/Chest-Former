package com.ChessFormer.screen;

import com.ChessFormer.ChessFormer;
import com.ChessFormer.FileLogger;
import com.ChessFormer.controller.MapController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import static com.ChessFormer.Game_Utilz.UNIT_SCALE;
import static com.ChessFormer.Game_Utilz.MAP_LEVEL_MAX;
import static com.ChessFormer.Game_Utilz.VIEWPORT_WIDTH;
import static com.ChessFormer.Game_Utilz.VIEWPORT_HEIGHT;


public class GameScreen extends InputAdapter implements Screen {
    private final FileLogger LOGGER;
    private final ChessFormer game;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Vector2 lastClickedPosition;


    MapController mapController;
    int level;

    public GameScreen(ChessFormer game, int level) {
        this.game = game; // kết nối tới Game
        this.level = level;
        LOGGER = new FileLogger(GameScreen.class.getName());
        LOGGER.info("GameScreen initialized with window size: " + Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight());
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        mapController = new MapController(level);
        mapRenderer = new OrthogonalTiledMapRenderer(mapController.getMap(), UNIT_SCALE);
        // Khởi tạo lastClickedPosition
        lastClickedPosition = new Vector2();

        // Đăng ký InputProcessor
        Gdx.input.setInputProcessor(this);

        mapController.show();
        camera.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        if (mapController.ifChangeMap())
            changeMap();

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

        mapController.touchDown(worldCoordinates.x, worldCoordinates.y);
        return true;
    }

    @Override public void resize(int i, int i1) {
        camera.update();
        LOGGER.info("Window resized to: " + i + "x" + i1);
    }

    public void changeMap() {
        batch = new SpriteBatch();
        int nextMapLevel = mapController.getMapLevel() + 1;
        if (nextMapLevel > MAP_LEVEL_MAX) return;
        Preferences prefs = Gdx.app.getPreferences("GameProgress");
        prefs.putBoolean("level_" + nextMapLevel + "_unlocked", true);
        prefs.flush();
        mapController = new MapController(nextMapLevel);
        mapRenderer = new OrthogonalTiledMapRenderer(mapController.getMap(), UNIT_SCALE);
        mapController.show();
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

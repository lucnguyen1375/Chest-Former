package com.ChessFormer.screen;

import com.ChessFormer.ChessFormer;
import com.ChessFormer.FileLogger;
import com.ChessFormer.controller.MapController;
import com.ChessFormer.model.Button;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import static com.ChessFormer.Game_Utilz.*;
import static com.ChessFormer.Game_Utilz.TILE_SIZE;


public class GameScreen extends InputAdapter implements Screen {
    private final FileLogger LOGGER;
    private final ChessFormer game;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Vector2 lastClickedPosition;
    private Stage stage;
    private int WINDOW_WIDTH;
    private int WINDOW_HEIGHT;
    private Button backButton;
    private Button resetButton;



    MapController mapController;
    int level;
    private Skin skin;

    public GameScreen(ChessFormer game, int level) {
        this.game = game; // kết nối tới Game
        this.level = level;
        WINDOW_WIDTH = Gdx.graphics.getWidth();
        WINDOW_HEIGHT = Gdx.graphics.getHeight();
        LOGGER = new FileLogger(GameScreen.class.getName());
        LOGGER.info("GameScreen initialized with window size: " + Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight());

    }

    @Override
    public void show() {
        LOGGER.info("GameScreen show() called");
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        mapController = new MapController(level);
        mapRenderer = new OrthogonalTiledMapRenderer(mapController.getMap(), UNIT_SCALE);

        // Khởi tạo lastClickedPosition
        lastClickedPosition = new Vector2();

        stage = new Stage(new ScreenViewport());
        camera.setToOrtho(false, WINDOW_WIDTH / TILE_SIZE, WINDOW_HEIGHT / TILE_SIZE);
        //stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage); // Xử lý UI trước


        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Chuyển tọa độ màn hình sang world coords của camera
                Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));
                lastClickedPosition.set(worldCoords.x, worldCoords.y);

                mapController.touchDown(worldCoords.x, worldCoords.y);
                return true; // trả về true nếu đã xử lý
            }
        });

        Gdx.input.setInputProcessor(multiplexer);



        backButton = new Button("back", btnName -> {
            System.out.println("Test button clicked");
            game.setScreen(new MenuScreen(game));
        });
        backButton.setSize(64, 64);
        backButton.setPosition(20, 30);

        resetButton = new Button("reset", btnName -> {
            System.out.println("Test button clicked");
            game.setScreen(new GameScreen(game, mapController.getMapLevel()));
        });
        resetButton.setSize(64, 64);
        resetButton.setPosition(120, 30);
        stage.addActor(resetButton);
        stage.addActor(backButton);

        mapController.show();

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

        stage.act(delta);
        stage.draw();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Chuyển đổi tọa độ màn hình thành tọa độ world
        Vector3 worldCoordinates = camera.unproject(new Vector3(screenX, screenY, 0));
        lastClickedPosition.set(worldCoordinates.x, worldCoordinates.y);

        mapController.touchDown(worldCoordinates.x, worldCoordinates.y);
        return true;
    }

//    @Override public void resize(int i, int i1) {
//        camera.update();
//        LOGGER.info("Window resized to: " + i + "x" + i1);
//    }

    @Override
    public void resize(int width, int height) {
        if (width == 0 || height == 0) {
            LOGGER.info("Skipped resize due to zero dimension: " + width + "x" + height);
            return;
        }

        stage.getViewport().update(width, height, true);
        camera.viewportWidth = VIEWPORT_WIDTH;
        camera.viewportHeight = VIEWPORT_HEIGHT;
        camera.update();

        LOGGER.info("Resized MainScreen to: " + width + "x" + height);
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
        stage.dispose();
    }
}

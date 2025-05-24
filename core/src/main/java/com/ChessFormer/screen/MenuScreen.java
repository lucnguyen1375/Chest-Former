package com.ChessFormer.screen;

import com.ChessFormer.ChessFormer;
import com.ChessFormer.FileLogger;
import com.ChessFormer.model.chess.Chess;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;


import java.lang.reflect.Method;

import static com.ChessFormer.Game_Utilz.TILE_SIZE;
import static com.ChessFormer.Game_Utilz.UNIT_SCALE;

public class MenuScreen implements Screen {
    private final FileLogger LOGGER;
    private ChessFormer game;
    private Stage stage;

    private TiledMap map;
    private Preferences prefs;
    private BitmapFont font;


    private OrthographicCamera camera;
    private SpriteBatch batch;

    private int WINDOW_WIDTH;
    private int WINDOW_HEIGHT;

    private OrthogonalTiledMapRenderer mapRenderer;


    public MenuScreen(ChessFormer game) {
        LOGGER = new FileLogger(MenuScreen.class.getName());
        this.game = game;
        WINDOW_WIDTH = Gdx.graphics.getWidth();
        WINDOW_HEIGHT = Gdx.graphics.getHeight();

        stage = new Stage(new ScreenViewport());

        Gdx.input.setInputProcessor(stage);

        prefs = Gdx.app.getPreferences("GameProgress");
        font = new BitmapFont();


    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.update();

        // Tải bản đồ
        try{
            map = new TmxMapLoader().load("Map_Assets/MenuMap.tmx");
            LOGGER.info("Map loaded: " + map);
        } catch (Exception e) {
            LOGGER.error("Error loading map: " + e.getMessage());
        }
        mapRenderer = new OrthogonalTiledMapRenderer(map, UNIT_SCALE);
        loadMenu();
        camera.setToOrtho(false, WINDOW_WIDTH / TILE_SIZE, WINDOW_HEIGHT / TILE_SIZE);
    }

    private void loadMenu() {
        MapLayer menu = map.getLayers().get("Menu");

        if (menu != null) {
            for (MapObject obj : menu.getObjects()) {
                float x = obj.getProperties().get("x", Float.class)  ;
                float y = (obj.getProperties().get("y", Float.class)-32) ;
                int level = Integer.parseInt(obj.getName());

                boolean unlocked = prefs.getBoolean("level_" + level + "_unlocked", level == 1);

                LevelButton button = new LevelButton(level, unlocked, x, y, lvl -> {
                    System.out.println("Clicked level " + lvl);
                    game.setScreen(new GameScreen(game, lvl));
                });
                button.setSize(64,64);
                stage.addActor(button);
            }
        }
    }





    @Override
    public void render(float delta) {
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();


        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    @Override public void dispose() {
        stage.dispose();
        font.dispose();
        map.dispose();
        mapRenderer.dispose();
        batch.dispose();
    }
}

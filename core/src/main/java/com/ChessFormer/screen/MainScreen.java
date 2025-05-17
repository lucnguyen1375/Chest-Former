package com.ChessFormer.screen;

import com.ChessFormer.ChessFormer;
import com.ChessFormer.FileLogger;
import com.ChessFormer.model.chess.Chess;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

import static com.ChessFormer.Game_Utilz.TILE_SIZE;
import static com.ChessFormer.Game_Utilz.UNIT_SCALE;

public class MainScreen implements Screen {

    private final FileLogger LOGGER;
    private ChessFormer game;
    private Stage stage;
    private Skin skin, circleSkin;
    private TextField usernameField;
    private TextField passwordField;
    private OrthographicCamera camera;
    private SpriteBatch batch;

    private int WINDOW_WIDTH;
    private int WINDOW_HEIGHT;

    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMap map;
    private List<Chess> playChessList;



    public MainScreen(ChessFormer game) {
        this.game = game; // kết nối tới Game
        LOGGER = new FileLogger(MainScreen.class.getName());
        WINDOW_WIDTH = Gdx.graphics.getWidth();
        WINDOW_HEIGHT = Gdx.graphics.getHeight();
        stage = new Stage(new ScreenViewport());

        Gdx.input.setInputProcessor(stage);
        playChessList = new ArrayList<>();

        Texture fontTexture = new Texture(Gdx.files.internal("Map_Assets/font.png"));


        TextureRegionDrawable fontDrawable = new TextureRegionDrawable(new TextureRegion(fontTexture));

// Tạo ImageButton
        Image fontText = new Image(fontDrawable);
        fontText.setSize(128, 128);






        Texture buttonTexture = new Texture(Gdx.files.internal("Map_Assets/playbutton.png"));
        TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));

// Tạo ImageButton
        ImageButton playButton = new ImageButton(buttonDrawable);
        playButton.setSize(128, 128);

// Force nội dung button co lại
        playButton.getImage().setScaling(Scaling.fit);  // hoặc Scaling.stretch








        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Sau khi đăng nhập đúng, chuyển sang màn chính
                game.setScreen(new GameScreen(game));
            }
        });
        playButton.addListener(new ActorGestureListener() {

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                playButton.setColor(1f, 0.8f, 0.5f, 1f);
                playButton.setScale(1.1f);
            }


            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                playButton.setColor(Color.WHITE);
                playButton.setScale(1.0f);
            }
        });




        Table table = new Table();

        table.setFillParent(true);
        table.top();
        // Gợi ý kích thước cân đối, ví dụ:
        fontText.setSize(1500, 1500);  // hoặc scale lại nếu cần

// Thêm ảnh vào table, với padding để không sát mép quá
        table.add(fontText).padTop(0).size(600, 400); // padTop tạo khoảng cách so với đỉnh

        stage.addActor(table);
        table = new Table();
        table.setFillParent(true);
        table.center();

        //table.add(playButton).colspan(2).padTop(10);
        table.add(playButton).size(128, 128);

        stage.addActor(table);
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();

        // Tải bản đồ
        try{
            map = new TmxMapLoader().load("Map_Assets/MainMap.tmx");
            LOGGER.info("Map loaded: " + map);
        } catch (Exception e) {
            LOGGER.error("Error loading map: " + e.getMessage());
        }


        mapRenderer = new OrthogonalTiledMapRenderer(map, UNIT_SCALE);

        loadChess();

        // Thiết lập camera
        camera.setToOrtho(false, WINDOW_WIDTH / TILE_SIZE, WINDOW_HEIGHT / TILE_SIZE);




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



    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Cập nhật camera

        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for(Chess chess : playChessList) {
            chess.draw(batch);
        }



        batch.end();

        stage.act(delta);
        stage.draw();
    }



    @Override public void resize(int width, int height) {
        // Cập nhật kích thước của stage
        stage.getViewport().update(width, height, true);
        LOGGER.info("Resized MainScreen to: " + width + "x" + height);
    }

    @Override public void pause() {}

    @Override public void resume() {}

    @Override public void hide() {}

    @Override public void dispose() {
        stage.dispose();

    }
}

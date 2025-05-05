package com.ChessFormer.screen;

import com.ChessFormer.ChessFormer;
import com.ChessFormer.FileLogger;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainScreen implements Screen {

    private final FileLogger LOGGER;
    private ChessFormer game;
    private Stage stage;
    private Skin skin, circleSkin;
    private TextField usernameField;
    private TextField passwordField;
    private TextButton playButton;

    public MainScreen(ChessFormer game) {
        this.game = game; // kết nối tới Game
        stage = new Stage(new ScreenViewport());
        LOGGER = new FileLogger(MainScreen.class.getName());

        Gdx.input.setInputProcessor(stage);

        try{
            skin = new Skin(Gdx.files.internal("skin/uiskin.json"));// skin mặc định của LibGDX
        } catch (Exception e) {
            LOGGER.error("Error loading skin: " + e.getMessage());
        }

        playButton = new TextButton("PLAY", skin);

        Image background;
        try {
            background = new Image(new Texture(Gdx.files.internal("Map_Assets/MainMap.png"))) ;
            background.setFillParent(true);
            stage.addActor(background);
        }
        catch (Exception e) {
            LOGGER.error("Error loading main background image: " + e.getMessage());
        }


        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Sau khi đăng nhập đúng, chuyển sang màn chính
                game.setScreen(new GameScreen(game));
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(playButton).colspan(2).padTop(10);

        stage.addActor(table);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
        skin.dispose();
    }
}

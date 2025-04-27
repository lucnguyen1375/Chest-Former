package com.ChessFormer.controller;

import com.ChessFormer.ChessFormer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class GameScreen implements Screen {
    private final ChessFormer game;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;

    public GameScreen(ChessFormer game, String mapPath) {
        this.game = game;

        // Tải map
        map = new TmxMapLoader().load(mapPath);
        renderer = new OrthogonalTiledMapRenderer(map, 1 / 32f);

        // Thiết lập camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800 / 32f, 600 / 32f); // Kích thước màn hình
        camera.update();
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        // Cập nhật và render map
        camera.update();
        renderer.setView(camera);
        renderer.render();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / 32f, height / 32f);
        camera.update();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
    }
}

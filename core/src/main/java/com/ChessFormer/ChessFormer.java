package com.ChessFormer;

import com.ChessFormer.model.MyActor;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Logger;

import java.awt.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ChessFormer extends ApplicationAdapter {

    private static final int TILE_SIZE = Game_Utilz.TILE_SIZE;
    private static final String MAP_PATH = Game_Utilz.MAP_FOLDER_PATH + "Map_Level_1.tmx";
    private static final float UNIT_SCALE = 1/32f;


    private int WINDOW_WIDTH;
    private int WINDOW_HEIGHT;

    private static FileLogger LOGGER;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;

    private Vector3 tmpVector = new Vector3(); // dùng để chuyển đổi tọa độ
    MyActor chessActor;
    float chessActorX, chessActorY;
    Stage stage;

    @Override
    public void create() {
        LOGGER = new FileLogger(ChessFormer.class.getName());
        LOGGER.clearExistedLogger();
        LOGGER.info("Khởi động Game ChessFormer");

        WINDOW_WIDTH = Gdx.graphics.getWidth();
        WINDOW_HEIGHT = Gdx.graphics.getHeight();
        LOGGER.info(String.format("Kích thước cửa sổ: %dx%d", WINDOW_WIDTH, WINDOW_HEIGHT));

        // Tải tilemap từ file TMX
        map = new TmxMapLoader().load(MAP_PATH);
        LOGGER.info("Đã tải map : " + MAP_PATH);

        // Tạo renderer
        renderer = new OrthogonalTiledMapRenderer(map, UNIT_SCALE);

        // Thiết lập camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WINDOW_WIDTH / TILE_SIZE, WINDOW_HEIGHT / TILE_SIZE);
        camera.update();
        LOGGER.info("Thiết lập camera với kích thước: " + WINDOW_WIDTH / TILE_SIZE + "x" + WINDOW_HEIGHT / TILE_SIZE);

        renderer.setView(camera);
        LOGGER.info("Renderer đã được thiết lập với camera");

        // Set up chess actor
        chessActorX = 0;
        chessActorY = 0;
        String chessTexturePath = "Chess_Assets/b_Queen.png";
        Texture chessTexture = new Texture(chessTexturePath);
        chessActor = new MyActor(chessTexture, 0, 0, 60 , 60);
        LOGGER.info("Tạo Chess Actor với Ảnh: " + chessTexturePath);

        // Set up stage
        stage = new Stage();
        stage.addActor(chessActor);
        LOGGER.info("Tạo Stage và thêm Chess Actor vào Stage");

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Chuyển tọa độ màn hình sang tọa độ thế giới
                tmpVector.set(screenX, screenY, 0);
                camera.unproject(tmpVector);

                // Chuyển tọa độ thế giới sang tọa độ ô
                int cellX = (int) (tmpVector.x);
                int cellY = (int) (tmpVector.y);

                Vector3 screenPos = new Vector3(cellX, cellY, 0);
                camera.project(screenPos);

                LOGGER.info(String.format("Người dùng click vào ô: [%d, %d] - Tọa độ màn hình: [%.0f, %.0f]",
                    cellX, cellY, screenPos.x, screenPos.y));

                chessActorX = cellX * TILE_SIZE;
                chessActorY = cellY * TILE_SIZE;
                return true;
            }
        });
    }

    @Override
    public void render() {
        // Xóa màn hình
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Cập nhật camera
        camera.update();
        renderer.setView(camera);

        // Render map
        renderer.render();

        // Render chess actor
        chessActor.setPosition(chessActorX,chessActorY);// Set position of chess actor
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Gọi khi cửa sổ thay đổi kích thước
        LOGGER.info(String.format("Kích thước cửa sổ mới: " + width + "x" + height));
        camera.update();
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
    }
}

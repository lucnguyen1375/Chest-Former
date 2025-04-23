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
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
//    private static final Logger LOGGER = new Logger(ChessFormer.class.getName(), Logger.DEBUG);
    private FileLogger LOGGER;
    private MyActor chessActor;

    private float chessActorX, chessActorY;

    Stage stage;

    private Vector3 tmpVector = new Vector3(); // dùng để chuyển đổi tọa độ



    @Override
    public void create() {
        // Khởi tạo logger
        LOGGER = new FileLogger(ChessFormer.class.getName());
        LOGGER.clearExistedLogger();
        LOGGER.info("Khởi động Game ChessFormer");

        int windowWidth = Gdx.graphics.getWidth();
        int windowHeight = Gdx.graphics.getHeight();
        LOGGER.info(String.format("Kích thước cửa sổ: %dx%d", windowWidth, windowHeight));


        // Tải tilemap từ file TMX
        String mapPath = "DemoMap.tmx";
        map = new TmxMapLoader().load(mapPath);
        LOGGER.info("Đã tải map : " + mapPath);


        // Lấy thuộc tính cơ bản
        int mapWidth = map.getProperties().get("width", Integer.class);
        int mapHeight = map.getProperties().get("height", Integer.class);
        int tileWidth = map.getProperties().get("tilewidth", Integer.class);
        int tileHeight = map.getProperties().get("tileheight", Integer.class);

        LOGGER.info("Map Width: " + mapWidth);
        LOGGER.info("Map Height: " + mapHeight);
        LOGGER.info("Tile Width: " + tileWidth);
        LOGGER.info("Tile Height: " + tileHeight);


        // Tạo renderer với scale 1/32 (tùy thuộc vào kích thước tile của bạn)
        float unitScale = 1/32f;
        renderer = new OrthogonalTiledMapRenderer(map, unitScale);

        // Thiết lập camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 15, 12); // Kích thước bàn cờ 8x8
        camera.update();
        LOGGER.info("Thiết lập Camera với số lượng ô: " + camera.viewportWidth + "x" + camera.viewportHeight);

        renderer.setView(camera);

        // Set up chess actor
        chessActorX = 0;
        chessActorY = 0;
        String chessTexturePath = "Chess_Assets/b_Queen.png";
        Texture chessTexture = new Texture(chessTexturePath);
        chessActor = new MyActor(chessTexture, 0, 0, tileWidth , tileHeight );
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

                chessActorX = screenPos.x;
                chessActorY = screenPos.y;
                return true;
            }
        });
    }

    @Override
    public void render() {
        // Xóa màn hình
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
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

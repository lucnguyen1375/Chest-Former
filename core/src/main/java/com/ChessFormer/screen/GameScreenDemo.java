//package com.ChessFormer.screen;
//
//import com.ChessFormer.ChessFormer;
//import com.ChessFormer.FileLogger;
//import com.ChessFormer.controller.MapController;
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.InputAdapter;
//import com.badlogic.gdx.Screen;
//import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.math.Vector3;
//
//import static com.ChessFormer.Game_Utilz.UNIT_SCALE;
//import static com.ChessFormer.Game_Utilz.MAP_LEVEL_MAX;
//import static com.ChessFormer.Game_Utilz.VIEWPORT_WIDTH;
//import static com.ChessFormer.Game_Utilz.VIEWPORT_HEIGHT;
//
//import com.ChessFormer.model.chess.ChessFactory;
//
//public class GameScreenDemo extends InputAdapter implements Screen {
//    private final FileLogger LOGGER;
//    private final ChessFormer game;
//
//    private SpriteBatch batch;
//    private OrthographicCamera camera;
//    private OrthogonalTiledMapRenderer mapRenderer;
//    private Vector2 lastClickedPosition;
//
//
//    MapController mapController;
//
//    public GameScreenDemo(ChessFormer game) {
//        this.game = game; // kết nối tới Game
//        LOGGER = new FileLogger(GameScreenDemo.class.getName());
//        LOGGER.info("GameScreen initialized with window size: " + Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight());
//    }
//
//    @Override
//    public void show() {
//        batch = new SpriteBatch();
//        camera = new OrthographicCamera();
//        mapController = new MapController(1);
//        mapRenderer = new OrthogonalTiledMapRenderer(mapController.getMap(), UNIT_SCALE);
//        // Khởi tạo lastClickedPosition
//        lastClickedPosition = new Vector2();
//
//        // Đăng ký InputProcessor
//        Gdx.input.setInputProcessor(this);
//
//        mapController.show();
//        camera.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
//    }
//
//    @Override
//    public void render(float delta) {
//        Gdx.gl.glClearColor(0, 0, 0, 1);
//        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);
//
//        if (mapController.ifChangeMap())
//            changeMap();
//
//        // Cập nhật camera
//        camera.update();
//        mapRenderer.setView(camera);
//        mapRenderer.render();
//        batch.setProjectionMatrix(camera.combined);
//        mapController.update(delta);
//        batch.begin();
//        mapController.draw(batch);
//        batch.end();
//    }
//
//    @Override
//    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//        // Chuyển đổi tọa độ màn hình thành tọa độ world
//        Vector3 worldCoordinates = camera.unproject(new Vector3(screenX, screenY, 0));
//        lastClickedPosition.set(worldCoordinates.x, worldCoordinates.y);
//
//        mapController.touchDown(worldCoordinates.x, worldCoordinates.y);
//        return true;
//    }
//
//    @Override public void resize(int i, int i1) {
//        camera.update();
//        LOGGER.info("Window resized to: " + i + "x" + i1);
//    }
//
//    public void changeMap() {
//        batch = new SpriteBatch();
//        int nextMapLevel = mapController.getMapLevel() + 1;
//        if (nextMapLevel > MAP_LEVEL_MAX) return;
//        mapController = new MapController(nextMapLevel);
//        mapRenderer = new OrthogonalTiledMapRenderer(mapController.getMap(), UNIT_SCALE);
//        mapController.show();
//    }
//
//    @Override public void pause() {}
//
//    @Override public void resume() {}
//
//    @Override public void hide() {}
//
//    @Override
//    public void dispose() {
//        batch.dispose();
//        mapController.dispose();
//        mapRenderer.dispose();
//    }
//}

package com.ChessFormer.screen;

import com.ChessFormer.ChessFormer;
import com.ChessFormer.FileLogger;
import com.ChessFormer.controller.MapController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ChessFormer.model.chess.*;
import com.badlogic.gdx.math.Polygon;

import java.util.ArrayList;
import java.util.List;

import static com.ChessFormer.Game_Utilz.*;

public class GameScreenDemo extends InputAdapter implements Screen {
    private final FileLogger LOGGER;
    private final ChessFormer game;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Vector2 lastClickedPosition;
    private MapController mapController;

    private Chess selectedChess;
    private List<Vector2> validMoves = new ArrayList<>();
    private List<Dot> dots = new ArrayList<>();

    public GameScreenDemo(ChessFormer game) {
        this.game = game;
        LOGGER = new FileLogger(GameScreenDemo.class.getName());
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        mapController = new MapController(1);
        mapRenderer = new OrthogonalTiledMapRenderer(mapController.getMap(), UNIT_SCALE);
        lastClickedPosition = new Vector2();
        Gdx.input.setInputProcessor(this);

        mapController.show();
        camera.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        if (mapController.ifChangeMap()) changeMap();

        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        mapController.update(delta);

        batch.begin();
        mapController.draw(batch);
        for (Dot dot : dots) dot.draw(batch);
        batch.end();
    }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 worldCoordinates = camera.unproject(new Vector3(screenX, screenY, 0));
        lastClickedPosition.set(worldCoordinates.x, worldCoordinates.y);

        if (selectedChess == null) {
            selectedChess = mapController.getChessAtPosition(lastClickedPosition);
            validMoves.clear();
            dots.clear();

            if (selectedChess != null && (selectedChess instanceof Knight || selectedChess instanceof King || selectedChess instanceof Rook || selectedChess instanceof Bishop || selectedChess instanceof Queen || selectedChess instanceof Pawn )) {
                LOGGER.info("Selected chess: " + selectedChess.getName() +
                    " at pixel: " + selectedChess.getPosition() +
                    " (tile: " + selectedChess.getPosition().x / UNIT_SCALE + "," +
                    selectedChess.getPosition().y / UNIT_SCALE + ")");

                Chess[][] board = mapController.generateBoardFromList(mapController.getChessList());
                List<Polygon> platforms = mapController.getPlatforms();

                if (selectedChess instanceof Knight) {
                    validMoves = ((Knight) selectedChess).getValidMoves(board, mapController.getChessList(), platforms);
                } else if (selectedChess instanceof King) {
                    validMoves = ((King) selectedChess).getValidMoves(board, mapController.getChessList(), platforms);
                } else if (selectedChess instanceof Rook) {
                    validMoves = ((Rook) selectedChess).getValidMoves(board, mapController.getChessList(), platforms);
                } else if (selectedChess instanceof Bishop) {
                    validMoves = ((Bishop) selectedChess).getValidMoves(board, mapController.getChessList(), platforms);
                } else if (selectedChess instanceof Queen) {
                    validMoves = ((Queen) selectedChess).getValidMoves(board, mapController.getChessList(), platforms);
                } else if (selectedChess instanceof Pawn) {
                    validMoves = ((Pawn) selectedChess).getValidMoves(board, mapController.getChessList(), platforms);
                }

                LOGGER.info("Danh sách nước đi hợp lệ:");
                for (Vector2 move : validMoves) {
                    LOGGER.info(" - Move to: " + move.x + ", " + move.y);
                }

                for (Vector2 movePos : validMoves) {
                    // Vẽ Dot ở đúng vị trí pixel
                    dots.add(new Dot(new Vector2(movePos.x, movePos.y)));
                }
            }

        } else {
            boolean moved = false;

            // Lấy tile x,y mà người dùng nhấn vào
            float tileX = (int)(lastClickedPosition.x );
            float tileY = (int)(lastClickedPosition.y );

            // ✅ In ra tọa độ ô vừa click và so sánh với các nước đi
            LOGGER.info("Clicked tile: " + tileX + "," + tileY);
            for (Vector2 movePos : validMoves) {
                LOGGER.info("Comparing with valid move: " + movePos.x + "," + movePos.y);
            }

            for (Vector2 movePos : validMoves) {
                if (Math.abs(movePos.x - tileX) < 0.5f && Math.abs(movePos.y - tileY) < 0.5f) {
                    LOGGER.info("Moving to: " + movePos.x + "," + movePos.y);
                    selectedChess.moveTo(movePos.x, movePos.y);
                    moved = true;
                    break;
                }
            }

            selectedChess = null;
            validMoves.clear();
            dots.clear();
            LOGGER.info(moved ? "Chess moved." : "Invalid move.");
        }


        return true;
    }

    @Override
    public void resize(int width, int height) {
        camera.update();
    }

//    public void changeMap() {
//        batch.dispose();
//        int nextMapLevel = mapController.getMapLevel() + 1;
//        LOGGER.info("Changing map to level: " + nextMapLevel);
//        if (nextMapLevel > MAP_LEVEL_MAX) return;
//        mapController = new MapController(nextMapLevel);
//        mapRenderer = new OrthogonalTiledMapRenderer(mapController.getMap(), UNIT_SCALE);
//        mapController.show();
//    }
    int nextMapLevel = 1;
public void changeMap() {
    //nextMapLevel = mapController.getMapLevel() + 1;
    nextMapLevel++;
    LOGGER.info("Changing map to level: " + nextMapLevel);
    if (nextMapLevel > 15) return;

    if (mapController != null) {
        mapController.dispose();
    }
    if (mapRenderer != null) {
        mapRenderer.dispose();
    }

    mapController = new MapController(nextMapLevel);
    mapRenderer = new OrthogonalTiledMapRenderer(mapController.getMap(), UNIT_SCALE);
    mapController.show();

    // Reset các biến liên quan
    selectedChess = null;
    validMoves.clear();
    dots.clear();
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

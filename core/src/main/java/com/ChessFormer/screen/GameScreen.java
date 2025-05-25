//
//package com.ChessFormer.screen;
//
//import com.ChessFormer.ChessFormer;
//import com.ChessFormer.FileLogger;
//import com.ChessFormer.model.chess.Chess;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.InputAdapter;
//import com.badlogic.gdx.Screen;
//import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.Sprite;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.maps.MapLayer;
//import com.badlogic.gdx.maps.MapObject;
//import com.badlogic.gdx.maps.objects.PolygonMapObject;
//import com.badlogic.gdx.maps.tiled.TiledMap;
//import com.badlogic.gdx.maps.tiled.TmxMapLoader;
//import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
//import com.badlogic.gdx.math.Polygon;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.math.Vector3;
//import com.badlogic.gdx.utils.Array;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.ChessFormer.Game_Utilz.*;
//import com.ChessFormer.model.chess.ChessFactory;
//import com.badlogic.gdx.graphics.GL20;
//import com.ChessFormer.model.chess.Knight;
//import com.ChessFormer.model.chess.Dot;
//
//public class GameScreen extends InputAdapter implements Screen {
//    private FileLogger LOGGER;
//    private final ChessFormer game;
//    private List<Chess> chessList;
//    private List<Polygon> platforms;
//
//    private SpriteBatch batch;
//    private OrthographicCamera camera;
//    private TiledMap map;
//    private OrthogonalTiledMapRenderer mapRenderer;
//    private Vector2 lastClickedPosition;
//
//    private int WINDOW_WIDTH;
//    private int WINDOW_HEIGHT;
//    private List<Polygon> blockRects;
//    private List<Chess> playChessList;
//
//    private Chess targetChess;
//    private Chess selectedChess;
//
//    private List<Vector2> validMoves = new ArrayList<>();
//    private List<Dot> dots = new ArrayList<>();
//
//    Array<Sprite> greenBarSprites = new Array<>();
//
//    public GameScreen(ChessFormer game) {
//        this.game = game;
//        WINDOW_WIDTH = Gdx.graphics.getWidth();
//        WINDOW_HEIGHT = Gdx.graphics.getHeight();
//        LOGGER = new FileLogger(GameScreen.class.getName());
//        LOGGER.info("GameScreen initialized with window size: " + WINDOW_WIDTH + "x" + WINDOW_HEIGHT);
//
//        blockRects = new ArrayList<>();
//        playChessList = new ArrayList<>();
//    }
//
//    @Override
//    public void show() {
//        batch = new SpriteBatch();
//        camera = new OrthographicCamera();
//
//        try {
//            map = new TmxMapLoader().load("Map_Assets/Map_Level_15.tmx");
//            LOGGER.info("Map loaded: " + map);
//        } catch (Exception e) {
//            LOGGER.error("Error loading map: " + e.getMessage());
//        }
//
//        mapRenderer = new OrthogonalTiledMapRenderer(map, UNIT_SCALE);
//        loadBlockRects();
//        loadChess();
//        loadGreenBar();
//
//        camera.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
//
//        Gdx.input.setInputProcessor(this);
//        lastClickedPosition = new Vector2();
//    }
//
//    @Override
//    public void render(float delta) {
//        Gdx.gl.glClearColor(0, 0, 0, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        batch.enableBlending();
//
//        checkIfHitTargetChess();
//
//        if (targetChess != null)
//            targetChess.update(delta, blockRects, playChessList);
//
//        camera.update();
//        mapRenderer.setView(camera);
//        mapRenderer.render();
//
//        batch.setProjectionMatrix(camera.combined);
//        batch.begin();
//
//        for (Chess chess : playChessList) {
//            chess.update(delta, blockRects, playChessList);
//            chess.draw(batch);
//        }
//
//        if (targetChess != null)
//            targetChess.draw(batch);
//
//        for (Sprite sprite : greenBarSprites) {
//            sprite.draw(batch);
//        }
//
//        // Vẽ dấu chấm các nước đi hợp lệ
//        for (Dot dot : dots) {
//            dot.draw(batch);
//        }
//
//        batch.end();
//    }
//
//    public void checkIfHitTargetChess() {
//        if (targetChess == null) return;
//        if (targetChess.IsRotating()) return;
//        for (Chess chess : playChessList) {
//            if (chess.getPosition().epsilonEquals(targetChess.getPosition(), 0.1f)) {
//                targetChess.setRotation(true);
//                return;
//            }
//        }
//    }
//
//    public void changeMap(String mapFile) {
//        if (map != null) map.dispose();
//        map = new TmxMapLoader().load(mapFile);
//        mapRenderer.setMap(map);
//    }
//
//    public void loadBlockRects() {
//        blockRects.clear();
//        MapLayer blockLayer = map.getLayers().get("Block");
//        if (blockLayer != null) {
//            for (MapObject obj : blockLayer.getObjects()) {
//                if (obj instanceof PolygonMapObject) {
//                    blockRects.add(((PolygonMapObject) obj).getPolygon());
//                }
//            }
//        } else {
//            LOGGER.info("Block layer not found in the map.");
//        }
//        LOGGER.info("Block rectangles loaded: " + blockRects.size());
//    }
//
//    public void loadChess() {
//        playChessList.clear();
//        MapLayer playerChessLayer = map.getLayers().get("PlayerChess");
//
//        if (playerChessLayer != null) {
//            for (MapObject obj : playerChessLayer.getObjects()) {
//                float x = obj.getProperties().get("x", Float.class);
//                float y = obj.getProperties().get("y", Float.class) - 32;
//                String name = obj.getName();
//
//                float tileX = x * UNIT_SCALE;
//                float tileY = y * UNIT_SCALE;
//                boolean isWhite = true;
//                Chess chess = ChessFactory.createChess(name, new Vector2(tileX, tileY), isWhite);
//
//                playChessList.add(chess);
//            }
//        }
//
//        MapLayer targetChessLayer = map.getLayers().get("TargetChess");
//        if (targetChessLayer != null) {
//            for (MapObject obj : targetChessLayer.getObjects()) {
//                float x = obj.getProperties().get("x", Float.class);
//                float y = obj.getProperties().get("y", Float.class) - 32;
//
//                boolean isWhite = false;
//                targetChess = ChessFactory.createChess("targetchess", new Vector2(x * UNIT_SCALE, y * UNIT_SCALE), isWhite);
//            }
//        }
//    }
//
//    @Override
//    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//        Vector3 worldCoordinates = camera.unproject(new Vector3(screenX, screenY, 0));
//        lastClickedPosition.set(worldCoordinates.x, worldCoordinates.y);
//
//        int tileX = (int) (worldCoordinates.x / UNIT_SCALE);
//        int tileY = (int) (worldCoordinates.y / UNIT_SCALE);
//
//        // Nếu click lên một quân cờ
//        for (Chess chess : playChessList) {
//            if (chess.getTileBounds().contains(worldCoordinates.x, worldCoordinates.y)) {
//                selectedChess = chess;
//                validMoves.clear();
//                dots.clear();
//
//                LOGGER.info("Selected chess: " + chess.getName() + " at position: " + chess.getPosition());
//
//                // Nếu là Knight thì lấy nước đi hợp lệ
//                if (selectedChess instanceof Knight) {
//                    Chess[][] board = generateBoardFromList(playChessList);
////                    validMoves = ((Knight) selectedChess).getValidMoves(board, playChessList);
//                    List<Vector2> validMoves = Knight.getValidMoves(board, chessList, platforms);
//
//                    for (Vector2 move : validMoves) {
//                        LOGGER.info("Valid Knight move: " + move);
//                    }
//
//                    // Tạo các Dot để vẽ dấu chấm
//                    for (Vector2 pos : validMoves) {
//                        dots.add(new Dot(pos));
//                    }
//                }
//
//                return false; // Đã chọn quân, không xử lý tiếp
//            }
//        }
//
//        if (selectedChess == null) return false;
//
//        Vector2 clickedWorld = new Vector2(worldCoordinates.x, worldCoordinates.y);
//
//        // Nếu quân được chọn là Knight, kiểm tra nước đi hợp lệ rồi di chuyển
//        if (selectedChess instanceof Knight) {
//            for (Vector2 move : validMoves) {
//                if (move.epsilonEquals(clickedWorld, UNIT_SCALE / 2f)) {
//                    selectedChess.moveTo(move.x, move.y);
//                    validMoves.clear();
//                    dots.clear();
//                    selectedChess = null;
//                    return true;
//                }
//            }
//            LOGGER.info("Clicked position not in valid Knight moves: " + clickedWorld);
//            return false;
//        }
//
//        // Nếu không phải Knight hoặc không phải nước đi hợp lệ, hiện tại không xử lý di chuyển
//        return false;
//    }
//
//    public Chess[][] generateBoardFromList(List<Chess> chessList) {
//        Chess[][] board = new Chess[12][20];
//        for (Chess chess : chessList) {
//            int x = (int) (chess.getPosition().x / UNIT_SCALE);
//            int y = (int) (chess.getPosition().y / UNIT_SCALE);
//            if (x >= 0 && x < 20 && y >= 0 && y < 12) {
//                board[y][x] = chess;
//            }
//        }
//        return board;
//    }
//
//    public void loadGreenBar() {
//        MapLayer greenBar = map.getLayers().get("GreenBar");
//
//        if (greenBar != null) {
//            for (MapObject obj : greenBar.getObjects()) {
//                float x = obj.getProperties().get("x", Float.class);
//                float y = obj.getProperties().get("y", Float.class) - 32;
//                String name = obj.getName();
//
//                String texturePath = "Map_Assets/" + name + "_32x32_top.png";
//
//                Texture texture = new Texture(Gdx.files.internal(texturePath));
//                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
//
//                Sprite sprite = new Sprite(texture);
//                sprite.setPosition(x * UNIT_SCALE, y * UNIT_SCALE);
//                sprite.setSize(32 * UNIT_SCALE, 32 * UNIT_SCALE);
//
//                greenBarSprites.add(sprite);
//            }
//        }
//    }
//
//    public boolean canMove(Vector2 newPosition) {
//        for (Polygon block : blockRects) {
//            if (block.contains(newPosition)) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public void resize(int i, int i1) {
//        camera.update();
//        LOGGER.info("Window resized to: " + i + "x" + i1);
//    }
//
//    @Override
//    public void pause() {}
//
//    @Override
//    public void resume() {}
//
//    @Override
//    public void hide() {}
//
//    @Override
//    public void dispose() {
//        batch.dispose();
//        map.dispose();
//        mapRenderer.dispose();
//    }
//}

package com.ChessFormer.screen;

import com.ChessFormer.ChessFormer;
import com.ChessFormer.FileLogger;
import com.ChessFormer.model.chess.Chess;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

import static com.ChessFormer.Game_Utilz.*;
import com.ChessFormer.model.chess.ChessFactory;
import com.badlogic.gdx.graphics.GL20;
import com.ChessFormer.model.chess.Knight;
import com.ChessFormer.model.chess.Dot;
import com.ChessFormer.model.chess.King;

public class GameScreen extends InputAdapter implements Screen {
    private FileLogger LOGGER;
    private final ChessFormer game;
    private List<Chess> chessList;
    private List<Polygon> platforms;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Vector2 lastClickedPosition;

    private int WINDOW_WIDTH;
    private int WINDOW_HEIGHT;
    private List<Polygon> blockRects;
    private List<Chess> playChessList;

    private Chess targetChess;
    private Chess selectedChess;

    private List<Vector2> validMoves = new ArrayList<>();
    private List<Dot> dots = new ArrayList<>();

    Array<Sprite> greenBarSprites = new Array<>();

    public GameScreen(ChessFormer game) {
        this.game = game;
        WINDOW_WIDTH = Gdx.graphics.getWidth();
        WINDOW_HEIGHT = Gdx.graphics.getHeight();
        LOGGER = new FileLogger(GameScreen.class.getName());
        LOGGER.info("GameScreen initialized with window size: " + WINDOW_WIDTH + "x" + WINDOW_HEIGHT);

        blockRects = new ArrayList<>();
        playChessList = new ArrayList<>();
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();

        try {
            map = new TmxMapLoader().load("Map_Assets/Map_Level_15.tmx");
            LOGGER.info("Map loaded: " + map);
        } catch (Exception e) {
            LOGGER.error("Error loading map: " + e.getMessage());
        }

        mapRenderer = new OrthogonalTiledMapRenderer(map, UNIT_SCALE);
        loadBlockRects();
        loadChess();
        loadGreenBar();

        camera.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

        Gdx.input.setInputProcessor(this);
        lastClickedPosition = new Vector2();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.enableBlending();

        checkIfHitTargetChess();

        if (targetChess != null)
            targetChess.update(delta, blockRects, playChessList);

        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (Chess chess : playChessList) {
            chess.update(delta, blockRects, playChessList);
            chess.draw(batch);
        }

        if (targetChess != null)
            targetChess.draw(batch);

        for (Sprite sprite : greenBarSprites) {
            sprite.draw(batch);
        }

        // Vẽ dấu chấm các nước đi hợp lệ
        for (Dot dot : dots) {
            dot.draw(batch);
        }

        batch.end();
    }

    public void checkIfHitTargetChess() {
        if (targetChess == null) return;
        if (targetChess.IsRotating()) return;
        for (Chess chess : playChessList) {
            if (chess.getPosition().epsilonEquals(targetChess.getPosition(), 0.1f)) {
                targetChess.setRotation(true);
                return;
            }
        }
    }

    public void changeMap(String mapFile) {
        if (map != null) map.dispose();
        map = new TmxMapLoader().load(mapFile);
        mapRenderer.setMap(map);
    }

    public void loadBlockRects() {
        blockRects.clear();
        MapLayer blockLayer = map.getLayers().get("Block");
        if (blockLayer != null) {
            for (MapObject obj : blockLayer.getObjects()) {
                if (obj instanceof PolygonMapObject) {
                    blockRects.add(((PolygonMapObject) obj).getPolygon());
                }
            }
        } else {
            LOGGER.info("Block layer not found in the map.");
        }
        LOGGER.info("Block rectangles loaded: " + blockRects.size());
    }

    public void loadChess() {
        playChessList.clear();
        MapLayer playerChessLayer = map.getLayers().get("PlayerChess");

        if (playerChessLayer != null) {
            for (MapObject obj : playerChessLayer.getObjects()) {
                float x = obj.getProperties().get("x", Float.class);
                float y = obj.getProperties().get("y", Float.class) - 32;
                String name = obj.getName();

                float tileX = x * UNIT_SCALE;
                float tileY = y * UNIT_SCALE;
                boolean isWhite = true;
                Chess chess = ChessFactory.createChess(name, new Vector2(tileX, tileY), isWhite);

                playChessList.add(chess);
            }
        }

        MapLayer targetChessLayer = map.getLayers().get("TargetChess");
        if (targetChessLayer != null) {
            for (MapObject obj : targetChessLayer.getObjects()) {
                float x = obj.getProperties().get("x", Float.class);
                float y = obj.getProperties().get("y", Float.class) - 32;

                boolean isWhite = false;
                targetChess = ChessFactory.createChess("targetchess", new Vector2(x * UNIT_SCALE, y * UNIT_SCALE), isWhite);
            }
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 worldCoordinates = camera.unproject(new Vector3(screenX, screenY, 0));
        lastClickedPosition.set(worldCoordinates.x, worldCoordinates.y);

        int tileX = (int) (worldCoordinates.x / UNIT_SCALE);
        int tileY = (int) (worldCoordinates.y / UNIT_SCALE);

        // Nếu click lên một quân cờ
        for (Chess chess : playChessList) {
            if (chess.getTileBounds().contains(worldCoordinates.x, worldCoordinates.y)) {
                selectedChess = chess;
                validMoves.clear();
                dots.clear();

                LOGGER.info("Selected chess: " + chess.getName() + " at position: " + chess.getPosition());

                Chess[][] board = generateBoardFromList(playChessList);

                // Lấy nước đi hợp lệ tùy theo loại quân
                if (selectedChess instanceof Knight) {
                    validMoves = ((Knight) selectedChess).getValidMoves(board, playChessList, blockRects);
                } else if (selectedChess instanceof King) {
                    validMoves = ((King) selectedChess).getValidMoves(board, playChessList, blockRects);
                } else {
                    // Nếu có các quân khác, xử lý tương tự ở đây
                    validMoves.clear();
                }

                // Tạo các Dot để vẽ dấu chấm
                for (Vector2 pos : validMoves) {
                    dots.add(new Dot(pos));
                }

                return false; // Đã chọn quân, không xử lý tiếp
            }
        }

        if (selectedChess == null) return false;

        Vector2 clickedWorld = new Vector2(worldCoordinates.x, worldCoordinates.y);

        // Nếu quân được chọn là Knight, kiểm tra nước đi hợp lệ rồi di chuyển
        if (selectedChess instanceof Knight) {
            for (Vector2 move : validMoves) {
                if (move.epsilonEquals(clickedWorld, UNIT_SCALE / 2f)) {
                    selectedChess.moveTo(move.x, move.y);
                    validMoves.clear();
                    dots.clear();
                    selectedChess = null;
                    return true;
                }
            }
            LOGGER.info("Clicked position not in valid Knight moves: " + clickedWorld);
            return false;
        }

        // Nếu quân được chọn là King, kiểm tra nước đi hợp lệ rồi di chuyển
        if (selectedChess instanceof King) {
            for (Vector2 move : validMoves) {
                if (move.epsilonEquals(clickedWorld, UNIT_SCALE / 2f)) {
                    selectedChess.moveTo(move.x, move.y);
                    validMoves.clear();
                    dots.clear();
                    selectedChess = null;
                    return true;
                }
            }
            LOGGER.info("Clicked position not in valid King moves: " + clickedWorld);
            return false;
        }

        // Nếu không phải Knight hay King hoặc không phải nước đi hợp lệ, hiện tại không xử lý di chuyển
        return false;
    }

    public Chess[][] generateBoardFromList(List<Chess> chessList) {
        Chess[][] board = new Chess[12][20];
        for (Chess chess : chessList) {
            int x = (int) (chess.getPosition().x / UNIT_SCALE);
            int y = (int) (chess.getPosition().y / UNIT_SCALE);
            if (x >= 0 && x < 20 && y >= 0 && y < 12) {
                board[y][x] = chess;
            }
        }
        return board;
    }

    public void loadGreenBar() {
        MapLayer greenBar = map.getLayers().get("GreenBar");

        if (greenBar != null) {
            for (MapObject obj : greenBar.getObjects()) {
                float x = obj.getProperties().get("x", Float.class);
                float y = obj.getProperties().get("y", Float.class);

                Sprite sprite = new Sprite(new Texture("UI/green_bar.png"));
                sprite.setPosition(x * UNIT_SCALE, y * UNIT_SCALE);
                greenBarSprites.add(sprite);
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        map.dispose();
        mapRenderer.dispose();
        // dispose các tài nguyên khác nếu cần
    }

    // Các phương thức còn lại (resize, pause, resume, hide) có thể giữ nguyên hoặc tùy chỉnh theo bạn

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = VIEWPORT_WIDTH;
        camera.viewportHeight = VIEWPORT_HEIGHT;
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }
//    public void dispose() {
//        batch.dispose();
//        map.dispose();
//        mapRenderer.dispose();
//    }
}

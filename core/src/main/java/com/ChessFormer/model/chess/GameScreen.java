package com.chessformer.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.chessformer.model.Chess;

public class GameScreen implements Screen {
    private SpriteBatch batch;
    private Texture pieceTexture;
    private Texture dotTexture;  // Dấu chấm
    private Chess piece;  // Quân cờ
    private Vector2 piecePos;
    private float tileSize = 64f;  // Kích thước ô cờ
    private boolean pieceSelected = false;
    private Vector2[] availableMoves;

    private OrthographicCamera camera;

    @Override
    public void show() {
        batch = new SpriteBatch();
        pieceTexture = new Texture("piece.png");  // Tải ảnh quân cờ
        dotTexture = new Texture("dot.png");  // Tải ảnh dấu chấm
        piece = new Chess("king", 3 * tileSize, 3 * tileSize);  // Tạo quân vua ở vị trí (3,3)
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false);
    }

    @Override
    public void render(float delta) {
        handleInput();
        update(delta);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(pieceTexture, piece.getPosition().x, piece.getPosition().y, tileSize, tileSize);

        // Vẽ dấu chấm lên các ô có thể di chuyển
        if (pieceSelected && availableMoves != null) {
            for (Vector2 move : availableMoves) {
                if (move != null) {
                    batch.draw(dotTexture, move.x * tileSize, move.y * tileSize, tileSize, tileSize);
                }
            }
        }

        batch.end();
    }

    private void update(float delta) {
        // Không có thay đổi trong phương thức update, trừ khi bạn muốn thêm logic di chuyển
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);
            int x = (int)(touch.x / tileSize);
            int y = (int)(touch.y / tileSize);

            // Kiểm tra nếu người chơi nhấn vào quân cờ
            if (Math.abs(x - piece.getPosition().x / tileSize) <= 1 && Math.abs(y - piece.getPosition().y / tileSize) <= 1) {
                pieceSelected = true;
                availableMoves = piece.getAvailableMoves();
            } else {
                pieceSelected = false;
                availableMoves = null;
            }
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        batch.dispose();
        pieceTexture.dispose();
        dotTexture.dispose();
    }
}

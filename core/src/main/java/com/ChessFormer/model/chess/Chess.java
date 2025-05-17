package com.ChessFormer.model.chess;

import com.ChessFormer.FileLogger;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

import static com.ChessFormer.Game_Utilz.UNIT_SCALE;
import static com.ChessFormer.Game_Utilz.GRAVITY;

public class Chess {
    public final FileLogger LOGGER = new FileLogger(Chess.class.getName());
    private Texture texture;
    private Sprite sprite;
    private Vector2 position; // WORLD POSITION
    private Vector2 velocity;
    private Vector2 targetPosition;
    private String name;
    private float originDistance;
    private float moveSpeed = 30f;
    private float rotation;
    private float rotationSpeed = 720f;
    private boolean isGrounded;
    private boolean isMoving;
    private boolean isRotating ;
    private boolean isOnOtherChess;

    public Chess(String name, Vector2 position, String texturePath) {
        this.name = name; // = type
        this.texture    = new Texture(texturePath);
        this.sprite     = new Sprite(texture);
        this.position = position;
        this.velocity       = new Vector2(0, 0);
        this.targetPosition = new Vector2();
        this.isGrounded     = false;
        this.isMoving       = false;
        this.isRotating     = false;
        this.isOnOtherChess = false;
        this.rotation = 0f;

        sprite.setSize(1f,1f);
        sprite.setOriginCenter();
        sprite.setPosition(this.position.x, this.position.y);
        LOGGER.info("Chess piece created: " + name + " at tilePosition: " + position);
    }

    public void update(float delta, List<Polygon> platforms, List<Chess> chessList) {
        if (isMoving) {
            moveProcess(delta);
        }
        else if (isGrounded == false && isOnOtherChess == false) {
            dropProcess(delta, platforms, chessList);
        }
        else if (isRotating) {
            velocity.set(0, -3f);
            rotationProcess(delta);
        }

        sprite.setRotation(rotation);
        sprite.setPosition(position.x, position.y);
    }

    public void rotationProcess(float delta) {
        position.add(velocity.x * delta, velocity.y * delta);
        rotation += rotationSpeed * delta;
        rotation %= 360; // Giới hạn góc quay từ 0 đến 360 độ
    }

    public boolean checkIfGrounded(List<Polygon> platforms) {
        float tmpX = position.x / UNIT_SCALE;
        float tmpY = position.y / UNIT_SCALE - 8f; // Giảm một chút để kiểm tra va chạm

        Rectangle tmpBounds = new Rectangle(tmpX, tmpY, getTileBounds().width, getTileBounds().height);
        for(Polygon platform : platforms) {
            Rectangle platformBounds = platform.getBoundingRectangle();
            if (tmpBounds.overlaps(platformBounds)) {
                LOGGER.info("Chess piece is grounded on platform: " + platform);
                return true;
            }
        }
        return false;
    }

    public boolean checkIfOnOtherChess(List<Chess> chessList) {
        Vector2 tmpPostition = new Vector2(position.x, position.y - 0.25f);
        for(Chess otherChess : chessList){
            if (this.equals(otherChess)){   continue;}

            Rectangle otherChessBounds = otherChess.getTileBounds();

            if (otherChessBounds.contains(tmpPostition)) {
                isOnOtherChess = true;
                return true;
            }

            isOnOtherChess = false;
        }
        return false;
    }

    public void dropProcess(float delta, List<Polygon> platforms, List<Chess> chessList) {
        if (checkIfGrounded(platforms) && !checkIfOnOtherChess(chessList)) {
            position.y = (int) position.y;
            isGrounded = true;
            velocity.y = 0;
        } else {
            isGrounded = false;
            velocity.y -= GRAVITY * delta; // Rơi xuống
            position.y += velocity.y * delta;
        }
    }

    public void moveTo(float x, float y) {
        targetPosition.set(x, y);
        Vector2 direction = new Vector2(targetPosition).sub(position);
        float distance = direction.len();
        moveSpeed = distance / 0.5f;// Adjust speed based on distance
        originDistance = distance;
        isMoving = true;
        isOnOtherChess = false;
    }

    public void moveProcess(float delta) {
        position.add(velocity.x * delta, velocity.y * delta);

        // Logic di chuyển hiện tại
        Vector2 direction = new Vector2(targetPosition).sub(position);
        float distance = direction.len();

        if (distance < originDistance / 30) {
            position.set(targetPosition);
            isGrounded = false;
            isMoving = false;
            velocity.setZero(); // Dừng lại
        } else {
            direction.nor();
            velocity.set(direction.scl(moveSpeed));
        }
    }

    public Rectangle getTileBounds() {
        float tileX = position.x;
        float tileY = position.y;
        return new Rectangle(tileX, tileY, 1, 1);
    }

    public Boolean IsRotating()     {return isRotating;}

    public String getName()         {return name;}

    public Vector2 getPosition()    {return position;}

    public void draw(SpriteBatch batch)     {sprite.draw(batch);}

    public void setRotation(Boolean state)  {this.isRotating = state;}
}

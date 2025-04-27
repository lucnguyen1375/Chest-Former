package com.ChessFormer.model;

import com.ChessFormer.FileLogger;
import com.ChessFormer.Game_Utilz;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Logger;

public class MyActor extends Actor {
    public static final int TILE_SIZE = Game_Utilz.TILE_SIZE; // Kích thước ô cờ
    private TextureRegion texture;
    FileLogger LOGGER = new FileLogger(MyActor.class.getName());
    private boolean isTouched = false;
    private final float SCALE_FACTOR = 1.5f;


    private static final float UNIT_SCALE = 32f;

    public MyActor(Texture texture, float x, float y, float width, float height) {
        this.texture = new TextureRegion(texture);
        setPosition(x, y);
        setSize(width, height);
        setOrigin(texture.getWidth() / 2, texture.getHeight() / 2);
        setBounds(x, y, width, height);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(),
                getRotation());
    }


    boolean getClicked(float x, float y){
        return (x >= getX() && x <= getX() + getWidth() &&
                y >= getY() && y <= getY() + getHeight());
    }

//    @Override
//    public void act(float delta) {
//        float x = Gdx.input.getX();
//        float y = Gdx.graphics.getHeight() - Gdx.input.getY();
//
//
//        if (Gdx.input.isTouched()) {
//            LOGGER.info("Tọa độ chuột: " + x + ", " + y);
//            if (getClicked(x,y)) {
//                //LOGGER.info("Đã chạm vào ô cờ");
//                if (!isTouched) {
//                    isTouched = true;
//                    LOGGER.info("Đã chạm vào ô cờ");
//                    setScale(SCALE_FACTOR);
//                    LOGGER.info("Kich thuoc o co " + getWidth() + "x" + getHeight());
//                    setPosition(getX() + 15, getY() + 15);
//                }
//            }
//            else  if (isTouched){
//                isTouched = false;
//                LOGGER.info("Đã rời khỏi ô cờ");
//                setPosition(getX() - 15, getY() - 15);
//                setScale(1f);
//            }
//        }
//    }
}

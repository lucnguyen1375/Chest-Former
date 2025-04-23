package com.ChessFormer.model;

import com.ChessFormer.FileLogger;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Logger;

public class MyActor extends Actor {
    private TextureRegion texture;
    FileLogger LOGGER = new FileLogger(MyActor.class.getName());

    private static final float UNIT_SCALE = 32f;

    public MyActor(Texture texture, float x, float y, float width, float height) {
        this.texture = new TextureRegion(texture);
        setPosition(x, y);
        setSize(width * 2, height * 2);
        setOrigin(texture.getWidth() / 2, texture.getHeight() / 2);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), 1, 1,
                getRotation());
    }

//    @Override
//    public void act(float delta) {
//        if (Gdx.input.isTouched()) {
//            float x = Gdx.input.getX() ;
//            float y = Gdx.input.getY() ;
//            setPosition(x, y);
//            LOGGER.info("Vị trí Actor Chess mới: " + getX() + ", " + getY());
//        }
//    }
}

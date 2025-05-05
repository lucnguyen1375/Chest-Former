package com.ChessFormer.model.chess;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Dot {
    private Texture texture;
    private Sprite sprite;

    public Dot(Vector2 position){
        this.texture = new Texture("Chess_Assets/b_Queen.png");
        this.sprite = new Sprite(texture);
        sprite.setSize(1, 1);
        sprite.setOriginCenter();
        sprite.setAlpha(0.5f);
        sprite.setPosition(position.x, position.y);
    }

    public void draw(SpriteBatch batch)     {sprite.draw(batch);}
}

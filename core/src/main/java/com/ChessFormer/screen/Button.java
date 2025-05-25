package com.ChessFormer.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Button extends Image {
    String name;

    public interface OnButtonClick {
        void onClick(String name); // Trả về "back" hoặc "reset"
    }

    public Button(String name, OnButtonClick clickHandler) {
        super(new TextureRegionDrawable(new Texture(Gdx.files.internal(getTexturePath(name)))));
        this.name = name;

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickHandler.onClick(name);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                setColor(Color.YELLOW);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                setColor(Color.WHITE);
            }
        });
    }

    private static String getTexturePath(String name) {
        return "Map_Assets/" + name + ".png";
    }
}

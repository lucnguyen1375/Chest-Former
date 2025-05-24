package com.ChessFormer.screen;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class LevelButton extends Image {
    private int level;
    private boolean unlocked;

    public interface OnLevelClick {
        void onClick(int level);
    }

    public LevelButton(int level, boolean unlocked, float x, float y, OnLevelClick clickHandler) {
        super(new TextureRegionDrawable(new Texture(Gdx.files.internal(getTexturePath(level, unlocked)))));
        this.level = level;
        this.unlocked = unlocked;

        //setSize(32, 32); // hoặc nhân UNIT_SCALE nếu cần
        setPosition(x, y);

        if (unlocked) {
            addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    clickHandler.onClick(level);
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
        } else {
            setColor(Color.GRAY); // có thể xám hóa button khóa
        }
    }

    private static String getTexturePath(int level, boolean unlocked) {
        return unlocked ? "Map_Assets/Levels/" + level + ".png" : "Map_Assets/Levels/lock.png";
    }
}

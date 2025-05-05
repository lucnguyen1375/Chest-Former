package com.ChessFormer;

import com.ChessFormer.screen.MainScreen;
import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ChessFormer extends Game {
    private FileLogger LOGGER ;

    @Override
    public void create() {
        // Khởi động với màn hình đăng nhập
        LOGGER = new FileLogger(ChessFormer.class.getName());
        LOGGER.clearExistedLogger();
        setScreen(new MainScreen(this));
        LOGGER.info("Game started - Loaded LoginScreen");
    }

    @Override
    public void render() {
        super.render(); // Tự gọi render() của màn hình hiện tại
    }

    @Override
    public void dispose() {
        // Hủy màn hình hiện tại nếu cần
        if (getScreen() != null) {
            getScreen().dispose();
        }
        LOGGER.info("Game disposed - Unloaded Game Screen");
    }

}

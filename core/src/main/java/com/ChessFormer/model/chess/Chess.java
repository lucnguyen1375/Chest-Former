package com.chessformer.model;

import com.badlogic.gdx.math.Vector2;

public class Chess {
    private String type;  // Loại quân cờ (ví dụ: "king", "queen", "bishop", ...)
    private Vector2 position;

    public Chess(String type, float x, float y) {
        this.type = type;
        this.position = new Vector2(x, y);
    }

    public String getType() {
        return type;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    // Tính toán các ô có thể di chuyển
    public Vector2[] getAvailableMoves() {
        Vector2[] moves = new Vector2[8];  // Vua có thể di chuyển 8 hướng tối đa
        int index = 0;

        if (type.equals("King")) {
            // Di chuyển cho quân vua (di chuyển 1 ô bất kỳ)
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;  // Bỏ qua vị trí hiện tại
                    moves[index++] = new Vector2(position.x + dx, position.y + dy);
                }
            }
        }
        // Cũng có thể thêm logic cho các quân khác như xe, mã, hậu, ...


        return moves;
    }
}

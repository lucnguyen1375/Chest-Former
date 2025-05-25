package com.ChessFormer.model.chess;

import com.badlogic.gdx.math.Polygon;
import static com.ChessFormer.Game_Utilz.UNIT_SCALE;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

public class Rook extends Chess {

    public Rook(Vector2 position, boolean isWhite) {
        super(isWhite ? "w_Rook" : "b_Rook", position, isWhite ? "Chess_Assets/w_Rook.png" : "Chess_Assets/b_Rook.png");
    }


    public List<Vector2> getValidMoves(Chess[][] board, List<Chess> chessList, List<Polygon> platforms) {
        List<Vector2> validMoves = new ArrayList<>();
        int cx = (int) getPosition().x;
        int cy = (int) getPosition().y;
        boolean isWhite = getName().startsWith("w") || getName().startsWith("W");

        checkDirection(validMoves, board, cx, cy, 1, 0, isWhite, platforms);   // Phải
        checkDirection(validMoves, board, cx, cy, -1, 0, isWhite, platforms);  // Trái
        checkDirection(validMoves, board, cx, cy, 0, 1, isWhite, platforms);   // Lên
        checkDirection(validMoves, board, cx, cy, 0, -1, isWhite, platforms);  // Xuống

        return validMoves;
    }

    private void checkDirection(List<Vector2> moves, Chess[][] board, int x, int y, int dx, int dy, boolean isWhite, List<Polygon> platforms) {
        int nx = x + dx;
        int ny = y + dy;

        while (nx >= 0 && nx < 20 && ny >= 0 && ny < 12) {
            // Kiểm tra vật cản
            boolean blockedByPlatform = false;
            for (Polygon polygon : platforms) {
                if (polygon.contains(nx, ny)) {
                    blockedByPlatform = true;
                    break;
                }
            }
            if (blockedByPlatform) break;

            Chess target = board[ny][nx];
            if (target == null) {
                moves.add(new Vector2(nx, ny));
            } else {
                boolean isEnemy = (isWhite && !target.getName().startsWith("w")) || (!isWhite && target.getName().startsWith("w"));
                if (isEnemy) {
                    moves.add(new Vector2(nx, ny));
                }
                break; // chạm quân
            }
            nx += dx;
            ny += dy;
        }
    }

}

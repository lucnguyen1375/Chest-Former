package com.ChessFormer.model.chess;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.math.Polygon;
public class Queen extends Chess {
    public Queen(Vector2 position, boolean isWhite) {
        super(isWhite ? "w_Queen" : "b_Queen", position,
            isWhite ? "Chess_Assets/w_Queen.png" : "Chess_Assets/b_Queen.png");
    }


    public List<Vector2> getValidMoves(Chess[][] board, List<Chess> chessList, List<Polygon> platforms) {
        List<Vector2> validMoves = new ArrayList<>();
        int cx = (int) getPosition().x;
        int cy = (int) getPosition().y;
        boolean isWhite = getName().startsWith("w") || getName().startsWith("W");

        int[] dx = {1, -1, 0, 0, 1, -1, 1, -1};
        int[] dy = {0, 0, 1, -1, 1, 1, -1, -1};

        for (int i = 0; i < dx.length; i++) {
            checkDirection(validMoves, board, cx, cy, dx[i], dy[i], isWhite);
        }

        return validMoves;
    }

    private void checkDirection(List<Vector2> moves, Chess[][] board, int x, int y, int dx, int dy, boolean isWhite) {
        int nx = x + dx;
        int ny = y + dy;

        while (nx >= 0 && nx < 20 && ny >= 0 && ny < 12) {
            Chess target = board[ny][nx];
            if (target == null) {
                moves.add(new Vector2(nx, ny));
            } else {
                boolean isEnemy = !target.getName().startsWith("w") && isWhite || !isWhite && target.getName().startsWith("w");
                if (isEnemy) {
                    moves.add(new Vector2(nx, ny));
                }
                break;
            }
            nx += dx;
            ny += dy;
        }
    }
}

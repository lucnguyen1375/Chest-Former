package com.ChessFormer.model.chess;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import static com.ChessFormer.Game_Utilz.UNIT_SCALE;
//public class Bishop extends Chess {
//    public Bishop(Vector2 position, boolean isWhite) {
//        super(isWhite ? "w_Bishop" : "b_Bishop", position,
//            isWhite ? "Chess_Assets/w_Bishop.png" : "Chess_Assets/b_Bishop.png");
//    }
//
//
//    public List<Vector2> getValidMoves(Chess[][] board, List<Chess> chessList,List<Polygon> platforms) {
//        List<Vector2> validMoves = new ArrayList<>();
//        int cx = (int) getPosition().x;
//        int cy = (int) getPosition().y;
//        boolean isWhite = getName().startsWith("w") || getName().startsWith("W");
//
//        checkDirection(validMoves, board, cx, cy, 1, 1, isWhite);   // ↗
//        checkDirection(validMoves, board, cx, cy, -1, 1, isWhite);  // ↖
//        checkDirection(validMoves, board, cx, cy, 1, -1, isWhite);  // ↘
//        checkDirection(validMoves, board, cx, cy, -1, -1, isWhite); // ↙
//
//        return validMoves;
//    }
//
//    private void checkDirection(List<Vector2> moves, Chess[][] board, int x, int y, int dx, int dy, boolean isWhite) {
//        int nx = x + dx;
//        int ny = y + dy;
//
//        while (nx >= 0 && nx < 20 && ny >= 0 && ny < 12) {
//            Chess target = board[ny][nx];
//            if (target == null) {
//                moves.add(new Vector2(nx, ny));
//            } else {
//                boolean isEnemy = !target.getName().startsWith("w") && isWhite || !isWhite && target.getName().startsWith("w");
//                if (isEnemy) {
//                    moves.add(new Vector2(nx, ny));
//                }
//                break;
//            }
//            nx += dx;
//            ny += dy;
//        }
//    }
//}

public class Bishop extends Chess {
    public Bishop(Vector2 position, boolean isWhite) {
        super(isWhite ? "w_Bishop" : "b_Bishop", position,
            isWhite ? "Chess_Assets/w_Bishop.png" : "Chess_Assets/b_Bishop.png");
    }

    public List<Vector2> getValidMoves(Chess[][] board, List<Chess> chessList, List<Polygon> platforms) {
        List<Vector2> validMoves = new ArrayList<>();
        int cx = (int) getPosition().x;
        int cy = (int) getPosition().y;
        boolean isWhite = getName().startsWith("w") || getName().startsWith("W");

        checkDirection(validMoves, board, cx, cy, 1, 1, isWhite, platforms);   // ↗
        checkDirection(validMoves, board, cx, cy, -1, 1, isWhite, platforms);  // ↖
        checkDirection(validMoves, board, cx, cy, 1, -1, isWhite, platforms);  // ↘
        checkDirection(validMoves, board, cx, cy, -1, -1, isWhite, platforms); // ↙

        return validMoves;
    }

    private void checkDirection(List<Vector2> moves, Chess[][] board, int x, int y, int dx, int dy, boolean isWhite, List<Polygon> platforms) {
        int nx = x + dx;
        int ny = y + dy;

        while (nx >= 0 && nx < 20 && ny >= 0 && ny < 12) {
            // Kiểm tra vật cản
            Vector2 pos = new Vector2(nx * UNIT_SCALE, ny * UNIT_SCALE);
            boolean blocked = false;
            for (Polygon p : platforms) {
                if (p.contains(pos)) {
                    blocked = true;
                    break;
                }
            }
            if (blocked) break;

            Chess target = board[ny][nx];
            if (target == null) {
                moves.add(new Vector2(nx, ny));
            } else {
                boolean isEnemy = (isWhite && target.getName().startsWith("b")) || (!isWhite && target.getName().startsWith("w"));
                if (isEnemy) {
                    moves.add(new Vector2(nx, ny));
                }
                break; // Dù là địch hay bạn cũng dừng lại
            }

            nx += dx;
            ny += dy;
        }
    }
}

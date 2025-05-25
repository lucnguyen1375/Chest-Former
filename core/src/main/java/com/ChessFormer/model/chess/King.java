package com.ChessFormer.model.chess;
import com.badlogic.gdx.math.Polygon;
import static com.ChessFormer.Game_Utilz.UNIT_SCALE;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
public class King extends Chess {
    public King(Vector2 position, boolean isWhite) {
        super(
            isWhite ? "w_King" : "b_King",
            position,
            isWhite ? "Chess_Assets/w_King.png" : "Chess_Assets/b_King.png"
        );
    }


    public List<Vector2> getValidMoves(Chess[][] board, List<Chess> chessList, List<Polygon> platforms)
    {
        List<Vector2> moves = new ArrayList<>();
        int x = (int) getPosition().x;
        int y = (int) getPosition().y;
        boolean isWhite = getName().startsWith("w") || getName().startsWith("W");

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && nx < 20 && ny >= 0 && ny < 12) {
                    Chess target = board[ny][nx];

                    //  Bỏ qua nếu có quân ta
                    if (target != null && ((isWhite && target.getName().startsWith("w")) || (!isWhite && target.getName().startsWith("b")))) {
                        continue;
                    }

                    //  Bỏ qua nếu có vật cản (platform)
                    boolean blocked = false;
                    for (Polygon polygon : platforms) {
                        if (polygon.contains(nx, ny)) {
                            blocked = true;
                            break;
                        }
                    }

                    if (!blocked) {
                        moves.add(new Vector2(nx, ny));
                    }
                }
            }
        }

        return moves;
    }
}

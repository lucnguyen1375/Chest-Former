package com.ChessFormer.model.chess;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.math.Polygon;
public class Pawn extends Chess {
    private boolean firstMove = true;

    public Pawn(Vector2 position, boolean isWhite) {
        super(isWhite ? "w_Pawn" : "b_Pawn", position, isWhite ? "Chess_Assets/w_Pawn.png" : "Chess_Assets/b_Pawn.png");
    }

    public List<Vector2> getValidMoves(Chess[][] board, List<Chess> chessList, List<Polygon> platforms) {
        List<Vector2> validMoves = new ArrayList<>();
        int x = (int) getPosition().x;
        int y = (int) getPosition().y;

        boolean isWhite = getName().startsWith("w");
        int dir = isWhite ? 1 : -1;

        // Một ô phía trước
        if (isInBounds(x, y + dir) && board[y + dir][x] == null) {
            validMoves.add(new Vector2(x, y + dir));

            // Nước đi đầu tiên có thể đi 2 ô
            if (firstMove && isInBounds(x, y + 2 * dir) && board[y + 2 * dir][x] == null) {
                validMoves.add(new Vector2(x, y + 2 * dir));
            }
        }

        // Ăn chéo
        if (isInBounds(x + 1, y + dir)) {
            Chess target = board[y + dir][x + 1];
            if (target != null && isEnemy(target, isWhite)) {
                validMoves.add(new Vector2(x + 1, y + dir));
            }
        }
        if (isInBounds(x - 1, y + dir)) {
            Chess target = board[y + dir][x - 1];
            if (target != null && isEnemy(target, isWhite)) {
                validMoves.add(new Vector2(x - 1, y + dir));
            }
        }

        return validMoves;
    }

    public void setFirstMoveFalse() {
        firstMove = false;
    }

    public boolean isPromotable() {
        int y = (int) getPosition().y;
        return (getName().startsWith("w") && y == 11) || (getName().startsWith("b") && y == 0);
    }

    private boolean isEnemy(Chess target, boolean isWhite) {
        return target != null && isWhite != target.getName().startsWith("w");
    }

    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < 20 && y >= 0 && y < 12;
    }
}

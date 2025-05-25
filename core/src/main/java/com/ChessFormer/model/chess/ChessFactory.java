package com.ChessFormer.model.chess;

import com.badlogic.gdx.math.Vector2;

public class ChessFactory {
    public static Chess createChess(String name, Vector2 position, boolean isWhite) {
        switch (name.toLowerCase()) {
            case "pawn":
                return new Pawn(position, isWhite);
            case "rook":
                return new Rook(position, isWhite);
            case "bishop":
                return new Bishop(position, isWhite);
            case "queen":
                return new Queen(position, isWhite);
            case "king":
                return new King(position, isWhite);
            case "knight":
                return new Knight(position, isWhite);
            case "targetchess": // Mặc định là bishop như bạn yêu cầu
                return new Bishop(position, isWhite);
            default:
                throw new IllegalArgumentException("Unknown chess type: " + name);
        }
    }
}



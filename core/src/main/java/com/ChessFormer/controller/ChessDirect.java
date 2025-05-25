package com.ChessFormer.controller;

import java.awt.*;
import java.util.List;

public class ChessDirect {

    public static final List<Point> KingDirect = List.of(
            new Point(1, 0),   // Right
            new Point(-1, 0),  // Left
            new Point(0, 1),   // Up
            new Point(0, -1),  // Down
            new Point(1, 1),   // Up-Right
            new Point(-1, -1), // Down-Left
            new Point(1, -1),  // Down-Right
            new Point(-1, 1)   // Up-Left
    );

    public static final List<Point> QueenDirect = List.of(
            new Point(1, 0),   // Right
            new Point(-1, 0),  // Left
            new Point(0, 1),   // Up
            new Point(0, -1),  // Down
            new Point(1, 1),   // Up-Right
            new Point(-1, -1), // Down-Left
            new Point(1, -1),  // Down-Right
            new Point(-1, 1)   // Up-Left
    );

    public static final List<Point> RookDirect = List.of(
            new Point(1, 0),   // Right
            new Point(-1, 0),  // Left
            new Point(0, 1),   // Up
            new Point(0, -1)   // Down
    );

    public static final List<Point> BishopDirect = List.of(
            new Point(1, 1),   // Up-Right
            new Point(-1, -1), // Down-Left
            new Point(1, -1),  // Down-Right
            new Point(-1, 1)   // Up-Left
    );

    public static final List<Point> KnightDirect = List.of(
            new Point(2, 1),   // Up-Right
            new Point(2, -1),  // Down-Right
            new Point(-2, 1),  // Up-Left
            new Point(-2, -1), // Down-Left
            new Point(1, 2),   // Right-Up
            new Point(-1, 2),  // Right-Down
            new Point(1, -2),  // Left-Up
            new Point(-1, -2)  // Left-Down
    );

    public static final List<Point> PawnDirect = List.of(
            new Point(0, 1),   // Up
            new Point(1, 1),   // Up-Right
            new Point(-1, 1)   // Up-Left
    );

    public static List<Point> getDirectByType(String type) {
        switch (type) {
            case "King":
                return KingDirect;
            case "Queen":
                return QueenDirect;
            case "Rook":
                return RookDirect;
            case "Bishop":
                return BishopDirect;
            case "Knight":
                return KnightDirect;
            case "Pawn":
                return PawnDirect;
            default:
                throw new IllegalArgumentException("Invalid chess piece type: " + type);
        }
    }
}

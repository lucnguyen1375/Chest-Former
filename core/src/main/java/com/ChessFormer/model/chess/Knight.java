package com.ChessFormer.model.chess;
import com.badlogic.gdx.math.Polygon;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import com.ChessFormer.ChessFormer;
import com.ChessFormer.FileLogger;
import com.ChessFormer.model.chess.Chess;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

import static com.ChessFormer.Game_Utilz.*;
import com.ChessFormer.model.chess.ChessFactory;
import com.badlogic.gdx.graphics.GL20;
import com.ChessFormer.model.chess.Knight;
import com.ChessFormer.model.chess.Dot;

import static com.ChessFormer.Game_Utilz.UNIT_SCALE;

public class Knight extends Chess {
    public Knight(Vector2 position, boolean isWhite) {
        super(
            isWhite ? "w_Knight" : "b_Knight",
            position,
            isWhite ? "Chess_Assets/w_Knight.png" : "Chess_Assets/b_Knight.png"
        );
    }

    public List<Vector2> getValidMoves(Chess[][] board, List<Chess> chessList, List<Polygon> platforms) {
        List<Vector2> moves = new ArrayList<>();
        int x = (int) (getPosition().x );
        int y = (int) (getPosition().y );
        System.out.println("Knight at tile: " + x + "," + y);

        boolean isWhite = getName().startsWith("w") || getName().startsWith("W");

        int[][] directions = {
            {1, 2}, {2, 1}, {-1, 2}, {-2, 1},
            {1, -2}, {2, -1}, {-1, -2}, {-2, -1}
        };

        for (int[] d : directions) {
            int nx = x + d[0];
            int ny = y + d[1];
            if (nx >= 0 && nx < 20 && ny >= 0 && ny < 12) {
                Chess target = board[ny][nx];
                if (target == null || (isWhite && target.getName().startsWith("b")) || (!isWhite && target.getName().startsWith("w"))) {
                    moves.add(new Vector2(nx, ny));
                }
            }
        }

        return moves;
    }
}

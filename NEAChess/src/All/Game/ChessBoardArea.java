package All.Game;

import All.Resources.THEMES;

import javax.swing.*;
import java.awt.*;

public class ChessBoardArea extends JLabel {
    private int size;
    private static THEMES  theme= Main.theme;
    ChessBoardArea(int size) {
        this.setOpaque(true);
        this.setVisible(true);
        this.setBorder(BorderFactory.createLineBorder(Color.black, 3));

        this.setLayout(new GridLayout(8, 8));
        this.size=size;
        addingChessTiles();
        putInChessPieces();
        ChessTile.nextTurn();
    }
    protected static void updateBoardAppearance() {
        theme= Main.theme;
        ChessTile.theme= Main.theme;
        ChessTile.resetBackgroundTileColor();
        ChessTile.resetPointers();
        ChessTile.resetBooleanValues();
    }

    private void addingChessTiles() {
        size += 2;
        this.setBounds(30, 15, 8 * size, 8 * size);
        for (int index = 0; index < 64; index++) {
            ChessTile c=new ChessTile(index);
            c.setPiece(Piece.nopiece);
            this.add(c, index);
        }
    }

    private void putInChessPieces() {
        // pawns
        for (int index = 8; index < 16; index++) {
            ChessTile.chessTiles.get(index).setPiece(Piece.BPAWN);
        }
        for (int index = 48; index < 56; index++) {
            ChessTile.chessTiles.get(index).setPiece(Piece.WPAWN);
        }

        // rooks
        ChessTile.chessTiles.get(0).setPiece(Piece.BROOK);
        ChessTile.chessTiles.get(7).setPiece(Piece.BROOK);
        ChessTile.chessTiles.get(56).setPiece(Piece.WROOK);
        ChessTile.chessTiles.get(63).setPiece(Piece.WROOK);

        // knights
        ChessTile.chessTiles.get(1).setPiece(Piece.BKNIGHT);
        ChessTile.chessTiles.get(6).setPiece(Piece.BKNIGHT);
        ChessTile.chessTiles.get(57).setPiece(Piece.WKNIGHT);
        ChessTile.chessTiles.get(62).setPiece(Piece.WKNIGHT);

        // bishops
        ChessTile.chessTiles.get(2).setPiece(Piece.BBISHOP);
        ChessTile.chessTiles.get(5).setPiece(Piece.BBISHOP);
        ChessTile.chessTiles.get(58).setPiece(Piece.WBISHOP);
        ChessTile.chessTiles.get(61).setPiece(Piece.WBISHOP);

        // kings
        ChessTile.chessTiles.get(4).setPiece(Piece.BKING);
        ChessTile.chessTiles.get(60).setPiece(Piece.WKING);

        // queens
        ChessTile.chessTiles.get(3).setPiece(Piece.BQUEEN);
        ChessTile.chessTiles.get(59).setPiece(Piece.WQUEEN);
    }

}

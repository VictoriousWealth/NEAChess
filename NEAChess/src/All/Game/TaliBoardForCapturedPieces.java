package All.Game;

import All.Game.ChessTile;
import All.Game.Piece;

import javax.swing.*;
import java.awt.*;

public class TaliBoardForCapturedPieces extends JTextArea {

    private int[] taliForCapturedPieces = new int[]{
            /*BPAWN*/ 0,/*WPAWN*/ 0,
            /*BROOK*/ 0,/*WROOK*/ 0,
            /*BKNIGHT*/ 0,/*WKNIGHT*/ 0,
            /*BBISHOP*/ 0,/*WBISHOP*/ 0,
            /*BKING*/ 0,/*WKING*/ 0,
            /*BQUEEN*/ 0,/*WQUEEN*/ 0
    };
    private String tali = (taliForCapturedPieces[0]>0? taliForCapturedPieces[0]+" black pawn, \n":"") +
            (taliForCapturedPieces[1]>0? taliForCapturedPieces[1]+" white pawn, \n":"") +
            (taliForCapturedPieces[2]>0? taliForCapturedPieces[2]+" black rook, \n":"") +
            (taliForCapturedPieces[3]>0? taliForCapturedPieces[3]+" white rook, \n":"") +
            (taliForCapturedPieces[4]>0? taliForCapturedPieces[4]+" black knight, \n":"") +
            (taliForCapturedPieces[5]>0? taliForCapturedPieces[5]+" white knight, \n":"") +
            (taliForCapturedPieces[6]>0? taliForCapturedPieces[6]+" black bishop, \n":"") +
            (taliForCapturedPieces[7]>0? taliForCapturedPieces[7]+" white bishop, \n":"") +
            (taliForCapturedPieces[8]>0? taliForCapturedPieces[8]+" black king, \n":"") +
            (taliForCapturedPieces[9]>0? taliForCapturedPieces[9]+" white king, \n":"") +
            (taliForCapturedPieces[10]>0? taliForCapturedPieces[10]+" black queen, \n":"") +
            (taliForCapturedPieces[11]>0? taliForCapturedPieces[11]+" white queen.":"");

    public TaliBoardForCapturedPieces(int size) {
        this.setOpaque(true);
        this.setVisible(true);
        this.setBorder(BorderFactory.createLineBorder(Color.black, 3));
        this.setBackground(new Color(168, 255, 5));
//        this.setBounds(size*8+100, 15, size*6, size*3);
        this.setMinimumSize(new Dimension(size*6, size*3));
        this.setText(ChessTile.piecesCaptured.toString());
        this.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        this.setEditable(false);
    }
    public void updateTali() {
        int[] tempOldTali=taliForCapturedPieces.clone();
        taliForCapturedPieces=new int[] {
                /*BPAWN*/ 0,/*WPAWN*/ 0,
                /*BROOK*/ 0,/*WROOK*/ 0,
                /*BKNIGHT*/ 0,/*WKNIGHT*/ 0,
                /*BBISHOP*/ 0,/*WBISHOP*/ 0,
                /*BKING*/ 0,/*WKING*/ 0,
                /*BQUEEN*/ 0,/*WQUEEN*/ 0
        };
        for (int index = 0; index < ChessTile.piecesCaptured.size(); index++) {
            switch (ChessTile.piecesCaptured.get(index)) {
                case BPAWN -> taliForCapturedPieces[0] += 1;
                case WPAWN -> taliForCapturedPieces[1] += 1;
                case BROOK -> taliForCapturedPieces[2] += 1;
                case WROOK -> taliForCapturedPieces[3] += 1;
                case BKNIGHT -> taliForCapturedPieces[4] += 1;
                case WKNIGHT -> taliForCapturedPieces[5] += 1;
                case BBISHOP -> taliForCapturedPieces[6] += 1;
                case WBISHOP -> taliForCapturedPieces[7] += 1;
                case BKING -> taliForCapturedPieces[8] += 1;
                case WKING -> taliForCapturedPieces[9] += 1;
                case BQUEEN -> taliForCapturedPieces[10] += 1;
                case WQUEEN -> taliForCapturedPieces[11] += 1;
            }
        }

        for (int index = 0; index < taliForCapturedPieces.length; index++) {
            if (taliForCapturedPieces[index]!=tempOldTali[index]) {
                switch (index) {
                    case 0, 1-> {
                        if (tali.contains(tempOldTali[index] + (index == 0 ? " black " : " white ") + "pawn \n")) {
                            tali = tali.replace(tempOldTali[index] + (index == 0 ? " black " : " white ") + "pawn \n",
                                    "");
                        }
                        tali = tali.concat(taliForCapturedPieces[index] +
                                (index == 0 ? " black " : " white ") + "pawn \n");
                    }
                    case 2, 3-> {
                        if (tali.contains(tempOldTali[index] + (index == 2 ? " black " : " white ") + "rook \n")) {
                            tali=tali.replace(tempOldTali[index] + (index == 2 ? " black " : " white ") +
                                            "rook \n", "");
                        }
                        tali = tali.concat(taliForCapturedPieces[index] +
                                (index == 2 ? " black " : " white ") + "rook \n");
                    }
                    case 4, 5-> {
                        if (tali.contains(tempOldTali[index] + (index == 4 ? " black " : " white ") + "knight \n")) {
                            tali=tali.replace(tempOldTali[index] + (index == 4 ? " black " : " white ") +
                                    "knight \n", "");
                        }
                        tali = tali.concat(taliForCapturedPieces[index] +
                                (index == 4 ? " black " : " white ") + "knight \n");
                    }
                    case 6, 7-> {
                        if (tali.contains(tempOldTali[index] + (index == 6 ? " black " : " white ") + "bishop \n")) {
                            tali=tali.replace(tempOldTali[index] + (index == 6 ? " black " : " white ") +
                                    "bishop \n", "");
                        }
                        tali = tali.concat(taliForCapturedPieces[index] +
                                (index == 6 ? " black " : " white ") + "bishop \n");
                    }
                    case 8, 9-> {
                        if (tali.contains(tempOldTali[index] + (index == 8 ? " black " : " white ") + "king \n")) {
                            tali=tali.replace(tempOldTali[index] + (index == 8 ? " black " : " white ") +
                                    "king \n", "");
                        }
                        tali = tali.concat(taliForCapturedPieces[index] +
                                (index == 8 ? " black " : " white ") + "king \n");
                    }
                    case 10, 11-> {
                        if (tali.contains(tempOldTali[index] + (index == 10 ? " black " : " white ") + "queen \n")) {
                            tali=tali.replace(tempOldTali[index] + (index == 10 ? " black " : " white ") +
                                    "queen \n", "");
                        }
                        tali = tali.concat(taliForCapturedPieces[index] +
                                (index == 10 ? " black " : " white ") + "queen \n");
                    }
                }
            }
        }
        this.setText(tali);
    }
}

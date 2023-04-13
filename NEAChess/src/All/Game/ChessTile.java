package All.Game;

import All.Resources.ImageFields;
import All.Resources.Palette;
import All.Resources.THEMES;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import static All.Game.Piece.*;


public class ChessTile extends JButton implements ActionListener {

    private static boolean alternator = true;
    private static final int TOTAL_COMPONENT_NUMBER = 64;
    protected static final ArrayList<ChessTile> chessTiles = new ArrayList<>();
    private static boolean noOtherTileIsSelected = true;
    protected static boolean isBlackTurn = false;
    protected static THEMES theme = Main.theme;
    public static Stack<Piece> piecesCaptured = new Stack<>();


    private static int blackKingPosition;
    private static int whiteKingPosition;


    private static boolean blackKingInCheck;
    private static boolean whiteKingInCheck;
    private static Piece pieceThatIsCheckingKing;
    private static int componentNumberOfPieceThatIsCheckingKing;
    private static boolean[]  canBeForcedToCaptureAnotherPiece = {false, false};
    private static boolean[] canCapturePieceThatIsCheckingTheKing = {false, false};


    protected final int componentNumber; // starts from zero
    protected int toBeSwappedComponentNumber;
    private boolean selected = false;
    private boolean possibleDestination = false;
    private boolean possibleToBeCaptured = false;
    private boolean occupied;
    private Piece piece;
    private PieceColor pieceColor;
    private int moveCounter = -1; // chose to implement this a global variable as certain moves such as en passant or
    // castling are only allowed depending on how many times a piece has moved
    private boolean locked;


    ChessTile(int componentNumber) {
        this.componentNumber = componentNumber;
        this.occupied = false;
        this.locked = false;

        this.setVisible(true);
        this.setOpaque(true);
        this.setBackgroundColor();
        this.addActionListener(this);
        chessTiles.add(this);
    }


    private void setBackgroundColor() {
        this.setBackground(alternator ? theme.get(Palette.lightTileColor) :
                theme.get(Palette.darkTileColor));
        if (!(componentNumber % 8 == 7)) {
            alternator = !alternator;
        }
    }


    protected static void resetBackgroundTileColor() {
        alternator = true;
        for (int index = 0; index < TOTAL_COMPONENT_NUMBER; index++) {
            chessTiles.get(index).setBackground(alternator ? theme.get(Palette.lightTileColor) :
                    theme.get(Palette.darkTileColor));
            if (!(index % 8 == 7)) {
                alternator = !alternator;
            }
        }
    }
    protected static void resetBooleanValues() {
        for (int index = 0; index < TOTAL_COMPONENT_NUMBER; index++) {
            chessTiles.get(index).selected = false;
            chessTiles.get(index).possibleToBeCaptured = false;
            chessTiles.get(index).possibleDestination = false;
        }
    }
    protected static void resetPointers() {
        for (int index = 0; index < TOTAL_COMPONENT_NUMBER; index++) {
            chessTiles.get(index).toBeSwappedComponentNumber = -1;
        }
    }


    public void setPiece(Piece piece) {
        this.piece = piece;
        this.occupied = true;
        if (this.moveCounter < 0) this.moveCounter = 0;

        switch (piece) {
            case BPAWN -> {
                this.setIcon(ImageFields.B_PAWN_IMAGE);
                this.pieceColor = PieceColor.black;
            }
            case WPAWN -> {
                this.setIcon(ImageFields.W_PAWN_IMAGE);
                this.pieceColor = PieceColor.white;
            }
            case BROOK -> {
                this.setIcon(ImageFields.B_ROOK_IMAGE);
                this.pieceColor = PieceColor.black;
            }
            case WROOK -> {
                this.setIcon(ImageFields.W_ROOK_IMAGE);
                this.pieceColor = PieceColor.white;
            }
            case BBISHOP -> {
                this.setIcon(ImageFields.B_BISHOP_IMAGE);
                this.pieceColor = PieceColor.black;
            }
            case WBISHOP -> {
                this.setIcon(ImageFields.W_BISHOP_IMAGE);
                this.pieceColor = PieceColor.white;
            }
            case BKNIGHT -> {
                this.setIcon(ImageFields.B_KNIGHT_IMAGE);
                this.pieceColor = PieceColor.black;
            }
            case WKNIGHT -> {
                this.setIcon(ImageFields.W_KNIGHT_IMAGE);
                this.pieceColor = PieceColor.white;
            }
            case BKING -> {
                this.setIcon(ImageFields.B_KING_IMAGE);
                this.pieceColor = PieceColor.black;
                blackKingPosition = this.componentNumber;
            }
            case WKING -> {
                this.setIcon(ImageFields.W_KING_IMAGE);
                this.pieceColor = PieceColor.white;
                whiteKingPosition = this.componentNumber;
            }
            case BQUEEN -> {
                this.setIcon(ImageFields.B_QUEEN_IMAGE);
                this.pieceColor = PieceColor.black;
            }
            case WQUEEN -> {
                this.setIcon(ImageFields.W_QUEEN_IMAGE);
                this.pieceColor = PieceColor.white;
            }
            case nopiece -> {
                this.occupied = false;
                this.setIcon(new ImageIcon());
                this.pieceColor = PieceColor.noColor;
            }
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this) {
            System.out.println(isBlackTurn ? blackKingPosition : whiteKingPosition);
            if (noOtherTileIsSelected && !this.possibleDestination && !this.possibleToBeCaptured) { // then select tile
                this.setBackground(theme.get(Palette.selectedTileColor));
                this.selected = true;
                noOtherTileIsSelected = false;
                if (this.occupied && (isBlackTurn ? this.isBlack() : this.isWhite())) { // will show possible moves and kills
                    this.showPossibleMoves(true);
                    this.showPossibleKills(true);
                }
            } else if (!noOtherTileIsSelected && !this.possibleDestination &&
                    !this.possibleToBeCaptured /*&& !noOtherTileIsSelected*/) {
                resetBackgroundTileColor();
                resetBooleanValues();
                resetPointers();
                noOtherTileIsSelected = true;
//                if (this.occupied && (isBlackTurn?this.isBlack():this.isWhite())) {
//                    this.selected=true;
//                    this.setBackground(theme.get(Palette.selectedTileColor));
//                    this.showPossibleMoves();
//                    this.showPossibleKills();
//                    noOtherTileIsSelected = false;
//                }
            } else if (this.possibleDestination) {
                if (chessTiles.get(this.toBeSwappedComponentNumber).piece == BPAWN &&
                        Arrays.equals(findAndGetRow(63), findAndGetRow(this.componentNumber))) {
                    openPopUpBlackPawnPromotionWindow(this.componentNumber, this.toBeSwappedComponentNumber);
                } else if (chessTiles.get(this.toBeSwappedComponentNumber).piece == WPAWN &&
                        Arrays.equals(findAndGetRow(1), findAndGetRow(this.componentNumber))) {
                    openPopUpWhitePawnPromotionWindow(this.componentNumber, this.toBeSwappedComponentNumber);
                } else {

                    // swap
                    if (!castling(false)) {
                        TaliBoardForMoves.collectInfoFromMovesMade(
                                chessTiles.get(this.toBeSwappedComponentNumber).piece, this.componentNumber,
                                this.toBeSwappedComponentNumber);
                        Main.taliBoardForMoves.updateTaliBoardMoves("M");
                        ChessTile temp = chessTiles.get(this.toBeSwappedComponentNumber);
//                {
//                    System.out.println(temp.componentNumber);
//                    System.out.println(this.toBeSwappedComponentNumber);
//
//                    System.out.println(this.piece);
//                    System.out.println(temp.piece);
//
//
//                    System.out.println(temp.getIcon().toString());
//                }
                        this.setPiece(temp.piece);
                        trackingKingPosition();
                        chessTiles.get(this.toBeSwappedComponentNumber).setPiece(nopiece);
                        // it wasn't working before because the value was passed by ref rather than by val
                        this.moveCounter = 1 + chessTiles.get(this.toBeSwappedComponentNumber).moveCounter;
                        chessTiles.get(this.toBeSwappedComponentNumber).moveCounter = 0;

//                // testing if moveCounter updates and tracks move correctly
//                System.out.println("The " +this.pieceColor+ " " + this.piece + " has made " + this.moveCounter + " moves in total throughout the game");
                    } else {
                        castling(true);
                    }
                }

                resetBackgroundTileColor();
                resetPointers();
                resetBooleanValues();
                noOtherTileIsSelected = true;
                nextTurn();
                Main.nextTurnButton.setText(ChessTile.isBlackTurn ? "It's Black's turn" : "It's White's turn");

                kingIsInCheck(isBlackTurn ? PieceColor.black : PieceColor.white);

                // used so that the player can remove their king from being in check
                if (isBlackTurn ? blackKingInCheck : whiteKingInCheck) {
                    for (ChessTile c : chessTiles) {
                        if (c.pieceColor == (isBlackTurn ? PieceColor.black : PieceColor.white)) {
                            c.locked = true;
                        }
                    }
                } else {
                    for (ChessTile c : chessTiles) {
                        if (c.pieceColor == (isBlackTurn ? PieceColor.black : PieceColor.white)) {
                            c.locked = false;
                        }
                    }
                }

                if (/*if king is in check AND cannot be moved to a different tile
                        AND another piece canNOT kill the piece checking him //TODO
                        AND cannot kill the piece that is checking him without being put into check again //TODO
                        AND another piece canNOT be moved to prevent capture */
                        Arrays.equals(showKingMoves(chessTiles.get(isBlackTurn ? blackKingPosition : whiteKingPosition),
                                false), new int[]{-1, -1, -1, -1, -1, -1, -1, -1})
                                && Arrays.equals(showKingKillables(chessTiles.get(isBlackTurn ? blackKingPosition : whiteKingPosition),
                                false), new int[]{-1, -1, -1, -1, -1, -1, -1, -1})
                                && checkIfPieceThatIsCheckingKingWillBeForcedToCaptureAnotherPieceOtherThanTheKingOnceItHasBeenMovedAndReturnTrueIfNone()
                                && checkIfPieceThatIsCheckingKingCanBeCapturedByAnotherPieceAndOrKingAndReturnFalseIfTrue()
                                && (isBlackTurn ? blackKingInCheck : whiteKingInCheck)

                    /* && !checkIfPieceThatIsCheckingKingCanBeCapturedByKingOrAnotherPiece()*/) {
                    openPopUpWinningWindow((isBlackTurn ? PieceColor.black : PieceColor.white));
                }

//                {
//                    System.out.println(temp.componentNumber);
//                    System.out.println(this.toBeSwappedComponentNumber);
//
//                    System.out.println(this.piece);
//                    System.out.println(temp.piece);
//
//
//                    System.out.println(this.getIcon().toString());
//                }
            } else /*if (this.possibleToBeCaptured)*/ {
                switch (this.piece) {
                    case BPAWN, WPAWN -> System.out.println("A " + (this.piece == BPAWN ? "black pawn" :
                            "white pawn") + " has been captured");
                    case BBISHOP, WBISHOP -> System.out.println("A " + (this.piece == BBISHOP ? "black bishop" :
                            "white bishop") + " has been captured");
                    case BROOK, WROOK -> System.out.println("A " + (this.piece == BROOK ? "black rook" :
                            "white rook") + " has been captured");
                    case BKNIGHT, WKNIGHT -> System.out.println("A " + (this.piece == BKNIGHT ? "black knight" :
                            "white knight") + " has been captured");
                    case BKING, WKING -> System.out.println("A " + (this.piece == BKING ? "black king" :
                            "white king") + " has been captured");
                    case BQUEEN, WQUEEN -> System.out.println("A " + (this.piece == BQUEEN ? "black queen" :
                            "white queen") + " has been captured");
                }

                piecesCaptured.push(this.piece);
                Main.taliForCapturedPieces.updateTali();
                TaliBoardForMoves.collectInfoFromMovesMade(
                        chessTiles.get(this.toBeSwappedComponentNumber).piece, this.componentNumber, this.toBeSwappedComponentNumber);
                Main.taliBoardForMoves.updateTaliBoardMoves("K");
                this.setPiece(chessTiles.get(this.toBeSwappedComponentNumber).piece);
                if (this.piece == BKING) {
                    blackKingPosition = this.componentNumber;
                } else if (this.piece == WKING) {
                    whiteKingPosition = this.componentNumber;
                }


                if (chessTiles.get(this.toBeSwappedComponentNumber).piece == BPAWN &&
                        Arrays.equals(findAndGetRow(63), findAndGetRow(this.componentNumber))) {
                    openPopUpBlackPawnPromotionWindow(this.componentNumber, this.toBeSwappedComponentNumber);
                } else if (chessTiles.get(this.toBeSwappedComponentNumber).piece == WPAWN &&
                        Arrays.equals(findAndGetRow(1), findAndGetRow(this.componentNumber))) {
                    openPopUpWhitePawnPromotionWindow(this.componentNumber, this.toBeSwappedComponentNumber);
                } else {
                    chessTiles.get(this.toBeSwappedComponentNumber).setPiece(nopiece);
                    this.moveCounter = 1 + chessTiles.get(this.toBeSwappedComponentNumber).moveCounter;
                    chessTiles.get(this.toBeSwappedComponentNumber).moveCounter = 0;
                }


                // testing if moveCounter updates and tracks move correctly
                System.out.println("The " + this.pieceColor + " " + this.piece + " has made " + this.moveCounter + " moves in total throughout the game");

                resetBackgroundTileColor();
                resetPointers();
                resetBooleanValues();
                nextTurn();
                Main.nextTurnButton.setText(ChessTile.isBlackTurn ? "It's Black's turn" : "It's White's turn");
                noOtherTileIsSelected = true;


                kingIsInCheck(isBlackTurn ? PieceColor.black : PieceColor.white);

                // used so that the player can remove their king from being in check
                if (isBlackTurn ? blackKingInCheck : whiteKingInCheck) {
                    for (ChessTile c : chessTiles) {
                        if (c.pieceColor == (isBlackTurn ? PieceColor.black : PieceColor.white)) {
                            c.locked = true;
                        }
                    }
                } else {
                    for (ChessTile c : chessTiles) {
                        if (c.pieceColor == (isBlackTurn ? PieceColor.black : PieceColor.white)) {
                            c.locked = false;
                        }
                    }
                }

                if (/*if king is in check AND cannot be moved to a different tile
                        AND another piece canNOT kill the piece checking him //TODO
                        AND cannot kill the piece that is checking him without being put into check again //TODO
                        AND another piece canNOT be moved to prevent capture */
                        Arrays.equals(showKingMoves(chessTiles.get(isBlackTurn ? blackKingPosition : whiteKingPosition),
                                false), new int[]{-1, -1, -1, -1, -1, -1, -1, -1})
                                && Arrays.equals(showKingKillables(chessTiles.get(isBlackTurn ? blackKingPosition : whiteKingPosition), false),
                                new int[]{-1, -1, -1, -1, -1, -1, -1, -1})
                                && checkIfPieceThatIsCheckingKingWillBeForcedToCaptureAnotherPieceOtherThanTheKingOnceItHasBeenMovedAndReturnTrueIfNone()
                                && checkIfPieceThatIsCheckingKingCanBeCapturedByAnotherPieceAndOrKingAndReturnFalseIfTrue()
                                && (isBlackTurn ? blackKingInCheck : whiteKingInCheck)

                    /* && !checkIfPieceThatIsCheckingKingCanBeCapturedByKingOrAnotherPiece()*/) {
                    openPopUpWinningWindow((isBlackTurn ? PieceColor.black : PieceColor.white));
                }
            }
        }
    }


    private void trackingKingPosition() {
        if (this.piece == BKING) {
            blackKingPosition = this.componentNumber;
        } else if (this.piece == WKING) {
            whiteKingPosition = this.componentNumber;
        }
    }


    private void showPossibleKills(boolean visible) {
        switch (this.piece) {
            case BPAWN -> showBlackPawnKillables(this, visible); // TODO en passant
            case WPAWN -> // TODO en passant
                    showWhitePawnKillables(this, visible);
            case BROOK, WROOK -> showRookKillables(this, visible); // DONE

            case BKNIGHT, WKNIGHT -> showKnightKillables(this, visible); // DONE

            case BBISHOP, WBISHOP -> showBishopKillables(this, visible); // DONE

            case BKING, WKING -> showKingKillables(this, visible); // DONE

            case BQUEEN, WQUEEN -> showQueenKillables(this, visible); // DONE
        }
    }

    private void showBlackPawnKillables(ChessTile c, boolean visible) {
        if (!c.locked) {
            if (c.componentNumber + 9 < TOTAL_COMPONENT_NUMBER) {
                if (chessTiles.get(c.componentNumber + 9).isWhite() &&
                        findAndGetRow(c.componentNumber + 9)[0] ==
                                findAndGetRow(c.componentNumber + 8)[0]) {
                    chessTiles.get(c.componentNumber + 9).setBackground(theme.get(Palette.possibleKillTileColor));
                    chessTiles.get(c.componentNumber + 9).toBeSwappedComponentNumber = c.componentNumber;
                    chessTiles.get(c.componentNumber + 9).possibleToBeCaptured = true;
                }
            }
            if (componentNumber + 7 < TOTAL_COMPONENT_NUMBER) {
                if (chessTiles.get(c.componentNumber + 7).isWhite() &&
                        findAndGetRow(c.componentNumber + 7)[0] ==
                                findAndGetRow(c.componentNumber + 8)[0]) {
                    chessTiles.get(c.componentNumber + 7).setBackground(theme.get(Palette.possibleKillTileColor));
                    chessTiles.get(c.componentNumber + 7).possibleToBeCaptured = true;
                    chessTiles.get(c.componentNumber + 7).toBeSwappedComponentNumber = c.componentNumber;
                }
            }
        }
        else {
            switch (pieceThatIsCheckingKing) {
                case WROOK -> {
                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(c.componentNumber + 9,
                            componentNumberOfPieceThatIsCheckingKing)) {
                        if (c.componentNumber + 9 < TOTAL_COMPONENT_NUMBER) {
                            if (chessTiles.get(c.componentNumber + 9).isWhite() &&
                                    findAndGetRow(c.componentNumber + 9)[0] ==
                                            findAndGetRow(c.componentNumber + 8)[0]) {
                                if (visible) {
                                    chessTiles.get(c.componentNumber + 9).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(c.componentNumber + 9).toBeSwappedComponentNumber = c.componentNumber;
                                    chessTiles.get(c.componentNumber + 9).possibleToBeCaptured = true;
                                }
                                canBeForcedToCaptureAnotherPiece[0] = true;
                            }
                        }
                    }
                }
                case WBISHOP -> {
                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(c.componentNumber + 9,
                            componentNumberOfPieceThatIsCheckingKing)) {
                        if (c.componentNumber + 9 < TOTAL_COMPONENT_NUMBER) {
                            if (chessTiles.get(c.componentNumber + 9).isWhite() &&
                                    findAndGetRow(c.componentNumber + 9)[0] ==
                                            findAndGetRow(c.componentNumber + 8)[0]) {
                                if (visible) {
                                    chessTiles.get(c.componentNumber + 9).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(c.componentNumber + 9).toBeSwappedComponentNumber = c.componentNumber;
                                    chessTiles.get(c.componentNumber + 9).possibleToBeCaptured = true;
                                }
                                canBeForcedToCaptureAnotherPiece[0] = true;
                            }
                        }
                    }
                }
                case WQUEEN -> {
                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(c.componentNumber + 9,
                            componentNumberOfPieceThatIsCheckingKing)) {
                        if (c.componentNumber + 9 < TOTAL_COMPONENT_NUMBER) {
                            if (chessTiles.get(c.componentNumber + 9).isWhite() &&
                                    findAndGetRow(c.componentNumber + 9)[0] ==
                                            findAndGetRow(c.componentNumber + 8)[0]) {
                                if (visible) {
                                    chessTiles.get(c.componentNumber + 9).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(c.componentNumber + 9).toBeSwappedComponentNumber = c.componentNumber;
                                    chessTiles.get(c.componentNumber + 9).possibleToBeCaptured = true;
                                }
                                canBeForcedToCaptureAnotherPiece[0] = true;
                            }
                        }
                    }
                }
            }
            if (c.componentNumber + 9 == componentNumberOfPieceThatIsCheckingKing) {
                if (visible) {
                    chessTiles.get(c.componentNumber + 9).setBackground(theme.get(Palette.possibleKillTileColor));
                    chessTiles.get(c.componentNumber + 9).toBeSwappedComponentNumber = c.componentNumber;
                    chessTiles.get(c.componentNumber + 9).possibleToBeCaptured = true;
                }
                canCapturePieceThatIsCheckingTheKing[0] = true;
            }
            switch (pieceThatIsCheckingKing) {
                case WROOK -> {
                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(c.componentNumber + 9,
                            componentNumberOfPieceThatIsCheckingKing)) {
                        if (c.componentNumber + 7 < TOTAL_COMPONENT_NUMBER) {
                            if (chessTiles.get(c.componentNumber + 7).isWhite() &&
                                    findAndGetRow(c.componentNumber + 7)[0] ==
                                            findAndGetRow(c.componentNumber + 8)[0]) {
                                if (visible) {
                                    chessTiles.get(c.componentNumber + 7).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(c.componentNumber + 7).toBeSwappedComponentNumber = c.componentNumber;
                                    chessTiles.get(c.componentNumber + 7).possibleToBeCaptured = true;
                                }
                                canBeForcedToCaptureAnotherPiece[0] = true;
                            }
                        }
                    }
                }
                case WBISHOP -> {
                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(c.componentNumber + 9,
                            componentNumberOfPieceThatIsCheckingKing)) {
                        if (c.componentNumber + 7 < TOTAL_COMPONENT_NUMBER) {
                            if (chessTiles.get(c.componentNumber + 7).isWhite() &&
                                    findAndGetRow(c.componentNumber + 7)[0] ==
                                            findAndGetRow(c.componentNumber + 8)[0]) {
                                if (visible) {
                                    chessTiles.get(c.componentNumber + 7).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(c.componentNumber + 7).toBeSwappedComponentNumber = c.componentNumber;
                                    chessTiles.get(c.componentNumber + 7).possibleToBeCaptured = true;
                                }
                                canBeForcedToCaptureAnotherPiece[0] = true;
                            }
                        }
                    }
                }
                case WQUEEN -> {
                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(c.componentNumber + 9,
                            componentNumberOfPieceThatIsCheckingKing)) {
                        if (c.componentNumber + 7 < TOTAL_COMPONENT_NUMBER) {
                            if (chessTiles.get(c.componentNumber + 7).isWhite() &&
                                    findAndGetRow(c.componentNumber + 7)[0] ==
                                            findAndGetRow(c.componentNumber + 8)[0]) {
                                if (visible) {
                                    chessTiles.get(c.componentNumber + 7).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(c.componentNumber + 7).toBeSwappedComponentNumber = c.componentNumber;
                                    chessTiles.get(c.componentNumber + 7).possibleToBeCaptured = true;
                                }
                                canBeForcedToCaptureAnotherPiece[0] = true;
                            }
                        }
                    }
                }
            }
            if (c.componentNumber + 7 == componentNumberOfPieceThatIsCheckingKing) {
                if (visible) {
                    chessTiles.get(c.componentNumber + 7).setBackground(theme.get(Palette.possibleKillTileColor));
                    chessTiles.get(c.componentNumber + 7).toBeSwappedComponentNumber = c.componentNumber;
                    chessTiles.get(c.componentNumber + 7).possibleToBeCaptured = true;
                }
                canCapturePieceThatIsCheckingTheKing[0] = true;
            }
        }
    }
    private void showWhitePawnKillables(ChessTile c, boolean visible) {
        if (!c.locked) {
            // finds out the c.componentNumbers of the valid tiles that the piece can move to
            if (c.componentNumber - 9 > -1) {
                if (chessTiles.get(c.componentNumber - 9).isBlack() &&
                        findAndGetRow(c.componentNumber - 9)[0] ==
                                findAndGetRow(c.componentNumber - 8)[0]) {
                    chessTiles.get(c.componentNumber - 9).setBackground(theme.get(Palette.possibleKillTileColor));
                    chessTiles.get(c.componentNumber - 9).possibleToBeCaptured = true;
                    chessTiles.get(c.componentNumber - 9).toBeSwappedComponentNumber = c.componentNumber;
                }
            }
            if (c.componentNumber - 7 > -1) {
                if (chessTiles.get(c.componentNumber - 7).isBlack() &&
                        findAndGetRow(c.componentNumber - 7)[0] ==
                                findAndGetRow(c.componentNumber - 8)[0]) {
                    chessTiles.get(c.componentNumber - 7).setBackground(theme.get(Palette.possibleKillTileColor));
                    chessTiles.get(c.componentNumber - 7).possibleToBeCaptured = true;
                    chessTiles.get(c.componentNumber - 7).toBeSwappedComponentNumber = c.componentNumber;
                }
            }
        } else {
            switch (pieceThatIsCheckingKing) {
                case BROOK -> {
                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(c.componentNumber - 9,
                            componentNumberOfPieceThatIsCheckingKing)) {
                        if (c.componentNumber - 9 > -1) {
                            if (chessTiles.get(c.componentNumber - 9).isBlack() &&
                                    findAndGetRow(c.componentNumber - 9)[0] ==
                                            findAndGetRow(c.componentNumber - 8)[0]) {
                                if (visible) {
                                    chessTiles.get(c.componentNumber - 9).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(c.componentNumber - 9).toBeSwappedComponentNumber = c.componentNumber;
                                    chessTiles.get(c.componentNumber - 9).possibleToBeCaptured = true;
                                }
                                canBeForcedToCaptureAnotherPiece[1] = true;
                            }
                        }
                    }
                }
                case BBISHOP -> {
                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(c.componentNumber - 9,
                            componentNumberOfPieceThatIsCheckingKing)) {
                        if (c.componentNumber - 9 < TOTAL_COMPONENT_NUMBER) {
                            if (chessTiles.get(c.componentNumber - 9).isBlack() &&
                                    findAndGetRow(c.componentNumber - 9)[0] ==
                                            findAndGetRow(c.componentNumber - 8)[0]) {
                                if (visible) {
                                    chessTiles.get(c.componentNumber - 9).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(c.componentNumber - 9).toBeSwappedComponentNumber = c.componentNumber;
                                    chessTiles.get(c.componentNumber - 9).possibleToBeCaptured = true;
                                }
                                canBeForcedToCaptureAnotherPiece[1] = true;
                            }
                        }
                    }
                }
                case BQUEEN -> {
                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(c.componentNumber - 9,
                            componentNumberOfPieceThatIsCheckingKing)) {
                        if (c.componentNumber - 9 < TOTAL_COMPONENT_NUMBER) {
                            if (chessTiles.get(c.componentNumber - 9).isBlack() &&
                                    findAndGetRow(c.componentNumber - 9)[0] ==
                                            findAndGetRow(c.componentNumber - 8)[0]) {
                                if (visible) {
                                    chessTiles.get(c.componentNumber - 9).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(c.componentNumber - 9).toBeSwappedComponentNumber = c.componentNumber;
                                    chessTiles.get(c.componentNumber - 9).possibleToBeCaptured = true;
                                }
                                canBeForcedToCaptureAnotherPiece[1] = true;
                            }
                        }
                    }
                }
            }
            if (c.componentNumber - 9 == componentNumberOfPieceThatIsCheckingKing) {
                if (visible) {
                    chessTiles.get(c.componentNumber - 9).setBackground(theme.get(Palette.possibleKillTileColor));
                    chessTiles.get(c.componentNumber - 9).toBeSwappedComponentNumber = c.componentNumber;
                    chessTiles.get(c.componentNumber - 9).possibleToBeCaptured = true;
                }
                canCapturePieceThatIsCheckingTheKing[1] = true;
            }

            switch (pieceThatIsCheckingKing) {
                case BROOK -> {
                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(c.componentNumber - 9,
                            componentNumberOfPieceThatIsCheckingKing)) {
                        if (c.componentNumber - 7 > -1) {
                            if (chessTiles.get(c.componentNumber - 7).isBlack() &&
                                    findAndGetRow(c.componentNumber - 9)[0] ==
                                            findAndGetRow(c.componentNumber - 8)[0]) {
                                if (visible) {
                                    chessTiles.get(c.componentNumber - 7).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(c.componentNumber - 7).toBeSwappedComponentNumber = c.componentNumber;
                                    chessTiles.get(c.componentNumber - 7).possibleToBeCaptured = true;
                                }
                                canBeForcedToCaptureAnotherPiece[1] = true;
                            }
                        }
                    }
                }
                case BBISHOP -> {
                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(c.componentNumber - 9,
                            componentNumberOfPieceThatIsCheckingKing)) {
                        if (c.componentNumber - 7 < TOTAL_COMPONENT_NUMBER) {
                            if (chessTiles.get(c.componentNumber - 7).isBlack() &&
                                    findAndGetRow(c.componentNumber - 7)[0] ==
                                            findAndGetRow(c.componentNumber - 8)[0]) {
                                if (visible) {
                                    chessTiles.get(c.componentNumber - 7).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(c.componentNumber - 7).toBeSwappedComponentNumber = c.componentNumber;
                                    chessTiles.get(c.componentNumber - 7).possibleToBeCaptured = true;
                                }
                                canBeForcedToCaptureAnotherPiece[1] = true;
                            }
                        }
                    }
                }
                case BQUEEN -> {
                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(c.componentNumber - 9,
                            componentNumberOfPieceThatIsCheckingKing)) {
                        if (c.componentNumber - 7 < TOTAL_COMPONENT_NUMBER) {
                            if (chessTiles.get(c.componentNumber - 7).isBlack() &&
                                    findAndGetRow(c.componentNumber - 7)[0] ==
                                            findAndGetRow(c.componentNumber - 8)[0]) {
                                if (visible) {
                                    chessTiles.get(c.componentNumber - 7).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(c.componentNumber - 7).toBeSwappedComponentNumber = c.componentNumber;
                                    chessTiles.get(c.componentNumber - 7).possibleToBeCaptured = true;
                                }
                                canBeForcedToCaptureAnotherPiece[1] = true;
                            }
                        }
                    }
                }
            }
            if (c.componentNumber - 7 == componentNumberOfPieceThatIsCheckingKing) {
                if (visible) {
                    chessTiles.get(c.componentNumber - 7).setBackground(theme.get(Palette.possibleKillTileColor));
                    chessTiles.get(c.componentNumber - 7).toBeSwappedComponentNumber = c.componentNumber;
                    chessTiles.get(c.componentNumber - 7).possibleToBeCaptured = true;
                }
                canCapturePieceThatIsCheckingTheKing[1] = true;
            }
        }
    }
    private void showBishopKillables(ChessTile c, boolean visible) {
        if (!c.locked) {
            if (!(c.componentNumber == findAndGetRow(c.componentNumber)[1])) {
                for (int index = c.componentNumber + 9; index < TOTAL_COMPONENT_NUMBER; index += 9) {
                    if ((c.piece == BBISHOP && chessTiles.get(index).isWhite()) ||
                            (c.piece == WBISHOP && chessTiles.get(index).isBlack())) {
                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                        chessTiles.get(index).possibleToBeCaptured = true;
                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                        break;
                    }
                    if (findAndGetRow(index)[1] == index) {
                        break;
                    }
                    if ((c.piece == BBISHOP ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    }
                }

                for (int index = c.componentNumber - 7; index > -1; index -= 7) {
                    if ((c.piece == BBISHOP && chessTiles.get(index).isWhite()) ||
                            (c.piece == WBISHOP && chessTiles.get(index).isBlack())) {
                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                        chessTiles.get(index).possibleToBeCaptured = true;
                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                        break;
                    }
                    if (findAndGetRow(index)[1] == index) {
                        break;
                    }
                    if ((c.piece == BBISHOP ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    }
                }
            }
            if (!(c.componentNumber == findAndGetRow(c.componentNumber)[0])) {
                for (int index = c.componentNumber - 9; index > -1; index -= 9) {
                    if ((c.piece == BBISHOP && chessTiles.get(index).isWhite()) ||
                            (c.piece == WBISHOP && chessTiles.get(index).isBlack())) {
                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                        chessTiles.get(index).possibleToBeCaptured = true;
                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                        break;
                    }
                    if (findAndGetRow(index)[0] == index) {
                        break;
                    }
                    if ((c.piece == BBISHOP ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    }
                }
                for (int index = c.componentNumber + 7; index < TOTAL_COMPONENT_NUMBER; index += 7) {
                    if ((c.piece == BBISHOP && chessTiles.get(index).isWhite()) ||
                            (c.piece == WBISHOP && chessTiles.get(index).isBlack())) {
                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                        chessTiles.get(index).possibleToBeCaptured = true;
                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                        break;
                    }
                    if (findAndGetRow(index)[0] == index) {
                        break;
                    }
                    if ((c.piece == BBISHOP ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    }
                }
            }
        } else {
            if (!(c.componentNumber == findAndGetRow(c.componentNumber)[1])) {
                for (int index = c.componentNumber + 9; index < TOTAL_COMPONENT_NUMBER; index += 9) {
                    if ((c.piece == BBISHOP && chessTiles.get(index).isWhite()) ||
                            (c.piece == WBISHOP && chessTiles.get(index).isBlack())) {
                        switch (pieceThatIsCheckingKing) {
                            case WROOK, BROOK -> {
                                if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                            case WBISHOP, BBISHOP -> {
                                if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                            case WQUEEN, BQUEEN -> {
                                if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                        }
                        break;
                    }
                    if (findAndGetRow(index)[1] == index) {
                        break;
                    }
                    if ((c.piece == BBISHOP ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    }
                }

                for (int index = c.componentNumber - 7; index > -1; index -= 7) {
                    if ((c.piece == BBISHOP && chessTiles.get(index).isWhite()) ||
                            (c.piece == WBISHOP && chessTiles.get(index).isBlack())) {
                        switch (pieceThatIsCheckingKing) {
                            case WROOK, BROOK -> {
                                if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                            case WBISHOP, BBISHOP -> {
                                if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                            case WQUEEN, BQUEEN -> {
                                if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                        }
                        break;
                    }
                    if (findAndGetRow(index)[1] == index) {
                        break;
                    }
                    if ((c.piece == BBISHOP ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    }
                }
            }
            if (!(c.componentNumber == findAndGetRow(c.componentNumber)[0])) {
                for (int index = c.componentNumber - 9; index > -1; index -= 9) {
                    if ((c.piece == BBISHOP && chessTiles.get(index).isWhite()) ||
                            (c.piece == WBISHOP && chessTiles.get(index).isBlack())) {
                        switch (pieceThatIsCheckingKing) {
                            case WROOK, BROOK -> {
                                if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                            case WBISHOP, BBISHOP -> {
                                if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                            case WQUEEN, BQUEEN -> {
                                if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                        }
                        break;
                    }
                    if (findAndGetRow(index)[0] == index) {
                        break;
                    }
                    if ((c.piece == BBISHOP ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    }
                }
                for (int index = c.componentNumber + 7; index < TOTAL_COMPONENT_NUMBER; index += 7) {
                    if ((c.piece == BBISHOP && chessTiles.get(index).isWhite()) ||
                            (c.piece == WBISHOP && chessTiles.get(index).isBlack())) {
                        switch (pieceThatIsCheckingKing) {
                            case WROOK, BROOK -> {
                                if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                            case WBISHOP, BBISHOP -> {
                                if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                            case WQUEEN, BQUEEN -> {
                                if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                        }
                        break;
                    }
                    if (findAndGetRow(index)[0] == index) {
                        break;
                    }
                    if ((c.piece == BBISHOP ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    }
                }
            }

            if (!(c.componentNumber == findAndGetRow(c.componentNumber)[1])) {
                for (int index = c.componentNumber + 9; index < TOTAL_COMPONENT_NUMBER; index += 9) {
                    if ((c.piece == BBISHOP && chessTiles.get(index).isWhite()) ||
                            (c.piece == WBISHOP && chessTiles.get(index).isBlack())) {
                        if (chessTiles.get(index).piece == pieceThatIsCheckingKing) {
                            if (visible) {
                                chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                chessTiles.get(index).possibleToBeCaptured = true;
                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                            }
                            if (c.isBlack()) {
                                canCapturePieceThatIsCheckingTheKing[0] = true;
                            } else {
                                canCapturePieceThatIsCheckingTheKing[1] = true;
                            }
                        }
                        break;
                    }
                    if (findAndGetRow(index)[1] == index) {
                        break;
                    }
                    if ((c.piece == BBISHOP ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    }
                }

                for (int index = c.componentNumber - 7; index > -1; index -= 7) {
                    if ((c.piece == BBISHOP && chessTiles.get(index).isWhite()) ||
                            (c.piece == WBISHOP && chessTiles.get(index).isBlack())) {
                        if (chessTiles.get(index).piece == pieceThatIsCheckingKing) {
                            if (visible) {
                                chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                chessTiles.get(index).possibleToBeCaptured = true;
                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                            }
                            if (c.isBlack()) {
                                canCapturePieceThatIsCheckingTheKing[0] = true;
                            } else {
                                canCapturePieceThatIsCheckingTheKing[1] = true;
                            }
                        }
                        break;
                    }
                    if (findAndGetRow(index)[1] == index) {
                        break;
                    }
                    if ((c.piece == BBISHOP ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    }
                }
            }
            if (!(c.componentNumber == findAndGetRow(c.componentNumber)[0])) {
                for (int index = c.componentNumber - 9; index > -1; index -= 9) {
                    if ((c.piece == BBISHOP && chessTiles.get(index).isWhite()) ||
                            (c.piece == WBISHOP && chessTiles.get(index).isBlack())) {
                        if (chessTiles.get(index).piece == pieceThatIsCheckingKing) {
                            if (visible) {
                                chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                chessTiles.get(index).possibleToBeCaptured = true;
                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                            }
                            if (c.isBlack()) {
                                canCapturePieceThatIsCheckingTheKing[0] = true;
                            } else {
                                canCapturePieceThatIsCheckingTheKing[1] = true;
                            }
                        }
                        break;
                    }
                    if (findAndGetRow(index)[0] == index) {
                        break;
                    }
                    if ((c.piece == BBISHOP ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    }
                }
                for (int index = c.componentNumber + 7; index < TOTAL_COMPONENT_NUMBER; index += 7) {
                    if ((c.piece == BBISHOP && chessTiles.get(index).isWhite()) ||
                            (c.piece == WBISHOP && chessTiles.get(index).isBlack())) {
                        if (chessTiles.get(index).piece == pieceThatIsCheckingKing) {
                            if (visible) {
                                chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                chessTiles.get(index).possibleToBeCaptured = true;
                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                            }
                            if (c.isBlack()) {
                                canCapturePieceThatIsCheckingTheKing[0] = true;
                            } else {
                                canCapturePieceThatIsCheckingTheKing[1] = true;
                            }
                        }
                        break;
                    }
                    if (findAndGetRow(index)[0] == index) {
                        break;
                    }
                    if ((c.piece == BBISHOP ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    }
                }
            }
        }
    }
    private void showKnightKillables(ChessTile c, boolean visible) {
        int[] arrayOfDestination = new int[8];
        arrayOfDestination[0] = (c.componentNumber + 2 * 8 + 1 < TOTAL_COMPONENT_NUMBER
                && findAndGetRow(c.componentNumber + 2 * 8 + 1)[1] == findAndGetRow(c.componentNumber + 2 * 8)[1]
                ? c.componentNumber + 2 * 8 + 1 : -1);
        arrayOfDestination[1] = (c.componentNumber + 2 * 8 - 1 < TOTAL_COMPONENT_NUMBER
                && findAndGetRow(c.componentNumber + 2 * 8 - 1)[1] == findAndGetRow(c.componentNumber + 2 * 8)[1]
                ? c.componentNumber + 2 * 8 - 1 : -1);
        arrayOfDestination[2] = (c.componentNumber - 2 * 8 + 1 > -1
                && findAndGetRow(c.componentNumber - 2 * 8 + 1)[1] == findAndGetRow(c.componentNumber - 2 * 8)[1]
                ? c.componentNumber - 2 * 8 + 1 : -1);
        arrayOfDestination[3] = (c.componentNumber - 2 * 8 - 1 > -1
                && findAndGetRow(c.componentNumber - 2 * 8 - 1)[1] == findAndGetRow(c.componentNumber - 2 * 8)[1]
                ? c.componentNumber - 2 * 8 - 1 : -1);
        arrayOfDestination[4] = (c.componentNumber + /* 1* */ 8 + 2 < TOTAL_COMPONENT_NUMBER
                && findAndGetRow(c.componentNumber + 8 + 2)[1] == findAndGetRow(c.componentNumber + 8)[1]
                ? c.componentNumber + /* 1* */ 8 + 2 : -1);
        arrayOfDestination[5] = (c.componentNumber + /* 1* */ 8 - 2 < TOTAL_COMPONENT_NUMBER
                && findAndGetRow(c.componentNumber + 8 - 2)[1] == findAndGetRow(c.componentNumber + 8)[1]
                ? c.componentNumber + /* 1* */ 8 - 2 : -1);
        arrayOfDestination[6] = (c.componentNumber - /* 1* */ 8 + 2 > -1
                && findAndGetRow(c.componentNumber - 8 + 2)[1] == findAndGetRow(c.componentNumber - 8)[1]
                ? c.componentNumber - /* 1* */ 8 + 2 : -1);
        arrayOfDestination[7] = (c.componentNumber - /* 1* */ 8 - 2 > -1
                && findAndGetRow(c.componentNumber - 8 - 2)[1] == findAndGetRow(c.componentNumber - 8)[1]
                ? c.componentNumber - /* 1* */ 8 - 2 : -1);

        // checking if array contains correct no of destinations per row (which should be 2)

        for (int destination : arrayOfDestination) {
            if (destination == -1 || (c.piece == BKNIGHT && chessTiles.get(destination).isBlack())
                    || (c.piece == WKNIGHT && chessTiles.get(destination).isWhite())
                    || (chessTiles.get(destination).piece == nopiece)) {
                continue;
            }
            if (!c.locked) {
                if (visible) {
                    chessTiles.get(destination).setBackground(theme.get(Palette.possibleKillTileColor));
                    chessTiles.get(destination).possibleToBeCaptured = true;
                    chessTiles.get(destination).toBeSwappedComponentNumber = c.componentNumber;
                }
            } else {
                switch (pieceThatIsCheckingKing) {
                    case WROOK, BROOK -> {
                        if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(destination, componentNumberOfPieceThatIsCheckingKing)) {
                            if (visible) {
                                chessTiles.get(destination).setBackground(theme.get(Palette.possibleKillTileColor));
                                chessTiles.get(destination).possibleToBeCaptured = true;
                                chessTiles.get(destination).toBeSwappedComponentNumber = c.componentNumber;
                            }
                            if (isBlackTurn) {
                                canBeForcedToCaptureAnotherPiece[0] = true;
                            } else {
                                canBeForcedToCaptureAnotherPiece[1] = true;
                            }
                        }
                    }
                    case WBISHOP, BBISHOP -> {
                        if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(destination, componentNumberOfPieceThatIsCheckingKing)) {
                            if (visible) {
                                chessTiles.get(destination).setBackground(theme.get(Palette.possibleKillTileColor));
                                chessTiles.get(destination).possibleToBeCaptured = true;
                                chessTiles.get(destination).toBeSwappedComponentNumber = c.componentNumber;
                            }
                            if (isBlackTurn) {
                                canBeForcedToCaptureAnotherPiece[0] = true;
                            } else {
                                canBeForcedToCaptureAnotherPiece[1] = true;
                            }
                        }
                    }
                    case WQUEEN, BQUEEN -> {
                        if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(destination, componentNumberOfPieceThatIsCheckingKing)) {
                            if (visible) {
                                chessTiles.get(destination).setBackground(theme.get(Palette.possibleKillTileColor));
                                chessTiles.get(destination).possibleToBeCaptured = true;
                                chessTiles.get(destination).toBeSwappedComponentNumber = c.componentNumber;
                            }
                            if (isBlackTurn) {
                                canBeForcedToCaptureAnotherPiece[0] = true;
                            } else {
                                canBeForcedToCaptureAnotherPiece[1] = true;
                            }
                        }
                    }
                }
                if (chessTiles.get(destination).piece == pieceThatIsCheckingKing) {
                    if (visible) {
                        chessTiles.get(destination).setBackground(theme.get(Palette.possibleKillTileColor));
                        chessTiles.get(destination).possibleToBeCaptured = true;
                        chessTiles.get(destination).toBeSwappedComponentNumber = c.componentNumber;
                        break;
                    }
                    if (c.isBlack()) {
                        canCapturePieceThatIsCheckingTheKing[0] = true;
                    } else {
                        canCapturePieceThatIsCheckingTheKing[1] = true;
                    }
                }
            }
        }

    }
    private void showRookKillables(ChessTile c, boolean visible) {
        if (!c.locked) {
            // +++++
            // up for black rook and down for white rook
            for (int index = componentNumber + 8; index < TOTAL_COMPONENT_NUMBER; index += 8) {
                if ((c.piece == BROOK ?
                        chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                    break;
                } else if ((c.piece == BROOK && chessTiles.get(index).isWhite()) ||
                        (c.piece == WROOK && chessTiles.get(index).isBlack())) {
                    chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                    chessTiles.get(index).possibleToBeCaptured = true;
                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                    break;
                }
            }
            // right for black rook and left for white rook
            // +1, +2, +3, until componentNumber+n==boundaryComponentOfTheRight
            for (int index = c.componentNumber + 1; index <= findAndGetRow(c.componentNumber)[1]; index++) {
                if ((c.piece == BROOK ?
                        chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                    break;
                } else if ((c.piece == BROOK && chessTiles.get(index).isWhite()) ||
                        (c.piece == WROOK && chessTiles.get(index).isBlack())) {
                    chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                    chessTiles.get(index).possibleToBeCaptured = true;
                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                    break;
                }
            }
            // -----
            // left for black rook and right for white rook
            for (int index = c.componentNumber - 1; index >= findAndGetRow(c.componentNumber)[0]; index--) {
                if ((c.piece == BROOK ?
                        chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                    break;
                } else if ((c.piece == BROOK && chessTiles.get(index).isWhite()) ||
                        (c.piece == WROOK && chessTiles.get(index).isBlack())) {
                    chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                    chessTiles.get(index).possibleToBeCaptured = true;
                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                    break;
                }
            }
            // down for black rook and up for white rook
            for (int index = c.componentNumber - 8; index > -1; index -= 8) {
                if ((c.piece == BROOK ?
                        chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                    break;
                } else if ((c.piece == BROOK && chessTiles.get(index).isWhite()) ||
                        (c.piece == WROOK && chessTiles.get(index).isBlack())) {
                    chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                    chessTiles.get(index).possibleToBeCaptured = true;
                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                    break;
                }
            }
        } else {
            // +++++
            // up for black rook and down for white rook
            for (int index = componentNumber + 8; index < TOTAL_COMPONENT_NUMBER; index += 8) {
                if ((c.piece == BROOK ?
                        chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                    break;
                } else if ((c.piece == BROOK && chessTiles.get(index).isWhite()) ||
                        (c.piece == WROOK && chessTiles.get(index).isBlack())) {
                    switch (pieceThatIsCheckingKing) {
                        case WROOK, BROOK -> {
                            if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index, componentNumberOfPieceThatIsCheckingKing)) {
                                if (visible) {
                                    chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(index).possibleToBeCaptured = true;
                                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                if (isBlackTurn) {
                                    canBeForcedToCaptureAnotherPiece[0] = true;
                                } else {
                                    canBeForcedToCaptureAnotherPiece[1] = true;
                                }
                            }
                        }
                        case BBISHOP, WBISHOP -> {
                            if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index, componentNumberOfPieceThatIsCheckingKing)) {
                                if (visible) {
                                    chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(index).possibleToBeCaptured = true;
                                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                if (isBlackTurn) {
                                    canBeForcedToCaptureAnotherPiece[0] = true;
                                } else {
                                    canBeForcedToCaptureAnotherPiece[1] = true;
                                }
                            }
                        }
                        case WQUEEN, BQUEEN -> {
                            if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index, componentNumberOfPieceThatIsCheckingKing)) {
                                if (visible) {
                                    chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(index).possibleToBeCaptured = true;
                                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                if (isBlackTurn) {
                                    canBeForcedToCaptureAnotherPiece[0] = true;
                                } else {
                                    canBeForcedToCaptureAnotherPiece[1] = true;
                                }
                            }
                        }
                    }
                    break;
                }
            }
            // right for black rook and left for white rook
            // +1, +2, +3, until componentNumber+n==boundaryComponentOfTheRight
            for (int index = c.componentNumber + 1; index <= findAndGetRow(c.componentNumber)[1]; index++) {
                if ((c.piece == BROOK ?
                        chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                    break;
                } else if ((c.piece == BROOK && chessTiles.get(index).isWhite()) ||
                        (c.piece == WROOK && chessTiles.get(index).isBlack())) {
                    switch (pieceThatIsCheckingKing) {
                        case WROOK, BROOK -> {
                            if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index, componentNumberOfPieceThatIsCheckingKing)) {
                                if (visible) {
                                    chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(index).possibleToBeCaptured = true;
                                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                if (isBlackTurn) {
                                    canBeForcedToCaptureAnotherPiece[0] = true;
                                } else {
                                    canBeForcedToCaptureAnotherPiece[1] = true;
                                }
                            }
                        }
                        case BBISHOP, WBISHOP -> {
                            if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index, componentNumberOfPieceThatIsCheckingKing)) {
                                if (visible) {
                                    chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(index).possibleToBeCaptured = true;
                                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                if (isBlackTurn) {
                                    canBeForcedToCaptureAnotherPiece[0] = true;
                                } else {
                                    canBeForcedToCaptureAnotherPiece[1] = true;
                                }
                            }
                        }
                        case WQUEEN, BQUEEN -> {
                            if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index, componentNumberOfPieceThatIsCheckingKing)) {
                                if (visible) {
                                    chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(index).possibleToBeCaptured = true;
                                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                if (isBlackTurn) {
                                    canBeForcedToCaptureAnotherPiece[0] = true;
                                } else {
                                    canBeForcedToCaptureAnotherPiece[1] = true;
                                }
                            }
                        }
                    }
                    break;
                }
            }
            // -----
            // left for black rook and right for white rook
            for (int index = c.componentNumber - 1; index >= findAndGetRow(c.componentNumber)[0]; index--) {
                if ((c.piece == BROOK ?
                        chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                    break;
                } else if ((c.piece == BROOK && chessTiles.get(index).isWhite()) ||
                        (c.piece == WROOK && chessTiles.get(index).isBlack())) {
                    switch (pieceThatIsCheckingKing) {
                        case WROOK, BROOK -> {
                            if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index, componentNumberOfPieceThatIsCheckingKing)) {
                                if (visible) {
                                    chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(index).possibleToBeCaptured = true;
                                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                if (isBlackTurn) {
                                    canBeForcedToCaptureAnotherPiece[0] = true;
                                } else {
                                    canBeForcedToCaptureAnotherPiece[1] = true;
                                }
                            }
                        }
                        case BBISHOP, WBISHOP -> {
                            if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index, componentNumberOfPieceThatIsCheckingKing)) {
                                if (visible) {
                                    chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(index).possibleToBeCaptured = true;
                                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                if (isBlackTurn) {
                                    canBeForcedToCaptureAnotherPiece[0] = true;
                                } else {
                                    canBeForcedToCaptureAnotherPiece[1] = true;
                                }
                            }
                        }
                        case WQUEEN, BQUEEN -> {
                            if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index, componentNumberOfPieceThatIsCheckingKing)) {
                                if (visible) {
                                    chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(index).possibleToBeCaptured = true;
                                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                if (isBlackTurn) {
                                    canBeForcedToCaptureAnotherPiece[0] = true;
                                } else {
                                    canBeForcedToCaptureAnotherPiece[1] = true;
                                }
                            }
                        }
                    }
                    break;
                }
            }
            // down for black rook and up for white rook
            for (int index = c.componentNumber - 8; index > -1; index -= 8) {
                if ((c.piece == BROOK ?
                        chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                    break;
                } else if ((c.piece == BROOK && chessTiles.get(index).isWhite()) ||
                        (c.piece == WROOK && chessTiles.get(index).isBlack())) {
                    switch (pieceThatIsCheckingKing) {
                        case WROOK, BROOK -> {
                            if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index, componentNumberOfPieceThatIsCheckingKing)) {
                                if (visible) {
                                    chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(index).possibleToBeCaptured = true;
                                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                if (isBlackTurn) {
                                    canBeForcedToCaptureAnotherPiece[0] = true;
                                } else {
                                    canBeForcedToCaptureAnotherPiece[1] = true;
                                }
                            }
                        }
                        case BBISHOP, WBISHOP -> {
                            if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index, componentNumberOfPieceThatIsCheckingKing)) {
                                if (visible) {
                                    chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(index).possibleToBeCaptured = true;
                                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                if (isBlackTurn) {
                                    canBeForcedToCaptureAnotherPiece[0] = true;
                                } else {
                                    canBeForcedToCaptureAnotherPiece[1] = true;
                                }
                            }
                        }
                        case WQUEEN, BQUEEN -> {
                            if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index, componentNumberOfPieceThatIsCheckingKing)) {
                                if (visible) {
                                    chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(index).possibleToBeCaptured = true;
                                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                if (isBlackTurn) {
                                    canBeForcedToCaptureAnotherPiece[0] = true;
                                } else {
                                    canBeForcedToCaptureAnotherPiece[1] = true;
                                }
                            }
                        }
                    }
                    break;
                }
            }

            // +++++
            // up for black rook and down for white rook
            for (int index = componentNumber + 8; index < TOTAL_COMPONENT_NUMBER; index += 8) {
                if ((c.piece == BROOK ?
                        chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                    break;
                } else if ((c.piece == BROOK && chessTiles.get(index).isWhite()) ||
                        (c.piece == WROOK && chessTiles.get(index).isBlack())) {
                    if (chessTiles.get(index).piece == pieceThatIsCheckingKing) {
                        if (visible) {
                            chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                            chessTiles.get(index).possibleToBeCaptured = true;
                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                        }
                        if (c.isBlack()) {
                            canCapturePieceThatIsCheckingTheKing[0] = true;
                        } else {
                            canCapturePieceThatIsCheckingTheKing[1] = true;
                        }
                    }
                    break;
                }
            }
            // right for black rook and left for white rook
            // +1, +2, +3, until componentNumber+n==boundaryComponentOfTheRight
            for (int index = c.componentNumber + 1; index <= findAndGetRow(c.componentNumber)[1]; index++) {
                if ((c.piece == BROOK ?
                        chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                    break;
                } else if ((c.piece == BROOK && chessTiles.get(index).isWhite()) ||
                        (c.piece == WROOK && chessTiles.get(index).isBlack())) {
                    if (chessTiles.get(index).piece == pieceThatIsCheckingKing) {
                        if (visible) {
                            chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                            chessTiles.get(index).possibleToBeCaptured = true;
                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                        }
                        if (c.isBlack()) {
                            canCapturePieceThatIsCheckingTheKing[0] = true;
                        } else {
                            canCapturePieceThatIsCheckingTheKing[1] = true;
                        }
                    }
                    break;
                }
            }
            // -----
            // left for black rook and right for white rook
            for (int index = c.componentNumber - 1; index >= findAndGetRow(c.componentNumber)[0]; index--) {
                if ((c.piece == BROOK ?
                        chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                    break;
                } else if ((c.piece == BROOK && chessTiles.get(index).isWhite()) ||
                        (c.piece == WROOK && chessTiles.get(index).isBlack())) {
                    if (chessTiles.get(index).piece == pieceThatIsCheckingKing) {
                        if (visible) {
                            chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                            chessTiles.get(index).possibleToBeCaptured = true;
                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                        }
                        if (c.isBlack()) {
                            canCapturePieceThatIsCheckingTheKing[0] = true;
                        } else {
                            canCapturePieceThatIsCheckingTheKing[1] = true;
                        }
                    }
                    break;
                }
            }
            // down for black rook and up for white rook
            for (int index = c.componentNumber - 8; index > -1; index -= 8) {
                if ((c.piece == BROOK ?
                        chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                    break;
                } else if ((c.piece == BROOK && chessTiles.get(index).isWhite()) ||
                        (c.piece == WROOK && chessTiles.get(index).isBlack())) {
                    if (chessTiles.get(index).piece == pieceThatIsCheckingKing) {
                        if (visible) {
                            chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                            chessTiles.get(index).possibleToBeCaptured = true;
                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                        }
                        if (c.isBlack()) {
                            canCapturePieceThatIsCheckingTheKing[0] = true;
                        } else {
                            canCapturePieceThatIsCheckingTheKing[1] = true;
                        }
                    }
                    break;
                }
            }
        }
    }
    private void showQueenKillables(ChessTile c, boolean visible) {
        if (!c.locked) {// copy and pasted code from BROOK AND WROOK
            {
                // +++++
                // up for black rook and down for white rook
                for (int index = c.componentNumber + 8; index < TOTAL_COMPONENT_NUMBER; index += 8) {
                    if ((c.piece == BQUEEN ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    } else if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                            (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                        chessTiles.get(index).possibleToBeCaptured = true;
                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                        break;
                    }
                }
                // right for black rook and left for white rook
                // +1, +2, +3, until componentNumber+n==boundaryComponentOfTheRight
                for (int index = c.componentNumber + 1; index <= findAndGetRow(c.componentNumber)[1]; index++) {
                    if ((c.piece == BQUEEN ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    } else if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                            (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                        chessTiles.get(index).possibleToBeCaptured = true;
                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                        break;
                    }
                }
                // -----
                // left for black rook and right for white rook
                for (int index = c.componentNumber - 1; index >= findAndGetRow(c.componentNumber)[0]; index--) {
                    if ((c.piece == BQUEEN ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    } else if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                            (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                        chessTiles.get(index).possibleToBeCaptured = true;
                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                        break;
                    }
                }
                // down for black rook and up for white rook
                for (int index = c.componentNumber - 8; index > -1; index -= 8) {
                    if ((c.piece == BQUEEN ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    } else if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                            (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                        chessTiles.get(index).possibleToBeCaptured = true;
                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                        break;
                    }
                }
            }

            // copy and pasted code from BBISHOP AND WBISHOP
            {
                if (!(c.componentNumber == findAndGetRow(c.componentNumber)[1])) {
                    for (int index = c.componentNumber + 9; index < TOTAL_COMPONENT_NUMBER; index += 9) {
                        if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                                (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                            chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                            chessTiles.get(index).possibleToBeCaptured = true;
                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                            break;
                        }
                        if (findAndGetRow(index)[1] == index) {
                            break;
                        }
                        if ((c.piece == BQUEEN ?
                                chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                            break;
                        }
                    }

                    for (int index = c.componentNumber - 7; index > -1; index -= 7) {
                        if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                                (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                            chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                            chessTiles.get(index).possibleToBeCaptured = true;
                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                            break;
                        }
                        if (findAndGetRow(index)[1] == index) {
                            break;
                        }
                        if ((c.piece == BQUEEN ?
                                chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                            break;
                        }
                    }
                }
                if (!(c.componentNumber == findAndGetRow(c.componentNumber)[0])) {
                    for (int index = c.componentNumber - 9; index > -1; index -= 9) {
                        if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                                (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                            chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                            chessTiles.get(index).possibleToBeCaptured = true;
                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                            break;
                        }
                        if (findAndGetRow(index)[0] == index) {
                            break;
                        }
                        if ((c.piece == BQUEEN ?
                                chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                            break;
                        }
                    }
                    for (int index = c.componentNumber + 7; index < TOTAL_COMPONENT_NUMBER; index += 7) {
                        if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                                (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                            chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                            chessTiles.get(index).possibleToBeCaptured = true;
                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                            break;
                        }
                        if (findAndGetRow(index)[0] == index) {
                            break;
                        }
                        if ((c.piece == BQUEEN ?
                                chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                            break;
                        }
                    }
                }
            }
        } else {
            // copy and pasted from showBishopKillables
            {
                if (!(c.componentNumber == findAndGetRow(c.componentNumber)[1])) {
                    for (int index = c.componentNumber + 9; index < TOTAL_COMPONENT_NUMBER; index += 9) {
                        if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                                (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                            switch (pieceThatIsCheckingKing) {
                                case WROOK, BROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index, componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                            chessTiles.get(index).possibleToBeCaptured = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        if (isBlackTurn) {
                                            canBeForcedToCaptureAnotherPiece[0] = true;
                                        } else {
                                            canBeForcedToCaptureAnotherPiece[1] = true;
                                        }
                                    }
                                }
                                case WBISHOP, BBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index, componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                            chessTiles.get(index).possibleToBeCaptured = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        if (isBlackTurn) {
                                            canBeForcedToCaptureAnotherPiece[0] = true;
                                        } else {
                                            canBeForcedToCaptureAnotherPiece[1] = true;
                                        }
                                    }
                                }
                                case WQUEEN, BQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index, componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                            chessTiles.get(index).possibleToBeCaptured = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        if (isBlackTurn) {
                                            canBeForcedToCaptureAnotherPiece[0] = true;
                                        } else {
                                            canBeForcedToCaptureAnotherPiece[1] = true;
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        if (findAndGetRow(index)[1] == index) {
                            break;
                        }
                        if ((c.piece == BQUEEN ?
                                chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                            break;
                        }
                    }
                    for (int index = c.componentNumber - 7; index > -1; index -= 7) {
                        if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                                (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                            switch (pieceThatIsCheckingKing) {
                                case WROOK, BROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index, componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                            chessTiles.get(index).possibleToBeCaptured = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        if (isBlackTurn) {
                                            canBeForcedToCaptureAnotherPiece[0] = true;
                                        } else {
                                            canBeForcedToCaptureAnotherPiece[1] = true;
                                        }
                                    }
                                }
                                case WBISHOP, BBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index, componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                            chessTiles.get(index).possibleToBeCaptured = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        if (isBlackTurn) {
                                            canBeForcedToCaptureAnotherPiece[0] = true;
                                        } else {
                                            canBeForcedToCaptureAnotherPiece[1] = true;
                                        }
                                    }
                                }
                                case WQUEEN, BQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index, componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                            chessTiles.get(index).possibleToBeCaptured = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        if (isBlackTurn) {
                                            canBeForcedToCaptureAnotherPiece[0] = true;
                                        } else {
                                            canBeForcedToCaptureAnotherPiece[1] = true;
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        if (findAndGetRow(index)[1] == index) {
                            break;
                        }
                        if ((c.piece == BQUEEN ?
                                chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                            break;
                        }
                    }
                }
                if (!(c.componentNumber == findAndGetRow(c.componentNumber)[0])) {
                    for (int index = c.componentNumber - 9; index > -1; index -= 9) {
                        if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                                (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                            switch (pieceThatIsCheckingKing) {
                                case WROOK, BROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index, componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                            chessTiles.get(index).possibleToBeCaptured = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        if (isBlackTurn) {
                                            canBeForcedToCaptureAnotherPiece[0] = true;
                                        } else {
                                            canBeForcedToCaptureAnotherPiece[1] = true;
                                        }
                                    }
                                }
                                case WBISHOP, BBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index, componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                            chessTiles.get(index).possibleToBeCaptured = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        if (isBlackTurn) {
                                            canBeForcedToCaptureAnotherPiece[0] = true;
                                        } else {
                                            canBeForcedToCaptureAnotherPiece[1] = true;
                                        }
                                    }
                                }
                                case WQUEEN, BQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index, componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                            chessTiles.get(index).possibleToBeCaptured = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        if (isBlackTurn) {
                                            canBeForcedToCaptureAnotherPiece[0] = true;
                                        } else {
                                            canBeForcedToCaptureAnotherPiece[1] = true;
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        if (findAndGetRow(index)[0] == index) {
                            break;
                        }
                        if ((c.piece == BQUEEN ?
                                chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                            break;
                        }
                    }
                    for (int index = c.componentNumber + 7; index < TOTAL_COMPONENT_NUMBER; index += 7) {
                        if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                                (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                            switch (pieceThatIsCheckingKing) {
                                case WROOK, BROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index, componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                            chessTiles.get(index).possibleToBeCaptured = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        if (isBlackTurn) {
                                            canBeForcedToCaptureAnotherPiece[0] = true;
                                        } else {
                                            canBeForcedToCaptureAnotherPiece[1] = true;
                                        }
                                    }
                                }
                                case WBISHOP, BBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index, componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                            chessTiles.get(index).possibleToBeCaptured = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        if (isBlackTurn) {
                                            canBeForcedToCaptureAnotherPiece[0] = true;
                                        } else {
                                            canBeForcedToCaptureAnotherPiece[1] = true;
                                        }
                                    }
                                }
                                case WQUEEN, BQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index, componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                            chessTiles.get(index).possibleToBeCaptured = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        if (isBlackTurn) {
                                            canBeForcedToCaptureAnotherPiece[0] = true;
                                        } else {
                                            canBeForcedToCaptureAnotherPiece[1] = true;
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        if (findAndGetRow(index)[0] == index) {
                            break;
                        }
                        if ((c.piece == BQUEEN ?
                                chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                            break;
                        }
                    }
                }
            }
            {
                if (!(c.componentNumber == findAndGetRow(c.componentNumber)[1])) {
                    for (int index = c.componentNumber + 9; index < TOTAL_COMPONENT_NUMBER; index += 9) {
                        if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                                (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                            if (chessTiles.get(index).piece == pieceThatIsCheckingKing) {
                                if (visible) {
                                    chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(index).possibleToBeCaptured = true;
                                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                if (c.isBlack()) {
                                    canCapturePieceThatIsCheckingTheKing[0] = true;
                                } else {
                                    canCapturePieceThatIsCheckingTheKing[1] = true;
                                }
                            }
                            break;
                        }
                        if (findAndGetRow(index)[1] == index) {
                            break;
                        }
                        if ((c.piece == BQUEEN ?
                                chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                            break;
                        }
                    }

                    for (int index = c.componentNumber - 7; index > -1; index -= 7) {
                        if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                                (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                            if (chessTiles.get(index).piece == pieceThatIsCheckingKing) {
                                if (visible) {
                                    chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(index).possibleToBeCaptured = true;
                                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                if (c.isBlack()) {
                                    canCapturePieceThatIsCheckingTheKing[0] = true;
                                } else {
                                    canCapturePieceThatIsCheckingTheKing[1] = true;
                                }
                            }
                            break;
                        }
                        if (findAndGetRow(index)[1] == index) {
                            break;
                        }
                        if ((c.piece == BQUEEN ?
                                chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                            break;
                        }
                    }
                }
                if (!(c.componentNumber == findAndGetRow(c.componentNumber)[0])) {
                    for (int index = c.componentNumber - 9; index > -1; index -= 9) {
                        if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                                (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                            if (chessTiles.get(index).piece == pieceThatIsCheckingKing) {
                                if (visible) {
                                    chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(index).possibleToBeCaptured = true;
                                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                if (c.isBlack()) {
                                    canCapturePieceThatIsCheckingTheKing[0] = true;
                                } else {
                                    canCapturePieceThatIsCheckingTheKing[1] = true;
                                }
                            }
                            break;
                        }
                        if (findAndGetRow(index)[0] == index) {
                            break;
                        }
                        if ((c.piece == BQUEEN ?
                                chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                            break;
                        }
                    }
                    for (int index = c.componentNumber + 7; index < TOTAL_COMPONENT_NUMBER; index += 7) {
                        if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                                (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                            if (chessTiles.get(index).piece == pieceThatIsCheckingKing) {
                                if (visible) {
                                    chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                    chessTiles.get(index).possibleToBeCaptured = true;
                                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                if (c.isBlack()) {
                                    canCapturePieceThatIsCheckingTheKing[0] = true;
                                } else {
                                    canCapturePieceThatIsCheckingTheKing[1] = true;
                                }
                            }
                            break;
                        }
                        if (findAndGetRow(index)[0] == index) {
                            break;
                        }
                        if ((c.piece == BQUEEN ?
                                chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                            break;
                        }
                    }
                }
            }

            // copy and pasted from showRookKillables
            {
                // +++++
                // up for black rook and down for white rook
                for (int index = componentNumber + 8; index < TOTAL_COMPONENT_NUMBER; index += 8) {
                    if ((c.piece == BQUEEN ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    } else if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                            (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                        switch (pieceThatIsCheckingKing) {
                            case WROOK, BROOK -> {
                                if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                            case BBISHOP, WBISHOP -> {
                                if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                            case WQUEEN, BQUEEN -> {
                                if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
                // right for black rook and left for white rook
                // +1, +2, +3, until componentNumber+n==boundaryComponentOfTheRight
                for (int index = c.componentNumber + 1; index <= findAndGetRow(c.componentNumber)[1]; index++) {
                    if ((c.piece == BQUEEN ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    } else if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                            (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                        switch (pieceThatIsCheckingKing) {
                            case WROOK, BROOK -> {
                                if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                            case BBISHOP, WBISHOP -> {
                                if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                            case WQUEEN, BQUEEN -> {
                                if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
                // -----
                // left for black rook and right for white rook
                for (int index = c.componentNumber - 1; index >= findAndGetRow(c.componentNumber)[0]; index--) {
                    if ((c.piece == BQUEEN ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    } else if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                            (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                        switch (pieceThatIsCheckingKing) {
                            case WROOK, BROOK -> {
                                if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                            case BBISHOP, WBISHOP -> {
                                if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                            case WQUEEN, BQUEEN -> {
                                if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
                // down for black rook and up for white rook
                for (int index = c.componentNumber - 8; index > -1; index -= 8) {
                    if ((c.piece == BQUEEN ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    } else if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                            (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                        switch (pieceThatIsCheckingKing) {
                            case WROOK, BROOK -> {
                                if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                            case BBISHOP, WBISHOP -> {
                                if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                            case WQUEEN, BQUEEN -> {
                                if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index, componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                        chessTiles.get(index).possibleToBeCaptured = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    if (isBlackTurn) {
                                        canBeForcedToCaptureAnotherPiece[0] = true;
                                    } else {
                                        canBeForcedToCaptureAnotherPiece[1] = true;
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }
            {
                // +++++
                // up for black rook and down for white QUEEN
                for (int index = componentNumber + 8; index < TOTAL_COMPONENT_NUMBER; index += 8) {
                    if ((c.piece == BQUEEN ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    } else if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                            (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                        if (chessTiles.get(index).piece == pieceThatIsCheckingKing) {
                            if (visible) {
                                chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                chessTiles.get(index).possibleToBeCaptured = true;
                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                            }
                            if (c.isBlack()) {
                                canCapturePieceThatIsCheckingTheKing[0] = true;
                            } else {
                                canCapturePieceThatIsCheckingTheKing[1] = true;
                            }
                        }
                        break;
                    }
                }
                // right for black QUEEN and left for white QUEEN
                // +1, +2, +3, until componentNumber+n==boundaryComponentOfTheRight
                for (int index = c.componentNumber + 1; index <= findAndGetRow(c.componentNumber)[1]; index++) {
                    if ((c.piece == BQUEEN ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    } else if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                            (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                        if (chessTiles.get(index).piece == pieceThatIsCheckingKing) {
                            if (visible) {
                                chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                chessTiles.get(index).possibleToBeCaptured = true;
                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                            }
                            if (c.isBlack()) {
                                canCapturePieceThatIsCheckingTheKing[0] = true;
                            } else {
                                canCapturePieceThatIsCheckingTheKing[1] = true;
                            }
                        }
                        break;
                    }
                }
                // -----
                // left for black QUEEN and right for white QUEEN
                for (int index = c.componentNumber - 1; index >= findAndGetRow(c.componentNumber)[0]; index--) {
                    if ((c.piece == BQUEEN ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    } else if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                            (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                        if (chessTiles.get(index).piece == pieceThatIsCheckingKing) {
                            if (visible) {
                                chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                chessTiles.get(index).possibleToBeCaptured = true;
                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                            }
                            if (c.isBlack()) {
                                canCapturePieceThatIsCheckingTheKing[0] = true;
                            } else {
                                canCapturePieceThatIsCheckingTheKing[1] = true;
                            }
                        }
                        break;
                    }
                }
                // down for black QUEEN and up for white QUEEN
                for (int index = c.componentNumber - 8; index > -1; index -= 8) {
                    if ((c.piece == BQUEEN ?
                            chessTiles.get(index).isBlack() : chessTiles.get(index).isWhite())) {
                        break;
                    } else if ((c.piece == BQUEEN && chessTiles.get(index).isWhite()) ||
                            (c.piece == WQUEEN && chessTiles.get(index).isBlack())) {
                        if (chessTiles.get(index).piece == pieceThatIsCheckingKing) {
                            if (visible) {
                                chessTiles.get(index).setBackground(theme.get(Palette.possibleKillTileColor));
                                chessTiles.get(index).possibleToBeCaptured = true;
                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                            }
                            if (c.isBlack()) {
                                canCapturePieceThatIsCheckingTheKing[0] = true;
                            } else {
                                canCapturePieceThatIsCheckingTheKing[1] = true;
                            }
                        }
                        break;
                }
            }
        }
    }

}
    private int[] showKingKillables(ChessTile c, boolean visible) {
        int[] possibleKills={
                (c.componentNumber+1< TOTAL_COMPONENT_NUMBER ?c.componentNumber+1:-1), // SAME ROW
                (Math.max(c.componentNumber - 1, -1)), // SAME ROW
                (c.componentNumber+8< TOTAL_COMPONENT_NUMBER ?c.componentNumber+8:-1), // ABOVE ROW FOR BLACK, BELOW ROW FOR WHITE
                (Math.max(c.componentNumber - 8, -1)),  // BELOW ROW FOR BLACK, ABOVE ROW FOR BLACK
                (c.componentNumber+9< TOTAL_COMPONENT_NUMBER ?c.componentNumber+9:-1), // ABOVE ROW FOR BLACK, BELOW ROW FOR WHITE
                (Math.max(c.componentNumber - 9, -1)), // BELOW ROW FOR BLACK, ABOVE ROW FOR BLACK
                (c.componentNumber+7< TOTAL_COMPONENT_NUMBER ?c.componentNumber+7:-1), // ABOVE ROW FOR BLACK, BELOW ROW FOR WHITE
                (Math.max(c.componentNumber - 7, -1)) // BELOW ROW FOR BLACK, ABOVE ROW FOR BLACK
        };
        if (findAndGetRow(possibleKills[0])[0]!= findAndGetRow(c.componentNumber)[0]) {
            possibleKills[0]=-1;
        }
        if (findAndGetRow(possibleKills[1])[0]!= findAndGetRow(c.componentNumber)[0]){
            possibleKills[1]=-1;
        }
        if (findAndGetRow(possibleKills[4])[0]!= findAndGetRow(c.componentNumber+8)[0]) {
            possibleKills[4]=-1;
        }
        if (findAndGetRow(possibleKills[5])[0]!= findAndGetRow(c.componentNumber-8)[0]){
            possibleKills[5]=-1;
        }
        if (findAndGetRow(possibleKills[6])[0]!= findAndGetRow(c.componentNumber+8)[0]) {
            possibleKills[6]=-1;
        }
        if (findAndGetRow(possibleKills[7])[0]!= findAndGetRow(c.componentNumber-8)[0]){
            possibleKills[7]=-1;
        }

        for (int index = 0; index < possibleKills.length; index++) {
            {
                int[] array = {possibleKills[index], -1};
                if (c.isWhite()) {
                    checkAndEliminateIfCapturableByBlackPawnIfWhiteKingWasInThatChessTile(array, 0);
                    if (array[0] == -1) {
                        possibleKills[index] = -1;
                    }
                } else if (c.isBlack()) {
                    checkAndEliminateIfCapturableByWhitePawnIfBlackKingWasInThatChessTile(array, 0);
                    if (array[0] == -1) {
                        possibleKills[index] = -1;
                    }
                }
            }
            {
                int[] array = {possibleKills[index], -1};
                checkAndEliminateIfCapturableByRookIfKingWasInThatChessTile(array, 0, c.pieceColor);
                if (array[0] == -1) {
                    possibleKills[index] = -1;
                }
            }

            {
                int[] array={possibleKills[index], -1};
                checkAndEliminateIfCapturableByBishopIfKingWasInThatChessTile(array, 0, c.pieceColor);
                if (array[0]==-1) {
                    possibleKills[index]=-1;
                }
            }
            {
                int[] array={possibleKills[index], -1};
                checkAndEliminateIfCapturableByKnightIfKingWasInThatChessTile(array, 0, c.pieceColor);
                if (array[0]==-1) {
                    possibleKills[index]=-1;
                }
            }
            {
                int[] array={possibleKills[index], -1};
                checkAndEliminateIfCapturableByQueenIfKingWasInThatChessTile(array, 0, c.pieceColor);
                if (array[0]==-1) {
                    possibleKills[index]=-1;
                }
            }
            {
                int[] array={possibleKills[index], -1};
                checkAndEliminateIfCapturableByKingIfKingWasInThatChessTile(array, 0, c.pieceColor);
                if (array[0]==-1) {
                    possibleKills[index]=-1;
                }
            }

        }

        for (int index = 0; index < possibleKills.length; index++) {
            int validDestination = possibleKills[index];
            if (validDestination != -1) {
                if ((c.piece == WKING ? chessTiles.get(validDestination).isBlack() : chessTiles.get(validDestination).isWhite())) {
                    if (visible) {
                        chessTiles.get(validDestination).setBackground(theme.get(Palette.possibleKillTileColor));
                        chessTiles.get(validDestination).possibleToBeCaptured = true;
                        chessTiles.get(validDestination).toBeSwappedComponentNumber = c.componentNumber;
                    }
                } else {
                    possibleKills[index]=-1;
                }
            }
        }
        return possibleKills;
    }


    private void showPossibleMoves(boolean visible) {
        switch (this.piece) {
            case BPAWN -> showBlackPawnMoves(this, visible); // TODO en passant
            case WPAWN -> showWhitePawnMoves(this, visible); // TODO en passant

            case BROOK, WROOK -> showRookMoves(this, visible); // DONE

            case BKNIGHT, WKNIGHT -> showKnightsMoves(this, visible); // DONE

            case BBISHOP, WBISHOP -> showBishopsMoves(this, visible); // DONE

            case BKING, WKING -> showKingMoves(this, true); // DONE

            case BQUEEN, WQUEEN -> showQueenMoves(this, visible); // DONE
        }
    }


    private int[] findAndGetRow(int componentNumber) {
        if (componentNumber>=0 && componentNumber<8) {
            return new int[]{0, 7};
        } else if (componentNumber>=8 && componentNumber<16) {
            return new int[]{8, 15};
        } else if (componentNumber>=16 && componentNumber<24) {
            return new int[]{16, 23};
        } else if (componentNumber>=24 && componentNumber<32) {
            return new int[]{24, 31};
        } else if (componentNumber>=32 && componentNumber<40) {
            return new int[]{32, 39};
        } else if (componentNumber>=40 && componentNumber<48) {
            return new int[]{40, 47};
        } else if (componentNumber>=48 && componentNumber<56) {
            return new int[]{48, 55};
        } else if (componentNumber>=56 && componentNumber<64) {
            return new int[]{56, 63};
        }
        return new int[]{-1, -1};
    }


    public static void nextTurn() {
        isBlackTurn=!isBlackTurn;
    }
    private boolean isBlack() {
        return this.pieceColor == PieceColor.black;
    }
    private boolean isWhite() {
        return this.pieceColor == PieceColor.white;
    }


    private boolean castling(boolean visible) {
        if (!locked){
            if (this.componentNumber + 2 < 64 && this.componentNumber - 1 > -1 &&
                    chessTiles.get(this.componentNumber + 2).piece == WROOK &&
                    chessTiles.get(this.componentNumber + 2).moveCounter == 0 &&
                    chessTiles.get(this.componentNumber - 1).piece == WKING &&
                    chessTiles.get(this.componentNumber - 1).moveCounter == 0) /* for white rook to the right of the screen*/ {
                if (visible) {
                    TaliBoardForMoves.collectInfoFromMovesMade(WROOK, this.componentNumber - 1,
                            this.componentNumber + 2 );
                    TaliBoardForMoves.collectInfoFromMovesMade(WKING, this.componentNumber,
                            this.componentNumber - 1);
                    Main.taliBoardForMoves.updateTaliBoardMoves("M");
                    chessTiles.get(this.componentNumber - 1).setPiece(WROOK);
                    this.setPiece(WKING);
                    this.moveCounter = 1;
                    whiteKingPosition = this.componentNumber;
                    chessTiles.get(this.componentNumber - 1).moveCounter = 1;
                    chessTiles.get(this.componentNumber + 2).setPiece(nopiece);
                }
                return true;
            } else if (this.componentNumber + 2 < 64 && this.componentNumber - 1 > -1 &&
                    chessTiles.get(this.componentNumber + 2).piece == BROOK &&
                    chessTiles.get(this.componentNumber + 2).moveCounter == 0 &&
                    chessTiles.get(this.componentNumber - 1).piece == BKING &&
                    chessTiles.get(this.componentNumber - 1).moveCounter == 0) /* for black rook to the right of the screen*/ {
                if (visible) {
                    TaliBoardForMoves.collectInfoFromMovesMade(BROOK, this.componentNumber - 1,
                            this.componentNumber + 2 );
                    TaliBoardForMoves.collectInfoFromMovesMade(BKING, this.componentNumber,
                            this.componentNumber - 1);
                    Main.taliBoardForMoves.updateTaliBoardMoves("M");
                    chessTiles.get(this.componentNumber - 1).setPiece(BROOK);
                    this.setPiece(BKING);
                    this.moveCounter = 1;
                    blackKingPosition = this.componentNumber;
                    chessTiles.get(this.componentNumber - 1).moveCounter = 1;
                    chessTiles.get(this.componentNumber + 2).setPiece(nopiece);
                }
                return true;
            }
        }
        return false;
    }
    private void showBlackPawnMoves(ChessTile c, boolean visible) {
        // finds out the componentNumbers of the valid tiles that the piece can move to
        // colors in the tiles with THEMEn[3]
        if (!c.locked){
            if (c.componentNumber + 8 < TOTAL_COMPONENT_NUMBER) {
                if (!chessTiles.get(c.componentNumber + 8).occupied) {
                    if (visible) {
                        chessTiles.get(c.componentNumber + 8).setBackground(theme.get(Palette.possibleMoveTileColor));
                        chessTiles.get(c.componentNumber + 8).possibleDestination = true;
                        chessTiles.get(c.componentNumber + 8).toBeSwappedComponentNumber = c.componentNumber;
                    }  // moving by 1

                    if (c.componentNumber + 16 < 32 && c.componentNumber + 16 > 23
                            && !chessTiles.get(c.componentNumber + 16).occupied) {
                        if (visible) {
                            chessTiles.get(c.componentNumber + 16).setBackground(theme.get(Palette.possibleMoveTileColor));
                            chessTiles.get(c.componentNumber + 16).possibleDestination = true;
                            chessTiles.get(c.componentNumber + 16).toBeSwappedComponentNumber = c.componentNumber;
                        }
                    } // moving by 2
                }
            }
        } else {
            // componentNumberOfPieceThatIsCheckingKing==c.componentNumber+8
            switch (pieceThatIsCheckingKing) {
                case WROOK -> {
                    if (c.componentNumber + 8 < TOTAL_COMPONENT_NUMBER) {
                        if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(c.componentNumber + 8,
                                componentNumberOfPieceThatIsCheckingKing)) {
                            if (visible) {
                                chessTiles.get(c.componentNumber + 8).setBackground(theme.get(Palette.possibleMoveTileColor));
                                chessTiles.get(c.componentNumber + 8).possibleDestination = true;
                                chessTiles.get(c.componentNumber + 8).toBeSwappedComponentNumber = c.componentNumber;
                            }
                            canBeForcedToCaptureAnotherPiece[0]=true;
                            // moving by 1
                        } else if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(c.componentNumber + 16,
                                componentNumberOfPieceThatIsCheckingKing)) {
                            if (c.componentNumber + 16 < 32 && c.componentNumber + 16 > 23
                                    && !chessTiles.get(c.componentNumber + 16).occupied) {
                                if (visible) {
                                    chessTiles.get(c.componentNumber + 16).setBackground(theme.get(Palette.possibleMoveTileColor));
                                    chessTiles.get(c.componentNumber + 16).possibleDestination = true;
                                    chessTiles.get(c.componentNumber + 16).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                canBeForcedToCaptureAnotherPiece[0]=true;
                            } // moving by 2
                        }
                    }
                } // DONE
                case WBISHOP -> {
                    if (c.componentNumber + 8 < TOTAL_COMPONENT_NUMBER) {
                        if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(c.componentNumber + 8, componentNumberOfPieceThatIsCheckingKing)) {
                            if (visible) {
                                chessTiles.get(c.componentNumber + 8).setBackground(theme.get(Palette.possibleMoveTileColor));
                                chessTiles.get(c.componentNumber + 8).possibleDestination = true;
                                chessTiles.get(c.componentNumber + 8).toBeSwappedComponentNumber = c.componentNumber;
                            }
                            canBeForcedToCaptureAnotherPiece[0]=true;
                            // moving by 1
                        } else if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(c.componentNumber + 16, componentNumberOfPieceThatIsCheckingKing)) {
                            if (c.componentNumber + 16 < 32 && c.componentNumber + 16 > 23
                                    && !chessTiles.get(c.componentNumber + 16).occupied) {
                                if (visible) {
                                    chessTiles.get(c.componentNumber + 16).setBackground(theme.get(Palette.possibleMoveTileColor));
                                    chessTiles.get(c.componentNumber + 16).possibleDestination = true;
                                    chessTiles.get(c.componentNumber + 16).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                canBeForcedToCaptureAnotherPiece[0]=true;
                            } // moving by 2
                        }
                    }
                }
                case WQUEEN -> {
                    if (c.componentNumber + 8 < TOTAL_COMPONENT_NUMBER) {
                        if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(c.componentNumber + 8, componentNumberOfPieceThatIsCheckingKing)) {
                            if (visible) {
                                chessTiles.get(c.componentNumber + 8).setBackground(theme.get(Palette.possibleMoveTileColor));
                                chessTiles.get(c.componentNumber + 8).possibleDestination = true;
                                chessTiles.get(c.componentNumber + 8).toBeSwappedComponentNumber = c.componentNumber;
                            }
                            canBeForcedToCaptureAnotherPiece[0]=true;  // moving by 1
                        } else if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(c.componentNumber + 16,
                                componentNumberOfPieceThatIsCheckingKing)) {
                            if (c.componentNumber + 16 < 32 && c.componentNumber + 16 > 23
                                    && !chessTiles.get(c.componentNumber + 16).occupied) {
                                if (visible) {
                                    chessTiles.get(c.componentNumber + 16).setBackground(theme.get(Palette.possibleMoveTileColor));
                                    chessTiles.get(c.componentNumber + 16).possibleDestination = true;
                                    chessTiles.get(c.componentNumber + 16).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                canBeForcedToCaptureAnotherPiece[0]=true;
                            } // moving by 2
                        }
                    }
                }
            }
        }
    } // TODO en passant
    private void showWhitePawnMoves(ChessTile c, boolean visible) {
        // finds out the componentNumbers of the valid tiles that the piece can move to
        // colors in the tiles with THEMEn[3]
        if (!c.locked) {
            if (!chessTiles.get(c.componentNumber - 8).occupied) {
                if (visible) {
                    chessTiles.get(c.componentNumber - 8).setBackground(theme.get(Palette.possibleMoveTileColor));
                    chessTiles.get(c.componentNumber - 8).possibleDestination = true;
                    chessTiles.get(c.componentNumber - 8).toBeSwappedComponentNumber = c.componentNumber;
                }  // moving by 1

                if (c.componentNumber - 16 > 31 && c.componentNumber - 16 < 40
                        && !chessTiles.get(c.componentNumber - 16).occupied) {
                    if (visible) {
                        chessTiles.get(c.componentNumber - 16).setBackground(theme.get(Palette.possibleMoveTileColor));
                        chessTiles.get(c.componentNumber - 16).possibleDestination = true;
                        chessTiles.get(c.componentNumber - 16).toBeSwappedComponentNumber = c.componentNumber;
                    }
                } // moving by 2
            }
        } else {
            // componentNumberOfPieceThatIsCheckingKing==c.componentNumber+8
            switch (pieceThatIsCheckingKing) {
                case BROOK -> {
                    if (c.componentNumber - 8 > -1) {
                        if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(c.componentNumber - 8,
                                componentNumberOfPieceThatIsCheckingKing)) {
                            if (visible) {
                                chessTiles.get(c.componentNumber - 8).setBackground(theme.get(Palette.possibleMoveTileColor));
                                chessTiles.get(c.componentNumber - 8).possibleDestination = true;
                                chessTiles.get(c.componentNumber - 8).toBeSwappedComponentNumber = c.componentNumber;
                            }
                            canBeForcedToCaptureAnotherPiece[1]=true;// moving by 1
                        } else if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(c.componentNumber - 16,
                                componentNumberOfPieceThatIsCheckingKing)) {
                            if (c.componentNumber - 16 < 32 && c.componentNumber - 16 > 23
                                    && !chessTiles.get(c.componentNumber - 16).occupied) {
                                if (visible) {
                                    chessTiles.get(c.componentNumber - 16).setBackground(theme.get(Palette.possibleMoveTileColor));
                                    chessTiles.get(c.componentNumber - 16).possibleDestination = true;
                                    chessTiles.get(c.componentNumber - 16).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                canBeForcedToCaptureAnotherPiece[1]=true;
                            } // moving by 2
                        }
                    }
                } // DONE
                case BBISHOP -> {
                    if (c.componentNumber - 8 < TOTAL_COMPONENT_NUMBER) {
                        if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(c.componentNumber - 8, componentNumberOfPieceThatIsCheckingKing)) {
                            if (visible) {
                                chessTiles.get(c.componentNumber - 8).setBackground(theme.get(Palette.possibleMoveTileColor));
                                chessTiles.get(c.componentNumber - 8).possibleDestination = true;
                                chessTiles.get(c.componentNumber - 8).toBeSwappedComponentNumber = c.componentNumber;
                            }
                            canBeForcedToCaptureAnotherPiece[1]=true;  // moving by 1
                        } else if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(c.componentNumber - 16, componentNumberOfPieceThatIsCheckingKing)) {
                            if (c.componentNumber - 16 < 32 && c.componentNumber - 16 > 23
                                    && !chessTiles.get(c.componentNumber - 16).occupied) {
                                if (visible) {
                                    chessTiles.get(c.componentNumber - 16).setBackground(theme.get(Palette.possibleMoveTileColor));
                                    chessTiles.get(c.componentNumber - 16).possibleDestination = true;
                                    chessTiles.get(c.componentNumber - 16).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                canBeForcedToCaptureAnotherPiece[1]=true;
                            } // moving by 2
                        }
                    }
                }
                case BQUEEN -> {
                    if (c.componentNumber - 8 < TOTAL_COMPONENT_NUMBER) {
                        if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(c.componentNumber - 8, componentNumberOfPieceThatIsCheckingKing)) {
                            if (visible) {
                                chessTiles.get(c.componentNumber - 8).setBackground(theme.get(Palette.possibleMoveTileColor));
                                chessTiles.get(c.componentNumber - 8).possibleDestination = true;
                                chessTiles.get(c.componentNumber - 8).toBeSwappedComponentNumber = c.componentNumber;
                            }
                            canBeForcedToCaptureAnotherPiece[1]=true;
                            // moving by 1
                        } else if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(c.componentNumber - 16, componentNumberOfPieceThatIsCheckingKing)) {
                            if (c.componentNumber - 16 < 32 && c.componentNumber - 16 > 23
                                    && !chessTiles.get(c.componentNumber - 16).occupied) {
                                if (visible) {
                                    chessTiles.get(c.componentNumber - 16).setBackground(theme.get(Palette.possibleMoveTileColor));
                                    chessTiles.get(c.componentNumber - 16).possibleDestination = true;
                                    chessTiles.get(c.componentNumber - 16).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                canBeForcedToCaptureAnotherPiece[1]=true;
                            } // moving by 2
                        }
                    }
                }
            }
        }
    } // TODO en passant
    private void showRookMoves(ChessTile c, boolean visible) {
        // +++++
        // up for black rook and down for white rook
        if (!c.locked) {
            for (int index = c.componentNumber + 8; index < TOTAL_COMPONENT_NUMBER; index += 8) {
                if (chessTiles.get(index).occupied) {
                    break;
                } else {
                    chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                    chessTiles.get(index).possibleDestination = true;
                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                }
            }
            // right for black rook and left for white rook
            // +1, +2, +3, until componentNumber+n==boundaryComponentOfTheRight
            for (int index = c.componentNumber + 1; index <= findAndGetRow(c.componentNumber)[1]; index++) {
                if (chessTiles.get(index).occupied) {
                    break;
                } else {
                    chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                    chessTiles.get(index).possibleDestination = true;
                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                }
            }
            // -----
            // left for black rook and right for white rook
            for (int index = c.componentNumber - 1; index >= findAndGetRow(c.componentNumber)[0]; index--) {
                if (chessTiles.get(index).occupied) {
                    break;
                } else {
                    chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                    chessTiles.get(index).possibleDestination = true;
                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                }
            }
            // down for black rook and up for white rook
            for (int index = c.componentNumber - 8; index > -1; index -= 8) {
                if (chessTiles.get(index).occupied) {
                    break;
                } else {
                    chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                    chessTiles.get(index).possibleDestination = true;
                    chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                }
            }
        } else {
            label: for (int index = c.componentNumber + 8; index < TOTAL_COMPONENT_NUMBER; index += 8) {
                if (chessTiles.get(index).occupied) {
                    break;
                } else {
                    if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()){
                        switch (pieceThatIsCheckingKing) {
                            case BROOK -> {
                                if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[1]=true;
                                    break label;
                                }
                            }
                            case BQUEEN -> {
                                if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[1]=true;
                                    break label;
                                }
                            }
                            case BBISHOP -> {
                                if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[1]=true;
                                    break label;
                                }
                            }
                        }
                    } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                        switch (pieceThatIsCheckingKing) {
                            case WROOK -> {
                                if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[0]=true;
                                    break label;
                                }
                            }
                            case WQUEEN -> {
                                if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[0]=true;
                                    break label;
                                }
                            }
                            case WBISHOP -> {
                                if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[0]=true;
                                    break label;
                                }
                            }
                        }
                    }
                }
            }
            // right for black rook and left for white rook
            // +1, +2, +3, until componentNumber+n==boundaryComponentOfTheRight
            label: for (int index = c.componentNumber + 1; index <= findAndGetRow(c.componentNumber)[1]; index++) {
                if (chessTiles.get(index).occupied) {
                    break;
                } else {
                    if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()){
                        switch (pieceThatIsCheckingKing) {
                            case BROOK -> {
                                if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[1]=true;
                                    break label;
                                }
                            }
                            case BQUEEN -> {
                                if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[1]=true;
                                    break label;
                                }
                            }
                            case BBISHOP -> {
                                if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[1]=true;
                                    break label;
                                }
                            }
                        }
                    } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                        switch (pieceThatIsCheckingKing) {
                            case WROOK -> {
                                if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[0]=true;
                                    break label;
                                }
                            }
                            case WQUEEN -> {
                                if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[0]=true;
                                    break label;
                                }
                            }
                            case WBISHOP -> {
                                if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[0]=true;
                                    break label;
                                }
                            }
                        }
                    }
                }
            }
            // -----
            // left for black rook and right for white rook
            label: for (int index = c.componentNumber - 1; index >= findAndGetRow(c.componentNumber)[0]; index--) {
                if (chessTiles.get(index).occupied) {
                    break;
                } else {
                    if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()){
                        switch (pieceThatIsCheckingKing) {
                            case BROOK -> {
                                if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[1]=true;
                                    break label;
                                }
                            }
                            case BQUEEN -> {
                                if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[1]=true;
                                    break label;
                                }
                            }
                            case BBISHOP -> {
                                if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[1]=true;
                                    break label;
                                }
                            }
                        }
                    } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                        switch (pieceThatIsCheckingKing) {
                            case WROOK -> {
                                if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[0]=true;
                                    break label;
                                }
                            }
                            case WQUEEN -> {
                                if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[0]=true;
                                    break label;
                                }
                            }
                            case WBISHOP -> {
                                if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[0]=true;
                                    break label;
                                }
                            }
                        }
                    }
                }
            }
            // down for black rook and up for white rook
            label: for (int index = c.componentNumber - 8; index > -1; index -= 8) {
                if (chessTiles.get(index).occupied) {
                    break;
                } else {
                    if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()){
                        switch (pieceThatIsCheckingKing) {
                            case BROOK -> {
                                if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[1]=true;
                                    break label;
                                }
                            }
                            case BQUEEN -> {
                                if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[1]=true;
                                    break label;
                                }
                            }
                            case BBISHOP -> {
                                if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[1]=true;
                                    break label;
                                }
                            }
                        }
                    } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                        switch (pieceThatIsCheckingKing) {
                            case WROOK -> {
                                if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[0]=true;
                                    break label;
                                }
                            }
                            case WQUEEN -> {
                                if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[0]=true;
                                    break label;
                                }
                            }
                            case WBISHOP -> {
                                if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                        componentNumberOfPieceThatIsCheckingKing)) {
                                    if (visible) {
                                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                        chessTiles.get(index).possibleDestination = true;
                                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                    }
                                    canBeForcedToCaptureAnotherPiece[0]=true;
                                    break label;
                                }
                            }
                        }
                    }
                }
            }
        }
    } // DONE //TODO improvements
    private void showBishopsMoves(ChessTile c, boolean visible) {
        if (!c.locked) {
            if (!(componentNumber == findAndGetRow(c.componentNumber)[1])) {
                for (int index = c.componentNumber + 9; index < TOTAL_COMPONENT_NUMBER; index += 9) {
                    if (chessTiles.get(index).occupied) {
                        break;
                    } else if (findAndGetRow(index)[1] == index) {
                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                        chessTiles.get(index).possibleDestination = true;
                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                        break;
                    } else {
                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                        chessTiles.get(index).possibleDestination = true;
                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                    }
                }

                for (int index = c.componentNumber - 7; index > -1; index -= 7) {
                    if (chessTiles.get(index).occupied) {
                        break;
                    } else if (findAndGetRow(index)[1] == index) {
                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                        chessTiles.get(index).possibleDestination = true;
                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                        break;
                    } else {
                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                        chessTiles.get(index).possibleDestination = true;
                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                    }
                }
            }
            if (!(componentNumber == findAndGetRow(c.componentNumber)[0])) {
                for (int index = c.componentNumber - 9; index > -1; index -= 9) {
                    if (chessTiles.get(index).occupied) {
                        break;
                    } else if (findAndGetRow(index)[0] == index) {
                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                        chessTiles.get(index).possibleDestination = true;
                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                        break;
                    } else {
                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                        chessTiles.get(index).possibleDestination = true;
                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                    }
                }
                for (int index = c.componentNumber + 7; index < TOTAL_COMPONENT_NUMBER; index += 7) {
                    if (chessTiles.get(index).occupied) {
                        break;
                    } else if (findAndGetRow(index)[0] == index) {
                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                        chessTiles.get(index).possibleDestination = true;
                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                        break;
                    } else {
                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                        chessTiles.get(index).possibleDestination = true;
                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                    }
                }
            }
        } else {
            if (!(componentNumber == findAndGetRow(c.componentNumber)[1])) {
                label: for (int index = c.componentNumber + 9; index < TOTAL_COMPONENT_NUMBER; index += 9) {
                    if (chessTiles.get(index).occupied) {
                        break;
                    } else if (findAndGetRow(index)[1] == index) {
                        if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()){
                            switch (pieceThatIsCheckingKing) {
                                case BROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                            }
                        } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                            switch (pieceThatIsCheckingKing) {
                                case WROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                            }
                        }
                    } else {
                        if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()){
                            switch (pieceThatIsCheckingKing) {
                                case BROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                            }
                        } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                            switch (pieceThatIsCheckingKing) {
                                case WROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                            }
                        }
                    }
                }

                label: for (int index = c.componentNumber - 7; index > -1; index -= 7) {
                    if (chessTiles.get(index).occupied) {
                        break;
                    } else if (findAndGetRow(index)[1] == index) {
                        if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()){
                            switch (pieceThatIsCheckingKing) {
                                case BROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                            }
                        } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                            switch (pieceThatIsCheckingKing) {
                                case WROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                            }
                        }
                    } else {
                        if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()){
                            switch (pieceThatIsCheckingKing) {
                                case BROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                            }
                        } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                            switch (pieceThatIsCheckingKing) {
                                case WROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (!(componentNumber == findAndGetRow(c.componentNumber)[0])) {
                label: for (int index = c.componentNumber - 9; index > -1; index -= 9) {
                    if (chessTiles.get(index).occupied) {
                        break;
                    } else if (findAndGetRow(index)[0] == index) {
                        if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()){
                            switch (pieceThatIsCheckingKing) {
                                case BROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                            }
                        } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                            switch (pieceThatIsCheckingKing) {
                                case WROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                            }
                        }
                    } else {
                        if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()){
                            switch (pieceThatIsCheckingKing) {
                                case BROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                            }
                        } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                            switch (pieceThatIsCheckingKing) {
                                case WROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                            }
                        }
                    }
                }
                label: for (int index = c.componentNumber + 7; index < TOTAL_COMPONENT_NUMBER; index += 7) {
                    if (chessTiles.get(index).occupied) {
                        break;
                    } else if (findAndGetRow(index)[0] == index) {
                        if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()){
                            switch (pieceThatIsCheckingKing) {
                                case BROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                            }
                        } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                            switch (pieceThatIsCheckingKing) {
                                case WROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                            }
                        }
                    } else {
                        if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()){
                            switch (pieceThatIsCheckingKing) {
                                case BROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                            }
                        } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                            switch (pieceThatIsCheckingKing) {
                                case WROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } // DONE //TODO improvements
    private void showKnightsMoves(ChessTile c, boolean visible) {
        if (!c.locked) {
            int[] arrayOfDestination = new int[8];
            arrayOfDestination[0] = (c.componentNumber + 2 * 8 + 1 < TOTAL_COMPONENT_NUMBER
                    && findAndGetRow(c.componentNumber + 2 * 8 + 1)[1] == findAndGetRow(c.componentNumber + 2 * 8)[1]
                    ? c.componentNumber + 2 * 8 + 1 : -1);
            arrayOfDestination[1] = (c.componentNumber + 2 * 8 - 1 < TOTAL_COMPONENT_NUMBER
                    && findAndGetRow(c.componentNumber + 2 * 8 - 1)[1] == findAndGetRow(c.componentNumber + 2 * 8)[1]
                    ? c.componentNumber + 2 * 8 - 1 : -1);
            arrayOfDestination[2] = (c.componentNumber - 2 * 8 + 1 > -1
                    && findAndGetRow(c.componentNumber - 2 * 8 + 1)[1] == findAndGetRow(c.componentNumber - 2 * 8)[1]
                    ? c.componentNumber - 2 * 8 + 1 : -1);
            arrayOfDestination[3] = (c.componentNumber - 2 * 8 - 1 > -1
                    && findAndGetRow(c.componentNumber - 2 * 8 - 1)[1] == findAndGetRow(c.componentNumber - 2 * 8)[1]
                    ? c.componentNumber - 2 * 8 - 1 : -1);
            arrayOfDestination[4] = (c.componentNumber + /* 1* */ 8 + 2 < TOTAL_COMPONENT_NUMBER
                    && findAndGetRow(c.componentNumber + 8 + 2)[1] == findAndGetRow(c.componentNumber + 8)[1]
                    ? c.componentNumber + /* 1* */ 8 + 2 : -1);
            arrayOfDestination[5] = (c.componentNumber + /* 1* */ 8 - 2 < TOTAL_COMPONENT_NUMBER
                    && findAndGetRow(c.componentNumber + 8 - 2)[1] == findAndGetRow(c.componentNumber + 8)[1]
                    ? c.componentNumber + /* 1* */ 8 - 2 : -1);
            arrayOfDestination[6] = (c.componentNumber - /* 1* */ 8 + 2 > -1
                    && findAndGetRow(c.componentNumber - 8 + 2)[1] == findAndGetRow(c.componentNumber - 8)[1]
                    ? c.componentNumber - /* 1* */ 8 + 2 : -1);
            arrayOfDestination[7] = (c.componentNumber - /* 1* */ 8 - 2 > -1
                    && findAndGetRow(c.componentNumber - 8 - 2)[1] == findAndGetRow(c.componentNumber - 8)[1]
                    ? c.componentNumber - /* 1* */ 8 - 2 : -1);

            // checking if array contains correct no of destinations per row (which should be 2)

            for (int destination : arrayOfDestination) {
                if (destination == -1 || chessTiles.get(destination).occupied) {
                    continue;
                }
                chessTiles.get(destination).setBackground(theme.get(Palette.possibleMoveTileColor));
                chessTiles.get(destination).possibleDestination = true;
                chessTiles.get(destination).toBeSwappedComponentNumber = c.componentNumber;
            }

        } else {
            int[] arrayOfDestination = new int[8];
            arrayOfDestination[0] = (c.componentNumber + 2 * 8 + 1 < TOTAL_COMPONENT_NUMBER
                    && findAndGetRow(c.componentNumber + 2 * 8 + 1)[1] == findAndGetRow(c.componentNumber + 2 * 8)[1]
                    ? c.componentNumber + 2 * 8 + 1 : -1);
            arrayOfDestination[1] = (c.componentNumber + 2 * 8 - 1 < TOTAL_COMPONENT_NUMBER
                    && findAndGetRow(c.componentNumber + 2 * 8 - 1)[1] == findAndGetRow(c.componentNumber + 2 * 8)[1]
                    ? c.componentNumber + 2 * 8 - 1 : -1);
            arrayOfDestination[2] = (c.componentNumber - 2 * 8 + 1 > -1
                    && findAndGetRow(c.componentNumber - 2 * 8 + 1)[1] == findAndGetRow(c.componentNumber - 2 * 8)[1]
                    ? c.componentNumber - 2 * 8 + 1 : -1);
            arrayOfDestination[3] = (c.componentNumber - 2 * 8 - 1 > -1
                    && findAndGetRow(c.componentNumber - 2 * 8 - 1)[1] == findAndGetRow(c.componentNumber - 2 * 8)[1]
                    ? c.componentNumber - 2 * 8 - 1 : -1);
            arrayOfDestination[4] = (c.componentNumber + /* 1* */ 8 + 2 < TOTAL_COMPONENT_NUMBER
                    && findAndGetRow(c.componentNumber + 8 + 2)[1] == findAndGetRow(c.componentNumber + 8)[1]
                    ? c.componentNumber + /* 1* */ 8 + 2 : -1);
            arrayOfDestination[5] = (c.componentNumber + /* 1* */ 8 - 2 < TOTAL_COMPONENT_NUMBER
                    && findAndGetRow(c.componentNumber + 8 - 2)[1] == findAndGetRow(c.componentNumber + 8)[1]
                    ? c.componentNumber + /* 1* */ 8 - 2 : -1);
            arrayOfDestination[6] = (c.componentNumber - /* 1* */ 8 + 2 > -1
                    && findAndGetRow(c.componentNumber - 8 + 2)[1] == findAndGetRow(c.componentNumber - 8)[1]
                    ? c.componentNumber - /* 1* */ 8 + 2 : -1);
            arrayOfDestination[7] = (c.componentNumber - /* 1* */ 8 - 2 > -1
                    && findAndGetRow(c.componentNumber - 8 - 2)[1] == findAndGetRow(c.componentNumber - 8)[1]
                    ? c.componentNumber - /* 1* */ 8 - 2 : -1);

            // checking if array contains correct no of destinations per row (which should be 2)

            label: for (int destination : arrayOfDestination) {
                if (destination == -1 || chessTiles.get(destination).occupied) {
                    continue;
                }
                if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()){
                    switch (pieceThatIsCheckingKing) {
                        case BROOK -> {
                            if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(destination,
                                    componentNumberOfPieceThatIsCheckingKing)) {
                                if (visible) {
                                    chessTiles.get(destination).setBackground(theme.get(Palette.possibleMoveTileColor));
                                    chessTiles.get(destination).possibleDestination = true;
                                    chessTiles.get(destination).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                canBeForcedToCaptureAnotherPiece[1]=true;
                            }
                        }
                        case BQUEEN -> {
                            if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(destination,
                                    componentNumberOfPieceThatIsCheckingKing)) {
                                if (visible) {
                                    chessTiles.get(destination).setBackground(theme.get(Palette.possibleMoveTileColor));
                                    chessTiles.get(destination).possibleDestination = true;
                                    chessTiles.get(destination).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                canBeForcedToCaptureAnotherPiece[1]=true;
                            }
                        }
                        case BBISHOP -> {
                            if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(destination,
                                    componentNumberOfPieceThatIsCheckingKing)) {
                                if (visible) {
                                    chessTiles.get(destination).setBackground(theme.get(Palette.possibleMoveTileColor));
                                    chessTiles.get(destination).possibleDestination = true;
                                    chessTiles.get(destination).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                canBeForcedToCaptureAnotherPiece[1]=true;
                            }
                        }
                    }
                } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                    switch (pieceThatIsCheckingKing) {
                        case WROOK -> {
                            if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(destination,
                                    componentNumberOfPieceThatIsCheckingKing)) {
                                if (visible) {
                                    chessTiles.get(destination).setBackground(theme.get(Palette.possibleMoveTileColor));
                                    chessTiles.get(destination).possibleDestination = true;
                                    chessTiles.get(destination).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                canBeForcedToCaptureAnotherPiece[0]=true;
                            }
                        }
                        case WQUEEN -> {
                            if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(destination,
                                    componentNumberOfPieceThatIsCheckingKing)) {
                                if (visible) {
                                    chessTiles.get(destination).setBackground(theme.get(Palette.possibleMoveTileColor));
                                    chessTiles.get(destination).possibleDestination = true;
                                    chessTiles.get(destination).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                canBeForcedToCaptureAnotherPiece[0]=true;
                            }
                        }
                        case WBISHOP -> {
                            if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(destination,
                                    componentNumberOfPieceThatIsCheckingKing)) {
                                if (visible) {
                                    chessTiles.get(destination).setBackground(theme.get(Palette.possibleMoveTileColor));
                                    chessTiles.get(destination).possibleDestination = true;
                                    chessTiles.get(destination).toBeSwappedComponentNumber = c.componentNumber;
                                }
                                canBeForcedToCaptureAnotherPiece[0]=true;
                            }
                        }
                    }
                }
            }
        }
    } // DONE //TODO improvements
    private void showQueenMoves(ChessTile c, boolean visible) {
        if (!c.locked) {
            {
                for (int index = c.componentNumber + 8; index < TOTAL_COMPONENT_NUMBER; index += 8) {
                    if (chessTiles.get(index).occupied) {
                        break;
                    } else {
                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                        chessTiles.get(index).possibleDestination = true;
                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                    }
                }
                // right for black rook and left for white rook
                // +1, +2, +3, until componentNumber+n==boundaryComponentOfTheRight
                for (int index = c.componentNumber + 1; index <= findAndGetRow(c.componentNumber)[1]; index++) {
                    if (chessTiles.get(index).occupied) {
                        break;
                    } else {
                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                        chessTiles.get(index).possibleDestination = true;
                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                    }
                }
                // -----
                // left for black rook and right for white rook
                for (int index = c.componentNumber - 1; index >= findAndGetRow(c.componentNumber)[0]; index--) {
                    if (chessTiles.get(index).occupied) {
                        break;
                    } else {
                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                        chessTiles.get(index).possibleDestination = true;
                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                    }
                }
                // down for black rook and up for white rook
                for (int index = c.componentNumber - 8; index > -1; index -= 8) {
                    if (chessTiles.get(index).occupied) {
                        break;
                    } else {
                        chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                        chessTiles.get(index).possibleDestination = true;
                        chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                    }
                }
            }
            {
                if (!(componentNumber == findAndGetRow(c.componentNumber)[1])) {
                    for (int index = c.componentNumber + 9; index < TOTAL_COMPONENT_NUMBER; index += 9) {
                        if (chessTiles.get(index).occupied) {
                            break;
                        } else if (findAndGetRow(index)[1] == index) {
                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                            chessTiles.get(index).possibleDestination = true;
                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                            break;
                        } else {
                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                            chessTiles.get(index).possibleDestination = true;
                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                        }
                    }

                    for (int index = c.componentNumber - 7; index > -1; index -= 7) {
                        if (chessTiles.get(index).occupied) {
                            break;
                        } else if (findAndGetRow(index)[1] == index) {
                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                            chessTiles.get(index).possibleDestination = true;
                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                            break;
                        } else {
                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                            chessTiles.get(index).possibleDestination = true;
                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                        }
                    }
                }
                if (!(componentNumber == findAndGetRow(c.componentNumber)[0])) {
                    for (int index = c.componentNumber - 9; index > -1; index -= 9) {
                        if (chessTiles.get(index).occupied) {
                            break;
                        } else if (findAndGetRow(index)[0] == index) {
                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                            chessTiles.get(index).possibleDestination = true;
                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                            break;
                        } else {
                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                            chessTiles.get(index).possibleDestination = true;
                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                        }
                    }
                    for (int index = c.componentNumber + 7; index < TOTAL_COMPONENT_NUMBER; index += 7) {
                        if (chessTiles.get(index).occupied) {
                            break;
                        } else if (findAndGetRow(index)[0] == index) {
                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                            chessTiles.get(index).possibleDestination = true;
                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                            break;
                        } else {
                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                            chessTiles.get(index).possibleDestination = true;
                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                        }
                    }
                }
            }
        } else {
            {
                int index;
                if (!(c.componentNumber == findAndGetRow(c.componentNumber)[1])) {
                    index = c.componentNumber + 9;
                    label:
                    for (int i = 0; i < 9; i++) {
                        if (index < TOTAL_COMPONENT_NUMBER) {
                            if (chessTiles.get(index).occupied) {
                                break;
                            } else if (findAndGetRow(index)[1] == index) {
                                if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()) {
                                    switch (pieceThatIsCheckingKing) {
                                        case BROOK -> {
                                            if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                        case BQUEEN -> {
                                            if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                        case BBISHOP -> {
                                            if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                    }
                                } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                                    switch (pieceThatIsCheckingKing) {
                                        case WROOK -> {
                                            if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                        case WQUEEN -> {
                                            if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                        case WBISHOP -> {
                                            if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()) {
                                    switch (pieceThatIsCheckingKing) {
                                        case BROOK -> {
                                            if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                        case BQUEEN -> {
                                            if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                        case BBISHOP -> {
                                            if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                    }
                                } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                                    switch (pieceThatIsCheckingKing) {
                                        case WROOK -> {
                                            if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                        case WQUEEN -> {
                                            if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                        case WBISHOP -> {
                                            if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                    }
                                }
                                index += 9;
                            }
                        }
                    }
                    index = c.componentNumber - 7;
                    label:
                    for (int i = 0; i < 9; i++) {
                        if (index > -1) {
                            if (chessTiles.get(index).occupied) {
                                break;
                            } else if (findAndGetRow(index)[1] == index) {
                                if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()) {
                                    switch (pieceThatIsCheckingKing) {
                                        case BROOK -> {
                                            if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                        case BQUEEN -> {
                                            if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                        case BBISHOP -> {
                                            if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                    }
                                } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                                    switch (pieceThatIsCheckingKing) {
                                        case WROOK -> {
                                            if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                        case WQUEEN -> {
                                            if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                        case WBISHOP -> {
                                            if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()) {
                                    switch (pieceThatIsCheckingKing) {
                                        case BROOK -> {
                                            if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                        case BQUEEN -> {
                                            if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                        case BBISHOP -> {
                                            if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                    }
                                } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                                    switch (pieceThatIsCheckingKing) {
                                        case WROOK -> {
                                            if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                        case WQUEEN -> {
                                            if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                        case WBISHOP -> {
                                            if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                    }
                                }
                                index -= 7;
                            }
                        }
                    }
                }
                if (!(c.componentNumber == findAndGetRow(c.componentNumber)[0])) {
                    index = c.componentNumber - 9;
                    label:
                    for (int i = 0; i < 9; i++) {
                        if (index > -1) {
                            if (chessTiles.get(index).occupied) {
                                break;
                            } else if (findAndGetRow(index)[0] == index) {
                                if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()) {
                                    switch (pieceThatIsCheckingKing) {
                                        case BROOK -> {
                                            if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                        case BQUEEN -> {
                                            if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                        case BBISHOP -> {
                                            if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                    }
                                } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                                    switch (pieceThatIsCheckingKing) {
                                        case WROOK -> {
                                            if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                        case WQUEEN -> {
                                            if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                        case WBISHOP -> {
                                            if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()) {
                                    switch (pieceThatIsCheckingKing) {
                                        case BROOK -> {
                                            if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                        case BQUEEN -> {
                                            if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                        case BBISHOP -> {
                                            if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                    }
                                } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                                    switch (pieceThatIsCheckingKing) {
                                        case WROOK -> {
                                            if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                        case WQUEEN -> {
                                            if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                        case WBISHOP -> {
                                            if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                    }
                                }
                                index -= 9;
                            }
                        }
                    }
                    index = c.componentNumber + 7;
                    label:
                    for (int i = 0; i < 9; i++) {
                        if (index < TOTAL_COMPONENT_NUMBER) {
                            if (chessTiles.get(index).occupied) {
                                break;
                            } else if (findAndGetRow(index)[0] == index) {
                                if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()) {
                                    switch (pieceThatIsCheckingKing) {
                                        case BROOK -> {
                                            if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                        case BQUEEN -> {
                                            if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                        case BBISHOP -> {
                                            if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                    }
                                } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                                    switch (pieceThatIsCheckingKing) {
                                        case WROOK -> {
                                            if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                        case WQUEEN -> {
                                            if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                        case WBISHOP -> {
                                            if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()) {
                                    switch (pieceThatIsCheckingKing) {
                                        case BROOK -> {
                                            if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                        case BQUEEN -> {
                                            if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                        case BBISHOP -> {
                                            if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[1]=true;
                                                break label;
                                            }
                                        }
                                    }
                                } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                                    switch (pieceThatIsCheckingKing) {
                                        case WROOK -> {
                                            if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                        case WQUEEN -> {
                                            if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                        case WBISHOP -> {
                                            if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                                    componentNumberOfPieceThatIsCheckingKing)) {
                                                chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                                chessTiles.get(index).possibleDestination = true;
                                                chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                                canBeForcedToCaptureAnotherPiece[0]=true;
                                                break label;
                                            }
                                        }
                                    }
                                }
                                index += 7;
                            }
                        }
                    }
                }
            }
            {
                label: for (int index = c.componentNumber + 8; index < TOTAL_COMPONENT_NUMBER; index += 8) {
                    if (chessTiles.get(index).occupied) {
                        break;
                    } else {
                        if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()){
                            switch (pieceThatIsCheckingKing) {
                                case BROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                            }
                        } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                            switch (pieceThatIsCheckingKing) {
                                case WROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                            }
                        }
                    }
                }
                // right for black rook and left for white rook
                // +1, +2, +3, until componentNumber+n==boundaryComponentOfTheRight
                label: for (int index = c.componentNumber + 1; index <= findAndGetRow(c.componentNumber)[1]; index++) {
                    if (chessTiles.get(index).occupied) {
                        break;
                    } else {
                        if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()){
                            switch (pieceThatIsCheckingKing) {
                                case BROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                            }
                        } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                            switch (pieceThatIsCheckingKing) {
                                case WROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                            }
                        }
                    }
                }
                // -----
                // left for black rook and right for white rook
                label: for (int index = c.componentNumber - 1; index >= findAndGetRow(c.componentNumber)[0]; index--) {
                    if (chessTiles.get(index).occupied) {
                        break;
                    } else {
                        if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()){
                            switch (pieceThatIsCheckingKing) {
                                case BROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                            }
                        } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                            switch (pieceThatIsCheckingKing) {
                                case WROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                            }
                        }
                    }
                }
                // down for black rook and up for white rook
                label: for (int index = c.componentNumber - 8; index > -1; index -= 8) {
                    if (chessTiles.get(index).occupied) {
                        break;
                    } else {
                        if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()){
                            switch (pieceThatIsCheckingKing) {
                                case BROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                                case BBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[1]=true;
                                        break label;
                                    }
                                }
                            }
                        } else if (chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite()) {
                            switch (pieceThatIsCheckingKing) {
                                case WROOK -> {
                                    if (canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WQUEEN -> {
                                    if (canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                                case WBISHOP -> {
                                    if (canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(index,
                                            componentNumberOfPieceThatIsCheckingKing)) {
                                        if (visible) {
                                            chessTiles.get(index).setBackground(theme.get(Palette.possibleMoveTileColor));
                                            chessTiles.get(index).possibleDestination = true;
                                            chessTiles.get(index).toBeSwappedComponentNumber = c.componentNumber;
                                        }
                                        canBeForcedToCaptureAnotherPiece[0]=true;
                                        break label;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } // DONE //TODO improvements
    private int[] showKingMoves(ChessTile cKing, boolean visible) {
        int[] possibleDestinations={
                (cKing.componentNumber+1< TOTAL_COMPONENT_NUMBER ?cKing.componentNumber+1:-1),
                (Math.max(cKing.componentNumber - 1, -1)),
                (cKing.componentNumber+8< TOTAL_COMPONENT_NUMBER ?cKing.componentNumber+8:-1),
                (Math.max(cKing.componentNumber - 8, -1)),
                (cKing.componentNumber+9< TOTAL_COMPONENT_NUMBER ?cKing.componentNumber+9:-1),
                (Math.max(cKing.componentNumber - 9, -1)),
                (cKing.componentNumber+7< TOTAL_COMPONENT_NUMBER ?cKing.componentNumber+7:-1),
                (Math.max(cKing.componentNumber - 7, -1))
        };


        if (findAndGetRow(possibleDestinations[0])[0]!= findAndGetRow(cKing.componentNumber)[0]) {
            possibleDestinations[0]=-1;
        }
        if (findAndGetRow(possibleDestinations[1])[0]!= findAndGetRow(cKing.componentNumber)[0]){
            possibleDestinations[1]=-1;
        }
        if (findAndGetRow(possibleDestinations[4])[0]!= findAndGetRow(cKing.componentNumber+8)[0]) {
            possibleDestinations[4]=-1;
        }
        if (findAndGetRow(possibleDestinations[5])[0]!= findAndGetRow(cKing.componentNumber-8)[0]){
            possibleDestinations[5]=-1;
        }
        if (findAndGetRow(possibleDestinations[6])[0]!= findAndGetRow(cKing.componentNumber+8)[0]) {
            possibleDestinations[6]=-1;
        }
        if (findAndGetRow(possibleDestinations[7])[0]!= findAndGetRow(cKing.componentNumber-8)[0]){
            possibleDestinations[7]=-1;
        }


        // checking if any one of possible destination will make the king capturable
        for (int index = 0; index < possibleDestinations.length; index++) {
            {
                int[] array = {possibleDestinations[index], -1};
                if (cKing.isWhite()) {
                    checkAndEliminateIfCapturableByBlackPawnIfWhiteKingWasInThatChessTile(array, 0);
                    if (array[0] == -1) {
                        possibleDestinations[index] = -1;
                    }
                } else if (cKing.isBlack()) {
                    checkAndEliminateIfCapturableByWhitePawnIfBlackKingWasInThatChessTile(array, 0);
                    if (array[0] == -1) {
                        possibleDestinations[index] = -1;
                    }
                }
            }
            {
                int[] array = {possibleDestinations[index], -1};
                checkAndEliminateIfCapturableByRookIfKingWasInThatChessTile(array, 0, cKing.pieceColor);
                if (array[0] == -1) {
                    possibleDestinations[index] = -1;
                }
            }

            {
                int[] array={possibleDestinations[index], -1};
                checkAndEliminateIfCapturableByBishopIfKingWasInThatChessTile(array, 0, cKing.pieceColor);
                if (array[0]==-1) {
                    possibleDestinations[index]=-1;
                }
            }
            {
                int[] array={possibleDestinations[index], -1};
                checkAndEliminateIfCapturableByKnightIfKingWasInThatChessTile(array, 0, cKing.pieceColor);
                if (array[0]==-1) {
                    possibleDestinations[index]=-1;
                }
            }
            {
                int[] array={possibleDestinations[index], -1};
                checkAndEliminateIfCapturableByQueenIfKingWasInThatChessTile(array, 0, cKing.pieceColor);
                if (array[0]==-1) {
                    possibleDestinations[index]=-1;
                }
            }
            {
                int[] array={possibleDestinations[index], -1};
                checkAndEliminateIfCapturableByKingIfKingWasInThatChessTile(array, 0, cKing.pieceColor);
                if (array[0]==-1) {
                    possibleDestinations[index]=-1;
                }
            }

        }


        for (int i = 0; i < possibleDestinations.length; i++) {
            int validDestination = possibleDestinations[i];
            if (validDestination != -1) {
                if (!chessTiles.get(validDestination).occupied && visible) {
                    chessTiles.get(validDestination).setBackground(theme.get(Palette.possibleMoveTileColor));
                    chessTiles.get(validDestination).possibleDestination = true;
                    chessTiles.get(validDestination).toBeSwappedComponentNumber = cKing.componentNumber;
                } else if (chessTiles.get(validDestination).occupied) {
                    possibleDestinations[i] = -1;
                }
            }
        }
        return possibleDestinations;
    } // DONE


    private boolean canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(int componentNumber, int rookComponentNumber) {
        if (rookComponentNumber>-1  && rookComponentNumber<TOTAL_COMPONENT_NUMBER) {
            // in the same row
            if (Arrays.equals(findAndGetRow(rookComponentNumber), findAndGetRow(isBlackTurn ? blackKingPosition : whiteKingPosition))) {
                if (Math.max(rookComponentNumber, isBlackTurn ? blackKingPosition : whiteKingPosition)>componentNumber
                        && Math.min(rookComponentNumber, isBlackTurn ? blackKingPosition : whiteKingPosition)<componentNumber) {
                    return true;
                }
            }
            // in the same column
            if (rookComponentNumber%8==(isBlackTurn ? blackKingPosition : whiteKingPosition)%8) {
                if (componentNumber%8==rookComponentNumber%8){
                    for (int index = rookComponentNumber+8; index < TOTAL_COMPONENT_NUMBER; index+=8) {
                        if (index==componentNumber) {
                            if (Math.max(rookComponentNumber, (isBlackTurn ? blackKingPosition : whiteKingPosition))>componentNumber
                            && Math.min(rookComponentNumber, (isBlackTurn ? blackKingPosition : whiteKingPosition))<componentNumber){
                                return true;
                            }
                        }
                    }
                    for (int index = rookComponentNumber-8; index > -1; index-=8) {
                        if (index == componentNumber) {
                            if (Math.max(rookComponentNumber, (isBlackTurn ? blackKingPosition : whiteKingPosition))>componentNumber
                                    && Math.min(rookComponentNumber, (isBlackTurn ? blackKingPosition : whiteKingPosition))<componentNumber){
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    } // DONE //TODO more testing
    private boolean canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(int componentNumber, int bishopComponentNumber) {
        if (bishopComponentNumber>-1  && bishopComponentNumber<TOTAL_COMPONENT_NUMBER) {
            if (!(bishopComponentNumber == findAndGetRow(bishopComponentNumber)[1])) {
                ArrayList<Integer> chessTilesComponentNumberInTheSameDiagonal = new ArrayList<>(8);
                for (int i = bishopComponentNumber+9; i < TOTAL_COMPONENT_NUMBER; i+=9) {
                    chessTilesComponentNumberInTheSameDiagonal.add(i);
                }
                for (int index = bishopComponentNumber + 9; index < TOTAL_COMPONENT_NUMBER; index += 9) {
                    if (index==componentNumber && chessTilesComponentNumberInTheSameDiagonal.contains(isBlackTurn?
                            blackKingPosition:whiteKingPosition)) {
                            return true;
                        }
                    if (findAndGetRow(index)[1] == index) {
                        break;
                    }
                }
                chessTilesComponentNumberInTheSameDiagonal.clear();
                for (int i = bishopComponentNumber- 7; i > -1; i-= 7) {
                    chessTilesComponentNumberInTheSameDiagonal.add(i);
                }
                for (int index = bishopComponentNumber - 7; index > -1; index -= 7) {
                    if (index==componentNumber && chessTilesComponentNumberInTheSameDiagonal.contains(isBlackTurn?
                            blackKingPosition:whiteKingPosition)) {
                        return true;
                    }
                    if (findAndGetRow(index)[1] == index) {
                        break;
                    }
                }
            }
            if (!(bishopComponentNumber == findAndGetRow(bishopComponentNumber)[0])) {
                ArrayList<Integer> chessTilesComponentNumberInTheSameDiagonal = new ArrayList<>(8);
                for (int i = bishopComponentNumber-9; i > -1; i-=9) {
                    chessTilesComponentNumberInTheSameDiagonal.add(i);
                }
                for (int index = bishopComponentNumber - 9; index > -1; index -= 9) {
                    if (index==componentNumber && chessTilesComponentNumberInTheSameDiagonal.contains(isBlackTurn?
                            blackKingPosition:whiteKingPosition)) {
                        return true;
                    }
                    if (findAndGetRow(index)[0] == index) {
                        break;
                    }
                }

                chessTilesComponentNumberInTheSameDiagonal.clear();
                for (int i = bishopComponentNumber+ 7; i < TOTAL_COMPONENT_NUMBER; i += 7) {
                    chessTilesComponentNumberInTheSameDiagonal.add(i);
                }
                for (int index = bishopComponentNumber + 7; index < TOTAL_COMPONENT_NUMBER; index += 7) {
                    if (index==componentNumber && chessTilesComponentNumberInTheSameDiagonal.contains(isBlackTurn?
                            blackKingPosition:whiteKingPosition)) {
                        return true;
                    }
                    if (findAndGetRow(index)[0] == index) {
                        break;
                    }
                }
            }
        }
        return false;
    } // DONE //TODO more testing
    private boolean canItBeCapturedByQueenInsteadOfKingIfKingInCheckByQueen(int componentNumber, int queenComponentNumber) {
        if (queenComponentNumber>-1  && queenComponentNumber<TOTAL_COMPONENT_NUMBER) {
            return canItBeCapturedByBishopInsteadOfKingIfKingInCheckByBishop(componentNumber, queenComponentNumber) ||
                    canItBeCapturedByRookInsteadOfTheKingIfKingInCheckByRook(componentNumber, queenComponentNumber);
        }
        return false;
    } // DONE //TODO more testing


    private void checkAndEliminateIfCapturableByBlackPawnIfWhiteKingWasInThatChessTile(int[] possibleDestinations, int index) {
        if (possibleDestinations[index]-7>-1) {
            if (Arrays.equals(findAndGetRow(possibleDestinations[index] - 8),
                    findAndGetRow(possibleDestinations[index] - 7))) {
                if (chessTiles.get(possibleDestinations[index] - 7).piece == BPAWN) {
                    possibleDestinations[index+1]=possibleDestinations[index] - 7;
                    possibleDestinations[index] = -1;
                }
            }
        }
        if (possibleDestinations[index]-9>-1) {
            if (Arrays.equals(findAndGetRow(possibleDestinations[index] - 8),
                    findAndGetRow(possibleDestinations[index] - 9))) {
                if (chessTiles.get(possibleDestinations[index] - 9).piece == BPAWN) {
                    possibleDestinations[index+1]=possibleDestinations[index] - 9;
                    possibleDestinations[index] = -1;
                    }
                }
            }
        } // DONE
    private void checkAndEliminateIfCapturableByWhitePawnIfBlackKingWasInThatChessTile(int[] possibleDestinations, int index) {
        if (possibleDestinations[index]+7<TOTAL_COMPONENT_NUMBER) {
            if (Arrays.equals(findAndGetRow(possibleDestinations[index] + 8),
                    findAndGetRow(possibleDestinations[index] + 7))) {
                if (chessTiles.get(possibleDestinations[index] + 7).piece == WPAWN) {
                    possibleDestinations[index+1]=possibleDestinations[index] + 7;
                    possibleDestinations[index] = -1;
                }
            }
        }
        if (possibleDestinations[index]+9<TOTAL_COMPONENT_NUMBER) {
            if (Arrays.equals(findAndGetRow(possibleDestinations[index] + 8),
                    findAndGetRow(possibleDestinations[index] + 9))) {
                if (chessTiles.get(possibleDestinations[index] + 9).piece == WPAWN) {
                    possibleDestinations[index+1]=possibleDestinations[index] + 9;
                    possibleDestinations[index] = -1;
                }
            }
        }
    } // DONE
    private void checkAndEliminateIfCapturableByRookIfKingWasInThatChessTile(int[] possibleDestinations, int index, PieceColor kingColor) {
        // get possibleDestinations[index] -8
        // if occupied by rook then make possibleDestinations[index]=-1
        // else if occupied by a different piece then break;
        // else if empty get possibleDestinations[index]-16 and repeat steps
        // as far possibleDestinations[index]-n>-1
        for (int offset = possibleDestinations[index]-8; offset > -1 ; offset-=8) {
            if (chessTiles.get(offset).occupied) {
                if (chessTiles.get(offset).piece==(kingColor== PieceColor.black? WROOK: BROOK)) {
                    possibleDestinations[index+1]=offset;
                    possibleDestinations[index]=-1;
                } else if (!(chessTiles.get(offset).piece==(kingColor== PieceColor.black? BKING: WKING))) {
                    break;
                }
            }
        }
        // the same algorithm applied but with different displacement
        for (int offset = possibleDestinations[index]+8; offset <TOTAL_COMPONENT_NUMBER ; offset+=8) {
            if (chessTiles.get(offset).occupied) {
                if (chessTiles.get(offset).piece==(kingColor== PieceColor.black? WROOK: BROOK)) {
                    possibleDestinations[index+1]=offset;
                    possibleDestinations[index]=-1;
                } else if (!(chessTiles.get(offset).piece==(kingColor== PieceColor.black? BKING: WKING))) {
                    break;
                }
            }
        }
        // now here we have to check if all moves a rook can make is on the same row
        for (int offset = possibleDestinations[index]-1; offset > -1 ; offset-=1) {
            if (Arrays.equals(findAndGetRow(offset), findAndGetRow(possibleDestinations[index]))) {
                if (chessTiles.get(offset).occupied) {
                    if (chessTiles.get(offset).piece == (kingColor == PieceColor.black ? WROOK : BROOK)) {
                        possibleDestinations[index+1]=offset;
                        possibleDestinations[index] = -1;
                    } else if (!(chessTiles.get(offset).piece==(kingColor== PieceColor.black? BKING: WKING))) {
                    break;
                }
                }
            }
        }
        // same thing for this one just starting from a different direction
        for (int offset = possibleDestinations[index]+1; offset <TOTAL_COMPONENT_NUMBER ; offset+=1) {
            if (Arrays.equals(findAndGetRow(offset), findAndGetRow(possibleDestinations[index]))) {
                if (chessTiles.get(offset).occupied) {
                    if (chessTiles.get(offset).piece == (kingColor == PieceColor.black ? WROOK : BROOK)) {
                        possibleDestinations[index+1]=offset;
                        possibleDestinations[index] = -1;
                    } else if (!(chessTiles.get(offset).piece==(kingColor== PieceColor.black? BKING: WKING))) {
                    break;
                }
                }
            }
        }
    } // DONE
    private void checkAndEliminateIfCapturableByBishopIfKingWasInThatChessTile(int[] possibleDestinations, int index, PieceColor kingColor) {
        // get possibleDestinations[index] +9
        // if occupied by bishop then make possibleDestinations[index]=-1
        // else if occupied by a different piece then break;
        // else if empty get possibleDestinations[index]+18 and repeat steps
        // as far possibleDestinations[index]+n<64
        for (int offset = possibleDestinations[index]+9; offset < TOTAL_COMPONENT_NUMBER; offset+=9) {
            if (Arrays.equals(findAndGetRow(offset), findAndGetRow(offset - 1))) {
                if (chessTiles.get(offset).occupied) {
                    if (chessTiles.get(offset).piece==(kingColor== PieceColor.white? BBISHOP: WBISHOP)) {
                        possibleDestinations[index+1]=offset;
                        possibleDestinations[index]=-1;
                        break;
                    } else if (!(chessTiles.get(offset).piece==(kingColor== PieceColor.black? BKING: WKING))) {
                        break;
                    }
                }
            }
        }
        // same process different numbers
        for (int offset = possibleDestinations[index]+7; offset < TOTAL_COMPONENT_NUMBER; offset+=7) {
            if (Arrays.equals(findAndGetRow(offset), findAndGetRow(offset + 1))) {
                if (chessTiles.get(offset).occupied) {
                    if (chessTiles.get(offset).piece==(kingColor== PieceColor.white? BBISHOP: WBISHOP)) {
                        possibleDestinations[index+1]=offset;
                        possibleDestinations[index]=-1;
                        break;
                    } else if (!(chessTiles.get(offset).piece==(kingColor== PieceColor.black? BKING: WKING))) {
                        break;
                    }
                }
            }
        }
        for (int offset = possibleDestinations[index]-9; offset > -1; offset-=9) {
            if (Arrays.equals(findAndGetRow(offset), findAndGetRow(offset + 1))) {
                if (chessTiles.get(offset).occupied) {
                    if (chessTiles.get(offset).piece==(kingColor== PieceColor.white? BBISHOP: WBISHOP)) {
                        possibleDestinations[index+1]=offset;
                        possibleDestinations[index]=-1;
                        break;
                    } else if (!(chessTiles.get(offset).piece==(kingColor== PieceColor.black? BKING: WKING))) {
                        break;
                    }
                }
            }
        }
        for (int offset = possibleDestinations[index]-7; offset > -1; offset-=7) {
            if (Arrays.equals(findAndGetRow(offset), findAndGetRow(offset - 1))) {
                if (chessTiles.get(offset).occupied) {
                    if (chessTiles.get(offset).piece==(kingColor== PieceColor.white? BBISHOP: WBISHOP)) {
                        possibleDestinations[index+1]=offset;
                        possibleDestinations[index]=-1;
                        break;
                    } else if (!(chessTiles.get(offset).piece==(kingColor== PieceColor.black? BKING: WKING))) {
                        break;
                    }
                }
            }
        }
    } // DONE
    private void checkAndEliminateIfCapturableByKnightIfKingWasInThatChessTile(int[] possibleDestinations, int index, PieceColor kingColor) {

        int[] arrayOfDestination=new int[8];
        arrayOfDestination[0]=(possibleDestinations[index]+2*8+1< TOTAL_COMPONENT_NUMBER
                && findAndGetRow(possibleDestinations[index]+2*8+1)[1]== findAndGetRow(possibleDestinations[index]+2*8)[1]
                ?possibleDestinations[index]+2*8+1:-1);
        arrayOfDestination[1]=(possibleDestinations[index]+2*8-1< TOTAL_COMPONENT_NUMBER
                && findAndGetRow(possibleDestinations[index]+2*8-1)[1]== findAndGetRow(possibleDestinations[index]+2*8)[1]
                ?possibleDestinations[index]+2*8-1:-1);
        arrayOfDestination[2]=(possibleDestinations[index]-2*8+1>-1
                && findAndGetRow(possibleDestinations[index]-2*8+1)[1]== findAndGetRow(possibleDestinations[index]-2*8)[1]
                ?possibleDestinations[index]-2*8+1:-1);
        arrayOfDestination[3]=(possibleDestinations[index]-2*8-1>-1
                && findAndGetRow(possibleDestinations[index]-2*8-1)[1]== findAndGetRow(possibleDestinations[index]-2*8)[1]
                ?possibleDestinations[index]-2*8-1:-1);
        arrayOfDestination[4]=(possibleDestinations[index]+ /* 1* */ 8+2< TOTAL_COMPONENT_NUMBER
                && findAndGetRow(possibleDestinations[index]+8+2)[1]== findAndGetRow(possibleDestinations[index]+8)[1]
                ?possibleDestinations[index]+ /* 1* */ 8+2:-1);
        arrayOfDestination[5]=(possibleDestinations[index]+ /* 1* */ 8-2< TOTAL_COMPONENT_NUMBER
                && findAndGetRow(possibleDestinations[index]+8-2)[1]== findAndGetRow(possibleDestinations[index]+8)[1]
                ?possibleDestinations[index]+ /* 1* */ 8-2:-1);
        arrayOfDestination[6]=(possibleDestinations[index]- /* 1* */ 8+2>-1
                && findAndGetRow(possibleDestinations[index]-8+2)[1]== findAndGetRow(possibleDestinations[index]-8)[1]
                ?possibleDestinations[index]- /* 1* */ 8+2:-1);
        arrayOfDestination[7]=(possibleDestinations[index]- /* 1* */ 8-2>-1
                && findAndGetRow(possibleDestinations[index]-8-2)[1]== findAndGetRow(possibleDestinations[index]-8)[1]
                ?possibleDestinations[index]- /* 1* */ 8-2:-1);

        // checking if array contains correct no of destinations per row (which should be 2)

        for (int destination : arrayOfDestination) {
            if (destination != -1) {
                if (chessTiles.get(destination).occupied) {
                    if (chessTiles.get(destination).piece == (kingColor == PieceColor.black ? WKNIGHT : BKNIGHT)) {
                        possibleDestinations[index+1]=destination;
                        possibleDestinations[index] = -1;
                        break;
                    }
                }
            }
        }
    } // DONE
    private void checkAndEliminateIfCapturableByQueenIfKingWasInThatChessTile(int[] possibleDestinations, int index, PieceColor kingColor) {
        // get possibleDestinations[index] +9
        // if occupied by bishop then make possibleDestinations[index]=-1
        // else if occupied by a different piece then break;
        // else if empty get possibleDestinations[index]+18 and repeat steps
        // as far possibleDestinations[index]+n<64
        for (int offset = possibleDestinations[index]+9; offset < TOTAL_COMPONENT_NUMBER; offset+=9) {
            if (Arrays.equals(findAndGetRow(offset), findAndGetRow(offset - 1))) {
                if (chessTiles.get(offset).occupied) {
                    if (chessTiles.get(offset).piece==(kingColor== PieceColor.white? BQUEEN: WQUEEN)) {
                        possibleDestinations[index+1]=offset;
                        possibleDestinations[index]=-1;
                    } else if (!(chessTiles.get(offset).piece==(kingColor== PieceColor.black? BKING: WKING))) {
                        break;
                    }
                }
            }
        }
        // same process different numbers
        for (int offset = possibleDestinations[index]+7; offset < TOTAL_COMPONENT_NUMBER; offset+=7) {
            if (Arrays.equals(findAndGetRow(offset), findAndGetRow(offset + 1))) {
                if (chessTiles.get(offset).occupied) {
                    if (chessTiles.get(offset).piece==(kingColor== PieceColor.white? BQUEEN: WQUEEN)) {
                        possibleDestinations[index+1]=offset;
                        possibleDestinations[index]=-1;
                    } else if (!(chessTiles.get(offset).piece==(kingColor== PieceColor.black? BKING: WKING))) {
                        break;
                    }
                }
            }
        }
        for (int offset = possibleDestinations[index]-9; offset > -1; offset-=9) {
            if (Arrays.equals(findAndGetRow(offset), findAndGetRow(offset + 1))) {
                if (chessTiles.get(offset).occupied) {
                    if (chessTiles.get(offset).piece==(kingColor== PieceColor.white? BQUEEN: WQUEEN)) {
                        possibleDestinations[index+1]=offset;
                        possibleDestinations[index]=-1;
                    } else if (!(chessTiles.get(offset).piece==(kingColor== PieceColor.black? BKING: WKING))) {
                        break;
                    }
                }
            }
        }
        for (int offset = possibleDestinations[index]-7; offset > -1; offset-=7) {
            if (Arrays.equals(findAndGetRow(offset), findAndGetRow(offset - 1))) {
                if (chessTiles.get(offset).occupied) {
                    if (chessTiles.get(offset).piece==(kingColor== PieceColor.white? BQUEEN: WQUEEN)) {
                        possibleDestinations[index+1]=offset;
                        possibleDestinations[index]=-1;
                    } else if (!(chessTiles.get(offset).piece==(kingColor== PieceColor.black? BKING: WKING))) {
                        break;
                    }
                }
            }
        }
        // get possibleDestinations[index] -8
        // if occupied by rook then make possibleDestinations[index]=-1
        // else if occupied by a different piece then break;
        // else if empty get possibleDestinations[index]-16 and repeat steps
        // as far possibleDestinations[index]-n>-1
        for (int offset = possibleDestinations[index]-8; offset > -1 ; offset-=8) {
            if (chessTiles.get(offset).occupied) {
                if (chessTiles.get(offset).piece==(kingColor== PieceColor.black? WQUEEN: BQUEEN)) {
                    possibleDestinations[index+1]=offset;
                    possibleDestinations[index]=-1;
                } else if (!(chessTiles.get(offset).piece==(kingColor== PieceColor.black? BKING: WKING))) {
                    break;
                }
            }
        }
        // the same algorithm applied but with different displacement
        for (int offset = possibleDestinations[index]+8; offset <TOTAL_COMPONENT_NUMBER ; offset+=8) {
            if (chessTiles.get(offset).occupied) {
                if (chessTiles.get(offset).piece==(kingColor== PieceColor.black? WQUEEN: BQUEEN)) {
                    possibleDestinations[index+1]=offset;
                    possibleDestinations[index]=-1;
                } else if (!(chessTiles.get(offset).piece==(kingColor== PieceColor.black? BKING: WKING))) {
                    break;
                }
            }
        }
        // now here we have to check if all moves a QUEEN can make is on the same row
        for (int offset = possibleDestinations[index]-1; offset > -1 ; offset-=1) {
            if (Arrays.equals(findAndGetRow(offset), findAndGetRow(possibleDestinations[index]))) {
                if (chessTiles.get(offset).occupied) {
                    if (chessTiles.get(offset).piece == (kingColor == PieceColor.black ? WQUEEN : BQUEEN)) {
                        possibleDestinations[index+1]=offset;
                        possibleDestinations[index] = -1;
                    } else if (!(chessTiles.get(offset).piece==(kingColor== PieceColor.black? BKING: WKING))) {
                        break;
                    }
                }
            }
        }
        // same thing for this one just starting from a different direction
        for (int offset = possibleDestinations[index]+1; offset <TOTAL_COMPONENT_NUMBER ; offset+=1) {
            if (Arrays.equals(findAndGetRow(offset), findAndGetRow(possibleDestinations[index]))) {
                if (chessTiles.get(offset).occupied) {
                    if (chessTiles.get(offset).piece == (kingColor == PieceColor.black ? WQUEEN : BQUEEN)) {
                        possibleDestinations[index+1]=offset;
                        possibleDestinations[index] = -1;
                    } else if (!(chessTiles.get(offset).piece==(kingColor== PieceColor.black? BKING: WKING))) {
                        break;
                    }
                }
            }
        }
    } // DONE
    private void checkAndEliminateIfCapturableByKingIfKingWasInThatChessTile(int[] possibleDestinations, int index, PieceColor kingColor) {
        int[] arrayOfDestinations = {
                (possibleDestinations[index]+1< TOTAL_COMPONENT_NUMBER ?possibleDestinations[index]+1:-1),
                (Math.max(possibleDestinations[index] - 1, -1)),
                (possibleDestinations[index]+8< TOTAL_COMPONENT_NUMBER ?possibleDestinations[index]+8:-1),
                (Math.max(possibleDestinations[index] - 8, -1)),
                (possibleDestinations[index]+9< TOTAL_COMPONENT_NUMBER ?possibleDestinations[index]+9:-1),
                (Math.max(possibleDestinations[index] - 9, -1)),
                (possibleDestinations[index]+7< TOTAL_COMPONENT_NUMBER ?possibleDestinations[index]+7:-1),
                (Math.max(possibleDestinations[index] - 7, -1))
        };

        if (findAndGetRow(arrayOfDestinations[0])[0]!= findAndGetRow(possibleDestinations[index])[0]) {
            arrayOfDestinations[0]=-1;
        }
        if (findAndGetRow(arrayOfDestinations[1])[0]!= findAndGetRow(possibleDestinations[index])[0]){
            arrayOfDestinations[1]=-1;
        }
        if (findAndGetRow(arrayOfDestinations[4])[0]!= findAndGetRow(possibleDestinations[index]+8)[0]) {
            arrayOfDestinations[4]=-1;
        }
        if (findAndGetRow(arrayOfDestinations[5])[0]!= findAndGetRow(possibleDestinations[index]-8)[0]){
            arrayOfDestinations[5]=-1;
        }
        if (findAndGetRow(arrayOfDestinations[6])[0]!= findAndGetRow(possibleDestinations[index]+8)[0]) {
            arrayOfDestinations[6]=-1;
        }
        if (findAndGetRow(arrayOfDestinations[7])[0]!= findAndGetRow(possibleDestinations[index]-8)[0]){
            arrayOfDestinations[7]=-1;
        }
        for (int destination : arrayOfDestinations) {
            if (destination != -1) {
                if (chessTiles.get(destination).piece == (kingColor == PieceColor.black ? WKING : BKING)) {
                    possibleDestinations[index + 1] = destination;
                    possibleDestinations[index] = -1;
                }
            }
        }
    } // DONE


    private void kingIsInCheck(PieceColor kingColor) {
        int[] array={(kingColor== PieceColor.black?blackKingPosition:whiteKingPosition), -1};
        if (kingColor== PieceColor.black) {
            checkAndEliminateIfCapturableByWhitePawnIfBlackKingWasInThatChessTile(array, 0);
            if (array[0]==-1) {
                JOptionPane.showMessageDialog(null, "Black" +
                        " king is now in check by opponent's pawn!", "King in check", JOptionPane.WARNING_MESSAGE);
                System.out.println("Black king is now in check by opponent's pawn!");
                pieceThatIsCheckingKing= WPAWN;
                componentNumberOfPieceThatIsCheckingKing=array[1];
                blackKingInCheck=true;
                return;
            }
        } else {
            checkAndEliminateIfCapturableByBlackPawnIfWhiteKingWasInThatChessTile(array, 0);
            if (array[0]==-1){
                JOptionPane.showMessageDialog(null, "White" +
                        " king is now in check by opponent's pawn!", "King in check", JOptionPane.WARNING_MESSAGE);
                System.out.println("Black king is now in check by opponent's pawn!");
                pieceThatIsCheckingKing= BPAWN;
                componentNumberOfPieceThatIsCheckingKing=array[1];
                whiteKingInCheck=true;
                return;
            }
        }
        checkAndEliminateIfCapturableByKingIfKingWasInThatChessTile(array, 0, kingColor);
        if (array[0]==-1){
            JOptionPane.showMessageDialog(null, (kingColor == PieceColor.black ? "Black" : "White") +
                    " king is now in check by opponent's " + "King" + "!", "King in check", JOptionPane.WARNING_MESSAGE);
            System.out.println("Black king is now in check by opponent's king!");
            pieceThatIsCheckingKing=kingColor == PieceColor.black? WKING: BKING;
            componentNumberOfPieceThatIsCheckingKing=array[1];
            if (kingColor== PieceColor.black) {
                blackKingInCheck=true;
            } else {
                whiteKingInCheck=true;
            }
            return;
        }
        checkAndEliminateIfCapturableByRookIfKingWasInThatChessTile(array, 0, kingColor);
        if (array[0]==-1){
            JOptionPane.showMessageDialog(null, (kingColor == PieceColor.black ? "Black" : "White") +
                    " king is now in check by opponent's " + "Rook" + "!", "King in check", JOptionPane.WARNING_MESSAGE);
            System.out.println("Black king is now in check by opponent's rook!");
            pieceThatIsCheckingKing=kingColor == PieceColor.black? WROOK: BROOK;
            componentNumberOfPieceThatIsCheckingKing=array[1];
            if (kingColor== PieceColor.black) {
                blackKingInCheck=true;
            } else {
                whiteKingInCheck=true;
            }
            return;
        }
        checkAndEliminateIfCapturableByBishopIfKingWasInThatChessTile(array, 0, kingColor);
        if (array[0]==-1){
            JOptionPane.showMessageDialog(null, (kingColor == PieceColor.black ? "Black" : "White") +
                    " king is now in check by opponent's " + "Bishop" + "!", "King in check", JOptionPane.WARNING_MESSAGE);
            System.out.println("Black king is now in check by opponent's bishop!");
            pieceThatIsCheckingKing=kingColor == PieceColor.black? WBISHOP: BBISHOP;
            componentNumberOfPieceThatIsCheckingKing=array[1];
            if (kingColor== PieceColor.black) {
                blackKingInCheck=true;
            } else {
                whiteKingInCheck=true;
            }
            return;
        }
        checkAndEliminateIfCapturableByKnightIfKingWasInThatChessTile(array, 0, kingColor);
        if (array[0]==-1){
            JOptionPane.showMessageDialog(null, (kingColor == PieceColor.black ? "Black" : "White") +
                    " king is now in check by opponent's " + "Knight" + "!", "King in check", JOptionPane.WARNING_MESSAGE);
            System.out.println("Black king is now in check by opponent's knight!");
            pieceThatIsCheckingKing=kingColor == PieceColor.black? WKNIGHT: BKNIGHT;
            componentNumberOfPieceThatIsCheckingKing=array[1];
            if (kingColor== PieceColor.black) {
                blackKingInCheck=true;
            } else {
                whiteKingInCheck=true;
            }
            return;
        }
        checkAndEliminateIfCapturableByQueenIfKingWasInThatChessTile(array, 0, kingColor);
        if (array[0]==-1){
            JOptionPane.showMessageDialog(null, (kingColor == PieceColor.black ? "Black" : "White") +
                    " king is now in check by opponent's " + "Queen" + "!", "King in check", JOptionPane.WARNING_MESSAGE);
            System.out.println("Black king is now in check by opponent's queen!");
            pieceThatIsCheckingKing=kingColor == PieceColor.black? WQUEEN: BQUEEN;
            componentNumberOfPieceThatIsCheckingKing=array[1];
            if (kingColor== PieceColor.black) {
                blackKingInCheck=true;
            } else {
                whiteKingInCheck=true;
            }
        }
        if (array[0]!=-1) {
            if (kingColor== PieceColor.black) {
                blackKingInCheck=false;
            } else {
                whiteKingInCheck=false;
            }
        }
//        if (visible){
//            JOptionPane.showMessageDialog(null, (kingColor == All.Game.PieceColor.black ? "Black" : "White") +
//                    " king is now in check!", "King in check", JOptionPane.WARNING_MESSAGE);
//        }

    }


    private static boolean checkIfPieceThatIsCheckingKingCanBeCapturedByAnotherPieceAndOrKingAndReturnFalseIfTrue() {
        canCapturePieceThatIsCheckingTheKing[0]=false;
        canCapturePieceThatIsCheckingTheKing[1]=false;
        if (isBlackTurn?chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isWhite():
               chessTiles.get(componentNumberOfPieceThatIsCheckingKing).isBlack()) {
           for (ChessTile c :chessTiles) {
               if (isBlackTurn?c.isBlack():c.isWhite()){
                   if (c.locked){
                       c.showPossibleKills(false);
                   }
               }
           }
           return !(isBlackTurn?canCapturePieceThatIsCheckingTheKing[0]:canCapturePieceThatIsCheckingTheKing[1]);
       }
       return false;
    } // TODO

    private static boolean checkIfPieceThatIsCheckingKingWillBeForcedToCaptureAnotherPieceOtherThanTheKingOnceItHasBeenMovedAndReturnTrueIfNone() {
        canBeForcedToCaptureAnotherPiece[0]=false;
        canBeForcedToCaptureAnotherPiece[1]=false;
        for (ChessTile c :chessTiles) {
            if (isBlackTurn?c.isBlack():c.isWhite()) {
                if (c.locked) {
                    c.showPossibleMoves(false);
                }
            }
        }
//        System.out.println(Arrays.toString(canBeForcedToCaptureAnotherPiece));
        return !(isBlackTurn?canBeForcedToCaptureAnotherPiece[0]:canBeForcedToCaptureAnotherPiece[1]);
    }


    public static void openPopUpWinningWindow(PieceColor pieceColor) {
        System.out.println("King has been checkmated.\nThe " + (pieceColor== PieceColor.black?"White":"Black") +
                " opponent has won.");
        JOptionPane.showMessageDialog(null, "The " + (pieceColor== PieceColor.black?"Black":"White")
                + " King has been checkmated.\nThe " + (pieceColor== PieceColor.black?"White":"Black") +
                " opponent has won.", (pieceColor== PieceColor.black?"White":"Black") +
                " opponent has won!!", JOptionPane.INFORMATION_MESSAGE);
    }
    public static void openPopUpBlackPawnPromotionWindow(int componentNumber, int toBeSwappedComponentNumber) {
        String[] options=new String[]{"Rook", "Bishop", "Knight", "Queen"};
        int index = JOptionPane.showOptionDialog(null, "Choose what piece do you want your pawn to promote to",
                "Pawn Promotion", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, ImageFields.B_PAWN_IMAGE, options, null);
        switch (index) {
            case 0 -> chessTiles.get(componentNumber).setPiece(BROOK);
            case 1 -> chessTiles.get(componentNumber).setPiece(BBISHOP);
            case 2 -> chessTiles.get(componentNumber).setPiece(BKNIGHT);
            case 3 -> chessTiles.get(componentNumber).setPiece(BQUEEN);
         }
        chessTiles.get(toBeSwappedComponentNumber).setPiece(nopiece);
        chessTiles.get(componentNumber).moveCounter = 1 + chessTiles.get(toBeSwappedComponentNumber).moveCounter;
        chessTiles.get(toBeSwappedComponentNumber).moveCounter = 0;
    }
    public static void openPopUpWhitePawnPromotionWindow(int componentNumber, int toBeSwappedComponentNumber) {
        String[] options=new String[]{"Rook", "Bishop", "Knight", "Queen"};
        int index = JOptionPane.showOptionDialog(null, "Choose what piece do you want your pawn to promote to",
                "Pawn Promotion", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, ImageFields.W_PAWN_IMAGE, options, null);
        switch (index) {
            case 0 -> chessTiles.get(componentNumber).setPiece(WROOK);
            case 1 -> chessTiles.get(componentNumber).setPiece(WBISHOP);
            case 2 ->  chessTiles.get(componentNumber).setPiece(WKNIGHT);
            case 3 -> chessTiles.get(componentNumber).setPiece(WQUEEN);
        }
        chessTiles.get(toBeSwappedComponentNumber).setPiece(nopiece);
        chessTiles.get(componentNumber).moveCounter = 1 + chessTiles.get(toBeSwappedComponentNumber).moveCounter;
        chessTiles.get(toBeSwappedComponentNumber).moveCounter = 0;
    }

}



// TODO after when a king is in check corcening killables, possibles moves when it comes to force capturing anpther piece rather than the king for pieces like the queen and pawn
//TODO PAWN MOVES WHEN IN KING IN CHECK AND IN FRONT OF THE PAWN
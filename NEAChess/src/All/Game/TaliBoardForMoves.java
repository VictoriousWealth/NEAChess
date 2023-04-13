package All.Game;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class TaliBoardForMoves extends JTextArea {

    public final JScrollPane scrollPane;
    public static ArrayList<String> moves=new ArrayList<>();
    public TaliBoardForMoves(int size) {
        this.setOpaque(true);
        this.setVisible(true);
        this.setBorder(BorderFactory.createLineBorder(Color.black, 3));
        this.setBackground(new Color(168, 255, 5));
        this.setMinimumSize(new Dimension(size*6, size*3));
//        this.setText(All.Game.ChessTile.piecesCaptured.toString());
        this.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        this.setEditable(false);
        this.setFont(new Font("Arial", Font.BOLD, 14));

        scrollPane = new JScrollPane(this, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVisible(true);
    }
    public static void collectInfoFromMovesMade(Piece piece, int componentNumber, int toBeSwappedComponentNumber) {
        String destination = convertThisComponentNumberToLocationInChessBoard(componentNumber);
        String origin = convertThisComponentNumberToLocationInChessBoard(toBeSwappedComponentNumber);
        moves.add(piece+": "  + origin + "->" + destination + ". ");
    }

    private static String convertThisComponentNumberToLocationInChessBoard(int componentNumber) {
        String location="";
        switch (componentNumber%8) {
            case 0-> location+="A";
            case 1-> location+="B";
            case 2-> location+="C";
            case 3-> location+="D";
            case 4-> location+="E";
            case 5-> location+="F";
            case 6-> location+="G";
            case 7-> location+="H";
        }

        switch (((componentNumber-(componentNumber%8))/8) + 1) {
            case 1-> {
                return location+"1";
            }
            case 2-> {
                return location+"2";
            }
            case 3-> {
                return location+"3";
            }
            case 4-> {
                return location+"4";
            }
            case 5-> {
                return location+"5";
            }
            case 6-> {
                return location+"6";
            }
            case 7-> {
                return location+"7";
            }
            case 8-> {
                return location+"8";
            }
        }
        return "";
    }
    public void updateTaliBoardMoves(String captureOrMove) {
        StringBuilder temp= new StringBuilder();
        for (String move:moves) {
            temp.append(move);
            if (captureOrMove.equals("k")) {
                temp.append(" captures");
            }
            temp.append("\n");
        }
        this.setText(temp.toString());
    }
}

package All.Game;

import All.Resources.ImageFields;
import All.Resources.THEMES;
import All.Resources.THEMES_ENUM;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    static int count=0;
    public static THEMES theme;
    protected static TaliBoardForCapturedPieces taliForCapturedPieces;

    protected static TaliBoardForMoves taliBoardForMoves;
    public static NextTurnButton nextTurnButton;
    private static int size;
    private final GridLayout gridLayout;

    Main() {
        size = 75;
        this.setTitle("ChessBoard View Window");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setSize(16* size, 9* size);

        theme = new THEMES(THEMES_ENUM.THEME1);
        this.add(new ChessBoardArea(size));

        JLabel toolsLabel=new JLabel();
        gridLayout = new GridLayout(3,1);
        gridLayout.setHgap(5);
        toolsLabel.setLayout(gridLayout);
        toolsLabel.setBorder(BorderFactory.createLineBorder(Color.black, 4));
        toolsLabel.setOpaque(true);
        toolsLabel.setVisible(true);
        toolsLabel.setBounds(size*8+100, 15, size*6, this.getHeight()-size);
        taliBoardForMoves=new TaliBoardForMoves(size);
        toolsLabel.add(taliBoardForMoves.scrollPane);
        taliForCapturedPieces = new TaliBoardForCapturedPieces(size);
        toolsLabel.add(taliForCapturedPieces);
        nextTurnButton = new NextTurnButton(size);
        toolsLabel.add(nextTurnButton);

        this.add(toolsLabel);


        MenuBar menuBar =new MenuBar();
        Menu menu = new Menu("Settings");


        MenuItem activateDeveloperMode=new MenuItem("Developer Mode Activated");
        activateDeveloperMode.addActionListener(e -> {
            if (e.getSource()==activateDeveloperMode) {
                nextTurnButton.setVisible(true);
                nextTurnButton.setEnabled(true);
            }
        });
        MenuItem deactivateDeveloperMode=new MenuItem("Developer Mode Deactivated");
        deactivateDeveloperMode.addActionListener(e -> {
            if (e.getSource()==deactivateDeveloperMode) {
                nextTurnButton.setVisible(false);
                nextTurnButton.setEnabled(false);
            }
        });
        MenuItem changeTheme = new MenuItem("Appearance", new MenuShortcut(55));
        changeTheme.addActionListener(e -> {
            if (e.getSource()==changeTheme) {
                count++;
                if (count%5==0) {
                    theme=new THEMES(THEMES_ENUM.THEME1);
                } else if (count%5==1) {
                    theme=new THEMES(THEMES_ENUM.THEME2);
                } else if (count%5==2) {
                    theme=new THEMES(THEMES_ENUM.THEME3);
                } else if (count%5==3) {
                    theme=new THEMES(THEMES_ENUM.THEME4);
                } else {
                    theme=new THEMES(THEMES_ENUM.THEME5);
                }
                update();
            }
        });


        menu.add(deactivateDeveloperMode);
        menu.add(activateDeveloperMode);
        menu.add(changeTheme);
        menuBar.add(menu);
        this.setMenuBar(menuBar);


        this.setVisible(true);

    }

    public static void main(String[] args) {
        new Main();
        System.out.println(ImageFields.B_BISHOP_IMAGE.toString());
        System.out.println(ImageFields.B_PAWN_IMAGE.getIconHeight());
    }
    private static void update() {
        ChessBoardArea.updateBoardAppearance();
    }

}

package All.Game;

import All.Resources.Palette;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NextTurnButton extends JButton implements ActionListener {

    // constructor of the class
    public NextTurnButton(int size) {
        // setting up the nextTurnButton's defaults
        this.setBackground(Main.theme.get(
                Palette.selectedTileColor));
        this.setBorder(BorderFactory.createLineBorder(
                Color.black, 4));
//        this.setBounds(size*8+100, size*3+50, size*6, size);
        this.setMinimumSize(new Dimension(size*6, size));
        this.setOpaque(true);
        this.setVisible(false);
        this.setEnabled(false);
        this.addActionListener(this); // adding in an actionListener
        this.setText(ChessTile.isBlackTurn?"It's Black's turn":
                "It's White's turn");
        this.setFocusable(false);
        this.setFocusPainted(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ChessTile.nextTurn();
        ChessTile.resetPointers();
        ChessTile.resetBooleanValues();
        ChessTile.resetBackgroundTileColor();
        this.setText(ChessTile.isBlackTurn?"It's Black's turn":"It's White's turn");
    } // allows the turn to be changed successfully and that chessTiles that have
    // changed colors from their default colors are changed back to their default
    // colors as far as it was done before an uncompleted move or captured but after
    // a previously completed move or capture and any pointers or boolean value
    // that were changed before the uncompleted move or captured but after a
    // previously completed move or capture are reset to their default values
}

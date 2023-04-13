package All.Game;

import javax.swing.*;
import java.awt.*;

public class PlayButtonScreen extends JFrame {

    private boolean colorChanging =true;
    boolean canBeDisposed =false;

    // the constructor of the class
    PlayButtonScreen(int size, int[] ratio) {
        // setting up the play button screen's defaults
        this.setTitle("PlayScreenOption");
        this.setSize(ratio[0]*size, ratio[1]*size);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(null);

        // setting up and creating a background container and adds it to the screen
        JLabel labelBackground=new JLabel();
        labelBackground.setBounds(0, 0, this.getWidth(), this.getHeight());
        labelBackground.setBorder(BorderFactory.createLineBorder(Color.black, 3));
        labelBackground.setVisible(true);
        labelBackground.setOpaque(true);
        labelBackground.setBackground(Color.blue);
        this.add(labelBackground);

        // creating an icon's container and setting up the icon and the icon's container's defaults
        ImageIcon icon = new ImageIcon("src/All/Resources/BPIcon.png");

        JLabel imageLabel = new JLabel();
        imageLabel.setBounds(this.getWidth()/2-icon.getIconWidth()/2,
                this.getHeight()/2-icon.getIconHeight()/2-50,
                icon.getIconWidth(), icon.getIconHeight());
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.black, 3));
        imageLabel.setBackground(new Color(30, 34, 42));
        imageLabel.setVisible(true);
        imageLabel.setOpaque(true);
        imageLabel.setIcon(icon); // adds the icon to its container
        labelBackground.add(imageLabel); // adds the icon's container to the background container

        // creating and setting up the playButton's defaults
        JButton playButton = new JButton("Play");
        playButton.setBounds(this.getWidth()/2 -icon.getIconWidth()/2,
                this.getHeight()/2-icon.getIconHeight()/2-50+icon.getIconHeight()+20,
                icon.getIconWidth(), 50);
        playButton.setBorder(BorderFactory.createLineBorder(Color.black, 3));
        playButton.setOpaque(true);
        playButton.setVisible(true);
        labelBackground.add(playButton); // adding the playButton to the screen

        this.setVisible(true); // setting up the screen visibility to true

        playButton.addActionListener(e -> {
            if (e.getSource()==playButton) {
                canBeDisposed =true;
                super.dispose();
                new Main();
            }
        }); // anonymous function used so to create and set up an actionListener for the playButton
            // which disposes this window and initialises the main window when clicked

        // code used so that changing of the background color can be initialized and
        // stopped when the button is clicked
        if (canBeDisposed) {
            colorChanging = false;
        }

        Color[] colors= new Color[150-12];
        for (int i = 0; i < 150-12; i++) {
            colors[i]=new Color(12+i, 12, 175);
        }
        label: while (colorChanging) {
            for (Color color : colors) {
                if (canBeDisposed) {
                    colorChanging=false;
                    break label;
                }
                try {
                    // delays for about ten milliseconds before changing color
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                labelBackground.setBackground(color);
            }
            for (int i = colors.length-1; i >=0 ; i--) {
                if (canBeDisposed) {
                    colorChanging=false;
                    break label;
                }
                try {
                    // delays for about ten milliseconds before changing color
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                labelBackground.setBackground(colors[i]);
            }
        }
    }

}

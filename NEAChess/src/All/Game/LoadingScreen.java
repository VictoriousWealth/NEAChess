package All.Game;

import All.Resources.ImageFields;

import javax.swing.*;
import java.awt.*;

public class LoadingScreen extends JFrame {

    private final JLabel labelBackground;
    private final JLabel imageLabel;
    private final JProgressBar progressBar;
    boolean colorChanging;
    int size;
    static int calls=0;


    // the constructor of the class
    LoadingScreen(int size, int[] ratio) {
        colorChanging =true;

        // setting up the window screen's defaults
        this.size=size;
        this.setTitle("Chess All.Game");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setSize(ratio[0]*size, ratio[1]*size);
        this.setIconImage(new ImageIcon(ImageFields.B_PAWN_IMAGE.toString()).getImage());

        // setting up and creating the background container and adding it to the window
        labelBackground = new JLabel();
        labelBackground.setBounds(0, 0, this.getWidth(), this.getHeight());
        labelBackground.setBorder(BorderFactory.createLineBorder(Color.black, 3));
        labelBackground.setVisible(true);
        labelBackground.setOpaque(true);
        labelBackground.setBackground(Color.blue);
        this.add(labelBackground);

        // setting up the icon
        ImageIcon icon =new ImageIcon("src/All/Resources/BPIcon.png");
        // setting up and creating the icon's container
        imageLabel = new JLabel();
        imageLabel.setBounds(this.getWidth()/2-icon.getIconWidth()/2,
                this.getHeight()/2-icon.getIconHeight()/2-50,
                icon.getIconWidth(), icon.getIconHeight());
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.black, 3));
        imageLabel.setBackground(new Color(30, 34, 42));
        imageLabel.setVisible(true);
        imageLabel.setOpaque(true);
        //adding the icon to its presetted container
        imageLabel.setIcon(icon);
//        System.out.println("height="+icon.getIconHeight()+", width="+ icon.getIconWidth());
        //adding the icon's container to the background container
        labelBackground.add(imageLabel);

        // setting up and creating the progress bar and addiing it to the background container
        progressBar = new JProgressBar();
        progressBar.setBounds(this.getWidth()/2 -icon.getIconWidth()/2,
                this.getHeight()/2-icon.getIconHeight()/2-50+icon.getIconHeight()+20,
                icon.getIconWidth(), 50);
        progressBar.setBorder(BorderFactory.createLineBorder(Color.black, 3));
        progressBar.setOpaque(true);
        progressBar.setVisible(true);
        progressBar.setBackground(Color.blue);
        labelBackground.add(progressBar);
        // setting up the limits of the progressbar as well as its initial value
        progressBar.setMaximum(100);
        progressBar.setMinimum(0);
        progressBar.setValue(10);

        // making the main window, loading screen, visible
        this.setVisible(true);

        // making a delay time of about 1 second before the background begins to change colour
        try {
            Thread.sleep(1111);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // code needed for the color of the background container to change
        Color[] colors= new Color[150-32];
        for (int i = 0; i < 150-32; i++) {
            colors[i]=new Color(32+i, 12, 175);
        }
        label: while (colorChanging) {
            for (Color color : colors) {
                // delays for about ten milliseconds before changing color
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                labelBackground.setBackground(color);
            }
            for (int i = colors.length-1; i >=0 ; i--) {
                // delays for about ten milliseconds before changing color
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                labelBackground.setBackground(colors[i]);
            }
            if (progressBar.getValue()==100) {
                this.dispose();
                new PlayButtonScreen(size, ratio);
                break label;
            }
            progressBar.setValue(makeItExponential());
            calls++;
        }
    }

    // allows the progressBar value to be updated in real time
    private int makeItExponential() {
        switch (calls) {
            case 0 -> {
                return 5;
            }
            case 1 -> {
                return 35;
            }
            case 2-> {
                return 55;
            }
            case 3-> {
                return 75;
            }
            case 4-> {
                return 89;
            }
            default -> {
                return 100;
            }
        }
    }

    // the main method of the project from which all other classes and methods are then executed.
    public static void main(String[] args) {
        int size = 75;
        int[] ratio = {16, 9};
        new LoadingScreen(size, ratio);
    }
}

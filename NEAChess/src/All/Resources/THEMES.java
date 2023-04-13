package All.Resources;

import java.awt.*;
import java.util.HashMap;

import static All.Resources.THEMES_ENUM.*;

public class THEMES extends HashMap<Enum, Color> {

    private final Color lightTileColor;
    private final Color darkTileColor;
    private final Color selectedTileColor;
    private final Color possibleKillTileColor;
    private final Color possibleMoveTileColor;

    public THEMES(THEMES_ENUM themes_enum) {
        if (themes_enum == THEME1) {
            lightTileColor = new Color(255, 255, 150);
            darkTileColor = new Color(182, 100, 5);
            selectedTileColor = new Color(24, 162, 58, 195);
            possibleMoveTileColor = new Color(7, 61, 0);
            possibleKillTileColor = new Color(157, 0, 0);
        } else if (themes_enum == THEME2) {
            lightTileColor = new Color(255, 255, 150);
            darkTileColor = new Color(20, 103, 155);
            selectedTileColor = new Color(24, 162, 58, 195);
            possibleMoveTileColor = new Color(7, 61, 0);
            possibleKillTileColor = new Color(157, 0, 0);
        } else if (themes_enum == THEME3) {
            lightTileColor = new Color(255, 255, 150);
            darkTileColor = new Color(0, 100, 5);
            selectedTileColor = new Color(255, 242, 70, 195);
            possibleMoveTileColor = new Color(252, 205, 0);
            possibleKillTileColor = new Color(157, 0, 0);
        } else if (themes_enum == THEME4) {
            lightTileColor = new Color(255, 255, 150);
            darkTileColor = new Color(98, 7, 126);
            selectedTileColor = new Color(255, 51, 223, 195);
            possibleMoveTileColor = new Color(199, 2, 138);
            possibleKillTileColor = new Color(157, 0, 0);
        } else {
            lightTileColor = new Color(255, 255, 255);
            darkTileColor = new Color(0, 0, 0);
            selectedTileColor = new Color(91, 181, 252, 168);
            possibleMoveTileColor = new Color(32, 96, 131);
            possibleKillTileColor = new Color(231, 11, 11);
        }
        this.put(Palette.lightTileColor, lightTileColor);
        this.put(Palette.darkTileColor, darkTileColor);
        this.put(Palette.selectedTileColor, selectedTileColor);
        this.put(Palette.possibleMoveTileColor, possibleMoveTileColor);
        this.put(Palette.possibleKillTileColor, possibleKillTileColor);
    }
}

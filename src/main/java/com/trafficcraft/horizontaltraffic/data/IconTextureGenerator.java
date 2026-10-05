package com.trafficcraft.horizontaltraffic.data;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class IconTextureGenerator {

    public static final int BG_COLOR = 0xFF181818;
    public static final int COLOR_RED = 0xFFFF2222;
    public static final int COLOR_YELLOW = 0xFFFFEB1A;
    public static final int COLOR_GREEN = 0xFF00EE00;
    public static final int GUI_ICON_COLOR = 0xFF373737;

    public static Map<String, boolean[][]> getIconGrids() {
        Map<String, boolean[][]> map = new LinkedHashMap<>();

        // 1. U_TURN (掉头)
        boolean[][] uTurn = new boolean[16][16];
        for (int y = 5; y <= 13; y++) {
            uTurn[y][10] = true;
            uTurn[y][11] = true;
        }
        uTurn[4][9] = true; uTurn[4][10] = true;
        uTurn[3][7] = true; uTurn[3][8] = true; uTurn[3][9] = true;
        uTurn[4][5] = true; uTurn[4][6] = true;
        uTurn[5][4] = true; uTurn[5][5] = true;
        uTurn[6][4] = true; uTurn[6][5] = true;
        uTurn[7][4] = true; uTurn[7][5] = true;
        // Arrowhead pointing down
        for (int x = 1; x <= 8; x++) uTurn[8][x] = true;
        for (int x = 2; x <= 7; x++) uTurn[9][x] = true;
        for (int x = 3; x <= 6; x++) uTurn[10][x] = true;
        uTurn[11][4] = true; uTurn[11][5] = true;
        map.put("u_turn", uTurn);

        // 2. LEFT_AND_U_TURN (左转 + 掉头)
        boolean[][] leftUTurn = new boolean[16][16];
        for (int y = 5; y <= 13; y++) {
            leftUTurn[y][11] = true;
            leftUTurn[y][12] = true;
        }
        leftUTurn[4][10] = true; leftUTurn[4][11] = true;
        leftUTurn[3][8] = true; leftUTurn[3][9] = true; leftUTurn[3][10] = true;
        leftUTurn[4][7] = true; leftUTurn[4][8] = true;
        leftUTurn[5][6] = true; leftUTurn[5][7] = true;
        // U-turn arrowhead pointing down at x=6..7
        for (int x = 4; x <= 9; x++) leftUTurn[6][x] = true;
        for (int x = 5; x <= 8; x++) leftUTurn[7][x] = true;
        leftUTurn[8][6] = true; leftUTurn[8][7] = true;
        // Left-turn branch at y=10..11
        for (int x = 3; x <= 10; x++) {
            leftUTurn[10][x] = true;
            leftUTurn[11][x] = true;
        }
        // Left arrowhead
        leftUTurn[8][3] = true;
        leftUTurn[9][2] = true; leftUTurn[9][3] = true;
        leftUTurn[10][1] = true; leftUTurn[10][2] = true; leftUTurn[10][3] = true;
        leftUTurn[11][1] = true; leftUTurn[11][2] = true; leftUTurn[11][3] = true;
        leftUTurn[12][2] = true; leftUTurn[12][3] = true;
        leftUTurn[13][3] = true;
        map.put("left_u_turn", leftUTurn);

        // 3. LEFT_STRAIGHT_RIGHT (左转 + 直行 + 右转)
        boolean[][] lsr = new boolean[16][16];
        for (int y = 5; y <= 14; y++) {
            lsr[y][7] = true;
            lsr[y][8] = true;
        }
        // Straight arrowhead
        lsr[1][7] = true; lsr[1][8] = true;
        for (int x = 6; x <= 9; x++) lsr[2][x] = true;
        for (int x = 5; x <= 10; x++) lsr[3][x] = true;
        for (int x = 6; x <= 9; x++) lsr[4][x] = true;
        lsr[5][7] = true; lsr[5][8] = true;
        // Left branch & arrowhead
        for (int x = 2; x <= 6; x++) {
            lsr[9][x] = true;
            lsr[10][x] = true;
        }
        lsr[7][3] = true;
        lsr[8][2] = true; lsr[8][3] = true;
        lsr[9][1] = true; lsr[9][2] = true; lsr[9][3] = true;
        lsr[10][1] = true; lsr[10][2] = true; lsr[10][3] = true;
        lsr[11][2] = true; lsr[11][3] = true;
        lsr[12][3] = true;
        // Right branch & arrowhead
        for (int x = 9; x <= 13; x++) {
            lsr[9][x] = true;
            lsr[10][x] = true;
        }
        lsr[7][12] = true;
        lsr[8][12] = true; lsr[8][13] = true;
        lsr[9][12] = true; lsr[9][13] = true; lsr[9][14] = true;
        lsr[10][12] = true; lsr[10][13] = true; lsr[10][14] = true;
        lsr[11][12] = true; lsr[11][13] = true;
        lsr[12][12] = true;
        map.put("left_straight_right", lsr);

        // 4. RIGHT_AND_U_TURN (右转 + 掉头)
        boolean[][] rightUTurn = new boolean[16][16];
        for (int y = 5; y <= 13; y++) {
            rightUTurn[y][6] = true;
            rightUTurn[y][7] = true;
        }
        rightUTurn[4][5] = true; rightUTurn[4][6] = true;
        rightUTurn[3][3] = true; rightUTurn[3][4] = true; rightUTurn[3][5] = true;
        rightUTurn[4][2] = true; rightUTurn[4][3] = true;
        rightUTurn[5][1] = true; rightUTurn[5][2] = true;
        // U-turn arrowhead pointing down at x=1..2
        for (int x = 0; x <= 4; x++) rightUTurn[6][x] = true;
        for (int x = 1; x <= 3; x++) rightUTurn[7][x] = true;
        rightUTurn[8][1] = true; rightUTurn[8][2] = true;
        // Right-turn branch at y=10..11
        for (int x = 7; x <= 12; x++) {
            rightUTurn[10][x] = true;
            rightUTurn[11][x] = true;
        }
        rightUTurn[8][12] = true;
        rightUTurn[9][12] = true; rightUTurn[9][13] = true;
        rightUTurn[10][12] = true; rightUTurn[10][13] = true; rightUTurn[10][14] = true;
        rightUTurn[11][12] = true; rightUTurn[11][13] = true; rightUTurn[11][14] = true;
        rightUTurn[12][12] = true; rightUTurn[12][13] = true;
        rightUTurn[13][12] = true;
        map.put("right_u_turn", rightUTurn);

        // 5. LEFT_AND_RIGHT (左转 + 右转)
        boolean[][] lr = new boolean[16][16];
        for (int y = 10; y <= 14; y++) {
            lr[y][7] = true;
            lr[y][8] = true;
        }
        // Left branch & arrowhead
        for (int x = 2; x <= 7; x++) {
            lr[9][x] = true;
            lr[10][x] = true;
        }
        lr[7][3] = true;
        lr[8][2] = true; lr[8][3] = true;
        lr[9][1] = true; lr[9][2] = true; lr[9][3] = true;
        lr[10][1] = true; lr[10][2] = true; lr[10][3] = true;
        lr[11][2] = true; lr[11][3] = true;
        lr[12][3] = true;
        // Right branch & arrowhead
        for (int x = 8; x <= 13; x++) {
            lr[9][x] = true;
            lr[10][x] = true;
        }
        lr[7][12] = true;
        lr[8][12] = true; lr[8][13] = true;
        lr[9][12] = true; lr[9][13] = true; lr[9][14] = true;
        lr[10][12] = true; lr[10][13] = true; lr[10][14] = true;
        lr[11][12] = true; lr[11][13] = true;
        lr[12][12] = true;
        map.put("left_right", lr);

        // 6. SLANTED_LEFT (斜向左转 ↖)
        boolean[][] sLeft = new boolean[16][16];
        // Diagonal stem from (12,13) up to (5,6)
        for (int i = 0; i <= 7; i++) {
            int x = 12 - i;
            int y = 13 - i;
            sLeft[y][x] = true;
            sLeft[y][x + 1] = true;
            sLeft[y - 1][x] = true;
        }
        // Diagonal arrowhead pointing to (2,2)
        sLeft[1][2] = true; sLeft[1][3] = true;
        sLeft[2][1] = true; sLeft[2][2] = true; sLeft[2][3] = true; sLeft[2][4] = true;
        sLeft[3][1] = true; sLeft[3][2] = true; sLeft[3][3] = true; sLeft[3][4] = true; sLeft[3][5] = true;
        sLeft[4][2] = true; sLeft[4][3] = true; sLeft[4][4] = true; sLeft[4][5] = true;
        sLeft[5][3] = true; sLeft[5][4] = true; sLeft[5][5] = true;
        // Wings along cross-axis
        sLeft[1][4] = true; sLeft[1][5] = true;
        sLeft[4][1] = true; sLeft[5][1] = true;
        map.put("slanted_left", sLeft);

        // 7. SLANTED_RIGHT (斜向右转 ↗) - Horizontal mirror of SLANTED_LEFT
        boolean[][] sRight = new boolean[16][16];
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                sRight[y][15 - x] = sLeft[y][x];
            }
        }
        map.put("slanted_right", sRight);

        // 8. SLANTED_LEFT_AND_RIGHT (双向斜箭头 ↖ ↗)
        boolean[][] slr = new boolean[16][16];
        for (int y = 11; y <= 14; y++) {
            slr[y][7] = true;
            slr[y][8] = true;
        }
        // Left slanted branch to ↖
        for (int i = 0; i <= 5; i++) {
            int x = 7 - i;
            int y = 11 - i;
            slr[y][x] = true;
            slr[y][x + 1] = true;
        }
        // Left diagonal arrowhead at (2,3)
        slr[2][3] = true; slr[2][4] = true;
        slr[3][2] = true; slr[3][3] = true; slr[3][4] = true;
        slr[4][2] = true; slr[4][3] = true; slr[4][4] = true;
        slr[5][3] = true; slr[5][4] = true;

        // Right slanted branch to ↗
        for (int i = 0; i <= 5; i++) {
            int x = 8 + i;
            int y = 11 - i;
            slr[y][x] = true;
            slr[y][x - 1] = true;
        }
        // Right diagonal arrowhead at (13,3)
        slr[2][11] = true; slr[2][12] = true;
        slr[3][11] = true; slr[3][12] = true; slr[3][13] = true;
        slr[4][11] = true; slr[4][12] = true; slr[4][13] = true;
        slr[5][11] = true; slr[5][12] = true;
        map.put("slanted_left_right", slr);

        return map;
    }

    public static void main(String[] args) throws IOException {
        String baseDir = args.length > 0 ? args[0] : ".";
        File resDir = new File(baseDir, "src/main/resources/assets");

        File tcBlockDir = new File(resDir, "trafficcraft/textures/block/traffic_light");
        File htBlockDir = new File(resDir, "horizontal_traffic/textures/block/traffic_light");
        tcBlockDir.mkdirs();
        htBlockDir.mkdirs();

        File tcGuiDir = new File(resDir, "trafficcraft/textures/gui");
        File htGuiDir = new File(resDir, "horizontal_traffic/textures/gui");
        tcGuiDir.mkdirs();
        htGuiDir.mkdirs();

        Map<String, boolean[][]> iconGrids = getIconGrids();

        // 1. Generate bulb textures (Red, Yellow, Green) for both namespaces
        int[] colors = new int[]{COLOR_RED, COLOR_YELLOW, COLOR_GREEN};
        String[] colorNames = new String[]{"red", "yellow", "green"};

        for (Map.Entry<String, boolean[][]> entry : iconGrids.entrySet()) {
            String name = entry.getKey();
            boolean[][] grid = entry.getValue();

            for (int c = 0; c < colors.length; c++) {
                int litColor = colors[c];
                String colorName = colorNames[c];

                BufferedImage bulb = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                for (int y = 0; y < 16; y++) {
                    for (int x = 0; x < 16; x++) {
                        if (grid[y][x]) {
                            bulb.setRGB(x, y, litColor);
                        } else {
                            bulb.setRGB(x, y, BG_COLOR);
                        }
                    }
                }

                String filename = name + "_" + colorName + ".png";
                ImageIO.write(bulb, "PNG", new File(tcBlockDir, filename));
                ImageIO.write(bulb, "PNG", new File(htBlockDir, filename));
            }
        }
        System.out.println("Generated " + (iconGrids.size() * 3) + " bulb textures!");

        // 2. Generate updated GUI icons.png
        File sourceIconsFile = new File(baseDir, "assets/trafficcraft/textures/gui/icons.png");
        BufferedImage iconsSheet;
        if (sourceIconsFile.exists()) {
            iconsSheet = ImageIO.read(sourceIconsFile);
        } else {
            iconsSheet = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        }

        int colIndex = 8;
        for (Map.Entry<String, boolean[][]> entry : iconGrids.entrySet()) {
            boolean[][] grid = entry.getValue();
            int startX = colIndex * 16;
            int startY = 16; // row 1 (v=1)

            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 16; x++) {
                    if (grid[y][x]) {
                        iconsSheet.setRGB(startX + x, startY + y, GUI_ICON_COLOR);
                    }
                }
            }
            colIndex++;
        }

        ImageIO.write(iconsSheet, "PNG", new File(tcGuiDir, "icons.png"));
        ImageIO.write(iconsSheet, "PNG", new File(htGuiDir, "icons.png"));
        System.out.println("Generated icons.png with 8 new icons in row 1, cols 8-15!");
    }
}

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

        // 1. U_TURN (掉头) - Matching TrafficCraft chevron style
        boolean[][] uTurn = new boolean[16][16];
        for (int y = 3; y <= 14; y++) {
            uTurn[y][10] = true;
            uTurn[y][11] = true;
        }
        for (int x = 5; x <= 9; x++) uTurn[1][x] = true;
        uTurn[2][4] = true; uTurn[2][5] = true; uTurn[2][9] = true; uTurn[2][10] = true;
        for (int y = 3; y <= 7; y++) {
            uTurn[y][3] = true;
            uTurn[y][4] = true;
        }
        // Downward open chevron
        uTurn[8][0] = true; uTurn[8][1] = true; uTurn[8][6] = true; uTurn[8][7] = true;
        uTurn[9][0] = true; uTurn[9][1] = true; uTurn[9][2] = true;
        uTurn[9][5] = true; uTurn[9][6] = true; uTurn[9][7] = true;
        for (int x = 1; x <= 6; x++) uTurn[10][x] = true;
        for (int x = 2; x <= 5; x++) uTurn[11][x] = true;
        uTurn[12][3] = true; uTurn[12][4] = true;
        map.put("u_turn", uTurn);

        // 2. LEFT_AND_U_TURN (左转 + 掉头: 左转在上方，掉头在下方)
        boolean[][] leftUTurn = new boolean[16][16];
        // Trunk on right at x=9..10
        for (int y = 2; y <= 14; y++) {
            leftUTurn[y][9] = true;
            leftUTurn[y][10] = true;
        }
        // Top: Left turn arrow (←)
        for (int x = 3; x <= 9; x++) {
            leftUTurn[3][x] = true;
            leftUTurn[4][x] = true;
        }
        leftUTurn[1][4] = true; leftUTurn[1][5] = true;
        leftUTurn[2][3] = true; leftUTurn[2][4] = true; leftUTurn[2][5] = true;
        leftUTurn[3][1] = true; leftUTurn[3][2] = true; leftUTurn[3][3] = true;
        leftUTurn[4][1] = true; leftUTurn[4][2] = true; leftUTurn[4][3] = true;
        leftUTurn[5][3] = true; leftUTurn[5][4] = true; leftUTurn[5][5] = true;
        leftUTurn[6][4] = true; leftUTurn[6][5] = true;
        // Bottom: U-turn arch & downward chevron
        for (int x = 5; x <= 9; x++) leftUTurn[7][x] = true;
        leftUTurn[8][4] = true; leftUTurn[8][5] = true; leftUTurn[8][8] = true; leftUTurn[8][9] = true;
        leftUTurn[9][3] = true; leftUTurn[9][4] = true;
        leftUTurn[10][1] = true; leftUTurn[10][2] = true; leftUTurn[10][5] = true; leftUTurn[10][6] = true;
        for (int x = 1; x <= 6; x++) leftUTurn[11][x] = true;
        for (int x = 2; x <= 5; x++) leftUTurn[12][x] = true;
        leftUTurn[13][3] = true; leftUTurn[13][4] = true;
        map.put("left_u_turn", leftUTurn);

        // 3. LEFT_STRAIGHT_RIGHT (左转 + 直行 + 右转)
        boolean[][] lsr = new boolean[16][16];
        for (int y = 5; y <= 14; y++) {
            lsr[y][7] = true;
            lsr[y][8] = true;
        }
        // Straight open chevron at top (stops cleanly at y=5)
        lsr[1][7] = true; lsr[1][8] = true;
        for (int x = 6; x <= 9; x++) lsr[2][x] = true;
        for (int x = 5; x <= 10; x++) lsr[3][x] = true;
        for (int x = 4; x <= 6; x++) lsr[4][x] = true;
        for (int x = 9; x <= 11; x++) lsr[4][x] = true;
        for (int x = 4; x <= 5; x++) lsr[5][x] = true;
        for (int x = 10; x <= 11; x++) lsr[5][x] = true;

        // Rows 6 & 7 are ONLY trunk (2 full rows of empty space separating top head from side wings)

        // Side horizontal crossbar at y=10..11
        for (int x = 2; x <= 13; x++) {
            lsr[10][x] = true;
            lsr[11][x] = true;
        }
        // Left chevron (y=8..13)
        for (int x = 3; x <= 4; x++) lsr[8][x] = true;
        for (int x = 2; x <= 4; x++) lsr[9][x] = true;
        for (int x = 1; x <= 3; x++) { lsr[10][x] = true; lsr[11][x] = true; }
        for (int x = 2; x <= 4; x++) lsr[12][x] = true;
        for (int x = 3; x <= 4; x++) lsr[13][x] = true;

        // Right chevron (y=8..13)
        for (int x = 11; x <= 12; x++) lsr[8][x] = true;
        for (int x = 11; x <= 13; x++) lsr[9][x] = true;
        for (int x = 12; x <= 14; x++) { lsr[10][x] = true; lsr[11][x] = true; }
        for (int x = 11; x <= 13; x++) lsr[12][x] = true;
        for (int x = 11; x <= 12; x++) lsr[13][x] = true;

        map.put("left_straight_right", lsr);

        // 4. RIGHT_AND_U_TURN (右转 + 掉头: 右转在上方，掉头在下方)
        boolean[][] rightUTurn = new boolean[16][16];
        // Trunk on left at x=6..7
        for (int y = 2; y <= 14; y++) {
            rightUTurn[y][6] = true;
            rightUTurn[y][7] = true;
        }
        // Top: Right turn arrow (→)
        for (int x = 7; x <= 13; x++) {
            rightUTurn[3][x] = true;
            rightUTurn[4][x] = true;
        }
        rightUTurn[1][10] = true; rightUTurn[1][11] = true;
        rightUTurn[2][10] = true; rightUTurn[2][11] = true; rightUTurn[2][12] = true;
        rightUTurn[3][12] = true; rightUTurn[3][13] = true; rightUTurn[3][14] = true;
        rightUTurn[4][12] = true; rightUTurn[4][13] = true; rightUTurn[4][14] = true;
        rightUTurn[5][10] = true; rightUTurn[5][11] = true; rightUTurn[5][12] = true;
        rightUTurn[6][10] = true; rightUTurn[6][11] = true;
        // Bottom: U-turn arch & downward chevron
        for (int x = 2; x <= 6; x++) rightUTurn[7][x] = true;
        rightUTurn[8][1] = true; rightUTurn[8][2] = true; rightUTurn[8][5] = true; rightUTurn[8][6] = true;
        rightUTurn[9][1] = true; rightUTurn[9][2] = true;
        rightUTurn[10][0] = true; rightUTurn[10][1] = true; rightUTurn[10][3] = true; rightUTurn[10][4] = true;
        for (int x = 0; x <= 4; x++) rightUTurn[11][x] = true;
        for (int x = 1; x <= 3; x++) rightUTurn[12][x] = true;
        rightUTurn[13][1] = true; rightUTurn[13][2] = true;
        map.put("right_u_turn", rightUTurn);

        // 5. LEFT_AND_RIGHT (左转 + 右转: 居中，大箭头)
        boolean[][] lr = new boolean[16][16];
        // Horizontal bar centered at y=7..8
        for (int x = 2; x <= 13; x++) {
            lr[7][x] = true;
            lr[8][x] = true;
        }
        // Vertical stem down to y=14
        for (int y = 9; y <= 14; y++) {
            lr[y][7] = true;
            lr[y][8] = true;
        }
        // Bold 10px-tall left chevron
        int[][] lrLeftWings = {
                {3, 4, 5}, {4, 3, 5}, {5, 2, 4}, {6, 1, 3}, {7, 1, 3},
                {8, 1, 3}, {9, 1, 3}, {10, 2, 4}, {11, 3, 5}, {12, 4, 5}
        };
        for (int[] w : lrLeftWings) {
            for (int x = w[1]; x <= w[2]; x++) lr[w[0]][x] = true;
        }
        // Bold 10px-tall right chevron
        int[][] lrRightWings = {
                {3, 10, 11}, {4, 10, 12}, {5, 11, 13}, {6, 12, 14}, {7, 12, 14},
                {8, 12, 14}, {9, 12, 14}, {10, 11, 13}, {11, 10, 12}, {12, 10, 11}
        };
        for (int[] w : lrRightWings) {
            for (int x = w[1]; x <= w[2]; x++) lr[w[0]][x] = true;
        }
        map.put("left_right", lr);

        // 6. SLANTED_LEFT (斜向左转 ↖) - Matching user hand-drawn sketch, large bold 9px arrowhead
        boolean[][] sLeft = new boolean[16][16];
        // Top horizontal barb (2px thick, 9px long)
        for (int y = 1; y <= 2; y++) {
            for (int x = 1; x <= 9; x++) sLeft[y][x] = true;
        }
        // Left vertical barb (2px thick, 9px long)
        for (int y = 1; y <= 9; y++) {
            sLeft[y][1] = sLeft[y][2] = true;
        }
        // 2px diagonal shaft from (3,3) down to (13,13)
        for (int i = 0; i <= 10; i++) {
            int y = 3 + i;
            int x = 3 + i;
            if (y < 16 && x < 16) {
                sLeft[y][x] = true;
                if (x + 1 < 16) sLeft[y][x + 1] = true;
            }
        }
        map.put("slanted_left", sLeft);

        // 7. SLANTED_RIGHT (斜向右转 ↗) - Horizontal mirror of SLANTED_LEFT (large bold 9px arrowhead)
        boolean[][] sRight = new boolean[16][16];
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                sRight[y][15 - x] = sLeft[y][x];
            }
        }
        map.put("slanted_right", sRight);

        // 8. SLANTED_LEFT_AND_RIGHT (双向斜箭头 ↖ ↗) - Matching user hand-drawn sketch, bold 2px lines
        boolean[][] slr = new boolean[16][16];
        // Top horizontal barbs (2px thick)
        for (int y = 1; y <= 2; y++) {
            for (int x = 1; x <= 6; x++) slr[y][x] = true;
        }
        // Left vertical barb (2px thick)
        for (int y = 1; y <= 6; y++) {
            slr[y][1] = slr[y][2] = true;
        }
        // 2px left diagonal shaft from (3,3) to (7,7)
        for (int i = 0; i <= 4; i++) {
            int y = 3 + i;
            int x = 3 + i;
            slr[y][x] = true;
            slr[y][x + 1] = true;
        }

        // Mirror left branch to right branch
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 8; x++) {
                slr[y][15 - x] = slr[y][x];
            }
        }

        // Central vertical stem at bottom x=7..8, y=7..14
        for (int y = 7; y <= 14; y++) {
            slr[y][7] = true;
            slr[y][8] = true;
        }

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
        File jarFile = new File(baseDir, "libs/trafficcraft-fabric-1.20.1-1.2.0-beta.3.jar");
        BufferedImage iconsSheet;
        if (jarFile.exists()) {
            try (java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile)) {
                java.util.zip.ZipEntry entry = jar.getEntry("assets/trafficcraft/textures/gui/icons.png");
                iconsSheet = ImageIO.read(jar.getInputStream(entry));
            }
        } else {
            iconsSheet = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        }

        int colIndex = 8;
        for (Map.Entry<String, boolean[][]> entry : iconGrids.entrySet()) {
            boolean[][] grid = entry.getValue();
            int startX = colIndex * 16;
            int startY = 16; // row 1 (v=1)

            // Clear the 16x16 slot first to ensure no leftover pixels from previous runs
            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 16; x++) {
                    iconsSheet.setRGB(startX + x, startY + y, 0);
                }
            }

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

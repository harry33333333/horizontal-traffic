package com.trafficcraft.horizontaltraffic.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;

import java.util.Arrays;

public class QuadTransformHelper {

    /**
     * Transforms a BakedQuad by scaling around center (0.5, 0, 0.5) and rotating around Y axis by angleDegrees.
     */
    public static BakedQuad transformQuad(BakedQuad quad, double scaleX, double scaleY, double scaleZ, double angleDegrees) {
        int[] original = quad.getVertices();
        int[] vertices = Arrays.copyOf(original, original.length);

        double rad = Math.toRadians(angleDegrees);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        for (int v = 0; v < 4; v++) {
            int idx = v * 8;
            float x = Float.intBitsToFloat(vertices[idx]);
            float y = Float.intBitsToFloat(vertices[idx + 1]);
            float z = Float.intBitsToFloat(vertices[idx + 2]);

            double dx = (x - 0.5) * scaleX;
            double dy = y * scaleY;
            double dz = (z - 0.5) * scaleZ;

            double nx = 0.5 + (dx * cos - dz * sin);
            double nz = 0.5 + (dx * sin + dz * cos);

            vertices[idx] = Float.floatToRawIntBits((float) nx);
            vertices[idx + 1] = Float.floatToRawIntBits((float) dy);
            vertices[idx + 2] = Float.floatToRawIntBits((float) nz);
        }

        return finalizeQuad(quad, vertices, cos, sin);
    }

    /**
     * Transforms the SupportPole of a traffic light so that its base remains connected to the bracket (at 0.3125 relative)
     * while its tip extends to the block boundary corner (0.7071 relative = 0.5 * sqrt(2)).
     */
    public static BakedQuad transformTrafficLightPoleQuad(BakedQuad quad, double angleDegrees, boolean isAxisZ, boolean isPositiveDir) {
        int[] original = quad.getVertices();
        int[] vertices = Arrays.copyOf(original, original.length);

        double rad = Math.toRadians(angleDegrees);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double targetTipRel = 0.5 * Math.sqrt(2.0);
        double baseRel = 0.3125;
        double factor = (targetTipRel - baseRel) / 0.1875;

        for (int v = 0; v < 4; v++) {
            int idx = v * 8;
            float x = Float.intBitsToFloat(vertices[idx]);
            float y = Float.intBitsToFloat(vertices[idx + 1]);
            float z = Float.intBitsToFloat(vertices[idx + 2]);

            double dx = x - 0.5;
            double dy = y;
            double dz = z - 0.5;

            if (isAxisZ) {
                if (isPositiveDir) {
                    dz = baseRel + (z - 0.8125) * factor;
                } else {
                    dz = -baseRel + (z - 0.1875) * factor;
                }
            } else {
                if (isPositiveDir) {
                    dx = baseRel + (x - 0.8125) * factor;
                } else {
                    dx = -baseRel + (x - 0.1875) * factor;
                }
            }

            double nx = 0.5 + (dx * cos - dz * sin);
            double nz = 0.5 + (dx * sin + dz * cos);

            vertices[idx] = Float.floatToRawIntBits((float) nx);
            vertices[idx + 1] = Float.floatToRawIntBits((float) dy);
            vertices[idx + 2] = Float.floatToRawIntBits((float) nz);
        }

        return finalizeQuad(quad, vertices, cos, sin);
    }

    private static BakedQuad finalizeQuad(BakedQuad quad, int[] vertices, double cos, double sin) {
        // Transform normal vector
        int normal = vertices[7];
        byte nx = (byte) (normal & 0xFF);
        byte ny = (byte) ((normal >> 8) & 0xFF);
        byte nz = (byte) ((normal >> 16) & 0xFF);

        double rNx;
        double rNz;
        if (nx == 0 && ny == 0 && nz == 0) {
            // Compute from vertices
            float x0 = Float.intBitsToFloat(vertices[0]);
            float y0 = Float.intBitsToFloat(vertices[1]);
            float z0 = Float.intBitsToFloat(vertices[2]);
            float x1 = Float.intBitsToFloat(vertices[8]);
            float y1 = Float.intBitsToFloat(vertices[9]);
            float z1 = Float.intBitsToFloat(vertices[10]);
            float x2 = Float.intBitsToFloat(vertices[16]);
            float y2 = Float.intBitsToFloat(vertices[17]);
            float z2 = Float.intBitsToFloat(vertices[18]);

            float e1x = x1 - x0, e1y = y1 - y0, e1z = z1 - z0;
            float e2x = x2 - x0, e2y = y2 - y0, e2z = z2 - z0;
            rNx = e1y * e2z - e1z * e2y;
            ny = (byte) ((e1z * e2x - e1x * e2z) * 127.0f);
            rNz = e1x * e2y - e1y * e2x;
        } else {
            rNx = nx * cos - nz * sin;
            rNz = nx * sin + nz * cos;
        }

        int newNormal = ((int) ((byte) rNx) & 0xFF)
                | (((int) ny & 0xFF) << 8)
                | (((int) ((byte) rNz) & 0xFF) << 16);

        for (int v = 0; v < 4; v++) {
            vertices[v * 8 + 7] = newNormal;
        }

        Direction dominantDir = Direction.getNearest((float) rNx, (float) ny / 127.0f, (float) rNz);
        return new BakedQuad(vertices, quad.getTintIndex(), dominantDir, quad.getSprite(), quad.isShade());
    }
}

package octree;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class OctreeQuantization {

    private static class OctreeNode {
        private double redSum;
        private double greenSum;
        private double blueSum;
        private int pixelCount;
        private OctreeNode[] children;

        public OctreeNode() {
            this.redSum = 0;
            this.greenSum = 0;
            this.blueSum = 0;
            this.pixelCount = 0;
            this.children = new OctreeNode[8];
        }

        public void addToNode(Color color, int level) {
            if (level == 8) {
                this.redSum += color.getRed();
                this.greenSum += color.getGreen();
                this.blueSum += color.getBlue();
                this.pixelCount++;
            } else {
                int index = getOctant(color, level);
                if (this.children[index] == null) {
                    this.children[index] = new OctreeNode();
                }
                this.children[index].addToNode(color, level + 1);
            }
        }

        private List<Color> getPalette(int colorCount, List<Color> palette) {
            if (this.pixelCount > 0) {
                double redAvg = this.redSum / this.pixelCount;
                double greenAvg = this.greenSum / this.pixelCount;
                double blueAvg = this.blueSum / this.pixelCount;
                palette.add(new Color((int) redAvg, (int) greenAvg, (int) blueAvg));
            }
            if (palette.size() < colorCount) {
                for (OctreeNode child : this.children) {
                    if (child != null) {
                        child.getPalette(colorCount, palette);
                    }
                }
            }
            return palette;
        }

        private int getOctant(Color color, int level) {
            int octant = 0;
            if ((color.getRed() & (1 << (7 - level))) != 0) {
                octant |= 4;
            }
            if ((color.getGreen() & (1 << (7 - level))) != 0) {
                octant |= 2;
            }
            if ((color.getBlue() & (1 << (7 - level))) != 0) {
                octant |= 1;
            }
            return octant;
        }
    }

    public static BufferedImage quantizeImage(BufferedImage image, int colorCount) {
        OctreeNode root = buildOctree(image);

        // Build palette
        List<Color> palette = new ArrayList<>();
        getPaletteColors(root, palette, colorCount);

        // Map colors to palette
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color oldColor = new Color(image.getRGB(x, y));
                Color newColor = getClosestColor(oldColor, palette);
                result.setRGB(x, y, newColor.getRGB());
            }
        }
        return result;
    }

    private static OctreeNode buildOctree(BufferedImage image) {
        OctreeNode root = new OctreeNode();

        // Build octree
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));
                root.addToNode(color, 0);
            }
        }

        return root;
    }

    private static void getPaletteColors(OctreeNode node, List<Color> palette, int colorCount) {
        if (node.pixelCount > 0) {
            double redAvg = node.redSum / node.pixelCount;
            double greenAvg = node.greenSum / node.pixelCount;
            double blueAvg = node.blueSum / node.pixelCount;
            palette.add(new Color((int) redAvg, (int) greenAvg, (int) blueAvg));
        }
        if (palette.size() < colorCount) {
            for (OctreeNode child : node.children) {
                if (child != null) {
                    getPaletteColors(child, palette, colorCount);
                }
            }
        }
    }

    private static Color getClosestColor(Color color, List<Color> palette) {
        Color closestColor = palette.get(0);
        int closestDistance = getColorDistance(color, closestColor);
        for (Color paletteColor : palette) {
            int distance = getColorDistance(color, paletteColor);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestColor = paletteColor;
            }
        }
        return closestColor;
    }

    private static int getColorDistance(Color c1, Color c2) {
        int redDiff = c1.getRed() - c2.getRed();
        int greenDiff = c1.getGreen() - c2.getGreen();
        int blueDiff = c1.getBlue() - c2.getBlue();
        return redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff;
    }
}
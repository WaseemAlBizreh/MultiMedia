package median_cut;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MedianCutColorQuantization {

    public static List<Color> quantizeImage(BufferedImage image, int numColors) {
        List<Color> colorPalette = new ArrayList<>();

        // Step 1: Create an initial cube containing all the colors in the image
        ColorCube initialCube = new ColorCube(image, 0, image.getWidth() - 1, 0, image.getHeight() - 1);

        // Step 2: Create a list to hold the color cubes
        List<ColorCube> colorCubes = new ArrayList<>();
        colorCubes.add(initialCube);

        // Step 3: Split cubes until desired number of colors is reached
        while (colorCubes.size() < numColors) {
            // Find the cube with the largest volume
            ColorCube largestCube = colorCubes.stream()
                    .max(Comparator.comparingInt(ColorCube::getVolume))
                    .orElse(null);

            if (largestCube == null) {
                break; // No more cubes to split
            }

            // Split the largest cube into two smaller cubes
            ColorCube[] splitCubes = largestCube.split();

            colorCubes.remove(largestCube);
            colorCubes.add(splitCubes[0]);
            colorCubes.add(splitCubes[1]);
        }

        // Step 4: Assign representative colors to cubes
        for (ColorCube cube : colorCubes) {
            colorPalette.add(cube.getRepresentativeColor());
        }

        return colorPalette;
    }

    private static class ColorCube {
        private BufferedImage image;
        private int minX, maxX, minY, maxY;
        private int[] colorHistogram;

        public ColorCube(BufferedImage image, int minX, int maxX, int minY, int maxY) {
            this.image = image;
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
            this.colorHistogram = new int[256 * 256 * 256];

            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    int pixelRGB = image.getRGB(x, y);
                    int r = (pixelRGB >> 16) & 0xFF;
                    int g = (pixelRGB >> 8) & 0xFF;
                    int b = pixelRGB & 0xFF;
                    int colorIndex = (r << 16) | (g << 8) | b;
                    colorHistogram[colorIndex]++;
                }
            }
        }

        public int getVolume() {
            return (maxX - minX + 1) * (maxY - minY + 1) * (colorHistogram.length / 256);
        }

        public ColorCube[] split() {
            int splitIndex = findSplitIndex();
            int splitRGB = splitIndexToRGB(splitIndex);

            ColorCube cube1 = new ColorCube(image, minX, maxX, minY, maxY);
            ColorCube cube2 = new ColorCube(image, minX, maxX, minY, maxY);

            if (splitRGB < 0) {
                cube1.maxX = (minX + maxX) / 2;
                cube2.minX = (minX + maxX) / 2 + 1;
            } else if (splitRGB < 1) {
                cube1.maxY = (minY + maxY) / 2;
                cube2.minY = (minY + maxY) / 2 + 1;
            } else {
                cube1.maxX = (minX + maxX) / 2;
                cube2.minX = (minX + maxX) / 2 + 1;
            }

            return new ColorCube[]{cube1, cube2};
        }

        private int findSplitIndex() {
            int sum = 0;
            int pixelCount = (maxX - minX + 1) * (maxY - minY + 1) * (colorHistogram.length / 256) / 2;
            int splitIndex = 0;

            for (int i = 0; i < colorHistogram.length; i++) {
                sum += colorHistogram[i];
                if (sum > pixelCount) {
                    splitIndex = i;
                    break;
                }
            }

            return splitIndex;
        }

        private int splitIndexToRGB(int splitIndex) {
            int r = (splitIndex >> 16) & 0xFF;
            int g = (splitIndex >> 8) & 0xFF;
            int b = splitIndex & 0xFF;
            return (r << 16) | (g << 8) | b;
        }

        public Color getRepresentativeColor() {
            int sumR = 0, sumG = 0, sumB = 0, count = 0;

            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    int pixelRGB = image.getRGB(x, y);
                    int r = (pixelRGB >> 16) & 0xFF;
                    int g = (pixelRGB >> 8) & 0xFF;
                    int b = pixelRGB & 0xFF;
                    sumR += r;
                    sumG += g;
                    sumB += b;
                    count++;
                }
            }

            int avgR = sumR / count;
            int avgG = sumG / count;
            int avgB = sumB / count;

            return new Color(avgR, avgG, avgB);
        }
    }
}

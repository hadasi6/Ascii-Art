package image;

import java.awt.*;
import java.util.Objects;

public class SubImageManager {

    private static final int MAX_RGB = 255;

    private Image paddedImageCache; // Cache for the padded image
    private Image[][] subImagesCache; // Cache for the sub-images
    private int lastResolution; // Last used resolution

    public SubImageManager(Image paddedImage) {
        this.paddedImageCache = Objects.requireNonNull(paddedImage, "Padded image cannot be null");
        this.subImagesCache = null;
        this.lastResolution = -1; // No resolution initially
    }

    public Image[][] getSubImages(int numCharsInRow) {
        if (subImagesCache == null || lastResolution != numCharsInRow) {
            subImagesCache = divideImageIntoSubImages(paddedImageCache, numCharsInRow);
            lastResolution = numCharsInRow;
        }
        return subImagesCache;
    }

    private Image[][] divideImageIntoSubImages(Image paddedImage, int numCharsInRow) {
        int imageWidth = paddedImage.getWidth();
        int imageHeight = paddedImage.getHeight();
        int squareSize = imageWidth / numCharsInRow;
        int numRows = imageHeight / squareSize;

        Image[][] subImages = new Image[numRows][numCharsInRow];

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCharsInRow; col++) {
                int startRow = row * squareSize;
                int startCol = col * squareSize;
                subImages[row][col] = extractSubImage(paddedImage, startRow, squareSize, startCol,
                        squareSize);
            }
        }
        return subImages;
    }

    private Image extractSubImage(Image image, int startRow, int height, int startCol, int width) {
        Color[][] subImagePixels = new Color[height][width];
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                subImagePixels[r][c] = image.getPixel(startRow + r, startCol + c);
            }
        }
        return new Image(subImagePixels, width, height);
    }

    /**
     * Calculates the brightness of image
     *
     * @param image The 2D array of square sub-images.
     * @return A 2D array of brightness values corresponding to each sub-image.
     */
    public double calculateBrightness(Image image) {
        int numRows = image.getHeight();
        int numCols = image.getWidth();

        double sumBrightness = 0;

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                Color pixelColor = image.getPixel(row, col);
                double greyPixel =
                        pixelColor.getRed() * 0.2126 + pixelColor.getGreen() * 0.7152 +
                                pixelColor.getBlue() * 0.0722;
                sumBrightness += greyPixel;
            }
        }
        return sumBrightness / (numRows * numCols) / MAX_RGB;
    }
}

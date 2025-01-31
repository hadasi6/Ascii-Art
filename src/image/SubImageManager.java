package image;

import java.awt.*;
import java.util.Objects;


/**
 * The SubImageManager class is responsible for managing sub-images of a padded image.
 * It includes methods to divide an image into sub-images and calculate the brightness of an image.
 *
 * @ Author: Hadas Elezra
 */
public class SubImageManager {

    // Constants
    private static final int MAX_RGB = 255; // Maximum value for RGB
    private static final double RED_COEFFICIENT = 0.2126; // Coefficient for red channel
    private static final double GREEN_COEFFICIENT = 0.7152; // Coefficient for green channel
    private static final double BLUE_COEFFICIENT = 0.0722; // Coefficient for blue channel
    private static SubImageManager instance; // Singleton instance

    // Fields
    private final Image paddedImageCache; // Cache for the padded image
    private Image[][] subImagesCache; // Cache for the sub-images
    private int lastResolution; // Last used resolution

    /**
     * Private constructor to prevent instantiation from outside the class.
     *
     * @param paddedImage The padded image to be managed.
     */
    private SubImageManager(Image paddedImage) {
        this.paddedImageCache = Objects.requireNonNull(paddedImage, "Padded image cannot be null");
        this.subImagesCache = null;
        this.lastResolution = -1; // No resolution initially
    }

    /**
     * Returns the singleton instance of SubImageManager.
     *
     * @param paddedImage The padded image to be managed.
     * @return The singleton instance of SubImageManager.
     */
    public static SubImageManager getInstance(Image paddedImage) { // Added static getInstance method
        if (instance == null) {
            instance = new SubImageManager(paddedImage);
        }
        return instance;
    }

    /**
     * Returns the sub-images of the padded image.
     *
     * @param numCharsInRow The number of characters in a row.
     * @return A 2D array of sub-images.
     */
    public Image[][] getSubImages(int numCharsInRow) {
        if (subImagesCache == null || lastResolution != numCharsInRow) {
            subImagesCache = divideImageIntoSubImages(paddedImageCache, numCharsInRow);
            lastResolution = numCharsInRow;
        }
        return subImagesCache;
    }

    /**
     * Divides the padded image into sub-images.
     *
     * @param paddedImage   The padded image to be divided.
     * @param numCharsInRow The number of characters in a row.
     * @return A 2D array of sub-images.
     */
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

    /**
     * Extracts a sub-image from the padded image.
     *
     * @param image    The padded image.
     * @param startRow The starting row of the sub-image.
     * @param height   The height of the sub-image.
     * @param startCol The starting column of the sub-image.
     * @param width    The width of the sub-image.
     * @return The extracted sub-image.
     */
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
     * Calculates the brightness of an image.
     *
     * @param image The image to calculate the brightness of.
     * @return The brightness value of the image.
     */
    public double calculateBrightness(Image image) {
        int numRows = image.getHeight();
        int numCols = image.getWidth();

        double sumBrightness = 0;

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                Color pixelColor = image.getPixel(row, col);
                double greyPixel =
                        pixelColor.getRed() * RED_COEFFICIENT + pixelColor.getGreen() * GREEN_COEFFICIENT +
                                pixelColor.getBlue() * BLUE_COEFFICIENT;
                sumBrightness += greyPixel;
            }
        }
        return sumBrightness / (numRows * numCols) / MAX_RGB;
    }
}

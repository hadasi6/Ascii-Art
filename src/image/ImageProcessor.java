package image;

import java.awt.*;

/**
 * A utility class for processing images.
 * Provides functionalities like padding an image to the nearest power of 2
 * while centering the original image in the padded space.
 */
public class ImageProcessor {

    // Private constructor to prevent instantiation
    private ImageProcessor() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Pads the given image to the nearest power of 2 dimensions and centers it.
     *
     * @param pixelArray The pixel array of the original image.
     * @param width The width of the original image.
     * @param height The height of the original image.
     * @return A new Image object representing the padded image with the original centered.
     */
    public static Image padImageToPowerOfTwo(Color[][] pixelArray, int width, int height) {
        // Calculate the new dimensions (next power of 2)
        int paddedWidth = nextPowerOfTwo(width);
        int paddedHeight = nextPowerOfTwo(height);

        // Create a new pixel array for the padded image
        Color[][] paddedPixelArray = new Color[paddedHeight][paddedWidth];

        // Calculate the starting position to center the original image
        int startRow = (paddedHeight - height) / 2;
        int startCol = (paddedWidth - width) / 2;

        // Copy the original pixel array into the center of the new pixel array
        for (int r = 0; r < paddedHeight; r++) {
            for (int c = 0; c < paddedWidth; c++) {
                paddedPixelArray[startRow + r][startCol + c] = new Color(pixelArray[r][c].getRed(),
                        pixelArray[r][c].getGreen(), pixelArray[r][c].getBlue()); // todo validate creating obj
            }
        }

        // Fill the new pixel array with white pixels as default
        for (int r = 0; r < startRow; r++) {
            for (int c = 0; c < paddedWidth; c++) {
                paddedPixelArray[r][c] = new Color(255, 255, 255); // White pixel
                paddedPixelArray[height + r][c] = new Color(255, 255, 255); // White pixel
            }
        }
        for (int r = startRow; r < startRow + paddedHeight; r++) {
            for (int c = 0; c < startCol; c++) {
                paddedPixelArray[r][c] = new Color(255, 255, 255); // White pixel
                paddedPixelArray[r][width + c] = new Color(255, 255, 255); // White pixel
            }
        }
        // Return a new Image object with the padded pixel array
        return new Image(paddedPixelArray, paddedWidth, paddedHeight);
    }

    /**
     * Calculates the next power of 2 greater than or equal to the given number.
     *
     * @param number The input number.
     * @return The next power of 2.
     */
    private static int nextPowerOfTwo(int number) {
        int power = 1;
        while (power < number) {
            power *= 2;
        }
        return power;
    }
}

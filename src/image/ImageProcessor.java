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
     * @param width      The width of the original image.
     * @param height     The height of the original image.
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
                        pixelArray[r][c].getGreen(), pixelArray[r][c].getBlue()); // todo validate creating
                // obj
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
     * Divides the given padded image into sub-images based on the resolution.
     *
     * @param paddedImage   The padded image to divide.
     * @param numCharsInRow The number of characters in each row of the ASCII art.
     * @return A 2D array of sub-images representing the divided image.
     */
    public static Image[][] divideImageIntoSubImages(Image paddedImage, int numCharsInRow) {
        int imageWidth = paddedImage.getWidth();
        int imageHeight = paddedImage.getHeight();

        // Calculate sub-image dimensions
        int squareSize = imageWidth / numCharsInRow;

        // Calculate the number of rows of square sub-images
        int numRows = imageHeight / squareSize;

        // Create a 2D array to hold the sub-images
        Image[][] subImages = new Image[numRows][numCharsInRow];

        // Divide the image into sub-images
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCharsInRow; col++) {
                int startRow = row * squareSize;
                int endRow = startRow + squareSize;
                int startCol = col * squareSize;
                int endCol = startCol + squareSize;

                // Extract the sub-image
                Image subImage = extractSubImage(paddedImage,
                        startRow, endRow, startCol, endCol);

                // Store the sub-image in the array
                subImages[row][col] = subImage;
            }
        }
        return subImages;
    }

    /**
     * Calculates the brightness of image
     *
     * @param image The 2D array of square sub-images.
     * @return A 2D array of brightness values corresponding to each sub-image.
     */
    public static double calculateBrightness(Image image) {
        int numRows = image.getWidth();
        int numCols = image.getHeight();

        double sumBrightness = 0;

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                Color pixelColor = image.getPixel(row, col);
                double greyPixel =
                        pixelColor.getRed() * 0.2126 + pixelColor.getGreen() * 0.7152 + pixelColor.getBlue() * 0.0722;
                sumBrightness += greyPixel;
            }
        }
        return sumBrightness / (numRows * numCols);
    }


    /**
     * Extracts a sub-image from the given image based on the specified bounds.
     *
     * @param image    The original image.
     * @param startRow The starting row index.
     * @param endRow   The ending row index.
     * @param startCol The starting column index.
     * @param endCol   The ending column index.
     * @return A new Image object representing the sub-image.
     */
    private static Image extractSubImage(Image image, int startRow, int endRow, int startCol, int endCol) {
        int squareSize = endRow - startRow; // גודל הריבוע
        Color[][] subImagePixels = new Color[squareSize][squareSize];

        for (int r = 0; r < squareSize; r++) {
            for (int c = 0; c < squareSize; c++) {
                // Use getPixel to access the original image's pixels
                subImagePixels[r][c] = image.getPixel(startRow + r, startCol + c);
            }
        }
        return new Image(subImagePixels, squareSize, squareSize);
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

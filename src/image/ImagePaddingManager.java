package image;

import java.awt.*;

/**
 * The ImagePaddingManager class is responsible for padding an image with white pixels
 * so that its dimensions are powers of two.
 * @ Author: Hadas Elezra
 */
public class ImagePaddingManager {

    /**
     * The color used for padding the image.
     */
    private static final Color PADDING_COLOR = new Color(255, 255, 255);

    // Private constructor to prevent instantiation
    private ImagePaddingManager() {
    }

    /**
     * Pads the given image with white pixels so that its dimensions are powers of two.
     *
     * @param image the image to be padded
     * @return a new padded image with dimensions that are powers of two
     * @throws IllegalArgumentException if the input image is null
     */
    public static Image padImageToPowerOfTwo(Image image) {
        if (image == null) {
            throw new IllegalArgumentException("Input image cannot be null");
        }
        int paddedWidth = nextPowerOfTwo(image.getWidth());
        int paddedHeight = nextPowerOfTwo(image.getHeight());

        Color[][] paddedPixelArray = new Color[paddedHeight][paddedWidth];
        int startRow = (paddedHeight - image.getHeight()) / 2;
        int startCol = (paddedWidth - image.getWidth()) / 2;

        // Initialize the padded array with white pixels
        for (int row = 0; row < paddedHeight; row++) {
            for (int col = 0; col < paddedWidth; col++) {
                paddedPixelArray[row][col] = new Color(PADDING_COLOR.getRGB());
            }
        }

        // Copy the original image pixels into the padded array
        for (int r = 0; r < image.getHeight(); r++) {
            for (int c = 0; c < image.getWidth(); c++) {
                Color pixel = image.getPixel(r, c);
                paddedPixelArray[startRow + r][startCol + c] = new Color(pixel.getRed(), pixel.getGreen(),
                        pixel.getBlue());
            }
        }
        return new Image(paddedPixelArray, paddedWidth, paddedHeight);
    }

    /**
     * Calculates the next power of two greater than or equal to the given number.
     *
     * @param number the number to find the next power of two for
     * @return the next power of two greater than or equal to the given number
     */
    private static int nextPowerOfTwo(int number) {
        int power = 1;
        while (power < number) {
            power *= 2;
        }
        return power;
    }
}

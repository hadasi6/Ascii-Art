package image;

import java.awt.*;

public class ImagePaddingManager {

    private ImagePaddingManager() {}

    public static Image padImageToPowerOfTwo(Image image) {
        int paddedWidth = nextPowerOfTwo(image.getWidth());
        int paddedHeight = nextPowerOfTwo(image.getHeight());

        Color[][] paddedPixelArray = new Color[paddedHeight][paddedWidth];
        int startRow = (paddedHeight - image.getHeight()) / 2;
        int startCol = (paddedWidth - image.getWidth()) / 2;

        for (int row = 0; row < paddedHeight; row++) {
            for (int col = 0; col < paddedWidth; col++) {
                paddedPixelArray[row][col] = new Color(255, 255, 255);
            }
        }

        for (int r = 0; r < image.getHeight(); r++) {
            for (int c = 0; c < image.getWidth(); c++) {
                Color pixel = image.getPixel(r, c);
                paddedPixelArray[startRow + r][startCol + c] = new Color(pixel.getRed(), pixel.getGreen(),
                        pixel.getBlue());
            }
        }
        return new Image(paddedPixelArray, paddedWidth, paddedHeight);
    }


    private static int nextPowerOfTwo(int number) {
        int power = 1;
        while (power < number) {
            power *= 2;
        }
        return power;
    }
}

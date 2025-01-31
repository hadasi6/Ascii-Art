package ascii_art;

import image.Image;
import image.SubImageManager;
import image_char_matching.SubImgCharMatcher;

import java.util.Set;

/**
 * The AsciiArtAlgorithm class is responsible for converting an image into ASCII art.
 * It divides the image into sub-images, calculates the brightness of each sub-image,
 * and matches the brightness to a character from a given character set.
 *
 * @ Author: Hadas Elezra
 */
public class AsciiArtAlgorithm {

    // Fields
    private final Image image;// The input image
    private final int resolution; // The resolution for dividing the image into sub-images
    private final Set<Character> charset; // The set of characters to use for ASCII art
    private final SubImgCharMatcher matcher; // The matcher for matching brightness to characters
    private final SubImageManager subImageManager; // The manager for handling sub-images

    /**
     * Constructs an AsciiArtAlgorithm with the given parameters.
     *
     * @param imageInput      The input image to be converted to ASCII art.
     * @param resolution      The resolution for dividing the image into sub-images.
     * @param charset         The set of characters to use for ASCII art.
     * @param matcher         The matcher for matching brightness to characters.
     * @param subImageManager The manager for handling sub-images.
     */
    public AsciiArtAlgorithm(Image imageInput, int resolution, Set<Character> charset,
                             SubImgCharMatcher matcher, SubImageManager subImageManager) {
        this.image = imageInput;
        this.resolution = resolution;
        this.charset = charset;
        this.matcher = matcher;
        this.subImageManager = subImageManager;
    }

    /**
     * Runs the ASCII art algorithm.
     * Divides the image into sub-images, calculates the brightness of each sub-image,
     * and matches the brightness to a character from the character set.
     *
     * @return A 2D array of characters representing the ASCII art.
     */
    public char[][] run() {

        Image[][] subImages = subImageManager.getSubImages(resolution); // Use the instance method
        int numRows = image.getHeight() / (image.getWidth() / resolution);

        char[][] chars = new char[subImages.length][subImages[0].length];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < resolution; col++) {
                double subImageBrightness = this.subImageManager.calculateBrightness(subImages[row][col]);
                chars[row][col] = matcher.getCharByImageBrightness(subImageBrightness);
            }
        }
        return chars;
    }
}

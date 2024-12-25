package ascii_art;

import image.Image;
import image.SubImageManager;
import image_char_matching.SubImgCharMatcher;
import java.util.Set;

public class AsciiArtAlgorithm {

    private Image image; //todo - final???
    private int resolution;
    private Set<Character> chars;

    private SubImgCharMatcher matcher;
    private SubImageManager subImageManager;
    private String roundingMethod; //todo - not in api

    public AsciiArtAlgorithm(Image imageInput, int resolution, Set<Character> charset,
                             SubImgCharMatcher matcher, SubImageManager subImageManager) {
        this.image = imageInput;
        this.resolution = resolution;
        this.chars = charset;
        this.matcher = matcher;
        this.subImageManager = subImageManager;
    }

    public char[][] run() {

        Image[][] subImages = subImageManager.getSubImages(resolution);
        int numRows = image.getHeight() / (image.getWidth() / resolution);

        char[][] chars = new char[subImages.length][subImages[0].length];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < resolution; col++) {
                double subImageBrightness = this.subImageManager.calculateBrightness(subImages[row][col]);
                //todo put here the code that supports rounding

                chars[row][col] = matcher.getCharByImageBrightness(subImageBrightness);
            }
        }
        return chars;
    }
}

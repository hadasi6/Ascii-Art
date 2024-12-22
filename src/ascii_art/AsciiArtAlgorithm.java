package ascii_art;

import ascii_output.ConsoleAsciiOutput.*;


import image.Image;
import image.ImageProcessor;
import image_char_matching.SubImgCharMatcher;

import ascii_output.ConsoleAsciiOutput;

import java.io.IOException;

public class AsciiArtAlgorithm {

    private Image image; //todo - final???
    private int resolution;
    private char[] chars;

    public AsciiArtAlgorithm(Image imageInput, int resolution, char[] charset) {
        this.image = imageInput;
        this.resolution = resolution;
        this.chars = charset;
    }

    public char [][] run() {
        Image paddedImage = ImageProcessor.padImageToPowerOfTwo(image);
        Image[][] subImages = ImageProcessor.divideImageIntoSubImages(image, resolution);

        //instance
        SubImgCharMatcher imgCharMatcher = new SubImgCharMatcher(chars);

//        int numRows = imageHeight / (imageWidth / numCharsInRow);
        int numRows = paddedImage.getHeight()/(paddedImage.getWidth()/resolution);

        char[][] chars = new char[numRows][resolution];
        for (int row=0; row < numRows; row++) {
            for (int col=0; col < resolution; col++) {
                double subImageBrightness = ImageProcessor.calculateBrightness(subImages[row][col]);
                chars[row][col] = imgCharMatcher.getCharByImageBrightness(subImageBrightness);
            }
        }
        return chars;
    }
}

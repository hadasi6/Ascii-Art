package image_char_matching;

import java.util.*;

/**
 * The SubImgCharMatcher class is responsible for matching characters to image brightness levels.
 * It maintains a set of characters and their corresponding brightness values, and provides methods
 * to add or remove characters, set the rounding method, and get a character based on image brightness.
 *
 * @ Author: Hadas Elezre
 */
public class SubImgCharMatcher {

    /**
     * The set of characters used for matching.
     */
    private final Set<Character> charSet;

    /**
     * A map of characters to their brightness values.
     */
    private final Map<Character, Double> brightnessMap;
    /**
     * A map of normalized brightness values to lists of characters.
     */
    private final TreeMap<Double, List<Character>> normalizedBrightnessMap;

    /**
     * The rounding method used for matching characters to brightness values.
     */
    private String roundingMethod;

    //Default values
    private static final String DEFAULT_ROUND = "abs"; // Default rounding method
    private static final int DEFAULT_PIXEL_RESOLUTION = 16; // Default pixel resolution


    //api
    /**
     * Constructs a SubImgCharMatcher with the given character set.
     *
     * @param charset the array of characters to be used for matching
     */
    public SubImgCharMatcher(char[] charset) {
        this.charSet = new HashSet<>();
        for (char c : charset) {
            charSet.add(c);
        }
        this.roundingMethod = DEFAULT_ROUND;
        this.brightnessMap = new HashMap<>();
        this.normalizedBrightnessMap = new TreeMap<>();
        calculateBrightness();
    }

    /**
     * Calculates the brightness for each character in the character set.
     */
    private void calculateBrightness() {
        for (char c : charSet) {
            boolean[][] boolArray = CharConverter.convertToBoolArray(c);
            double brightness = calculateArrayBrightness(boolArray);
            brightnessMap.put(c, brightness);
        }
        normalizeBrightness();
    }

    /**
     * Normalizes the brightness values of the characters.
     */
    private void normalizeBrightness() {
        if (brightnessMap.isEmpty()) {
            return;
        }
        double minBrightness = Collections.min(brightnessMap.values());
        double maxBrightness = Collections.max(brightnessMap.values());

        normalizedBrightnessMap.clear();
        double brightnessRange = maxBrightness - minBrightness;
        for (Map.Entry<Character, Double> entry : brightnessMap.entrySet()) {
            double normalizedBrightness = (entry.getValue() - minBrightness) / brightnessRange;
            normalizedBrightnessMap
                    .computeIfAbsent(normalizedBrightness, k -> new ArrayList<>())
                    .add(entry.getKey());
        }
        for (List<Character> chars : normalizedBrightnessMap.values()) {
            chars.sort(Comparator.naturalOrder());
        }
    }

    /**
     * Calculates the brightness of a boolean array representing a character.
     *
     * @param boolArray the boolean array representing the character
     * @return the brightness value of the character
     */
    private double calculateArrayBrightness(boolean[][] boolArray) {
        int litPixels = 0;
        for (boolean[] row : boolArray) {
            for (boolean pixel : row) {
                if (pixel) {
                    litPixels++;
                }
            }
        }
        return (double) litPixels / DEFAULT_PIXEL_RESOLUTION;
    }

    /**
     * Gets a character based on the given image brightness.
     *
     * @param brightness the brightness value of the image
     * @return the character that best matches the brightness value
     */
    public char getCharByImageBrightness(double brightness) {
        Map.Entry<Double, List<Character>> lower = normalizedBrightnessMap.floorEntry(brightness);
        Map.Entry<Double, List<Character>> higher = normalizedBrightnessMap.ceilingEntry(brightness);

        if (lower == null) {
            return higher.getValue().get(0);
        }
        if (higher == null) {
            return lower.getValue().get(0);
        }

        switch (roundingMethod) {
            case "up":
                return higher.getValue().get(0);
            case "down":
                return lower.getValue().get(0);
            default: // "abs"
                double diffLower = Math.abs(brightness - lower.getKey());
                double diffHigher = Math.abs(brightness - higher.getKey());
                return (diffLower <= diffHigher) ? lower.getValue().get(0) : higher.getValue().get(0);
        }
    }

    /**
     * Adds a character to the character set and updates the brightness values.
     *
     * @param c the character to be added
     */
    public void addChar(char c) {
        if (!charSet.contains(c)) {
            charSet.add(c);
            boolean[][] boolArray = CharConverter.convertToBoolArray(c);
            double brightness = calculateArrayBrightness(boolArray);
            brightnessMap.put(c, brightness);
            normalizeBrightness();
        }
    }

    //api
    /**
     * Removes a character from the character set and updates the brightness values.
     *
     * @param c the character to be removed
     */
    public void removeChar(char c) {
        if (brightnessMap.containsKey(c)) {
            brightnessMap.remove(c);
            charSet.remove(c);
            normalizeBrightness();
        }
    }

    /**
     * Sets the rounding method for matching characters to brightness values.
     *
     * @param method the rounding method ("up", "down", or "abs")
     */
    public void setRoundingMethod(String method) {
        if (method.equals("up") || method.equals("down") || method.equals("abs")) {
            this.roundingMethod = method;
        } else {
            throw new IllegalArgumentException("Invalid rounding method: " + method);
        }
    }

    /**
     * Gets the character set.
     *
     * @return the character set
     */
    public Set<Character> getCharSet() {
        return charSet;
    }

}

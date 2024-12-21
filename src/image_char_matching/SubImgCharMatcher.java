package image_char_matching;

import java.util.*;

public class SubImgCharMatcher {

    public char[] charSet;
    private Map<Character, Double> brightnessMap;
    private TreeMap<Double, List<Character>> normalizedBrightnessMap;

    //api
    public SubImgCharMatcher(char[] charset) {
        this.charSet = charset; //todo - sort?
        this.brightnessMap = new HashMap<>();
        this.normalizedBrightnessMap = new TreeMap<>();
        calculateBrightness();
    }

    private void calculateBrightness() {
        for (char c: charSet) {
            boolean[][] boolArray = CharConverter.convertToBoolArray(c);
            double brightness = calculateArrayBrightness(boolArray);
            brightnessMap.put(c, brightness);
        }
        normalizeBrightness();
    }

    private void normalizeBrightness() {
        double minBrightness = Collections.min(brightnessMap.values());
        double maxBrightness = Collections.max(brightnessMap.values());

        normalizedBrightnessMap.clear();

        for (Map.Entry<Character, Double> entry: brightnessMap.entrySet()) {
            double normalizedBrightness = (entry.getValue()-minBrightness) / (maxBrightness-minBrightness);
            normalizedBrightnessMap
                    .computeIfAbsent(normalizedBrightness, k -> new ArrayList<>())
                    .add(entry.getKey());
        }
        for (List<Character> chars : normalizedBrightnessMap.values()) {
            chars.sort(Comparator.naturalOrder());
        }
    }

    private double calculateArrayBrightness(boolean[][] boolArray) {
        int totalPixels = boolArray.length*boolArray[0].length;
        int litPixels = 0;
        for (boolean[] row : boolArray) {
            for (boolean pixel : row) {
                if (pixel) {
                    litPixels++;
                }
            }
        }
        return (double) litPixels / (double) totalPixels; //todo - delete double?
    }

    //api
    public char getCharByImageBrightness(double brightness) {
        Map.Entry<Double, List<Character>> lower = normalizedBrightnessMap.floorEntry(brightness);
        Map.Entry<Double, List<Character>> higher = normalizedBrightnessMap.ceilingEntry(brightness);

        if (lower==null) {
            return higher.getValue().get(0);
        }
        if (higher==null) {
            return lower.getValue().get(0);
        }

        double diffLower = Math.abs(brightness-lower.getKey());
        double diffHigher = Math.abs(brightness-higher.getKey());

        return (diffLower <= diffHigher) ? lower.getValue().get(0) : higher.getValue().get(0);
    }

    //api
    public void addChar(char c) {
        if (!brightnessMap.containsKey(c)) { // הוספה: בדיקה אם התו כבר קיים
            boolean[][] boolArray = CharConverter.convertToBoolArray(c);
            double brightness = calculateArrayBrightness(boolArray);
            brightnessMap.put(c, brightness);
            normalizeBrightness(); // הוספה: חישוב מחדש של הערכים המנורמלים
        }

    }

    //api
    public void removeChar(char c) {
        if (brightnessMap.containsKey(c)) { // הוספה: בדיקה אם התו קיים
            brightnessMap.remove(c);
            normalizeBrightness(); // הוספה: חישוב מחדש של הערכים המנורמלים
        }
    }

//    public char[][] chooseChars(int numCharsInRow, Character[] charSet)



}

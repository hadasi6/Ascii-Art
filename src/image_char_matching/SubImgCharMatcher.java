package image_char_matching;

import java.util.*;

public class SubImgCharMatcher {

    private Set<Character> charSet;
    private Map<Character, Double> brightnessMap;
    private TreeMap<Double, List<Character>> normalizedBrightnessMap;

    private String roundingMethod;

    private static final String DEFAULT_ROUND = "abs"; // default todo - validate explain
    private static final int DEFAULT_PIXEL_RESOLUTION = 16;
//    private final HashMap<Character, Double> cache = new HashMap<>();

    //api
    public SubImgCharMatcher(char[] charset) {
        this.roundingMethod = DEFAULT_ROUND;
        this.charSet = new HashSet<>(); /* todo - sort? */
        for (char c : charset) {
            charSet.add(c);
        }
        this.brightnessMap = new HashMap<>();
        this.normalizedBrightnessMap = new TreeMap<>();
        calculateBrightness();
    }

    private void calculateBrightness() {
        for (char c : charSet) {
            boolean[][] boolArray = CharConverter.convertToBoolArray(c);
            double brightness = calculateArrayBrightness(boolArray);
            brightnessMap.put(c, brightness);
        }
        normalizeBrightness();
    }

    private void normalizeBrightness() {
        if (brightnessMap.isEmpty()) {
            return;
        }
        double minBrightness = Collections.min(brightnessMap.values());
        double maxBrightness = Collections.max(brightnessMap.values());

        normalizedBrightnessMap.clear();
        double normVal = maxBrightness - minBrightness; //todo - change name
        for (Map.Entry<Character, Double> entry : brightnessMap.entrySet()) {
            double normalizedBrightness = (entry.getValue() - minBrightness) / normVal;
            normalizedBrightnessMap
                    .computeIfAbsent(normalizedBrightness, k -> new ArrayList<>())
                    .add(entry.getKey());
        }
        for (List<Character> chars : normalizedBrightnessMap.values()) {
            chars.sort(Comparator.naturalOrder());
        }
    }

    private double calculateArrayBrightness(boolean[][] boolArray) {
//        int totalPixels = boolArray.length*boolArray[0].length;
        int litPixels = 0;
        for (boolean[] row : boolArray) {
            for (boolean pixel : row) {
                if (pixel) {
                    litPixels++;
                }
            }
        }
        return (double) litPixels / DEFAULT_PIXEL_RESOLUTION; //todo - delete double?
    }

    //api
    public char getCharByImageBrightness(double brightness) {
        Map.Entry<Double, List<Character>> lower = normalizedBrightnessMap.floorEntry(brightness);
        Map.Entry<Double, List<Character>> higher = normalizedBrightnessMap.ceilingEntry(brightness);

        if (lower == null) {
            return higher.getValue().get(0);
        }
        if (higher == null) {
            return lower.getValue().get(0);
        }

        // בחירת תו לפי שיטת העיגול שנבחרה
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

    //api
    public void addChar(char c) {
        if (!charSet.contains(c)) { // הוספה: בדיקה אם התו כבר קיים
            charSet.add(c);
            boolean[][] boolArray = CharConverter.convertToBoolArray(c);
            double brightness = calculateArrayBrightness(boolArray);
            brightnessMap.put(c, brightness);

            //todo - add this as attributes?
            double minBrightness = Collections.min(brightnessMap.values());
            double maxBrightness = Collections.max(brightnessMap.values());

            normalizeBrightness();
            //todo - validate adding
//            if (brightness<minBrightness || brightness>maxBrightness) {
//                normalizeBrightness();
//            } else {
//                double normalizedBrightness = (brightness-minBrightness) / (maxBrightness-minBrightness);
//                normalizedBrightnessMap
//                        .computeIfAbsent(normalizedBrightness, k -> new ArrayList<>())
//                        .add(c);
//                normalizedBrightnessMap.get(normalizedBrightness).sort(Comparator.naturalOrder());
//            }
            // הוספה: חישוב מחדש של הערכים המנורמלים
        }
    }

    //api
    public void removeChar(char c) { //todo validate if checking conatain in brightnessMap and contaim in
        // charset
        if (brightnessMap.containsKey(c)) { // הוספה: בדיקה אם התו קיים
            brightnessMap.remove(c);
            charSet.remove(c);

            //todo  - as in add
            normalizeBrightness(); // הוספה: חישוב מחדש של הערכים המנורמלים
        }
    }

    //todo - not in api
    public void setRoundingMethod(String method) {
        if (method.equals("up") || method.equals("down") || method.equals("abs")) {
            this.roundingMethod = method;
        } else { //todo - validate if needed
            throw new IllegalArgumentException("Invalid rounding method: " + method);
        }
    }

    //todo - not in api
    public Set<Character> getCharSet() {
        return charSet;
    }

}

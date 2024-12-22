package ascii_art;

import ascii_output.ConsoleAsciiOutput;
import image.Image;
import image_char_matching.SubImgCharMatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class Shell {

    //commands:
    private static final String COMMAND_EXIT = "exit";
    private static final String COMMAND_PREFIX = ">>> ";
    private static final String COMMAND_CHARS = "chars";
    private static final String COMMAND_ADD = "add";
    private static final String COMMAND_REMOVE = "remove";
    private static final String COMMAND_RES = "res";
    private static final String CONSOLE_COMMAND = "console";
    private static final String RENDER_COMMAND = "render";

    private static final char MIN_CHAR = 32;
    private static final char MAX_CHAR = 127;

    //default args:
    private static final int DEFAULT_RESOLUTION = 2;
    private static final String DEFAULT_ROUNDED_BRIGHTNESS = "abs";  //todo change
    private static final char[] DEFAULT_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};


    private int resolution;
//    private final Set<Character> characterSet;
    public Shell() {
        this.resolution = DEFAULT_RESOLUTION;
//        this.characterSet = new HashSet<>();
//        for (char c : DEFAULT_CHARS) {
//            this.characterSet.add(c);
//        }.
    }

    public void run(String imageName) {

        Image inputImage;
        try {
            inputImage = new Image(imageName);
        } catch (IOException e) {
            //todo - validate if needed to print
            return;
        }
        SubImgCharMatcher subImgCharMatcher = new SubImgCharMatcher(DEFAULT_CHARS);

        AsciiArtAlgorithm asciiArtAlgorithm = new AsciiArtAlgorithm(inputImage, resolution, DEFAULT_CHARS);

        System.out.println(COMMAND_PREFIX);
        String command = KeyboardInput.readLine();

        while (!command.equals(COMMAND_EXIT)) {
            try {
                String[] parts = command.split("\\s+", 2);
                String baseCommand = parts[0];
                String arguments = parts.length > 1 ? parts[1] : "";

                switch (baseCommand) {
                    case COMMAND_CHARS:
                        printSortedChars(subImgCharMatcher.charSet);
                        break;
                    case COMMAND_ADD:
                        handleAdd(arguments, subImgCharMatcher);
                        break;
                    case COMMAND_REMOVE:
                        handleRemove(arguments, subImgCharMatcher);
                        break;
                    case COMMAND_RES:
                        handleResolution(arguments, inputImage);
                        break;
                    case COMMAND_OUTPUT:
                        handleOutput(arguments);
                        break;
                    case COMMAND_ROUND:
                        handleRounding(arguments);
                        break;
                    case COMMAND_ASCII_ART:
                        handleAsciiArt();
                        break;
                    default:
                        throw new CommandException("Did not execute due to incorrect command.");
                }
            } catch (CommandException | CharsetException | ResolutionException e) {
                System.out.println(e.getMessage());
            }

            System.out.println(">>> ");
            command = KeyboardInput.readLine();
        }

    }

    public static void main(String[] args) {
        Shell shell = new Shell();
        shell.run(args[0]);
    }

    private void handleAdd(String arguments, SubImgCharMatcher subImgCharMatch) throws CommandException, CharsetException{
        if (arguments.isEmpty()) {
            throw new CommandException("Did not add due to incorrect format.");
        }

        if (arguments.equals("all")) {
            for (char c = MIN_CHAR; c <= MAX_CHAR; c++) {
                subImgCharMatch.addChar(c);
            }
        } else if (arguments.equals("space")) {
            subImgCharMatch.addChar(' ');
        } else if (arguments.length() == 1) {
            char c = arguments.charAt(0);
            if (c >= 32 && c <= 126) {
                subImgCharMatch.addChar(c);
            } else {
                throw new CharsetException("Character out of valid ASCII range.");
            }
        } else if (arguments.matches(".-.")) {
            char start = arguments.charAt(0);
            char end = arguments.charAt(2);
            if (start >= 32 && end >= 32 && start <= 126 && end <= 126) {
                if (start <= end) {
                    for (char c = start; c <= end; c++) {
                        subImgCharMatch.addChar(c);
                    }
                } else {
                    for (char c = start; c >= end; c--) {
                        subImgCharMatch.addChar(c);
                    }
                }
            } else {
                throw new CharsetException("Character range out of valid ASCII range.");
            }
        } else {
            throw new CommandException("Did not add due to incorrect format.");
        }
    }

    private void handleRemove(String arguments, SubImgCharMatcher subImgCharMatch) throws CommandException, CharsetException {
        if (arguments.isEmpty()) {
            throw new CommandException("Did not remove due to incorrect format.");
        }

        if (arguments.equals("all")) {
            Character[] charArray = subImgCharMatch.charSet.toArray(new Character[0]);
            for (Character c : charArray) {
                subImgCharMatch.removeChar(c);
            }
        } else if (arguments.equals("space")) {
            subImgCharMatch.removeChar(' ');
        } else if (arguments.length() == 1) {
            char c = arguments.charAt(0);
            if (c >= MIN_CHAR && c <= MAX_CHAR) {
                subImgCharMatch.removeChar(c);
            } else {
                throw new CharsetException("Character out of valid ASCII range.");
            }
        } else if (arguments.matches(".-.")) {
            char start = arguments.charAt(0);
            char end = arguments.charAt(2);
            if (start >= 32 && end >= MIN_CHAR && start <= MAX_CHAR && end <= MAX_CHAR) {
                if (start <= end) {
                    for (char c = start; c <= end; c++) {
                        subImgCharMatch.removeChar(c);
                    }
                } else {
                    for (char c = start; c >= end; c--) {
                        subImgCharMatch.removeChar(c);
                    }
                }
            } else {
                throw new CharsetException("Character range out of valid ASCII range.");
            }
        } else {
            throw new CommandException("Did not remove due to incorrect format.");
        }
    }

    private void handleResolution(String arguments, Image image, SubImgCharMatcher subImgCharMatch) throws ResolutionException, CommandException {
        int minResolution = Math.max(1, image.getWidth() / image.getHeight());
        int maxResolution = image.getWidth();

        if (arguments.isEmpty()) {
            System.out.println("Resolution set to " + resolution + ".");
        } else if (arguments.equals("up")) {
            if (resolution * 2 <= maxResolution) {
                resolution *= 2;
                System.out.println("Resolution set to " + resolution + ".");
            } else {
                throw new ResolutionException("Did not change resolution due to exceeding boundaries.");
            }
        } else if (arguments.equals("down")) {
            if (resolution / 2 >= minResolution) {
                resolution /= 2;
                System.out.println("Resolution set to " + resolution + ".");
            } else {
                throw new ResolutionException("Did not change resolution due to exceeding boundaries.");
            }
        } else {
            throw new CommandException("Did not change resolution due to incorrect format.");
        }
    }



    private void printSortedChars(Set<Character> charset) {
        TreeSet<Character> sortedSet = new TreeSet<>(charset);

        for (char c : sortedSet) {
            System.out.print(c + " ");
        }
        System.out.println();
    }
}
//}
//
//public static void main(String[] args) throws IOException {
//    Image img = new Image("board.jpeg");
//    char[] charse = new char[] {'m', 'o'};
//    AsciiArtAlgorithm alg = new AsciiArtAlgorithm(img, 2, charse);
//    char[][] output = alg.run();
//    ConsoleAsciiOutput console = new ConsoleAsciiOutput();
//    console.out(output);
//}
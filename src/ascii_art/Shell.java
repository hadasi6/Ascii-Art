package ascii_art;

import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;
import image.ImagePaddingManager;
import image.SubImageManager;
import image_char_matching.SubImgCharMatcher;

import java.io.IOException;
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
    private static final String COMMAND_ROUND = "round";

    private static final String COMMAND_OUTPUT = "output";
    private static final String COMMAND_ASCII_ART = "asciiArt";

    private static final char MIN_CHAR = 32;
    private static final char MAX_CHAR = 127;

    //default args:
    private static final int DEFAULT_RESOLUTION = 2;
    private static final String DEFAULT_ROUNDING = "abs";  //todo change
    private static final char[] DEFAULT_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    private static final Set<Character> DEFAULT_CHAR_SET = Set.of('0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9');

    private String DEFAULT_FONT = "Courier New";
    private SubImgCharMatcher charMatcher;
    private int resolution;
    private AsciiOutput output;
    //    private final Set<Character> characterSet;
    public Shell() {

        this.resolution = DEFAULT_RESOLUTION;
        this.output = new ConsoleAsciiOutput();
        this.charMatcher = new SubImgCharMatcher(DEFAULT_CHARS);
        charMatcher.setRoundingMethod(DEFAULT_ROUNDING);
    }

    public void run(String imageName) {
        Image inputImage;
        try {
            inputImage = new Image(imageName);
        } catch (IOException e) {
            //todo - validate if needed to print
            return;
        }
        Image paddedImage = ImagePaddingManager.padImageToPowerOfTwo(inputImage);

        SubImageManager subImageManager = new SubImageManager(paddedImage);

        System.out.print(COMMAND_PREFIX);
        String command = KeyboardInput.readLine();

        while (!command.equals(COMMAND_EXIT)) {

            try {
                String[] parts = command.split("\\s+", 3);
                String baseCommand = parts[0];
                String arguments = parts.length > 1 ? parts[1] : "";

                switch (baseCommand) {
                    case COMMAND_CHARS:
                        printSortedChars();
                        break;
                    case COMMAND_ADD:
                        handleAdd(arguments);
                        break;
                    case COMMAND_REMOVE:
                        handleRemove(arguments);
                        break;
                    case COMMAND_RES:
                        handleResolution(arguments, paddedImage);
                        break;
                    case COMMAND_ROUND:
                        handleRounding(arguments);
                        break;
                    case COMMAND_OUTPUT:
                        handleOutput(arguments);
                        break;
                    case COMMAND_ASCII_ART:
                        handleAsciiArt(paddedImage, subImageManager);
                        break;
                    default:
                        throw new CommandException("Did not execute due to incorrect command.");
                }
            } catch (IOException | CommandException | CharsetException | ResolutionException e) {
                System.out.println(e.getMessage());
            }
            System.out.print(COMMAND_PREFIX);
            command = KeyboardInput.readLine();

        }

    }

    public static void main(String[] args) {
        Shell shell = new Shell();
        shell.run(args[0]);
    }

    private void handleAdd(String arguments) throws CommandException, CharsetException {
        if (arguments.isEmpty()) {
            throw new CommandException("Did not add due to incorrect format.");
        }

        if (arguments.equals("all")) {
            for (char c = MIN_CHAR; c <= MAX_CHAR; c++) {
                charMatcher.addChar(c);
            }
        } else if (arguments.equals("space")) {
            charMatcher.addChar(' ');
        } else if (arguments.length() == 1) {
            char c = arguments.charAt(0);
            if (c >= MIN_CHAR && c < MAX_CHAR) {
                charMatcher.addChar(c);
            } else {
                throw new CharsetException("Character out of valid ASCII range.");
            }
        } else if (arguments.matches(".-.")) {
            char start = arguments.charAt(0);
            char end = arguments.charAt(2);
            if (start >= MIN_CHAR && end >= MIN_CHAR && start < MAX_CHAR && end < MAX_CHAR) {
                if (start <= end) {
                    for (char c = start; c <= end; c++) {
                        charMatcher.addChar(c);
                    }
                } else {
                    for (char c = start; c >= end; c--) {
                        charMatcher.addChar(c);
                    }
                }
            } else {
                throw new CharsetException("Character range out of valid ASCII range.");
            }
        } else {
            throw new CommandException("Did not add due to incorrect format.");
        }
    }

    private void handleRemove(String arguments) throws CommandException, CharsetException {
        if (arguments.isEmpty()) {
            throw new CommandException("Did not remove due to incorrect format.");
        }

        if (arguments.equals("all")) {
            Character[] charArray = charMatcher.getCharSet().toArray(new Character[0]);
            for (Character c : charArray) {
                charMatcher.removeChar(c);
            }
        } else if (arguments.equals("space")) {
            charMatcher.removeChar(' ');
        } else if (arguments.length() == 1) {
            char c = arguments.charAt(0);
            if (c >= MIN_CHAR && c <= MAX_CHAR) {
                charMatcher.removeChar(c);
            } else {
                throw new CharsetException("Character out of valid ASCII range.");
            }
        } else if (arguments.matches(".-.")) {
            char start = arguments.charAt(0);
            char end = arguments.charAt(2);
            if (start >= MIN_CHAR && end >= MIN_CHAR && start <= MAX_CHAR && end <= MAX_CHAR) {
                if (start <= end) {
                    for (char c = start; c <= end; c++) {
                        charMatcher.removeChar(c);
                    }
                } else {
                    for (char c = start; c >= end; c--) {
                        charMatcher.removeChar(c);
                    }
                }
            } else {
                throw new CharsetException("Character range out of valid ASCII range.");
            }
        } else {
            throw new CommandException("Did not remove due to incorrect format.");
        }
    }

    private void handleResolution(String arguments, Image image) throws ResolutionException,
            CommandException {
        int minResolution = Math.max(1, image.getWidth() / image.getHeight()); //todo to transmit
        int maxResolution = image.getWidth();


        if (arguments.isEmpty()) {
            System.out.println("Resolution set to " + resolution + ".");
        } else if (arguments.equals("up")) {  //todo validate upper case
            if (resolution * 2 <= maxResolution) {
                resolution *= 2; //todo - validate if there is something else that need to be changed
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

    private void handleRounding(String arguments) throws CommandException {
        if (arguments.equals("up") || arguments.equals("down") || arguments.equals("abs")) {

            charMatcher.setRoundingMethod(arguments);
//            roundingMethod = arguments;
            System.out.println("Rounding method set to " + arguments + ".");
        } else {
            throw new CommandException("Did not change rounding method due to incorrect format.");
        }
    }

    private void handleOutput(String arguments) throws CommandException {

        if (arguments.equals("console")) {
            ConsoleAsciiOutput consoleOutPut = new ConsoleAsciiOutput();
            this.output = consoleOutPut;
            System.out.println("Output set to console.");
        } else if (arguments.equals("html")) {
            HtmlAsciiOutput htmlOutPut = new HtmlAsciiOutput("out.html", DEFAULT_FONT);
            this.output = htmlOutPut;
            System.out.println("Output set to html.");
        } else {
            throw new CommandException("Did not change output method due to incorrect format.");
        }
    }

    private void printSortedChars() {
        TreeSet<Character> sortedSet = new TreeSet<>(charMatcher.getCharSet());

        for (char c : sortedSet) {
            System.out.print(c + " ");
        }
        System.out.println();
    }

    private void handleAsciiArt(Image image, SubImageManager subImageManager) throws IOException {
        if (charMatcher.getCharSet().size() < 2) {
            throw new IOException("Did not execute. Charset is too small.");
        }
        AsciiArtAlgorithm algorithm = new AsciiArtAlgorithm(image, resolution, charMatcher.getCharSet(),
                charMatcher, subImageManager);

        char[][] art = algorithm.run();
        output.out(art);
    }
}

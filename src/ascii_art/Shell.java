package ascii_art;

import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;
import image.ImagePaddingManager;
import image.SubImageManager;
import image_char_matching.SubImgCharMatcher;

import java.io.IOException;
import java.util.TreeSet;

/**
 * The Shell class provides a command-line interface for generating ASCII art from images.
 * It supports various commands to manipulate the character set, resolution, output method, and more.
 *
 * @ Author: Hadas Elezre
 */
public class Shell {

    //commands:
    private static final String COMMAND_EXIT = "exit"; // Command to exit the shell
    private static final String COMMAND_PREFIX = ">>> "; // Command prefix for user input.
    private static final String COMMAND_CHARS = "chars"; // Command to display the character set.
    private static final String COMMAND_ADD = "add"; // Command to add characters to the chars set.
    private static final String COMMAND_REMOVE = "remove"; //Command to remove chars from the character set.
    private static final String COMMAND_RES = "res"; // Command to change the resolution.
    private static final String COMMAND_ROUND = "round"; // Command to change the rounding method.
    private static final String COMMAND_OUTPUT = "output"; //Command to change the output method.
    private static final String COMMAND_ASCII_ART = "asciiArt"; //Command to generate ASCII art.

    // Character range
    private static final char MIN_CHAR = 32; // Minimum ASCII value for characters.
    private static final char MAX_CHAR = 127; // Maximum ASCII value for characters.

    //default args:
    private static final int DEFAULT_RESOLUTION = 2; // Default resolution
    private static final String DEFAULT_ROUNDING = "abs"; // Default rounding method
    private static final char[] DEFAULT_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'}; //
    // Default character set
    private static final String DEFAULT_FONT = "Courier New"; // Default font for HTML output

    //fields:
    /**
     * The matcher for matching brightness to characters.
     */
    private final SubImgCharMatcher charMatcher;
    /**
     * The current resolution for the ASCII art.
     */
    private int resolution;
    /**
     * The current output method for the ASCII art.
     */
    private AsciiOutput output;

    /**
     * Constructs a Shell instance with default settings.
     */
    public Shell() {
        this.resolution = DEFAULT_RESOLUTION;
        this.output = new ConsoleAsciiOutput();
        this.charMatcher = new SubImgCharMatcher(DEFAULT_CHARS);
        charMatcher.setRoundingMethod(DEFAULT_ROUNDING);
    }

    /**
     * Runs the shell with the specified image.
     *
     * @param imageName The name of the image file.
     */
    public void run(String imageName) {
        Image inputImage;
        try {
            inputImage = new Image(imageName);
            Image paddedImage = ImagePaddingManager.padImageToPowerOfTwo(inputImage);
            SubImageManager subImageManager = SubImageManager.getInstance(paddedImage);

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
                } catch (IOException | CommandException | ResolutionException e) {
                    System.out.println(e.getMessage());
                }
                System.out.print(COMMAND_PREFIX);
                command = KeyboardInput.readLine();
            }
        } catch (IOException e) {
            System.out.println("Did not execute due to incorrect command.");
        }
    }

    /**
     * The main method to start the shell.
     *
     * @param args The arguments.
     */
    public static void main(String[] args) {
        Shell shell = new Shell();
        shell.run(args[0]);
    }

    /**
     * Handles the 'add' command to add characters to the character set.
     *
     * @param arguments The arguments for the 'add' command.
     * @throws CommandException If the format is incorrect.
     */
    private void handleAdd(String arguments) throws CommandException {
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
                throw new CommandException("Did not add due to incorrect format.");
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
                throw new CommandException("Did not add due to incorrect format.");
            }
        } else {
            throw new CommandException("Did not add due to incorrect format.");
        }
    }

    /**
     * Handles the 'remove' command to remove characters from the character set.
     *
     * @param arguments The arguments for the 'remove' command.
     * @throws CommandException If the format is incorrect.
     */
    private void handleRemove(String arguments) throws CommandException {
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
                throw new CommandException("Did not remove due to incorrect format.");
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
                throw new CommandException("Did not remove due to incorrect format.");
            }
        } else {
            throw new CommandException("Did not remove due to incorrect format.");
        }
    }

    /**
     * Handles the 'res' command to change the resolution.
     *
     * @param arguments The arguments for the 'res' command.
     * @param image     The image to adjust the resolution for.
     * @throws ResolutionException If the resolution is out of bounds.
     * @throws CommandException    If the format is incorrect.
     */
    private void handleResolution(String arguments, Image image) throws ResolutionException,
            CommandException {
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

    /**
     * Handles the 'round' command to change the rounding method.
     *
     * @param arguments The arguments for the 'round' command.
     * @throws CommandException If the format is incorrect.
     */
    private void handleRounding(String arguments) throws CommandException {
        if (arguments.equals("up") || arguments.equals("down") || arguments.equals("abs")) {
            charMatcher.setRoundingMethod(arguments);
            System.out.println("Rounding method set to " + arguments + ".");
        } else {
            throw new CommandException("Did not change rounding method due to incorrect format.");
        }
    }

    /**
     * Handles the 'output' command to change the output method.
     *
     * @param arguments The arguments for the 'output' command.
     * @throws CommandException If the format is incorrect.
     */
    private void handleOutput(String arguments) throws CommandException {
        if (arguments.equals("console")) {
            this.output = new ConsoleAsciiOutput();
            System.out.println("Output set to console.");
        } else if (arguments.equals("html")) {
            this.output = new HtmlAsciiOutput("out.html", DEFAULT_FONT);
            System.out.println("Output set to html.");
        } else {
            throw new CommandException("Did not change output method due to incorrect format.");
        }
    }

    /**
     * Prints the sorted character set.
     */
    private void printSortedChars() {
        TreeSet<Character> sortedSet = new TreeSet<>(charMatcher.getCharSet());
        for (char c : sortedSet) {
            System.out.print(c + " ");
        }
        System.out.println();
    }

    /**
     * Handles the 'asciiArt' command to generate ASCII art.
     *
     * @param image           The image to convert to ASCII art.
     * @param subImageManager The manager for handling sub-images.
     * @throws IOException If there is an error generating the ASCII art.
     */
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

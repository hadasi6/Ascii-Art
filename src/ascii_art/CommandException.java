package ascii_art;

/**
 * The CommandException class represents an exception that is thrown
 * when an invalid command is encountered in the Shell.
 *
 * @ Author: Hadas Elezra
 */
public class CommandException extends Exception {
    /**
     * Constructs a CommandException with the specified detail message.
     *
     * @param message the detail message
     */
    public CommandException(String message) {
        super(message);
    }
}

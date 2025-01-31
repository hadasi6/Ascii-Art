package ascii_art;

/**
 * The ResolutionException class represents an exception that is thrown
 * when the resolution is out of bounds in the Shell.
 *
 * @ Author: Hadas Elezre
 */
public class ResolutionException extends Exception {
    /**
     * Constructs a ResolutionException with the specified detail message.
     *
     * @param message the detail message
     */
    public ResolutionException(String message) {
        super(message);
    }
}

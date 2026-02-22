package storageSpec.exception;

import storageSpec.Privilege;

/**
 * Thrown when a user attempts an operation that requires a higher {@link Privilege} level
 * than the one they currently hold.
 */
public class StoragePermissionException extends StorageException {

    public StoragePermissionException(Privilege required, Privilege actual) {
        super("Operation requires privilege " + required + ", but user has " + actual);
    }

    public StoragePermissionException(String message) {
        super(message);
    }
}


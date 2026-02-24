package storageSpec.exception;

/**
 * Base exception for all storage-related errors.
 * Implementations should throw subclasses for specific failure modes.
 */
public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}


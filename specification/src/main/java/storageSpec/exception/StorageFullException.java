package storageSpec.exception;

/**
 * Thrown when an operation would exceed the storage's configured size limit.
 */
public class StorageFullException extends StorageException {

    public StorageFullException(long requiredBytes, long availableBytes) {
        super("Storage full. Required: " + requiredBytes + " bytes, available: " + availableBytes + " bytes");
    }

    public StorageFullException(String message) {
        super(message);
    }
}


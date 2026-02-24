package storageSpec.exception;

/**
 * Thrown when a file operation involves an extension that has been
 * forbidden in the storage configuration.
 */
public class ForbiddenExtensionException extends StorageException {

    public ForbiddenExtensionException(String extension) {
        super("File extension is forbidden in this storage: " + extension);
    }
}


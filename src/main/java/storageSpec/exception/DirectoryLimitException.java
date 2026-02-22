package storageSpec.exception;

/**
 * Thrown when adding a file or subdirectory to a directory would exceed
 * the configured maximum number of children for that directory.
 */
public class DirectoryLimitException extends StorageException {

    public DirectoryLimitException(String dirPath, int limit) {
        super("Directory '" + dirPath + "' has reached its maximum child limit of " + limit);
    }
}


package storageSpec;

import storageSpec.exception.StorageException;

/**
 * Handles storage creation and authentication lifecycle.
 * <p>
 * Each back-end (local file system, Google Drive, etc.) provides its own implementation.
 * The {@link StorageManager} delegates to this interface.
 */
public interface IStorageAuthProvider {

    /**
     * Checks whether a storage exists at the given location.
     *
     * @param storageNameAndPath implementation-defined storage location
     * @return {@code true} if a storage exists at that location
     */
    boolean storageExists(String storageNameAndPath);

    /**
     * Creates and initializes a new storage rooted at {@code storageNameAndPath}.
     * The calling user (identified by {@code username}/{@code password}) becomes the
     * {@link Privilege#ADMIN ADMIN}.
     *
     * @param storageNameAndPath location where the storage will be created
     * @param username           admin user name
     * @param password           admin password
     * @throws StorageException if the storage already exists or cannot be created
     */
    void initStorage(String storageNameAndPath, String username, String password);

    /**
     * Authenticates a user against an existing storage and returns a fully initialised
     * {@link StorageSession} bound to the authenticated user.
     *
     * @param storageNameAndPath location of the storage to log in to
     * @param username           user name
     * @param password           password
     * @return a {@link StorageSession} ready for use
     * @throws StorageException if credentials are invalid, the storage does not exist,
     *                          or login fails for any other reason
     */
    StorageSession logIn(String storageNameAndPath, String username, String password);

    /**
     * Persists any pending changes and releases resources held by the session.
     *
     * @param session the session to close
     * @throws StorageException if logout or persistence fails
     */
    void logOut(StorageSession session);
}


package storageSpec;

import lombok.Getter;
import lombok.Setter;
import storageSpec.exception.StorageException;
import storageSpec.serialization.ISerialization;

/**
 * Replaces the static-field {@code UserManager}.
 * <p>
 * Holds registered implementations ({@link IStorageAuthProvider}, {@link ISerialization})
 * and exposes storage lifecycle operations (init, login, logout).
 * <p>
 * Instantiate once (e.g.&nbsp;in your application entry point) and inject where needed —
 * no hidden global state.
 * <p>
 * Example usage:
 * <pre>{@code
 * LocalStorageProvider local = new LocalStorageProvider();
 * StorageManager manager = new StorageManager(local, new JsonSerialization());
 *
 * manager.initStorage("/my/storage", "admin", "secret");
 * StorageSession session = manager.logIn("/my/storage", "admin", "secret");
 * session.createDir("reports", "/my/storage");
 * manager.logOut(session);
 * }</pre>
 */
@Getter
@Setter
public class StorageManager {

    private IStorageAuthProvider authProvider;
    private ISerialization serialization;

    /**
     * Creates a fully-configured manager.
     *
     * @param authProvider the authentication/lifecycle provider (local, Google Drive, etc.)
     * @param serialization the serialization strategy (JSON, XML, etc.)
     */
    public StorageManager(IStorageAuthProvider authProvider, ISerialization serialization) {
        this.authProvider = authProvider;
        this.serialization = serialization;
    }

    /**
     * No-arg constructor — use setters before calling any method.
     */
    public StorageManager() {
    }

    // ── Delegating lifecycle methods ─────────────────────────────────────────

    /**
     * Checks whether a storage exists at the given location.
     *
     * @param storageNameAndPath storage location
     * @return true if the storage exists
     * @throws StorageException if no auth provider is registered
     */
    public boolean storageExists(String storageNameAndPath) {
        return requireAuthProvider().storageExists(storageNameAndPath);
    }

    /**
     * Creates and initialises a new storage. The calling user becomes ADMIN.
     *
     * @param storageNameAndPath location for the new storage
     * @param username           admin user name
     * @param password           admin password
     * @throws StorageException if no auth provider is registered, or creation fails
     */
    public void initStorage(String storageNameAndPath, String username, String password) {
        requireAuthProvider().initStorage(storageNameAndPath, username, password);
    }

    /**
     * Authenticates a user and returns a {@link StorageSession}.
     *
     * @param storageNameAndPath storage location
     * @param username           user name
     * @param password           password
     * @return an active session
     * @throws StorageException if no auth provider is registered, or login fails
     */
    public StorageSession logIn(String storageNameAndPath, String username, String password) {
        return requireAuthProvider().logIn(storageNameAndPath, username, password);
    }

    /**
     * Persists pending changes and releases the session.
     *
     * @param session the session to close
     * @throws StorageException if no auth provider is registered, or logout fails
     */
    public void logOut(StorageSession session) {
        requireAuthProvider().logOut(session);
    }

    // ── Internal guards ──────────────────────────────────────────────────────

    private IStorageAuthProvider requireAuthProvider() {
        if (authProvider == null) {
            throw new StorageException("No IStorageAuthProvider registered in StorageManager");
        }
        return authProvider;
    }

    /**
     * Returns the registered serialization implementation or throws.
     *
     * @return the serialization implementation
     * @throws StorageException if none is registered
     */
    public ISerialization requireSerialization() {
        if (serialization == null) {
            throw new StorageException("No ISerialization registered in StorageManager");
        }
        return serialization;
    }
}


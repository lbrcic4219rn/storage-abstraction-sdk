package storageSpec;

import lombok.Getter;
import lombok.Setter;
import storageSpec.ops.IStorageAdminOps;
import storageSpec.ops.IStorageOps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Data container representing a storage instance.
 * <p>
 * Holds metadata (name, ID, root location) and configuration (size limit,
 * forbidden extensions, per-directory child limits). Does <em>not</em> contain
 * any business logic — operations are in {@link IStorageOps} and
 * {@link IStorageAdminOps}.
 */
@Getter
@Setter
public class Storage {

    /**
     * Human-readable name of the storage.
     */
    private String storageName;

    /**
     * Unique storage identifier.
     */
    private String storageId;

    /**
     * Root location of the storage (local path, Google Drive folder ID, etc.).
     */
    private String rootLocation;

    /**
     * Maximum allowed total size of the storage in bytes.
     * {@code 0} means unlimited.
     */
    private long storageSize;

    /**
     * File extensions that are forbidden in this storage (without leading dot).
     */
    private Collection<String> forbiddenExtensions = new ArrayList<>();

    /**
     * Maximum number of direct children allowed per directory.
     * Key = directory path/id, Value = max children count.
     */
    private Map<String, Integer> dirsMaxChildrenCount = new HashMap<>();

    /**
     * All users who have access to this storage.
     */
    private Collection<User> users = new ArrayList<>();

    /**
     * Constructs a storage with the given name, root location, and ID.
     *
     * @param storageName  human-readable name
     * @param rootLocation root location (path, folder ID, etc.)
     * @param storageID    unique identifier
     */
    public Storage(String storageName, String rootLocation, String storageID) {
        this.storageName = storageName;
        this.rootLocation = rootLocation;
        this.storageId = storageID;
    }

    /**
     * Adds a user to this storage's user list.
     *
     * @param user the user to add
     */
    public void addUser(User user) {
        this.users.add(user);
    }

    /**
     * Removes a user from this storage's user list.
     *
     * @param user the user to remove
     */
    public void removeUser(User user) {
        this.users.remove(user);
    }
}

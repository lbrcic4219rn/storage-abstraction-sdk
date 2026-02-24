package storageSpec;

import lombok.Getter;
import lombok.Setter;
import storageSpec.ops.IStorageAdminOps;
import storageSpec.ops.IStorageOps;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple principal: credentials + a map of storage-id → privilege.
 * <p>
 * This class holds <em>no</em> business logic or file-system operations.
 * All operations are performed through {@link IStorageOps} and
 * {@link IStorageAdminOps}, with privilege enforcement in {@link StorageSession}.
 */
@Getter
@Setter
public class User {

    /**
     * Login name.
     */
    private String userName;

    /**
     * Password. Implementations should store a hash rather than plain text.
     */
    private String password;

    /**
     * Map of storageID → {@link Privilege} this user holds for each storage.
     */
    private Map<String, Privilege> storagesAndPrivileges = new HashMap<>();

    /**
     * Constructs an empty user.
     */
    public User() {
    }

    /**
     * Constructs a user with the given credentials.
     *
     * @param userName login name
     * @param password password
     */
    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    /**
     * Grants (or overwrites) a privilege for the given storage.
     *
     * @param storageId storage identifier
     * @param privilege privilege level
     */
    public void addStorage(String storageId, Privilege privilege) {
        storagesAndPrivileges.put(storageId, privilege);
    }

    /**
     * Revokes access to the given storage.
     *
     * @param storageId storage identifier
     */
    public void removeStorage(String storageId) {
        storagesAndPrivileges.remove(storageId);
    }

    /**
     * Returns the privilege this user holds for the given storage,
     * or {@code null} if the user has no access.
     *
     * @param storageId storage identifier
     * @return privilege or null
     */
    public Privilege getPrivilegeFor(String storageId) {
        return storagesAndPrivileges.get(storageId);
    }
}


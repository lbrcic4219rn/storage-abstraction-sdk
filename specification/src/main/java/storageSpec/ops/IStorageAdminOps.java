package storageSpec.ops;

import storageSpec.Privilege;
import storageSpec.StorageSession;
import storageSpec.exception.StorageException;

import java.util.Collection;

/**
 * Administrative operations on a storage.
 * <p>
 * Only users holding {@link Privilege#ADMIN} may invoke these methods.
 * Privilege enforcement is <strong>not</strong> the responsibility of the implementation —
 * it is handled by {@link StorageSession}.
 */
public interface IStorageAdminOps {

    /**
     * Sets the maximum allowed total size of the storage in bytes.
     * A value of {@code 0} means unlimited.
     *
     * @param bytes maximum size in bytes
     * @throws StorageException if the value is invalid or the operation fails
     */
    void setStorageSize(long bytes);

    /**
     * Defines which file extensions are forbidden in the storage
     * (e.g.&nbsp;"exe", "bat"). Replaces any previously set list.
     *
     * @param extensions collection of forbidden extensions (without leading dot)
     * @throws StorageException if the operation fails
     */
    void setForbiddenExtensions(Collection<String> extensions);

    /**
     * Sets the maximum number of direct children (files + subdirectories)
     * allowed in the directory at {@code dirPath}.
     *
     * @param number  maximum child count
     * @param dirPath directory to constrain
     * @throws StorageException if the directory does not exist or the operation fails
     */
    void setMaxFileNumberInDir(int number, String dirPath);

    /**
     * Adds a new user with the given credentials and privilege level to the storage.
     *
     * @param userName  login name
     * @param password  password
     * @param privilege the privilege level to grant
     * @throws StorageException if the user already exists or the operation fails
     */
    void addUser(String userName, String password, Privilege privilege);

    /**
     * Removes the user with the given {@code userName} from the storage.
     *
     * @param userName login name of the user to remove
     * @throws StorageException if the user does not exist or the operation fails
     */
    void removeUser(String userName);

    /**
     * Updates the privilege level of an existing user.
     *
     * @param userName     login name of the user
     * @param newPrivilege the new privilege level
     * @throws StorageException if the user does not exist or the operation fails
     */
    void updateUserPrivilege(String userName, Privilege newPrivilege);
}


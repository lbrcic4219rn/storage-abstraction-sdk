package storageSpec.serialization;

import storageSpec.Privilege;
import storageSpec.Storage;
import storageSpec.exception.StorageException;

import java.util.List;
import java.util.Map;

/**
 * Handles persistence of user credentials and storage metadata.
 * <p>
 * Implementations are free to use JSON, XML, a database, or any other format.
 * The {@code filePath} parameter is an <em>implementation-defined resource identifier</em> —
 * it may be a local file path, a Google Drive file ID, a URL, or any other locator
 * meaningful to the concrete implementation.
 */
public interface ISerialization {

    /**
     * Persists a user's credentials and storage-privilege map to the resource
     * identified by {@code filePath}. If {@code append} is {@code true}, existing
     * data is preserved and the new record is added; otherwise the resource is overwritten.
     *
     * @param filePath               implementation-defined resource identifier
     * @param userName               the user's login name
     * @param password               the user's password (implementations should hash)
     * @param storagesAndPrivileges  map of storageID → {@link Privilege}
     * @param append                 {@code true} to append, {@code false} to overwrite
     * @throws StorageException if the write fails
     */
    void saveUserData(String filePath, String userName, String password,
                      Map<String, Privilege> storagesAndPrivileges, boolean append);

    /**
     * Reads and parses the resource identified by {@code filePath}, returning
     * all stored user records.
     *
     * @param filePath implementation-defined resource identifier
     * @return list of {@link UserData} — never null, empty list if no users found
     * @throws StorageException if the read or parse fails
     */
    List<UserData> readSavedUsers(String filePath);

    /**
     * Persists storage metadata (name, ID, root location, size limit,
     * forbidden extensions, directory child limits) to the resource identified
     * by {@code filePath}.
     *
     * @param filePath implementation-defined resource identifier
     * @param storage  the {@link Storage} whose metadata should be saved
     * @throws StorageException if the write fails
     */
    void saveStorageData(String filePath, Storage storage);

    /**
     * Reads and parses the resource identified by {@code filePath}, returning
     * the storage metadata.
     *
     * @param filePath implementation-defined resource identifier
     * @return a populated {@link StorageData} object
     * @throws StorageException if the read or parse fails
     */
    StorageData readStorageData(String filePath);
}
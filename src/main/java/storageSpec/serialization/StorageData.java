package storageSpec.serialization;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Map;

/**
 * Data-transfer object carrying persisted storage metadata.
 */
@Setter
@Getter
public class StorageData {

    /**
     * Human-readable name of the storage.
     */
    private String storageName;

    /**
     * Unique storage identifier.
     */
    private String storageID;

    /**
     * Root location of the storage (local path, Google Drive folder ID, etc.).
     */
    private String rootLocation;

    /**
     * Maximum allowed total size of the storage in bytes. {@code 0} means unlimited.
     */
    private long storageSize;

    /**
     * File extensions that are forbidden in this storage (without leading dot).
     */
    private Collection<String> forbiddenExtensions;

    /**
     * Maximum number of direct children allowed per directory.
     * Key = directory path/id, Value = max children count.
     */
    private Map<String, Integer> dirsMaxChildrenCount;

    /**
     * Constructs an empty StorageData.
     */
    public StorageData() {
    }
}

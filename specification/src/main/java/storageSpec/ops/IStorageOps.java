package storageSpec.ops;

import storageSpec.StorageSession;
import storageSpec.exception.DirectoryLimitException;
import storageSpec.exception.ForbiddenExtensionException;
import storageSpec.exception.StorageException;
import storageSpec.exception.StorageFullException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Core file-system operations that every storage implementation (local, Google Drive, etc.)
 * must provide.
 * <p>
 * Implementations contain <strong>zero privilege logic</strong> — privilege enforcement
 * is handled by {@link StorageSession}, which wraps any {@code IStorageOperations}
 * implementation as a decorator.
 * <p>
 * Methods throw {@link StorageException} (or a subclass) on failure instead of returning
 * integer status codes.
 */
public interface IStorageOps {

    // ── Directory operations ─────────────────────────────────────────────────

    /**
     * Creates a directory named {@code dirName} at the given {@code path}.
     *
     * @param dirName name of the new directory
     * @param path    parent path where the directory will be created
     * @throws DirectoryLimitException if the parent directory's child limit is reached
     * @throws StorageException        if the directory cannot be created for any other reason
     */
    void createDir(String dirName, String path);

    /**
     * Creates a directory named {@code dirName} at {@code path}, then inside it creates
     * {@code numberOfDirs} subdirectories named {@code namePrefix + counter}
     * (e.g.&nbsp;prefix&nbsp;=&nbsp;"dir", count&nbsp;=&nbsp;3 → dir1, dir2, dir3).
     *
     * @param dirName      name of the parent directory
     * @param path         parent path where the directory will be created
     * @param namePrefix   prefix for the generated subdirectory names
     * @param numberOfDirs how many subdirectories to create
     * @throws DirectoryLimitException if any directory's child limit is reached
     * @throws StorageException        if directories cannot be created
     */
    void createDir(String dirName, String path, String namePrefix, int numberOfDirs);

    // ── File operations ──────────────────────────────────────────────────────

    /**
     * Creates an empty file with the given name and type (e.g.&nbsp;".txt") at {@code path}.
     *
     * @param fileName name of the new file (without extension)
     * @param path     directory path where the file will be created
     * @param fileType file extension, e.g. ".txt", ".png"
     * @throws ForbiddenExtensionException if the extension is forbidden
     * @throws DirectoryLimitException     if the directory's child limit is reached
     * @throws StorageFullException        if the storage has no remaining space
     * @throws StorageException            on any other failure
     */
    void createFile(String fileName, String path, String fileType);

    /**
     * Uploads an existing file from {@code sourcePath} into the storage at
     * {@code destinationPath}.
     *
     * @param fileName        name of the file to upload
     * @param sourcePath      location of the existing file (local path, URL, etc.)
     * @param destinationPath storage path where the file will be placed
     * @param fileType        file extension, e.g. ".txt", ".png"
     * @throws ForbiddenExtensionException if the extension is forbidden
     * @throws DirectoryLimitException     if the destination directory is full
     * @throws StorageFullException        if the storage has no remaining space
     * @throws StorageException            on any other failure
     */
    void uploadFile(String fileName, String sourcePath, String destinationPath, String fileType);

    /**
     * Moves multiple files/directories to {@code destinationPath}.
     *
     * @param filePaths       paths of files/directories to move
     * @param destinationPath target directory
     * @throws StorageException if any file cannot be moved
     */
    void move(Collection<String> filePaths, String destinationPath);

    /**
     * Moves a single file or directory to {@code destinationPath}.
     *
     * @param filePath        path of the file/directory to move
     * @param destinationPath target directory
     * @throws StorageException if the move fails
     */
    void move(String filePath, String destinationPath);

    /**
     * Deletes the file or directory at {@code path}.
     * If the path points to a directory, all contents are deleted recursively.
     *
     * @param path path to delete
     * @throws StorageException if deletion fails
     */
    void delete(String path);

    /**
     * Downloads the file or directory at {@code path} to {@code destinationPath}
     * on the local machine.
     *
     * @param path            storage path to download
     * @param destinationPath local path where the file will be saved
     * @throws StorageException if the download fails
     */
    void download(String path, String destinationPath);

    /**
     * Renames the file or directory at {@code path} to {@code newName}.
     *
     * @param path    path to the file/directory to rename
     * @param newName the new name (not a full path — just the name)
     * @throws StorageException if the rename fails
     */
    void rename(String path, String newName);

    /**
     * Copies the file or directory from {@code sourcePath} to {@code destinationPath}.
     *
     * @param sourcePath      path to the file/directory to copy
     * @param destinationPath target directory
     * @throws ForbiddenExtensionException if the extension is forbidden at destination
     * @throws StorageFullException        if the storage has no remaining space
     * @throws StorageException            on any other failure
     */
    void copy(String sourcePath, String destinationPath);

    // ── Search & query ───────────────────────────────────────────────────────

    /**
     * Lists paths of all files directly inside the directory at {@code dirPath}.
     *
     * @param dirPath directory to list
     * @return paths of files — never null, empty list if none found
     */
    List<String> listFiles(String dirPath);

    /**
     * Lists paths of all subdirectories directly inside the directory at {@code dirPath}.
     *
     * @param dirPath directory to list
     * @return paths of subdirectories — never null, empty list if none found
     */
    List<String> listDirs(String dirPath);

    /**
     * Searches the entire storage for files or directories whose name matches {@code name}.
     *
     * @param name name to search for
     * @return matching paths — never null
     */
    List<String> searchByName(String name);

    /**
     * Searches for files with the given extension inside the directory at {@code dirPath}.
     *
     * @param extension file extension to match (e.g. ".txt")
     * @param dirPath   directory to search
     * @return matching file paths — never null
     */
    List<String> searchByExtension(String extension, String dirPath);

    /**
     * Lists files in the directory at {@code dirPath}, sorted alphabetically by name.
     *
     * @param dirPath directory to list
     * @return sorted file paths — never null
     */
    List<String> listFilesSortedByName(String dirPath);

    /**
     * Lists files in the directory at {@code dirPath}, sorted by creation date (ascending).
     *
     * @param dirPath directory to list
     * @return sorted file paths — never null
     */
    List<String> listFilesSortedByDate(String dirPath);

    /**
     * Searches the entire storage for files created between {@code start} and {@code end}.
     *
     * @param start inclusive start of the date range
     * @param end   inclusive end of the date range
     * @return matching file paths — never null
     */
    List<String> searchByCreationDateRange(LocalDateTime start, LocalDateTime end);

    /**
     * Searches files inside {@code dirPath} that were created between
     * {@code start} and {@code end}.
     *
     * @param start   inclusive start of the date range
     * @param end     inclusive end of the date range
     * @param dirPath directory to search
     * @return matching file paths — never null
     */
    List<String> searchByCreationDateRange(LocalDateTime start, LocalDateTime end, String dirPath);

    /**
     * Returns the last-modification date of the file or directory at {@code path}
     * as an ISO-8601 string, or {@code null} if unavailable.
     *
     * @param path file/directory path
     * @return ISO-8601 date string or null
     */
    String getModificationDate(String path);

    /**
     * Returns the creation date of the file or directory at {@code path}
     * as an ISO-8601 string, or {@code null} if unavailable.
     *
     * @param path file/directory path
     * @return ISO-8601 date string or null
     */
    String getCreationDate(String path);

    /**
     * Checks whether a file or directory exists at {@code path}.
     *
     * @param path path to check
     * @return true if it exists, false otherwise
     */
    boolean fileExists(String path);

    /**
     * Returns the size of the file at {@code path} in bytes.
     *
     * @param path file path
     * @return size in bytes
     * @throws StorageException if the file does not exist or the size cannot be determined
     */
    long getFileSize(String path);
}


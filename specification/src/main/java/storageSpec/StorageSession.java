package storageSpec;

import lombok.Getter;
import storageSpec.exception.StoragePermissionException;
import storageSpec.ops.IStorageAdminOps;
import storageSpec.ops.IStorageOps;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Binds a {@link User} to an active {@link Storage} and enforces privilege checks
 * before delegating every operation to the underlying {@link IStorageOps} /
 * {@link IStorageAdminOps} implementation.
 * <p>
 * Neither the local implementation nor the Google Drive implementation should
 * contain any privilege logic — all enforcement lives here.
 * <p>
 * Usage:
 * <pre>{@code
 * StorageSession session = manager.logIn("/my/storage", "alice", "secret");
 * session.createDir("reports", "/my/storage/docs");   // privilege checked automatically
 * }</pre>
 */
public class StorageSession implements IStorageOps, IStorageAdminOps {

    @Getter
    private final User currentUser;
    @Getter
    private final Storage storage;
    private final IStorageOps ops;
    private final IStorageAdminOps admin;

    /**
     * Creates a new session binding a user to a storage through the given operation delegates.
     *
     * @param currentUser the authenticated user
     * @param storage     the storage being operated on
     * @param ops         the concrete file-system operations implementation
     * @param admin       the concrete admin operations implementation
     */
    public StorageSession(User currentUser, Storage storage,
                          IStorageOps ops, IStorageAdminOps admin) {
        this.currentUser = currentUser;
        this.storage = storage;
        this.ops = ops;
        this.admin = admin;
    }

    // ── Privilege helper ─────────────────────────────────────────────────────

    private void require(Privilege required) {
        Privilege actual = currentUser.getPrivilegeFor(storage.getStorageId());
        if (actual == null || !actual.satisfies(required)) {
            throw new StoragePermissionException(required, actual);
        }
    }

    // ── IStorageOperations — directory ops ───────────────────────────────────

    @Override
    public void createDir(String dirName, String path) {
        require(Privilege.UPLOAD);
        ops.createDir(dirName, path);
    }

    @Override
    public void createDir(String dirName, String path, String namePrefix, int numberOfDirs) {
        require(Privilege.UPLOAD);
        ops.createDir(dirName, path, namePrefix, numberOfDirs);
    }

    // ── IStorageOperations — file ops ────────────────────────────────────────

    @Override
    public void createFile(String fileName, String path, String fileType) {
        require(Privilege.UPLOAD);
        ops.createFile(fileName, path, fileType);
    }

    @Override
    public void uploadFile(String fileName, String sourcePath, String destinationPath, String fileType) {
        require(Privilege.UPLOAD);
        ops.uploadFile(fileName, sourcePath, destinationPath, fileType);
    }

    @Override
    public void move(Collection<String> filePaths, String destinationPath) {
        require(Privilege.UPLOAD);
        ops.move(filePaths, destinationPath);
    }

    @Override
    public void move(String filePath, String destinationPath) {
        require(Privilege.UPLOAD);
        ops.move(filePath, destinationPath);
    }

    @Override
    public void delete(String path) {
        require(Privilege.DELETE);
        ops.delete(path);
    }

    @Override
    public void download(String path, String destinationPath) {
        require(Privilege.DOWNLOAD);
        ops.download(path, destinationPath);
    }

    @Override
    public void rename(String path, String newName) {
        require(Privilege.UPLOAD);
        ops.rename(path, newName);
    }

    @Override
    public void copy(String sourcePath, String destinationPath) {
        require(Privilege.UPLOAD);
        ops.copy(sourcePath, destinationPath);
    }

    // ── IStorageOperations — search & query ──────────────────────────────────

    @Override
    public List<String> listFiles(String dirPath) {
        require(Privilege.READ);
        return ops.listFiles(dirPath);
    }

    @Override
    public List<String> listDirs(String dirPath) {
        require(Privilege.READ);
        return ops.listDirs(dirPath);
    }

    @Override
    public List<String> searchByName(String name) {
        require(Privilege.READ);
        return ops.searchByName(name);
    }

    @Override
    public List<String> searchByExtension(String extension, String dirPath) {
        require(Privilege.READ);
        return ops.searchByExtension(extension, dirPath);
    }

    @Override
    public List<String> listFilesSortedByName(String dirPath) {
        require(Privilege.READ);
        return ops.listFilesSortedByName(dirPath);
    }

    @Override
    public List<String> listFilesSortedByDate(String dirPath) {
        require(Privilege.READ);
        return ops.listFilesSortedByDate(dirPath);
    }

    @Override
    public List<String> searchByCreationDateRange(LocalDateTime start, LocalDateTime end) {
        require(Privilege.READ);
        return ops.searchByCreationDateRange(start, end);
    }

    @Override
    public List<String> searchByCreationDateRange(LocalDateTime start, LocalDateTime end, String dirPath) {
        require(Privilege.READ);
        return ops.searchByCreationDateRange(start, end, dirPath);
    }

    @Override
    public String getModificationDate(String path) {
        require(Privilege.READ);
        return ops.getModificationDate(path);
    }

    @Override
    public String getCreationDate(String path) {
        require(Privilege.READ);
        return ops.getCreationDate(path);
    }

    @Override
    public boolean fileExists(String path) {
        require(Privilege.READ);
        return ops.fileExists(path);
    }

    @Override
    public long getFileSize(String path) {
        require(Privilege.READ);
        return ops.getFileSize(path);
    }

    // ── IStorageAdmin ────────────────────────────────────────────────────────

    @Override
    public void setStorageSize(long bytes) {
        require(Privilege.ADMIN);
        admin.setStorageSize(bytes);
    }

    @Override
    public void setForbiddenExtensions(Collection<String> extensions) {
        require(Privilege.ADMIN);
        admin.setForbiddenExtensions(extensions);
    }

    @Override
    public void setMaxFileNumberInDir(int number, String dirPath) {
        require(Privilege.ADMIN);
        admin.setMaxFileNumberInDir(number, dirPath);
    }

    @Override
    public void addUser(String userName, String password, Privilege privilege) {
        require(Privilege.ADMIN);
        admin.addUser(userName, password, privilege);
    }

    @Override
    public void removeUser(String userName) {
        require(Privilege.ADMIN);
        admin.removeUser(userName);
    }

    @Override
    public void updateUserPrivilege(String userName, Privilege newPrivilege) {
        require(Privilege.ADMIN);
        admin.updateUserPrivilege(userName, newPrivilege);
    }

}


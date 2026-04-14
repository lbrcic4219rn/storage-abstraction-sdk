package ops;

import storageSpec.ops.IStorageOps;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class LocalStorageOps implements IStorageOps {
    @Override
    public void createDir(String dirName, String path) {

    }

    @Override
    public void createDir(String dirName, String path, String namePrefix, int numberOfDirs) {

    }

    @Override
    public void createFile(String fileName, String path, String fileType) {

    }

    @Override
    public void uploadFile(String fileName, String sourcePath, String destinationPath, String fileType) {

    }

    @Override
    public void move(Collection<String> filePaths, String destinationPath) {

    }

    @Override
    public void move(String filePath, String destinationPath) {

    }

    @Override
    public void delete(String path) {

    }

    @Override
    public void download(String path, String destinationPath) {

    }

    @Override
    public void rename(String path, String newName) {

    }

    @Override
    public void copy(String sourcePath, String destinationPath) {

    }

    @Override
    public List<String> listFiles(String dirPath) {
        return List.of();
    }

    @Override
    public List<String> listDirs(String dirPath) {
        return List.of();
    }

    @Override
    public List<String> searchByName(String name) {
        return List.of();
    }

    @Override
    public List<String> searchByExtension(String extension, String dirPath) {
        return List.of();
    }

    @Override
    public List<String> listFilesSortedByName(String dirPath) {
        return List.of();
    }

    @Override
    public List<String> listFilesSortedByDate(String dirPath) {
        return List.of();
    }

    @Override
    public List<String> searchByCreationDateRange(LocalDateTime start, LocalDateTime end) {
        return List.of();
    }

    @Override
    public List<String> searchByCreationDateRange(LocalDateTime start, LocalDateTime end, String dirPath) {
        return List.of();
    }

    @Override
    public String getModificationDate(String path) {
        return "";
    }

    @Override
    public String getCreationDate(String path) {
        return "";
    }

    @Override
    public boolean fileExists(String path) {
        return false;
    }

    @Override
    public long getFileSize(String path) {
        return 0;
    }
}

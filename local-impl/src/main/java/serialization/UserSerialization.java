package serialization;

import storageSpec.Privilege;
import storageSpec.Storage;
import storageSpec.serialization.ISerialization;
import storageSpec.serialization.StorageData;
import storageSpec.serialization.UserData;

import java.util.List;
import java.util.Map;

public class UserSerialization implements ISerialization {

    @Override
    public void saveUserData(String filePath, String userName, String password, Map<String, Privilege> storagesAndPrivileges, boolean append) {

    }

    @Override
    public List<UserData> readSavedUsers(String filePath) {
        return List.of();
    }

    @Override
    public void saveStorageData(String filePath, Storage storage) {

    }

    @Override
    public StorageData readStorageData(String filePath) {
        return null;
    }
}

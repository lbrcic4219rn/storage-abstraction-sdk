import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import storageSpec.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class UserSerializationLocalImpl implements ISerialization {

    @Override
    public void saveUserData(String filePath, String userName, String password, Map<String, Privilege> storagesAndPrivileges, boolean append) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            UserData userData = new UserData();
            userData.setUserName(userName);
            userData.setPassword(password);
            userData.setStoragesAndPrivileges(storagesAndPrivileges);
            String json = objectMapper.writeValueAsString(userData);
            java.io.File file = new java.io.File(filePath);
            if(append)
                Files.write(file.toPath(), Arrays.asList(json), StandardOpenOption.APPEND);
            else
                //FileUtils.write(file, "", Charset.defaultCharset());
                Files.write(file.toPath(), Arrays.asList(json), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<UserData> readSavedUsers(String filePath){
        //System.out.println("readSaveUsers :" + filePath);
        List<UserData> myUsers = new ArrayList<>();
        UserData userData;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Scanner scanner = new Scanner(new File(filePath));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.length() == 0)
                    continue;
                userData = objectMapper.readValue(line, UserData.class);
                myUsers.add(userData);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return myUsers;
    }

    @Override
    public void saveStorageData(String fileWhereToSave, Storage storage) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            StorageData storageData = new StorageData();

            storageData.setStorageName(storage.getStorageName());
            storageData.setStorageID(storage.getStorageID());
            storageData.setStorageSize(storage.getStorageSize());
            storageData.setForbiddenExtensions(storage.getForbiddenExtensions());
            storageData.setRootLocation(storage.getRootLocation());
            storageData.setDirsMaxChildrenCount(storage.getDirsMaxChildrenCount());

            String json = objectMapper.writeValueAsString(storageData);
            File file = new File(fileWhereToSave);
            Files.write(file.toPath(), Arrays.asList(json), StandardOpenOption.TRUNCATE_EXISTING);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public StorageData readStorageData(String fileFromWhereToRead) {
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File(fileFromWhereToRead);
        StorageData storageData = null;
        try {
            storageData = objectMapper.readValue(jsonFile, StorageData.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return storageData;
    }
}

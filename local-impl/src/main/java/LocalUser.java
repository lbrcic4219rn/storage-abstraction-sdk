import org.apache.commons.io.FileUtils;
import storageSpec.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

public class LocalUser extends AbstractUser {


    static {
        try {
            UserManager.registerUser(new LocalUser());
            UserManager.registerUserSerializator(new UserSerializationLocalImpl());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean storageExists(String storageNameAndPath) {
        File newFolder = new File(storageNameAndPath);
        File usersJson = new File(storageNameAndPath + "\\users.json");
        File storageJson = new File(storageNameAndPath + "\\storage.json");
        if(Files.exists(newFolder.toPath())) {
            return Files.exists(usersJson.toPath()) && Files.exists(storageJson.toPath());
        }
        return false;
    }

    @Override
    public int initStorage(String storageNameAndPath, String username, String password) {
        File newFolder = new File(storageNameAndPath);
        File usersJson = new File(storageNameAndPath + "\\users.json");
        File storageJson = new File(storageNameAndPath + "\\storage.json");
        boolean isCreated = newFolder.mkdirs();
        if (isCreated) {
            this.setUserName(username);
            this.setPassword(password);
            String[] storageNames = storageNameAndPath.split("\\\\");
            String storageName = storageNames[storageNames.length - 1];
            HashMap<String, Privilege> storagesAndPrivileges = new HashMap<>();
            storagesAndPrivileges.put(storageNameAndPath, Privilege.ADMIN);
            this.initStoragesAndPrivileges(storagesAndPrivileges);
            Storage storage = new Storage(storageName, this, storageNameAndPath, storageNameAndPath);
            storage.setStorageSize(0);
            storage.setDirsMaxChildrenCount(new HashMap<>());
            storage.setForbiddenExtensions(new ArrayList<>());
            this.setCurrentActiveStorage(storage);
            try {
                usersJson.createNewFile();
                storageJson.createNewFile();
                ISerialization ser = UserManager.getUserSerializator();
                ser.saveUserData(usersJson.getPath(), this.getUserName(), this.getPassword(), this.getStoragesAndPrivileges(), false);
                ser.saveStorageData(storageJson.getPath(), storage);
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        } else {
            return 0;
        }
        return 1;
    }

    @Override
    public int logIn(String storageNameAndPath, String username, String password) {
        File usersJson = new File(storageNameAndPath + "\\users.json");
        File storageJson = new File(storageNameAndPath + "\\storage.json");
        ISerialization ser = UserManager.getUserSerializator();
        List<UserData> usersData = ser.readSavedUsers(usersJson.getPath());
        StorageData storageData = ser.readStorageData(storageJson.getPath());
        boolean flag = false;
        for(UserData ud: usersData){

            if(ud.getUserName().equalsIgnoreCase(username) && ud.getPassword().equals(password)) {
                flag = true;
                this.setUserName(username);
                this.setPassword(password);
                this.setStoragesAndPrivileges(ud.getStoragesAndPrivileges());
            }
        }
        if(!flag)
            return 0;

        String[] storageNames  = storageNameAndPath.split("\\\\");
        String storageName = storageNames[storageNames.length - 1];
        Storage storage = new Storage(storageName , this, storageNameAndPath, storageNameAndPath);
        storage.setDirsMaxChildrenCount(storageData.getDirsMaxChildrenCount());
        storage.setStorageSize(storageData.getStorageSize());
        storage.setForbiddenExtensions(storageData.getForbiddenExtensions());
        this.setCurrentActiveStorage(storage);
        return 1;
    }

    @Override
    public int createDir(String dirName, String path) {
        if(!checkPrivilege(Privilege.UPLOAD)){
            System.out.println("Nemate dovoljno visok nivo privilegije za ovu operaciju.");
            return 0;
        }
        File newFolder = new File(this.getCurrentActiveStorage().getStorageID() + path + dirName);

        if(!canCreateFileOrFolder(this.getCurrentActiveStorage().getStorageID() + path, 1)){
            System.out.println("Cant create more dirs");
            return 0;
        }
        boolean isMade = newFolder.mkdirs();
        if(isMade)
            return 1;
        else
            return 0;
    }

    @Override
    public int createDir(String dirName, String path, String namePrefix, int numberOfFiles) {
        if(!checkPrivilege(Privilege.UPLOAD)){
            System.out.println("Nemate dovoljno visok nivo privilegije za ovu operaciju.");
            return 0;
        }
        if(!canCreateFileOrFolder(this.getCurrentActiveStorage().getStorageID() + path, 1)){
            System.out.println("Cant create more dirs");
            return 0;
        }
        for(int i = 0; i < numberOfFiles; i++){
            File newDir = new File(this.getCurrentActiveStorage().getStorageID() + path + "\\" + namePrefix + i);
            boolean isMade = newDir.mkdirs();
            if(!isMade)
                return 0;
        }
        return 1;
    }

    @Override
    public int createFile(String fileName, String path, String fileType) {
        if(!checkPrivilege(Privilege.UPLOAD)){
            System.out.println("Nemate dovoljno visok nivo privilegije za ovu operaciju.");
            return 0;
        }
        if(!canCreateFileOrFolder(this.getCurrentActiveStorage().getStorageID() + path, 1)){
            System.out.println("Cant create more files");
            return 0;
        }
        ArrayList<String> forbidenExtentions = (ArrayList<String>)this.getCurrentActiveStorage().getForbiddenExtensions();
        if(forbidenExtentions.contains(fileType)){
            System.out.println("forbiden Extention");
            return 0;
        }
        File newFile = new File(this.getCurrentActiveStorage().getStorageID() + path + "\\" + fileName + fileType);
        try {
            newFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    @Override
    public int uploadExistingFile(String fileName, String path, String whereToFind, String fileType) {
        if(!checkPrivilege(Privilege.UPLOAD)){
            System.out.println("Nemate dovoljno visok nivo privilegije za ovu operaciju.");
            return 0;
        }
        if(!canCreateFileOrFolder(this.getCurrentActiveStorage().getStorageID() + path, 1)){
            System.out.println("Cant upload more files");
            return 0;
        }
        ArrayList<String> forbidenExtentions = (ArrayList<String>)this.getCurrentActiveStorage().getForbiddenExtensions();
        if(forbidenExtentions.contains(fileType)){
            System.out.println("forbiden Extention");
            return 0;
        }
        File source = new File(whereToFind);

        File dest = new File(this.getCurrentActiveStorage().getStorageID() + path + "\\" + fileName + fileType);

        // + "\\" + fileName
        try {
            FileUtils.copyFile(source, dest);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    @Override
    public int move(Collection<String> files, String path) {
        if(!checkPrivilege(Privilege.UPLOAD)){
            System.out.println("Nemate dovoljno visok nivo privilegije za ovu operaciju.");
            return 0;
        }
        for(String filePath: files){
            if(move(filePath, path) == 0){
                return 0;
            }
        }
        return 1;
    }

    @Override
    public int move(String file, String path) {
        if(!checkPrivilege(Privilege.UPLOAD)){
            System.out.println("Nemate dovoljno visok nivo privilegije za ovu operaciju.");
            return 0;
        }
        String[] pathParts = file.split("\\\\");
        String fileName = pathParts[pathParts.length - 1];
        try {
            Files.move(Paths.get(this.getCurrentActiveStorage().getStorageID() + file), Paths.get(this.getCurrentActiveStorage().getStorageID() + path + "\\" + fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    @Override
    public int delete(String path) {
        if(!checkPrivilege(Privilege.DELETE)){
            System.out.println("Nemate dovoljno visok nivo privilegije za ovu operaciju.");
            return 0;
        }
        File toDelete = new File(this.getCurrentActiveStorage().getStorageID() + path);
        if(toDelete.isDirectory()){
            try {
                FileUtils.deleteDirectory(toDelete);
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        } else {
            try {
                Files.delete(toDelete.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 1;
    }

    @Override
    public int download(String path, String whereToDownload){
        if(!checkPrivilege(Privilege.DOWNLOAD)){
            System.out.println("Nemate dovoljno visok nivo privilegije za ovu operaciju.");
            return 0;
        }
        File source = new File(this.getCurrentActiveStorage().getStorageID() + path);
        String[] pathParts = path.split("\\\\");
        String fileName = pathParts[pathParts.length - 1];
        File dest = new File(whereToDownload + "\\" + fileName);
        if(source.isDirectory()){
            try {
                FileUtils.copyDirectory(source, dest);
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        }else{
            try {
                FileUtils.copyFile(source, dest);
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 1;
    }

    @Override
    public Collection<String> searchFilesInDir(String dirPath) {
        Collection<String> result = new ArrayList<>();
        if(!checkPrivilege(Privilege.READ)){
            System.out.println("Nemate dovoljno visok nivo privilegije za ovu operaciju.");
            return result;
        }
        File dir = new File(this.getCurrentActiveStorage().getStorageID() + dirPath);
        File[] listOfFiles  = dir.listFiles();
        for(File file: listOfFiles){
            if (!file.isDirectory())
                result.add(file.getPath());
        }
        return result;
    }

    @Override
    public Collection<String> searchDirsInDir(String dirPath) {
        Collection<String> result = new ArrayList<>();
        if(!checkPrivilege(Privilege.READ)){
            System.out.println("Nemate dovoljno visok nivo privilegije za ovu operaciju.");
            return result;
        }
        File dir = new File(this.getCurrentActiveStorage().getStorageID() + dirPath);
        File[] listOfFiles = dir.listFiles();

        for(File file: listOfFiles){
            if (file.isDirectory())
                result.add(file.getPath());
        }
        return result;
    }

    @Override
    public Collection<String> searchByName(String nameToFind) {
        Collection<String> result = new ArrayList<>();
        if(!checkPrivilege(Privilege.READ)){
            System.out.println("Nemate dovoljno visok nivo privilegije za ovu operaciju.");
            return result;
        }
        try {
            Stream<Path> paths = Files.walk(Paths.get(this.getCurrentActiveStorage().getStorageID()));
            paths.forEach( path -> {
                if(path.endsWith(nameToFind))
                    result.add(path.toString());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Collection<String> searchByExtension(String extension, String dirPath) {
        Collection<String> result = new ArrayList<>();
        if(!checkPrivilege(Privilege.READ)){
            System.out.println("Nemate dovoljno visok nivo privilegije za ovu operaciju.");
            return result;
        }
        try {
            Stream<Path> paths = Files.walk(Paths.get(this.getCurrentActiveStorage().getStorageID()));
            paths.forEach( path -> {
                if(path.toString().contains(extension))
                    result.add(path.toString());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Collection<String> getFilesInDirSortedByName(String s) {
        Collection<String> result = new ArrayList<>();
        if(!checkPrivilege(Privilege.READ)){
            System.out.println("Nemate dovoljno visok nivo privilegije za ovu operaciju.");
            return result;
        }
        File dir = new File(this.getCurrentActiveStorage().getStorageID() + s);
        File[] listOfFiles  = dir.listFiles();

        for(File file: listOfFiles){
            if (!file.isDirectory())
                result.add(file.getPath());
        }
        Collections.sort((List)result);
        return result;
    }

    @Override
    public String getModificationDate(String path) {
        if(!checkPrivilege(Privilege.READ)){
            System.out.println("Nemate dovoljno visok nivo privilegije za ovu operaciju.");
            return "";
        }
        File file = new File(this.getCurrentActiveStorage().getStorageID() + path);
        long modificationDate = file.lastModified();
        DateFormat sdf  = new SimpleDateFormat("hh:mm:ss, dd/MM/yyyy");
        return sdf.format(modificationDate);
    }

    @Override
    public String getCreationDate(String path) {
        if(!checkPrivilege(Privilege.READ)){
            System.out.println("Nemate dovoljno visok nivo privilegije za ovu operaciju.");
            return "";
        }
        File file = new File(this.getCurrentActiveStorage().getStorageID() + path);

        DateFormat sdf  = new SimpleDateFormat("hh:mm:ss, dd/MM/yyyy");
        return sdf.format(this.fileOrDirCreationDate(file));
    }

    private Date fileOrDirCreationDate(File file){
        if(!checkPrivilege(Privilege.READ)){
            System.out.println("Nemate dovoljno visok nivo privilegije za ovu operaciju.");
            return null;
        }
        BasicFileAttributes attrs;
        try {
            attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            FileTime time = attrs.creationTime();
            return new Date(time.toMillis());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Collection<String> searchByDateCreationRange(Date date, Date date1) {
        Collection<String> result = new ArrayList<>();
        if(!checkPrivilege(Privilege.READ)){
            System.out.println("Nemate dovoljno visok nivo privilegije za ovu operaciju.");
            return result;
        }
        try {
            Stream<Path> paths = Files.walk(Paths.get(this.getCurrentActiveStorage().getStorageID()));
            paths.forEach( path -> {
                Date creationDate = this.fileOrDirCreationDate(new File(path.toString()));
                if( date.compareTo(creationDate) <= 0 && date1.compareTo(creationDate) >= 0){
                    result.add(path.toString());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Collection<String> searchFilesInDirByDateCreationRange(Date date, Date date1, String path) {
        Collection<String> result = new ArrayList<>();
        if(!checkPrivilege(Privilege.READ)){
            System.out.println("Nemate dovoljno visok nivo privilegije za ovu operaciju.");
            return result;
        }
        File dir = new File(this.getCurrentActiveStorage().getStorageID() + path);
        File[] listOfFiles  = dir.listFiles();
        for(File file: listOfFiles){
            Date creationDate = this.fileOrDirCreationDate(file);
            if( date.compareTo(creationDate) <= 0 && date1.compareTo(creationDate) >= 0 && !file.isDirectory()){
                result.add(file.getPath());
            }
        }
        return result;

    }

    @Override
    public int setStorageSize(int bytes) {
        if(!checkPrivilege(Privilege.ADMIN)){
            System.out.println("Nemate dovoljno visok nivo privilegije za ovu operaciju.");
            return 0;
        }
        System.out.println("POZVAN LOKAL SET STORAGE SIZE");
        File storageJson = new File(this.getCurrentActiveStorage().getStorageID() + "\\storage.json");
        ISerialization ser = UserManager.getUserSerializator();
        this.getCurrentActiveStorage().setStorageSize(bytes);
        ser.saveStorageData(storageJson.getPath(), this.getCurrentActiveStorage());
        return 1;
    }

    @Override
    public int setForbiddenExtensions(Collection<String> collection) {
        if(!checkPrivilege(Privilege.ADMIN)){
            System.out.println("Nemate dovoljno visok nivo privilegije za ovu operaciju.");
            return 0;
        }
        File storageJson = new File(this.getCurrentActiveStorage().getStorageID() + "\\storage.json");
        ISerialization ser = UserManager.getUserSerializator();
        this.getCurrentActiveStorage().setForbiddenExtensions(collection);
        ser.saveStorageData(storageJson.getPath(), this.getCurrentActiveStorage());
        return 1;
    }

    @Override
    public int setMaxFileNumberInDir(int number, String dirPath) {
        if(!checkPrivilege(Privilege.ADMIN)){
            System.out.println("Nemate dovoljno visok nivo privilegije za ovu operaciju.");
            return 0;
        }
        File storageJson = new File(this.getCurrentActiveStorage().getStorageID() + "\\storage.json");
        ISerialization ser = UserManager.getUserSerializator();
        this.getCurrentActiveStorage().getDirsMaxChildrenCount().put(
                this.getCurrentActiveStorage().getStorageID() + dirPath,
                number
        );
        this.getCurrentActiveStorage().setDirsMaxChildrenCount(
                this.getCurrentActiveStorage().getDirsMaxChildrenCount()
        );
        ser.saveStorageData(storageJson.getPath(), this.getCurrentActiveStorage());
        return 1;
    }

    @Override
    public int addUser(String username, String password, Privilege privilege) {
        if(!checkPrivilege(Privilege.ADMIN)){
            System.out.println("Nemate dovoljno visok nivo privilegije za ovu operaciju.");
            return 0;
        }
        File usersJson = new File(this.getCurrentActiveStorage().getStorageID() + "\\users.json");
        ISerialization ser = UserManager.getUserSerializator();
        HashMap<String, Privilege> storagesAndPrivileges = new HashMap<>();
        storagesAndPrivileges.put(this.getCurrentActiveStorage().getStorageID(), privilege);
        ser.saveUserData(usersJson.getPath(), username, password, storagesAndPrivileges, true);
        return 1;
    }

    @Override
    public int removeUser(String username) {
        if(!checkPrivilege(Privilege.ADMIN)){
            System.out.println("Nemate dovoljno visok nivo privilegije za ovu operaciju.");
            return 0;
        }
        System.out.println("pozvan");
        File usersJson = new File(this.getCurrentActiveStorage().getStorageID() + "\\users.json");
        ISerialization ser = UserManager.getUserSerializator();
        List<UserData> users = ser.readSavedUsers(usersJson.getPath());
        UserData toDelete = null;
        for(UserData user: users){
            System.out.println("trenutno: " + user.getUserName());
            if((user.getUserName()).equalsIgnoreCase(username)){
                System.out.println("usao za: " + user.getUserName()) ;
                toDelete = user;

            }
        }
        if(toDelete != null){
            System.out.println("desio se delete");
            users.remove(toDelete);

        }
        boolean flag = true;
        for(UserData user: users){
            System.out.println("Dodavanje za : " + user.getUserName());
            if(flag){
                ser.saveUserData(usersJson.getPath(), user.getUserName(), user.getPassword(), user.getStoragesAndPrivileges(), false);
                System.out.println("append false ");
                flag = false;
            }
            else{
                ser.saveUserData(usersJson.getPath(), user.getUserName(), user.getPassword(), user.getStoragesAndPrivileges(), true);
                System.out.println("append true ");
            }
        }
        return 1;
    }

    private boolean checkPrivilege(Privilege minimumPrivilegeLevel){
        Privilege privilege = null;
        for (String stID: super.getStoragesAndPrivileges().keySet()) {
            if(stID.equalsIgnoreCase(super.getCurrentActiveStorage().getStorageID())){
                privilege = super.getStoragesAndPrivileges().get(stID);
            }
        }
        if(privilege != null && privilege.ordinal() >= minimumPrivilegeLevel.ordinal()){
            return true;
        }
        return false;
    }

    private boolean canCreateFileOrFolder(String path, int desiredNumber){
        System.out.println("path: " + path);
        File dir = new File(path);
        File[] listOfFiles = dir.listFiles();
        Stream<Path> files = null;
        try {
            files = Files.list(Paths.get(dir.getPath()));
            long count = files.count();
            int maxChildrenAllowed = 0;

            if((this.getCurrentActiveStorage().getDirsMaxChildrenCount()).get(path.substring(0, path.length() - 1)) != null) {
                System.out.println(this.getCurrentActiveStorage().getDirsMaxChildrenCount());
                maxChildrenAllowed = (this.getCurrentActiveStorage().getDirsMaxChildrenCount()).get(path.substring(0, path.length() - 1));
            }
            if(maxChildrenAllowed == 0){
                return true;
            }
            System.out.println(listOfFiles.length + desiredNumber);
            System.out.println(count);
            System.out.println(listOfFiles.length);
            System.out.println(desiredNumber);
            if(maxChildrenAllowed < listOfFiles.length + desiredNumber){
                return false;
            }
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

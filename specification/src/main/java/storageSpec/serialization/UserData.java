package storageSpec.serialization;

import lombok.Getter;
import lombok.Setter;
import storageSpec.Privilege;

import java.util.Map;

/**
 * Data-transfer object carrying a user's persisted credentials and privilege map.
 */
@Setter
@Getter
public class UserData {

    /**
     * User's login name.
     */
    private String userName;

    /**
     * User's password.
     */
    private String password;

    /**
     * Map of storageID → {@link Privilege} this user holds.
     */
    private Map<String, Privilege> storagesAndPrivileges;

    /**
     * Constructs an empty UserData.
     */
    public UserData() {
    }
}

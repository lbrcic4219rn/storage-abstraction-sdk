package storageSpec;

import lombok.RequiredArgsConstructor;

/**
 * Hierarchical user privileges.
 * Each level implicitly includes all levels below it
 * READ     -> can list and read files
 * DOWNLOAD -> READ + download files
 * UPLOAD   -> DOWNLOAD + create/upload files
 * DELETE   -> UPLOAD + delete files and directories
 * ADMIN    -> DELETE + storage configuration and user management
 */

@RequiredArgsConstructor
public enum Privilege {
    READ(0),
    DOWNLOAD(1),
    UPLOAD(2),
    DELETE(3),
    ADMIN(4);

    private final int level;

    public boolean satisfies(Privilege other) {
        return this.level >= other.level;
    }
}

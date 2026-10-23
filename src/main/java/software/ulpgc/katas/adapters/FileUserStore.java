package software.ulpgc.katas.adapters;

import software.ulpgc.katas.model.User;
import software.ulpgc.katas.ports.UserStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileUserStore implements UserStore {
    private final File root;

    public FileUserStore(File root) {
        this.root = root;
        checkFolderExists();
    }

    private void checkFolderExists() {
        if (root.exists()) return;
        root.mkdirs();
    }

    @Override
    public void store(User user) {
        try (OutputStream outputStream = new FileOutputStream(new File(root, user.name() + ".jpg"))) {
            outputStream.write(user.picture());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

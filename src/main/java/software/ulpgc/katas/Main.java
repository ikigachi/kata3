package software.ulpgc.katas;

import software.ulpgc.katas.adapters.FileUserStore;
import software.ulpgc.katas.adapters.RandomUserReader;
import software.ulpgc.katas.model.User;
import software.ulpgc.katas.ports.UserStore;

import java.io.*;
import java.util.List;

public class Main {
    static void main() throws IOException {
        List<User> users = new RandomUserReader().read(10);
        UserStore store = new FileUserStore(new File("users"));
        for (User user : users)
            store.store(user);

    }

}

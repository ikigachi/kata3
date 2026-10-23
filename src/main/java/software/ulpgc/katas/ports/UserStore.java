package software.ulpgc.katas.ports;

import software.ulpgc.katas.model.User;

public interface UserStore {
    void store(User user);
}

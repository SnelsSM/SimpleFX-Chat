package collections;

import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by snels on 03.12.2016.
 */
public class Users {

    private ConcurrentHashMap<String, ObjectOutputStream> usersMap = new ConcurrentHashMap<String, ObjectOutputStream>();
    private CopyOnWriteArrayList<String> usersList = new CopyOnWriteArrayList<String>();

    public void addUser(String login, ObjectOutputStream objectOutputStream) {
        usersMap.put(login, objectOutputStream);
        usersList.add(login);
    }

    public void removeUser(String login) {
        usersMap.remove(login);
        usersList.remove(login);
    }

    public ConcurrentHashMap getUsersMap() {
        return usersMap;
    }

    public CopyOnWriteArrayList<String> getUsersList() {
        return usersList;
    }
}

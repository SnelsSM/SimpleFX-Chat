package sqltasks;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by snels on 01.12.2016.
 */
public class History {
    private int id;
    private CopyOnWriteArrayList messages;

    public History() {

    }

    public History(int id, CopyOnWriteArrayList messages) {
        this.id = id;
        this.messages = messages;
    }

    public int getId() {
        return id;
    }

    public CopyOnWriteArrayList getMessages() {
        return messages;
    }
}


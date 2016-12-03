package messages;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by snels on 02.12.2016.
 */
public class MessageHistory implements Serializable{

    private ArrayList<Message> messages = new ArrayList<Message>();

    public void addMessage(Message message) {
        messages.add(message);
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }
}

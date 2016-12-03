package messages;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by snels on 21.11.2016.
 */
public class Message implements Serializable {

    private String login;
    private String message;
    private long date;
    private MESSAGE_TYPE messageType;
    private CopyOnWriteArrayList usersOnline;

    public enum MESSAGE_TYPE {
        HELLO,
        BYE,
        LOGINERROR,
        SERVER,
        HISTORY
    }

    public Message() {

    }

    // Конструктор для обычного сообщения клиента
    public Message(String login, String message, long date) {
        this.login = login;
        this.message = message;
        this.date = date;
    }

    public Message(String login, String message, long date, MESSAGE_TYPE messageType) {
        this.login = login;
        this.message = message;
        this.date = date;
        this.messageType = messageType;
    }

    public Message(String login, MESSAGE_TYPE messageType) {
        this.login = login;
        this.messageType = messageType;
        this.date = new Date().getTime();
    }

    public Message(String login, MESSAGE_TYPE messageType, CopyOnWriteArrayList usersOnline) {
        this.login = login;
        this.messageType = messageType;
        this.usersOnline = usersOnline;
        this.date = new Date().getTime();
    }

    public String getLogin() {
        return login;
    }

    public String getMessage() {
        return message;
    }

    public MESSAGE_TYPE getMessageType() {
        return messageType;
    }

    public CopyOnWriteArrayList getUsersOnline() {
        return usersOnline;
    }

    public long getDate() {
        return this.date;
    }

    public void setDate() {
        this.date = new Date().getTime();
    }
}

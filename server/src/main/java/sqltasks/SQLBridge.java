package sqltasks;

import messages.Message;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by snels on 01.12.2016.
 */
public class SQLBridge {

    private SQLQueries sqlQueries = new SQLQueries();
    private CopyOnWriteArrayList<Message> messagesList = new CopyOnWriteArrayList<Message>();
    private int historyLim = 5;
    private History history;

    public void startSQL() {
        sqlQueries.connectDB();
        sqlQueries.createDB();
    }

    public void addMessage(Message message) {
        if (messagesList.size() >= historyLim) {
            messagesList.add(message);
            messagesList.remove(0);
        } else {
            messagesList.add(message);
        }
        sqlQueries.writeDB(message.getLogin(), message.getMessage(), message.getDate());
    }

    public History getHistory() {
        if (messagesList.size() < historyLim) {
            history = sqlQueries.readDB(-1);
            messagesList = history.getMessages();
        } else {
            history = new History(sqlQueries.getId(), messagesList);
        }
        return history;
    }

    public History upHistory(int id) {
        return sqlQueries.readDB(id);
    }

    public int getId() {
        return sqlQueries.getId();
    }

}
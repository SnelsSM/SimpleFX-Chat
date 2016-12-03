package controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.web.WebView;
import messages.Message;
import messages.MessageHistory;
import methods.HTMLSpecialChars;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;

import static messages.Message.MESSAGE_TYPE.HISTORY;

/**
 * Created by snels on 26.11.2016.
 */

public class Connection {
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private MainController mainController;
    private WebView webView;
    private boolean loginError;
    private boolean hostError;
    private Thread thread;
    private String login;
    private String css_id;

    private ObservableList<String> usersOnline = FXCollections.observableArrayList();
    private ObservableList<String> messages = FXCollections.observableArrayList();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

    private HTMLSpecialChars chars = new HTMLSpecialChars();

    public void setWebView(WebView webView) {
        this.webView = webView;
    }

    public ObservableList<String> getUsersOnline() {
        return usersOnline;
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    public ObservableList<String> getMessages() {
        return messages;
    }

    public boolean isLoginError() {
        return loginError;
    }

    public boolean isHostError() {
        return hostError;
    }


    public void startConnection(String host, int port, String login) {
        try {
            if (login.length() < 4) { return; }
            hostError = false;
            socket = new Socket(host, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(new Message(login, Message.MESSAGE_TYPE.HELLO));
            inputStream = new ObjectInputStream(socket.getInputStream());
            if (((Message) inputStream.readObject()).getMessageType() == Message.MESSAGE_TYPE.LOGINERROR) {
                loginError = true;
                try {socket.close();} catch (IOException e) {e.printStackTrace();}
                return;
                }

            this.login = login;
            thread = new Thread(socketListener);
            thread.setDaemon(true);
            thread.start();  // Запускаем в отдельном потоке слушатель приходящих сообщений
        } catch (IOException e) {
            hostError = true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Thread.State getThreadState() {
        return thread.getState();
    }

    public void sendMessage(String login, String message) throws IOException {
        outputStream.writeObject(new Message(login, chars.stringToHtmlString(message), 0));
    }

    public void historyQuery() {
        try {
            outputStream.writeObject(new Message(login, Message.MESSAGE_TYPE.HISTORY));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Task socketListener = new Task() {
        @Override
        protected Object call() throws Exception {
            while (true) {
                Object is = inputStream.readObject();


                // Это срань, без которой JavaFX валит исключениями
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (is instanceof Message) {
                            Message message = (Message) is;
                        if (message.getLogin().equals(login)) css_id = "from_me";
                        else css_id = "to_me";
                        //Собственно, добавляем к нашему списку новое сообщение
                        if (message.getMessageType() == Message.MESSAGE_TYPE.HELLO) { //Если подключился пользователь

                            if (usersOnline.size() == 0) usersOnline.addAll(message.getUsersOnline());
                            else usersOnline.add(message.getLogin());
                            messages.add(message.getLogin() + " подключился к чату");
                            mainController.setListMessages("add('System','" + message.getLogin() + " подключился к чату', 'from_server','" + message.getDate() + "')");

                        } else if (message.getMessageType() == Message.MESSAGE_TYPE.BYE) {

                            usersOnline.remove(message.getLogin());
                            messages.add(message.getLogin() + " отключился от чата");
                            mainController.setListMessages("add('System','" + message.getLogin() + " отключился от чата', 'from_server','" + message.getDate() + "')");

                        } else if (message.getMessageType() == Message.MESSAGE_TYPE.LOGINERROR) {


                        } else if (message.getMessageType() == Message.MESSAGE_TYPE.SERVER) {

                        } else {
                            mainController.setListMessages("add('" + message.getLogin() + "','" + message.getMessage() + "','" + css_id + "','" + message.getDate() + "')");
                            messages.add(message.getLogin() + ": " + message.getMessage());
                        }
                    } else if (is instanceof MessageHistory) {
                            mainController.upHistory((MessageHistory)is);
                        }
                    }
                });
            }
        }
    };

}

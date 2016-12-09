package controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.web.WebView;
import messages.HeartBeat;
import messages.Message;
import messages.MessageHistory;
import methods.HTMLSpecialChars;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static messages.Message.MESSAGE_TYPE.HISTORY;

/**
 * Created by snels on 26.11.2016.
 */

public class Connection {
    private Socket socket;
    private String host;
    private int port;
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

    private HTMLSpecialChars chars = new HTMLSpecialChars();


    public ObservableList<String> getUsersOnline() {
        return usersOnline;
    }


    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    public boolean isLoginError() {
        return loginError;
    }

    public boolean isHostError() {
        return hostError;
    }


    public void startConnection(String host, int port, String login) {

        this.host = host;
        this.port = port;

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
            startTask ();
              // Запускаем в отдельном потоке слушатель приходящих сообщений


            Thread ping = new Thread(heartBeat);
            ping.setDaemon(true);
            ping.start();

        } catch (IOException e) {
            hostError = true;
            System.out.println(hostError);
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

    public void startTask () {
        this.thread = new Thread(socketListener);
        this.thread.setDaemon(true);
        this.thread.start();
    }

    Task socketListener = new Task() {
        @Override
        protected Object call() throws Exception {
            System.out.println("Поток заработал");
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



    Task heartBeat = new Task() {
        @Override
        protected Object call() {
            int i = 0;
            while (true) {
                System.out.println("Поток 1 " + thread.getState());
                try {
                    Thread.sleep(5000);
                    if (hostError) {
                        hostError = false;
                        reConnect();
                        startTask();
                    }
                    outputStream.writeObject(i++);
                } catch (IOException e) {
                    System.out.println("Сработало 1");
                    hostError = true;
                } catch (InterruptedException e) {

                }
            }
        }
    };

    private void reConnect() throws IOException {
        socket.close();
        inputStream.close();

        socket = new Socket(host, port);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.writeObject(new Message(login, Message.MESSAGE_TYPE.HELLO));
        inputStream = new ObjectInputStream(socket.getInputStream());

        System.out.println(thread.getState());
        System.out.println("Реконнект закончен");
    }
}

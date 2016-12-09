package threads;

import collections.Users;
import messages.Message;
import messages.MessageHistory;
import sqltasks.History;
import sqltasks.SQLBridge;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by snels on 21.11.2016.
 */
public class ClientThread extends Thread {
    private Socket socket;
    private SQLBridge SQLBridge;
    private Users users;
    private ArrayList user;
    private ObjectOutputStream outputStream;
    private Message message;
    private Object input;
    private String login;
    private boolean isAdded = false;
    private boolean isExist = false;
    private int id;


    public ClientThread(Socket socket, SQLBridge SQLBridge, Users users) {
        this.socket = socket;
        this.SQLBridge = SQLBridge;
        this.users = users;
        this.start();
    }

    public void run() {

        try {

            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());;
            id = SQLBridge.getId();


            while (true) {
                input = inputStream.readObject();
                if (input instanceof Integer) {
                    System.out.println((Integer)input);
                    outputStream.writeObject(input);
                } else {
                    message = (Message) input;
                    message.setDate();

                    if (!isAdded) {
                        login = message.getLogin();
                        for (String newLogin : users.getUsersList()) {
                            if (newLogin.equals(login)) {
                                outputStream.writeObject(new Message(login, Message.MESSAGE_TYPE.LOGINERROR));
                                socket.close();
                                isExist = true;
                                return;
                            }
                        }
                        if (!isExist) {
                            users.addUser(login, outputStream);
                            isAdded = true;
                            broadcast(new Message(login, Message.MESSAGE_TYPE.SERVER));
                        }

                        user = SQLBridge.getUser(login, socket);


                        for (Object message : SQLBridge.getHistory().getMessages()) {
                            outputStream.writeObject(message);
                        }
                    }

                    if (message.getMessageType() == Message.MESSAGE_TYPE.HELLO) {
                        if (!isExist)
                            System.out.println("Пользователь " + login + " присоединился к чату");

                    } else if (message.getMessageType() == Message.MESSAGE_TYPE.HISTORY) {
                        if (id != 0) {
                            History temp = SQLBridge.upHistory(id);
                            MessageHistory messageHistory = new MessageHistory();
                            for (Object message : temp.getMessages()) {
                                messageHistory.addMessage((Message) message);
                            }

                            outputStream.writeObject(messageHistory);
                            this.id = temp.getId();
                            System.out.println(this.id);
                        }
                    } else {
                        SQLBridge.addMessage(message);
                        System.out.println("[" + login + "]: " + message.getMessage());
                    }


                    broadcast(message);
                }
            }

        } catch (IOException e) {
            // Поток более недоступен, поэтому удаляем его из коллекции
            if (!isExist)
                System.out.println("Пользователь " + login + " выходит из чата");

            users.removeUser(login);
            broadcast(new Message(login, Message.MESSAGE_TYPE.BYE, users.getUsersList()));

            // И закрываем сокет
            try {
                socket.close();
            } catch (IOException io) {
                io.printStackTrace();
            }
        } catch (ClassNotFoundException nf) {
            nf.printStackTrace();
        }
    }

    private void broadcast(Message message) {

        try {
                ConcurrentHashMap<String, ObjectOutputStream> map = users.getUsersMap();
            for (Map.Entry entry : map.entrySet()) {
                if (message.getMessageType() != Message.MESSAGE_TYPE.HISTORY) {
                    if (message.getMessageType() == Message.MESSAGE_TYPE.HELLO) {
                        ((ObjectOutputStream) entry.getValue()).writeObject(new Message(login, Message.MESSAGE_TYPE.HELLO, users.getUsersList()));
                    } else if (message.getMessageType() == Message.MESSAGE_TYPE.BYE) {
                        ((ObjectOutputStream) entry.getValue()).writeObject(new Message(login, Message.MESSAGE_TYPE.BYE, users.getUsersList()));
                    } else {
                        ((ObjectOutputStream) entry.getValue()).writeObject(message);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Упс!");
            return;
        }
    }
}
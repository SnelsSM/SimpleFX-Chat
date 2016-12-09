package sqltasks;

import messages.Message;

import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by snels on 01.12.2016.
 */
public class SQLQueries {

    private Connection connect;
    private Statement statement;
    private ResultSet resultSet;

    public void connectDB(){
        connect = null;
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connect = DriverManager.getConnection("jdbc:sqlite:ChatLog.s3db");
            statement = connect.createStatement();
            System.out.println("База подключена");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createDB() {
        try {
            statement = connect.createStatement();
            statement.execute("CREATE TABLE if not exists 'public' " +
                    "('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'date' INT, 'name' text, 'message' text);");

            statement.execute("CREATE TABLE if not exists 'users' " +
                    "('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'login' text, 'socket' BLOB, 'access' INT DEFAULT '1');");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeDB() {
        try {
            connect.close();
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void writeDB(String name, String message, long time) {
        try {
            statement.execute("INSERT INTO 'public' " +
                    "('date', 'name', 'message') VALUES ('" +time+ "', '" +name+ "', '" +message+ "'); ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public History readDB(int atId) {
        int id = 0;
        CopyOnWriteArrayList<Message> list = new CopyOnWriteArrayList<Message>();
        try {
            if (atId == -1) {
                resultSet = statement.executeQuery("SELECT * FROM public ORDER BY id DESC LIMIT 5");
            } else {
                resultSet = statement.executeQuery("SELECT * FROM public WHERE id<" + atId + " ORDER BY id DESC LIMIT 5");
            }

            while(resultSet.next()) {
                if (id == 0) id = resultSet.getInt("id");
                if (id > resultSet.getInt("id")) id = resultSet.getInt("id");

                long date = resultSet.getInt("date");
                String name = resultSet.getString("name");
                String message = resultSet.getString("message");
                list.add(0, new Message(name, message, date));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new History(id, list);
    }

    public ArrayList<Object> getUser(String login, Socket socket) {
        ArrayList<Object> list = new ArrayList<Object>();
        try {
            resultSet = statement.executeQuery("SELECT * FROM users WHERE login = '" + login + "' LIMIT 1;");

            if(!resultSet.next()) {
                statement.executeUpdate("INSERT INTO 'users' " +
                        "('login', 'socket') VALUES ('" + login + "','" + socket + "');");
                resultSet = statement.executeQuery("SELECT * FROM users WHERE login = '" + login + "' LIMIT 1;");
                resultSet.next();
            }
            list.add(resultSet.getInt("id"));
            list.add(resultSet.getString("login"));
            list.add(resultSet.getObject("socket"));
            list.add(resultSet.getInt("access"));

        } catch (SQLException e) {
            e.printStackTrace();
        }

       return list;
    }


    public int getId() {
        int i = 0;
        try {
            resultSet = statement.executeQuery("SELECT max(id) FROM public;");
            if (resultSet.next())
                i = (resultSet.getInt(1) - 5 + 1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

}
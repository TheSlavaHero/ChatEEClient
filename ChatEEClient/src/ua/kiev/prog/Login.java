package ua.kiev.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class Login {
    private String login;
    private String password;
    private boolean online;
    private long time;

    public Login(String login, String password) {
        this.login = login;
        this.password = password;
        this.time = System.currentTimeMillis();
    }

    public Login() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public long getTime() {
        return time;
    }

    public void setTime() {
        this.time = System.currentTimeMillis();
    }

    public String toJSON() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    public static Login fromJSON(String s) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(s, Login.class);
    }

    @Override
    public String toString() {
        return "Login{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", online=" + online +
                ", time=" + time +
                '}';
    }
}

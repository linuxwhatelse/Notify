package de.linuxwhatelse.android.notify.models;

/**
 * Created by tadly on 12/24/14.
 */
public class Client {
    private int id;
    private String name = "";
    private String host = "";
    private int port = 8022;
    private String user = "";
    private String pwd = "";
    private String allowedSSID = "";
    private boolean isActive = true;
    private boolean overwriteGlobalNotifications = false;
    private boolean overwriteGlobalEvents = false;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getAllowedSSID() {
        return allowedSSID;
    }

    public void setAllowedSSID(String allowedSSID) {
        this.allowedSSID = allowedSSID;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isOverwriteGlobalNotifications() {
        return overwriteGlobalNotifications;
    }

    public void setOverwriteGlobalNotifications(boolean overwriteGlobalNotifications) {
        this.overwriteGlobalNotifications = overwriteGlobalNotifications;
    }

    public boolean isOverwriteGlobalEvents() {
        return overwriteGlobalEvents;
    }

    public void setOverwriteGlobalEvents(boolean overwriteGlobalEvents) {
        this.overwriteGlobalEvents = overwriteGlobalEvents;
    }

}

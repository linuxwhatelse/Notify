package de.linuxwhatelse.android.notify.models;

import android.graphics.drawable.Drawable;

/**
 * Created by tadly on 12/23/14.
 */
public class Application {
    private String appName;
    private String appPackage;
    private Drawable appIcon;
    private boolean isActivated;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean isActivated) {
        this.isActivated = isActivated;
    }
}

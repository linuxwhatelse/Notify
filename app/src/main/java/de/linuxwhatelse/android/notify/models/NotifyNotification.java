package de.linuxwhatelse.android.notify.models;

import android.app.Notification;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by tadly on 12/25/14.
 */
public class NotifyNotification {
    private Context context;

    private int id;
    private String appName;
    private String appPackage;
    private String title;
    private String text;
    private String bigText;
    private String infoText;
    private String tickerText;
    private String subText;
    private int displayTime;
    private String largeIcon;
    private String appIcon;
    private String smallIcon;
    private Notification.Action[] actions;

    public NotifyNotification() {
    }

    public NotifyNotification(Context context, StatusBarNotification sbn) {
        this.context = context;

        Notification mNotification = sbn.getNotification();

        Bundle extras = mNotification.extras;

        String largeIcon = getBase64EncodedIcon((Bitmap) extras.getParcelable(Notification.EXTRA_LARGE_ICON));

        this.setId(sbn.getId());
        this.setAppName(getAppName(sbn));

        ;
        this.setTitle(extras.getCharSequence(Notification.EXTRA_TITLE, "").toString());
        this.setText(extras.getCharSequence(Notification.EXTRA_TEXT, "").toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.setBigText(extras.getCharSequence(Notification.EXTRA_BIG_TEXT, "").toString());
        } else {
            this.setBigText("");
        }
        this.setInfoText(extras.getCharSequence(Notification.EXTRA_INFO_TEXT, "").toString());
        this.setTickerText((mNotification.tickerText != null) ? mNotification.tickerText.toString() : "");
        this.setSubText(extras.getCharSequence(Notification.EXTRA_SUB_TEXT, "").toString());
        this.setDisplayTime(5000);
        this.setLargeIcon(largeIcon);
        this.setAppIcon(getApplicationIcon(sbn));
        this.setSmallIcon(getNotificationSmallIcon(sbn, extras));
        this.setActions(mNotification.actions);

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getBigText() {
        return bigText;
    }

    public void setBigText(String bigText) {
        this.bigText = bigText;
    }

    public String getInfoText() {
        return infoText;
    }

    public void setInfoText(String infoText) {
        this.infoText = infoText;
    }

    public String getTickerText() {
        return tickerText;
    }

    public void setTickerText(String tickerText) {
        this.tickerText = tickerText;
    }

    public String getSubText() {
        return subText;
    }

    public void setSubText(String subText) {
        this.subText = subText;
    }

    public int getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(int displayTime) {
        this.displayTime = displayTime;
    }

    public String getLargeIcon() {
        return largeIcon;
    }

    public void setLargeIcon(String largeIcon) {
        this.largeIcon = largeIcon;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public String getSmallIcon() {
        return smallIcon;
    }

    public void setSmallIcon(String smallIcon) {
        this.smallIcon = smallIcon;
    }

    public Notification.Action[] getActions() {
        return this.actions;
    }

    public void setActions(Notification.Action[] actions) {
        this.actions = actions;
    }

    public JSONObject getAsJSON() {
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject largeIcon = new JSONObject();
            largeIcon.put("mimetype", "image/png");
            largeIcon.put("data", this.getLargeIcon());

            JSONObject appIcon = new JSONObject();
            appIcon.put("mimetype", "image/png");
            appIcon.put("data", this.getAppIcon());

            JSONObject smallIcon = new JSONObject();
            smallIcon.put("mimetype", "image/png");
            smallIcon.put("data", this.getSmallIcon());

            jsonObject.put("id", this.getId());
            jsonObject.put("appName", this.getAppName());
            jsonObject.put("title", this.getTitle());
            jsonObject.put("text", this.getText());
            jsonObject.put("bigText", this.getBigText());
            jsonObject.put("infoText", this.getInfoText());
            jsonObject.put("tickerText", this.getTickerText());
            jsonObject.put("subText", this.getSubText());
            jsonObject.put("displayTime", this.getDisplayTime());
            jsonObject.put("largeIcon", largeIcon);
            jsonObject.put("appIcon", appIcon);
            jsonObject.put("smallIcon", smallIcon);
        } catch (JSONException ex) {
            jsonObject = null;
        }

        return jsonObject;
    }


    /*
     * Helper functions to convert a
     * StatusBarNotification to a NotifyNontification
     */
    private String getAppName(StatusBarNotification sbn) {
        String appName;
        try {
            ApplicationInfo applicationInfo = this.context.createPackageContext(sbn.getPackageName(), Context.CONTEXT_IGNORE_SECURITY).getApplicationInfo();
            appName = applicationInfo.loadLabel(this.context.getPackageManager()).toString();
        } catch (PackageManager.NameNotFoundException e) {
            appName = "";
        }

        return appName;
    }

    private String getNotificationSmallIcon(StatusBarNotification sbn, Bundle extras) {
        String smallIcon = "";

        int smallIconResource = extras.getInt(Notification.EXTRA_SMALL_ICON);

        Context packageContext = null;
        try {
            packageContext = this.context.createPackageContext(sbn.getPackageName(), Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }

        try {
            Drawable smallIconDrawable = packageContext.getResources().getDrawable(smallIconResource, null);
            smallIcon = getBase64EncodedIcon(getBitmapFromDrawable(smallIconDrawable));
        } catch (Resources.NotFoundException e) {
            return "";
        }

        return smallIcon;
    }

    private String getApplicationIcon(StatusBarNotification sbn) {
        String applicationIcon = "";

        try {
            applicationIcon = getBase64EncodedIcon(getBitmapFromDrawable(this.context.getPackageManager().getApplicationIcon(sbn.getPackageName())));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return applicationIcon;
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private String getBase64EncodedIcon(Bitmap image) {
        if (image != null) {
            image = resizeBitmap(image, 96, 96);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            return Base64.encodeToString(byteArray, Base64.NO_WRAP);
        } else {
            return "";
        }
    }

    private Bitmap resizeBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        if (newWidth < width && newHeight < height) {
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);

            return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        } else {
            return bm;
        }
    }
}

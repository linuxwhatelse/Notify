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
    private StatusBarNotification sbn;
    private Notification notification;
    private Bundle notificationExtras;

    private int id;
    private String appName;
    private String packageName;
    private int priority;
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


    public NotifyNotification(Context context, StatusBarNotification sbn) {
        this.context = context;

        this.sbn = sbn;
        this.notification = sbn.getNotification();
        this.notificationExtras = this.notification.extras;

        String largeIcon = getBase64EncodedIcon((Bitmap) this.notificationExtras.getParcelable(Notification.EXTRA_LARGE_ICON));

        this.id = sbn.getId();
        this.appName = getAppName();
        this.packageName = sbn.getPackageName();
        this.priority = this.notification.priority;

        this.title = this.notificationExtras.getCharSequence(Notification.EXTRA_TITLE, "").toString();
        this.text = this.notificationExtras.getCharSequence(Notification.EXTRA_TEXT, "").toString();
        this.bigText = this.notificationExtras.getCharSequence(Notification.EXTRA_BIG_TEXT, "").toString();
        this.infoText = this.notificationExtras.getCharSequence(Notification.EXTRA_INFO_TEXT, "").toString();
        this.tickerText = (this.notification.tickerText != null) ? this.notification.tickerText.toString() : "";
        this.subText = this.notificationExtras.getCharSequence(Notification.EXTRA_SUB_TEXT, "").toString();
        this.displayTime = 5000;
        this.largeIcon = largeIcon;
        this.appIcon = getApplicationIcon();
        this.smallIcon = getNotificationSmallIcon();
        this.actions = this.notification.actions;

    }

    public JSONObject getAsJSON() {
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject largeIcon = new JSONObject();
            largeIcon.put("mimetype", "image/png");
            largeIcon.put("data", this.largeIcon);

            JSONObject appIcon = new JSONObject();
            appIcon.put("mimetype", "image/png");
            appIcon.put("data", this.appIcon);

            JSONObject smallIcon = new JSONObject();
            smallIcon.put("mimetype", "image/png");
            smallIcon.put("data", this.smallIcon);

            jsonObject.put("id", this.id);
            jsonObject.put("appName", this.appName);
            jsonObject.put("packageName", this.packageName);
            jsonObject.put("priority", this.priority);
            jsonObject.put("title", this.title);
            jsonObject.put("text", this.text);
            jsonObject.put("bigText", this.bigText);
            jsonObject.put("infoText", this.infoText);
            jsonObject.put("tickerText", this.tickerText);
            jsonObject.put("subText", this.subText);
            jsonObject.put("displayTime", this.displayTime);
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
    private String getAppName() {
        String appName;
        try {
            ApplicationInfo applicationInfo = this.context.createPackageContext(sbn.getPackageName(), Context.CONTEXT_IGNORE_SECURITY).getApplicationInfo();
            appName = applicationInfo.loadLabel(this.context.getPackageManager()).toString();
        } catch (PackageManager.NameNotFoundException e) {
            appName = "";
        }

        return appName;
    }

    private String getNotificationSmallIcon() {
        String smallIcon = "";

        int smallIconResource = this.notificationExtras.getInt(Notification.EXTRA_SMALL_ICON);

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

    private String getApplicationIcon() {
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

    public boolean hastTitleAndText() {
        return !this.title.equals("") &&
                (!this.text.equals("")
                        || !this.bigText.equals("")
                        || !this.infoText.equals("")
                        || !this.tickerText.equals("")
                        || !this.subText.equals(""));
    }
}

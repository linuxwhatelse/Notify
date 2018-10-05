package de.linuxwhatelse.android.notify.services;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Base64;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;

import de.linuxwhatelse.android.notify.models.Client;

/**
 * Created by tadly on 12/25/14.
 */
public class Publisher extends AsyncTask<Object, Void, Boolean> {
    Context context;

    Client client;
    String path;
    JSONObject data;


    public Publisher(Context context) {
        this.context = context;
    }

    public static void send(Context context, ArrayList<Client> clients, String path, JSONObject data) {
        for (Client client : clients) {
            Publisher sender = new Publisher(context);
            sender.execute(client, path, data);
        }
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        if (params.length != 3)
            return false;

        this.client = (Client) params[0];
        this.path = (String) params[1];
        this.data = (JSONObject) params[2];

        if (client == null)
            return false;

        if (path == null || path.equals(""))
            return false;

        if (data == null)
            return false;


        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        // Verify if the current SSID is allowed and check whether or not the client is reachable
        if (!client.getAllowedSSID().equals("") && wifiInfo.getSSID() != null && !client.getAllowedSSID().equals(wifiInfo.getSSID().replace("\"", ""))) {
            return false;
        }

        try {
            if (!InetAddress.getByName(client.getHost()).isReachable(1000)) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }


        String auth = null;
        if (!client.getUser().equals("") && !client.getPwd().equals("")) {
            try {
                auth = Base64.encodeToString((client.getUser() + ":" + client.getPwd()).getBytes("UTF-8"), Base64.NO_WRAP);
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }

        HttpURLConnection conn = null;
        try {
            URL url = new URL("http://" + client.getHost() + ":" + client.getPort() + path);

            byte[] bytes = data.toString().getBytes();

            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Encoding", "identity");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestProperty("Authorization", "Basic " + auth);

            BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());

            out.write(bytes);

            out.flush();
            out.close();

            return true;
        } catch (IOException e) {
            return false;
        } finally {
            assert conn != null;
            conn.disconnect();
        }
    }
}

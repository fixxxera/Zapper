package com.example.fixxxer.thingy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        WifiManager wifimanager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //Check if wifi is on and if not, enable it
        assert wifimanager != null;
        turnWifiOnIfDisabled(wifimanager);

        //Check for configured devices
        new UDP().execute();
//        try {
//            new Request().execute(new URL("http://192.168.0.99/GetAdminData"));
//            Log.e("Request", "Testing");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
    }


    private boolean isConnected() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return (connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected());
    }

    private void turnWifiOnIfDisabled(WifiManager wifimanager) {

        if (wifimanager.getWifiState() == WifiManager.WIFI_STATE_DISABLED || wifimanager.getWifiState() == WifiManager.WIFI_STATE_DISABLING) {
            wifimanager.setWifiEnabled(true);
        }
    }

    private void connectToDevice() {
        try {

            //Create WiFi manager instance
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

            //Create new configuration for the device
            WifiConfiguration wifiConfig = new WifiConfiguration();

            //Set SSID and password for the network
            wifiConfig.SSID = String.format("\"%s\"", "SKY_Zapper_8031006C");
            wifiConfig.preSharedKey = String.format("\"%s\"", getString(R.string.zapper_wifi_pass));

            //Add network to stack if the manager is reached
            assert wifiManager != null;
            int netId = wifiManager.addNetwork(wifiConfig);

            //Disconnect from current network
            wifiManager.disconnect();

            //Enable new network
            wifiManager.enableNetwork(netId, true);

            //Connect to new network
            wifiManager.reconnect();

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    public class Request extends AsyncTask<URL, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(URL... strings) {
            String response = "";
            JSONObject object = null;
            try {
                BufferedReader reader;
                InputStream is;
                StringBuilder responseBuilder = new StringBuilder();
                HttpURLConnection conn = (HttpURLConnection) strings[0].openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                is = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));
                for (String line; (line = reader.readLine()) != null; ) {
                    responseBuilder.append(line).append("\n");
                }
                object = new JSONObject(responseBuilder.toString());
                is.close();
                reader.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayList<String> keys = new ArrayList<>();
            try {
                assert object != null;
                keys.add(object.getString("NetworksCount"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return keys;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            Log.e("Response", strings.get(0));
            //TODO
            super.onPostExecute(strings);
        }
    }

    public class UDP extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String text = "koko";
            int server_port = 7432;
            try {
                DatagramSocket clientSocket = new DatagramSocket();
                InetAddress IPAddress = InetAddress.getByName("255.255.255.255");
                byte[] sendData = ("02904").getBytes();
                byte[] receiveData = new byte[1024];
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, server_port);
                clientSocket.send(sendPacket);
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                Log.e("Receiving response", "Response");
                text = new String(receivePacket.getData());
                clientSocket.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return text;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("Socket response", s);
        }
    }
}
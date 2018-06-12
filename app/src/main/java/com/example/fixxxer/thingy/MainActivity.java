package com.example.fixxxer.thingy;

import android.annotation.SuppressLint;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Disconnect from current network and connect to Sky Zapper hotspot
        connectToDevice();

        //Initialize device with pin and obtain response
        new Request().execute("http://google.bg");
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
    public class Request extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            //Send a request to the given URL and get the JSON response
            //TODO

            //Get the response and parse it
            //TODO

            //Return the needed information back to UI thread
            //TODO
            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            //Update UI
            //TODO
            super.onPostExecute(strings);
        }
    }
}

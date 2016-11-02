package findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc.Model.WifiInfo;
import findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc.util.AppUtil;


public class MyFirebaseMessagingService extends FirebaseMessagingService{

    @Override
    public void onMessageSent(String s) {
        Log.d("onMessageSent","onMessageSent");
        super.onMessageSent(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("onMessageReceived","Inicio");

        WifiManager mainWifi;
        ArrayList<WifiInfo> wifis = new ArrayList<WifiInfo>();

        final JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if(mainWifi.isWifiEnabled()==false)
        {
            mainWifi.setWifiEnabled(true);
        }
        mainWifi.startScan();
        mainWifi.getScanResults();

        wifis.clear();

        List<ScanResult> wifiList;
        wifiList = mainWifi.getScanResults();
        for(int i = 0; i < wifiList.size(); i++)
        {
            ScanResult wifi =  wifiList.get(i);
            WifiInfo info = new WifiInfo();
            info.setBSSID(wifi.BSSID);
            info.setRSSI(wifi.level);
            info.setSSID(wifi.SSID);
            wifis.add(info);
        }

        Log.d("onMessageReceived","Pegou APS");

        try{
            //for (int i = 0; i < wifis.size(); i++){
            for(WifiInfo info : wifis){
                jsonObject.put("BSSID",info.getBSSID());
                jsonObject.put("RSSI",info.getRSSI());
                jsonObject.put("SSID",info.getSSID());

                jsonArray.put(jsonObject);

                jsonObject = new JSONObject();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        String url = AppUtil.getServer() + "usuario/myAccessPoints?";
        Log.d("APS",jsonArray.toString());
        try{
            SharedPreferences preferences = getSharedPreferences("LoginActivityPreferences", MODE_PRIVATE);
            String matricula = preferences.getString("matricula","");
            url += "apList=" + URLEncoder.encode(jsonArray.toString(),"UTF-8");
            url += "&matricula=" + URLEncoder.encode(matricula, "UTF-8");

            URL urlObj = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) urlObj.openConnection();
            InputStream is = urlConnection.getInputStream();
            /*
            RequestQueue mRequestQueue;

            // Set up the network to use HttpURLConnection as the HTTP client.
            Network network = new BasicNetwork(new HurlStack());

            // Instantiate the cache
            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

            // Instantiate the RequestQueue with the cache and network.
            mRequestQueue = new RequestQueue(cache, network);

            // Start the queue
            mRequestQueue.start();
            JsonObjectRequest request = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            error.printStackTrace();
                        }
                    });

            mRequestQueue.add(request);*/
        }catch(Exception e){
            e.printStackTrace();
        }


        super.onMessageReceived(remoteMessage);

    }

    public MyFirebaseMessagingService() {
    }
}

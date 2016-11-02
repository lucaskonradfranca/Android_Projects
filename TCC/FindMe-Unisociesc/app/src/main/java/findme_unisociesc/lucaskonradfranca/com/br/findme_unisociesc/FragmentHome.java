package findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc.Model.WifiInfo;
import findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc.util.AppUtil;

public class FragmentHome extends Fragment {

    private TextView textoLocalizacao;
    private ImageView imagemLocalizacao;
    private Button botaoLocalizar;
    private Button botaoAtualizar;

    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    private final Handler handler = new Handler();
    ArrayList<WifiInfo> wifis = new ArrayList<WifiInfo>();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textoLocalizacao = (TextView) view.findViewById(R.id.idTxtLocalizacao);
        imagemLocalizacao = (ImageView) view.findViewById(R.id.idImagemLocalizacao);
        botaoLocalizar = (Button) view.findViewById(R.id.idBotaoLocalizar);
        botaoAtualizar = (Button) view.findViewById(R.id.idBotaoAtualizar);

        botaoLocalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"Localizar um amigo",Toast.LENGTH_SHORT).show();
            }
        });

        botaoAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMyLocation(view);
            }
        });

        getMyLocation(view);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainWifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);

        //receiverWifi = new WifiReceiver();
        //getContext().registerReceiver(receiverWifi, new IntentFilter(
                //WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        if(mainWifi.isWifiEnabled()==false)
        {
            mainWifi.setWifiEnabled(true);
        }

        //doInback();
    }

    @Override
    public void onPause()
    {
        //getContext().unregisterReceiver(receiverWifi);
        super.onPause();
    }

    @Override
    public void onResume()
    {
        //getContext().registerReceiver(receiverWifi, new IntentFilter(
                //WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_layout, container, false);
        return v;
    }
/*
    public void doInback()
    {
        handler.postDelayed(new Runnable() {

            @Override
            public void run()
            {
                mainWifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);

                receiverWifi = new WifiReceiver();
                getContext().registerReceiver(receiverWifi, new IntentFilter(
                        WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                mainWifi.startScan();
                doInback();
            }
        }, 1000);

    }
*/
    public void getMyLocation(View v){
        final View myView = v;
        final JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        mainWifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);

        //receiverWifi = new WifiReceiver();
        //getContext().registerReceiver(receiverWifi, new IntentFilter(
//                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
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

        //AppUtil.exibeMensagem((Activity) v.getContext(),"teste",jsonArray.toString(),R.drawable.ic_alert);

        final ProgressDialog progress = AppUtil.getProgress((Activity) v.getContext(),getString(R.string.carregando),"Buscando localização, aguarde...");
        progress.show();

        RequestQueue mRequestQueue;

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the cache
        Cache cache = new DiskBasedCache(v.getContext().getCacheDir(), 1024 * 1024); // 1MB cap

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        String url = AppUtil.getServer() + "usuario/getMyLocation?apList=";

        try{
            url += URLEncoder.encode(jsonArray.toString(),"UTF-8");
        }catch(Exception e){
            e.printStackTrace();
        }

        // Start the queue
        mRequestQueue.start();
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            boolean status = response.getBoolean("status");
                            String msg = response.getString("msg");

                            if (status){
                                textoLocalizacao.setText(getString(R.string.minha_localizacao)+ " " + msg);
                            }else{
                                textoLocalizacao.setText("Não foi possível identificar a sua localização.");
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }


                        progress.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        progress.dismiss();
                        error.printStackTrace();
                        AppUtil.exibeMensagem((Activity) myView.getContext(),getString(R.string.erro),getString(R.string.erro_conexao),R.drawable.ic_alert);
                    }
                });

        mRequestQueue.add(request);

        //getContext().unregisterReceiver(receiverWifi);
    }

    class WifiReceiver extends BroadcastReceiver
    {
        public void onReceive(Context c, Intent intent)
        {

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

        }
    }

}

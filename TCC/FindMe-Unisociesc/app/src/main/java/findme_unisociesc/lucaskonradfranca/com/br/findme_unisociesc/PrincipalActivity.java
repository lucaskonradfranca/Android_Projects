package findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabWidget;

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
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URLEncoder;

import findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc.Model.Usuario;
import findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc.util.AppUtil;

public class PrincipalActivity extends FragmentActivity {
    private FragmentTabHost mTabHost;

    private static Usuario usuario = null;

    public Usuario getUsuario() {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences("LoginActivityPreferences", MODE_PRIVATE);
        String json = sharedPreferences.getString("objUsuario","");
        usuario = gson.fromJson(json, Usuario.class);
        return usuario;
    }
    public void setUsuario(Usuario user) {
        Gson gson = new Gson();
        String json = gson.toJson(usuario);
        SharedPreferences sharedPreferences = getSharedPreferences("LoginActivityPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor     = sharedPreferences.edit();
        editor.putString("objUsuario",json);
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        usuario = (Usuario) intent.getSerializableExtra("usuario");

        setContentView(R.layout.activity_principal);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(
                mTabHost.newTabSpec("tabHome").setIndicator(null, getDrawable(R.drawable.ic_action_home)),
                FragmentHome.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("tabAmigos").setIndicator(null, getDrawable(R.drawable.ic_action_amigos)),
                FragmentAmigos.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("tabAddAmigos").setIndicator(null, getDrawable(R.drawable.ic_request_frients)),
                FragmentSolicitaAmigos.class, null);

        TabWidget tabWidget = mTabHost.getTabWidget();

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

    }

    @Override
    protected void onResume() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);

        menu.removeItem(R.id.action_menu_config_user);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId){
            case R.id.action_menu_sair:
                AlertDialog.Builder alert = new AlertDialog.Builder(PrincipalActivity.this);

                alert.setTitle(getString(R.string.sair));
                alert.setMessage(getString(R.string.confirma_sair));
                alert.setPositiveButton(getString(R.string.sim), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences preferences = getSharedPreferences("LoginActivityPreferences", MODE_PRIVATE);

                        SharedPreferences.Editor editor = preferences.edit();
                        String matricula = preferences.getString("matricula","");
                        editor.clear();
                        editor.commit();

                        //apaga o token do usuário
                        RequestQueue mRequestQueue;

                        // Set up the network to use HttpURLConnection as the HTTP client.
                        Network network = new BasicNetwork(new HurlStack());

                        // Instantiate the cache
                        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

                        // Instantiate the RequestQueue with the cache and network.
                        mRequestQueue = new RequestQueue(cache, network);

                        String url = null;

                        try{
                            url = AppUtil.getServer() + "usuario/updateToken?matricula="+ URLEncoder.encode(matricula,"UTF-8")+"&token=";
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        // Start the queue
                        mRequestQueue.start();
                        JsonObjectRequest request = new JsonObjectRequest
                                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("TOKEN","response - OK");
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // TODO Auto-generated method stub
                                        error.printStackTrace();
                                        AppUtil.geraLog(error);
                                    }
                                });

                        mRequestQueue.add(request);

                        finish();
                    }
                });
                alert.setNegativeButton(getString(R.string.nao),null);
                alert.setIcon(R.drawable.icon_exit);
                alert.show();
                break;
            case R.id.action_menu_sobre:
                startActivity(new Intent(PrincipalActivity.this,SobreActivity.class));
                break;
            case R.id.action_menu_configuracoes:
                startActivity(new Intent(PrincipalActivity.this,ConfiguracoesActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Para o correto funcionamento do aplicativo, é necessário ativar a Localização do dispositivo. Clique em OK para acessar as configurações e ativar a localização.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


}
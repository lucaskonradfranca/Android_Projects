package findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

import findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc.Model.Usuario;
import findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc.util.AppUtil;

public class BuscarAmigosActivity extends Activity {

    private EditText edtNome;
    private ImageView imageBuscar;
    private ListView listViewUsuarios;

    private ArrayList<String> listaNomes = new ArrayList<String>();
    private ArrayList<Usuario> listaUsuarios = new ArrayList<Usuario>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_amigos);

        edtNome = (EditText) findViewById(R.id.idEditTextNome);
        imageBuscar = (ImageView) findViewById(R.id.idImgViewBuscar);
        listViewUsuarios = (ListView) findViewById(R.id.idListViewUsuarios);

        buscar();

        imageBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscar();
            }
        });

        listViewUsuarios.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, final View viewItm, final int position, long id){
                final String amigo = listViewUsuarios.getItemAtPosition(position).toString();
                final CharSequence[] items = {
                        "Adicionar",
                        getString(R.string.evento_bloquear),
                        "Desbloquear",
                        getString(R.string.evento_localizar)
                };

                AlertDialog.Builder alert = new AlertDialog.Builder(viewItm.getContext());
                alert.setTitle(getString(R.string.opcoes_amigos));
                alert.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                adicionar(position);
                                break;
                            case 1:
                                blockUsuario(position, true, viewItm); //bloquear
                                break;
                            case 2:
                                blockUsuario(position, false, viewItm); //desbloquar
                                break;
                            case 3:
                                localizar(position, viewItm);
                                break;
                        }
                    }
                });
                alert.create();
                alert.show();

            }
        });

    }

    private void blockUsuario(int position, final boolean bloquear, final View view) {
        final ProgressDialog progress = AppUtil.getProgress(BuscarAmigosActivity.this,getString(R.string.carregando),"Processando");
        progress.show();

        RequestQueue mRequestQueue;

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        String url = "";

        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences("LoginActivityPreferences", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("objUsuario","");
        Usuario user = gson.fromJson(json, Usuario.class);
        String matriculaAmigo = listaUsuarios.get(position).getMatricula();
        try{
            url = AppUtil.getServer() + "usuario/bloquear?matricula="+ URLEncoder.encode(user.getMatricula(),"UTF-8");
            url += "&matriculaAmigo=" + URLEncoder.encode(matriculaAmigo,"UTF-8");
            if (bloquear){
                url+= "&block=S";
            }else{
                url+= "&block=N";
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        // Start the queue
        mRequestQueue.start();
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        boolean status = false;
                        String msg;
                        try{
                            status = response.getBoolean("status");
                            msg = response.getString("msg");

                            if (status){
                                if (bloquear){
                                    AppUtil.exibeMensagem(BuscarAmigosActivity.this,"Sucesso","Amigo bloqueado com sucesso.",R.drawable.ic_alert);
                                }else{
                                    AppUtil.exibeMensagem(BuscarAmigosActivity.this,"Sucesso","Amigo desbloqueado com sucesso.",R.drawable.ic_alert);
                                }
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                            status = false;
                            msg = "Erro no parse do retorno da requisição.";
                        }

                        progress.dismiss();

                        if (! status){
                            AppUtil.exibeMensagem(BuscarAmigosActivity.this,"Erro",msg,R.drawable.ic_alert);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        progress.dismiss();
                        AppUtil.geraLog(error);
                        AppUtil.exibeMensagem(BuscarAmigosActivity.this,getString(R.string.erro),getString(R.string.erro_conexao),R.drawable.ic_alert);
                    }
                });

        mRequestQueue.add(request);


        progress.dismiss();
    }

    private void localizar(final int position, View viewItm) {
        final ProgressDialog progress = AppUtil.getProgress(this,getString(R.string.carregando),"Buscando localização, aguarde...");
        progress.show();

        RequestQueue mRequestQueue;

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        String url = null;

        SharedPreferences preferences = getSharedPreferences("LoginActivityPreferences", MODE_PRIVATE);
        String usuarioOrigem = preferences.getString("matricula","");

        try{
            url = AppUtil.getServer() + "usuario/getLocation?matricula="+ URLEncoder.encode(listaUsuarios.get(position).getMatricula(),"UTF-8");
            url += "&usuarioOrigem=" + URLEncoder.encode(usuarioOrigem,"UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }

        // Start the queue
        mRequestQueue.start();
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progress.dismiss();
                        boolean status = false;
                        String msg;

                        try{
                            status = response.getBoolean("status");
                            msg = response.getString("msg");

                            if (status){
                                Intent intent = new Intent(BuscarAmigosActivity.this,LocationActivity.class);
                                intent.putExtra("localizacao",msg);
                                intent.putExtra("usuario",listaUsuarios.get(position).getNome());
                                startActivity(intent);
                            }else{
                                AppUtil.exibeMensagem(BuscarAmigosActivity.this,"Localização",msg,R.drawable.ic_alert);
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        error.printStackTrace();
                        progress.dismiss();
                        AppUtil.geraLog(error);
                        AppUtil.exibeMensagem(BuscarAmigosActivity.this,getString(R.string.erro),getString(R.string.erro_conexao),R.drawable.ic_alert);
                    }
                });

        mRequestQueue.add(request);
    }

    private void adicionar(int position) {
        final ProgressDialog progress = AppUtil.getProgress(this,getString(R.string.carregando),"Enviando solicitação, aguarde...");
        progress.show();

        RequestQueue mRequestQueue;

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        SharedPreferences preferences = getSharedPreferences("LoginActivityPreferences", MODE_PRIVATE);
        String json = preferences.getString("objUsuario","");
        Gson gson = new Gson();
        Usuario user = gson.fromJson(json, Usuario.class);

        String url = "";

        try{
            url = AppUtil.getServer() + "usuario/adicionar?matricula="+ URLEncoder.encode(user.getMatricula(),"UTF-8");
            url += "&matriculaAmigo="+URLEncoder.encode(listaUsuarios.get(position).getMatricula(),"UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }

        // Start the queue
        mRequestQueue.start();
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        boolean status = false;
                        String msg;
                        try{
                            status = response.getBoolean("status");
                            msg = response.getString("msg");

                            if (status){
                                AppUtil.exibeMensagem(BuscarAmigosActivity.this,"Sucesso","Solicitaçao enviada com sucesso.",R.drawable.ic_request_frients);
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                            status = false;
                            msg = "Erro no parse do retorno da requisição.";
                        }

                        progress.dismiss();

                        if (! status && ! msg.isEmpty()){
                            AppUtil.exibeMensagem(BuscarAmigosActivity.this,"Erro",msg,R.drawable.ic_alert);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        progress.dismiss();
                        AppUtil.geraLog(error);
                        AppUtil.exibeMensagem(BuscarAmigosActivity.this,getString(R.string.erro),getString(R.string.erro_conexao),R.drawable.ic_alert);
                    }
                });

        mRequestQueue.add(request);


        progress.dismiss();
    }

    private void buscar() {
        String filtro = edtNome.getText().toString();

        final ProgressDialog progress = AppUtil.getProgress(this,getString(R.string.carregando),"Buscando amigos, aguarde...");
        progress.show();

        RequestQueue mRequestQueue;

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        SharedPreferences preferences = getSharedPreferences("LoginActivityPreferences", MODE_PRIVATE);
        String json = preferences.getString("objUsuario","");
        Gson gson = new Gson();
        Usuario user = gson.fromJson(json, Usuario.class);

        String url = "";

        try{
            url = AppUtil.getServer() + "usuario/getUsuarios?matricula="+ URLEncoder.encode(user.getMatricula(),"UTF-8");
            url += "&filtro="+URLEncoder.encode(filtro,"UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }

        listaUsuarios.clear();
        listaNomes.clear();
        // Start the queue
        mRequestQueue.start();
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        boolean status = false;
                        String msg;
                        try{
                            status = response.getBoolean("status");
                            msg = response.getString("msg");

                            if (status){

                                String matricula = "";
                                String nome = "";
                                String email = "";
                                String data_nascimento = "";
                                String first_login = "";
                                int nivel_privacidade;

                                JSONObject objResponse = response.getJSONObject("objeto");
                                JSONArray arrayResponse = objResponse.getJSONArray("entity");

                                int i;

                                for(i = 0; i < arrayResponse.length(); i++){
                                    JSONObject o = (JSONObject) arrayResponse.get(i);
                                    matricula       = o.getString("matricula").trim();
                                    nome            = o.getString("nome").trim();
                                    email           = o.getString("email").trim();
                                    data_nascimento = o.getString("data_nascimento").trim();
                                    first_login     = o.getString("first_login").trim();
                                    nivel_privacidade = o.getInt("nivel_privacidade");

                                    listaNomes.add(nome.trim());
                                    listaUsuarios.add(new Usuario(matricula, nome, email, "", data_nascimento, first_login,nivel_privacidade));
                                }

                                ArrayAdapter<String> amigosAdapter = new ArrayAdapter<String>(BuscarAmigosActivity.this,
                                        android.R.layout.simple_list_item_1,
                                        android.R.id.text1,
                                        listaNomes);

                                listViewUsuarios.setAdapter(amigosAdapter);
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                            status = false;
                            msg = "Erro no parse do retorno da requisição.";
                        }

                        progress.dismiss();

                        if (! status){
                            AppUtil.exibeMensagem(BuscarAmigosActivity.this,"Erro",msg,R.drawable.ic_alert);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        progress.dismiss();
                        AppUtil.geraLog(error);
                        AppUtil.exibeMensagem(BuscarAmigosActivity.this,getString(R.string.erro),getString(R.string.erro_conexao),R.drawable.ic_alert);
                    }
                });

        mRequestQueue.add(request);


        progress.dismiss();

    }
}

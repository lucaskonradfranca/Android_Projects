package findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

import findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc.Model.Usuario;
import findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc.util.AppUtil;

public class FragmentAmigos extends Fragment{

    private ListView listaAmigos;
    private Button botaoBuscarAmigos;

    private TextView txtResult;
    private RelativeLayout layoutTexto;

    private ArrayList<String> amigos = new ArrayList<String>();
    private ArrayList<Usuario> listaUsuarios = new ArrayList<Usuario>();
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listaAmigos = (ListView) view.findViewById(R.id.idListaAmigos);
        botaoBuscarAmigos = (Button) view.findViewById(R.id.idBotaoAtualizar);

        txtResult = (TextView) view.findViewById(R.id.idTxtSolicita1);
        layoutTexto = (RelativeLayout) view.findViewById(R.id.idLayoutTexto1);

        carregarAmigos(view);

        /*if (amigos.size() < 1){
            carregarAmigos(view);
        }else{
            ArrayAdapter<String> amigosAdapter = new ArrayAdapter<String>(view.getContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    amigos);

            listaAmigos.setAdapter(amigosAdapter);
        }*/

        listaAmigos.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, final View viewItm, final int position, long id){
                final String amigo = listaAmigos.getItemAtPosition(position).toString();
                final CharSequence[] items = {
                        getString(R.string.evento_localizar),
                        getString(R.string.evento_bloquear),
                        "Desbloquar",
                        getString(R.string.evento_excluir)
                };

                AlertDialog.Builder alert = new AlertDialog.Builder(viewItm.getContext());
                alert.setTitle(getString(R.string.opcoes_amigos));
                alert.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                localizar(position, viewItm);
                                break;
                            case 1:
                                blockUsuario(position, true, viewItm); //bloquear
                                break;
                            case 2:
                                blockUsuario(position, false, viewItm); //desbloquar
                                break;
                            case 3:
                                excluir(position, viewItm);
                                break;
                        }
                    }
                });
                alert.create();
                alert.show();

            }
        });

        botaoBuscarAmigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procurarAmigos(view);
            }
        });

    }

    private void excluir(int position, final View view) {
        final ProgressDialog progress = AppUtil.getProgress(view.getContext(),getString(R.string.carregando),"Processando");
        progress.show();

        RequestQueue mRequestQueue;

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the cache
        Cache cache = new DiskBasedCache(view.getContext().getCacheDir(), 1024 * 1024); // 1MB cap

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        String url = "";

        Gson gson = new Gson();
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("LoginActivityPreferences", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("objUsuario","");
        Usuario user = gson.fromJson(json, Usuario.class);
        String matriculaAmigo = listaUsuarios.get(position).getMatricula();
        try{
            url = AppUtil.getServer() + "usuario/excluir?matricula="+ URLEncoder.encode(user.getMatricula(),"UTF-8");
            url += "&matriculaAmigo=" + URLEncoder.encode(matriculaAmigo,"UTF-8");
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
                                AppUtil.exibeMensagem((Activity) view.getContext(),"Sucesso","Amigo excluído com sucesso.",R.drawable.ic_alert);
                            }else{
                                AppUtil.exibeMensagem((Activity) view.getContext(),"Erro","Ocorreram erros ao excluir o amigo. " + msg ,R.drawable.ic_alert);
                            }

                            carregarAmigos(view);

                        }catch (Exception e){
                            e.printStackTrace();
                            status = false;
                            msg = "Erro no parse do retorno da requisição.";
                        }

                        progress.dismiss();

                        if (! status){
                            AppUtil.exibeMensagem((Activity) view.getContext(),"Erro",msg,R.drawable.ic_alert);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        progress.dismiss();
                        AppUtil.exibeMensagem((Activity) view.getContext(),getString(R.string.erro),getString(R.string.erro_conexao),R.drawable.ic_alert);
                    }
                });

        mRequestQueue.add(request);


        progress.dismiss();
    }

    private void blockUsuario(int position, final boolean bloquear, final View view) {
        final ProgressDialog progress = AppUtil.getProgress(view.getContext(),getString(R.string.carregando),"Processando");
        progress.show();

        RequestQueue mRequestQueue;

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the cache
        Cache cache = new DiskBasedCache(view.getContext().getCacheDir(), 1024 * 1024); // 1MB cap

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        String url = "";

        Gson gson = new Gson();
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("LoginActivityPreferences", Context.MODE_PRIVATE);
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
                                    AppUtil.exibeMensagem((Activity) view.getContext(),"Sucesso","Amigo bloqueado com sucesso.",R.drawable.ic_alert);
                                }else{
                                    AppUtil.exibeMensagem((Activity) view.getContext(),"Sucesso","Amigo desbloqueado com sucesso.",R.drawable.ic_alert);
                                }
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                            status = false;
                            msg = "Erro no parse do retorno da requisição.";
                        }

                        progress.dismiss();

                        if (! status){
                            AppUtil.exibeMensagem((Activity) view.getContext(),"Erro",msg,R.drawable.ic_alert);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        progress.dismiss();
                        AppUtil.exibeMensagem((Activity) view.getContext(),getString(R.string.erro),getString(R.string.erro_conexao),R.drawable.ic_alert);
                    }
                });

        mRequestQueue.add(request);


        progress.dismiss();
    }

    private void procurarAmigos(View view) {

        startActivity(new Intent(getActivity(),BuscarAmigosActivity.class));
    }

    private void localizar(final int position, final View v){
        final ProgressDialog progress = AppUtil.getProgress((Activity) v.getContext(),getString(R.string.carregando),"Buscando localização, aguarde...");
        progress.show();

        RequestQueue mRequestQueue;

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the cache
        Cache cache = new DiskBasedCache(v.getContext().getCacheDir(), 1024 * 1024); // 1MB cap

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        String url = null;

        SharedPreferences preferences = getContext().getSharedPreferences("LoginActivityPreferences", getContext().MODE_PRIVATE);
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
                                Intent intent = new Intent(getActivity(),LocationActivity.class);
                                intent.putExtra("localizacao",msg);
                                intent.putExtra("usuario",listaUsuarios.get(position).getNome());
                                startActivity(intent);
                            }else{
                                AppUtil.exibeMensagem((Activity) v.getContext(),"Localização",msg,R.drawable.ic_alert);
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
                        AppUtil.exibeMensagem((Activity) v.getContext(),getString(R.string.erro),getString(R.string.erro_conexao),R.drawable.ic_alert);
                    }
                });

        mRequestQueue.add(request);
    }

    private void carregarAmigos(final View view){
        final ProgressDialog progress = AppUtil.getProgress(view.getContext(),getString(R.string.carregando),getString(R.string.buscando_amigos));
        progress.show();

        amigos.clear();
        listaUsuarios.clear();

        RequestQueue mRequestQueue;

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the cache
        Cache cache = new DiskBasedCache(view.getContext().getCacheDir(), 1024 * 1024); // 1MB cap

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        String url = "";

        Gson gson = new Gson();
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("LoginActivityPreferences", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("objUsuario","");
        Usuario user = gson.fromJson(json, Usuario.class);
        try{
            url = AppUtil.getServer() + "usuario/getAmigos?matricula="+ URLEncoder.encode(user.getMatricula(),"UTF-8");
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

                                    amigos.add(nome.trim());
                                    listaUsuarios.add(new Usuario(matricula, nome, email, "", data_nascimento, first_login,nivel_privacidade));
                                }

                                if (amigos.size() > 0){
                                    txtResult.setText("");
                                    layoutTexto.setVisibility(View.GONE);
                                }else{
                                    txtResult.setText("Você não possui nenhum amigo.");
                                    layoutTexto.setVisibility(View.VISIBLE);
                                }

                                ArrayAdapter<String> amigosAdapter = new ArrayAdapter<String>(view.getContext(),
                                        android.R.layout.simple_list_item_1,
                                        android.R.id.text1,
                                        amigos);

                                listaAmigos.setAdapter(amigosAdapter);
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                            status = false;
                            msg = "Erro no parse do retorno da requisição.";
                        }

                        progress.dismiss();

                        if (! status){
                            AppUtil.exibeMensagem((Activity) view.getContext(),"Erro",msg,R.drawable.ic_alert);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        progress.dismiss();
                        AppUtil.exibeMensagem((Activity) view.getContext(),getString(R.string.erro),getString(R.string.erro_conexao),R.drawable.ic_alert);
                    }
                });

        mRequestQueue.add(request);


        progress.dismiss();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_amigos_layout, container, false);

        return v;
    }
}

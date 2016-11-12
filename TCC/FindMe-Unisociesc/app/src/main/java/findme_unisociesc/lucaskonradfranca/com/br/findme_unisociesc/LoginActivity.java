package findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URLEncoder;

import findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc.Model.Usuario;
import findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc.util.AppUtil;

public class LoginActivity extends Activity {

    private final int INTERNET=1;
    private final int ACCESS_WIFI_STATE=2;
    private final int CHANGE_WIFI_STATE=3;
    private final int CHANGE_WIFI_MULTICAST_STATE=4;
    private final int WRITE_EXTERNAL_STORAGE=5;
    private final int ACCESS_FINE_LOCATION=6;
    private final int ACCESS_COARSE_LOCATION=7;

    private EditText txtLogin;
    private EditText txtSenha;
    private Button botaoEntrar;
    private Button botaoCadastrar;
    private CheckBox checkManterLogado;
    private ImageView imgHelp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String matricula = getIntent().getStringExtra("matricula");
        String senha     = getIntent().getStringExtra("senha");

        verificaPermissoes();

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(getBaseContext());

        if (status != ConnectionResult.SUCCESS){
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(LoginActivity.this, status, 2404).show();
            }
        }

        //Carrega os componentes nas variáveis.
        carregarComponentesPrivate();

        txtLogin.setText(matricula);
        txtSenha.setText(senha);

        //Adiciona os eventos dos botões.
        carregarEventosButtons();

        //Botão manter conectado.
        SharedPreferences preferences = getSharedPreferences("LoginActivityPreferences", MODE_PRIVATE);
        boolean conectado             = preferences.getBoolean("manter_conectado",false);

        if(conectado){
            String data_nascimento = preferences.getString("data_nascimento","");
            String email = preferences.getString("email","");
            String nome = preferences.getString("nome","");
            senha = preferences.getString("senha","");
            matricula = preferences.getString("matricula","");

            Usuario usuario = new Usuario();

            usuario.setMatricula(matricula);
            usuario.setData_nascimento(data_nascimento);
            usuario.setEmail(email);
            usuario.setFirst_login("N");
            usuario.setNome(nome);
            usuario.setSenha(senha);

            atualizarToken();

            ChamarActivityPrincipal(usuario);
        }

    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

    private void verificaPermissoes() {

        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.INTERNET);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.INTERNET, INTERNET);
        }

        permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_WIFI_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.ACCESS_WIFI_STATE, ACCESS_WIFI_STATE);
        }

        permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.CHANGE_WIFI_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.CHANGE_WIFI_STATE, CHANGE_WIFI_STATE);
        }

        permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.CHANGE_WIFI_MULTICAST_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.CHANGE_WIFI_MULTICAST_STATE, CHANGE_WIFI_MULTICAST_STATE);
        }

        permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);
        }

        permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION);
        }

        permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, ACCESS_COARSE_LOCATION);
        }

    }

    private void carregarComponentesPrivate(){
        txtLogin          = (EditText) findViewById(R.id.idEdtLogin);
        txtSenha          = (EditText) findViewById(R.id.idEdtSenha);
        botaoEntrar       = (Button) findViewById(R.id.idBtnEntrar);
        //botaoCadastrar    = (Button) findViewById(R.id.idBtnCadastrar);
        //checkManterLogado = (CheckBox) findViewById(R.id.idCheckManterLogado);
        imgHelp           = (ImageView) findViewById(R.id.idHelpLogin);
    }

    private void carregarEventosButtons(){
        botaoEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickBtnLogar();
            }
        });

        imgHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = "Para realizar o login, é necessário utilizar a sua matrícula da Unisociesc. \n"+
                        "Caso seja o seu primeiro acesso, a sua senha será a sua data de nascimento, no formato DDMMAAAA. \n"+
                        "  Exemplo: Primeiro login. Matrícula: 111111111. Senha: 25121997 \n"+
                        "Ao realizar o seu primeiro acesso, será solicitado a alteração de sua senha, e atualização de seus dados. \n"+
                        "Caso não seja o seu primeiro acesso, deverá ser utilizada a senha cadastrada no primeiro login.";
                AppUtil.exibeMensagem(LoginActivity.this,"Ajuda",msg,R.drawable.ic_help);
            }
        });

        /*botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickBtnCadastrar();
            }
        });*/
    }

    private void clickBtnLogar(){
        //realizar o login
        String login = txtLogin.getText().toString();
        final String senha = txtSenha.getText().toString();
        final boolean manterLogado = true; //checkManterLogado.isChecked();

        if(login == null || login.isEmpty()){
            txtLogin.setError(getString(R.string.erro_login_vazio));
            txtLogin.requestFocus();
        }else if(senha == null || senha.isEmpty()){
            txtSenha.setError(getString(R.string.erro_senha_vazio));
            txtSenha.requestFocus();
        } else {
            final ProgressDialog progress = AppUtil.getProgress(this,getString(R.string.carregando),getString(R.string.carregando_login));
            progress.show();

            RequestQueue mRequestQueue;

            // Set up the network to use HttpURLConnection as the HTTP client.
            Network network = new BasicNetwork(new HurlStack());

            // Instantiate the cache
            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

            // Instantiate the RequestQueue with the cache and network.
            mRequestQueue = new RequestQueue(cache, network);

            String url = null;

            try{
                url = AppUtil.getServer() + "login?user="+URLEncoder.encode(login,"UTF-8")+"&pass="+URLEncoder.encode(senha,"UTF-8");
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
                        String msg = "";
                        String matricula = "";
                        String nome = "";
                        String email = "";
                        String data_nascimento = "";
                        String first_login = "";
                        int nivel_privacidade = 0;
                        try{
                            status = response.getBoolean("status");
                            msg = response.getString("msg");

                            if (status){
                                JSONObject a = response.getJSONObject("objeto");
                                matricula       = a.getString("matricula").trim();
                                nome            = a.getString("nome").trim();
                                email           = a.getString("email").trim();
                                data_nascimento = a.getString("data_nascimento").trim();
                                first_login     = a.getString("first_login").trim();
                                nivel_privacidade = a.getInt("nivel_privacidade");
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                            status = false;
                            msg = "Erro no parse do retorno da requisição.";
                        }

                        progress.dismiss();

                        if (status){
                            //Salva preferência de manter logado
                            if(manterLogado){
                                SharedPreferences sharedPreferences = getSharedPreferences("LoginActivityPreferences", MODE_PRIVATE);
                                SharedPreferences.Editor editor     = sharedPreferences.edit();

                                editor.putBoolean("manter_conectado",true);
                                editor.putString("matricula",matricula);
                                editor.putString("nome",nome);
                                editor.putString("email",email);
                                editor.putString("data_nascimento",data_nascimento);
                                editor.commit();

                                atualizarToken();
                            }

                            if(first_login.equals("S")){
                                //Abre tela para atualizar o cadastro do usuário.
                                chamarActivityAtualizaCadastro(matricula, nome, email, data_nascimento);
                            }else{
                                Usuario usuario = new Usuario();

                                usuario.setMatricula(matricula);
                                usuario.setData_nascimento(data_nascimento);
                                usuario.setEmail(email);
                                usuario.setFirst_login("N");
                                usuario.setNome(nome);
                                usuario.setSenha(senha);
                                usuario.setNivel_privacidade(nivel_privacidade);
                                Gson gson = new Gson();
                                String json = gson.toJson(usuario);
                                SharedPreferences sharedPreferences = getSharedPreferences("LoginActivityPreferences", MODE_PRIVATE);
                                SharedPreferences.Editor editor     = sharedPreferences.edit();
                                editor.putString("objUsuario",json);
                                editor.commit();

                                //realiza o login.
                                ChamarActivityPrincipal(usuario);
                            }


                        }else{
                            AppUtil.exibeMensagem(LoginActivity.this,"Erro",msg,R.drawable.ic_alert);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        progress.dismiss();
                        error.printStackTrace();

                        AppUtil.geraLog(error);

                        /*String matricula       = "113001625";
                        String nome            = "Lucas";
                        String email           = "lucas@email.com";
                        String data_nascimento = "29091992";
                        String first_login     = "N";
                        int nivel_privacidade = 0;

                        Usuario usuario = new Usuario();

                        usuario.setMatricula(matricula);
                        usuario.setData_nascimento(data_nascimento);
                        usuario.setEmail(email);
                        usuario.setFirst_login("N");
                        usuario.setNome(nome);
                        usuario.setSenha(senha);
                        usuario.setNivel_privacidade(nivel_privacidade);
                        Gson gson = new Gson();
                        String json = gson.toJson(usuario);
                        SharedPreferences sharedPreferences = getSharedPreferences("LoginActivityPreferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor     = sharedPreferences.edit();
                        editor.putString("objUsuario",json);
                        editor.commit();

                        //realiza o login.
                        ChamarActivityPrincipal(usuario);*/

                        AppUtil.exibeMensagem(LoginActivity.this,getString(R.string.erro),getString(R.string.erro_conexao),R.drawable.ic_alert);
                    }
                });

            mRequestQueue.add(request);

        }

    }

    private void clickBtnCadastrar(){
        startActivity(new Intent(LoginActivity.this,CadastroActivity.class));
    }

    private void ChamarActivityPrincipal(Usuario usuario){
        Intent intent = new Intent(LoginActivity.this, PrincipalActivity.class);
        intent.putExtra("usuario",usuario);
        startActivity(intent);
        finish();
    }

    private void chamarActivityAtualizaCadastro(String matricula, String nome, String email, String data_nascimento){
        Intent intent = new Intent(this, ConfigUserActivity.class);
        intent.putExtra("MATRICULA", matricula);
        intent.putExtra("NOME", nome);
        intent.putExtra("EMAIL", email);
        intent.putExtra("DATA_NASCIMENTO", data_nascimento);
        intent.putExtra("SENHA",data_nascimento);
        intent.putExtra("LOGIN","S");
        intent.putExtra("NIVEL_PRIVACIDADE",0);
        startActivity(intent);
        finish();
    }

    private void atualizarToken()
    {
        SharedPreferences preferences = getSharedPreferences("LoginActivityPreferences", MODE_PRIVATE);
        String matricula = preferences.getString("matricula","");
        preferences = getSharedPreferences("TokenPreferences", MODE_PRIVATE);
        String token = preferences.getString("token","");

        RequestQueue mRequestQueue;

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        String url = null;

        try{
            url = AppUtil.getServer() + "usuario/updateToken?matricula="+ URLEncoder.encode(matricula,"UTF-8")+"&token="+URLEncoder.encode(token,"UTF-8");
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
    }
}

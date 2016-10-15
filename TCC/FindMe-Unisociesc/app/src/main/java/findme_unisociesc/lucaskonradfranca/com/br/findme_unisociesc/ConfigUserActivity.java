package findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc.Model.Usuario;
import findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc.util.AppUtil;

public class ConfigUserActivity extends Activity {

    private Button btnConfirmar;
    private Button btnCancelar;
    private EditText edtNome;
    private EditText edtMatricula;
    private EditText edtSenha;
    private EditText edtDataNascimento;
    private EditText edtEmail;

    private String senha;
    private String matricula;
    private String data_nascimento;
    private String login;
    private String nome;
    private String email;
    private int nivel_privacidade;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //String matricula = "";
        //String nome = "";
        //String email = "";
        //final String data_nascimento = "";
        //String login = "";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_user);

        btnCancelar  = (Button) findViewById(R.id.idBtnCancelar);
        btnConfirmar = (Button) findViewById(R.id.idBtnConfirmar);
        edtNome      = (EditText) findViewById(R.id.idEditNome);
        edtMatricula = (EditText) findViewById(R.id.idEditLogin);
        edtSenha     = (EditText) findViewById(R.id.idEditSenha);
        edtEmail     = (EditText) findViewById(R.id.idEditEmail);
        edtDataNascimento = (EditText) findViewById(R.id.idEditDataNascimento);

        matricula       = getIntent().getStringExtra("MATRICULA");
        nome            = getIntent().getStringExtra("NOME");
        email           = getIntent().getStringExtra("EMAIL");
        data_nascimento = getIntent().getStringExtra("DATA_NASCIMENTO");
        senha           = getIntent().getStringExtra("SENHA");
        login           = getIntent().getStringExtra("LOGIN");
        nivel_privacidade = getIntent().getIntExtra("NIVEL_PRIVACIDADE",0);
        if ( login.equals("S")){
            try{
                getActionBar().setDisplayHomeAsUpEnabled(false);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        edtMatricula.setText(matricula);
        edtNome.setText(nome);
        edtEmail.setText(email);
        edtDataNascimento.setText(data_nascimento);
        edtSenha.setText(senha);


        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        if(mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon-1);
                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    edtDataNascimento.setText(current);
                    edtDataNascimento.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();
        };

        edtDataNascimento.addTextChangedListener(tw);
        edtDataNascimento.setText(edtDataNascimento.getText()); // faz isso para aparecer o valor formatado no campo.

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( login.equals("S")) {
                    Intent intent = new Intent(ConfigUserActivity.this, LoginActivity.class);

                    intent.putExtra("matricula", matricula);
                    intent.putExtra("senha", senha);
                    startActivity(intent);
                }
                finish();
            }
        });

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valido = true;
                if ( login.equals("S")){
                    if (senha.equals(edtSenha.getText().toString())){
                        edtSenha.setError(getString(R.string.valida_senha));
                        valido = false;
                    }
                }

                if (edtNome.getText().equals("")){
                    edtNome.setError(getString(R.string.erro_nome_vazio));
                    valido = false;
                }

                if (edtEmail.getText().equals("")){
                    edtEmail.setError(getString(R.string.erro_email_vazio));
                    valido = false;
                }else{
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText()).matches()){
                        valido = false;
                        edtEmail.setError(getString(R.string.erro_email_invalido));
                    }
                }

                if (edtDataNascimento.getText().equals("")){
                    edtDataNascimento.setError(getString(R.string.erro_nascimento_vazio));
                    valido = false;
                }

                if (valido) {
                    final ProgressDialog progress = AppUtil.getProgress(ConfigUserActivity.this,getString(R.string.carregando),getString(R.string.carregando_update));
                    progress.show();

                    RequestQueue mRequestQueue;

                    // Set up the network to use HttpURLConnection as the HTTP client.
                    Network network = new BasicNetwork(new HurlStack());

                    // Instantiate the cache
                    Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

                    // Instantiate the RequestQueue with the cache and network.
                    mRequestQueue = new RequestQueue(cache, network);

                    String url = null;
                    try {
                        url = AppUtil.getServer() + "usuario/update?matricula="+URLEncoder.encode(edtMatricula.getText().toString(),"UTF-8")+
                                                                  "&senha="+URLEncoder.encode(edtSenha.getText().toString(),"UTF-8")+
                                                                  "&nome="+URLEncoder.encode(edtNome.getText().toString(),"UTF-8")+
                                                                  "&email="+URLEncoder.encode(edtEmail.getText().toString(),"UTF-8")+
                                                                  "&data_nascimento="+URLEncoder.encode(edtDataNascimento.getText().toString(),"UTF-8")+
                                                                  "&first_login=N"+
                                                                  "&nivel_privacidade="+nivel_privacidade;
                    } catch (UnsupportedEncodingException e) {
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
                                    try{
                                        status = response.getBoolean("status");
                                        msg = response.getString("msg");

                                    }catch (Exception e){
                                        e.printStackTrace();
                                        status = false;
                                        msg = "Erro no parse do retorno da requisição.";
                                    }

                                    progress.dismiss();

                                    if (status){
                                        if (login.equals("S")){
                                            Usuario usuario = new Usuario();

                                            usuario.setMatricula(edtMatricula.getText().toString());
                                            usuario.setData_nascimento(edtDataNascimento.getText().toString());
                                            usuario.setEmail(edtEmail.getText().toString());
                                            usuario.setFirst_login("N");
                                            usuario.setNome(edtNome.getText().toString());
                                            usuario.setSenha(edtSenha.getText().toString());
                                            usuario.setNivel_privacidade(nivel_privacidade);

                                            Intent intent = new Intent(ConfigUserActivity.this, PrincipalActivity.class);
                                            intent.putExtra("usuario",usuario);

                                            Gson gson = new Gson();
                                            String json = gson.toJson(usuario);
                                            SharedPreferences sharedPreferences = getSharedPreferences("LoginActivityPreferences", MODE_PRIVATE);
                                            SharedPreferences.Editor editor     = sharedPreferences.edit();
                                            editor.putString("objUsuario",json);
                                            editor.commit();

                                            startActivity(intent);
                                        }
                                        finish();
                                    }else{
                                        AppUtil.exibeMensagem(ConfigUserActivity.this,"Erro",msg,R.drawable.ic_alert);
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO Auto-generated method stub
                                    progress.dismiss();
                                    AppUtil.exibeMensagem(ConfigUserActivity.this,"Erro","volleyerror"+error.getStackTrace(),R.drawable.ic_alert);
                                    error.printStackTrace();
                                }
                            });

                    mRequestQueue.add(request);
                }
            }
        });

    }
}

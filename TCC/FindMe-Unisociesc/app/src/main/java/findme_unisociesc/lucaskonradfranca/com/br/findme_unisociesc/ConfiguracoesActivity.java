package findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc.Model.Usuario;
import findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc.util.AppUtil;

public class ConfiguracoesActivity extends Activity {

    private RadioGroup groupPrivacidade;
    private RadioButton radioPrivacidade;
    private Button btnConfirmar;
    private Button btnCancelar;
    private Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        groupPrivacidade = (RadioGroup) findViewById(R.id.idRadioGroupPrivacidade);
        btnCancelar = (Button) findViewById(R.id.idBtnCancelarCfg);
        btnConfirmar = (Button) findViewById(R.id.idBtnConfirmarCfg);


        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences("LoginActivityPreferences", MODE_PRIVATE);
        String json = sharedPreferences.getString("objUsuario","");

        user = gson.fromJson(json, Usuario.class);

        RadioButton rbTodos     = (RadioButton) findViewById(R.id.idRBTodos);
        RadioButton rbAmigos    = (RadioButton) findViewById(R.id.idRBAmigos);
        RadioButton rbNinguem   = (RadioButton) findViewById(R.id.idRBNinguem);
        switch (user.getNivel_privacidade()){
            case 0:
                rbTodos.setChecked(true);
                rbAmigos.setChecked(false);
                rbNinguem.setChecked(false);
                break;
            case 1:
                rbTodos.setChecked(false);
                rbAmigos.setChecked(true);
                rbNinguem.setChecked(false);
                break;
            case 2:
                rbTodos.setChecked(false);
                rbAmigos.setChecked(false);
                rbNinguem.setChecked(false);
                break;
            case 3:
                rbTodos.setChecked(false);
                rbAmigos.setChecked(false);
                rbNinguem.setChecked(true);
                break;
        }

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progress = AppUtil.getProgress(ConfiguracoesActivity.this,getString(R.string.carregando),getString(R.string.carregando_update));
                progress.show();

                RequestQueue mRequestQueue;

                // Set up the network to use HttpURLConnection as the HTTP client.
                Network network = new BasicNetwork(new HurlStack());

                // Instantiate the cache
                Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

                // Instantiate the RequestQueue with the cache and network.
                mRequestQueue = new RequestQueue(cache, network);

                int nivel_privacidade = 0;
                if (groupPrivacidade.getCheckedRadioButtonId()>0){
                    radioPrivacidade = (RadioButton) findViewById(groupPrivacidade.getCheckedRadioButtonId());
                    switch (radioPrivacidade.getId()){
                        case R.id.idRBTodos:
                            nivel_privacidade = 0;
                            break;
                        case R.id.idRBAmigos:
                            nivel_privacidade = 1;
                            break;
                        case R.id.idRBNinguem:
                            nivel_privacidade = 3;
                            break;
                    }
                }

                String url = null;
                try {
                    url = AppUtil.getServer() + "usuario/update?matricula="+ URLEncoder.encode(user.getMatricula(),"UTF-8")+
                            "&nivel_privacidade="+nivel_privacidade;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                // Start the queue
                mRequestQueue.start();
                final int finalNivel_privacidade = nivel_privacidade;
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
                                    user.setNivel_privacidade(finalNivel_privacidade);

                                    Gson gson = new Gson();
                                    String json = gson.toJson(user);
                                    SharedPreferences sharedPreferences = getSharedPreferences("LoginActivityPreferences", MODE_PRIVATE);
                                    SharedPreferences.Editor editor     = sharedPreferences.edit();
                                    editor.putString("objUsuario",json);
                                    editor.commit();

                                    finish();
                                }else{
                                    AppUtil.exibeMensagem(ConfiguracoesActivity.this,"Erro",msg,R.drawable.ic_alert);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO Auto-generated method stub
                                progress.dismiss();
                                AppUtil.exibeMensagem(ConfiguracoesActivity.this,"Erro","volleyerror"+error.getStackTrace(),R.drawable.ic_alert);
                                error.printStackTrace();
                            }
                        });

                mRequestQueue.add(request);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        
        menu.removeItem(R.id.action_menu_configuracoes);
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId){
            case R.id.action_menu_sair:
                AlertDialog.Builder alert = new AlertDialog.Builder(ConfiguracoesActivity.this);

                alert.setTitle(getString(R.string.sair));
                alert.setMessage(getString(R.string.confirma_sair));
                alert.setPositiveButton(getString(R.string.sim), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences preferences = getSharedPreferences("LoginActivityPreferences", MODE_PRIVATE);

                        SharedPreferences.Editor editor = preferences.edit();

                        editor.clear();
                        editor.commit();
                        finish();
                    }
                });
                alert.setNegativeButton(getString(R.string.nao),null);
                alert.setIcon(R.drawable.icon_exit);
                alert.show();
                break;
            case R.id.action_menu_sobre:
                startActivity(new Intent(ConfiguracoesActivity.this,SobreActivity.class));
                break;
            case R.id.action_menu_config_user:
                Intent intent = new Intent(ConfiguracoesActivity.this, ConfigUserActivity.class);
                intent.putExtra("MATRICULA", user.getMatricula());
                intent.putExtra("NOME", user.getNome());
                intent.putExtra("EMAIL", user.getEmail());
                intent.putExtra("DATA_NASCIMENTO", user.getData_nascimento());
                intent.putExtra("SENHA",user.getSenha());
                intent.putExtra("NIVEL_PRIVACIDADE",user.getNivel_privacidade());
                intent.putExtra("LOGIN","N");
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}

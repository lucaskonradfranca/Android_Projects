package findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc.Model.Usuario;

public class ConfiguracoesActivity extends Activity {

    private EditText editNome;
    private EditText editEmail;
    private EditText editLogin;
    private EditText editSenha;
    private RadioGroup groupPrivacidade;
    private RadioButton radioPrivacidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        groupPrivacidade = (RadioGroup) findViewById(R.id.idRadioGroupPrivacidade);

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
                Gson gson = new Gson();
                SharedPreferences sharedPreferences = getSharedPreferences("LoginActivityPreferences", MODE_PRIVATE);
                String json = sharedPreferences.getString("objUsuario","");
                Usuario user = gson.fromJson(json, Usuario.class);

                Intent intent = new Intent(ConfiguracoesActivity.this, ConfigUserActivity.class);
                intent.putExtra("MATRICULA", user.getMatricula());
                intent.putExtra("NOME", user.getNome());
                intent.putExtra("EMAIL", user.getEmail());
                intent.putExtra("DATA_NASCIMENTO", user.getData_nascimento());
                intent.putExtra("SENHA",user.getSenha());
                intent.putExtra("LOGIN","N");
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}

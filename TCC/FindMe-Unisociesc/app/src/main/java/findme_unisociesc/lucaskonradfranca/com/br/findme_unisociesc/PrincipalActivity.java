package findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabWidget;

import com.google.gson.Gson;

import findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc.Model.Usuario;

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

        TabWidget tabWidget = mTabHost.getTabWidget();
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
                startActivity(new Intent(PrincipalActivity.this,SobreActivity.class));
                break;
            case R.id.action_menu_configuracoes:
                startActivity(new Intent(PrincipalActivity.this,ConfiguracoesActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
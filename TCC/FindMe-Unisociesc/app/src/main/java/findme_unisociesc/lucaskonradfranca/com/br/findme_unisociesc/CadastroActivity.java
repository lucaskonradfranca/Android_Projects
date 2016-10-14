package findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CadastroActivity extends Activity {

    private TextView txtNome;
    private TextView txtEmail;
    private TextView txtUsuario;
    private TextView txtSenha;
    private Button btnCadastra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        txtNome     = (TextView) findViewById(R.id.idEdtNome);
        txtEmail    = (TextView) findViewById(R.id.idEdtEmail);
        txtUsuario  = (TextView) findViewById(R.id.idEdtLogin);
        txtSenha    = (TextView) findViewById(R.id.idEdtSenha);
        btnCadastra = (Button) findViewById(R.id.idBtnCadastrar);

        btnCadastra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validaCadastro()){
                    finish();
                }
            }
        });

    }

    private boolean validaCadastro(){
        boolean valido = true;

        String nome    = txtNome.getText().toString();
        String email   = txtEmail.getText().toString();
        String usuario = txtUsuario.getText().toString();
        String senha   = txtSenha.getText().toString();

        if(nome == null || nome.isEmpty()){
            valido = false;
            txtNome.setError(getString(R.string.erro_nome_vazio));
            txtNome.requestFocus();
        }else if(email == null || email.isEmpty()){
            valido = false;
            txtEmail.setError(getString(R.string.erro_email_vazio));
            txtEmail.requestFocus();
        }else if(usuario == null || usuario.isEmpty()){
            valido = false;
            txtUsuario.setError(getString(R.string.erro_login_vazio));
            txtUsuario.requestFocus();
        }else if(senha == null || senha.isEmpty()){
            valido = false;
            txtSenha.setError(getString(R.string.erro_senha_vazio));
            txtSenha.requestFocus();
        }

        if (valido){
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                valido = false;
                txtEmail.setError(getString(R.string.erro_email_invalido));
                txtEmail.requestFocus();
            }
        }

        return valido;
    }

}

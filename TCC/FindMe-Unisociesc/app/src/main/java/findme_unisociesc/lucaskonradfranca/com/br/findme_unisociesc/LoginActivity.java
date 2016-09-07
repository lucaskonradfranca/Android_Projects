package findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class LoginActivity extends Activity {

    private EditText txtLogin;
    private EditText txtSenha;
    private Button botaoEntrar;
    private Button botaoCadastrar;
    private CheckBox checkManterLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Carrega os componentes nas variáveis.
        carregarComponentesPrivate();

        //Adiciona os eventos dos botões.
        carregarEventosButtons();

    }

    private void carregarComponentesPrivate(){
        txtLogin          = (EditText) findViewById(R.id.idEdtLogin);
        txtSenha          = (EditText) findViewById(R.id.idEdtSenha);
        botaoEntrar       = (Button) findViewById(R.id.idBtnEntrar);
        botaoCadastrar    = (Button) findViewById(R.id.idBtnCadastrar);
        checkManterLogado = (CheckBox) findViewById(R.id.idCheckManterLogado);
    }

    private void carregarEventosButtons(){
        botaoEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickBtnLogar();
            }
        });

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickBtnCadastrar();
            }
        });
    }

    private void clickBtnLogar(){
        //realizar o login
        String login = txtLogin.getText().toString();
        String senha = txtSenha.getText().toString();
        boolean manterLogado = checkManterLogado.isChecked();

        if(login == null || login.isEmpty()){
            txtLogin.setError(getString(R.string.erro_login_vazio));
            txtLogin.requestFocus();
        }else if(senha == null || senha.isEmpty()){
            txtSenha.setError(getString(R.string.erro_senha_vazio));
            txtSenha.requestFocus();
        } else {
            //realiza o login.
            startActivity(new Intent(LoginActivity.this, PrincipalActivity.class));
            finish();
        }

    }

    private void clickBtnCadastrar(){
        //abrir tela de cadastro
    }

}

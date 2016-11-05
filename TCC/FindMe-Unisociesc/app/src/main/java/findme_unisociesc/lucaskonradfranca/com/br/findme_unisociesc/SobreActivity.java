package findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SobreActivity extends Activity {

    private TextView txtSobre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        txtSobre = (TextView) findViewById(R.id.idTxtSobre);

        txtSobre.setText("O aplicativo FindMe foi construído para facilitar a localização das pessoas. " +
                "Com esse aplicativo, é possível identificar a localização das pessoas dentro do centro universitário Unisociesc. " +
                "O FindMe preza pela privacidade dos seus usuários, fornecendo apenas as informações que os usuários decidirem compartilhar." +
                " \nEste aplicativo foi concebido como trabalho de conclusão de curso, e sua utilização é restrita apenas aos alunos e professores" +
                " do centro universitário Unisociesc, no campus Marquês de Olinda. As informações obtidas nesse aplicativo não podem " +
                "ser utilizadas para fins que não sejam benéficos para os seus usuários.");
    }
}

package findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class LocationActivity extends Activity {
    private TextView textoLocal;
    private ImageView imagemLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        textoLocal = (TextView) findViewById(R.id.idTxtLocal);
        imagemLocal = (ImageView) findViewById(R.id.idImagemLocal);

        Intent intent = getIntent();
        String local = intent.getStringExtra("localizacao");
        String usuario = intent.getStringExtra("usuario");

        textoLocal.setText("Localizaçao de " + usuario.trim() + ": " + local);

    }
}

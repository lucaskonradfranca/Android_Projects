package findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class LocationActivity extends Activity {
    private TextView textoLocal;
    //private ImageView imagemLocal;
    private WebView webView;
    private TextView textoNome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        textoLocal = (TextView) findViewById(R.id.idTxtLocal);
        textoNome = (TextView) findViewById(R.id.idTxtNomeUsuario);
        //imagemLocal = (ImageView) findViewById(R.id.idImagemLocal);
        webView = (WebView) findViewById(R.id.idWebViewLocation);

        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        Intent intent = getIntent();
        String local = intent.getStringExtra("localizacao");
        String usuario = intent.getStringExtra("usuario");

        textoNome.setText(usuario);

        textoLocal.setText(local);

        webView.loadUrl("file:///android_asset/image.html?sala="+local);

    }
}

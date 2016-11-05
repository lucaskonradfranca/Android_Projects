package findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.ProgressBar;


/**
 * Created by Lucas on 08/10/2016.
 */
public class AppUtil {

    //private static String SERVER = "http://192.168.137.1:8080/FindMe-Server/";
    private static String SERVER = "http://192.168.0.11:8080/FindMe-Server/";
    public static String getServer() {
        return SERVER;
    }

    public static void exibeMensagem(Activity activity, String titulo, String msg, int icone){
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);

        alert.setTitle(titulo);
        alert.setMessage(msg);
        alert.setNeutralButton("Ok",null);
        alert.setIcon(icone);
        alert.show();
    }

    public static ProgressDialog getProgress(Context context, String title, String message){
        ProgressDialog progress = new ProgressDialog(context);
        progress.setTitle(title);
        progress.setMessage(message);
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        return progress;
    }

}

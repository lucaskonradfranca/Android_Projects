package findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.webkit.WebView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;


/**
 * Created by Lucas on 08/10/2016.
 */
public class
AppUtil {

    //private static String SERVER = "http://192.168.137.1:8080/FindMe-Server/";
    //private static String SERVER = "http://192.168.0.11:8080/FindMe-Server/";
    private static String SERVER = "http://192.168.1.3:8080/FindMe-Server/";
    //private static String SERVER = "http://10.8.143.217:8080/FindMe-Server/";
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

    public static void geraLog(Exception exception){
        String filename = "findme_log.txt";
        String root = Environment.getExternalStorageDirectory().toString();
        String filePath = root + "/";

        File file = new File(filePath, filename);

        FileOutputStream stream;
        PrintWriter writer;
        try {
            stream = new FileOutputStream(file, true);
            writer = new PrintWriter(stream);
            //writer.println(mensagem);
            exception.printStackTrace(writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void posicionaWebView(final WebView view, final String url) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int x = getPosX(url);
                view.scrollTo(x,0);
            }
        }, 300);
    }

    private static int getPosX(String url) {
        String sala = url.substring(url.length()-4);
        int x = 0;
        if (sala.equals("B001")){
            x = 1600;
        }else if (sala.equals("B002")){
            x = 1600;
        }else if (sala.equals("B003")){
            x = 1300;
        }else if (sala.equals("B004")){
            x = 1100;
        }else if (sala.equals("B005")){
            x = 1100;
        }else if (sala.equals("B006")){
            x = 850;
        }else if (sala.equals("B007")){
            x = 850;
        }else if (sala.equals("B008")){
            x = 600;
        }else if (sala.equals("B009")){
            x = 600;
        }else if (sala.equals("B010")){
            x = 350;
        }else if (sala.equals("B011")){
            x = 350;
        }else if (sala.equals("B012")){
            x = 0;
        }else if (sala.equals("B013")){
            x = 0;
        }
        return x;
    }
}

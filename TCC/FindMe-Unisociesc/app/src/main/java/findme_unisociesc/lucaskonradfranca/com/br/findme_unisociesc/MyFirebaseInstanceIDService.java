package findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URLEncoder;

import findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc.util.AppUtil;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    public MyFirebaseInstanceIDService() {
    }

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        sendRegistrationToServer(refreshedToken);
        super.onTokenRefresh();
    }

    private void sendRegistrationToServer(String refreshedToken) {
        SharedPreferences preferences = getSharedPreferences("TokenPreferences", MODE_PRIVATE);

        SharedPreferences.Editor editor     = preferences.edit();
        editor.putString("token",refreshedToken);
        editor.commit();
    }
}

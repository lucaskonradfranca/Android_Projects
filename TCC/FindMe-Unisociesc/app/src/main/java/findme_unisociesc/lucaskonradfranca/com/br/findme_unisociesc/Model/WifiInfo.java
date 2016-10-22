package findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc.Model;

/**
 * Created by Lucas on 21/10/2016.
 */
public class WifiInfo {

    private String BSSID; // basic service set identifier - nome da conexão
    private String SSID;  // service set identifier - identificador único da conexão
    private float RSSI;  // received signal strength indicator - potencia do sinal (-87 a -32)

    public WifiInfo(String bssid, String ssid, int signal){
        this.BSSID = bssid;
        this.SSID  = ssid;
        this.RSSI = signal;
    }

    public WifiInfo(){

    }

    public String getBSSID() {
        return BSSID;
    }

    public String getSSID() {
        return SSID;
    }

    public float getRSSI() {
        return RSSI;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public void setRSSI(float RSSI) {
        this.RSSI = RSSI;
    }
}


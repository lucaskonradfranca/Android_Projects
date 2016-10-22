package models;

public class APResource {

	String BSSID; // basic service set identifier - nome da conexão
    String SSID;  // service set identifier - identificador único da conexão
    float RSSI;  // received signal strength indicator - potencia do sinal (-87 a -32)

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

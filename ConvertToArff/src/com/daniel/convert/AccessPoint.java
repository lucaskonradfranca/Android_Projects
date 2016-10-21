package com.daniel.convert;

public class AccessPoint {

	String bssid;
	// service set identifier - mac do AP
	String ssid;
	// received signal strength indicator - potencia do sinal (-87 a -32)
	String rssi;
	// nome da sala
	String sala;
	// numero da contagem, identificador único por contagem (UUID)
	String contagem;
	
	int sequencia;
	
	public int getSequencia() {
		return sequencia;
	}

	public void setSequencia(int sequencia) {
		this.sequencia = sequencia;
	}

	boolean gerou;
	
	public boolean isGerou() {
		return gerou;
	}

	public void setGerou(boolean gerou) {
		this.gerou = gerou;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getRssi() {
		return rssi;
	}

	public void setRssi(String rssi) {
		this.rssi = rssi;
	}

	public String getSala() {
		return sala;
	}

	public void setSala(String sala) {
		this.sala = sala;
	}

	public String getContagem() {
		return contagem;
	}

	public void setContagem(String contagem) {
		this.contagem = contagem;
	}

}

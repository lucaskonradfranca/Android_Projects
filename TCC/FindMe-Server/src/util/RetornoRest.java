package util;

public class RetornoRest {
	private boolean status;
	private String msg;
	private Object objeto;
	
	public Object getObjeto() {
		return objeto;
	}
	public void setObjeto(Object objeto) {
		this.objeto = objeto;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	@Override
	public String toString() {
		if (objeto == null){
			return "RetornoRest [status=" + status + ", msg=" + msg + "]";
		}else{
			return "RetornoRest [status=" + status + ", msg=" + msg + ", objeto=["+objeto.toString()+"]]";
		}
	}
}

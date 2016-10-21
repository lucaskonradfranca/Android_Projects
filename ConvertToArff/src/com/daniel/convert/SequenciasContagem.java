package com.daniel.convert;

import java.util.ArrayList;
import java.util.List;

public class SequenciasContagem {

	private List<Integer> sequencias = new ArrayList<Integer>();
	private String contagem;
	public List<Integer> getSequencias() {
		return sequencias;
	}
	public void setSequencias(List<Integer> sequencias) {
		this.sequencias = sequencias;
	}
	public void addSequencia(Integer sequencia) {
		this.sequencias.add(sequencia);
	}
	public String getContagem() {
		return contagem;
	}
	public void setContagem(String contagem) {
		this.contagem = contagem;
	}
	
		
}

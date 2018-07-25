package mase.entities;

import java.awt.Point;

public class Celula {
	private Point posicao;
	private Point posicaoAnterior;
	private int peso = 0;
	private long soma = Long.MAX_VALUE;
	private boolean visitado = false;
	private String owner = null;

	public Celula(Point posicao, int peso) {
		this.posicao = posicao;
		this.peso = peso;
	}
	
	public Celula(Celula copy){
		this.posicao = copy.getPosicao();
		this.posicaoAnterior = copy.getPosicaoAnterior();
		this.peso = copy.getPeso();
		this.soma = copy.getSoma();
		this.visitado = copy.getVisitado();
		this.owner = copy.getOwner();
	}

	public Point getPosicao() {
		return posicao;
	}

	public Point getPosicaoAnterior() {
		return posicaoAnterior;
	}

	public int getPeso() {
		return peso;
	}

	public long getSoma() {
		return soma;
	}

	public void setSoma(Point posicaoAnterior, long soma) {
		this.posicaoAnterior = posicaoAnterior;
		this.soma = soma;
	}

	public void setVisitado(boolean visitado) {
		this.visitado = visitado;
	}

	public boolean getVisitado() {
		return visitado;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		if (owner == null) {
			this.owner = owner;
		}
	}
}
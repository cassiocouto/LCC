package mase.behaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.awt.Point;
import java.io.IOException;

import mase.entities.Celula;
import mase.entities.agents.GRIDManager;
import mase.entities.agents.TrackerAgent;
import mase.main.Main;
import java.util.ArrayList;

public class DijkstraPathFind extends CyclicBehaviour {

	private static final long serialVersionUID = 1L;
	private ArrayList<Point> espacosIniciais;
	private Point pontoInicialAtual;
	private Celula[][] meuEspacoOriginal;
	private Celula[][] melhorEspaco;
	private Point pontoInicialEscolhido;
	private Point pontoFinalEscolhido;
	private Celula[][] meuEspaco;
	private ArrayList<Point> pontosAtuais;
	private ArrayList<Point> pontosVizinhos;
	private ArrayList<Point> pontosFinais;
	private long menorSomaEncontrada = Long.MAX_VALUE;

	public DijkstraPathFind(ArrayList<Point> espacosIniciais, Celula[][] meuEspaco) {
		this.espacosIniciais = espacosIniciais;
		meuEspacoOriginal = meuEspaco;
		pontosFinais = Main.getEspacosFinais();
	}

	public void action() {
		if (espacosIniciais.size() > 0) {
			pontoInicialAtual = espacosIniciais.remove(0);
		} else {
			ACLMessage m = new ACLMessage(ACLMessage.PROPOSE);
			System.out.println(myAgent.getName() + " - proponho:" + menorSomaEncontrada + "!");
			m.setContent("" + menorSomaEncontrada);
			m.addReceiver(GRIDManager.enderecoManager);
			myAgent.send(m);
			myAgent.doWait();
			m = myAgent.receive();
			if (m.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
				System.out.println(myAgent.getName() + " - ganhei a proposta!");
				try {
					m = new ACLMessage(ACLMessage.INFORM);
					m.setContentObject(encontrarMenorCaminho());
					m.addReceiver(GRIDManager.enderecoManager);
					myAgent.send(m);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {

				System.out.println(myAgent.getName() + " - perdi a proposta...");
			}
			myAgent.doDelete();
		}
		meuEspaco = TrackerAgent.cloneEspaco(meuEspacoOriginal);
		
		pontosAtuais = null;
		pontosVizinhos = null;
		do {
			encontrarPontosAtuais();
			evaluarPontosVizinhosLocais();
		} while (!pontosVizinhos.isEmpty());
		encontrarMenorSoma();

	}

	

	public void encontrarPontosAtuais() {
		if (pontosAtuais == null) {

			meuEspaco[pontoInicialAtual.x][pontoInicialAtual.y].setSoma(pontoInicialAtual, 0);

			pontosAtuais = new ArrayList<Point>();
			pontosVizinhos = new ArrayList<Point>();

			pontosAtuais.add(pontoInicialAtual);

		} else {

			pontosAtuais = pontosVizinhos;
			pontosVizinhos = new ArrayList<Point>();

		}

	}

	public void evaluarPontosVizinhosLocais() {
		for (Point referencia : pontosAtuais) {
			Celula atual = meuEspaco[referencia.x][referencia.y];

			if (atual.getVisitado()) {
				continue;
			}
			for (int i = referencia.x - 1; i <= referencia.x + 1; i++) {
				for (int j = referencia.y - 1; j <= referencia.y + 1; j++) {
					if (i < 0 || i >= meuEspaco.length || j < 0 || j >= meuEspaco[0].length
							|| (i == referencia.x && j == referencia.y) || meuEspaco[i][j].getVisitado()) {
						continue;
					} else {
						long tentativa = atual.getSoma() + meuEspaco[i][j].getPeso();
						if (tentativa < meuEspaco[i][j].getSoma()) {
							meuEspaco[i][j].setSoma(referencia, tentativa);
						}
						pontosVizinhos.add(meuEspaco[i][j].getPosicao());

					}
				}
			}
			meuEspaco[referencia.x][referencia.y].setVisitado(true);
		}
	}

	public void encontrarMenorSoma() {
		long menorSoma = Long.MAX_VALUE;
		pontoFinalEscolhido = pontosFinais.get(0);
		for (int i = 0; i < pontosFinais.size(); i++) {
			Point referencia = pontosFinais.get(i);
			if (menorSoma > meuEspaco[referencia.x][referencia.y].getSoma()) {
				menorSoma = meuEspaco[referencia.x][referencia.y].getSoma();
				pontoFinalEscolhido = referencia;
			}
		}

		if (menorSoma < menorSomaEncontrada) {
			menorSomaEncontrada = menorSoma;
			// System.out.println(myAgent.getName() + " - Encontrei a soma:" +
			// menorSoma);
			melhorEspaco = TrackerAgent.cloneEspaco(meuEspaco);
			pontoInicialEscolhido = pontoInicialAtual;

		}

	}

	public ArrayList<Point> encontrarMenorCaminho() {
		ArrayList<Point> menorCaminhoEncontrado = new ArrayList<Point>();
		do {
			menorCaminhoEncontrado.add(pontoFinalEscolhido);
			pontoFinalEscolhido = melhorEspaco[pontoFinalEscolhido.x][pontoFinalEscolhido.y].getPosicaoAnterior();
		} while (!pontoFinalEscolhido.equals(pontoInicialEscolhido));
		menorCaminhoEncontrado.add(pontoInicialEscolhido);
		return menorCaminhoEncontrado;
	}
}
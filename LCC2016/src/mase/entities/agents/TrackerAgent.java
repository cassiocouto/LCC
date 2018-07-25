package mase.entities.agents;

import jade.core.AID;
import jade.core.Agent;
import java.awt.Point;
import java.util.ArrayList;

import mase.behaviours.AStarPathFind;
import mase.behaviours.DijkstraPathFind;
import mase.entities.Celula;

public class TrackerAgent extends Agent {

	public static AID[] enderecos;
	
	private static final long serialVersionUID = 1L;
	private int estrategia;
	ArrayList<Point> espacosIniciais;
	Celula[][] espaco;

	public TrackerAgent(ArrayList<Point> espacosIniciais, int estrategia, Celula[][] espaco) {
		
		this.espacosIniciais = espacosIniciais;
		this.estrategia = estrategia;
		this.espaco = espaco;
	}

	public void setup() {
		System.out.println(getName()+" - entrou");
		int meuId = Integer.parseInt(getLocalName().replace("tracker", ""));
		enderecos[meuId] = getAID();
		if (estrategia == GRIDManager.ESTRATEGIA_DIJKSTRA) {
			this.addBehaviour(new DijkstraPathFind(espacosIniciais, espaco));
		}else{
			this.addBehaviour(new AStarPathFind(espacosIniciais));
		}
	}
	
	public static Celula[][] cloneEspaco(Celula[][] ref) {
		Celula[][] aux = new Celula[ref.length][ref[0].length];
		for (int i = 0; i < ref.length; i++) {
			for(int j = 0; j < ref.length; j++){
				aux[i][j] = new Celula(ref[i][j]);
			}
		}
		return aux;
	}

}

package mase.entities.agents;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import mase.behaviours.AceitarPropostas;
import mase.behaviours.AdicionarAgentes;
import mase.behaviours.EvaluarResultado;
import mase.entities.Celula;
import mase.main.Main;

public class GRIDManager extends Agent {

	public static AID GRIDManagerAddress = null;
	public static final int ESTRATEGIA_DIJKSTRA = 0;
	public static final int ESTRATEGIA_ASTAR = 1;

	private static final long serialVersionUID = 1L;
	private int qtdAgentes = 1;
	private ArrayList<Point> espacosIniciais;
	private int estrategiaEscolhida = ESTRATEGIA_ASTAR;
	private Celula[][] espaco;

	public GRIDManager(Celula[][] espaco) {

		this.espacosIniciais = new ArrayList<Point>();
		this.espacosIniciais.addAll(0, Main.getEspacosIniciais());
		TrackerAgent.enderecos = new AID[qtdAgentes];
		this.espaco = espaco;
	}

	public void setup() {
		GRIDManagerAddress = getAID();
		int indice = espacosIniciais.size() / qtdAgentes;
		HashMap<String, Agent> agentes = new HashMap<String, Agent>();
		for (int i = 0; i < qtdAgentes; i++) {
			ArrayList<Point> subconjunto = new ArrayList<Point>();
			if (i != qtdAgentes - 1) {
				subconjunto.addAll(0, espacosIniciais.subList(i * indice, (i + 1) * indice));
			} else {
				subconjunto.addAll(0, espacosIniciais.subList(i * indice, espacosIniciais.size()));
			}
			TrackerAgent a = new TrackerAgent(subconjunto, estrategiaEscolhida, espaco);
			agentes.put("tracker"+i, a);
		}
		SequentialBehaviour lista = new SequentialBehaviour();
		lista.addSubBehaviour(new AdicionarAgentes(agentes));
		if(estrategiaEscolhida == ESTRATEGIA_DIJKSTRA){
			lista.addSubBehaviour(new AceitarPropostas(qtdAgentes));
			lista.addSubBehaviour(new EvaluarResultado(qtdAgentes));
		}else{
			
		}
		
		addBehaviour(lista);
		

	}

}

package mase.behaviours;

import jade.core.behaviours.OneShotBehaviour;
import mase.main.Main;

public class EvaluarResultado extends OneShotBehaviour {

	private static final long serialVersionUID = 1L;
	private int qtdAgentes;
	
	public EvaluarResultado(int qtdAgentes) {
		this.qtdAgentes = qtdAgentes;
	}
	
	
	@Override
	public void action() {
		long tempo = System.currentTimeMillis() - Main.tempoInicio;
		System.out.println("Tempo decorrido (ms): " + (tempo));
		Main.salvarImagem(Main.getCaminhoEncontrado(0), Main.getCusto(), tempo, qtdAgentes);
		myAgent.doDelete();
	}

}

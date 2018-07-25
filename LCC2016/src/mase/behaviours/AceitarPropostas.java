package mase.behaviours;

import java.awt.Point;
import java.util.ArrayList;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import mase.entities.agents.TrackerAgent;
import mase.main.Main;

public class AceitarPropostas extends OneShotBehaviour {

	private static final long serialVersionUID = 1L;

	private long menorSomaPossivel = Long.MAX_VALUE;
	private ArrayList<Point> menorCaminhoPossivel = null;
	private int countPropostas = 0;
	private long[] propostas;
	private int qtdAgentes;

	public AceitarPropostas(int qtdAgentes) {
		this.qtdAgentes = qtdAgentes;
		propostas = new long[qtdAgentes];
	}

	public void action() {
		do {

			ACLMessage m = null;
			while (m == null) {
				myAgent.doWait(1000);
				m = myAgent.receive();
			}
			int sender = Integer.parseInt(m.getSender().getLocalName().replace("tracker", ""));
			long proposta = Long.parseLong(m.getContent());
			propostas[sender] = proposta;
			countPropostas++;
		} while (countPropostas < qtdAgentes);

		long menorProp = Long.MAX_VALUE;
		int escolhido = -1;
		for (int i = 0; i < propostas.length; i++) {
			if (menorProp > propostas[i]) {
				menorProp = propostas[i];
				escolhido = i;
			}
		}
		menorSomaPossivel = menorProp;
		for (int i = 0; i < qtdAgentes; i++) {
			if (i == escolhido) {
				ACLMessage m = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				m.addReceiver(TrackerAgent.enderecos[i]);
				myAgent.send(m);
			} else {
				ACLMessage m = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
				m.addReceiver(TrackerAgent.enderecos[i]);
				myAgent.send(m);
			}
		}

		myAgent.doWait();
		ACLMessage resp = myAgent.receive();
		try {
			menorCaminhoPossivel = (ArrayList<Point>) resp.getContentObject();
			Main.addCaminhoEncontrado(menorCaminhoPossivel, "tracker" + escolhido);
			Main.setCusto(menorSomaPossivel);
			System.out.println(myAgent.getName() + " - a proposta escolhida foi: tracker" + escolhido + ": "
					+ menorSomaPossivel + "!");
		} catch (UnreadableException e) {
			e.printStackTrace();
		}

	}

}

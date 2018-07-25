package mase.behaviours;

import java.util.HashMap;
import java.util.Set;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

public class AdicionarAgentes extends OneShotBehaviour {

	private static final long serialVersionUID = 1L;
	
	
	private HashMap<String, Agent> agentes;

	
	public AdicionarAgentes(HashMap<String, Agent> agentes){
		this.agentes = agentes;
	}
	
	

	public void action() {
		
		Set<String> chaves = agentes.keySet();
		for(String apelido:chaves){
			Agent a = agentes.get(apelido);
			try {
				this.myAgent.getContainerController().acceptNewAgent(apelido, a);
				this.myAgent.getContainerController().getAgent(apelido).start();
			} catch (StaleProxyException e) {
				e.printStackTrace();
			} catch (ControllerException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	

}

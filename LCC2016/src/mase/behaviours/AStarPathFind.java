package mase.behaviours;

import java.awt.Point;
import java.util.*;

import jade.core.behaviours.CyclicBehaviour;
import mase.entities.Celula;
import mase.entities.agents.TrackerAgent;
import mase.main.Main;

public class AStarPathFind extends CyclicBehaviour {

	private static final long serialVersionUID = 1L;
	private ArrayList<Point> espacosIniciais;
	private Point pontoInicial;
	private Point pontoAtual;
	private Point pontoFinalEscolhido;
	private Celula[][] meuEspaco;
	private ArrayList<Point> pontosFinais;
	boolean encontrado = false;
	boolean debug = true;
	int count = 1;// debug

	ArrayList<Point> grupoAberto;
	ArrayList<Point> grupoFechado;
	ArrayList<Point> grupoExcluido;

	private long menorSomaEncontrada = Long.MAX_VALUE;
	long max = 1000;
	boolean evitarMax = true;

	public AStarPathFind(ArrayList<Point> espacosIniciais) {
		this.espacosIniciais = espacosIniciais;
		meuEspaco = TrackerAgent.cloneEspaco(Main.getEspaco());
		pontosFinais = Main.getEspacosFinais();
		grupoAberto = new ArrayList<Point>();
		grupoFechado = new ArrayList<Point>();
		grupoExcluido = new ArrayList<Point>();

	}

	public void action() {
		if (espacosIniciais.size() > 0) {
			pontoInicial = escolherInicio(pontoInicial);
			meuEspaco[pontoInicial.x][pontoInicial.y].setSoma(pontoInicial, 0);
			grupoAberto.add(pontoInicial);
			encontrado = false;
			do {
				evaluarVizinhosLocais();
			} while (!encontrado);
			System.out.println(myAgent.getName() + " - com o ponto inicial " + pontoInicial + " achei a soma: "
					+ meuEspaco[pontoFinalEscolhido.x][pontoFinalEscolhido.y].getSoma());
			ArrayList<Point> menorCaminhoEncontrado = recuperarCaminho();
		} else {

		}
	}

	public Point escolherInicio(Point ultimoPontoEscolhido) {
		Point escolhido = new Point();
		int indice = -1;
		if (ultimoPontoEscolhido == null) {
			double dist = Double.MAX_VALUE;
			for (int i = 0; i < pontosFinais.size(); i++) {
				for (int j = 0; j < espacosIniciais.size(); j++) {
					double tentativa = distanciaEuclidiana(pontosFinais.get(i), espacosIniciais.get(j));
					if (dist > tentativa) {
						dist = tentativa;
						pontoFinalEscolhido = pontosFinais.get(i);
						indice = j;
					}
				}
			}

		} else {
			double dist = Double.MAX_VALUE;
			for (int i = 0; i < espacosIniciais.size(); i++) {
				double tentativa = distanciaEuclidiana(espacosIniciais.get(i), ultimoPontoEscolhido);
				if (dist > tentativa) {
					dist = tentativa;
					indice = i;
				}
			}

		}
		escolhido = espacosIniciais.remove(indice);
		return escolhido;
	}

	public double distanciaEuclidiana(Point a, Point b) {
		double soma1 = a.x - b.x;
		double soma2 = a.y - b.y;
		double soma3 = (soma1 * soma1) + (soma2 * soma2);
		return Math.sqrt(soma3);
	}

	public double distanciaManhattan(Point a, Point b) {
		double soma1 = a.x - b.x;
		if (soma1 < 0) {
			soma1 = soma1 * (1);
		}
		double soma2 = a.y - b.y;
		if (soma2 < 0) {
			soma2 = soma2 * (1);
		}
		return soma1 + soma2;
	}

	public void evaluarVizinhosLocais() {

		ArrayList<Point> grupoAbertoNovo = new ArrayList<Point>();
		if (grupoAberto.size() == 0) {
			grupoAberto.addAll(grupoFechado);
			grupoAberto.removeAll(grupoExcluido);
			grupoFechado = new ArrayList<Point>();
			count = -1;
		} else {
			count = 2;
		}
		if (debug) {
			System.out.print("Grupo aberto: " + grupoAberto.size() + "\n");
			for (Point p : grupoAberto) {
				System.out.print("(" + p.x + ", " + p.y + ") ");
			}
			System.out.print("\n");
		}
		for (int k = 0; k < grupoAberto.size(); k++) {
			pontoAtual = grupoAberto.get(k);
			if (pontoAtual.x == pontoFinalEscolhido.x && pontoAtual.y == pontoFinalEscolhido.y) {
				encontrado = true;
				return;
			}
			ArrayList<Point> posicoes = new ArrayList<Point>();
			ArrayList<Double> custos = new ArrayList<Double>();
			for (int i = pontoAtual.x - 1; i <= pontoAtual.x + 1; i++) {
				for (int j = pontoAtual.y - 1; j <= pontoAtual.y + 1; j++) {
					if (i < 0 || i >= meuEspaco.length || j < 0 || j >= meuEspaco[0].length
							|| (i == pontoAtual.x && j == pontoAtual.y || meuEspaco[i][j].getVisitado()
									|| (meuEspaco[i][j].getPeso() == max && evitarMax))) {
						continue;
					} else {
						double custo = meuEspaco[pontoAtual.x][pontoAtual.y].getSoma() + meuEspaco[i][j].getPeso()
								+ distanciaManhattan(meuEspaco[i][j].getPosicao(), pontoFinalEscolhido);
						custos.add(custo);
						posicoes.add(meuEspaco[i][j].getPosicao());
						if (debug)
							System.out.println("ponto (" + i + ", " + j + "): " + custo);
					}

				}
			}
			if (custos.size() != 0) {

				double menorChave = Double.MAX_VALUE;
				for (int i = 0; i < custos.size(); i++) {
					if (menorChave > custos.get(i)) {
						menorChave = custos.get(i);
					}
				}
				while (custos.indexOf(menorChave) != -1) {
					int indiceLista = custos.indexOf(menorChave);
					Point avaliado = posicoes.remove(indiceLista);
					custos.remove(indiceLista);

					if (avaliado != null) {
						int indice = grupoAbertoNovo.indexOf(avaliado);
						if (indice == -1) {
							grupoAbertoNovo.add(avaliado);
							meuEspaco[avaliado.x][avaliado.y].setSoma(pontoAtual,
									meuEspaco[pontoAtual.x][pontoAtual.y].getSoma()
											+ meuEspaco[avaliado.x][avaliado.y].getPeso());
						} else {
							if (meuEspaco[pontoAtual.x][pontoAtual.y].getSoma()
									+ meuEspaco[avaliado.x][avaliado.y].getPeso() < meuEspaco[avaliado.x][avaliado.y]
											.getSoma()) {
								meuEspaco[avaliado.x][avaliado.y].setSoma(pontoAtual,
										meuEspaco[pontoAtual.x][pontoAtual.y].getSoma()
												+ meuEspaco[avaliado.x][avaliado.y].getPeso());
							}
						}
					} else {
						break;
					}
				}
			} else {
				grupoExcluido.add(pontoAtual);
			}
			meuEspaco[pontoAtual.x][pontoAtual.y].setVisitado(true);
			grupoFechado.add(pontoAtual);

		}
		grupoAberto.clear();
		grupoAberto.addAll(grupoAbertoNovo);
		count--;
		if (debug && count <= 0) {
			Scanner reader = new Scanner(System.in); // Reading from System.in
			System.out.println("Enter a number: ");
			int n = reader.nextInt();
			count = count + n;
			reader.close();
		}
	}

	public ArrayList<Point> recuperarCaminho() {
		ArrayList<Point> aux = new ArrayList<Point>();
		aux.add(pontoAtual);
		do {
			meuEspaco[pontoAtual.x][pontoAtual.y].setVisitado(false);
			pontoAtual = meuEspaco[pontoAtual.x][pontoAtual.y].getPosicaoAnterior();
			aux.add(pontoAtual);
		} while (pontoAtual.x != pontoInicial.x && pontoAtual.y != pontoInicial.y);
		meuEspaco[pontoAtual.x][pontoAtual.y].setVisitado(false);
		return aux;
	}

}

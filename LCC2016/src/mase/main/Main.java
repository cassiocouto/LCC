package mase.main;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import mase.entities.agents.GRIDManager;
import mase.entities.Celula;

public class Main {
	public String worcwest = "res/worcwest15.bmp";
	public String factory = "res/factory15.bmp";
	public String powerline = "res/powerline15.bmp";
	private static Celula[][] espaco;
	private static ArrayList<Point> espacosIniciais;
	private static ArrayList<Point> espacosFinais;
	private static ArrayList<ArrayList<Point>> caminhosEncontrados;
	private static long custo;

	public static long tempoInicio;

	public static void main(String[] args) {
		Main m = new Main();
		m.iniciarGrafoComPesos();
		m.iniciarEspacosIniciais();
		m.iniciarEspacosFinais();
		m.iniciarPlataforma();
	}

	public void iniciarGrafoComPesos() {
		BufferedImage imgWorcwest;
		try {
			imgWorcwest = ImageIO.read(new File(worcwest));
			int altura = imgWorcwest.getHeight();
			int largura = imgWorcwest.getWidth();
			espaco = new Celula[altura][largura];

			for (int i = 0; i < altura; i++) {
				for (int j = 0; j < largura; j++) {
					Color c = new Color(imgWorcwest.getRGB(i, j));
					int red = c.getRed();
					int green = c.getGreen();
					int blue = c.getBlue();
					int peso = 0;
					if (red == 0 && green == 255 && blue == 0) {
						// floresta decidua
						peso = 4;
					} else if (red == 255 && green == 255 && blue == 0) {
						// floresta conifera
						peso = 5;
					} else if (red == 255 && green == 0 && blue == 0) {
						// agricultura
						peso = 1;
					} else if (red == 0 && green == 0 && blue == 255) {
						// urbanizacao
						peso = 1000;
					} else { // removed
						peso = Integer.MAX_VALUE;
					}

					espaco[i][j] = new Celula(new Point(i, j), peso);
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}
	}

	public void iniciarEspacosIniciais() {
		espacosIniciais = new ArrayList<Point>();
		try {
			BufferedImage imgFactory = ImageIO.read(new File(powerline));
			int altura = imgFactory.getHeight();
			int largura = imgFactory.getWidth();
			for (int i = 0; i < altura; i++) {
				for (int j = 0; j < largura; j++) {
					Color c = new Color(imgFactory.getRGB(i, j));
					int red = c.getRed();
					int green = c.getGreen();
					int blue = c.getBlue();
					if (red != 255 && green != 255 && blue != 255 && espaco[i][j].getPeso() != Integer.MAX_VALUE) {
						espacosIniciais.add(new Point(i, j));
					}
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void iniciarEspacosFinais() {
		espacosFinais = new ArrayList<Point>();
		try {
			BufferedImage imgPowerline = ImageIO.read(new File(factory));
			int altura = imgPowerline.getHeight();
			int largura = imgPowerline.getWidth();
			for (int i = 0; i < altura; i++) {
				for (int j = 0; j < largura; j++) {
					Color c = new Color(imgPowerline.getRGB(i, j));
					int red = c.getRed();
					int green = c.getGreen();
					int blue = c.getBlue();
					if (red != 255 && green != 255 && blue != 255 && espaco[i][j].getPeso() != Integer.MAX_VALUE) {
						espacosFinais.add(new Point(i, j));
					}
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void iniciarPlataforma() {
		Runtime runtime = Runtime.instance();

		Profile profile = new ProfileImpl();
		profile.setParameter(Profile.GUI, "false");
		profile.setParameter(Profile.CONTAINER_NAME, "Cont_LCC_0");
		profile.setParameter(Profile.PLATFORM_ID, "MASE_0");

		AgentContainer agentCont = runtime.createMainContainer(profile);
		try {
			tempoInicio = System.currentTimeMillis();
			GRIDManager g = new GRIDManager(espaco);
			agentCont.acceptNewAgent("GRIDManager", g);
			agentCont.getAgent("GRIDManager").start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void salvarImagem() {

		BufferedImage imgResultado;
		try {
			String nomeNovaImagem = "imagemfinal" + System.currentTimeMillis() + ".bmp";
			File novaImagem = new File(nomeNovaImagem);
			novaImagem.createNewFile();
			imgResultado = new BufferedImage(espaco.length, espaco[0].length, BufferedImage.TYPE_INT_RGB);
			for (int i = 0; i < espaco.length; i++) {
				for (int j = 0; j < espaco[0].length; j++) {
					int red = 0;
					int green = 0;
					int blue = 255;
					if (espaco[i][j].getPeso() == 4) {
						// floresta decidua
						red = 0;
						green = 255;
						blue = 0;
					} else if (espaco[i][j].getPeso() == 5) {
						// floresta conifera
						red = 255;
						green = 255;
						blue = 0;
					} else if (espaco[i][j].getPeso() == 1) {
						// agricultura
						red = 255;
						green = 0;
						blue = 0;
					} else if (espaco[i][j].getPeso() == 100) {
						// urbanizacao
						red = 0;
						green = 0;
						blue = 255;
					}
					red = (red << 16) & (0x00ff0000);
					green = (green << 8) & (0x0000ff00);
					blue = (blue) & (0x000000ff);
					int rgb = 0xff000000 | red | green | blue;
					imgResultado.setRGB(i, j, rgb);
				}
			}
			ImageIO.write(imgResultado, "bmp", novaImagem);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void salvarImagem(ArrayList<Point> caminhoUnico, long custo, long tempo, int qtdAgentes) {

		BufferedImage imgResultado;
		try {
			String nomeNovaImagem = "imagemfinal_qtdAgentes_" + qtdAgentes + "_tempo_" + tempo + "_custo_" + custo
					+ ".bmp";
			File novaImagem = new File(nomeNovaImagem);
			novaImagem.createNewFile();
			imgResultado = new BufferedImage(espaco.length, espaco[0].length, BufferedImage.TYPE_INT_RGB);
			for (int i = 0; i < espaco.length; i++) {
				for (int j = 0; j < espaco[0].length; j++) {
					int red = 0;
					int green = 0;
					int blue = 0;
					// if (espaco[i][j].getPeso() == 4) {
					// // floresta decidua
					// red = 0;
					// green = 255;
					// blue = 0;
					// } else if (espaco[i][j].getPeso() == 5) {
					// // floresta conifera
					// red = 255;
					// green = 255;
					// blue = 0;
					// } else if (espaco[i][j].getPeso() == 1) {
					// // agricultura
					// red = 255;
					// green = 0;
					// blue = 0;
					// } else if (espaco[i][j].getPeso() == 100) {
					// // urbanizacao
					// red = 0;
					// green = 0;
					// blue = 255;
					// }
					red = (red << 16) & (0x00ff0000);
					green = (green << 8) & (0x0000ff00);
					blue = (blue) & (0x000000ff);
					int rgb = 0xff000000 | red | green | blue;
					imgResultado.setRGB(i, j, rgb);
				}
			}
			
			for(Point ponto:espacosIniciais){
				int red = 0xff;
				int green = 0xff;
				int blue = 0x00;
				int rgb = 0xff000000 | red | green | blue;
				imgResultado.setRGB(ponto.x, ponto.y, rgb);
			}
			for(Point ponto:espacosFinais){
				int red = 0xff;
				int green = 0xff;
				int blue = 0x00;
				int rgb = 0xff000000 | red | green | blue;
				imgResultado.setRGB(ponto.x, ponto.y, rgb);
			}
			
			for(Point ponto:caminhoUnico){
				int red = 0xcc;
				int green = 0x00;
				int blue = 0x66;
				int rgb = 0xff000000 | red | green | blue;
				imgResultado.setRGB(ponto.x, ponto.y, rgb);
			}
			
			ImageIO.write(imgResultado, "bmp", novaImagem);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static Celula[][] getEspaco() {
		return espaco;
	}

	public static synchronized ArrayList<Point> getEspacosIniciais() {
		return espacosIniciais;
	}

	public static synchronized Point getProximoEspacoInicial() {
		if (espacosIniciais.isEmpty()) {
			return null;
		}
		return espacosIniciais.remove(0);
	}

	public static synchronized ArrayList<Point> getEspacosFinais() {
		return espacosFinais;
	}

	public static ArrayList<Point> getCaminhoEncontrado(int index){
		return caminhosEncontrados.get(index);
	}
	
	public static synchronized void addCaminhoEncontrado(ArrayList<Point> caminho, String owner) {
		if (caminhosEncontrados == null) {
			caminhosEncontrados = new ArrayList<ArrayList<Point>>();
		}
		caminhosEncontrados.add(caminho);
		for (Point posicao : caminho) {
			espaco[posicao.x][posicao.y].setOwner(owner);
		}
	}

	public static long getCusto() {
		return custo;
	}

	public static void setCusto(long custo) {
		Main.custo = custo;
	}

}

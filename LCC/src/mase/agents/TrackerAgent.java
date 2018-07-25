package mase.agents;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import mase.main.Main;

public class TrackerAgent extends Agent {

	private static final long serialVersionUID = 1L;
	private int id;
	private ArrayList<Point> initialSpaces;

	public TrackerAgent(int id, ArrayList<Point> initialSpaces) {
		this.id = id;
		this.initialSpaces = initialSpaces;

	}

	public void setup() {
		Main.agentsAddresses[id] = getAID();
		if (Main.choosenStrategy == Main.DIJKSTRA_STRATEGY) {
			this.addBehaviour(new DijkstraPathFind(initialSpaces));
		}
	}

	private class DijkstraPathFind extends CyclicBehaviour {

		private static final long serialVersionUID = 1L;
		private boolean[][] visited;
		private Long[][] sum;
		private Point[][] parent;
		private ArrayList<Point> initialSpaces;
		private Point initialSpace;
		private ArrayList<Point> actualSpaces;
		private ArrayList<Point> adjacentSpaces;

		private ArrayList<Point> pathFound;
		private long minimumCost = Long.MAX_VALUE;

		public DijkstraPathFind(ArrayList<Point> initialSpaces) {
			this.initialSpaces = initialSpaces;
		}

		public void action() {
			if (initialSpaces.size() == 0) {
				// proposing path
				ACLMessage m = new ACLMessage(ACLMessage.PROPOSE);
				m.addReceiver(Main.GRIDManagerAddress);
				m.setContent(minimumCost + "");
				myAgent.send(m);
				myAgent.doWait();

				m = myAgent.receive();
				if (m.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
					m = new ACLMessage(ACLMessage.INFORM);
					m.addReceiver(Main.GRIDManagerAddress);
					try {
						m.setContentObject(pathFound);
						myAgent.send(m);
					} catch (IOException e) {
						e.printStackTrace();
					}

				}

				myAgent.doDelete();

			}
			initialSpace = initialSpaces.remove(0);

			int height = Main.getWeightedGraph().length;
			int width = Main.getWeightedGraph()[0].length;
			visited = new boolean[height][width];
			sum = new Long[height][width];
			parent = new Point[height][width];
			sum[initialSpace.x][initialSpace.y] = (long)Main.getWeightedGraph()[initialSpace.x][initialSpace.y].getWeight();

			actualSpaces = new ArrayList<Point>();
			adjacentSpaces = new ArrayList<Point>();

			actualSpaces.add(initialSpace);

			do {
				for (Point actualSpace : actualSpaces) {
					for (int i = -1; i <= 1; i++) {
						for (int j = -1; j <= 1; j++) {
							if (i == 0 && j == 0)
								continue;
							int nextX = (int) (actualSpace.x + i);
							if (nextX < 0 || nextX >= height)
								break;

							int nextY = (int) (actualSpace.y + j);
							if (nextY < 0 || nextY >= width)
								continue;

							if (visited[nextX][nextY])
								continue;

							long tentative = Main.getWeightedGraph()[nextX][nextY].getWeight()
									+ sum[actualSpace.x][actualSpace.y];
							if (sum[nextX][nextY] == null || tentative < sum[nextX][nextY]) {
								sum[nextX][nextY] = tentative;
								parent[nextX][nextY] = actualSpace;
							}
							if (!adjacentSpaces.contains(new Point(nextX, nextY))) {
								adjacentSpaces.add(new Point(nextX, nextY));
							}
						}
					}
					visited[actualSpace.x][actualSpace.y] = true;
				}
				actualSpaces = new ArrayList<Point>();
				actualSpaces.addAll(adjacentSpaces);
				adjacentSpaces = new ArrayList<Point>();
			} while (actualSpaces.size() != 0);
			retrievePath();
		}

		public void retrievePath() {
			long currentMinimumSum = Long.MAX_VALUE;
			Point choosenFinalSpace = null;

			for (Point finalSpace : Main.getFinalSpaces()) {
				if (currentMinimumSum > sum[finalSpace.x][finalSpace.y]) {
					currentMinimumSum = sum[finalSpace.x][finalSpace.y];
					choosenFinalSpace = finalSpace;
				}
			}

			if (minimumCost < currentMinimumSum) {
				return;
			} else {
				minimumCost = currentMinimumSum;
				pathFound = new ArrayList<Point>();
				Point actualPoint = choosenFinalSpace;
				do {
					pathFound.add(actualPoint);
					actualPoint = parent[actualPoint.x][actualPoint.y];
				} while (actualPoint != initialSpace);
				pathFound.add(actualPoint);
			}
		}

	}
}

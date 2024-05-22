import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

class Drumuri {
	private static final String INPUT_FILE = "drumuri.in";
	private static final String OUTPUT_FILE = "drumuri.out";

	static class Edge {
		int from;
		int to;
		int cost;

		Edge(int from, int to, int cost) {
			this.from = from;
			this.to = to;
			this.cost = cost;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			Edge edge = (Edge) o;
			return to == edge.to && from == edge.from && cost == edge.cost;
		}

		@Override
		public int hashCode() {
			return Objects.hash(from, to, cost);
		}

		@Override
		public String toString() {
			return String.format("%d -> %d : %d", from, to, cost);
		}
	}

	private static long[] dijkstra(int start, List<List<Edge>> graph, int N) {
		long[] dist = new long[N + 1];
		Arrays.fill(dist, Long.MAX_VALUE);
		dist[start] = 0;
		PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingLong(e -> e.cost));
		pq.add(new Edge(start, start, 0));

		while (!pq.isEmpty()) {
			Edge current = pq.poll();

			if (current.cost > dist[current.to]) {
				continue;
			}

			for (Edge edge : graph.get(current.to)) {
				long newCost = dist[current.to] + edge.cost;
				if (newCost < dist[edge.to]) {
					dist[edge.to] = newCost;
					pq.add(new Edge(current.to, edge.to, edge.cost));
				}
			}
		}
		return dist;
	}

	// functie de inversare a graf-ului
	private static List<List<Edge>> reverse(List<List<Edge>> graph, int N) {
		List<List<Edge>> reverseGraph = new ArrayList<>();
		for (int i = 0; i <= N; i++) {
			reverseGraph.add(new ArrayList<>());
		}
		for (int i = 1; i <= N; i++) {
			for (Edge e : graph.get(i)) {
				reverseGraph.get(e.to).add(new Edge(e.to, e.from, e.cost));
			}
		}
		return reverseGraph;
	}

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(INPUT_FILE));
		StringTokenizer st = new StringTokenizer(br.readLine());
		int N = Integer.parseInt(st.nextToken());
		int M = Integer.parseInt(st.nextToken());

		List<List<Edge>> graph = new ArrayList<>();
		for (int i = 0; i <= N; i++) {
			graph.add(new ArrayList<>());
		}

		for (int i = 0; i < M; i++) {
			st = new StringTokenizer(br.readLine());
			int a = Integer.parseInt(st.nextToken());
			int b = Integer.parseInt(st.nextToken());
			int c = Integer.parseInt(st.nextToken());
			// adaug muchia in graf
			graph.get(a).add(new Edge(a, b, c));
		}

		st = new StringTokenizer(br.readLine());
		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		int z = Integer.parseInt(st.nextToken());

		br.close();

		long[] distFromX = dijkstra(x, graph, N);
		long[] distFromY = dijkstra(y, graph, N);
		long[] distToZ = dijkstra(z, reverse(graph, N), N);

		// acum trebuie sa merg prin toate grafurile si sa aflu costul minim
		long minCost = Long.MAX_VALUE;

		// trbeuie sa am drum de la Y la Z si de la X la Z
		// pot face asta si prin intermediul unui nod intermediar si poate chiar
		// si vecinul acestuia
		for (int i = 1; i <= N; i++) {
			// Pentru fiecare vecin al nodului i
			for (Edge edge : graph.get(i)) {
				int neighbor = edge.to;  // Vecinul nodului i
				long i_to_neighbor = edge.cost;  // Costul muchiei de la i la vecin
		
				// Verific daca drumurile sunt valide pentru pornirea de la X
				if (distFromX[i] != Long.MAX_VALUE && distToZ[neighbor] != Long.MAX_VALUE
					&& distFromY[neighbor] != Long.MAX_VALUE) {
					// pot sa merg X -> i -> neighbor -> Z + Y -> neighbor (-> Z)
					long cost_starting_with_x = distFromX[i] + i_to_neighbor
						+ distToZ[neighbor] + distFromY[neighbor];
					minCost = Math.min(minCost, cost_starting_with_x);
				}
		
				// Verific daca drumurile sunt valide pentru pornirea de la Y
				if (distFromY[i] != Long.MAX_VALUE && distToZ[neighbor] != Long.MAX_VALUE
					&& distFromX[neighbor] != Long.MAX_VALUE) {
					// pot sa merg Y -> i -> neighbor -> Z + X -> neighbor (-> Z)
					long cost_starting_with_y = distFromY[i] + i_to_neighbor
						+ distToZ[neighbor] + distFromX[neighbor];
					minCost = Math.min(minCost, cost_starting_with_y);
				}
			}
		}

		PrintWriter pw = new PrintWriter(new FileWriter(OUTPUT_FILE));
		pw.println(minCost);
		pw.close();
	}
}
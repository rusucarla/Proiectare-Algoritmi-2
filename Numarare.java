import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;

class Edge {
	int from;
	int to;

	Edge(int from, int to) {
		this.from = from;
		this.to = to;
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
		return to == edge.to && from == edge.from;
	}

	@Override
	public int hashCode() {
		return Objects.hash(from, to);
	}

	@Override
	public String toString() {
		return String.format("%d -> %d", from, to);
	}
}

public class Numarare {
	private static final String INPUT_FILE = "numarare.in";
	private static final String OUTPUT_FILE = "numarare.out";
	private static final int MOD = 1000000007;

	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(INPUT_FILE));
		StringTokenizer st = new StringTokenizer(reader.readLine());
		int N = Integer.parseInt(st.nextToken());
		int M = Integer.parseInt(st.nextToken());

		List<List<Integer>> adjList1 = new ArrayList<>();
		List<List<Integer>> adjList2 = new ArrayList<>();
		Set<Edge> edges1 = new HashSet<>();
		Set<Edge> edges2 = new HashSet<>();

		for (int i = 0; i <= N; i++) {
			adjList1.add(new ArrayList<>());
			adjList2.add(new ArrayList<>());
		}

		loadEdges(reader, adjList1, edges1, M);
		loadEdges(reader, adjList2, edges2, M);

		// dp[i] = numarul de cai distincte catre nodul i
		int[] dp1 = new int[N + 1];
		int[] dp2 = new int[N + 1];
		// nodul 1 este nodul de start si are o singura cale catre el
		dp1[1] = dp2[1] = 1;

		for (int i = 1; i <= N; i++) {
			// pentru fiecare muchie (i, j) din graful 1
			for (int j : adjList1.get(i)) {
				// daca muchia (i, j) exista si in graful 2 
				// (pentru ca trebuie nodurile sa coincida)
				if (edges2.contains(new Edge(i, j))) {
					// numarul de cai catre nodul j se actualizeaza
					// cu numarul de cai catre nodul i + numarul de cai catre nodul j
					dp1[j] = (dp1[j] + dp1[i]) % MOD;
				}
			}
			// acelasi lucru pentru graful 2
			for (int j : adjList2.get(i)) {
				if (edges1.contains(new Edge(i, j))) {
					dp2[j] = (dp2[j] + dp2[i]) % MOD;
				}
			}
		}

		BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE));
		// Afisez numarul de lanturi elementare comune intre graful 1 si graful
		// 2 pentru nodul N (1 -> N)
		writer.write(String.valueOf(dp1[N]));
		writer.close();
		reader.close();
	}

	private static void loadEdges(BufferedReader reader,
			List<List<Integer>> adjList, Set<Edge> edges,
			int M) throws IOException {
		StringTokenizer st;
		for (int i = 0; i < M; i++) {
			st = new StringTokenizer(reader.readLine());
			int u = Integer.parseInt(st.nextToken());
			int v = Integer.parseInt(st.nextToken());
			adjList.get(u).add(v);
			edges.add(new Edge(u, v));
		}
	}
}

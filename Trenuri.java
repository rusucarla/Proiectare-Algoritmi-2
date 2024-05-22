import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.StringTokenizer;

public class Trenuri {
	static final String INPUT_FILE = "trenuri.in";
	static final String OUTPUT_FILE = "trenuri.out";
	static List<List<Integer>> adjList;
	static Map<String, Integer> cityIndex;
	static List<String> indexCity;
	static int[] inDegree;

	// Metoda pentru sortare topologica
	static List<Integer> topologicalSort(int numVertices) {
		List<Integer> order = new ArrayList<>();
		Queue<Integer> queue = new LinkedList<>();

		for (int i = 1; i <= numVertices; i++) {
			if (inDegree[i] == 0) {
				queue.add(i);
			}
		}

		while (!queue.isEmpty()) {
			int vertex = queue.poll();
			order.add(vertex);
			// -1 pentru corectarea indexarii
			for (int neighbor : adjList.get(vertex - 1)) {
				inDegree[neighbor]--;
				if (inDegree[neighbor] == 0) {
					queue.add(neighbor);
				}
			}
		}

		return order;
	}

	// Metoda pentru gasirea celui mai lung drum
	static int longestPath(int start, int end, int numVertices) {
		List<Integer> topOrder = topologicalSort(numVertices);
		// +1 pentru a gestiona corect indexarea de la 1
		int[] distance = new int[numVertices + 1];
		Arrays.fill(distance, Integer.MIN_VALUE);
		distance[start] = 0;

		for (int i : topOrder) {
			if (distance[i] != Integer.MIN_VALUE) {
				// -1 pentru corectarea indexarii
				for (int neighbor : adjList.get(i - 1)) {
					if (distance[neighbor] < distance[i] + 1) {
						distance[neighbor] = distance[i] + 1;
					}
				}
			}
		}
		// pentru ca vreau sa am numarul de orase, nu de trasee (a.k.a. vizitez
		// si orasul de start)
		return distance[end] + 1;
	}

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(INPUT_FILE));

		StringTokenizer st = new StringTokenizer(br.readLine());
		final String startCity = st.nextToken();
		final String endCity = st.nextToken();

		st = new StringTokenizer(br.readLine());
		final int M = Integer.parseInt(st.nextToken());

		adjList = new ArrayList<>();
		cityIndex = new HashMap<>();
		indexCity = new ArrayList<>();
		// +2 pentru siguranta si pentru indexare de la 1
		inDegree = new int[M + 2];

		int index = 1;
		for (int i = 0; i < M; i++) {
			st = new StringTokenizer(br.readLine());
			String x = st.nextToken();
			String y = st.nextToken();

			if (!cityIndex.containsKey(x)) {
				cityIndex.put(x, index);
				indexCity.add(x);
				// Initializare lista pentru fiecare oras nou
				adjList.add(new ArrayList<>()); 
				index++;
			}
			if (!cityIndex.containsKey(y)) {
				cityIndex.put(y, index);
				indexCity.add(y);
				// Initializare lista pentru fiecare oras nou
				adjList.add(new ArrayList<>()); 
				index++;
			}

			int xIndex = cityIndex.get(x);
			int yIndex = cityIndex.get(y);
			// -1 pentru corectarea indexarii
			// Adaug muchia x -> y
			adjList.get(xIndex - 1).add(yIndex);
			// Incrementez gradul intern pentru y 
			inDegree[yIndex]++;
		}
		br.close();

		int startIdx = cityIndex.get(startCity);
		int endIdx = cityIndex.get(endCity);

		int maxPath = longestPath(startIdx, endIdx, index - 1);
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(OUTPUT_FILE)));
		out.println(maxPath);
		out.close();
	}
}

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

public class Scandal {
	private static final String INPUT_FILE = "scandal.in";
	private static final String OUTPUT_FILE = "scandal.out";
	static int numar_prieteni, numar_reguli;
	static List<List<Integer>> graph, reverseGraph;
	static boolean[] visited;
	static Stack<Integer> stack;
	static int[] component;
	static boolean[] invitat;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(INPUT_FILE));
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(OUTPUT_FILE)));
		StringTokenizer st = new StringTokenizer(br.readLine());

		numar_prieteni = Integer.parseInt(st.nextToken());
		numar_reguli = Integer.parseInt(st.nextToken());

		graph = new ArrayList<>(2 * numar_prieteni + 1);
		reverseGraph = new ArrayList<>(2 * numar_prieteni + 1);
		for (int i = 0; i <= 2 * numar_prieteni; i++) {
			graph.add(new ArrayList<>());
			reverseGraph.add(new ArrayList<>());
		}

		for (int i = 0; i < numar_reguli; i++) {
			st = new StringTokenizer(br.readLine());
			int prieten_x = Integer.parseInt(st.nextToken());
			int prieten_y = Integer.parseInt(st.nextToken());
			int cerinta = Integer.parseInt(st.nextToken());

			// negarea unui prieten i se va face cu + numar_prieteni
			// pentru a fi tratat drept un nod separat => daca vom gasi in
			// aceeasi componenta nodul si negarea sa vom sti ca exista o contradictie

			switch (cerinta) {
				case 0:
					// Cel putin unul dintre ei trebuie sa fie prezent
					// adica : not(prieten_x) => prieten_y
					addRegula(prieten_x + numar_prieteni, prieten_y);
					// si  not(prieten_y) => prieten_x
					addRegula(prieten_y + numar_prieteni, prieten_x);
					break;
				case 1:
					// Daca prieten_x nu e prezent, atunci prieten_y nu poate fi
					// prezent
					addRegula(prieten_x + numar_prieteni, prieten_y + numar_prieteni);
					// pentru consistena aplic si implicarea inversa
					// not(x) => not(y) == y => x
					addRegula(prieten_y, prieten_x);
					break;
				case 2:
					// Daca prieten_y nu e prezent, atunci prieten_x nu poate fi
					// prezent
					addRegula(prieten_y + numar_prieteni, prieten_x + numar_prieteni);
					// pentru consistenta aplic si implicare inversa
					// not(y) => not(x) == x => y
					addRegula(prieten_x, prieten_y);
					break;
				case 3:
					// Cel putin unul dintre ei trebuie sa nu participe
					// adica : prieten_x => not(prieten_y)
					addRegula(prieten_x, prieten_y + numar_prieteni);
					// si prieten_y => not(prieten_x)
					addRegula(prieten_y, prieten_x + numar_prieteni);
					break;
				default:
					break;
			}
		}
		// Aflu cu ajutorul algoritmului lui kosaraju componentele tari conexe
		algKosaraju();
		// Verific daca solutia gasita este satisfiabila (colorata ok)
		if (isSatisfiable()) {
			List<Integer> petrecareti = new ArrayList<>();
			for (int i = 1; i <= numar_prieteni; i++) {
				if (invitat[i]) {
					// Daca poate fi prezent, il adaug in lista de participanti
					petrecareti.add(i);
				}
			}
			// Afisez numarul de participanti si apoi pe fiecare participant
			out.println(petrecareti.size());
			for (int petrecaret : petrecareti) {
				out.println(petrecaret);
			}
		}

		out.close();
		br.close();
	}

	static void addRegula(int from, int to) {
		graph.get(from).add(to);
		reverseGraph.get(to).add(from);
	}

	static void dfs_non_reverse(int node) {
		// Marchez nodul ca vizitat
		visited[node] = true;
		// Parcurg vecinii nodului curent si aplic dfs pe ei
		// pentru a construi ordinea de terminare a nodurilor
		for (int neighbor : graph.get(node)) {
			if (!visited[neighbor]) {
				dfs_non_reverse(neighbor);
			}
		}
		stack.push(node);
	}

	static void dfs_reverse(int node, int numar_componente) {
		// Setez componenta nodului la care am ajuns
		component[node] = numar_componente;
		// Parcurg vecinii nodului curent si aplic dfs pe ei pentru a construi
		// componenta tare conexa curenta
		for (int neighbor : reverseGraph.get(node)) {
			if (component[neighbor] == -1) {
				dfs_reverse(neighbor, numar_componente);
			}
		}
	}
	// Algoritmul lui Kosaraju
	// modficat fata de cel din laborator pentru a tine minte din ce componenta
	// face parte fiecare nod (pentru a verifica contradictiile)
	static void algKosaraju() {
		// Pentru a trata nodurle negare ca noduri separate
		// am folosit 2 * numar_prieteni + 1 (pentru indexare de la 1)
		visited = new boolean[2 * numar_prieteni + 1];
		// Un stack global ca sa retin ordinea de terminare a nodurilor
		stack = new Stack<>();
		// Vectorul de componente: component[i] = j => nodul i se afla in componenta j
		component = new int[2 * numar_prieteni + 1];
		// Initial toate nodurile sunt in afara componentelor
		Arrays.fill(component, -1);

		// Aplic dfs pe graful initial si tin cont de ordinea de terminare (in stack)
		for (int i = 1; i <= 2 * numar_prieteni; i++) {
			if (!visited[i]) {
				dfs_non_reverse(i);
			}
		}
		// Am deja graful inversat, aplic dfs pe el in ordinea de terminare a
		// nodurilor
		// folosesc numar_componente pentru a tine minte numarul componentei
		int numar_componente = 0;
		// Cat timp mai am noduri in stack si nu e "vizitat" (nu stim in ce
		// componenta e) aplic dfs pe graful inversat
		while (!stack.isEmpty()) {
			int node = stack.pop();
			if (component[node] == -1) {
				dfs_reverse(node, numar_componente++);
			}
		}
	}

	// Verific daca solutia gasita este satisfiabila
	static boolean isSatisfiable() {
		invitat = new boolean[numar_prieteni + 1];
		for (int i = 1; i <= numar_prieteni; i++) {
			// Daca un nod si negarea sa se afla in aceeasi componenta => contradictie
			if (component[i] == component[i + numar_prieteni]) {
				return false;
			}
			// Pentru ca ordinea de terminare determina numarul componentei, cu
			// cat se termina mai tarziu cu atat e mai "important"
			// Astfel ca a fost determinat ca e adevarat inainte de negarea sa
			// si e "primul venit primul servit"
			if (component[i] > component[i + numar_prieteni]) {
				invitat[i] = true;
			} else {
				invitat[i] = false;
			}
		}
		return true;
	}
}

import busca.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ValeraTubes implements Estado, Heuristica, Antecessor {
    public String getDescricao() {
        return
                "Power has got a rectangle table consisting of n rows and m columns. \n" +
                        "Valera numbered the table rows starting from one, from top to bottom \n" +
                        "and the columns – starting from one, from left to right. We will \n" +
                        "represent cell that is on the intersection of row x and column y \n" +
                        "by a pair of integers (x, y). \n\n";

    }

    int n, m, k;
    int[][] table;
    int[][][] tubes;
    int[] tubosColocados;

    /**
     *  cria um estado inicial (vazio: nenhum tubo colocado)
     */
    public ValeraTubes(int linha, int coluna, int tubos) {
        n = linha;
        m = coluna;
        k = tubos;

        table = new int[n][m];
        for (int l = 0; l < n; l++) {
            for (int c = 0; c < m; c++) {
                table[l][c] = 0;
            }
        }

        tubes = new int[k][m*n][2];
        tubosColocados = new int[k];
        for (int t = 0; t < k; t++) {
            tubosColocados[t] = 0;
        }
    }

    /**
     *  cria um estado inicial a partir de outro (copia)
     */
    ValeraTubes(ValeraTubes modelo) {
        n = modelo.n;
        m = modelo.m;
        k = modelo.k;

        table = new int[n][m];
        for (int l = 0; l < n; l++) {
            for (int c = 0; c < m; c++) {
                table[l][c] = modelo.table[l][c];
            }
        }

        tubes = new int[k][m*n][2];
        for (int i=0;i<k;i++) {
            for (int j=0;j<m*n;j++) {
                for (int o=0;o<2;o++) {
                    tubes[i][j][o] = modelo.tubes[i][j][o];
                }
            }
        }

        tubosColocados = new int[k];
        for (int t = 0; t < k; t++) {
            tubosColocados[t] = modelo.tubosColocados[t];
        }
    }

    /**
     * verifica se o estado e meta
     */
    public boolean ehMeta() {
        int totalDeTubos = 0;
        for (int i= 0; i < k; i++){
            if (tubosColocados[i] < 2){
                return false;
            } else {
                totalDeTubos = totalDeTubos + tubosColocados[i];
            }
        }
        if (totalDeTubos < m*n){
            return false;
        }

        return true;
    }

    /**
     * Custo para geracao do estado
     */
    public int custo() {
        return 1;
    }

    /**
     * gera uma lista de sucessores do nodo.
     */
    public List<Estado> sucessores() {
        List<Estado> suc = new LinkedList<Estado>(); // a lista de sucessores

        for (int t = 1; t <= k; t++) { // coloca o tubo k em todas as posicoes livres
            for (int l = 0; l < n; l++) {
                for (int c = 0; c < m; c++) {
                    if (table[l][c] == 0) {
                        ValeraTubes novo = new ValeraTubes(this);
                        novo.table[l][c] = t;
                        novo.tubes[t-1][novo.tubosColocados[t-1]][0] = l;
                        novo.tubes[t-1][novo.tubosColocados[t-1]][1] = c;
                        novo.tubosColocados[t-1] = novo.tubosColocados[t-1]+1;

                        if (novo.ehValido(t)) {//Verifica se o tubo pode ser colocado nessa posicao
                            suc.add(novo);
                        }
                    }
                }
            }
        }
        return suc;
    }

    /** returna true se o estado e valido */
    private boolean ehValido(int t) {
        if (tubosColocados[t-1] > 1) {
            if (Math.abs(tubes[t-1][tubosColocados[t-1]-2][0] - tubes[t-1][tubosColocados[t-1]-1][0]) +
                    Math.abs(tubes[t-1][tubosColocados[t-1]-2][1] - tubes[t-1][tubosColocados[t-1]-1][1]) == 1) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * verifica se um estado e igual a outro
     * (usado para poda)
     */
    public boolean equals(Object o) {
        if (o instanceof ValeraTubes) {
            ValeraTubes e = (ValeraTubes) o;
            for (int i = 0; i < k; i++) {
                if (tubosColocados[i] != e.tubosColocados[i]) {
                    return false;
                } else {
                    for (int w = 0; w < tubosColocados[i]; w++) {
                        if (tubes[i][w][0] != e.tubes[i][w][0] || tubes[i][w][1] != e.tubes[i][w][1]) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * retorna o hashCode desse estado
     * (usado para poda, conjunto de fechados)
     */
    public int hashCode() {
        return toString().hashCode();
    }

    public String toString() {
        StringBuffer r = new StringBuffer("\n");

        r.append("\n\n");
        for (int i=0;i<n;i++) {
            for (int j=0;j<m;j++) {
                r.append(table[i][j]);
                if (j+1<m) {
                    r.append(" ");
                }
            }
            if (i+1<n) {
                r.append("\n");
            } else {
                r.append("  h()="+h()+"\n");

            }
        }
        r.append("\n");
        for (int q = 0; q < k ; q++) {
            int tubo = q+1;
            if (tubosColocados[q] > 0) {
                r.append("Tubo" + tubo + ": ");
                for (int w = 0; w < tubosColocados[q]; w++) {
                    r.append("[" + tubes[q][w][0] + ", " + tubes[q][w][1] + "]");
                }
            } else {
                r.append("Tubo " + (tubo) + " ainda não posicionado" );
            }
            if (q < k-1) {
                r.append("\n");
            }
        }

        return r.toString();
    }

    public List<Estado> antecessores() {
        return sucessores();
    }

    /**
     * Heuristica
     */
    public int h() {
        return h1() + h2();
    }

    /**
     * Heuristica: Numero de tubos ainda não colocados ou menores q 2
     */
    public int h1() {
        int tubosNP = 0;
        for(int i = 0; i < k; i++){
            if (tubosColocados[i] == 0) {
                tubosNP = tubosNP + 2;
            }
            if (tubosColocados[i] == 1) {
                tubosNP = tubosNP + 1;
            }
        }
        return tubosNP;
    }


    /**
     * Heuristica: Numero de posições bloqueadas
     */
    public int h2() {
        ArrayList<int[]> livres = new ArrayList<int[]>();
        ArrayList<int[]> duvidas = new ArrayList<int[]>();
        for (int l = 0; l < n; l++) {
            for (int c = 0; c < m; c++) {
                boolean vazio = true;
                int tipo = 0;
                if (table[l][c] == 0){
                    if (l == 0){
                        if (c == 0){ //0 0
                            if (ehPonta(l+1, c, table[l+1][c]) || ehPonta(l, c+1, table[l][c+1])) { //tem ponta do lado
                                tipo = 2;
                            }
                            else if (table[l+1][c] == 0 || table[l][c+1] == 0){ //tem uma posição livre do lado - duvida
                                tipo = 1;
                            }
                        } else if (c == m-1) { //0 m
                                if (ehPonta(l+1, c, table[l+1][c]) || ehPonta(l, c-1, table[l][c-1])) {
                                    tipo = 2;
                                }
                                else if (table[l+1][c] == 0 || table[l][c-1] == 0){
                                    tipo = 1;
                                }
                        } else { //0 _
                            if (ehPonta(l+1, c, table[l+1][c]) || ehPonta(l, c-1, table[l][c-1]) || ehPonta(l, c+1, table[l][c+1])) {
                                tipo = 2;
                            }
                            else if (table[l+1][c] == 0 || table[l][c-1] == 0 || table[l][c+1] == 0){
                                tipo = 1;
                            }
                        }
                    } else if (l == n-1) {
                        if (c == 0){ //n 0
                            if (ehPonta(l-1, c, table[l-1][c]) || ehPonta(l, c+1, table[l][c+1])) {
                                tipo = 2;
                            }
                            else if (table[l-1][c] == 0 ||table[l][c+1] == 0){
                                tipo = 1;
                            }
                        } else if (c == m-1) { //n m
                            if (ehPonta(l-1, c, table[l-1][c]) || ehPonta(l, c-1, table[l][c-1])) {
                                tipo = 2;
                            }
                            else if (table[l-1][c] == 0 || table[l][c-1] == 0){
                                tipo = 1;
                            }
                        } else { //n _
                            if (ehPonta(l-1, c, table[l-1][c]) || ehPonta(l, c-1, table[l][c-1]) || ehPonta(l, c+1, table[l][c+1])) {
                                tipo = 2;
                            }
                            else if (table[l-1][c] == 0 || table[l][c-1] == 0 || table[l][c+1] == 0){
                                tipo = 1;
                            }
                        }
                    } else if (c == 0) { //_ 0
                        if (ehPonta(l+1, c, table[l+1][c]) || ehPonta(l-1, c, table[l-1][c]) || ehPonta(l, c+1, table[l][c+1])) {
                            tipo = 2;
                        }
                        else if (table[l+1][c] == 0 || table[l-1][c] == 0 || table[l][c+1] == 0){
                            tipo = 1;
                        }

                    } else if (c == m-1) { //_ m
                        if (ehPonta(l+1, c, table[l+1][c]) || ehPonta(l-1, c, table[l-1][c]) || ehPonta(l, c-1, table[l][c-1])) {
                            tipo = 2;
                        }
                        else if (table[l+1][c] == 0 || table[l-1][c] == 0 || table[l][c-1] == 0){
                            tipo = 1;
                        }

                    } else { //_ _
                        if (ehPonta(l+1, c, table[l+1][c]) || ehPonta(l-1, c, table[l-1][c]) || ehPonta(l, c+1, table[l][c+1]) || ehPonta(l, c-1, table[l][c-1])) {
                            tipo = 2;
                        }
                        else if (table[l+1][c] == 0 || table[l-1][c] == 0 || table[l][c+1] == 0 || table[l][c-1] == 0){
                            tipo = 1;
                        }
                    }
                } else{
                    vazio = false;
                }
                if (vazio) {
                    //System.out.println(tipo + " " + l + " " + c);
                    int pos[] = new int[2];
                    pos[0] = l;
                    pos[1] = c;
                    if (tipo == 0) { //existe a certeza de um bloqueio
                        return 10*m*n;
                    } else if (tipo == 1) { //posição em duvida
                        duvidas.add(pos);
                    } else if (tipo == 2) { //posição livre
                        livres.add(pos);
                    }
                }
            }
        }
        return 2*duvidas.size() + livres.size(); //queremos mais posições livres q ocupadas
    }

    private boolean ehPonta(int l, int c, int t) {
        if (t == 0) {
            return false;
        } else {
            if (tubes[t - 1][tubosColocados[t - 1]-1][0] == l && tubes[t - 1][tubosColocados[t - 1]-1][1] == c) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static void main(String[] a) {
        Estado inicial = new ValeraTubes(4, 4, 3);

        Nodo n = null;
        Date tempoInicio = new Date();

        int metodo = 3;

        if (metodo == 1) {
            System.out.println("Busca em Largura");
            n = new BuscaLargura().busca(inicial);
        } else if (metodo == 2) { // n:5 m:5 k:5 t:15392
            System.out.println("Busca em Profundidade");
            n = new BuscaProfundidade().busca(inicial);
        } else if (metodo == 3) { // n:5 m:5 k:5 t:157
            System.out.println("Busca AEstrela");
            n = new AEstrela().busca(inicial);
        }

        if (n == null) {
            System.out.println("Sem Solucao!");
        } else {

            Date agora = new Date();
            long tempo = agora.getTime() - tempoInicio.getTime();
            System.out.println("Solucao:\n" + n.montaCaminho() + "\n\n" + "Tempo de execuçao: " + tempo + "\n\n");

            System.out.println("Max Memory   : " +
                    Runtime.getRuntime().maxMemory());
            System.out.println("Total Memory : " +
                    Runtime.getRuntime().totalMemory());
            System.out.println("Free Memory  : " +
                    Runtime.getRuntime().freeMemory());
        }
    }
}

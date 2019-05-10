package exemplos;

import grafo.Grafo;
import grafo.GrafoDirigido;
import grafo.GrafoNaoDirigido;
import grafo.Vertice;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import busca.Antecessor;
import busca.BuscaLargura;
import busca.Estado;
import busca.Nodo;
import busca.Heuristica;

public class Powergrid implements Estado, Antecessor {
    public String getDescricao() {
	return "description";
    }
    
    int religador;
    int custo = 5;
    String status; // precisa? nao vai ter relevancia na geracao do caminho
    static int beta = 70;
    
    public Powergrid(int r) {
	this.religador = r;
    }
    
    public Powergrid(int r, int c, String s) {
	this.religador = r;
	this.custo = c;
	this.status = s;
    }
    
    public boolean equals(Object o) {
	if (o instanceof Powergrid) {
	    Powergrid e = (Powergrid)o;
	    return e.religador == this.religador;
	}
	return false;	
    }
    
    public static void setBeta(int b) {
	beta = b;
    }
    
    public int getBeta() {
	return beta;
    }
    
    public int hashCode() {
	return new Integer(religador).hashCode();
    }
    
    /**
     * verifica se o estado e meta
     */
    public boolean ehMeta() {
        return this.religador == meta;
    }
    
    /**
     * Custo para geracao de um estado
     */
    public int custo() {
        return 1;
    }
    
    /**
     * gera uma lista de sucessores do nodo.
     */
    public List<Estado> sucessores() {
        List<Estado> suc = new LinkedList<Estado>(); // a lista de sucessores
        Map<Vertice,Integer> custos = mapa.getVertice(religador).getCustoAdjacentes();
        for (Vertice v: custos.keySet()) {
            suc.add(new EstadoMapa(v.getId(), custos.get(v)));
        }
        return suc;
    }
    
    public List<Estado> antecessores() {
        return sucessores();
    }
    
    public String toString() {
        return religadores[religador] + " ";
    }
    
    public static int getMeta() {
        return meta; //lower beta
    }
    
    public static void setMeta(int religador) {
        meta = religador;
    }
    
    /** informacao estatica (o mapa) */
    public static Grafo mapa = new GrafoDirigido();
    static {
        for (int i=0; i<10; i++) {
            mapa.criaVertice(i);
        }
        mapa.criaAresta(0,1);
        mapa.criaAresta(0,2);

        mapa.criaAresta(1,3);
        mapa.criaAresta(1,4);
        
        mapa.criaAresta(2,5);
        mapa.criaAresta(2,8); 

//        mapa.criaAresta(3,4,1); 3 gasta 10
//        mapa.criaAresta(3,5,1);

        mapa.criaAresta(4,6);
        mapa.criaAresta(4,7);
        mapa.criaAresta(4,5); // e -> m

        mapa.criaAresta(5,4);
        mapa.criaAresta(5,2);
        
//        mapa.criaAresta(6,7,2);
        
//        mapa.criaAresta(7,8,2);
//        mapa.criaAresta(7,9,4);
        
        mapa.criaAresta(8,9);

//        mapa.criaAresta(9,10,1);
//        mapa.criaAresta(9,12,3);
    }
    
    //   RELIGADORES
    //  'A', 'B', 'C', 'P', 'G', 'O', 'N', 'L', 'F', 'M'
    //   0,   1,   2,   3,   4,   5,   6,   7,   8,   9
//    public final static char religadores[] = { 'A', 'B', 'C', 'P', 'G', 'O', 'F', 'N', 'M', 'L'};    
    public final static int religadores[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private static int meta; //lower beta
    
    public static void main(String[] a) {
	int beta = 70;
	
	setBeta(beta);
	Powergrid inicial = new Powergrid(0);
	
	Powergrid.setMeta(15); // change
	
	
        System.out.println(inicial.getDescricao());
        System.out.println("estado inicial= "+inicial);

        Nodo s = new BuscaLargura().busca(inicial);
        if (s != null) {
            System.out.println("solucao = "+s.montaCaminho());
            System.out.println("\toperacoes = "+s.getProfundidade());
            System.out.println("\tcusto = "+s.g());
        }
        
        // test
        /*
        Queue<Nodo> abertos = new PriorityQueue<Nodo>();
        abertos.add(new Nodo(new EstadoMapa(4,50),null));
        abertos.add(new Nodo(new EstadoMapa(1,300),null));
        abertos.add(new Nodo(new EstadoMapa(2,100),null));
        abertos.add(new Nodo(new EstadoMapa(3,200),null));
        abertos.add(new Nodo(new EstadoMapa(5,2),null));
        while (!abertos.isEmpty()) {
            System.out.println(abertos.remove());
        }
        */
       
    }
}
package it.polito.tdp.gosales.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.gosales.dao.GOsalesDAO;

public class Model {
	private GOsalesDAO dao;
	private List<Retailers> retailers;
	private SimpleWeightedGraph<Retailers, DefaultWeightedEdge> grafo;
	private Map<Integer, Retailers> retailerIdMap;
	private List<Arco> edges;
	
	public Model() {
		dao = new GOsalesDAO();
		List<Retailers> ret = this.dao.getAllRetailers();
		
		this.retailerIdMap = new HashMap<>();
		for(Retailers r : ret) {
			this.retailerIdMap.put(r.getCode(), r);
		}
	}
	
	public void creaGrafo(String country, int anno, int m) {
		retailers = this.dao.getVertici(country);
		
		
		this.grafo = new SimpleWeightedGraph<Retailers, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(this.grafo, this.retailers);
		
		this.edges = this.dao.getEdge(anno, country, m, retailerIdMap);
		
		for(Arco a: edges) {
			Graphs.addEdgeWithVertices(this.grafo, a.getR1(), a.getR2(), a.getPeso());
		}
		System.out.println("Grafo creato\n");
		System.out.println("Con "+this.grafo.vertexSet().size()+" vertici\n");
		System.out.println("Con "+this.grafo.edgeSet().size()+" archi\n");
	}
	
	public Connessa getCompConn(Retailers r) {
		ConnectivityInspector<Retailers, DefaultWeightedEdge> inspector = new ConnectivityInspector<Retailers, DefaultWeightedEdge>(this.grafo);
		Set<Retailers> connessi = inspector.connectedSetOf(r);
		int dimConn =  connessi.size();
	
		int somma = 0;
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			if(connessi.contains(this.grafo.getEdgeSource(e))&& connessi.contains(this.grafo.getEdgeTarget(e))) {
				somma += this.grafo.getEdgeWeight(e);
			}
		}
		Connessa c = new Connessa(dimConn, somma);
		return c;
	}
	
	public List<Products> getRetailerYear(Retailers r, int anno){
		return this.dao.getProductsRetailersYear(r, anno);
	}
	
	public List<String> getCountry(){
		return dao.getCountry();
	}
	
	public List<Integer> getYear(){
		return this.dao.getYear();
	}
	
	public int numVer() {
		return this.grafo.vertexSet().size();
	}
	
	public int numEdge() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Retailers> getVertici(){
		return retailers;
	}
	
	public List<Arco> getEdge(){
		List<Arco> archi = new ArrayList<>(this.edges);
		Collections.sort(archi);
		return archi;
	}
	
	//public SimulationResult eseguiSimulazione(Products prodotto, int q, int n, Retailers r) {
		
	//}
}

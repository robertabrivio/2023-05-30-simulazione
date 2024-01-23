package it.polito.tdp.gosales.model;

import java.time.LocalDate;
import java.util.PriorityQueue;

import it.polito.tdp.gosales.dao.GOsalesDAO;
import it.polito.tdp.gosales.model.Event.EventType;

public class Simulatore {
	//parametri in ingresso
	private Retailers r;
	private int anno;
	private Products p;
	private int N;
	private int nConnessi;
	
	//parametri
	int avgD;
	int avgQ;
	private GOsalesDAO dao;
	private double costoUnitario;
	private double prezzoUnitario;
	double threshold;
	
	//variabili in uscita
	private int clientiTot;
	private int clientiSodd;
	private double costoTot;
	private double ricavo;
	
	//stato del mondo
	private int Q;
	
	//coda di eventi
	PriorityQueue<Event> queue;

	public Simulatore(Retailers r, int anno, Products p, int n, int q) {
		super();
		this.r = r;
		this.anno = anno;
		this.p = p;
		N = n;
		Q = q;
		this.avgD = this.dao.getAvgD(r, p, anno);
		this.avgQ = this.dao.getAvgQ(r, p, anno);
		this.costoUnitario = p.getUnit_cost();
		this.prezzoUnitario = p.getUnit_price();
		
		this.threshold = Math.min(0.2+0.1*nConnessi, 0.5);
		
		popolaCoda();
	}
	
	public void popolaCoda() {
		//eventi rifornimento
		for(int i = 1; i<=12; i++) {
			this.queue.add(new Event(EventType.RIFORNIMENTO, LocalDate.of(anno, i, 1)));
		}
		
		//eventi vendida
		LocalDate date = LocalDate.of(anno, 1, 15);
		while(date.isBefore(LocalDate.of(anno, 12, 31))) {
			this.queue.add(new Event(EventType.VENDITA, date));
			date = date.plusDays(avgD);
		}
	}
	
	public void processaEventi() {
		while(this.queue.isEmpty()) {
			Event e = this.queue.poll();
			switch(e.getType()) {
			case RIFORNIMENTO:
				double prob = Math.random();
				if(prob<this.threshold) {
					Q += 0.8*N;
					this.costoTot += this.costoUnitario*0.8*N; 
				}else {
					Q+=N;
					this.costoTot += this.costoUnitario*N;
				}
				break;
			case VENDITA:
				this.clientiTot++;
				if(avgQ>=0.9*Q) {
					this.clientiSodd++;
				}
				if(avgQ>=Q) {
					this.ricavo += this.prezzoUnitario*avgQ;
					Q-=avgQ;
				}else {
					Q = 0;
					this.ricavo += this.prezzoUnitario*Q;
				}
				break;
			default:
				break;
			}
		}
	}

}

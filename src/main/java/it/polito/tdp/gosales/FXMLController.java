package it.polito.tdp.gosales;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.gosales.model.Arco;
import it.polito.tdp.gosales.model.Connessa;
import it.polito.tdp.gosales.model.Model;
import it.polito.tdp.gosales.model.Products;
import it.polito.tdp.gosales.model.Retailers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnAnalizzaComponente;

    @FXML
    private Button btnCreaGrafo;

    @FXML
    private Button btnSimula;

    @FXML
    private ComboBox<Integer> cmbAnno;

    @FXML
    private ComboBox<String> cmbNazione;

    @FXML
    private ComboBox<Products> cmbProdotto;

    @FXML
    private ComboBox<Retailers> cmbRivenditore;

    @FXML
    private TextArea txtArchi;

    @FXML
    private TextField txtN;

    @FXML
    private TextField txtNProdotti;

    @FXML
    private TextField txtQ;

    @FXML
    private TextArea txtResult;

    @FXML
    private TextArea txtVertici;

    @FXML
    void doAnalizzaComponente(ActionEvent event) {
    	int anno = this.cmbAnno.getSelectionModel().getSelectedItem();
    	Retailers r = null;
    	try {
    		r = this.cmbRivenditore.getSelectionModel().getSelectedItem(); 
    		Connessa c = this.model.getCompConn(r);
    		this.txtResult.appendText("La componente connessa di "+r+"ha dimensione "+c.getNumConn()+"\n");
    		this.txtResult.appendText("Il peso totale degli archi della componente connessa è "+c.getPeso()+"\n");
    		
    		this.cmbProdotto.getItems().clear();
    		this.cmbProdotto.getItems().addAll(this.model.getRetailerYear(r,anno));
    		
    		this.cmbProdotto.setDisable(false);
    		this.txtN.setDisable(false);
    		this.txtQ.setDisable(false);
    		this.btnSimula.setDisable(false);
    		this.txtN.clear();
    		this.txtQ.clear();
    		
    	}catch(Exception e ) {
    		this.txtResult.setText("Selezionare un rivenditore");
    	}
    	

    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	int anno = 0;
    	String country;
    	int m = 0;
    	try {
    		anno = this.cmbAnno.getSelectionModel().getSelectedItem();
    		country = this.cmbNazione.getSelectionModel().getSelectedItem();
    		m = Integer.parseInt(this.txtNProdotti.getText());
    		
    		if(country==null) {
    			this.txtResult.setText("Selezionare un paese valido");
    			return;
    		}
    		
    		if(m ==0) {
    			this.txtResult.setText("Inserire un numero");
    			return;
    		}
    		
    		this.model.creaGrafo(country, anno, m);
    		
    		this.cmbRivenditore.setDisable(false);
    		this.cmbRivenditore.getItems().addAll(this.model.getVertici());
    		this.btnAnalizzaComponente.setDisable(false); 
    		
    		this.txtResult.clear();
    		this.txtArchi.clear();
    		this.txtVertici.clear();
    		this.txtResult.setText("Grafo creato\n");
    		this.txtResult.appendText("Con "+this.model.numVer()+" vertici e "+this.model.numEdge()+" archi\n");
    		List<Retailers> lista = this.model.getVertici();
    		for(Retailers r : lista) {
    			this.txtVertici.appendText(r.toString()+"\n");
    		}
    		
    		List<Arco> edges= this.model.getEdge();
    		for(Arco a : edges) {
    			this.txtArchi.appendText(a.toString()+"\n");
    		}
    		
    	}catch (NumberFormatException e) {
    		this.txtResult.setText("Selezionare un anno");
    	}

    }

    @FXML
    void doSimulazione(ActionEvent event) {
    	
    	int q = 0;
    	int n = 0;
    	try {
    		q = Integer.parseInt(this.txtQ.getText());
    		n = Integer.parseInt(this.txtN.getText());
    		
    		if(q<0) {
    			this.txtResult.setText("q deve essere un numero positivo");
    			return;
    		}
    		
    		if(n<0) {
    			this.txtResult.setText("n deve essere un numero positivo");
    			return;
    		}
    		
    	}catch(NumberFormatException e) {
    		
    	}

    }

    @FXML
    void initialize() {
        assert btnAnalizzaComponente != null : "fx:id=\"btnAnalizzaComponente\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbAnno != null : "fx:id=\"cmbAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbNazione != null : "fx:id=\"cmbNazione\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbProdotto != null : "fx:id=\"cmbProdotto\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbRivenditore != null : "fx:id=\"cmbRivenditore\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtArchi != null : "fx:id=\"txtArchi\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtN != null : "fx:id=\"txtN\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtNProdotti != null : "fx:id=\"txtNProdotti\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtQ != null : "fx:id=\"txtQ\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtVertici != null : "fx:id=\"txtVertici\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	this.cmbNazione.getItems().setAll(this.model.getCountry());
    	this.cmbAnno.getItems().addAll(this.model.getYear());
    	
    }

}

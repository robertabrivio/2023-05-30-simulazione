package it.polito.tdp.gosales.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import it.polito.tdp.gosales.model.Arco;
import it.polito.tdp.gosales.model.DailySale;
import it.polito.tdp.gosales.model.Products;
import it.polito.tdp.gosales.model.Retailers;

public class GOsalesDAO {
	
	
	/**
	 * Metodo per leggere la lista di tutti i rivenditori dal database
	 * @return
	 */
	public List<Retailers> getAllRetailers(){
		String query = "SELECT r.* "
				+ "FROM go_retailers r";
		List<Retailers> result = new ArrayList<Retailers>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Retailers(rs.getInt("Retailer_code"), 
						rs.getString("Retailer_name"),
						rs.getString("Type"), 
						rs.getString("Country")));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}

	public List<Retailers> getVertici(String country){
		String query = "SELECT r.* "
				+ "FROM go_retailers r "
				+ "WHERE r.`Country` = ?";
		List<Retailers> result = new ArrayList<Retailers>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, country);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Retailers(rs.getInt("Retailer_code"), 
						rs.getString("Retailer_name"),
						rs.getString("Type"), 
						country));
			}
			conn.close();
			Collections.sort(result);
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
	
	
	/**
	 * Metodo per leggere la lista di tutti i prodotti dal database
	 * @return
	 */
	public List<Products> getAllProducts(){
		String query = "SELECT * from go_products";
		List<Products> result = new ArrayList<Products>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Products(rs.getInt("Product_number"), 
						rs.getString("Product_line"), 
						rs.getString("Product_type"), 
						rs.getString("Product"), 
						rs.getString("Product_brand"), 
						rs.getString("Product_color"),
						rs.getDouble("Unit_cost"), 
						rs.getDouble("Unit_price")));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
		
	
	public int getAvgD(Retailers r,Products p, int anno){
		String query = "SELECT s.`Retailer_code`, 12*30/COUNT(*) as avgd"
				+ "FROM go_daily_sales s "
				+ "WHERE s.`Retailer_code` = ? and s.`Product_number`= ? and year(s.`Date`) = ?";
		int result=0;
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			st.setInt(1, r.getCode());
			st.setInt(2, p.getNumber());
			st.setInt(3, anno);
			ResultSet rs = st.executeQuery();

			if (rs.first()) {
				result = (int) rs.getDouble("avgD");
			}
			
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
	
	public int getAvgQ(Retailers r,Products p, int anno){
		String query = "SELECT s.`Retailer_code`, SUM(s.`Quantity`)/COUNT(*) as avgQ "
				+ "FROM go_daily_sales s "
				+ "WHERE s.`Retailer_code` = ? and s.`Product_number`= ? and year(s.`Date`)= ?";
		int result=0;
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			st.setInt(1, r.getCode());
			st.setInt(2, p.getNumber());
			st.setInt(3, anno);
			ResultSet rs = st.executeQuery();

			if (rs.first()) {
				result = (int) rs.getDouble("avgQ");
			}
			
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
	
	public List<Products> getProductsRetailersYear(Retailers r, int anno){
		String query = "SELECT p.* "
				+ "FROM go_daily_sales s, go_products p "
				+ "WHERE s.`Product_number` = p.`Product_number` and YEAR(s.`Date`) = ?"
				+ " and s.`Retailer_code` = ?";
		List<Products> result = new ArrayList<Products>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			st.setInt(1,anno);
			st.setInt(2, r.getCode());
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Products(rs.getInt("Product_number"), 
						rs.getString("Product_line"), 
						rs.getString("Product_type"), 
						rs.getString("Product"), 
						rs.getString("Product_brand"), 
						rs.getString("Product_color"),
						rs.getDouble("Unit_cost"), 
						rs.getDouble("Unit_price")));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
	
	public List<Arco> getEdge(int anno, String country, int m, Map<Integer, Retailers> idMap){
		String sql = "SELECT s1.`Retailer_code` as ret1, s2.`Retailer_code` as ret2, COUNT(distinct s1.`Product_number`) as totProd "
				+ "FROM go_daily_sales s1 , go_daily_sales s2, go_retailers r1, go_retailers r2 "
				+ "WHERE r1.`Retailer_code` = s1.`Retailer_code` and r2.`Retailer_code` = s2.`Retailer_code` "
				+ "and s1.`Product_number` = s2.`Product_number` "
				+ "and YEAR(s1.`Date`) = ? and YEAR(s1.`Date`) = YEAR(s2.`Date`) "
				+ "and r1.`Country` = ? and r1.`Country` = r2.`Country` "
				+ "and r1.Retailer_code < r2.Retailer_code "
				+ "GROUP BY s1.`Retailer_code`, s2.`Retailer_code` "
				+ "HAVING totProd>=?";
		List<Arco> edges = new ArrayList<>();
		
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			st.setString(2, country);
			st.setInt(3, m);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				Arco a = new Arco(idMap.get(rs.getInt("ret1")), idMap.get(rs.getInt("ret2")), rs.getInt("totProd"));
				edges.add(a);
			}
			conn.close();
			return edges; 
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	
	/**
	 * Metodo per leggere la lista di tutte le vendite nel database
	 * @return
	 */
	public List<DailySale> getAllSales(){
		String query = "SELECT * from go_daily_sales";
		List<DailySale> result = new ArrayList<DailySale>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new DailySale(rs.getInt("retailer_code"),
				rs.getInt("product_number"),
				rs.getInt("order_method_code"),
				rs.getTimestamp("date").toLocalDateTime().toLocalDate(),
				rs.getInt("quantity"),
				rs.getDouble("unit_price"),
				rs.getDouble("unit_sale_price")  ));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<String> getCountry(){
		String sql = "SELECT distinct r.`Country` "
				+ "FROM go_retailers r";
		List<String> country = new ArrayList<>();
		
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				country.add(rs.getString("Country"));
			}
			
			conn.close();
			return country;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
	
	public List<Integer> getYear(){
		String sql = "SELECT distinct YEAR(s.`Date`) as year "
				+ "FROM go_daily_sales s";
		List<Integer> year = new ArrayList<>();
		
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				year.add(rs.getInt("year"));
			}
			conn.close();
			return year;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
	
	
}

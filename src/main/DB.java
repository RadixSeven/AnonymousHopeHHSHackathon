package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import data.Facility;
import data.Review;

/**
 * DB helper written in a real hurry. Tedious code.
 * Replace this with some ORM (perhaps jOOQ).
 * @author ravi
 *
 */
public class DB {
	String url = "jdbc:sqlite:C:/users/ravi/Desktop/Test.db";
	Connection conn;
	Statement stmt;

	public DB() {
		try {
			conn = DriverManager.getConnection(url);
			stmt = conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ResultSet query(String sql) throws SQLException {
		stmt.execute(sql);
		return stmt.getResultSet();
	}

	public ArrayList<String> strcoldist(String tbl, String col) throws SQLException {
		ArrayList<String> res = new ArrayList<String>();
		ResultSet rs = query(String.format("SELECT DISTINCT %s FROM %s", col, tbl));
		while (rs.next())
			res.add(rs.getString(1));
		return res;
	}

	public ArrayList<String> strcol(String tbl, String col) throws SQLException {
		ArrayList<String> res = new ArrayList<String>();
		ResultSet rs = query(String.format("SELECT %s FROM %s", col, tbl));
		while (rs.next())
			res.add(rs.getString(1));
		return res;
	}
	
	public ArrayList<Facility> getFacilityByZip(String zip) throws SQLException {
		String sql = String.format("SELECT id, name1, name2, street1, street2, zip from Facility where zip = '%s'", zip);
		ArrayList<Facility> facilities = new ArrayList<>();
		ResultSet rs = query(sql);
		while(rs.next()) {
			Facility facility = new Facility();
			facility.id = rs.getInt("id");
			facility.name1 = rs.getString("name1");
			facility.name2 = rs.getString("name2");
			facility.street1 = rs.getString("street1");
			facility.street2 = rs.getString("street2");
			facility.zip = rs.getString("zip");
			facilities.add(facility);
		}
		return facilities;
	}
	
	// add facility (later)
	
	
	// add review (now)
	public void addReview(Review review, int facilityID) throws SQLException {
		// TODO - Fix SQL
		String sql = String.format("INSERT INTO Review where FacilityID = %d INTO ", facilityID);
		stmt.execute(sql);
	}
	
	// reviews per facility (now)
	public ArrayList<Review> reviewsByFacilityID(int facilityID) {
		ArrayList<Review> reviews = new ArrayList<Review>();
		// TODO - Fix SQL and add code;
		return reviews;
	}
	
}

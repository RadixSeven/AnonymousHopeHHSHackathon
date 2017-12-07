package main;

import java.security.interfaces.RSAKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

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
	
    public String toSql(Boolean b) {
    	return b == null ? "Null" : (b ? "True" : "False");
    }
    
    public String toSql(Integer i) {
    	return i == null ? "Null" : i.toString();
    }

    public String toSql(Long l) {
    	return l == null ? "Null" : l.toString();
    }
    
    public String toSql(String s) {
    	return s == null ? "Null" : String.format("'%s'", s);
    }
    
    
    public final String INSURANCE_ID = "insurance_id";
    public final String REVIEW_ID = "review_id";
    public final String FACILITY_ID = "facility_id";
    public final String RATING = "rating";
    public final String TEXT = "text";
    public final String COST_PER_MONTH = "cost_per_month";
    public final String ROLE= "role";
    public final String LONGEST_TIME_SOBER = "longest_time_sober";
    public final String DRUG_DEALERS = "drug_dealers";
    public final String EMPLOYED_SINCE= "employed_since";
    
    /** Comma-separated list of the fields in the review table */ 
    private String reviewFields() {
    	return String.format("%s, %s, %s, %s, %s, %s, %s, %s," +
			"%s", REVIEW_ID, FACILITY_ID, RATING, TEXT, COST_PER_MONTH,
			ROLE, LONGEST_TIME_SOBER, DRUG_DEALERS, EMPLOYED_SINCE);
    }

	// add review (now)
	public void addReview(Review review) throws SQLException {
		// Still need to insert insurance
		stmt.execute(
			String.format("INSERT INTO Review (%s) VALUES (%d, %d, %s, %s, %s, %s, %s, %s, %s); ",
				reviewFields(), review.reviewId, review.facilityId, toSql(review.rating),
				toSql(review.text), toSql(review.costPerMonth),
				toSql(review.role), toSql(review.longestTimeSober),
				toSql(review.drugDealers), toSql(review.employedSince)
				));
		for(String insurance: review.insurances) {
			stmt.execute(
				String.format("INSERT INTO Review_Insurance (%s, %s) VALUES (%d, %s)",
						REVIEW_ID, INSURANCE_ID, review, insurance));
		}
	}
	
	
	public int reviewId;
	public int facilityId;
	public Integer rating;
	public String text;
	public String[] insurances;
	public Long costPerMonth;
	public String role;
	public String longestTimeSober;
	public Boolean drugDealers;
	public Boolean employedSince;
	
	// reviews per facility (now)
	public ArrayList<Review> reviewsByFacilityId(int facilityId) throws SQLException {
		String sql = String.format("SELECT %s from Facility where facility_id = %d", 
				reviewFields(), facilityId);
		ResultSet rs = query(sql);
		ArrayList<Review> reviews = new ArrayList<Review>();
		while(rs.next()) {
			Review r = new Review();
			r.reviewId = rs.getInt(REVIEW_ID);
			r.facilityId = rs.getInt(FACILITY_ID);
			r.rating = rs.getInt(RATING);
			r.rating = rs.wasNull() ? null : r.rating;
			r.text = rs.getString(TEXT);
			r.text = rs.wasNull() ? null : r.text;
			r.costPerMonth = rs.getLong(COST_PER_MONTH);
			r.costPerMonth = rs.wasNull() ? null : r.costPerMonth;
			r.role = rs.getString(ROLE);
			r.role = rs.wasNull() ? null : r.role;
			r.longestTimeSober = rs.getString(LONGEST_TIME_SOBER);
			r.longestTimeSober = rs.wasNull() ? null : r.longestTimeSober;
			r.drugDealers = rs.getBoolean(DRUG_DEALERS);
			r.drugDealers = rs.wasNull() ? null : r.drugDealers;
			r.employedSince = rs.getBoolean(EMPLOYED_SINCE);
			r.employedSince = rs.wasNull() ? null : r.employedSince;
			reviews.add(r);
		}
		
		// Create sql string to query for matching ids (to perform the join in memory)
		Integer[] review_ids = reviews.stream().map(r -> r.reviewId).toArray(size -> new Integer[size]);
		StringBuilder sb = new StringBuilder(String.format(
				"SELECT %s, %s FROM Review_Insurance WHERE %s IN (",
				REVIEW_ID, INSURANCE_ID, REVIEW_ID));
		boolean hasPrevious = false;
		for(int rid : review_ids) {
			if(hasPrevious) {
				sb.append(",");
			}
			sb.append(rid);
			hasPrevious = true;
		}
		sb.append(") ORDER_BY %s;", REVIEW_ID); // I don't use the ordering below but could
		
		// Group the insurance response into lists
		HashMap<Integer, ArrayList<String>> review_insurances = new HashMap<>();
		rs = query(sb.toString());
		while(rs.next()) {
			Integer rid = rs.getInt(REVIEW_ID);
			if(! review_insurances.containsKey(rid)) {
				review_insurances.put(rid, new ArrayList<String>());
			}
			review_insurances.get(rid).add(rs.getString(INSURANCE_ID));
		}
		
		// Merge the insurances into the review objects
		for(Review r : reviews) {
			r.insurances = (String[]) review_insurances.get(r.reviewId).toArray();
		}
		
		return reviews;
	}
}

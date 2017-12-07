package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import org.eclipse.jdt.annotation.NonNullByDefault;
import data.Facility;
import data.Review;

/**
 * DB helper written in a real hurry. Tedious code.
 * Replace this with some ORM (perhaps jOOQ).
 * @author ravi, eric
 *
 */
@NonNullByDefault
public class DB {
	Connection conn;
	
	static DB db;
	
	public Statement stmt;

	public DB() {
		try {
			String url = System.getenv().get("AH_DB_PATH");
			//System.out.println(url);
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(url);
			stmt = conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static DB getInstance() {
		if (db == null) {
			db = new DB();
		}
		return db;
	}

	public ResultSet query(String sql) throws SQLException {
		db.stmt.execute(sql);
		return stmt.getResultSet();
	}

	public ArrayList<String> strcoldist(String tbl, String col) throws SQLException {
		ArrayList<String> res = new ArrayList<String>();
		ResultSet rs = query(String.format("SELECT DISTINCT %s FROM %s", col, tbl));
		while (rs.next())
			res.add(rs.getString(1));
		return res;
	}
	

	public String[] allzips() throws SQLException  {
		ArrayList<String> zips = strcoldist("Facility", "zip");
		return zips.toArray(new String[zips.size()]);
	}
	
	public ArrayList<String> strcol(String tbl, String col) throws SQLException {
		ArrayList<String> res = new ArrayList<String>();
		ResultSet rs = query(String.format("SELECT %s FROM %s", col, tbl));
		while (rs.next())
			res.add(rs.getString(1));
		return res;
	}
	
	final String F_ID="Facility.id";
	final String F_NAME1="Facility.name1";
	final String F_NAME2="Facility.name2";
	final String F_STREET1="Facility.street1";
	final String F_STREET2="Facility.street2";
	final String F_ZIP="Facility.zip";
    final String RI_REVIEW_ID = "Review_Insurance.review_id";
    final String RI_INSURANCE_ID = "Review_Insurance.insurance_id";
    final String R_REVIEW_ID = "Review.review_id";
    final String R_FACILITY_ID = "Review.facility_id";
    final String R_RATING = "Review.rating";
    final String R_TEXT = "Review.text";
    final String R_COST_PER_MONTH = "Review.cost_per_month";
    final String R_ROLE= "Review.role";
    final String R_LONGEST_TIME_SOBER = "Review.longest_time_sober";
    final String R_DRUG_DEALERS = "Review.drug_dealers";
    final String R_EMPLOYED_SINCE= "Review.employed_since";

    public Facility[] getFacilityByZip(String zip) throws SQLException {
		
		String sql = String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s from Facility " +
				"LEFT JOIN Review ON %s = %s LEFT JOIN Review_Insurance ON %s = %s " + 
				"WHERE %s = '%s' ORDER BY %s, %s",
				// Fields from Facility
				F_ID, F_NAME1, F_NAME2, F_STREET1, F_STREET2, F_ZIP,
				// Fields from Review
				R_REVIEW_ID, R_RATING, R_TEXT, R_COST_PER_MONTH, R_ROLE, R_LONGEST_TIME_SOBER, 
				R_DRUG_DEALERS, R_EMPLOYED_SINCE,
				// Fields from Review_Insurance
				RI_INSURANCE_ID,
				// Join conditions
				F_ID, R_FACILITY_ID,
				R_REVIEW_ID, RI_REVIEW_ID,
				F_ZIP, zip,
				// Order by
				F_ID, R_REVIEW_ID
				);
		//System.out.println(sql);
		ArrayList<Facility> facilities = new ArrayList<>();
		Facility cur_f = null;
		Review cur_r = null;
		
		ResultSet rs = query(sql);
		while(rs.next()) {
			int f_id = rs.getInt("id");
			if(cur_f == null || cur_f.id != f_id) {
				cur_f = new Facility();
				cur_f.id = f_id;
				cur_f.reviews = new ArrayList<>();
				cur_r = null;
				facilities.add(cur_f);
			}
			cur_f.id = rs.getInt("id");
			cur_f.name1 = rs.getString("name1");
			cur_f.name2 = rs.getString("name2");
			cur_f.street1 = rs.getString("street1");
			cur_f.street2 = rs.getString("street2");
			cur_f.zip = rs.getString("zip");

			Integer r_id = rs.getInt("review_id");
			r_id = rs.wasNull() ? null : r_id;
			if(r_id != null) {
				if(cur_r == null || cur_r.reviewId != r_id) {
					cur_r = new Review();
					cur_r.reviewId = r_id;
					cur_r.insurances = new ArrayList<>();
					cur_r.facility = cur_f;
					cur_f.reviews.add(cur_r);
				}

				cur_r.rating = rs.getInt("rating");
				cur_r.text = rs.getString("text");
				cur_r.text = rs.wasNull() ? null : cur_r.text;
				cur_r.costPerMonth = rs.getDouble("cost_per_month");
				cur_r.costPerMonth = rs.wasNull() ? null : cur_r.costPerMonth;
				cur_r.role = rs.getString("role");
				cur_r.role = rs.wasNull() ? null : cur_r.role;
				cur_r.longestTimeSober = rs.getString("longest_time_sober");
				cur_r.longestTimeSober = rs.wasNull() ? null : cur_r.longestTimeSober;
				cur_r.drugDealers = rs.getBoolean("drug_dealers");
				cur_r.drugDealers = rs.wasNull() ? null : cur_r.drugDealers;
				cur_r.employedSince = rs.getBoolean("employed_since");
				cur_r.employedSince = rs.wasNull() ? null : cur_r.employedSince;
				
				String insurance = rs.getString("insurance_id");
				insurance = rs.wasNull() ? null : insurance;
				if(insurance != null) {
					cur_r.insurances.add(insurance);
				}
			}
		}
		return facilities.toArray(new Facility[facilities.size()]);
	}
	
	// add facility (later)
	
    public String toSql(Boolean b) {
    	return b == null ? "Null" : (b ? "True" : "False");
    }
    
    public String toSql(Double b) {
    	return b == null ? "Null" : b.toString();
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
    
    
    /** Comma-separated list of the fields in the review table ... assumes table called "Review" */ 
    private String reviewFields() {
    	return String.format("%s, %s, %s, %s, %s, %s, %s, %s," +
			"%s", R_REVIEW_ID, R_FACILITY_ID, R_RATING, R_TEXT, R_COST_PER_MONTH,
			R_ROLE, R_LONGEST_TIME_SOBER, R_DRUG_DEALERS, R_EMPLOYED_SINCE);
    }

	// add review (now)
	public void addReview(Review review) throws SQLException {
		// Still need to insert insurance
		db.stmt.execute(
			String.format("INSERT INTO Review (%s) VALUES (%d, %d, %s, %s, %s, %s, %s, %s, %s); ",
				reviewFields(), review.reviewId, review.facility.id, toSql(review.rating),
				toSql(review.text), toSql(review.costPerMonth),
				toSql(review.role), toSql(review.longestTimeSober),
				toSql(review.drugDealers), toSql(review.employedSince)
				));
		for(String insurance: review.insurances) {
			db.stmt.execute(
				String.format("INSERT INTO Review_Insurance (%s, %s) VALUES (%d, %s)",
						RI_REVIEW_ID, RI_INSURANCE_ID, review, insurance));
		}
	}
	
	public Facility[] dummyFacilities() {
		ArrayList<Facility> facilities = new ArrayList<Facility>();
		Facility facility = new Facility();
		facility.id = 1;
		facility.name1 = "222";
		facility.zip = "12345";
		facilities.add(facility);
		return facilities.toArray(new Facility[facilities.size()]);
	}
}

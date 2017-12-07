package main;

import java.sql.SQLException;

public class Main {

	public static void test() throws SQLException {
		DB db = new DB();
		String[] zips = main.DB.getInstance().allzips();
		System.out.println(zips.toString());
	}
	
	public static void main(String[] args) throws SQLException {
		test();
		//System.out.println( System.getenv().get("AH_DB_PATH"));
	}

}

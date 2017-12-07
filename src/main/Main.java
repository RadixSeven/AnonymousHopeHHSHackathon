package main;

import java.sql.SQLException;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) throws SQLException {
		DB db = new DB();
		ArrayList<String> zips = db.strcoldist("Facility", "zip");
		System.out.println(zips.toString());
	}

}

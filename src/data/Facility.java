package data;

import java.util.ArrayList;

import org.eclipse.jdt.annotation.NonNull;

public class Facility {
	public int id;
	@NonNull public String name1;
	@NonNull public String name2;
	@NonNull public String street1;
	@NonNull public String street2;
	@NonNull public String zip;
	@NonNull public ArrayList<Review> reviews;
}

package data;

import java.util.ArrayList;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * A review of a facility
 * All the nullable fields might not be entered
 * @author Eric
 *
 */
public class Review {
	public int reviewId;
	/** Facility being reviewed */
	@NonNull public Facility facility;
	/** Star Rating - must be clicked to submit any review*/
	@NonNull public Integer rating;
	/** Text review */
	@Nullable public String text;
	/** Insurances specifically listed in the review ... Note that not listing an insurance does not mean it was not received... */
	@NonNull public ArrayList<String> insurances;
	/** Cost per month of the treatment received by the patient in dollars per month */
	@Nullable public Double costPerMonth;
	/** The role of the person giving the rating */
	@Nullable public String role;
	/** Enumerated type for longest time sober */
	@Nullable public String longestTimeSober;
	/** True if drug dealers near entrance, false if not. Null if question not answered. */
	@Nullable public Boolean drugDealers;
	/** True if the patient has been employed since the treatment */
	@Nullable public Boolean employedSince;
}

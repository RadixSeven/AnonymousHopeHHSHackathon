package controllers;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Vbox;

import data.Facility;
import data.Review;

public class FacilityListener implements EventListener {
	
	Label name;
	Grid gridReviews;
	Facility facility;
	Rows rowsReviews;
	
	public FacilityListener(Label name, Grid gridReviews, Rows rowsReviews, Facility facility) {
		this.name = name;
		this.facility = facility;
		this.gridReviews = gridReviews;
		this.rowsReviews = rowsReviews;
	}
	
    public void onEvent(Event event) {
        name.setValue(facility.name1);
        rowsReviews.getChildren().clear();
        for(Review review: facility.reviews) {
        	Row row = new Row();
        	Vbox vb1 = new Vbox();
        	// 59860
        	// controllers.ZKHelper.ratingWidget(review.rating)
        	addReviewField(vb1, "Rating", controllers.ZKHelper.ratingWidget(review.rating));
        	addReviewField(vb1, "Role", review.role);
        	addReviewField(vb1, "Text", review.text);
        	addReviewField(vb1, "Cost per Month", review.costPerMonth.toString());
        	addReviewField(vb1, "Longest Time Sober", review.longestTimeSober);
        	row.appendChild(vb1);
        	row.setParent(rowsReviews);
        	rowsReviews.appendChild(row);
        }
    }
    
    public Hbox addReviewField(Vbox vb, String name, Hbox w) {
    	Hbox hb = new Hbox();
    	
    	Label lbl = new Label();
    	lbl.setValue(name);
    	hb.appendChild(lbl);
    	
    	hb.appendChild(w);
    	vb.appendChild(hb);
    	return hb;
    }
    
    public Hbox addReviewField(Vbox vb, String name, String value) {
    	Hbox hb = new Hbox();
    	
    	Label lbl = new Label();
    	lbl.setValue(name);
    	hb.appendChild(lbl);
    	
    	Label data = new Label();
    	data.setValue(value);
    	hb.appendChild(data);
    	vb.appendChild(hb);
    	return hb;
    }
}
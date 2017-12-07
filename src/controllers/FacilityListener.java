package controllers;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Label;

import data.Facility;

public class FacilityListener implements EventListener {
	
	Label name;
	Facility facility;
	
	public FacilityListener(Label name, Facility facility) {
		this.name = name;
		this.facility = facility;
	}
	
    public void onEvent(Event event) {
        name.setValue(facility.name1);
    }
}
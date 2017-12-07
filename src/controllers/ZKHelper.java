package controllers;

import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;

public class ZKHelper {
	public static Hbox ratingWidget(int i) {
		Hbox hb = new Hbox();
		for(int c = 0; c < i; c++) {
			Image image = new Image("img/star.png");
			image.setWidth("32px");
			image.setHeight("32px");
			hb.appendChild(image);
		}
		hb.setWidth("300px");
		return hb;
	}
}

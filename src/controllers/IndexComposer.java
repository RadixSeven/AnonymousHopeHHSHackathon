package controllers;

import org.zkoss.gmaps.Gmaps;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class IndexComposer extends SelectorComposer<Window>  {
    
	@WireVariable
    private Gmaps map;
	
	@Listen("onClick = #cmdClick")
	public void click() {
		map.setZoom(10);
	}
}

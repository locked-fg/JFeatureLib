package de.lmu.dbs.jfeaturelib.features.surf;


import ij.gui.GenericDialog;
import ij.plugin.PlugIn;


public class About_ implements PlugIn {
	String title = "SURF: About";
		
	@Override
	public void run(String arg) {
		
		String text = Params.programVersion + "\n"
			+ "(c) 2009 Eugen Labun <Labun@gmx.net>\n"
			+ "www.labun.com/imagej-surf\n" // TODO create this website with info, downloads, etc. !
			+ " \n"
			+ "Based on OpenSURF (Rev.45) by Christopher Evans\n"
			+ "and OpenCV SURF v2.0.0 by Liu Liu, Ian Mahon et al.";

		GenericDialog gd = new GenericDialog( title );
		gd.addMessage(text);
		gd.showDialog();

	}	

}

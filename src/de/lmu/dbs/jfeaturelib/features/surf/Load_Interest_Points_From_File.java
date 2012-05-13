package de.lmu.dbs.jfeaturelib.features.surf;


import ij.IJ;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;
import java.util.List;




// Designed according to the "PlugIn Design Guidelines" 
// http://pacific.mpi-cbg.de/wiki/index.php/PlugIn_Design_Guidelines

public class Load_Interest_Points_From_File implements PlugIn {
	
	@Override
	public void run(String arg) {

		OpenDialog od = new OpenDialog("SURF: TEST Load Interest Points From File", arg);
		String dir = od.getDirectory();
		String fileName = od.getFileName();
		if (fileName == null)
			return;
		String fullName = dir + fileName;
		List<InterestPoint> ipts = InterestPoint.loadFromFile(fullName);
		IJ.showStatus("SURF: Loading Interest Points from File " + fullName);
		IJFacade.setLastResult(ipts);
		
	}	

}

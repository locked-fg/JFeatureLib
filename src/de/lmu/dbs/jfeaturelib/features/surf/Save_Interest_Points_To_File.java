package de.lmu.dbs.jfeaturelib.features.surf;


import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.io.SaveDialog;
import ij.plugin.PlugIn;
import java.util.List;




// Designed according to the "PlugIn Design Guidelines" 
// http://pacific.mpi-cbg.de/wiki/index.php/PlugIn_Design_Guidelines

public class Save_Interest_Points_To_File implements PlugIn {
	String title = "SURF: Save Interest Points To File";

	@Override
	public void run(String arg) {
		
		List<InterestPoint> ipts = IJFacade.getLastResult();
		if (ipts == null) {
			IJ.error(title, "No cached result.\nRun 'Find Interest Points' first.");
			return;
		}
		
		String proposedName = "surf";
		
		ImagePlus img = WindowManager.getCurrentImage();
		if (img != null) {
			proposedName = img.getTitle().split(":")[0] + "__" + proposedName;
		}
		
		SaveDialog sd = new SaveDialog(title, proposedName, ".txt");
		String dir = sd.getDirectory();
		String fileName = sd.getFileName();
		if (fileName == null) 
			return;
		String fullName = dir + fileName;
		boolean inclDescriptor = true; // TODO make true by default + dialog!
		IJ.showStatus("SURF: Saving Interest Points to File " + fullName);
		
		InterestPoint.saveToFile(ipts, fullName, inclDescriptor);
		
	}	

}

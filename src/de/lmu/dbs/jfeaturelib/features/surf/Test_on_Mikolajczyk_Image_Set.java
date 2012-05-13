package de.lmu.dbs.jfeaturelib.features.surf;


import ij.IJ;
import ij.ImagePlus;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;




// Designed according to the "PlugIn Design Guidelines" 
// http://pacific.mpi-cbg.de/wiki/index.php/PlugIn_Design_Guidelines

public class Test_on_Mikolajczyk_Image_Set implements PlugIn {
	
	@Override
	public void run(String arg) {
		
//		The choosen directory must contain following files:
//		H1to2p, H1to3p, H1to4p, H1to5p, H1to6p;
//		img1.ppm, img2.ppm, img3.ppm, img4.ppm, img5.ppm, img6.ppm
//		placed into the following subdirectories:

		String[] sets = {"bark", "bikes", "boat", "graf", "leuven", "trees", "ubc", "wall"};
//		String[] sets = {"graf"}; // debug
		
		
		// TODO add message window explaining this operation with a link to the Mikolajczyk' site
		// -> http://www.robots.ox.ac.uk/~vgg/research/affine/
		// -> http://www.robots.ox.ac.uk/~vgg/data/data-aff.html
		
			OpenDialog od = new OpenDialog("SURF: Choose the directory containing subdirectories with Mikolajczyk's test images", null, "filename will be ignored");
			String dir = od.getDirectory();
			if (dir == null) // cancel pressed
				return;
			
			String curDir;
			ImagePlus imp1, imp2;
			IntegralImage intImg1, intImg2;
			List<InterestPoint> ipts1, ipts2;
			Map<InterestPoint, InterestPoint> matchedPoints;
			int margin;
			int count1, count2;
			float[][] h;
			
			Params params = new Params(); // Params.getSurfParamsFromDialog(gd);
			boolean doReverseComparisonToo = true; // TODO: use params from the dialog

			/** Allowed deviation (in pixels, plus/minus) from the coordinates computed with the homography matrix. */
			float tolerance = 1.5f; // TODO: use params from the dialog
			
			IJ.setColumnHeadings("Matched Points\tMatched Points (also matched by Homography)");

			for (String set : sets) {
				curDir = dir + set + File.separator;
				imp1 = new ImagePlus(getImageFileName(curDir, '1'));
				intImg1 = new IntegralImage(imp1.getProcessor(), true);
				ipts1 = IJFacade.detectAndDescribeInterestPoints(intImg1, params);

				IJ.write(set);

				for (char i = '2'; i <= '6'; i++ ) {
//				for (char i = '2'; i <= '3'; i++ ) { // debug
					imp2 = new ImagePlus(getImageFileName(curDir, i));
					intImg2 = new IntegralImage(imp2.getProcessor(), true);
					ipts2 = IJFacade.detectAndDescribeInterestPoints(intImg2, params);
					matchedPoints = Matcher.findMathes(ipts1, ipts2, doReverseComparisonToo);
					count1 = matchedPoints.size();

					h = loadHomographyMatrixFromFile(curDir + "H1to" + i + "p");
					margin = params.getMaxFilterSize(0) / 2 + 1; // <-- depends on margin in Detector. TODO: add additional margin? 
					count2 = Matcher.countMatchesUsingHomography(matchedPoints, imp1, margin, h, imp2, tolerance);
					
					IJ.write(count1 + "\t" + count2); 

				}
			}

	
			
	}

	private String getImageFileName(String curDir, char i) {
		String fileName = curDir + "img" + i + ".ppm";
		if (! new File(fileName).exists())
			fileName = curDir + "img" + i + ".pgm";
		return fileName;
	}

	float[][] loadHomographyMatrixFromFile(String fileName) {
		float[][] res = new float[3][3];
		try {
			Scanner in = new Scanner(new File(fileName));
			for (int i=0; i<3; i++)
				for (int j=0; j<3; j++)
					res[i][j] = in.nextFloat();
			in.close();
		} catch (FileNotFoundException e) {
			IJ.error("SURF: loadHomographyFromFile", e.getMessage());
			res = null;
		}
		return res;

	}

	float[][] loadHomographyMatrixFromFile() {

		OpenDialog od = new OpenDialog("SURF: Choose the file containing 3x3 Homography Matrix", null);
		String dir = od.getDirectory();
		String fileName = od.getFileName();
		if (fileName == null)
			return null;
		String fullName = dir + fileName;

		return loadHomographyMatrixFromFile(fullName);
	}
	

}

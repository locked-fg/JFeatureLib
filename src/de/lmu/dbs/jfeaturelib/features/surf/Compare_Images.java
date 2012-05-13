package de.lmu.dbs.jfeaturelib.features.surf;


import de.lmu.dbs.jfeaturelib.features.surf.Matcher.Point2Df;
import ij.IJ;
import static ij.IJ.d2s;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map.Entry;
import java.util.*;




// Designed according to the "PlugIn Design Guidelines" 
// http://pacific.mpi-cbg.de/wiki/index.php/PlugIn_Design_Guidelines

public class Compare_Images implements PlugIn {
	String title = "SURF: Compare Images";

	@Override
	public void run(String arg) {
		
		// Get IDs of opened images.
		int[] imageIDs = WindowManager.getIDList();
		if (imageIDs == null || imageIDs.length < 2) {
			IJ.error(title, "You need at least two images, e.g. :\n1) object to find,\n2) image where to search for.\n"+
					"Open these images in ImageJ.");
			return;
		}
		
		// Prepare list of opened images.
		String[] images = new String[imageIDs.length];
		for (int i = 0; i < imageIDs.length; i++) 
			images[i] = WindowManager.getImage(imageIDs[i]).getTitle();

		final GenericDialog gd = new GenericDialog( "Parameter" + " (" + title + ")" );
		gd.addChoice("Image 1 (e.g. object to find):", images, images[0]);
		gd.addChoice("Image 2 (e.g. image where to search for):", images, images[1]);
		// prevents, that one IP is matched to many other IPs:
		gd.addCheckbox("Perform reverse comparison too (gives more accurate results)", true);
		// ^ (even better should be the using of extended (128-dim.) descriptor)
		gd.addCheckbox("Use homography matrix to check correctness (Open File dialog will be shown)", false);
		
		Params.addSurfParamsToDialog(gd);
		gd.showDialog();
		
		if (gd.wasCanceled())
			return;
		
		//////////////////////////////////////////////////////////////////////////
		
		ImagePlus image1 = WindowManager.getImage(gd.getNextChoice());
		ImagePlus image2 = WindowManager.getImage(gd.getNextChoice());

		boolean doReverseComparisonToo = gd.getNextBoolean();

		boolean useHomography = gd.getNextBoolean();
		float[][] homography = null;
		if (useHomography) {
			homography = loadHomographyMatrixFromFile();
			if (homography==null) return;
		}
		
		Params p1 = Params.getSurfParamsFromDialog(gd);
		Params p2 = new Params(p1);
		long begin, end;
		
		p1.getStatistics().startTime = new Date();
		p1.getStatistics().imageTitle = image1.getTitle();
		begin = System.currentTimeMillis();
		IntegralImage intImg1 = new IntegralImage(image1.getProcessor(), true);
		end = System.currentTimeMillis();
		p1.getStatistics().timeIntegralImage = end - begin;
		List<InterestPoint> ipts1 = IJFacade.detectAndDescribeInterestPoints(intImg1, p1);

		p2.getStatistics().startTime = new Date();
		p2.getStatistics().imageTitle = image2.getTitle();
		begin = System.currentTimeMillis();
		IntegralImage intImg2 = new IntegralImage(image2.getProcessor(), true);
		end = System.currentTimeMillis();
		p2.getStatistics().timeIntegralImage = end - begin;
		List<InterestPoint> ipts2 = IJFacade.detectAndDescribeInterestPoints(intImg2, p2);
		
		begin = System.currentTimeMillis();
		Map<InterestPoint, InterestPoint> matchedPoints = Matcher.findMathes(ipts1, ipts2);
		if (doReverseComparisonToo) {
			Map<InterestPoint, InterestPoint> matchedPointsReverse = Matcher.findMathes(ipts2, ipts1);
			matchedPoints = intersection(matchedPoints, matchedPointsReverse);
		}
		end = System.currentTimeMillis();
		long timeMatcher = end - begin;
		
		if (matchedPoints.size() == 0) {
			IJ.showMessage(title, "No matches found.");
			return;
		}
		
		
		// Draw matched interest points:
		
		// 1) prepare a copy of image1
		ImageProcessor image1ProcessorCopy = image1.getProcessor().duplicate().convertToRGB();
		String image1NewTitle = String.format("%s: %d of %d Interest Points", 
				image1.getTitle().split(":")[0], matchedPoints.size(), ipts1.size());
		ImagePlus image1Copy = new ImagePlus(image1NewTitle, image1ProcessorCopy);
		
		// 2) prepare a copy of image2
		ImageProcessor imageProcessor2Copy = image2.getProcessor().duplicate().convertToRGB();
		String image2NewTitle = String.format("%s: %d of %d Interest Points", 
				image2.getTitle().split(":")[0], matchedPoints.size(), ipts2.size());
		ImagePlus image2Copy = new ImagePlus(image2NewTitle, imageProcessor2Copy);
		
		for (Entry<InterestPoint, InterestPoint> pair : matchedPoints.entrySet()) {
			IJFacade.drawSingleInterestPoint(image1ProcessorCopy, p1, pair.getKey());
			IJFacade.drawSingleInterestPoint(imageProcessor2Copy, p2, pair.getValue());
		}
		
		image1Copy.show();
		image2Copy.show();
		
		
		if (p1.isDisplayStatistics() || p2.isDisplayStatistics()) {
			IJFacade.initializeStatisticsWindow();
			if (p1.isDisplayStatistics())
				IJFacade.displayStatistics(p1);
			if (p2.isDisplayStatistics())
				IJFacade.displayStatistics(p2);
			IJ.write("");
			IJ.write("Matcher:\t"+timeMatcher);
			
		}

		if (useHomography) {
			if (!IJ.isResultsWindow())
				IJFacade.initializeStatisticsWindow();
			IJ.write("");
			IJ.write("Check Matches With Homography");
			IJ.write("IPoint-1 \t\t" + "IPoint-2 (Homography) \t\t" + "IPoint-2 (Matcher) \t\t" + "Deviation from Homography\t");
			IJ.write("X \t Y \t"     + "X \t Y \t"                  + "X \t Y \t"            + "X \t Y");

			InterestPoint ip1, ip2;
			Point2Df ip2H;
			int i = 0;
			for (Entry<InterestPoint, InterestPoint> pair : matchedPoints.entrySet()) {
				ip1 = pair.getKey();
				ip2 = pair.getValue();
				ip2H = Matcher.getTargetPointByHomography(new Point2Df(ip1.x, ip1.y), homography);
				IJ.write(d2s(ip1.x) + "\t" + d2s(ip1.y) + "\t" + d2s(ip2H.x) + "\t" + d2s(ip2H.y) + "\t" 
						+ d2s(ip2.x) + "\t" + d2s(ip2.y) + "\t" + d2s(ip2.x-ip2H.x) + "\t" + d2s(ip2.y-ip2H.y));
				i++;
			}

		}
			
	}

	/** Return a new Map contaning only those entries from map1 that also contain (as reversed key/value paar) in map2. */
	Map<InterestPoint, InterestPoint> intersection(Map<InterestPoint, InterestPoint> map1, Map<InterestPoint, InterestPoint> map2) {
		// take only those points that matched in the reverse comparison too
		Map<InterestPoint, InterestPoint> result = new HashMap<InterestPoint, InterestPoint>();
		for (InterestPoint ipt1 : map1.keySet()) {
			InterestPoint ipt2 = map1.get(ipt1);
			if (ipt1 == map2.get(ipt2))
				result.put(ipt1, ipt2);
		}
		return result;
	}

	float[][] loadHomographyMatrixFromFile() {

		OpenDialog od = new OpenDialog("Choose a file containing 3x3 Homography Matrix" + " (" + title + ")", null);
		String dir = od.getDirectory();
		String fileName = od.getFileName();
		if (fileName == null)
			return null;
		String fullName = dir + fileName;
		float[][] res = new float[3][3];
		
		try {
			Scanner in = new Scanner(new File(fullName));
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
	

}

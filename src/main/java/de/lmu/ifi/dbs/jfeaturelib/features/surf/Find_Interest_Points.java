/*
 * This file is part of the JFeatureLib project: http://jfeaturelib.googlecode.com
 * JFeatureLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * JFeatureLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JFeatureLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * You are kindly asked to refer to the papers of the according authors which 
 * should be mentioned in the Javadocs of the respective classes as well as the 
 * JFeatureLib project itself.
 * 
 * Hints how to cite the projects can be found at 
 * https://code.google.com/p/jfeaturelib/wiki/Citation
 */
package de.lmu.ifi.dbs.jfeaturelib.features.surf;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import java.util.Date;
import java.util.List;

// Designed according to the "PlugIn Design Guidelines" 
// http://pacific.mpi-cbg.de/wiki/index.php/PlugIn_Design_Guidelines
public class Find_Interest_Points implements PlugIn {

    String title = "SURF: Find Interest Points";

    @Override
    public void run(String arg) {

        // Get IDs of opened images.
        int[] imageIDs = WindowManager.getIDList();
        if (imageIDs == null || imageIDs.length < 1) {
            IJ.error(title, "Please open an image first.");
            return;
        }

        int activeImageID = IJ.getImage().getID();
        // Prepare list of opened images
        int activeImageIndex = 0;
        String[] images = new String[imageIDs.length];
        for (int i = 0; i < imageIDs.length; i++) {
            images[i] = WindowManager.getImage(imageIDs[i]).getTitle();
            if (imageIDs[i] == activeImageID) {
                activeImageIndex = i;
            }
        }

        final GenericDialog gd = new GenericDialog("Parameter" + " (" + title + ")");
        gd.addChoice("Image:", images, images[activeImageIndex]);

        Params.addSurfParamsToDialog(gd);
        gd.showDialog();

        if (gd.wasCanceled()) {
            return;
        }

        //////////////////////////////////////////////////////////////////

        ImagePlus activeImage = WindowManager.getImage(gd.getNextChoice());
        Params p = Params.getSurfParamsFromDialog(gd);

        p.getStatistics().startTime = new Date();
        p.getStatistics().imageTitle = activeImage.getTitle();

        long begin, end;

        begin = System.currentTimeMillis();
        IntegralImage intImg = new IntegralImage(activeImage.getProcessor(), true);
        end = System.currentTimeMillis();
        p.getStatistics().timeIntegralImage = end - begin;

        List<InterestPoint> ipts = IJFacade.detectAndDescribeInterestPoints(intImg, p);

        if (ipts.size() == 0) {
            IJ.showMessage(title, "No Interest Points found.");
            return;
        }


        // Draw interest points on a copy of the active image.
        ImageProcessor ip2 = activeImage.getProcessor().duplicate().convertToRGB();
        IJFacade.drawInterestPoints(ip2, ipts, p);
        String title = String.format("%s: %d Interest Points",
                activeImage.getTitle().split(":")[0], ipts.size());
        ImagePlus imp2 = new ImagePlus(title, ip2);
        imp2.show();
//		imp2.getCanvas().zoom100Percent();

        if (p.isDisplayStatistics()) {
            IJFacade.initializeStatisticsWindow();
            IJFacade.displayStatistics(p);
        }


    }
}

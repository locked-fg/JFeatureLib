package de.lmu.dbs.jfeaturelib.features.surf;

import ij.ImagePlus;
import static java.lang.Math.round;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

//TODO move IJ-related code into IJFacade
public class Matcher {

    public static class Point2D {

        public int x, y;

        public Point2D() {
        }

        public Point2D(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class Point2Df {

        public float x, y;

        public Point2Df() {
        }

        public Point2Df(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public static Map<InterestPoint, InterestPoint> findMathes(List<InterestPoint> ipts1, List<InterestPoint> ipts2) {
        return findMathes(ipts1, ipts2, 64);
    }

    public static Map<InterestPoint, InterestPoint> findMathes(List<InterestPoint> ipts1, List<InterestPoint> ipts2, boolean doReverseComparisonToo) {
        return findMathes(ipts1, ipts2, 64, doReverseComparisonToo);
    }

    public static Map<InterestPoint, InterestPoint> findMathes(List<InterestPoint> ipts1, List<InterestPoint> ipts2, int size, boolean doReverseComparisonToo) {

        Map<InterestPoint, InterestPoint> matchedPoints = Matcher.findMathes(ipts1, ipts2, size);

        if (doReverseComparisonToo) {
            Map<InterestPoint, InterestPoint> matchedPointsReverse = Matcher.findMathes(ipts2, ipts1, size);

            // take only those points that matched in the reverse comparison too
            Map<InterestPoint, InterestPoint> matchedPointsBoth = new HashMap<InterestPoint, InterestPoint>();
            for (InterestPoint ipt1 : matchedPoints.keySet()) {
                InterestPoint ipt2 = matchedPoints.get(ipt1);
                if (ipt1 == matchedPointsReverse.get(ipt2)) {
                    matchedPointsBoth.put(ipt1, ipt2);
                }
            }
            matchedPoints = matchedPointsBoth;
        }
        return matchedPoints;
    }

    /**
     * Finds matching points using the sign of laplacian and a linear nearest
     * neighbor search.
     */
    public static Map<InterestPoint, InterestPoint> findMathes(List<InterestPoint> ipts1, List<InterestPoint> ipts2, int size) {
        Map<InterestPoint, InterestPoint> res = new HashMap<InterestPoint, InterestPoint>();
        float distance, bestDistance, secondBest;
        InterestPoint bestMatch;
        int descSize = size;
        float delta;
        float[] v1, v2;

        for (InterestPoint p1 : ipts1) {
            bestDistance = secondBest = Float.MAX_VALUE;
            bestMatch = null;

            ipts2Loop:
            for (InterestPoint p2 : ipts2) {

                // (NB: There is no check fo sign of laplacian in OpenSURF)
                if (p1.sign != p2.sign) {
                    continue;
                }

                // Compare descriptors (based on calculating of squared distance between two vectors)
                distance = 0;
                v1 = p1.descriptor;
                v2 = p2.descriptor;
                for (int i = 0; i < descSize; i++) {
                    delta = v1[i] - v2[i];
                    distance += delta * delta;
                    if (distance >= secondBest) {
                        continue ipts2Loop;
                    }
                }
                if (distance < bestDistance) {
                    secondBest = bestDistance;
                    bestDistance = distance;
                    bestMatch = p2;
                } else { // distance < secondBest
                    secondBest = distance;
                }

            }


            // Threshold values in other implementations:
            // OpenSURF:                    0.65
            // OpenCV-2.0.0 (find_obj.cpp): 0.6
            // Orig. SURF:                  0.5
            if (bestDistance < 0.5f * secondBest) {

                // Matching point found.
                res.put(p1, bestMatch);
                // Store the change in position (p1 -> p2) into the
                // matchingPoint:
                bestMatch.dx = bestMatch.x - p1.x;
                bestMatch.dy = bestMatch.y - p1.y;

                // debug
                // System.out.printf("%3d)  %5.1f, %5.1f   (%7.5f)   %5.1f, %5.1f\n",
                // res.size(), p1.x, p1.y, bestDistance, bestMatch.x, bestMatch.y);
                // System.out.println(bestDistance);
            }

        }

        return res;
    }

    /**
     * @param h 3x3 homography matrix.
     */
    public static Point2Df getTargetPointByHomography(Point2Df p1, float[][] h) {
        float p1_z = 1.0f;
        float Z = (h[2][0] * p1.x + h[2][1] * p1.y + h[2][2] * p1_z);
        float X = (h[0][0] * p1.x + h[0][1] * p1.y + h[0][2] * p1_z) / Z;
        float Y = (h[1][0] * p1.x + h[1][1] * p1.y + h[1][2] * p1_z) / Z;
        return new Point2Df(X, Y);
    }

    public static int countMatchesUsingHomography(Map<InterestPoint, InterestPoint> matches, ImagePlus imp1, int margin, float[][] h, ImagePlus imp2, float tolerance) {
        int count = 0;
        float x1, y1, x2, y2, x2H, y2H, z2H, dx, dy;
        for (Entry<InterestPoint, InterestPoint> pair : matches.entrySet()) {
            // Point 1:
            x1 = pair.getKey().x;
            y1 = pair.getKey().y;

            // Point 2:
            x2 = pair.getValue().x;
            y2 = pair.getValue().y;

            // Point 2 according to homography:
            z2H = (h[2][0] * x1 + h[2][1] * y1 + h[2][2]);
            x2H = (h[0][0] * x1 + h[0][1] * y1 + h[0][2]) / z2H;
            y2H = (h[1][0] * x1 + h[1][1] * y1 + h[1][2]) / z2H;

            // if (     (x2H, y2H) within imp2   && ... ? 

            // IJ.write(d2s(x1)+"\t"+d2s(y1)+"\t"+d2s(x2)+"\t"+d2s(y2)+"\t"+d2s(x2H)+"\t"+d2s(y2H)+"\t"+d2s(x2H-x2)+"\t"+d2s(y2H-y2));
            // // TODO: sorted output?
            dx = Math.abs(x2H - x2);
            dy = Math.abs(y2H - y2);

            if (dx <= tolerance && dy <= tolerance) {
                count++;
            }
        }
        return count;
    }

    /**
     * Find homography between matched points and translate src_corners to
     * dst_corners. NOT IMPLEMENTED YET.
     */
    // "finds where the object corners should be in the frame"
    // Can be used to draw a box around the object found in the image (using
    // dst_corners[] as points)
    // TODO: check implementation, parameter and return type!
    // Parameter are defined in OpenSURF:main.cpp as:
    // CvPoint src_corners[4] = {{0,0}, {img->width,0}, {img->width,
    // img->height}, {0, img->height}}; //<-- EL: object to find
    // CvPoint dst_corners[4]; // <--- EL: image where to search for
    static int translateCorners(Map<InterestPoint, InterestPoint> matches, Point2D[] src_corners, Point2D[] dst_corners) {
        // TODO: ^^ rename to findHomography()
        // src_corners, dst_corners: 4 elements in each case //TODO: rewrite as
        // ...(?)
        int n = matches.size();
        if (n < 4) {
            return 0; // TODO: meaning?
        }
        Point2Df[] pt1 = new Point2Df[n]; // == matrix _pt1 (1 row, n
        // cols) // object Points
        Point2Df[] pt2 = new Point2Df[n]; // == matrix _pt2 (1 row, n
        // cols) // image Points

        // Copy Ipoints from match vector into cvPoint vectors
        // EL: purpose? -> cvFindHomography needs 2 vectors of points! (see
        // below)
        // EL: NB, I use pairs<objectPoint, imagePoint> (OpenSURF: vice versa!)

        int i = 0;
        for (Entry<InterestPoint, InterestPoint> pair : matches.entrySet()) {
            pt1[i] = new Point2Df(pair.getKey().x, pair.getKey().y);
            pt2[i] = new Point2Df(pair.getValue().x, pair.getValue().y);
            i++;
        }

        // Find the homography (transformation) between the two sets of points
        final int CV_RANSAC = 8; // TODO: stub
        double[] h = cvFindHomography(pt1, pt2, CV_RANSAC, 5); // homography is
        // 3x3 matrix
        if (h == null) {
            return 0;
        }

        // Translate src_corners to dst_corners using homography
        for (i = 0; i < 4; i++) {
            double x = src_corners[i].x, y = src_corners[i].y;
            double Z = 1. / (h[6] * x + h[7] * y + h[8]);
            double X = (h[0] * x + h[1] * y + h[2]) * Z;
            double Y = (h[3] * x + h[4] * y + h[5]) * Z;
            dst_corners[i] = new Point2D((int) round(X), (int) round(Y));
        }
        return 1;
    }

    /**
     * It's a stub. There is no implementation yet.
     */
    static double[] cvFindHomography(Point2Df[] objectPoints, Point2Df[] imagePoints, int method, double ransacReprojThreshold) {
        double[] homography = null; // new double[9]; // 3x3 matrix
        // TODO: implementation
        return homography;
    }
}

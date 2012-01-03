package de.lmu.dbs.jfeaturelib.pointDetector;

import de.lmu.dbs.jfeaturelib.ImagePoint;
import de.lmu.dbs.jfeaturelib.Progress;
import ij.plugin.filter.Convolver;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

/**
 * Harris Corner Detection
 *
 * @author Mariagrazia Messina - mariagraziamess@libero.it
 * http://svg.dmi.unict.it/iplab/imagej/Plugins/Feature%20Point%20Detectors/Harris/harris.htm
 */
public class Harris implements PointDetector {

    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private List<ImagePoint> resultingCorners;
    // lista che conterr‡  i conrner ad agni iterazione
    private List<int[]> corners;
    //dimensioni di mezza finestra
    private int halfwindow = 1;
    // varianza della gaussiana
    private float gaussiansigma = 0;
    // parametri dii soglia
    private int minDistance = 0;
    private int minMeasure = 0;
    private int piramidi = 0;
    // oggetto utilizzato per il calcolo del gradiente
    private GradientVector gradient = new GradientVector();
    //matrice dei corners
    int matriceCorner[][];

    /**
     * Creates Harris Corner detection with default parameters
     */
    public Harris() {
        this.gaussiansigma = 1.4f;
        this.minMeasure = 10;
        this.minDistance = 80;
        this.piramidi = 1;
    }

    /**
     * Creates Harris Corner Detection
     *
     * @param gaussianSigma Gaussian Variance (Default: 1.4f)
     * @param minDistance Distance Threshold (Default: 10)
     * @param minMeasure Value Threshold (Default: 80)
     * @param iteractions Number of Iteractions (Default: 1)
     */
    public Harris(float gaussianSigma, int minDistance, int minMeasure, int iteractions) {
        this.gaussiansigma = gaussianSigma;
        this.minMeasure = minMeasure;
        this.minDistance = minDistance;
        this.piramidi = iteractions;
    }

    /**
     * Returns the Corners as an ImagePoint List
     *
     * @return ImagePoint List
     */
    @Override
    public List<ImagePoint> getPoints() {
        return resultingCorners;
    }

    /**
     * Defines the capability of the algorithm.
     *
     * @see PlugInFilter
     * @see #supports()
     */
    @Override
    public EnumSet<Supports> supports() {
        EnumSet set = EnumSet.of(
                Supports.NoChanges,
                Supports.DOES_8G);
        return set;
    }

    /**
     * Starts the Harris Corner Detection
     *
     * @param ip ImageProcessor of the source image
     */
    @Override
    public void run(ImageProcessor ip) {
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);

        ByteProcessor bp = Supporto.copyByteProcessor(ip);
        int width = bp.getWidth();
        int height = bp.getHeight();
        int potenza = (int) Math.pow(2, piramidi - 1);
        if ((width / potenza < 8) || (height / potenza < 8)) {
            piramidi = 1;
        }

        ByteProcessor newbp;
        List<int[]> tmp = new ArrayList<>();
        int[] numero = new int[this.piramidi];

        for (int i = 0; i < this.piramidi; i++) {
            corners = new ArrayList<>();
            resultingCorners = new ArrayList<>();
            filter(bp, this.minMeasure, this.minDistance, i);
            for (int[] n : corners) {
                tmp.add(n);
            }
            numero[i] = corners.size();

            bp = Supporto.smussaEsottocampiona(bp, 3, this.gaussiansigma);
        }

        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }

    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Harris Corner Detection
     *
     * @param c immagine
     * @param minMeasure saglio sul valore minimo che assume il corner
     * @param minDistance soglia sulla distanza minima tra 2 corners
     */
    private void filter(ByteProcessor c, int minMeasure, int minDistance, int factor) {

        int width = c.getWidth();
        int height = c.getHeight();

        // scurire l'immagine
        ByteProcessor c2 = new ByteProcessor(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                c2.set(x, y, (int) (c.get(x, y) * 0.80));
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // harris response(-1 se il pixel non Ë un massimo locale)
                int h = (int) spatialMaximaofHarrisMeasure(c, x, y);

                // aggiunge il corner alla lista se supera un valore di soglia
                if (h >= minMeasure) {
                    if (factor != 0) {
                        int XY[] = mappatura(x, y, factor);
                        x = XY[0];
                        y = XY[1];
                    }

                    corners.add(new int[]{x, y, h});
                    resultingCorners.add(new ImagePoint(x, y));

                }
            }
        }

        // si tengono i valori di risposta pi˘ alti
        Iterator<int[]> iter = corners.iterator();
        while (iter.hasNext()) {
            int[] p = iter.next();
            for (int[] n : corners) {
                if (n == p) {
                    continue;
                }
                int dist = (int) Math.sqrt((p[0] - n[0]) * (p[0] - n[0]) + (p[1] - n[1]) * (p[1] - n[1]));
                if (dist > minDistance) {
                    continue;
                }
                if (n[2] < p[2]) {
                    continue;
                }
                iter.remove();
                break;
            }
        }


    }

    /**
     * reatituisce il valore del pixel (x,y) se Ë un massimo, altrimenti
     * restituisce -1
     *
     * @param c immagine
     * @param x coordinata x
     * @param y coordinata y
     * @return la harris response se il pixel Ë un massimo locale, -1 altrimenti
     */
    private double spatialMaximaofHarrisMeasure(ByteProcessor c, int x, int y) {
        int n = 8;
        int[] dx = new int[]{-1, 0, 1, 1, 1, 0, -1, -1};
        int[] dy = new int[]{-1, -1, -1, 0, 1, 1, 1, 0};
        //si calcola il valore di harris response nel punto x,y
        double w = harrisMeasure(c, x, y);
        //per ogni punto dell'intorno di x,y si calcola il valore della harris response
        for (int i = 0; i < n; i++) {
            double wk = harrisMeasure(c, x + dx[i], y + dy[i]);
            //se almeno un valore calcolato in un punto dell'intorno Ë maggiore di quello del punto in questione, esso non
            // Ë un massimo locale e si restituisce -1
            if (wk >= w) {
                return -1;
            }
        }
        //in caso contrario Ë un massimo locale
        return w;
    }

    /**
     * computa harris corner response
     *
     * @param c Image map
     * @param x coordinata x
     * @param y y coordinata y
     * @return harris corner response
     */
    private double harrisMeasure(ByteProcessor c, int x, int y) {
        double m00 = 0, m01 = 0, m10 = 0, m11 = 0;

        // k = det(A) - lambda * trace(A)^2
        // A matrice del secondo momento
        // lambda generalmente Ë tra 0.04 e 0.06. qui Ë stato fissato a 0.06

        for (int dy = -halfwindow; dy <= halfwindow; dy++) {
            for (int dx = -halfwindow; dx <= halfwindow; dx++) {
                int xk = x + dx;
                int yk = y + dy;
                if (xk < 0 || xk >= c.getWidth()) {
                    continue;
                }
                if (yk < 0 || yk >= c.getHeight()) {
                    continue;
                }

                // calcolo del gradiente (derivate prime parziali ) di c nel punto xk,yk
                double[] g = gradient.getVector(c, xk, yk);
                double gx = g[0];
                double gy = g[1];

                // calcolo il peso della finestra gaussiana nel punto dx,dy
                double gw = gaussian(dx, dy, gaussiansigma);

                // creazione degli elementi della matrice
                m00 += gx * gx * gw;
                m01 += gx * gy * gw;
                m10 = m01;
                m11 += gy * gy * gw;
            }
        }

        // harris = det(A) - 0.06*traccia(A)^2;
        //det(A)=m00*m11 - m01*m10
        double det = m00 * m11 - m01 * m10;
        //tr(A)=(m00+m11)*(m00+m11);
        double traccia = (m00 + m11);
        // harris response= det-k tr^2;
        double harris = det - 0.06 * (traccia * traccia);
        return harris / (256 * 256);
    }

    /**
     * Funzione per il computo della Gaussian window
     *
     * @param x coordinata x
     * @param y coordinata y
     * @param sigma2 variannza
     * @return valore della funzione
     */
    private double gaussian(double x, double y, float sigma2) {
        double t = (x * x + y * y) / (2 * sigma2);
        double u = 1.0 / (2 * Math.PI * sigma2);
        double e = u * Math.exp(-t);
        return e;
    }

    /**
     * Funzione che realizza la mappatura dei pixel dell'immagine
     * sottocampionata, nell'immagine originale
     *
     * @param x coordinata x
     * @param y coordinata y
     * @param fact parametro di scala
     * @return coordinate x e y nell'immagine originale
     */
    public int[] mappatura(int x, int y, int fact) {
        int nuoviXY[] = new int[2];
        nuoviXY[0] = x * (2 * fact);
        nuoviXY[1] = y * (2 * fact);
        return nuoviXY;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
}

/**
 * Gradient vector classe che effettua il calcolo del gradiente smussato,
 * effetturando le derivate x e y di una gaussiana
 *
 * @author Messina Mariagrazia
 *
 */
class GradientVector {

    int halfwindow = 1;
    double sigma2 = 1.2;
    double[][] kernelGx = new double[2 * halfwindow + 1][2 * halfwindow + 1];
    double[][] kernelGy = new double[2 * halfwindow + 1][2 * halfwindow + 1];

    /**
     * Metodo costruttore
     *
     */
    public GradientVector() {
        for (int y = -halfwindow; y <= halfwindow; y++) {
            for (int x = -halfwindow; x <= halfwindow; x++) {
                kernelGx[halfwindow + y][halfwindow + x] = Gx(x, y);
                kernelGy[halfwindow + y][halfwindow + x] = Gy(x, y);
            }
        }
    }

    /**
     * Funzione che realizza lo smussamento dell'immagine mediante una gaussiana
     * per poi calcolarne la derivata x (operatore Drog)
     *
     * @param x coordinata x
     * @param y coordinata y
     * @return volere della gaussiana nel punto x,y
     */
    private double Gx(int x, int y) {
        double t = (x * x + y * y) / (2 * sigma2);
        double d2t = -x / sigma2;
        double e = d2t * Math.exp(-t);
        return e;
    }

    /**
     * Funzione che realizza lo smussamento dell'immagine mediante una gaussiana
     * per poi calcolarne la derivata y (operatore Drog)
     *
     * @param x coordinata x
     * @param y coordinata y
     * @return volere della gaussiana nel punto x,y
     */
    private double Gy(int x, int y) {
        double t = (x * x + y * y) / (2 * sigma2);
        double d2t = -y / sigma2;
        double e = d2t * Math.exp(-t);
        return e;
    }

    // restituisce  il vettore del Gradient per il pixel(x,y)
    /**
     * Funzione che inserisce in un vettore il valore del gradiente dei punti
     * appartenenti ad una finestre
     *
     * @param x coordinata x
     * @param y coordinata y
     * @param c immagine
     * @return volere del gradiente x e y in tutti i punti della finestra
     */
    public double[] getVector(ByteProcessor c, int x, int y) {
        double gx = 0, gy = 0;
        for (int dy = -halfwindow; dy <= halfwindow; dy++) {
            for (int dx = -halfwindow; dx <= halfwindow; dx++) {
                int xk = x + dx;
                int yk = y + dy;
                double vk = c.getPixel(xk, yk); // <-- value of the pixel
                gx += kernelGx[halfwindow - dy][halfwindow - dx] * vk;
                gy += kernelGy[halfwindow - dy][halfwindow - dx] * vk;
            }
        }

        double[] gradientVector = new double[]{gx, gy};

        return gradientVector;
    }
}

class Supporto {

    public static ByteProcessor smussaEsottocampiona(ByteProcessor input, int window, float sigma)
            throws IllegalArgumentException {
        ByteProcessor prepocessing = copyByteProcessor(input);
        float gauss[] = initGaussianKernel(window, sigma);
        Convolver convolver = new Convolver();
        ImageProcessor temp = prepocessing.convertToFloat();
        convolver.convolve(temp, gauss, (int) Math.sqrt(gauss.length), (int) Math.sqrt(gauss.length));
        prepocessing = copyByteProcessor(temp);
        int prepocessingWidth = prepocessing.getWidth();
        int prepocessingHeight = prepocessing.getHeight();
        ByteProcessor out = new ByteProcessor(prepocessingWidth / 2, prepocessingHeight / 2);
        if (prepocessingWidth % 2 != 0) {
            prepocessingWidth--;
        }
        if (prepocessingHeight % 2 != 0) {
            prepocessingHeight--;
        }
        for (int i = 0, x = 0; i < prepocessingWidth; i = i + 2) {
            for (int j = 0, y = 0; j < prepocessingHeight; j = j + 2) {
                out.set(x, y, prepocessing.get(i, j));
                y++;
            }
            x++;
        }
        return out;
    }

    public static ColorProcessor cambioColore(ByteProcessor image) {
        Image im = image.createImage();
        return new ColorProcessor(im);
    }

    /**
     * ********************************************* METODI
     * MIEI****************************************************
     */
    /**
     *
     * Metodo che copia un ImageProcessor in un ByteProcessor.
     *
     * @param ip input ImageProcessor.
     * @return ByteProcessor.
     */
    public static ByteProcessor copyByteProcessor(ImageProcessor ip) {
        ByteProcessor bp = new ByteProcessor(ip.getWidth(), ip.getHeight());
        for (int y = 0; y < ip.getHeight(); y++) {
            for (int x = 0; x < ip.getWidth(); x++) {
                bp.set(x, y, ip.getPixel(x, y));
            }
        }
        return bp;
    }

    /**
     * Realizza la gaussiana e ne inserisce i valori in un array
     *
     * @param window numero di righi e colonne della matrice gaussiana. Deve
     * essere dispari
     * @param sigma
     * @return array della gaussiana
     * @throws IllegalArgumentException se la finestra Ë negativa, zero o pari.
     * se sigma Ë zero o negativa.
     */
    public static float[] initGaussianKernel(int window, float sigma) throws
            IllegalArgumentException {
        controlInput(window, sigma);
        short aperture = (short) (window / 2);
        float[][] gaussianKernel = new float[2 * aperture + 1][2 * aperture + 1];
        float out[] = new float[(2 * aperture + 1) * (2 * aperture + 1)];
        int k = 0;
        float sum = 0;
        for (int dy = -aperture; dy <= aperture; dy++) {
            for (int dx = -aperture; dx <= aperture; dx++) {
                gaussianKernel[dx + aperture][dy + aperture] = (float) Math.exp(-(dx * dx + dy * dy) / (2 * sigma * sigma));
                sum += gaussianKernel[dx + aperture][dy + aperture];
            }
        }
        for (int dy = -aperture; dy <= aperture; dy++) {
            for (int dx = -aperture; dx <= aperture; dx++) {
                out[k++] = gaussianKernel[dx + aperture][dy + aperture] / sum;
            }
        }
        return out;
    }

    /**
     * controllo dei valori della gaussiana
     *
     * @param window la finestra della gaussiana.
     * @param sigma il valore di sigma della gaussiana
     * @throws IllegalArgumentException se la finestra Ë negativa, zero o non Ë
     * dispari. se sigma Ë zero o negativa.
     */
    private static void controlInput(int window, float sigma) throws
            IllegalArgumentException {
        if (window % 2 == 0) {
            throw new IllegalArgumentException("Window isn't an odd.");
        }
        if (window <= 0) {
            throw new IllegalArgumentException("Window is negative or zero");
        }
        if (sigma <= 0) {
            throw new IllegalArgumentException("Sigma of the gaussian is zero or negative.");
        }
    }

    /**
     * metodo che disegna i corners nell'immagine
     *
     * @param corn lista di tutti i corners trvati
     * @param i immagine su cui disegnare i corners
     * @param colori array che specifica il numero di corners trovati ad ogni
     * iterazione
     * @return immagine a colori con i corners identificati da delle piccole
     * croci colorate
     */
    public static ColorProcessor disegna(List<int[]> corn, ColorProcessor i, int[] colori) {
        int width = i.getWidth();
        int height = i.getHeight();
        //crea le linee orizzontali
        int R = 0;
        int G = 0;
        int B = 255;
        int colore = (R << 16) | (G << 8) | B;
        int conta = 1;
        int j = 0;
        boolean esiste = true;
        int tuttiColori[] = new int[colori.length];
        tuttiColori[0] = colore;

        for (int[] p : corn) {

            if (conta > colori[j]) {
                conta = 1;
                esiste = true;
                if (j < colori.length - 1) {
                    j++;
                    while (esiste) {
                        R = (int) (Math.random() * 256);
                        G = (int) (Math.random() * 256);
                        B = (int) (Math.random() * 256);

                        colore = (R << 16) | (G << 8) | B;
                        esiste = false;

                        for (int k = 0; k < tuttiColori.length; k++) {
                            if (colore == tuttiColori[k]) {
                                esiste = true;
                            }
                        }
                    }

                    tuttiColori[j] = colore;

                    colore = (R << 16) | (G << 8) | B;
                }
            }

            for (int dx = -2; dx <= 2; dx++) {
                if (p[0] + dx < 0 || p[0] + dx >= width) {
                    continue;
                }
                i.set(p[0] + dx, p[1], colore);

            }

            //crea le linee verticali
            for (int dy = -2; dy <= 2; dy++) {

                if (p[1] + dy < 0 || p[1] + dy >= height) {
                    continue;
                }

                i.set(p[0], p[1] + dy, colore);
            }
            ++conta;
        }

        return i;
    }
}

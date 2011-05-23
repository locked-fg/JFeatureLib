package de.lmu.dbs.haralick;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public abstract class Calculator {

    public static final int VALUES = 1;
    public static final int GRAY_HISTOGRAM = 1 << 1;
    public static final int MOMENTS = 1 << 2;
    public static final int ROUGHNESS = 1 << 3;
    public static final int TEXTURE = 1 << 4;
    public static final int ALL = VALUES | GRAY_HISTOGRAM | MOMENTS | ROUGHNESS | TEXTURE;
    /** The number of gray levels in an image */
    protected static final int GRAY_RANGES = 256;
    /** The number of gray values for the textures */
    protected static final int NUM_GRAY_VALUES = 32;
    /** The scale for the gray values for conversion rgb to gray values. */
    protected static final double GRAY_SCALE = (double) GRAY_RANGES / (double) NUM_GRAY_VALUES;
    /** Contains the distances of the neighboring pixels for one pixel. */
    protected static final int[] DISTANCES = {1, 3, 5, 7, 11};
    /** The range of the h values. */
    protected static final int HS_H_RANGES = 8;
    /** The range of the s values. */
    protected static final int HS_S_RANGES = 4;
    /** The range of the u values. */
    protected static final int UV_U_RANGES = 6;
    /** The range of the v values. */
    protected static final int UV_V_RANGES = 6;
    protected int imageWidth;
    protected int imageHeight;
    protected int imageSize;
    protected BufferedImage image;
    protected Map<String, DescriptorInfo> descriptors;

    public Calculator(Calculator calc) {
        this.imageWidth = calc.imageWidth;
        this.imageHeight = calc.imageHeight;
        this.imageSize = calc.imageSize;
        this.image = calc.image;
        this.descriptors = new HashMap<String, DescriptorInfo>();
    }

    public Calculator(BufferedImage image) {
        this.imageWidth = image.getWidth();
        this.imageHeight = image.getHeight();
        this.imageSize = this.imageWidth * this.imageHeight;
        this.image = image;
        this.descriptors = new HashMap<String, DescriptorInfo>();
    }

    public Collection<DescriptorInfo> getAllCalculatedDescriptorInfos() {
        return this.descriptors.values();
    }

    public DescriptorInfo getDescriptorInfo(String name) {
        if (!this.hasCalculatedDescriptorInfo(name)) {
            this.calculate();
        }
        return this.descriptors.get(name);
    }

    public boolean hasCalculatedDescriptorInfo(String name) {
        return this.descriptors.containsKey(name);
    }

    public void addDescriptorInfo(String key, double[] data) {
        this.descriptors.put(key,
                new DescriptorInfo(key, data));
    }

    public abstract void calculate();

    public static Collection<DescriptorInfo> calculateAll(BufferedImage image, int calculatorFlag) {
        ArrayList<DescriptorInfo> descriptorInfos = new ArrayList<DescriptorInfo>();

        ValueCalculator vc = new ValueCalculator(image);
        vc.calculate();
        if ((calculatorFlag & VALUES) == VALUES) {
            descriptorInfos.addAll(vc.getAllCalculatedDescriptorInfos());
        }

        if ((calculatorFlag & GRAY_HISTOGRAM) == GRAY_HISTOGRAM) {
            GrayHistogramCalculator ghc = new GrayHistogramCalculator(vc);
            ghc.calculate();
            descriptorInfos.addAll(ghc.getAllCalculatedDescriptorInfos());
        }

        if ((calculatorFlag & MOMENTS) == MOMENTS) {
            MomentsCalculator mc = new MomentsCalculator(vc);
            mc.calculate();
            descriptorInfos.addAll(mc.getAllCalculatedDescriptorInfos());
        }

        if ((calculatorFlag & ROUGHNESS) == ROUGHNESS) {
            RoughnessCalculator rc = new RoughnessCalculator(vc);
            rc.calculate();
            descriptorInfos.addAll(rc.getAllCalculatedDescriptorInfos());
        }

//        if ((calculatorFlag & TEXTURE) == TEXTURE) {
//            TextureCalculator tx = new TextureCalculator(vc);
//            tx.calculate();
//            descriptorInfos.addAll(tx.getAllCalculatedDescriptorInfos());
//        }

        // ensure results are in the same order
        Collections.sort(descriptorInfos,
                new Comparator<DescriptorInfo>() {

                    @Override
                    public int compare(DescriptorInfo o1, DescriptorInfo o2) {
                        return o1.name.compareTo(o2.name);
                    }
                });

        return descriptorInfos;
    }
}

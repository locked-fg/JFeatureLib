package de.lmu.dbs.features.haralick;

@Deprecated
public class DescriptorInfo {

    public String name;
    public double[] data;

    public DescriptorInfo(String name, double[] data) {
        this.name = name;
        this.data = data;
    }
}

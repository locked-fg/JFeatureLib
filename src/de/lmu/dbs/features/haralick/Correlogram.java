package de.lmu.dbs.features.haralick;

@Deprecated
public class Correlogram {
	private int[][]correlogram;
	private int maxvalue;
	
	public Correlogram(int[][] incorr) {
		super();
		correlogram = incorr;
		maxvalue = 1;
	}
	
	public Correlogram(int[][] incorr,int inmax) {
		super();
		correlogram = incorr;
		maxvalue = inmax;
	}
	public Correlogram() {
		super();
		correlogram = new int [1][1];
		maxvalue = 1;
	}
	
	public int[][] getCorrelogram() {
		return correlogram;
	}
	public void setCorrelogram(int[][] correlogram) {
		this.correlogram = correlogram;
	}
	public int getMaxvalue() {
		return maxvalue;
	}
	public void setMaxvalue(int maxvalue) {
		this.maxvalue = maxvalue;
	}
}

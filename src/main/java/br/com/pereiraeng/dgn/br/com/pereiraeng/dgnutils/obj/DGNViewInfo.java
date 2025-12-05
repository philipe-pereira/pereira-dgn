package br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj;

public class DGNViewInfo {
	public int flags;
	public byte[] levels = new byte[8];
	public DGNPoint origin = new DGNPoint();
	public DGNPoint delta = new DGNPoint();
	public double[] transmatrx = new double[9];
	public double conversion;
	public long activez;
}

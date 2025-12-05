package br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.elm;

import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.DGNPoint;

public class DGNElemCellHeader extends DGNElem {
	/**
	 * !< Total length of cell in words, excluding the first 19 words (header +
	 * totlength field)
	 */
	public int totlength;
	/**
	 * !< Cell name
	 */
	private char[] name = new char[7];
	/**
	 * !< Class bitmap
	 */
	public short cclass;
	/**
	 * !< Levels used in cell
	 */
	public short[] levels = new short[4];

	/**
	 * !< X/Y/Z minimums for cell
	 */
	public DGNPoint rnglow = new DGNPoint();
	/**
	 * !< X/Y/Z maximums for cell
	 */
	public DGNPoint rnghigh = new DGNPoint();

	/**
	 * !< 2D/3D Transformation Matrix
	 */
	public double[] trans = new double[9];
	/**
	 * !< Cell Origin
	 */
	public DGNPoint origin = new DGNPoint();

	public double xscale;
	public double yscale;
	public double rotation;

	public String getName() {
		return new String(name);
	}

	public char[] getNameC() {
		return name;
	}
}

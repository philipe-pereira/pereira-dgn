package br.com.pereiraeng.dgn.object.elements;

public class DGNElemComplexHeader extends DGNElem {

	/**
	 * !< Total length of surface in words, excluding the first 19 words (header
	 * + totlength field)
	 */
	public int totlength;
	/** !< # of elements in surface */
	public int numelems;
	/**
	 * !< surface/solid type (only used for 3D surface/solid). One of DGNSUT_*
	 * or DGNSOT_*.
	 */
	public int surftype;
	/**
	 * !< # of elements in each boundary (only used for 3D surface/solid).
	 */
	public int boundelms;
}

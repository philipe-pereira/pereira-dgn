package br.com.pereiraeng.dgn.object.elements;

public class DGNElemBSplineCurveHeader extends DGNElem {
	/**
	 * !< Total length of B-Spline curve in words, excluding the first 20 words
	 * (header + desc_words field)
	 */
	public long desc_words;
	/** !< B-spline order: 2-15 */
	public byte order;
	/** !< Properties: ORing of DGNBSC_ flags */
	public byte properties;
	/** !< curve type */
	public byte curve_type;
	/** !< number of poles, max. 101 */
	public short num_poles;
	/** !< number of knots */
	public short num_knots;
}

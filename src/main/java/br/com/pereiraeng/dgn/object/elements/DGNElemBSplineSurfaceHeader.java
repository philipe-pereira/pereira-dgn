package br.com.pereiraeng.dgn.object.elements;

public class DGNElemBSplineSurfaceHeader extends DGNElem {

	/**
	 * !< Total length of B-Spline surface in words, excluding the first 20
	 * words (header + desc_words field)
	 */
	public long desc_words;
	/** !< curve type */
	public byte curve_type;
	/** !< B-spline U order: 2-15 */
	public byte u_order;
	/**
	 * !< surface U properties: ORing of DGNBSC_ flags
	 */
	public byte u_properties;
	/** !< number of poles */
	public short num_poles_u;
	/** !< number of knots */
	public short num_knots_u;
	/** !< number of rule lines */
	public short rule_lines_u;

	/** !< B-spline V order: 2-15 */
	public char v_order;
	/**
	 * !< surface V properties: Oring of DGNBSS_ flags
	 */
	public byte v_properties;
	/** !< number of poles */
	public short num_poles_v;
	/** !< number of knots */
	public short num_knots_v;
	/** !< number of rule lines */
	public short rule_lines_v;

	/** !< number of boundaries */
	public short num_bounds;
}

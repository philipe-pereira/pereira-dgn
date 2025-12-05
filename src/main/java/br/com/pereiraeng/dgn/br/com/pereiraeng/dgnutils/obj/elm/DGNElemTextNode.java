package br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.elm;

import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.DGNPoint;

public class DGNElemTextNode extends DGNElem {

	/*
	 * !< Total length of the node (bytes = totlength * 2 + 38)
	 */
	public int totlength;

	/* !< Number of text strings */
	public int numelems;
	/* !< text node number */
	public int node_number;
	/* !< maximum length allowed, characters */
	public short max_length;
	/* !< maximum length used */
	public short max_used;
	/* !< text font used */
	public short font_id;
	/* !< justification type, see DGNJ_ */
	public short justification;
	/* !< spacing between text strings */
	long line_spacing;
	/* !< length multiplier */
	public double length_mult;
	/* !< height multiplier */
	public double height_mult;
	/* !< rotation angle (2d) */
	public double rotation;
	/* !< Snap origin (as defined by user) */
	public DGNPoint origin = new DGNPoint();
}

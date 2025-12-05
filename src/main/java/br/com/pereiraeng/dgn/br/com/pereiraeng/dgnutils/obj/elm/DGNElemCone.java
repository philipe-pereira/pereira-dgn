package br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.elm;

import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.DGNPoint;

public class DGNElemCone extends DGNElem {

	/** !< Unknown data */
	public short unknown;
	/** !< Orientation quaternion */
	public int[] quat = new int[4];
	/** !< center of first circle */
	public DGNPoint center_1;
	/** !< radius of first circle */
	public 	double radius_1;
	/** !< center of second circle */
	public 	DGNPoint center_2;
	/** !< radius of second circle */
	public 	double radius_2;
}

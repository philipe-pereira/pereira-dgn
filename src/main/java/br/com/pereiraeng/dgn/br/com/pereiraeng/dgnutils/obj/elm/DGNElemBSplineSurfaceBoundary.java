package br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.elm;

import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.DGNPoint;

public class DGNElemBSplineSurfaceBoundary extends DGNElem {

	/** !< boundary number */
	public short number;
	/** !< number of boundary vertices */
	public short numverts;
	/**
	 * !< Array of 1 or more 2D boundary vertices (in UV space)
	 */
	public DGNPoint[] vertices;
}

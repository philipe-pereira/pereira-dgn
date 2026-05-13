package br.com.pereiraeng.dgn.object.elements;

import br.com.pereiraeng.dgn.object.DGNPoint;

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

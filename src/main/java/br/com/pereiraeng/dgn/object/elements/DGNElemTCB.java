package br.com.pereiraeng.dgn.object.elements;

import br.com.pereiraeng.dgn.object.DGNViewInfo;

public class DGNElemTCB extends DGNElem {

	/** !< Dimension (2 or 3) */
	public int dimension;

	/** !< X origin of UOR space in master units(?) */
	public double origin_x;
	/** !< Y origin of UOR space in master units(?) */
	public double origin_y;
	/** !< Z origin of UOR space in master units(?) */
	public double origin_z;

	/** !< UOR per subunit. */
	public long uor_per_subunit;
	/** !< User name for subunits (2 chars) */
	public char[] sub_units = new char[3];
	/** !< Subunits per master unit. */
	public long subunits_per_master;
	/**
	 * !< User name for master units (2 chars)
	 */
	public char[] master_units = new char[3];

	public DGNViewInfo[] views = new DGNViewInfo[8];
}

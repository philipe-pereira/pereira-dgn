package br.com.pereiraeng.dgn.object.elements;

public class DGNElemColorTable extends DGNElem {

	public int screen_flag;
	/**
	 * !< Color table, 256 colors by red (0), green(1) and blue(2) component.
	 */
	public byte[][] color_info = new byte[256][3];

}

package br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.elm;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;

import br.com.pereiraeng.swing.interfaces.DesM;
import br.com.pereiraeng.swing.interfaces.WL;

public class DGNElem implements DesM, Cloneable {
	public int offset;
	public int size;

	/**
	 * !< Element number (zero based)
	 */
	public int element_id;
	/**
	 * !< Structure type: (DGNST_*)
	 */
	public int stype;
	/**
	 * !< Element Level: 0-63
	 */
	public int level;
	/**
	 * !< Element type (DGNT_)
	 */
	public int type;
	/**
	 * !< Is element complex?
	 */
	public boolean complex;
	/**
	 * !< Is element deleted?
	 */
	public boolean deleted;

	/**
	 * !< Graphic group number
	 */
	public int graphic_group;
	/**
	 * !< Properties: ORing of DGNPF_ flags
	 */
	public int properties;
	/**
	 * !< Color index (0-255)
	 */
	public int color;
	/**
	 * !< Line Weight (0-31)
	 */
	public int weight;
	/**
	 * !< Line Style: One of DGNS_* values
	 */
	public int style;

	/**
	 * !< Bytes of attribute data, usually zero.
	 */
	public int attr_bytes;
	/**
	 * !< Raw attribute data
	 */
	public byte[] attr_data;

	/**
	 * !< Bytes of raw data, usually zero.
	 */
	public int raw_bytes;
	/**
	 * !< All raw element data including header.
	 */
	public byte[] raw_data;

	// ------------------------------------------------------------------

	private transient boolean highlight = false, drawable = true;

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}

	@Override
	public void setDrawable(boolean drawable) {
		this.drawable = drawable;
	}

	@Override
	public boolean isDrawable() {
		return (!deleted && drawable) || highlight;
	}

	@Override
	public boolean wasDrawn() {
		return true;
	}

	protected WL wl;

	@Override
	public void setWL(WL wl) {
		this.wl = wl;
	}

	@Override
	public void drawObject(Graphics2D g) {
		g.setColor(highlight ? Color.RED : Color.BLACK);
		drawObject(g, 0., 0., 1., 1.);
	}

	public void drawObject(Graphics2D g, double dx, double dy, double scale_x, double scale_y) {
	}

	@Override
	public Float getMin() {
		return new Point2D.Float();
	}

	@Override
	public Float getMax() {
		return new Point2D.Float();
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
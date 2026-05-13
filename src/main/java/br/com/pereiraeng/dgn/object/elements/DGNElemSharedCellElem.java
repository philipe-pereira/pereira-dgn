package br.com.pereiraeng.dgn.object.elements;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;

import br.com.pereiraeng.dgn.object.DGNPoint;

public class DGNElemSharedCellElem extends DGNElem {

	public DGNElemSharedCellElem() {
		this.insert = new DGNPoint();
	}

	public String name;

	public DGNPoint insert = null;

	public double scale_x = 1., scale_y = 1.;

	public DGNElemSharedCellDefn definition;

	// ----------------- DRAWER -----------------

	@Override
	public void drawObject(Graphics2D g) {
		if (definition.elems != null) {
			for (DGNElem el : definition.elems) {
				el.setWL(wl);
				el.drawObject(g, insert.x, insert.y, scale_x, scale_y);
			}
		}
	}

	@Override
	public Float getMin() {
		return new Point2D.Float((float) this.insert.x, (float) this.insert.y);
	}

	@Override
	public Float getMax() {
		return new Point2D.Float((float) this.insert.x, (float) this.insert.y);
	}
}

package br.com.pereiraeng.dgn.object;

import java.awt.geom.Point2D;

public class DGNPoint extends Point2D.Double {
	private static final long serialVersionUID = 1L;

	public DGNPoint() {
		this(0., 0., 0.);
	}

	public DGNPoint(DGNPoint p) {
		this(p.x, p.y, p.z);
	}

	public DGNPoint(double x, double y) {
		this(x, y, 0.);
	}

	public DGNPoint(double x, double y, double z) {
		super(x, y);
		this.z = z;
	}

	/**
	 * !< z, up coordinate. Zero for 2D objects.
	 */
	public double z;

	@Override
	public String toString() {
		return String.format("(%.5f;%.5f;%.5f)", x, y, z);
	}
}

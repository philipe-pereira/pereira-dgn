package br.com.pereiraeng.dgn.object.elements;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;

import br.com.pereiraeng.core.BinaryUtils;
import br.com.pereiraeng.dgn.DGNfile;
import br.com.pereiraeng.dgn.DGNlib;
import br.com.pereiraeng.dgn.DGNreader;
import br.com.pereiraeng.dgn.DGNwriter;
import br.com.pereiraeng.dgn.object.DGNPoint;
import br.com.pereiraeng.swing.LeafOM;

public class DGNElemArc extends DGNElem {
	/**
	 * !< Origin of ellipse
	 */
	public DGNPoint origin = new DGNPoint();

	/**
	 * !< Primary axis length
	 */
	public double primary_axis;
	/**
	 * !< Secondary axis length
	 */
	public double secondary_axis;

	/**
	 * !< Counterclockwise rotation in degrees
	 */
	public double rotation;
	public int[] quat = new int[4];

	/**
	 * !< Start angle (degrees counterclockwise of primary axis)
	 */
	public double startang;

	/**
	 * !< Sweep angle (degrees)
	 */
	public double sweepang;

	// -------------------- EDITING ELEMENT --------------------

	public void changeBinary(DGNfile psDGN) {
		DGNPoint origin = (DGNPoint) this.origin.clone();
		if (super.type == DGNlib.DGNT_ARC) {

			/* axes */
			double dfScaledAxis = primary_axis / psDGN.scale;
			System.arraycopy(BinaryUtils.IEEE2vax(dfScaledAxis), 0, raw_data, 44, 8);

			dfScaledAxis = secondary_axis / psDGN.scale;
			System.arraycopy(BinaryUtils.IEEE2vax(dfScaledAxis), 0, raw_data, 52, 8);

			/* origin */
			if (psDGN.dimension == 3) {

				DGNreader.DGNInverseTransformPoint(psDGN, origin);

				System.arraycopy(BinaryUtils.IEEE2vax(origin.x), 0, raw_data, 76, 8);
				System.arraycopy(BinaryUtils.IEEE2vax(origin.y), 0, raw_data, 84, 8);
				System.arraycopy(BinaryUtils.IEEE2vax(origin.z), 0, raw_data, 92, 8);
			} else {
				DGNreader.DGNInverseTransformPoint(psDGN, origin);

				System.arraycopy(BinaryUtils.IEEE2vax(origin.x), 0, raw_data, 64, 8);
				System.arraycopy(BinaryUtils.IEEE2vax(origin.y), 0, raw_data, 72, 8);
			}
		} else {
			/* axes */
			double dfScaledAxis = primary_axis / psDGN.scale;
			System.arraycopy(BinaryUtils.IEEE2vax(dfScaledAxis), 0, raw_data, 36, 8);

			dfScaledAxis = secondary_axis / psDGN.scale;
			System.arraycopy(BinaryUtils.IEEE2vax(dfScaledAxis), 0, raw_data, 44, 8);

			if (psDGN.dimension == 3) {
				/* origin */
				DGNreader.DGNInverseTransformPoint(psDGN, origin);
				System.arraycopy(BinaryUtils.IEEE2vax(origin.x), 0, raw_data, 68, 8);
				System.arraycopy(BinaryUtils.IEEE2vax(origin.y), 0, raw_data, 76, 8);
				System.arraycopy(BinaryUtils.IEEE2vax(origin.z), 0, raw_data, 84, 8);
			} else {
				/* origin */
				DGNreader.DGNInverseTransformPoint(psDGN, origin);
				System.arraycopy(BinaryUtils.IEEE2vax(origin.x), 0, raw_data, 56, 8);
				System.arraycopy(BinaryUtils.IEEE2vax(origin.y), 0, raw_data, 64, 8);
			}
		}

		DGNPoint sMin = new DGNPoint(), sMax = new DGNPoint();

		sMin.x = this.origin.x - Math.max(primary_axis, secondary_axis);
		sMin.y = this.origin.y - Math.max(primary_axis, secondary_axis);
		sMin.z = this.origin.z - Math.max(primary_axis, secondary_axis);
		sMax.x = this.origin.x + Math.max(primary_axis, secondary_axis);
		sMax.y = this.origin.y + Math.max(primary_axis, secondary_axis);
		sMax.z = this.origin.z + Math.max(primary_axis, secondary_axis);

		DGNwriter.DGNWriteBounds(psDGN, this, sMin, sMax);
	}

	// ----------------- DRAWER -----------------

	@Override
	public void drawObject(Graphics2D g, double dx, double dy, double scale_x, double scale_y) {
		Point p = LeafOM.getTranformedPoint((float) (scale_x * origin.x + dx), (float) (scale_y * origin.y + dy),
				super.wl);
		Point size = LeafOM.getTranformedPoint(0f, 0f, (float) (primary_axis * scale_x),
				(float) (-1f * secondary_axis * scale_y), wl);

		g.drawArc(p.x - size.x, p.y - size.y, 2 * size.x, 2 * size.y, (int) (rotation + startang), (int) sweepang);
	}

	@Override
	public Float getMin() {
		return new Point2D.Float((float) (origin.x - primary_axis), (float) (origin.y - secondary_axis));
	}

	@Override
	public Float getMax() {
		return new Point2D.Float((float) (origin.x + primary_axis), (float) (origin.y + secondary_axis));
	}
}

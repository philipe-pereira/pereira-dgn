package br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.elm;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;

import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.DGNfile;
import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.DGNlib;
import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.DGNreader;
import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.DGNwriter;
import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.DGNPoint;
import br.com.pereiraeng.swing.LeafOM;

public class DGNElemMultiPoint extends DGNElem {

	/**
	 * !< Number of vertices in "vertices"
	 */
	public int num_vertices;

	/**
	 * !< Array of two or more vertices
	 */
	public DGNPoint[] vertices;

	// -------------------- EDITING ELEMENT --------------------

	public void changeBinary(DGNfile dgnFile) {
		if (type == DGNlib.DGNT_LINE) {
			DGNreader.DGNInverseTransformPointToInt(dgnFile, vertices[0], raw_data, 36);
			DGNreader.DGNInverseTransformPointToInt(dgnFile, vertices[1], raw_data, 36 + dgnFile.dimension * 4);
		} else {
			for (int i = 0; i < num_vertices; i++)
				DGNreader.DGNInverseTransformPointToInt(dgnFile, vertices[i], raw_data, 38 + dgnFile.dimension * i * 4);
		}

		DGNPoint sMin = new DGNPoint(vertices[0]), sMax = new DGNPoint(vertices[0]);

		for (int i = 1; i < num_vertices; i++) {
			sMin.x = Math.min(vertices[i].x, sMin.x);
			sMin.y = Math.min(vertices[i].y, sMin.y);
			sMin.z = Math.min(vertices[i].z, sMin.z);
			sMax.x = Math.max(vertices[i].x, sMax.x);
			sMax.y = Math.max(vertices[i].y, sMax.y);
			sMax.z = Math.max(vertices[i].z, sMax.z);
		}

		DGNwriter.DGNWriteBounds(dgnFile, this, sMin, sMax);
	}

	// ----------------- DRAWER -----------------

	@Override
	public void drawObject(Graphics2D g, double dx, double dy, double scale_x, double scale_y) {
		Point p = LeafOM.getTranformedPoint((float) (scale_x * vertices[0].x + dx),
				(float) (scale_y * vertices[0].y + dy), wl);

		for (int i = 1; i < num_vertices; i++) {
			Point p0 = LeafOM.getTranformedPoint((float) (scale_x * vertices[i].x + dx),
					(float) (scale_y * vertices[i].y + dy), wl);
			g.drawLine((int) p.x, (int) p.y, (int) p0.x, (int) p0.y);
			p = p0;
		}
	}

	@Override
	public Float getMin() {
		Point2D.Float out = new Point2D.Float();
		for (int i = 0; i < num_vertices; i++) {
			DGNPoint p0 = vertices[i];
			out.x = (float) Math.min(out.x, p0.x);
			out.y = (float) Math.min(out.y, p0.y);
		}
		return out;
	}

	@Override
	public Float getMax() {
		Point2D.Float out = new Point2D.Float();
		for (int i = 0; i < num_vertices; i++) {
			DGNPoint p0 = vertices[i];
			out.x = (float) Math.max(out.x, p0.x);
			out.y = (float) Math.max(out.y, p0.y);
		}
		return out;
	}
}

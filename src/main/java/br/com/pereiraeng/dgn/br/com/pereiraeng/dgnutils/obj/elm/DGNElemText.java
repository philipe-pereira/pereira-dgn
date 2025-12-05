package br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.elm;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.Arrays;

import br.com.pereiraeng.core.BinaryUtils;
import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.DGNfile;
import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.DGNreader;
import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.DGNwriter;
import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.DGNPoint;
import br.com.pereiraeng.swing.LeafOM;

public class DGNElemText extends DGNElem {

	/** !< Microstation font id, no list available */
	public int font_id;
	/** !< Justification, see DGNJ_* */
	public int justification;
	/** !< Char width in master (if square) */
	public double length_mult;
	/** !< Char height in master units */
	public double height_mult;
	/** !< Counterclockwise rotation in degrees */
	public double rotation;
	/** !< Bottom left corner of text. */
	public DGNPoint origin = new DGNPoint();
	/**
	 * !< Actual text (length varies, \0 terminated
	 */
	public String string;

	public void changeBinary(DGNfile dgnFile, boolean text, boolean orig, boolean scal) {
		if (text) {
			// se o texto ('string') foi alterado...

			// ---------------- NÚMERO DE BYTES ----------------
			// JAVA
			if (dgnFile.dimension == 2)
				raw_bytes = 60 + string.length();
			else
				raw_bytes = 76 + string.length();
			raw_bytes += (raw_bytes % 2);

			// BINÁRIO
			int nWords = (raw_bytes / 2) - 2;
			raw_data[2] = (byte) (nWords % 256);
			raw_data[3] = (byte) (nWords / 256);

			// ---------------- TEXTO ----------------
			// JAVA
			// já foi alterado...

			// BINÁRIO
			int nBase = -1;
			if (dgnFile.dimension == 2)
				nBase = 58;
			else
				nBase = 74;
			raw_data[nBase] = (byte) string.length();
			raw_data[nBase + 1] = 0;

			// ensure space or truncate
			raw_data = Arrays.copyOf(raw_data, raw_bytes);

			System.arraycopy(string.getBytes(), 0, raw_data, nBase + 2, string.length());

		}

		if (orig) {
			// se a âncora do texto foi alterada...
			if (dgnFile.dimension == 2)
				DGNreader.DGNInverseTransformPointToInt(dgnFile, origin, raw_data, 50);
			else
				DGNreader.DGNInverseTransformPointToInt(dgnFile, origin, raw_data, 62);
		}

		if (scal) {
			// se o tamanho do texto foi alterado...
			int nIntValue = (int) (length_mult * 1000.0 / (dgnFile.scale * 6.0) + 0.5);
			BinaryUtils.getBytesME(nIntValue, raw_data, 38);

			nIntValue = (int) (height_mult * 1000.0 / (dgnFile.scale * 6.0) + 0.5);
			BinaryUtils.getBytesME(nIntValue, raw_data, 42);
		}

		// ---------------- BORDAS ----------------
		// JAVA

		// Set the core raw data, including the bounds.
		DGNPoint sMin = new DGNPoint(), sMax = new DGNPoint(), sLowLeft = new DGNPoint(), sLowRight = new DGNPoint(),
				sUpLeft = new DGNPoint(), sUpRight = new DGNPoint();

		// calculate bounds if rotation is 0
		sMin.x = origin.x;
		sMin.y = origin.y;
		sMin.z = 0.0;
		sMax.x = origin.x + length_mult * string.length();
		sMax.y = origin.y + height_mult;
		sMax.z = 0.0;

		// calculate rotated bounding box coordinates
		double length = sMax.x - sMin.x;
		double height = sMax.y - sMin.y;
		double diagonal = Math.hypot(length, height);
		sLowLeft.x = sMin.x;
		sLowLeft.y = sMin.y;
		sLowRight.x = sMin.x + Math.cos(rotation * Math.PI / 180.0) * length;
		sLowRight.y = sMin.y + Math.sin(rotation * Math.PI / 180.0) * length;
		sUpRight.x = sMin.x + Math.cos((rotation * Math.PI / 180.0) + Math.atan(height / length)) * diagonal;
		sUpRight.y = sMin.y + Math.sin((rotation * Math.PI / 180.0) + Math.atan(height / length)) * diagonal;
		sUpLeft.x = sMin.x + Math.cos((rotation + 90.0) * Math.PI / 180.0) * height;
		sUpLeft.y = sMin.y + Math.sin((rotation + 90.0) * Math.PI / 180.0) * height;

		// calculate new values for bounding box
		sMin.x = Math.min(sLowLeft.x, Math.min(sLowRight.x, Math.min(sUpLeft.x, sUpRight.x)));
		sMin.y = Math.min(sLowLeft.y, Math.min(sLowRight.y, Math.min(sUpLeft.y, sUpRight.y)));
		sMax.x = Math.max(sLowLeft.x, Math.max(sLowRight.x, Math.max(sUpLeft.x, sUpRight.x)));
		sMax.y = Math.max(sLowLeft.y, Math.max(sLowRight.y, Math.max(sUpLeft.y, sUpRight.y)));
		sMin.x = origin.x - length_mult * string.length();
		sMin.y = origin.y - height_mult;
		sMin.z = 0.0;
		sMax.x = origin.x + length_mult * string.length();

		// BINÁRIO
		DGNwriter.DGNWriteBounds(dgnFile, this, sMin, sMax);
	}

	// ----------------- DRAWER -----------------

	private static final float LACOCHAMBRE = -1.3f;

	@Override
	public void drawObject(Graphics2D g, double dx, double dy, double scale_x, double scale_y) {
		Point p = LeafOM.getTranformedPoint((float) (scale_x * origin.x + dx), (float) (scale_y * origin.y + dy), wl);
		int size = LeafOM.getTranformedPoint(0f, 0f, 0, (float) (LACOCHAMBRE * scale_y * height_mult), wl).y;

		g.setFont(new Font("Courier", Font.PLAIN, size));
		g.drawString(string, p.x, p.y);
	}

	@Override
	public Float getMin() {
		return new Point2D.Float((float) origin.x, (float) origin.y);
	}

	@Override
	public Float getMax() {
		return new Point2D.Float((float) (origin.x - LACOCHAMBRE * height_mult * string.length()),
				(float) (origin.y - LACOCHAMBRE * height_mult));
	}
}

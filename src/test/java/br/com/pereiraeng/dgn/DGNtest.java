package br.com.pereiraeng.dgn;

import br.com.pereiraeng.dgn.object.DGNPoint;
import br.com.pereiraeng.dgn.object.elements.DGNElem;

public class DGNtest {

	public static void main(String[] args) {

		int task = -100;

		switch (task) {
		case 0:
			DGNfile hNewDGN = DGNwriter.DGNCreate("001.dgn", "files/eng/seed.dgn",
					DGNlib.DGNCF_USE_SEED_UNITS | DGNlib.DGNCF_USE_SEED_ORIGIN, 0.0, 0.0, 0.0, 0, 0, "", "");

			if (hNewDGN == null)
				return;

			// ----------------- Write one line segment to it -----------------

			DGNwriter.writeLine(hNewDGN, new DGNPoint(0, 0, 100), new DGNPoint(10000, 4000, 110));
			// --------------------- Write a line string ---------------------

			DGNwriter.writeMultiLine(hNewDGN, new DGNPoint(0, 1000), new DGNPoint(6000, 5000),
					new DGNPoint(12000, 6000));

			// ------------------------- Write an Arc -------------------------

			DGNwriter.writeArc(hNewDGN, 9, 2000.0, 3000.0, 500.0, 2000.0, 1000.0, 0.0, 270.0, 0.0, null);
			// ---------------- Write an Ellipse with fill info ----------------

			DGNwriter.writeEllipse(hNewDGN, 9, 200.0, 30.0, 5.0, 1000.0, 1000.0, 0.0, 360.0, 0.0, null);
			// --------------------- Write some text ---------------------,

			DGNwriter.writeText(hNewDGN, 9, "This is a test string", 0, DGNlib.DGNJ_CENTER_TOP, 200.0, 200.0, 0.0, null,
					2000.0, 3000.0, 0.0, new int[][] { { DGNlib.DGNLT_XBASE, 7, 101 }, { DGNlib.DGNLT_DMRS, 7, 101 } });
			DGNwriter.writeText(hNewDGN, 9, "------- 30 degrees", 0, DGNlib.DGNJ_CENTER_TOP, 200.0, 200.0, 30.0, null,
					2000.0, 3000.0, 0.0, null);
			DGNwriter.writeText(hNewDGN, 9, "------- 90 degrees", 0, DGNlib.DGNJ_CENTER_TOP, 200.0, 200.0, 90.0, null,
					2000.0, 3000.0, 0.0, null);
			// ----- Write a complex shape consisting of two line strings -----
			DGNElem[] psMembers = new DGNElem[2];

			DGNPoint[] asPoints = new DGNPoint[] { new DGNPoint(8000, 8000), new DGNPoint(6000, 8000),
					new DGNPoint(6000, 6000) };
			psMembers[0] = DGNwriter.DGNCreateMultiPointElem(hNewDGN, DGNlib.DGNT_LINE_STRING, 3, asPoints);
			DGNwriter.DGNUpdateElemCore(hNewDGN, psMembers[0], 9, 0, 9, 1, 0);

			asPoints = new DGNPoint[] { new DGNPoint(6000, 6000), new DGNPoint(8000, 6000), new DGNPoint(8000, 8000) };
			psMembers[1] = DGNwriter.DGNCreateMultiPointElem(hNewDGN, DGNlib.DGNT_LINE_STRING, 3, asPoints);
			DGNwriter.DGNUpdateElemCore(hNewDGN, psMembers[1], 9, 0, 9, 1, 0);

			DGNwriter.writeComplexGroup(hNewDGN, psMembers);
			// -------------- Write a cell with two line strings --------------
			psMembers = new DGNElem[2];

			asPoints = new DGNPoint[] { new DGNPoint(7000, 7000), new DGNPoint(5000, 7000), new DGNPoint(5000, 5000) };
			psMembers[0] = DGNwriter.DGNCreateMultiPointElem(hNewDGN, DGNlib.DGNT_LINE_STRING, 3, asPoints);
			DGNwriter.DGNUpdateElemCore(hNewDGN, psMembers[0], 10, 0, 9, 1, 0);

			asPoints = new DGNPoint[] { new DGNPoint(5000, 5000), new DGNPoint(8000, 5000), new DGNPoint(7000, 7000) };
			psMembers[1] = DGNwriter.DGNCreateMultiPointElem(hNewDGN, DGNlib.DGNT_LINE_STRING, 3, asPoints);
			DGNwriter.DGNUpdateElemCore(hNewDGN, psMembers[1], 9, 0, 9, 1, 0);

			DGNwriter.writeCellGroup(hNewDGN, "BE70", new DGNPoint(0, 0), 1.0, 1.0, 0.0, psMembers);

			// Close it.
			hNewDGN.close();
			break;
		}

	}
}

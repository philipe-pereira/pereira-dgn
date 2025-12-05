package br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.util.Collection;

import br.com.pereiraeng.math.geometry.Elipse;
import br.com.pereiraeng.core.Orientation;
import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.DGNPoint;
import br.com.pereiraeng.drawing.drawutils.LID;
import br.com.pereiraeng.electricalcircuit.components.*;

public class DGNcircuit {

	public static void getDGNs(File fileDGN, Dimension grade, Collection<ElecElem> ees, Collection<Object[]> fore,
			Collection<Object[]> back, Dimension ground) {

		DGNfile hNewDGN = DGNwriter.DGNCreate(fileDGN.getAbsolutePath(), "files/eng/seed.dgn",
				DGNlib.DGNCF_USE_SEED_UNITS | DGNlib.DGNCF_USE_SEED_ORIGIN, 0.0, 0.0, 0.0, 0, 0, "", "");

		if (hNewDGN == null)
			return;

		// background
		if (back != null)
			draw(hNewDGN, back, 1f, 0, 0, ground.width, ground.height);

		// circuito
		for (ElecElem ee : ees) {
			if (ee instanceof No) {
				// se for um nó... nada mais que vários segmentos de reta
				No no = (No) ee;

				int np = no.getNumPoints();
				if (np > 1) {
					DGNPoint[] points = new DGNPoint[np];
					for (int i = 0; i < points.length; i++) {
						no.setIndex(i);
						points[i] = new DGNPoint(no.getX() * grade.width, -no.getY() * grade.height);
					}

					No.NodeType type = no.getType();
					int color = type.getColor(), weight = type.getWeight(), style = type.getStyle();

					if (np == 2)
						DGNwriter.writeLine(hNewDGN, color, weight, style, points[0], points[1]);
					else
						DGNwriter.writeMultiLine(hNewDGN, color, weight, style, points);
				}
			} else {
				// se for um componente... aí varia para cada caso

				if (ee instanceof Comp) {
					Comp comp = (Comp) ee;

					// todo elemento tem um terminal (e ele se localiza nas
					// coordenadas (0;0) da referência do próprio objeto)
					No no = comp.getNo();
					drawConnection(hNewDGN, no, comp.getConnectionPoint(), comp.getX(), comp.getY(), 0, 0, grade);

					if (comp instanceof CompCustom) {
						// se for um elemento customizado
						CompCustom cc = (CompCustom) comp;

						// conector dos demais terminais
						for (int i = 1; i < cc.getNs(); i++) {
							Point tp = cc.getTerminalPoint(i - 1);
							drawConnection(hNewDGN, cc.getNN(i), cc.getConnectionPointN(i), cc.getX(), cc.getY(), tp.x,
									tp.y, grade);
						}

						// instruções de desenhos (vinda dos XML da biblioteca)
						draw(hNewDGN, cc.getDrawInstructions(), ElecElemSeed.MULT, cc.getX(), cc.getY(), grade.width,
								grade.height);
					} else if (comp instanceof Ground) {
						Ground gnd = (Ground) comp;

						int x = gnd.getX() * grade.width, y = -gnd.getY() * grade.height;

						int y0 = y - grade.height;
						DGNwriter.writeLine(hNewDGN, new DGNPoint(x, y), new DGNPoint(x, y0));
						DGNwriter.writeLine(hNewDGN, new DGNPoint(x - 2 * grade.width, y0),
								new DGNPoint(x + 2 * grade.width, y0));
						DGNwriter.writeLine(hNewDGN, new DGNPoint(x - 3 * grade.width / 2, y - 3 * grade.height / 2),
								new DGNPoint(x + 3 * grade.width / 2, y - 3 * grade.height / 2));
						DGNwriter.writeLine(hNewDGN, new DGNPoint(x - grade.width, y - 2 * grade.height),
								new DGNPoint(x + grade.width, y - 2 * grade.height));
					} else if (comp instanceof Source) {
						// TODO
					} else if (comp instanceof Switch) {
						// TODO
					} else if (comp instanceof RLCcomp) {
						// TODO
					} else if (comp instanceof ZfComp) {
						// TODO
					} else if (comp instanceof Quadripole) {
						Quadripole quad = (Quadripole) comp;

						// conector dos demais terminais
						boolean b = Orientation.VERTICAL.ordinal() == quad.getOrientation();

						// 2
						drawConnection(hNewDGN, quad.getN2(), quad.getConnectionPoint2(), quad.getX(), quad.getY(),
								b ? 5 : 0, b ? 0 : 5, grade);
						// 3
						drawConnection(hNewDGN, quad.getN3(), quad.getConnectionPoint3(), quad.getX(), quad.getY(),
								b ? 0 : 9, b ? 9 : 0, grade);
						// 4
						drawConnection(hNewDGN, quad.getN4(), quad.getConnectionPoint4(), quad.getX(), quad.getY(),
								b ? 5 : 9, b ? 9 : 5, grade);

						// desenhar caixa do quadripolo
						int x0 = quad.getX() * grade.width;
						int y0 = quad.getY() * grade.height;

						int size = 7 * grade.width;

						float xA = x0 + (b ? -1 : 1) * grade.width, yA = -(y0 + (b ? 1 : -1) * grade.height);

						DGNwriter.writeShape(hNewDGN, DGNwriter.COLOR_DEFAULT, new DGNPoint(xA, yA),
								new DGNPoint(xA + size, yA), new DGNPoint(xA + size, yA - size),
								new DGNPoint(xA, yA - size), new DGNPoint(xA, yA));

						// símbolo
						String symbol = quad.getSymbol();
						if (!"".equals(symbol))
							DGNwriter.writeText(hNewDGN, DGNwriter.COLOR_DEFAULT, symbol, 0, DGNlib.DGNJ_CENTER_TOP,
									120., 120., 0., null, x0 + (b ? 1 : 3) * grade.width,
									-(y0 + (b ? 5 : 3) * grade.height), 0., null);

						// conectores
						// 1
						DGNwriter.writeLine(hNewDGN, new DGNPoint(x0, -y0),
								new DGNPoint(x0 + (b ? 0 : 1) * grade.height, -y0 - (b ? 1 : 0) * grade.height));
						// 2
						DGNwriter.writeLine(hNewDGN,
								new DGNPoint(x0 + (b ? 0 : 9) * grade.height, -y0 - (b ? 9 : 0) * grade.height),
								new DGNPoint(x0 + (b ? 0 : 8) * grade.height, -y0 - (b ? 8 : 0) * grade.height));
						// 3
						DGNwriter.writeLine(hNewDGN,
								new DGNPoint(x0 + (b ? 5 : 0) * grade.height, -y0 - (b ? 0 : 5) * grade.height),
								new DGNPoint(x0 + (b ? 5 : 1) * grade.height, -y0 - (b ? 1 : 5) * grade.height));
						// 4
						DGNwriter.writeLine(hNewDGN,
								new DGNPoint(x0 + (b ? 5 : 9) * grade.height, -y0 - (b ? 9 : 5) * grade.height),
								new DGNPoint(x0 + (b ? 5 : 8) * grade.height, -y0 - (b ? 8 : 5) * grade.height));
					} else if (comp instanceof MagCouple) {
						// TODO
					}
				}
			}
		}

		// foreground
		if (fore != null)
			draw(hNewDGN, fore, 1f, 0, 0, ground.width, ground.height);

		// Close it.
		hNewDGN.close();
	}

	/**
	 * Função que desenha a conexão do 'corpo do elemento' (terminal) até o nó
	 * 
	 * @param hNewDGN
	 * @param no      nó
	 * @param cp      índice do ponto do nó ao qual este terminal está conectado
	 * @param x       abscissa do elemento
	 * @param y       ordenada do elemento
	 * @param tpx     abscissa do terminal nas coordenadas do elemento
	 * @param tpy     ordenada do terminal nas coordenadas do elemento
	 * @param grade
	 */
	private static void drawConnection(DGNfile hNewDGN, No no, int cp, int x, int y, int tpx, int tpy,
			Dimension grade) {
		no.setIndex(cp);
		DGNwriter.writeLine(hNewDGN, new DGNPoint(no.getX() * grade.width, -no.getY() * grade.height),
				new DGNPoint((x + tpx) * grade.width, -(y + tpy) * grade.height));
	}

	/**
	 * 
	 * @param hNewDGN
	 * @param insts
	 * @param mult
	 * @param x0
	 * @param y0
	 * @param width
	 * @param height
	 */
	public static void draw(DGNfile hNewDGN, Collection<Object[]> insts, float mult, int x0, int y0, int width,
			int height) {
		for (Object[] inst : insts) {
			LID.DrawAction da = LID.DrawAction.valueOf(((String) inst[0]).toUpperCase());
			// TODO cores
			switch (da) {
			case LINE:
				if (inst[6] != null) // TODO acertar o dash
					DGNwriter.writeLine(hNewDGN, DGNwriter.COLOR_DEFAULT, 1, DGNlib.DGNS_SHORT_DASH,
							new DGNPoint((x0 + ((int) inst[1]) * mult) * width,
									-(y0 + ((int) inst[2]) * mult) * height),
							new DGNPoint((x0 + ((int) inst[3]) * mult) * width,
									-(y0 + ((int) inst[4]) * mult) * height));
				else
					DGNwriter.writeLine(hNewDGN,
							new DGNPoint((x0 + ((int) inst[1]) * mult) * width,
									-(y0 + ((int) inst[2]) * mult) * height),
							new DGNPoint((x0 + ((int) inst[3]) * mult) * width,
									-(y0 + ((int) inst[4]) * mult) * height));
				break;
			case RECT:
				float xA = (x0 + ((int) inst[1]) * mult) * width;
				float yA = -(y0 + ((int) inst[2]) * mult) * height;
				float xB = xA + (((int) inst[3]) * mult) * width;
				float yB = yA - (((int) inst[4]) * mult) * height;
				DGNwriter.writeShape(hNewDGN, DGNwriter.COLOR_DEFAULT, new DGNPoint(xA, yA), new DGNPoint(xB, yA),
						new DGNPoint(xB, yB), new DGNPoint(xA, yB), new DGNPoint(xA, yA));
				break;
			case PATH:
				if ("visible".equals(inst[1])) {
					// TODO impor as restrições (tem de ser somente M-A)
					String[] d = ((String) inst[2]).trim().split("\\s+");

					int rx = Integer.parseInt(d[4]);
					int ry = Integer.parseInt(d[5]);

					double[] arcData = Elipse.getArc(Integer.parseInt(d[1]), Integer.parseInt(d[2]),
							Integer.parseInt(d[9]), Integer.parseInt(d[10]), rx, ry, Integer.parseInt(d[6]),
							"1".equals(d[7]), "1".equals(d[8]));

					DGNwriter.writeArc(hNewDGN, DGNwriter.COLOR_DEFAULT, (arcData[0] * mult + x0) * width,
							-(arcData[1] * mult + y0) * height, 0, rx * mult * width, ry * mult * height,
							Math.toDegrees(arcData[2]), Math.toDegrees(arcData[3]), 0, null);
				}
				break;
			case TEXT:
				float sizeFactor = (float) inst[4];
				DGNwriter.writeText(hNewDGN, DGNwriter.COLOR_DEFAULT, (String) inst[inst.length - 1], 0,
						DGNlib.DGNJ_CENTER_TOP, sizeFactor * 6., sizeFactor * 6., 0., null,
						(x0 + ((int) inst[1]) * mult) * width, -(y0 + ((int) inst[2]) * mult) * height, 0., null);
				break;
			default:
				System.err.println("DGNcircuit:\tainda não fiz...");
				break;
			}
		}
	}
}

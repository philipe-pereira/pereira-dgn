package br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils;

import java.util.Arrays;

import br.com.pereiraeng.core.BinaryUtils;
import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.elm.DGNElem;

public class DGNlib {

	/**
	 * double é que igual ao 'inteiro' 0x80000000 (este número não pode ser expresso
	 * como um <code>int</code>, uma vez que está fora no intervalo (o número
	 * hexadecimal acima é igual a -2147483648, ou seja, à
	 * {@link Integer#MIN_VALUE})
	 */
	public static final double MAX_INT = -1. * Integer.MIN_VALUE;

	/* Structure types */

	/**
	 * DGNElemCore style: Element uses DGNElemCore structure
	 */
	public static final int DGNST_CORE = 1;

	/**
	 * DGNElemCore style: Element uses DGNElemMultiPoint structure
	 */
	public static final int DGNST_MULTIPOINT = 2;

	/**
	 * DGNElemCore style: Element uses DGNElemColorTable structure
	 */
	public static final int DGNST_COLORTABLE = 3;

	/**
	 * DGNElemCore style: Element uses DGNElemTCB structure
	 */
	public static final int DGNST_TCB = 4;

	/**
	 * DGNElemCore style: Element uses DGNElemArc structure
	 */
	public static final int DGNST_ARC = 5;

	/**
	 * DGNElemCore style: Element uses DGNElemText structure
	 */
	public static final int DGNST_TEXT = 6;

	/**
	 * DGNElemCore style: Element uses DGNElemComplexHeader structure
	 */
	public static final int DGNST_COMPLEX_HEADER = 7;

	/**
	 * DGNElemCore style: Element uses DGNElemCellHeader structure
	 */
	public static final int DGNST_CELL_HEADER = 8;

	/**
	 * DGNElemCore style: Element uses DGNElemTagValue structure
	 */
	public static final int DGNST_TAG_VALUE = 9;

	/**
	 * DGNElemCore style: Element uses DGNElemTagSet structure
	 */
	public static final int DGNST_TAG_SET = 10;

	/**
	 * DGNElemCore style: Element uses DGNElemCellLibrary structure
	 */
	public static final int DGNST_CELL_LIBRARY = 11;

	/**
	 * DGNElemCore style: Element uses DGNElemCone structure
	 */
	public static final int DGNST_CONE = 12;

	/**
	 * DGNElemCore style: Element uses DGNElemTextNode structure
	 */
	public static final int DGNST_TEXT_NODE = 13;

	/**
	 * DGNElemCore style: Element uses DGNElemBSplineSurfaceHeader structure
	 */
	public static final int DGNST_BSPLINE_SURFACE_HEADER = 14;

	/**
	 * DGNElemCore style: Element uses DGNElemBSplineCurveHeader structure
	 */
	public static final int DGNST_BSPLINE_CURVE_HEADER = 15;

	/**
	 * DGNElemCore style: Element uses DGNElemBSplineSurfaceBoundary structure
	 */
	public static final int DGNST_BSPLINE_SURFACE_BOUNDARY = 16;

	/**
	 * DGNElemCore style: Element uses DGNElemKnotWeight structure
	 */
	public static final int DGNST_KNOT_WEIGHT = 17;

	/**
	 * DGNElemCore style: Element uses DGNElemSharedCellDefn structure
	 */
	public static final int DGNST_SHARED_CELL_DEFN = 18;

	/**
	 * DGNElemCore style: Element uses DGNElemSharedCellElem structure
	 */
	public static final int DGNST_SHARED_CELL_ELEM = 19;

	/* Element types */

	public static final int DGNT_CELL_LIBRARY = 1;

	public static final int DGNT_CELL_HEADER = 2;
	public static final int DGNT_LINE = 3;
	public static final int DGNT_LINE_STRING = 4;
	public static final int DGNT_GROUP_DATA = 5;
	public static final int DGNT_SHAPE = 6;
	public static final int DGNT_TEXT_NODE = 7;
	public static final int DGNT_DIGITIZER_SETUP = 8;
	public static final int DGNT_TCB = 9;
	public static final int DGNT_LEVEL_SYMBOLOGY = 10;
	public static final int DGNT_CURVE = 11;
	public static final int DGNT_COMPLEX_CHAIN_HEADER = 12;

	/**
	 * Complex shape
	 */
	public static final int DGNT_COMPLEX_SHAPE_HEADER = 14;
	public static final int DGNT_ELLIPSE = 15;
	public static final int DGNT_ARC = 16;
	public static final int DGNT_TEXT = 17;
	public static final int DGNT_3DSURFACE_HEADER = 18;
	public static final int DGNT_3DSOLID_HEADER = 19;
	public static final int DGNT_BSPLINE_POLE = 21;
	public static final int DGNT_POINT_STRING = 22;
	public static final int DGNT_BSPLINE_SURFACE_HEADER = 24;
	public static final int DGNT_BSPLINE_SURFACE_BOUNDARY = 25;
	public static final int DGNT_BSPLINE_KNOT = 26;
	public static final int DGNT_BSPLINE_CURVE_HEADER = 27;
	public static final int DGNT_BSPLINE_WEIGHT_FACTOR = 28;
	public static final int DGNT_CONE = 23;
	public static final int DGNT_SHARED_CELL_DEFN = 34;
	public static final int DGNT_SHARED_CELL_ELEM = 35;
	public static final int DGNT_TAG_VALUE = 37;
	public static final int DGNT_APPLICATION_ELEM = 66;

	/* Line Styles */

	public static final int DGNS_SOLID = 0;
	public static final int DGNS_DOTTED = 1;
	public static final int DGNS_MEDIUM_DASH = 2;
	public static final int DGNS_LONG_DASH = 3;
	public static final int DGNS_DOT_DASH = 4;
	public static final int DGNS_SHORT_DASH = 5;
	public static final int DGNS_DASH_DOUBLE_DOT = 6;
	public static final int DGNS_LONG_DASH_SHORT_DASH = 7;

	/* 3D Surface Types */

	public static final int DGNSUT_SURFACE_OF_PROJECTION = 0;
	public static final int DGNSUT_BOUNDED_PLANE = 1;
	public static final int DGNSUT_BOUNDED_PLANE2 = 2;
	public static final int DGNSUT_RIGHT_CIRCULAR_CYLINDER = 3;
	public static final int DGNSUT_RIGHT_CIRCULAR_CONE = 4;
	public static final int DGNSUT_TABULATED_CYLINDER = 5;
	public static final int DGNSUT_TABULATED_CONE = 6;
	public static final int DGNSUT_CONVOLUTE = 7;
	public static final int DGNSUT_SURFACE_OF_REVOLUTION = 8;
	public static final int DGNSUT_WARPED_SURFACE = 9;

	/* 3D Solid Types */

	public static final int DGNSOT_VOLUME_OF_PROJECTION = 0;
	public static final int DGNSOT_VOLUME_OF_REVOLUTION = 1;
	public static final int DGNSOT_BOUNDED_VOLUME = 2;

	/* Class */

	public static final int DGNC_PRIMARY = 0;
	public static final int DGNC_PATTERN_COMPONENT = 1;
	public static final int DGNC_CONSTRUCTION_ELEMENT = 2;
	public static final int DGNC_DIMENSION_ELEMENT = 3;
	public static final int DGNC_PRIMARY_RULE_ELEMENT = 4;
	public static final int DGNC_LINEAR_PATTERNED_ELEMENT = 5;
	public static final int DGNC_CONSTRUCTION_RULE_ELEMENT = 6;

	/* Group Data level numbers. */

	/* These are symbolic values for the typ 5 (DGNT_GROUP_DATA) */
	/* level values that have special meanings. */
	public static final int DGN_GDL_COLOR_TABLE = 1;
	public static final int DGN_GDL_NAMED_VIEW = 3;
	public static final int DGN_GDL_REF_FILE = 9;

	/* Word 17 property flags. */
	public static final int DGNPF_HOLE = 0x8000;
	public static final int DGNPF_SNAPPABLE = 0x4000;
	public static final int DGNPF_PLANAR = 0x2000;
	public static final int DGNPF_ORIENTATION = 0x1000;
	public static final int DGNPF_ATTRIBUTES = 0x0800;
	public static final int DGNPF_MODIFIED = 0x0400;
	public static final int DGNPF_NEW = 0x0200;
	public static final int DGNPF_LOCKED = 0x0100;
	public static final int DGNPF_CLASS = 0x000f;

	/* DGNElementInfo flag values. */
	public static final int DGNEIF_DELETED = 0x01;
	public static final int DGNEIF_COMPLEX = 0x02;

	/* Justifications */
	public static final int DGNJ_LEFT_TOP = 0;
	public static final int DGNJ_LEFT_CENTER = 1;
	public static final int DGNJ_LEFT_BOTTOM = 2;

	/**
	 * text node header only
	 */
	public static final int DGNJ_LEFTMARGIN_TOP = 3;

	/**
	 * text node header only
	 */
	public static final int DGNJ_LEFTMARGIN_CENTER = 4;

	/**
	 * text node header only
	 */
	public static final int DGNJ_LEFTMARGIN_BOTTOM = 5;
	public static final int DGNJ_CENTER_TOP = 6;
	public static final int DGNJ_CENTER_CENTER = 7;
	public static final int DGNJ_CENTER_BOTTOM = 8;
	/**
	 * text node header only
	 */
	public static final int DGNJ_RIGHTMARGIN_TOP = 9;
	/**
	 * text node header only
	 */
	public static final int DGNJ_RIGHTMARGIN_CENTER = 10;
	/**
	 * text node header only
	 */
	public static final int DGNJ_RIGHTMARGIN_BOTTOM = 11;
	public static final int DGNJ_RIGHT_TOP = 12;
	public static final int DGNJ_RIGHT_CENTER = 13;
	public static final int DGNJ_RIGHT_BOTTOM = 14;

	/* DGN file reading options. */
	public static final int DGNO_CAPTURE_RAW_DATA = 0x01;

	/* Known attribute linkage types, including my synthetic ones. */
	public static final int DGNLT_DMRS = 0x0000;
	public static final int DGNLT_INFORMIX = 0x3848;
	public static final int DGNLT_ODBC = 0x5e62;
	public static final int DGNLT_ORACLE = 0x6091;
	public static final int DGNLT_RIS = 0x71FB;
	public static final int DGNLT_SYBASE = 0x4f58;
	public static final int DGNLT_XBASE = 0x1971;
	public static final int DGNLT_SHAPE_FILL = 0x0041;
	public static final int DGNLT_ASSOC_ID = 0x7D2F;

	/* File creation options. */
	public static final int DGNCF_USE_SEED_UNITS = 0x01;
	public static final int DGNCF_USE_SEED_ORIGIN = 0x02;
	public static final int DGNCF_COPY_SEED_FILE_COLOR_TABLE = 0x04;
	public static final int DGNCF_COPY_WHOLE_SEED_FILE = 0x08;

	/* B-Spline Curve flags. Also used for U-direction of surfaces */
	public static final int DGNBSC_CURVE_DISPLAY = 0x10;
	public static final int DGNBSC_POLY_DISPLAY = 0x20;
	public static final int DGNBSC_RATIONAL = 0x40;
	public static final int DGNBSC_CLOSED = 0x80;

	/* B-Spline Curve flags for V-direction of surfaces. */
	public static final int DGNBSS_ARC_SPACING = 0x40;
	public static final int DGNBSS_CLOSED = 0x80;

	/**
	 * Convert type to name.
	 *
	 * Returns a human readable name for an element type such as DGNT_LINE.
	 *
	 * @param nType the DGNT_* type code to translate.
	 *
	 * @return a pointer to an internal string with the translation. This string
	 *         should not be modified or freed.
	 */
	public static String DGNTypeToName(int nType) {
		switch (nType) {
		case DGNlib.DGNT_CELL_LIBRARY:
			return "Cell Library";
		case DGNlib.DGNT_CELL_HEADER:
			return "Cell Header";
		case DGNlib.DGNT_LINE:
			return "Line";
		case DGNlib.DGNT_LINE_STRING:
			return "Line String";
		case DGNlib.DGNT_POINT_STRING:
			return "Point String";
		case DGNlib.DGNT_GROUP_DATA:
			return "Group Data";
		case DGNlib.DGNT_SHAPE:
			return "Shape";
		case DGNlib.DGNT_TEXT_NODE:
			return "Text Node";
		case DGNlib.DGNT_DIGITIZER_SETUP:
			return "Digitizer Setup";
		case DGNlib.DGNT_TCB:
			return "TCB";
		case DGNlib.DGNT_LEVEL_SYMBOLOGY:
			return "Level Symbology";
		case DGNlib.DGNT_CURVE:
			return "Curve";
		case DGNlib.DGNT_COMPLEX_CHAIN_HEADER:
			return "Complex Chain Header";
		case DGNlib.DGNT_COMPLEX_SHAPE_HEADER:
			return "Complex Shape Header";
		case DGNlib.DGNT_ELLIPSE:
			return "Ellipse";
		case DGNlib.DGNT_ARC:
			return "Arc";
		case DGNlib.DGNT_TEXT:
			return "Text";
		case DGNlib.DGNT_BSPLINE_POLE:
			return "B-Spline Pole";
		case DGNlib.DGNT_BSPLINE_SURFACE_HEADER:
			return "B-Spline Surface Header";
		case DGNlib.DGNT_BSPLINE_SURFACE_BOUNDARY:
			return "B-Spline Surface Boundary";
		case DGNlib.DGNT_BSPLINE_KNOT:
			return "B-Spline Knot";
		case DGNlib.DGNT_BSPLINE_CURVE_HEADER:
			return "B-Spline Curve Header";
		case DGNlib.DGNT_BSPLINE_WEIGHT_FACTOR:
			return "B-Spline Weight Factor";
		case DGNlib.DGNT_APPLICATION_ELEM:
			return "Application Element";
		case DGNlib.DGNT_SHARED_CELL_DEFN:
			return "Shared Cell Definition";
		case DGNlib.DGNT_SHARED_CELL_ELEM:
			return "Shared Cell Element";
		case DGNlib.DGNT_TAG_VALUE:
			return "Tag Value";
		case DGNlib.DGNT_CONE:
			return "Cone";
		case DGNlib.DGNT_3DSURFACE_HEADER:
			return "3D Surface Header";
		case DGNlib.DGNT_3DSOLID_HEADER:
			return "3D Solid Header";
		default:
			return String.valueOf(nType);
		}
	}

	/**
	 * Returns requested linkage raw data.
	 *
	 * A pointer to the raw data for the requested attribute linkage is returned as
	 * well as (potentially) various information about the linkage including the
	 * linkage type, database entity number and MSLink value, and the length of the
	 * raw linkage data in bytes.
	 *
	 * If the requested linkage (iIndex) does not exist a value of zero is returned.
	 *
	 * The entity number is (loosely speaking) the index of the table within the
	 * current database to which the MSLINK value will refer. The entity number
	 * should be used to lookup the table name in the MSCATALOG table. The MSLINK
	 * value is the key value for the record in the target table.
	 *
	 * @param hDGN          the file from which the element originated.
	 * @param psElement     the element to report on.
	 * @param iIndex        the zero based index of the linkage to fetch.
	 * @param pnLinkageType variable to return linkage type. This may be one of the
	 *                      predefined DGNLT_ values or a different value. This
	 *                      pointer may be null.
	 * @param pnEntityNum   variable to return the entity number in or null if not
	 *                      required.
	 * @param pnMSLink      variable to return the MSLINK value in, or null if not
	 *                      required.
	 * @param pnLength      variable to returned the linkage size in bytes or null.
	 * 
	 * @return pointer to raw internal linkage data. This data should not be altered
	 *         or freed. null returned on failure.
	 */

	public static byte[] DGNGetLinkage(DGNfile hDGN, DGNElem psElement, int iIndex,
			int[] linkageTypeEntityNumMSLinkLength) {
		int nAttrOffset;
		int iLinkage, nLinkSize;

		for (iLinkage = 0, nAttrOffset = 0; (nLinkSize = DGNGetAttrLinkSize(psElement,
				nAttrOffset)) != 0; iLinkage++, nAttrOffset += nLinkSize) {
			if (iLinkage == iIndex) {
				int nLinkageType = 0, nEntityNum = 0, nMSLink = 0;
				if (nLinkSize <= 4)
					throw new IllegalArgumentException("!02");

				if (psElement.attr_data[nAttrOffset] == 0x00 && (psElement.attr_data[nAttrOffset + 1] == 0x00
						|| psElement.attr_data[nAttrOffset + 1] == 0x80)) {
					nLinkageType = DGNlib.DGNLT_DMRS;
					nEntityNum = BinaryUtils.unsignedShort2int(psElement.attr_data, nAttrOffset + 2);
					nMSLink = (psElement.attr_data[nAttrOffset + 4] & 0xFF)
							+ (psElement.attr_data[nAttrOffset + 5] & 0xFF) * 256
							+ (psElement.attr_data[nAttrOffset + 6] & 0xFF) * 65536;
				} else
					nLinkageType = psElement.attr_data[nAttrOffset + 2] + psElement.attr_data[nAttrOffset + 3] * 256;

				// Possibly an external database linkage?
				if (nLinkSize == 16 && nLinkageType != DGNlib.DGNLT_SHAPE_FILL) {
					nEntityNum = BinaryUtils.unsignedShort2int(psElement.attr_data, nAttrOffset + 6);
					nMSLink = (psElement.attr_data[nAttrOffset + 8] & 0xFF)
							+ (psElement.attr_data[nAttrOffset + 9] & 0xFF) * 256
							+ (psElement.attr_data[nAttrOffset + 10] & 0xFF) * 65536
							+ (psElement.attr_data[nAttrOffset + 11] & 0xFF) * 16777216;

				}

				linkageTypeEntityNumMSLinkLength[0] = nLinkageType;
				linkageTypeEntityNumMSLinkLength[1] = nEntityNum;
				linkageTypeEntityNumMSLinkLength[2] = nMSLink;
				linkageTypeEntityNumMSLinkLength[3] = nLinkSize;

				return Arrays.copyOfRange(psElement.attr_data, nAttrOffset, psElement.attr_data.length);
			}
		}

		return null;
	}

	/**
	 * Get attribute linkage size.
	 *
	 * Returns the size, in bytes, of the attribute linkage starting at byte offset
	 * nOffset. On failure a value of 0 is returned.
	 *
	 * @param hDGN      the file from which the element originated.
	 * @param psElement the element to report on.
	 * @param nOffset   byte offset within attribute data of linkage to check.
	 *
	 * @return size of linkage in bytes, or zero.
	 */

	private static int DGNGetAttrLinkSize(DGNElem psElement, int nOffset) {
		if (psElement.attr_bytes < nOffset + 4)
			return 0;

		/* DMRS Linkage */
		if ((psElement.attr_data[nOffset] == 0 && psElement.attr_data[nOffset + 1] == 0)
				|| (psElement.attr_data[nOffset] == 0 && psElement.attr_data[nOffset + 1] == 0x80))
			return 8;

		/* If low order bit of second byte is set, first byte is length */
		if ((psElement.attr_data[nOffset + 1] & 0x10) != 0)
			return (0xFF & psElement.attr_data[nOffset]) * 2 + 2;

		/* unknown */
		return 0;
	}

	public static void DGNRotationToQuaternion(double dfRotation, int[] panQuaternion) {
		double dfRadianRot = (dfRotation / 180.0) * Math.PI;

		panQuaternion[0] = (int) (Math.cos(-dfRadianRot / 2.0) * 2147483647);
		panQuaternion[1] = 0;
		panQuaternion[2] = 0;
		panQuaternion[3] = (int) (Math.sin(-dfRadianRot / 2.0) * 2147483647);
	}
}

package br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collection;

import br.com.pereiraeng.core.BinaryUtils;
import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.DGNPoint;
import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.elm.DGNElem;
import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.elm.DGNElemArc;
import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.elm.DGNElemCellHeader;
import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.elm.DGNElemColorTable;
import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.elm.DGNElemComplexHeader;
import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.elm.DGNElemCone;
import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.elm.DGNElemMultiPoint;
import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.elm.DGNElemText;
import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.elm.DGNElementInfo;

public class DGNwriter {

	// objetos já foram criados (e não queremos perturbá-los)

	/**
	 * 
	 * @param newFile
	 * @param seed
	 * @param elements
	 * @param copy     <code>true</code> se for para fazer uma cópia do elementos
	 *                 antes de escrevê-lo no arquivo (isso pode ser necessário caso
	 *                 não se queira perturbar os elementos fontes durante o
	 *                 processo de escrita)
	 */
	public static void write(File newFile, File seed, Collection<DGNElem> elements, boolean copy) {
		DGNfile hNewDGN = DGNwriter.DGNCreate(newFile.getAbsolutePath(), seed.getAbsolutePath(),
				DGNlib.DGNCF_USE_SEED_UNITS | DGNlib.DGNCF_USE_SEED_ORIGIN, 0.0, 0.0, 0.0, 0, 0, "", "");

		if (hNewDGN == null)
			return;

		long offset = hNewDGN.tell();
		for (DGNElem e : elements) {
			DGNElem ne = null;
			if (copy)
				ne = (DGNElem) e.clone();
			else
				ne = e;

			ne.offset = (int) offset;
			offset += ne.raw_bytes;

			DGNwriter.DGNWriteElement(hNewDGN, ne);
		}

		// Close it.
		hNewDGN.close();
	}

	// ---------------------------------------------------------------------------

	public static final int COLOR_DEFAULT = 8;

	// objetos tem de ser criados a partir de seus parâmetros

	public static void writeLine(DGNfile hDGN, DGNPoint dgnPoint1, DGNPoint dgnPoint2) {
		writeLine(hDGN, COLOR_DEFAULT, 1, DGNlib.DGNS_SOLID, dgnPoint1, dgnPoint2);
	}

	public static void writeLine(DGNfile hDGN, int colorIndex, int weight, int style, DGNPoint dgnPoint1,
			DGNPoint dgnPoint2) {
		DGNElem element = DGNwriter.DGNCreateMultiPointElem(hDGN, DGNlib.DGNT_LINE, 2, dgnPoint1, dgnPoint2);
		updateWrite(hDGN, element, colorIndex, weight, style);
	}

	public static void writeMultiLine(DGNfile hDGN, DGNPoint... dgnPoints) {
		writeMultiLine(hDGN, COLOR_DEFAULT, 1, DGNlib.DGNS_SOLID, dgnPoints);
	}

	public static void writeMultiLine(DGNfile hDGN, int colorIndex, int weight, int style, DGNPoint... dgnPoints) {
		DGNElem element = DGNwriter.DGNCreateMultiPointElem(hDGN, DGNlib.DGNT_LINE_STRING, dgnPoints.length, dgnPoints);
		updateWrite(hDGN, element, colorIndex, weight, style);
	}

	public static void writeCurve(DGNfile hDGN, int colorIndex, DGNPoint... dgnPoints) {
		DGNElem element = DGNwriter.DGNCreateMultiPointElem(hDGN, DGNlib.DGNT_CURVE, dgnPoints.length, dgnPoints);
		updateWrite(hDGN, element, colorIndex, 1, DGNlib.DGNS_SOLID);
	}

	public static void writeShape(DGNfile hDGN, int colorIndex, DGNPoint... dgnPoints) {
		DGNElem element = DGNwriter.DGNCreateMultiPointElem(hDGN, DGNlib.DGNT_SHAPE, dgnPoints.length, dgnPoints);
		updateWrite(hDGN, element, colorIndex, 1, DGNlib.DGNS_SOLID);
	}

	public static void writeArc(DGNfile hDGN, int colorIndex, double dfOriginX, double dfOriginY, double dfOriginZ,
			double dfPrimaryAxis, double dfSecondaryAxis, double dfStartAngle, double dfSweepAngle, double dfRotation,
			int[] panQuaternion) {
		DGNElem element = DGNwriter.DGNCreateArcElem(hDGN, DGNlib.DGNT_ARC, dfOriginX, dfOriginY, dfOriginZ,
				dfPrimaryAxis, dfSecondaryAxis, dfStartAngle, dfSweepAngle, dfRotation, panQuaternion);
		updateWrite(hDGN, element, colorIndex, 1, DGNlib.DGNS_SOLID);
	}

	public static void writeEllipse(DGNfile hDGN, int colorIndex, double dfOriginX, double dfOriginY, double dfOriginZ,
			double dfPrimaryAxis, double dfSecondaryAxis, double dfStartAngle, double dfSweepAngle, double dfRotation,
			int[] panQuaternion) {
		DGNElem element = DGNwriter.DGNCreateArcElem(hDGN, DGNlib.DGNT_ELLIPSE, dfOriginX, dfOriginY, dfOriginZ,
				dfPrimaryAxis, dfSecondaryAxis, dfStartAngle, dfSweepAngle, dfRotation, panQuaternion);
		updateWrite(hDGN, element, colorIndex, 1, DGNlib.DGNS_SOLID);
	}

	public static void writeText(DGNfile hDGN, int colorIndex, String pszText, int nFontId, int nJustification,
			double dfLengthMult, double dfHeightMult, double dfRotation, int[] panQuaternion, double dfOriginX,
			double dfOriginY, double dfOriginZ, int[][] linkage) {
		DGNElem element = DGNwriter.DGNCreateTextElem(hDGN, pszText, nFontId, nJustification, dfLengthMult,
				dfHeightMult, dfRotation, panQuaternion, dfOriginX, dfOriginY, dfOriginZ);
		if (linkage != null)
			for (int i = 0; i < linkage.length; i++)
				DGNwriter.DGNAddMSLink(hDGN, element, linkage[i][0], linkage[i][1], linkage[i][2]);
		updateWrite(hDGN, element, colorIndex, 1, DGNlib.DGNS_SOLID);
	}

	public static void writeComplexGroup(DGNfile hDGN, DGNElem... psMembers) {
		DGNElem element = DGNwriter.DGNCreateComplexHeaderFromGroup(hDGN, DGNlib.DGNT_COMPLEX_SHAPE_HEADER,
				psMembers.length, psMembers);
		DGNwriter.DGNUpdateElemCore(hDGN, element, 9, 0, 3, 1, DGNlib.DGNS_SOLID);
		DGNwriter.DGNAddShapeFillInfo(hDGN, element, 7);

		DGNwriter.DGNWriteElement(hDGN, element);
		for (int i = 0; i < psMembers.length; i++)
			DGNwriter.DGNWriteElement(hDGN, psMembers[i]);
	}

	public static void writeCellGroup(DGNfile hNewDGN, String name, DGNPoint psOrigin, double dfXScale, double dfYScale,
			double dfRotation, DGNElem... psMembers) {
		DGNElem element = DGNwriter.DGNCreateCellHeaderFromGroup(hNewDGN, name, (short) 1, null, psMembers.length,
				psMembers, psOrigin, dfXScale, dfYScale, dfRotation);

		DGNwriter.DGNWriteElement(hNewDGN, element);
		for (int i = 0; i < psMembers.length; i++)
			DGNwriter.DGNWriteElement(hNewDGN, psMembers[i]);
	}

	private static void updateWrite(DGNfile hDGN, DGNElem element, int colorIndex, int weight, int style) {
		DGNwriter.DGNUpdateElemCore(hDGN, element, 15, 0, colorIndex, weight, style);
		DGNwriter.DGNWriteElement(hDGN, element);
	}

	// ---------------------------------------------------------------------------

	/**
	 * Resize an existing element.
	 *
	 * If the new size is the same as the old nothing happens.
	 *
	 * Otherwise, the old element in the file is marked as deleted, and the
	 * DGNElem.offset and element_id are set to -1 indicating that the element
	 * should be written to the end of file when next written by DGNWriteElement().
	 * The internal raw data buffer is updated to the new size.
	 * 
	 * Only elements with "raw_data" loaded may be moved.
	 *
	 * In normal use the DGNResizeElement() call would be called on a previously
	 * loaded element, and afterwards the raw_data would be updated before calling
	 * DGNWriteElement(). If DGNWriteElement() isn't called after DGNResizeElement()
	 * then the element will be lost having been marked as deleted in it's old
	 * position but never written at the new location.
	 *
	 * @param hDGN      the DGN file on which the element lives.
	 * @param psElement the element to alter.
	 * @param nNewSize  the desired new size of the element in bytes. Must be a
	 *                  multiple of 2.
	 *
	 * @return true on success, or false on error.
	 */
	static boolean DGNResizeElement(DGNfile hDGN, DGNElem psElement, int nNewSize) {
		DGNfile psDGN = (DGNfile) hDGN;

		// Check various conditions.
		if (psElement.raw_bytes == 0 || psElement.raw_bytes != psElement.size) {
			System.err.printf("Raw bytes not loaded, or not matching element size.");
			return false;
		}

		if (nNewSize % 2 == 1) {
			System.err.printf("DGNResizeElement(%d): can't change to odd (not divisible by two) size.", nNewSize);
			return false;
		}

		if (nNewSize == psElement.raw_bytes)
			return true;

		// Mark the existing element as deleted if the element has to move to
		// the end of the file.

		if (psElement.offset != -1) {
			long nOldFLoc = psDGN.tell();
			byte[] abyLeader = new byte[2];

			if (!psDGN.seek(psElement.offset) || psDGN.read(abyLeader, abyLeader.length, 1) != 1) {
				System.err.printf(
						"Failed seek or read when trying to mark existing\nelement as deleted in DGNResizeElement()\n");
				return false;
			}

			abyLeader[1] |= 0x80;

			if (!psDGN.seek(psElement.offset) || psDGN.write(abyLeader, abyLeader.length, 1) != 1) {
				System.err.println(
						"Failed seek or write when trying to mark existing\nelement as deleted in DGNResizeElement()\n");
				return false;
			}

			psDGN.seek(nOldFLoc);

			if (psElement.element_id != -1 && psDGN.index_built)
				psDGN.element_index[psElement.element_id].flags |= DGNlib.DGNEIF_DELETED;
		}

		psElement.offset = -1; /* move to end of file. */
		psElement.element_id = -1;

		// Set the new size information, and realloc the raw data buffer.
		psElement.size = nNewSize;
		psElement.raw_data = new byte[nNewSize];
		psElement.raw_bytes = nNewSize;

		// Update the size information within the raw buffer.
		int nWords = (nNewSize / 2) - 2;

		psElement.raw_data[2] = (byte) (nWords % 256);
		psElement.raw_data[3] = (byte) (nWords / 256);

		return true;
	}

	/**
	 * Write element to file.
	 *
	 * Only elements with "raw_data" loaded may be written. This should include
	 * elements created with the various DGNCreate*() functions, and those read from
	 * the file with the DGNO_CAPTURE_RAW_DATA flag turned on with DGNSetOptions().
	 *
	 * The passed element is written to the indicated file. If the DGNElem.offset
	 * field is -1 then the element is written at the end of the file (and
	 * offset/element are reset properly) otherwise the element is written back to
	 * the location indicated by DGNElem.offset.
	 *
	 * If the element is added at the end of the file, and if an element index has
	 * already been built, it will be updated to reference the new element.
	 *
	 * This function takes care of ensuring that the end-of-file marker is
	 * maintained after the last element.
	 *
	 * @param hDGN      the file to write the element to.
	 * @param psElement the element to write.
	 *
	 * @return true on success or false in case of failure.
	 */
	public static boolean DGNWriteElement(DGNfile hDGN, DGNElem psElement) {
		DGNfile psDGN = (DGNfile) hDGN;

		// If this element hasn't been positioned yet, place it at the end of
		// the file.
		if (psElement.offset == -1) {
			int[] nJunk = new int[2];

			// We must have an index, in order to properly assign the
			// element id of the newly written element. Ensure it is built.
			if (!psDGN.index_built)
				DGNreader.DGNBuildIndex(psDGN);

			// Read the current "last" element.
			if (!DGNreader.DGNGotoElement(hDGN, psDGN.element_count - 1))
				return false;

			if (!DGNreader.DGNLoadRawElement(psDGN, nJunk))
				return false;

			// Establish the position of the new element.
			psElement.offset = (int) psDGN.tell();
			psElement.element_id = psDGN.element_count;

			// Grow element buffer if needed.
			if (psDGN.element_count == psDGN.max_element_count) {
				psDGN.max_element_count += 500;

				psDGN.element_index = Arrays.copyOf(
						psDGN.element_index == null ? new DGNElementInfo[0] : psDGN.element_index,
						psDGN.max_element_count);
			}

			// Set up the element info
			DGNElementInfo psInfo = new DGNElementInfo();
			psDGN.element_index[psDGN.element_count] = psInfo;
			psInfo.level = (byte) psElement.level;
			psInfo.type = (byte) psElement.type;
			psInfo.stype = (byte) psElement.stype;
			psInfo.offset = psElement.offset;
			if (psElement.complex)
				psInfo.flags = DGNlib.DGNEIF_COMPLEX;
			else
				psInfo.flags = 0;

			psDGN.element_count++;
		}

		// Write out the element.
		if (!psDGN.seek(psElement.offset) || psDGN.write(psElement.raw_data, psElement.raw_bytes, 1) != 1) {
			System.err.printf("Error seeking or writing new element of %d bytes at %d.", psElement.offset,
					psElement.raw_bytes);
			return false;
		}

		psDGN.next_element_id = psElement.element_id + 1;

		// Write out the end of file 0xffff marker (if we were extending the
		// file), but push the file pointer back before this EOF when done.
		if (psDGN.next_element_id == psDGN.element_count) {
			byte[] abyEOF = new byte[2];

			abyEOF[0] = (byte) 0xff;
			abyEOF[1] = (byte) 0xff;

			psDGN.write(abyEOF, 2, 1);
			psDGN.seek(psDGN.tell() - 2L);
		}

		return true;
	}

	/**
	 * Create new DGN file.
	 *
	 * This function will create a new DGN file based on the provided seed file, and
	 * return a handle on which elements may be read and written.
	 *
	 * The following creation flags may be passed:
	 * <ul>
	 * <li>DGNCF_USE_SEED_UNITS: The master and subunit resolutions and names from
	 * the seed file will be used in the new file. The nMasterUnitPerSubUnit,
	 * nUORPerSubUnit, pszMasterUnits, and pszSubUnits arguments will be ignored.
	 * <li>DGNCF_USE_SEED_ORIGIN: The origin from the seed file will be used and the
	 * X, Y and Z origin passed into the call will be ignored.
	 * <li>DGNCF_COPY_SEED_FILE_COLOR_TABLE: Should the first color table occuring
	 * in the seed file also be copied?
	 * <li>DGNCF_COPY_WHOLE_SEED_FILE: By default only the first three elements
	 * (TCB, Digitizer Setup and Level Symbology) are copied from the seed file. If
	 * this flag is provided the entire seed file is copied verbatim (with the TCB
	 * origin and units possibly updated).
	 * </ul>
	 * 
	 * @param pszNewFilename         the filename to create. If it already exists it
	 *                               will be overwritten.
	 * @param pszSeedFile            the seed file to copy header from.
	 * @param nCreationFlags         An ORing of DGNCF_* flags that are to take
	 *                               effect.
	 * @param dfOriginX              the X origin for the file.
	 * @param dfOriginY              the Y origin for the file.
	 * @param dfOriginZ              the Z origin for the file.
	 * @param nSubUnitsPerMasterUnit the number of subunits in one master unit.
	 * @param nUORPerSubUnit         the number of UOR (units of resolution) per
	 *                               subunit.
	 * @param pszMasterUnits         the name of the master units (2 characters).
	 * @param pszSubUnits            the name of the subunits (2 characters).
	 */
	public static DGNfile DGNCreate(String pszNewFilename, String pszSeedFile, int nCreationFlags, double dfOriginX,
			double dfOriginY, double dfOriginZ, int nSubUnitsPerMasterUnit, int nUORPerSubUnit, String pszMasterUnits,
			String pszSubUnits) {
		DGNfile psSeed, psDGN;
		RandomAccessFile fpNew = null;
		DGNElem psSrcTCB;

		// Open seed file, and read TCB element.
		psSeed = DGNfile.DGNOpen(pszSeedFile, false);
		if (psSeed == null)
			return null;

		psSeed.options = DGNlib.DGNO_CAPTURE_RAW_DATA;

		psSrcTCB = DGNreader.DGNReadElement(psSeed);

		if (psSrcTCB.raw_bytes < 1536)
			throw new IllegalArgumentException("!200");

		// Open output file.
		try {
			fpNew = new RandomAccessFile(pszNewFilename, "rw");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		if (fpNew == null) {
			System.err.printf("Failed to open output file: %s", pszNewFilename);
			return null;
		}

		// Modify TCB appropriately for the output file.
		byte[] pabyRawTCB = new byte[psSrcTCB.raw_bytes];

		System.arraycopy(psSrcTCB.raw_data, 0, pabyRawTCB, 0, psSrcTCB.raw_bytes);

		if (!((nCreationFlags & DGNlib.DGNCF_USE_SEED_UNITS) > 0)) {
			System.arraycopy(pszMasterUnits.getBytes(), 0, pabyRawTCB, 1120, 2);
			System.arraycopy(pszSubUnits.getBytes(), 0, pabyRawTCB, 1122, 2);

			BinaryUtils.getBytesME(nUORPerSubUnit, pabyRawTCB, 1116);
			BinaryUtils.getBytesME(nSubUnitsPerMasterUnit, pabyRawTCB, 1112);
		} else {
			nUORPerSubUnit = BinaryUtils.getIntME(pabyRawTCB, 1116);
			nSubUnitsPerMasterUnit = BinaryUtils.getIntME(pabyRawTCB, 1112);
		}

		if (!((nCreationFlags & DGNlib.DGNCF_USE_SEED_ORIGIN) > 0)) {
			dfOriginX = (nUORPerSubUnit * nSubUnitsPerMasterUnit);
			dfOriginY = (nUORPerSubUnit * nSubUnitsPerMasterUnit);
			dfOriginZ = (nUORPerSubUnit * nSubUnitsPerMasterUnit);

			System.arraycopy(BinaryUtils.IEEE2vax(dfOriginX), 0, pabyRawTCB, 1240, 8);
			System.arraycopy(BinaryUtils.IEEE2vax(dfOriginY), 0, pabyRawTCB, 1248, 8);
			System.arraycopy(BinaryUtils.IEEE2vax(dfOriginZ), 0, pabyRawTCB, 1256, 8);
		}

		// Write TCB and EOF to new file.
		byte[] abyEOF = new byte[2];

		try {
			fpNew.write(pabyRawTCB, 0, psSrcTCB.raw_bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}

		abyEOF[0] = (byte) 0xff;
		abyEOF[1] = (byte) 0xff;

		try {
			fpNew.write(abyEOF, 0, 2);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Close and re-open using DGN API.
		try {
			fpNew.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		psDGN = DGNfile.DGNOpen(pszNewFilename, true);

		// Now copy over elements according to options in effect.
		DGNElem psSrcElement, psDstElement;

		while ((psSrcElement = DGNreader.DGNReadElement(psSeed)) != null) {
			if ((nCreationFlags & DGNlib.DGNCF_COPY_WHOLE_SEED_FILE) > 0
					|| (psSrcElement.stype == DGNlib.DGNST_COLORTABLE
							&& (nCreationFlags & DGNlib.DGNCF_COPY_SEED_FILE_COLOR_TABLE) > 0)
					|| psSrcElement.element_id <= 2) {
				psDstElement = DGNCloneElement(psDGN, psSrcElement);
				DGNWriteElement(psDGN, psDstElement);
			}
		}

		psSeed.close();

		return psDGN;
	}

	/**
	 * Clone a retargetted element.
	 *
	 * Creates a copy of an element in a suitable form to write to a different file
	 * than that it was read from.
	 *
	 * NOTE: At this time the clone operation will fail if the source and
	 * destination file have a different origin or master/sub units.
	 *
	 * @param hDGNDst      the destination file (to which the returned element may
	 *                     be written).
	 * @param psSrcElement the element to be cloned (from hDGNSrc).
	 *
	 * @return null on failure, or an appropriately modified copy of the source
	 *         element suitable to write to hDGNDst.
	 */
	static DGNElem DGNCloneElement(DGNfile hDGNDst, DGNElem psSrcElement) {

		DGNreader.DGNLoadTCB(hDGNDst);
		DGNElem psClone = (DGNElem) psSrcElement.clone();

		// Clear location and id information.
		psClone.offset = -1;
		psClone.element_id = -1;

		return psClone;
	}

	/**
	 * Change element core values.
	 * 
	 * The indicated values in the element are updated in the structure, as well as
	 * in the raw data. The updated element is not written to disk. That must be
	 * done with DGNWriteElement(). The element must have raw_data loaded.
	 * 
	 * @param hDGN          the file on which the element belongs.
	 * @param psElement     the element to modify.
	 * @param nLevel        the new level value.
	 * @param nGraphicGroup the new graphic group value.
	 * @param nColor        the new color index.
	 * @param nWeight       the new element weight.
	 * @param nStyle        the new style value for the element.
	 *
	 * @return Returns true on success or false on failure.
	 */
	public static boolean DGNUpdateElemCore(DGNfile hDGN, DGNElem psElement, int nLevel, int nGraphicGroup, int nColor,
			int nWeight, int nStyle) {
		psElement.level = nLevel;
		psElement.graphic_group = nGraphicGroup;

		psElement.color = nColor;
		psElement.weight = nWeight;
		psElement.style = nStyle;

		return DGNUpdateElemCoreExtended(hDGN, psElement);
	}

	/**
	 * Update internal raw data representation.
	 *
	 * The raw_data representation of the passed element is updated to reflect the
	 * various core fields. The DGNElem level, type, complex, deleted,
	 * graphic_group, properties, color, weight and style values are all applied to
	 * the raw_data representation. Spatial bounds, element type specific
	 * information and attributes are not updated in the raw data.
	 *
	 * @param hDGN      the file to which the element belongs.
	 * @param psElement the element to be updated.
	 *
	 * @return true on success, or false on failure.
	 */

	static boolean DGNUpdateElemCoreExtended(DGNfile hDGN, DGNElem psElement) {
		byte[] rd = psElement.raw_data;
		int nWords = (psElement.raw_bytes / 2) - 2;

		if (psElement.raw_data == null || psElement.raw_bytes < 36)
			throw new IllegalArgumentException("!015");

		// Setup first four bytes.
		rd[0] = (byte) psElement.level;
		if (psElement.complex)
			rd[0] |= 0x80;

		rd[1] = (byte) psElement.type;
		if (psElement.deleted)
			rd[1] |= 0x80;

		rd[2] = (byte) (nWords % 256);
		rd[3] = (byte) (nWords / 256);

		// If the attribute offset hasn't been set, set it now under the
		// assumption it should point to the end of the element.
		if (psElement.raw_data[30] == 0 && psElement.raw_data[31] == 0) {
			int nAttIndex = (psElement.raw_bytes - 32) / 2;

			psElement.raw_data[30] = (byte) (nAttIndex % 256);
			psElement.raw_data[31] = (byte) (nAttIndex / 256);
		}
		// Handle the graphic properties.
		if (psElement.raw_bytes > 36 && DGNreader.DGNElemTypeHasDispHdr(psElement.type)) {
			rd[28] = (byte) (psElement.graphic_group % 256);
			rd[29] = (byte) (psElement.graphic_group / 256);
			rd[32] = (byte) (psElement.properties % 256);
			rd[33] = (byte) (psElement.properties / 256);
			rd[34] = (byte) (psElement.style | (psElement.weight << 3));
			rd[35] = (byte) psElement.color;
		}

		return true;
	}

	static void DGNInitializeElemCore(DGNfile hDGN, DGNElem psElement) {
		psElement.offset = -1;
		psElement.element_id = -1;
	}

	/**
	 * Write bounds to element raw data.
	 * 
	 * @param psInfo
	 * @param psElement
	 * @param psMin
	 * @param psMax
	 */
	public static void DGNWriteBounds(DGNfile psInfo, DGNElem psElement, DGNPoint psMin, DGNPoint psMax) {
		if (psElement.raw_bytes < 28)
			throw new IllegalArgumentException("!016");

		DGNreader.DGNInverseTransformPointToInt(psInfo, psMin, psElement.raw_data, 4);
		DGNreader.DGNInverseTransformPointToInt(psInfo, psMax, psElement.raw_data, 16);

		// convert from twos complement to "binary offset" format.

		psElement.raw_data[5] ^= 0x80;
		psElement.raw_data[9] ^= 0x80;
		psElement.raw_data[13] ^= 0x80;
		psElement.raw_data[17] ^= 0x80;
		psElement.raw_data[21] ^= 0x80;
		psElement.raw_data[25] ^= 0x80;
	}

	/**
	 * Create new multi-point element.
	 *
	 * The newly created element will still need to be written to file using
	 * DGNWriteElement(). Also the level and other core values will be defaulted.
	 * Use DGNUpdateElemCore() on the element before writing to set these values.
	 *
	 * NOTE: There are restrictions on the nPointCount for some elements. For
	 * instance, DGNT_LINE can only have 2 points. Maximum element size precludes
	 * very large numbers of points.
	 *
	 * @param hDGN        the file on which the element will eventually be written.
	 * @param nType       the type of the element to be created. It must be one of
	 *                    DGNT_LINE, DGNT_LINE_STRING, DGNT_SHAPE, DGNT_CURVE or
	 *                    DGNT_BSPLINE_POLE.
	 * @param nPointCount the number of points in the pasVertices list.
	 * @param pasVertices the list of points to be written.
	 *
	 * @return the new element (a DGNElemMultiPoint structure) or null on failure.
	 */
	public static DGNElem DGNCreateMultiPointElem(DGNfile hDGN, int nType, int nPointCount, DGNPoint... pasVertices) {
		DGNElemMultiPoint psMP;
		DGNElem psCore;
		DGNfile psDGN = (DGNfile) hDGN;
		int i;
		DGNPoint sMin, sMax;

		if (!(nType == DGNlib.DGNT_LINE || nType == DGNlib.DGNT_LINE_STRING || nType == DGNlib.DGNT_SHAPE
				|| nType == DGNlib.DGNT_CURVE || nType == DGNlib.DGNT_BSPLINE_POLE))
			throw new IllegalArgumentException("!030");

		DGNreader.DGNLoadTCB(hDGN);

		// Is this too many vertices to write to a single element?
		if (nPointCount > 101) {
			System.err.printf("Attempt to create %s element with %d points failed.\n" + "Element would be too large.",
					DGNlib.DGNTypeToName(nType), nPointCount);
			return null;
		}

		// Allocate element.
		psMP = new DGNElemMultiPoint();
		psCore = psMP;

		DGNInitializeElemCore(hDGN, psCore);
		psCore.stype = DGNlib.DGNST_MULTIPOINT;
		psCore.type = nType;

		// Set multipoint specific information in the structure.
		psMP.num_vertices = nPointCount;
		psMP.vertices = new DGNPoint[nPointCount];
		for (int j = 0; j < nPointCount; j++)
			psMP.vertices[j] = pasVertices[j];

		// Setup Raw data for the multipoint section.
		if (nType == DGNlib.DGNT_LINE) {
			if (nPointCount != 2)
				throw new IllegalArgumentException("!060");

			psCore.raw_bytes = 36 + psDGN.dimension * 4 * nPointCount;

			psCore.raw_data = new byte[psCore.raw_bytes];

			DGNreader.DGNInverseTransformPointToInt(psDGN, pasVertices[0], psCore.raw_data, 36);
			DGNreader.DGNInverseTransformPointToInt(psDGN, pasVertices[1], psCore.raw_data, 36 + psDGN.dimension * 4);
		} else {
			if (nPointCount < 2)
				throw new IllegalArgumentException("!061");

			psCore.raw_bytes = 38 + psDGN.dimension * 4 * nPointCount;
			psCore.raw_data = new byte[psCore.raw_bytes];

			psCore.raw_data[36] = (byte) (nPointCount % 256);
			psCore.raw_data[37] = (byte) (nPointCount / 256);

			for (i = 0; i < nPointCount; i++)
				DGNreader.DGNInverseTransformPointToInt(psDGN, pasVertices[i], psCore.raw_data,
						38 + psDGN.dimension * i * 4);
		}

		// Set the core raw data, including the bounds.
		DGNUpdateElemCoreExtended(hDGN, psCore);

		sMin = new DGNPoint(pasVertices[0]);
		sMax = new DGNPoint(pasVertices[0]);
		for (i = 1; i < nPointCount; i++) {
			sMin.x = Math.min(pasVertices[i].x, sMin.x);
			sMin.y = Math.min(pasVertices[i].y, sMin.y);
			sMin.z = Math.min(pasVertices[i].z, sMin.z);
			sMax.x = Math.max(pasVertices[i].x, sMax.x);
			sMax.y = Math.max(pasVertices[i].y, sMax.y);
			sMax.z = Math.max(pasVertices[i].z, sMax.z);
		}

		DGNWriteBounds(psDGN, psCore, sMin, sMax);

		return psCore;
	}

	static DGNElem DGNCreateArcElem2D(DGNfile hDGN, int nType, double dfOriginX, double dfOriginY, double dfPrimaryAxis,
			double dfSecondaryAxis, double dfRotation, double dfStartAngle, double dfSweepAngle) {
		return DGNCreateArcElem(hDGN, nType, dfOriginX, dfOriginY, 0.0, dfPrimaryAxis, dfSecondaryAxis, dfStartAngle,
				dfSweepAngle, dfRotation, null);
	}

	/**
	 * Create Arc or Ellipse element.
	 *
	 * Create a new 2D or 3D arc or ellipse element. The start angle, and sweep
	 * angle are ignored for DGNT_ELLIPSE but used for DGNT_ARC.
	 *
	 * The newly created element will still need to be written to file using
	 * DGNWriteElement(). Also the level and other core values will be defaulted.
	 * Use DGNUpdateElemCore() on the element before writing to set these values.
	 *
	 * @param hDGN            the DGN file on which the element will eventually be
	 *                        written.
	 * @param nType           either DGNT_ELLIPSE or DGNT_ARC to select element
	 *                        type.
	 * @param dfOriginX       the origin (center of rotation) of the arc (X).
	 * @param dfOriginY       the origin (center of rotation) of the arc (Y).
	 * @param dfOriginZ       the origin (center of rotation) of the arc (Y).
	 * @param dfPrimaryAxis   the length of the primary axis.
	 * @param dfSecondaryAxis the length of the secondary axis.
	 * @param dfStartAngle    start angle, degrees counterclockwise of primary axis.
	 * @param dfSweepAngle    sweep angle, degrees
	 * @param dfRotation      Counterclockwise rotation in degrees.
	 * @param panQuaternion   3D orientation quaternion (null to use rotation).
	 * 
	 * @return the new element (DGNElemArc) or null on failure.
	 */

	public static DGNElem DGNCreateArcElem(DGNfile hDGN, int nType, double dfOriginX, double dfOriginY,
			double dfOriginZ, double dfPrimaryAxis, double dfSecondaryAxis, double dfStartAngle, double dfSweepAngle,
			double dfRotation, int[] panQuaternion) {
		DGNElemArc psArc;
		DGNElem psCore;
		DGNfile psDGN = (DGNfile) hDGN;
		DGNPoint sMin = new DGNPoint(), sMax = new DGNPoint(), sOrigin = new DGNPoint();
		int nAngle;

		if (!(nType == DGNlib.DGNT_ARC || nType == DGNlib.DGNT_ELLIPSE))
			throw new IllegalArgumentException("!070");

		DGNreader.DGNLoadTCB(hDGN);

		// Allocate element.
		psArc = new DGNElemArc();
		psCore = psArc;

		DGNInitializeElemCore(hDGN, psCore);
		psCore.stype = DGNlib.DGNST_ARC;
		psCore.type = nType;

		// Set arc specific information in the structure.
		sOrigin.x = dfOriginX;
		sOrigin.y = dfOriginY;
		sOrigin.z = dfOriginZ;

		psArc.origin = sOrigin;
		psArc.primary_axis = dfPrimaryAxis;
		psArc.secondary_axis = dfSecondaryAxis;
		psArc.quat = new int[4];
		psArc.startang = dfStartAngle;
		psArc.sweepang = dfSweepAngle;

		psArc.rotation = dfRotation;
		if (panQuaternion == null) {
			DGNlib.DGNRotationToQuaternion(dfRotation, psArc.quat);
		} else {
			psArc.quat = Arrays.copyOf(panQuaternion, 4);
		}

		// Setup Raw data for the arc section.
		if (nType == DGNlib.DGNT_ARC) {
			double dfScaledAxis;

			if (psDGN.dimension == 3)
				psCore.raw_bytes = 100;
			else
				psCore.raw_bytes = 80;
			psCore.raw_data = new byte[psCore.raw_bytes];

			/* start angle */
			nAngle = (int) (dfStartAngle * 360000.0);
			BinaryUtils.getBytesME(nAngle, psCore.raw_data, 36);

			/* sweep angle */
			if (dfSweepAngle < 0.0) {
				nAngle = (int) (Math.abs(dfSweepAngle) * 360000.0);
				nAngle |= 0x80000000;
			} else if (dfSweepAngle > 364.9999) {
				nAngle = 0;
			} else {
				nAngle = (int) (dfSweepAngle * 360000.0);
			}
			BinaryUtils.getBytesME(nAngle, psCore.raw_data, 40);

			/* axes */
			dfScaledAxis = dfPrimaryAxis / psDGN.scale;
			System.arraycopy(BinaryUtils.IEEE2vax(dfScaledAxis), 0, psCore.raw_data, 44, 8);

			dfScaledAxis = dfSecondaryAxis / psDGN.scale;
			System.arraycopy(BinaryUtils.IEEE2vax(dfScaledAxis), 0, psCore.raw_data, 52, 8);

			if (psDGN.dimension == 3) {
				/* quaternion */
				BinaryUtils.getBytesME(psArc.quat[0], psCore.raw_data, 60);
				BinaryUtils.getBytesME(psArc.quat[1], psCore.raw_data, 64);
				BinaryUtils.getBytesME(psArc.quat[2], psCore.raw_data, 68);
				BinaryUtils.getBytesME(psArc.quat[3], psCore.raw_data, 72);

				/* origin */
				DGNreader.DGNInverseTransformPoint(psDGN, sOrigin);

				System.arraycopy(BinaryUtils.IEEE2vax(sOrigin.x), 0, psCore.raw_data, 76, 8);
				System.arraycopy(BinaryUtils.IEEE2vax(sOrigin.y), 0, psCore.raw_data, 84, 8);
				System.arraycopy(BinaryUtils.IEEE2vax(sOrigin.z), 0, psCore.raw_data, 92, 8);
			} else {
				/* rotation */
				nAngle = (int) (dfRotation * 360000.0);
				BinaryUtils.getBytesME(nAngle, psCore.raw_data, 60);

				/* origin */
				DGNreader.DGNInverseTransformPoint(psDGN, sOrigin);
				System.arraycopy(BinaryUtils.IEEE2vax(sOrigin.x), 0, psCore.raw_data, 64, 8);
				System.arraycopy(BinaryUtils.IEEE2vax(sOrigin.y), 0, psCore.raw_data, 72, 8);
			}
		}

		// Setup Raw data for the ellipse section.
		else {
			double dfScaledAxis;

			if (psDGN.dimension == 3)
				psCore.raw_bytes = 92;
			else
				psCore.raw_bytes = 72;
			psCore.raw_data = new byte[psCore.raw_bytes];

			/* axes */
			dfScaledAxis = dfPrimaryAxis / psDGN.scale;
			System.arraycopy(BinaryUtils.IEEE2vax(dfScaledAxis), 0, psCore.raw_data, 36, 8);

			dfScaledAxis = dfSecondaryAxis / psDGN.scale;
			System.arraycopy(BinaryUtils.IEEE2vax(dfScaledAxis), 0, psCore.raw_data, 44, 8);

			if (psDGN.dimension == 3) {
				/* quaternion */
				BinaryUtils.getBytesME(psArc.quat[0], psCore.raw_data, 52);
				BinaryUtils.getBytesME(psArc.quat[1], psCore.raw_data, 56);
				BinaryUtils.getBytesME(psArc.quat[2], psCore.raw_data, 60);
				BinaryUtils.getBytesME(psArc.quat[3], psCore.raw_data, 64);

				/* origin */
				DGNreader.DGNInverseTransformPoint(psDGN, sOrigin);
				System.arraycopy(BinaryUtils.IEEE2vax(sOrigin.x), 0, psCore.raw_data, 68, 8);
				System.arraycopy(BinaryUtils.IEEE2vax(sOrigin.y), 0, psCore.raw_data, 76, 8);
				System.arraycopy(BinaryUtils.IEEE2vax(sOrigin.z), 0, psCore.raw_data, 84, 8);
			} else {
				/* rotation */
				nAngle = (int) (dfRotation * 360000.0);
				BinaryUtils.getBytesME(nAngle, psCore.raw_data, 52);

				/* origin */
				DGNreader.DGNInverseTransformPoint(psDGN, sOrigin);
				System.arraycopy(BinaryUtils.IEEE2vax(sOrigin.x), 0, psCore.raw_data, 56, 8);
				System.arraycopy(BinaryUtils.IEEE2vax(sOrigin.y), 0, psCore.raw_data, 64, 8);
			}

			psArc.startang = 0.0;
			psArc.sweepang = 360.0;
		}

		// Set the core raw data, including the bounds.
		DGNUpdateElemCoreExtended(hDGN, psCore);

		sMin.x = dfOriginX - Math.max(dfPrimaryAxis, dfSecondaryAxis);
		sMin.y = dfOriginY - Math.max(dfPrimaryAxis, dfSecondaryAxis);
		sMin.z = dfOriginZ - Math.max(dfPrimaryAxis, dfSecondaryAxis);
		sMax.x = dfOriginX + Math.max(dfPrimaryAxis, dfSecondaryAxis);
		sMax.y = dfOriginY + Math.max(dfPrimaryAxis, dfSecondaryAxis);
		sMax.z = dfOriginZ + Math.max(dfPrimaryAxis, dfSecondaryAxis);

		DGNWriteBounds(psDGN, psCore, sMin, sMax);

		return psCore;
	}

	/**
	 * Create Cone element.
	 *
	 * Create a new 3D cone element.
	 *
	 * The newly created element will still need to be written to file using
	 * DGNWriteElement(). Also the level and other core values will be defaulted.
	 * Use DGNUpdateElemCore() on the element before writing to set these values.
	 *
	 * @param hDGN          the DGN file on which the element will eventually be
	 *                      written.
	 * @param dfCenter1X    the center of the first bounding circle (X).
	 * @param dfCenter1Y    the center of the first bounding circle (Y).
	 * @param dfCenter1Z    the center of the first bounding circle (Z).
	 * @param dfRadius1     the radius of the first bounding circle.
	 * @param dfCenter2X    the center of the second bounding circle (X).
	 * @param dfCenter2Y    the center of the second bounding circle (Y).
	 * @param dfCenter2Z    the center of the second bounding circle (Z).
	 * @param dfRadius2     the radius of the second bounding circle.
	 * @param panQuaternion 3D orientation quaternion (null for default orientation
	 *                      - circles parallel to the X-Y plane).
	 * 
	 * @return the new element (DGNElemCone) or null on failure.
	 */

	static DGNElem DGNCreateConeElem(DGNfile hDGN, double dfCenter_1X, double dfCenter_1Y, double dfCenter_1Z,
			double dfRadius_1, double dfCenter_2X, double dfCenter_2Y, double dfCenter_2Z, double dfRadius_2,
			int[] panQuaternion) {
		DGNElemCone psCone;
		DGNElem psCore;
		DGNfile psDGN = (DGNfile) hDGN;
		DGNPoint sMin = new DGNPoint(), sMax = new DGNPoint(), sCenter_1 = new DGNPoint(), sCenter_2 = new DGNPoint();
		double dfScaledRadius;

		DGNreader.DGNLoadTCB(hDGN);

		// Allocate element.
		psCone = new DGNElemCone();
		psCore = psCone;

		DGNInitializeElemCore(hDGN, psCore);
		psCore.stype = DGNlib.DGNST_CONE;
		psCore.type = DGNlib.DGNT_CONE;

		// Set cone specific information in the structure.
		sCenter_1.x = dfCenter_1X;
		sCenter_1.y = dfCenter_1Y;
		sCenter_1.z = dfCenter_1Z;
		sCenter_2.x = dfCenter_2X;
		sCenter_2.y = dfCenter_2Y;
		sCenter_2.z = dfCenter_2Z;
		psCone.center_1 = sCenter_1;
		psCone.center_2 = sCenter_2;
		psCone.radius_1 = dfRadius_1;
		psCone.radius_2 = dfRadius_2;

		psCone.quat = new int[4];
		if (panQuaternion != null) {
			psCone.quat = Arrays.copyOf(panQuaternion, 4);
		} else {
			psCone.quat[0] = 1 << 31;
			psCone.quat[1] = 0;
			psCone.quat[2] = 0;
			psCone.quat[3] = 0;
		}

		// Setup Raw data for the cone.
		psCore.raw_bytes = 118;
		psCore.raw_data = new byte[psCore.raw_bytes];

		/* unknown data */
		psCore.raw_data[36] = 0;
		psCore.raw_data[37] = 0;

		/* quaternion */
		BinaryUtils.getBytesME(psCone.quat[0], psCore.raw_data, 38);
		BinaryUtils.getBytesME(psCone.quat[1], psCore.raw_data, 42);
		BinaryUtils.getBytesME(psCone.quat[2], psCore.raw_data, 46);
		BinaryUtils.getBytesME(psCone.quat[3], psCore.raw_data, 50);

		/* center_1 */
		DGNreader.DGNInverseTransformPoint(psDGN, sCenter_1);
		System.arraycopy(BinaryUtils.IEEE2vax(sCenter_1.x), 0, psCore.raw_data, 54, 8);
		System.arraycopy(BinaryUtils.IEEE2vax(sCenter_1.y), 0, psCore.raw_data, 62, 8);
		System.arraycopy(BinaryUtils.IEEE2vax(sCenter_1.z), 0, psCore.raw_data, 70, 8);

		/* radius_1 */
		dfScaledRadius = psCone.radius_1 / psDGN.scale;
		System.arraycopy(BinaryUtils.IEEE2vax(dfScaledRadius), 0, psCore.raw_data, 78, 8);

		/* center_2 */
		DGNreader.DGNInverseTransformPoint(psDGN, sCenter_2);
		System.arraycopy(BinaryUtils.IEEE2vax(sCenter_2.x), 0, psCore.raw_data, 86, 8);
		System.arraycopy(BinaryUtils.IEEE2vax(sCenter_2.y), 0, psCore.raw_data, 94, 8);
		System.arraycopy(BinaryUtils.IEEE2vax(sCenter_2.z), 0, psCore.raw_data, 102, 8);

		/* radius_2 */
		dfScaledRadius = psCone.radius_2 / psDGN.scale;
		System.arraycopy(BinaryUtils.IEEE2vax(dfScaledRadius), 0, psCore.raw_data, 110, 8);

		// Set the core raw data, including the bounds.
		DGNUpdateElemCoreExtended(hDGN, psCore);

		// FIXME: Calculate bounds. Do we need to take the quaternion into
		// account?
		// kintel 20030819

		// Old implementation attempt:
		// What if center_1.z > center_2.z ?
		// double largestRadius =
		// psCone.radius_1>psCone.radius_2?psCone.radius_1:psCone.radius_2;
		// sMin.x = psCone.center_1.x-largestRadius;
		// sMin.y = psCone.center_1.y-largestRadius;
		// sMin.z = psCone.center_1.z;
		// sMax.x = psCone.center_2.x+largestRadius;
		// sMax.y = psCone.center_2.y+largestRadius;
		// sMax.z = psCone.center_2.z;

		DGNWriteBounds(psDGN, psCore, sMin, sMax);

		return psCore;
	}

	/**
	 * Create text element.
	 *
	 * The newly created element will still need to be written to file using
	 * DGNWriteElement(). Also the level and other core values will be defaulted.
	 * Use DGNUpdateElemCore() on the element before writing to set these values.
	 *
	 * @param hDGN           the file on which the element will eventually be
	 *                       written.
	 * @param pszText        the string of text.
	 * @param nFontId        microstation font id for the text. 1 may be used as
	 *                       default.
	 * @param nJustification text justification. One of DGNJ_LEFT_TOP,
	 *                       DGNJ_LEFT_CENTER, DGNJ_LEFT_BOTTOM, DGNJ_CENTER_TOP,
	 *                       DGNJ_CENTER_CENTER, DGNJ_CENTER_BOTTOM, DGNJ_RIGHT_TOP,
	 *                       DGNJ_RIGHT_CENTER, DGNJ_RIGHT_BOTTOM.
	 * @param dfLengthMult   character width in master units.
	 * @param dfHeightMult   character height in master units.
	 * @param dfRotation     Counterclockwise text rotation in degrees.
	 * @param panQuaternion  3D orientation quaternion (null to use rotation).
	 * @param dfOriginX      Text origin (X).
	 * @param dfOriginY      Text origin (Y).
	 * @param dfOriginZ      Text origin (Z).
	 * 
	 * @return the new element (DGNElemText) or null on failure.
	 */
	public static DGNElem DGNCreateTextElem(DGNfile hDGN, String pszText, int nFontId, int nJustification,
			double dfLengthMult, double dfHeightMult, double dfRotation, int[] panQuaternion, double dfOriginX,
			double dfOriginY, double dfOriginZ) {
		DGNElemText psText;
		DGNElem psCore;
		DGNPoint sMin = new DGNPoint(), sMax = new DGNPoint(), sLowLeft = new DGNPoint(), sLowRight = new DGNPoint(),
				sUpLeft = new DGNPoint(), sUpRight = new DGNPoint();
		int nIntValue, nBase;
		double length, height, diagonal;

		DGNreader.DGNLoadTCB(hDGN);

		// Allocate element.
		psText = new DGNElemText();
		psCore = psText;

		DGNInitializeElemCore(hDGN, psCore);
		psCore.stype = DGNlib.DGNST_TEXT;
		psCore.type = DGNlib.DGNT_TEXT;

		// Set arc specific information in the structure.
		psText.font_id = nFontId;
		psText.justification = nJustification;
		psText.length_mult = dfLengthMult;
		psText.height_mult = dfHeightMult;
		psText.rotation = dfRotation;
		psText.origin.x = dfOriginX;
		psText.origin.y = dfOriginY;
		psText.origin.z = dfOriginZ;
		psText.string = pszText;

		// Setup Raw data for the text specific portion.
		if (hDGN.dimension == 2)
			psCore.raw_bytes = 60 + pszText.length();
		else
			psCore.raw_bytes = 76 + pszText.length();

		psCore.raw_bytes += (psCore.raw_bytes % 2);
		psCore.raw_data = new byte[psCore.raw_bytes];

		psCore.raw_data[36] = (byte) nFontId;
		psCore.raw_data[37] = (byte) nJustification;

		nIntValue = (int) (dfLengthMult * 1000.0 / (hDGN.scale * 6.0) + 0.5);
		BinaryUtils.getBytesME(nIntValue, psCore.raw_data, 38);

		nIntValue = (int) (dfHeightMult * 1000.0 / (hDGN.scale * 6.0) + 0.5);
		BinaryUtils.getBytesME(nIntValue, psCore.raw_data, 42);

		if (hDGN.dimension == 2) {
			nIntValue = (int) (dfRotation * 360000.0);
			BinaryUtils.getBytesME(nIntValue, psCore.raw_data, 46);

			DGNreader.DGNInverseTransformPointToInt(hDGN, psText.origin, psCore.raw_data, 50);

			nBase = 58;
		} else {
			int[] anQuaternion = new int[4];

			if (panQuaternion == null)
				DGNlib.DGNRotationToQuaternion(dfRotation, anQuaternion);
			else
				anQuaternion = Arrays.copyOf(panQuaternion, 4);

			BinaryUtils.getBytesME(anQuaternion[0], psCore.raw_data, 46);
			BinaryUtils.getBytesME(anQuaternion[1], psCore.raw_data, 50);
			BinaryUtils.getBytesME(anQuaternion[2], psCore.raw_data, 54);
			BinaryUtils.getBytesME(anQuaternion[3], psCore.raw_data, 58);

			DGNreader.DGNInverseTransformPointToInt(hDGN, psText.origin, psCore.raw_data, 62);
			nBase = 74;
		}

		psCore.raw_data[nBase] = (byte) pszText.length();
		psCore.raw_data[nBase + 1] = 0; /* edflds? */
		System.arraycopy(pszText.getBytes(), 0, psCore.raw_data, nBase + 2, pszText.length());

		// Set the core raw data, including the bounds.
		DGNUpdateElemCoreExtended(hDGN, psCore);

		// calculate bounds if rotation is 0
		sMin.x = dfOriginX;
		sMin.y = dfOriginY;
		sMin.z = 0.0;
		sMax.x = dfOriginX + dfLengthMult * pszText.length();
		sMax.y = dfOriginY + dfHeightMult;
		sMax.z = 0.0;

		// calculate rotated bounding box coordinates
		length = sMax.x - sMin.x;
		height = sMax.y - sMin.y;
		diagonal = Math.sqrt(length * length + height * height);
		sLowLeft.x = sMin.x;
		sLowLeft.y = sMin.y;
		sLowRight.x = sMin.x + Math.cos(psText.rotation * Math.PI / 180.0) * length;
		sLowRight.y = sMin.y + Math.sin(psText.rotation * Math.PI / 180.0) * length;
		sUpRight.x = sMin.x + Math.cos((psText.rotation * Math.PI / 180.0) + Math.atan(height / length)) * diagonal;
		sUpRight.y = sMin.y + Math.sin((psText.rotation * Math.PI / 180.0) + Math.atan(height / length)) * diagonal;
		sUpLeft.x = sMin.x + Math.cos((psText.rotation + 90.0) * Math.PI / 180.0) * height;
		sUpLeft.y = sMin.y + Math.sin((psText.rotation + 90.0) * Math.PI / 180.0) * height;

		// calculate new values for bounding box
		sMin.x = Math.min(sLowLeft.x, Math.min(sLowRight.x, Math.min(sUpLeft.x, sUpRight.x)));
		sMin.y = Math.min(sLowLeft.y, Math.min(sLowRight.y, Math.min(sUpLeft.y, sUpRight.y)));
		sMax.x = Math.max(sLowLeft.x, Math.max(sLowRight.x, Math.max(sUpLeft.x, sUpRight.x)));
		sMax.y = Math.max(sLowLeft.y, Math.max(sLowRight.y, Math.max(sUpLeft.y, sUpRight.y)));
		sMin.x = dfOriginX - dfLengthMult * pszText.length();
		sMin.y = dfOriginY - dfHeightMult;
		sMin.z = 0.0;
		sMax.x = dfOriginX + dfLengthMult * pszText.length();
		sMax.y = dfOriginY + dfHeightMult;
		sMax.z = 0.0;

		DGNWriteBounds(hDGN, psCore, sMin, sMax);

		return psCore;
	}

	/**
	 * Create color table element.
	 *
	 * Creates a color table element with the indicated color table.
	 *
	 * Note that color table elements are actally of type DGNT_GROUP_DATA(5) and
	 * always on level 1. Do not alter the level with DGNUpdateElemCore() or the
	 * element will essentially be corrupt.
	 *
	 * The newly created element will still need to be written to file using
	 * DGNWriteElement(). Also the level and other core values will be defaulted.
	 * Use DGNUpdateElemCore() on the element before writing to set these values.
	 *
	 * @param hDGN        the file to which the element will eventually be written.
	 * @param nScreenFlag the screen to which the color table applies (0 = left, 1 =
	 *                    right).
	 * @param             abyColorInfo[8][3] array of 256 color entries. The first
	 *                    is the background color.
	 *
	 * @return the new element (DGNElemColorTable) or null on failure.
	 */

	static DGNElem DGNCreateColorTableElem(DGNfile hDGN, int nScreenFlag, byte[][] abyColorInfo) {
		DGNElemColorTable psCT;
		DGNElem psCore;

		// Allocate element.
		psCT = new DGNElemColorTable();
		psCore = psCT;

		DGNInitializeElemCore(hDGN, psCore);
		psCore.stype = DGNlib.DGNST_COLORTABLE;
		psCore.type = DGNlib.DGNT_GROUP_DATA;
		psCore.level = DGNlib.DGN_GDL_COLOR_TABLE;

		// Set colortable specific information in the structure.
		psCT.screen_flag = nScreenFlag;
		psCT.color_info = Arrays.copyOf(abyColorInfo, 768);

		// Setup Raw data for the color table specific portion.
		psCore.raw_bytes = 806;
		psCore.raw_data = new byte[psCore.raw_bytes];

		psCore.raw_data[36] = (byte) (nScreenFlag % 256);
		psCore.raw_data[37] = (byte) (nScreenFlag / 256);

		System.arraycopy(abyColorInfo[255], 0, psCore.raw_data, 38, 3);
		System.arraycopy(abyColorInfo, 0, psCore.raw_data, 41, 783);

		// Set the core raw data.
		DGNUpdateElemCoreExtended(hDGN, psCore);

		return psCore;
	}

	/**
	 * Create complex chain/shape header.
	 *
	 * The newly created element will still need to be written to file using
	 * DGNWriteElement(). Also the level and other core values will be defaulted.
	 * Use DGNUpdateElemCore() on the element before writing to set these values.
	 *
	 * The nTotLength is the sum of the size of all elements in the complex group
	 * plus 5. The DGNCreateComplexHeaderFromGroup() can be used to build a complex
	 * element from the members more conveniently.
	 *
	 * @param hDGN       the file on which the element will be written.
	 * @param nType      DGNT_COMPLEX_CHAIN_HEADER or DGNT_COMPLEX_SHAPE_HEADER.
	 *                   depending on whether the list is open or closed (last point
	 *                   equal to last) or if the object represents a surface or a
	 *                   solid.
	 * @param nTotLength the value of the totlength field in the element.
	 * @param nNumElems  the number of elements in the complex group not including
	 *                   the header element.
	 *
	 * @return the new element (DGNElemComplexHeader) or null on failure.
	 */
	static DGNElem DGNCreateComplexHeaderElem(DGNfile hDGN, int nType, int nTotLength, int nNumElems) {
		DGNElemComplexHeader psCH;
		DGNElem psCore;
		byte[] abyRawZeroLinkage = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };

		if (!(nType == DGNlib.DGNT_COMPLEX_CHAIN_HEADER || nType == DGNlib.DGNT_COMPLEX_SHAPE_HEADER))
			throw new IllegalArgumentException("!040");

		DGNreader.DGNLoadTCB(hDGN);

		// Allocate element.
		psCH = new DGNElemComplexHeader();
		psCore = psCH;

		DGNInitializeElemCore(hDGN, psCore);
		psCore.complex = true;
		psCore.stype = DGNlib.DGNST_COMPLEX_HEADER;
		psCore.type = nType;

		// Set complex header specific information in the structure.
		psCH.totlength = nTotLength - 4;
		psCH.numelems = nNumElems;
		psCH.surftype = 0;
		psCH.boundelms = 0;

		// Setup Raw data for the complex specific portion.
		psCore.raw_bytes = 40;
		psCore.raw_data = new byte[psCore.raw_bytes];

		psCore.raw_data[36] = (byte) ((nTotLength - 4) % 256);
		psCore.raw_data[37] = (byte) ((nTotLength - 4) / 256);
		psCore.raw_data[38] = (byte) (nNumElems % 256);
		psCore.raw_data[39] = (byte) (nNumElems / 256);

		// Set the core raw data.
		DGNUpdateElemCoreExtended(hDGN, psCore);

		// Elements have to be at least 48 bytes long, so we have to add a dummy
		// bit of attribute data to fill out the length.
		DGNAddRawAttrLink(hDGN, psCore, 8, abyRawZeroLinkage);

		return psCore;
	}

	/**
	 * Create complex chain/shape header.
	 *
	 * This function is similar to DGNCreateComplexHeaderElem(), but it takes care
	 * of computing the total size of the set of elements being written, and
	 * collecting the bounding extents. It also takes care of some other convenience
	 * issues, like marking all the member elements as complex, and setting the
	 * level based on the level of the member elements.
	 * 
	 * @param hDGN      the file on which the element will be written.
	 * @param nType     DGNT_COMPLEX_CHAIN_HEADER or DGNT_COMPLEX_SHAPE_HEADER.
	 *                  depending on whether the list is open or closed (last point
	 *                  equal to last) or if the object represents a surface or a
	 *                  solid.
	 * @param nNumElems the number of elements in the complex group not including
	 *                  the header element.
	 * @param papsElems array of pointers to nNumElems elements in the complex
	 *                  group. Some updates may be made to these elements.
	 *
	 * @return the new element (DGNElemComplexHeader) or null on failure.
	 */

	public static DGNElem DGNCreateComplexHeaderFromGroup(DGNfile hDGN, int nType, int nNumElems, DGNElem[] papsElems) {
		int nTotalLength = 5;
		int nLevel;
		DGNElem psCH;
		DGNPoint sMin = null, sMax = null;

		DGNreader.DGNLoadTCB(hDGN);

		if (nNumElems < 1 || papsElems == null) {
			System.err.println("Need at least one element to form a complex group.");
			return null;
		}

		// Collect the total size, and bounds.
		nLevel = papsElems[0].level;

		for (int i = 0; i < nNumElems; i++) {
			DGNPoint sThisMin = new DGNPoint(), sThisMax = new DGNPoint();

			nTotalLength += papsElems[i].raw_bytes / 2;

			papsElems[i].complex = true;
			papsElems[i].raw_data[0] |= 0x80;

			if (papsElems[i].level != nLevel) {
				System.err.println("Not all level values matching in a complex set group!");
			}

			DGNreader.DGNGetElementExtents(hDGN, papsElems[i], sThisMin, sThisMax);
			if (i == 0) {
				sMin = new DGNPoint(sThisMin);
				sMax = new DGNPoint(sThisMax);
			} else {
				sMin.x = Math.min(sMin.x, sThisMin.x);
				sMin.y = Math.min(sMin.y, sThisMin.y);
				sMin.z = Math.min(sMin.z, sThisMin.z);
				sMax.x = Math.max(sMax.x, sThisMax.x);
				sMax.y = Math.max(sMax.y, sThisMax.y);
				sMax.z = Math.max(sMax.z, sThisMax.z);
			}
		}

		// Create the corresponding complex header.
		psCH = DGNCreateComplexHeaderElem(hDGN, nType, nTotalLength, nNumElems);
		DGNUpdateElemCore(hDGN, psCH, papsElems[0].level, psCH.graphic_group, psCH.color, psCH.weight, psCH.style);

		DGNWriteBounds((DGNfile) hDGN, psCH, sMin, sMax);

		return psCH;
	}

	/**
	 * Create 3D solid/surface.
	 *
	 * The newly created element will still need to be written to file using
	 * DGNWriteElement(). Also the level and other core values will be defaulted.
	 * Use DGNUpdateElemCore() on the element before writing to set these values.
	 *
	 * The nTotLength is the sum of the size of all elements in the solid group plus
	 * 6. The DGNCreateSolidHeaderFromGroup() can be used to build a solid element
	 * from the members more conveniently.
	 *
	 * @param hDGN        the file on which the element will be written.
	 * @param nType       DGNT_3DSURFACE_HEADER or DGNT_3DSOLID_HEADER.
	 * @param nSurfType   the surface/solid type, one of DGNSUT_* or DGNSOT_*.
	 * @param nBoundElems the number of elements in each boundary.
	 * @param nTotLength  the value of the totlength field in the element.
	 * @param nNumElems   the number of elements in the solid not including the
	 *                    header element.
	 *
	 * @return the new element (DGNElemComplexHeader) or null on failure.
	 */
	static DGNElem

			DGNCreateSolidHeaderElem(DGNfile hDGN, int nType, int nSurfType, int nBoundElems, int nTotLength,
					int nNumElems) {
		DGNElemComplexHeader psCH;
		DGNElem psCore;
		byte[] abyRawZeroLinkage = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };

		if (!(nType == DGNlib.DGNT_3DSURFACE_HEADER || nType == DGNlib.DGNT_3DSOLID_HEADER))
			throw new IllegalArgumentException("!050");

		DGNreader.DGNLoadTCB(hDGN);

		// Allocate element.
		psCH = new DGNElemComplexHeader();
		psCore = psCH;

		DGNInitializeElemCore(hDGN, psCore);
		psCore.complex = true;
		psCore.stype = DGNlib.DGNST_COMPLEX_HEADER;
		psCore.type = nType;

		// Set solid header specific information in the structure.
		psCH.totlength = nTotLength - 4;
		psCH.numelems = nNumElems;
		psCH.surftype = nSurfType;
		psCH.boundelms = nBoundElems;

		// Setup Raw data for the solid specific portion.
		psCore.raw_bytes = 42;

		psCore.raw_data = new byte[psCore.raw_bytes];

		psCore.raw_data[36] = (byte) ((nTotLength - 4) % 256);
		psCore.raw_data[37] = (byte) ((nTotLength - 4) / 256);
		psCore.raw_data[38] = (byte) (nNumElems % 256);
		psCore.raw_data[39] = (byte) (nNumElems / 256);
		psCore.raw_data[40] = (byte) psCH.surftype;
		psCore.raw_data[41] = (byte) (psCH.boundelms - 1);

		// Set the core raw data.
		DGNUpdateElemCoreExtended(hDGN, psCore);

		// Elements have to be at least 48 bytes long, so we have to add a dummy
		// bit of attribute data to fill out the length.
		DGNAddRawAttrLink(hDGN, psCore, 8, abyRawZeroLinkage);

		return psCore;
	}

	/**
	 * Create 3D solid/surface header.
	 *
	 * This function is similar to DGNCreateSolidHeaderElem(), but it takes care of
	 * computing the total size of the set of elements being written, and collecting
	 * the bounding extents. It also takes care of some other convenience issues,
	 * like marking all the member elements as complex, and setting the level based
	 * on the level of the member elements.
	 * 
	 * @param hDGN        the file on which the element will be written.
	 * @param nType       DGNT_3DSURFACE_HEADER or DGNT_3DSOLID_HEADER.
	 * @param nSurfType   the surface/solid type, one of DGNSUT_* or DGNSOT_*.
	 * @param nBoundElems the number of boundary elements.
	 * @param nNumElems   the number of elements in the solid not including the
	 *                    header element.
	 * @param papsElems   array of pointers to nNumElems elements in the solid. Some
	 *                    updates may be made to these elements.
	 *
	 * @return the new element (DGNElemComplexHeader) or null on failure.
	 */

	static DGNElem DGNCreateSolidHeaderFromGroup(DGNfile hDGN, int nType, int nSurfType, int nBoundElems, int nNumElems,
			DGNElem[] papsElems) {
		int nTotalLength = 6;
		int i, nLevel;
		DGNElem psCH;
		DGNPoint sMin = null, sMax = null;

		DGNreader.DGNLoadTCB(hDGN);

		if (nNumElems < 1 || papsElems == null) {
			System.err.println("Need at least one element to form a solid.");
			return null;
		}

		// Collect the total size, and bounds.
		nLevel = papsElems[0].level;

		for (i = 0; i < nNumElems; i++) {
			DGNPoint sThisMin = new DGNPoint(), sThisMax = new DGNPoint();

			nTotalLength += papsElems[i].raw_bytes / 2;

			papsElems[i].complex = true;
			papsElems[i].raw_data[0] |= 0x80;

			if (papsElems[i].level != nLevel) {
				System.err.println("Not all level values matching in a complex set group!");
			}

			DGNreader.DGNGetElementExtents(hDGN, papsElems[i], sThisMin, sThisMax);
			if (i == 0) {
				sMin = new DGNPoint(sThisMin);
				sMax = new DGNPoint(sThisMax);
			} else {
				sMin.x = Math.min(sMin.x, sThisMin.x);
				sMin.y = Math.min(sMin.y, sThisMin.y);
				sMin.z = Math.min(sMin.z, sThisMin.z);
				sMax.x = Math.max(sMax.x, sThisMax.x);
				sMax.y = Math.max(sMax.y, sThisMax.y);
				sMax.z = Math.max(sMax.z, sThisMax.z);
			}
		}

		// Create the corresponding solid header.
		psCH = DGNCreateSolidHeaderElem(hDGN, nType, nSurfType, nBoundElems, nTotalLength, nNumElems);
		DGNUpdateElemCore(hDGN, psCH, papsElems[0].level, psCH.graphic_group, psCH.color, psCH.weight, psCH.style);

		DGNWriteBounds((DGNfile) hDGN, psCH, sMin, sMax);

		return psCH;
	}

	/**
	 * Create cell header.
	 *
	 * The newly created element will still need to be written to file using
	 * DGNWriteElement(). Also the level and other core values will be defaulted.
	 * Use DGNUpdateElemCore() on the element before writing to set these values.
	 *
	 * Generally speaking the function DGNCreateCellHeaderFromGroup() should be used
	 * instead of this function.
	 *
	 * @param hDGN        the file handle on which the element is to be written.
	 * @param nTotLength  total length of cell in words not including the 38 bytes
	 *                    of the cell header that occur before the totlength
	 *                    indicator.
	 * @param pszName
	 * 
	 * @param nClass      the class value for the cell.
	 * @param panLevels   an array of shorts holding the bit mask of levels in
	 *                    effect for this cell. This array should contain 4 shorts
	 *                    (64 bits).
	 * @param psRangeLow  the cell diagonal origin in original cell file
	 *                    coordinates.
	 * @param psRangeHigh the cell diagonal top left corner in original cell file
	 *                    coordinates.
	 * @param psOrigin    the origin of the cell in output file coordinates.
	 * @param dfXScale    the amount of scaling applied in the X dimension in
	 *                    mapping from cell file coordinates to output file
	 *                    coordinates.
	 * @param dfYScale    the amount of scaling applied in the Y dimension in
	 *                    mapping from cell file coordinates to output file
	 *                    coordinates.
	 * @param dfRotation  the amount of rotation (degrees counterclockwise) in
	 *                    mapping from cell coordinates to output file coordinates.
	 *
	 * @return the new element (DGNElemCellHeader) or null on failure.
	 */
	static DGNElem DGNCreateCellHeaderElem(DGNfile hDGN, int nTotLength, String pszName, short nClass, byte[] panLevels,
			DGNPoint psRangeLow, DGNPoint psRangeHigh, DGNPoint psOrigin, double dfXScale, double dfYScale,
			double dfRotation) {
		DGNElemCellHeader psCH;
		DGNElem psCore;
		DGNfile psInfo = (DGNfile) hDGN;

		DGNreader.DGNLoadTCB(hDGN);

		// Allocate element.
		psCH = new DGNElemCellHeader();
		psCore = psCH;

		DGNInitializeElemCore(hDGN, psCore);
		psCore.stype = DGNlib.DGNST_CELL_HEADER;
		psCore.type = DGNlib.DGNT_CELL_HEADER;

		// Set complex header specific information in the structure.
		psCH.totlength = nTotLength;

		// Setup Raw data for the cell header specific portion.
		if (psInfo.dimension == 2)
			psCore.raw_bytes = 92;
		else
			psCore.raw_bytes = 124;
		psCore.raw_data = new byte[psCore.raw_bytes];

		psCore.raw_data[36] = (byte) (nTotLength % 256);
		psCore.raw_data[37] = (byte) (nTotLength / 256);

		System.arraycopy(BinaryUtils.DGNAsciiToRad50b(pszName), 0, psCore.raw_data, 38, 2);
		if (pszName.length() > 3)
			System.arraycopy(BinaryUtils.DGNAsciiToRad50b(pszName.substring(3)), 0, psCore.raw_data, 40, 2);

		psCore.raw_data[42] = (byte) (nClass % 256);
		psCore.raw_data[43] = (byte) (nClass / 256);

		System.arraycopy(panLevels, 0, psCore.raw_data, 44, 8);

		if (psInfo.dimension == 2) {
			DGNPointToInt(psInfo, psRangeLow, psCore.raw_data, 52);
			DGNPointToInt(psInfo, psRangeHigh, psCore.raw_data, 60);

			DGNreader.DGNInverseTransformPointToInt(psInfo, psOrigin, psCore.raw_data, 84);
		} else {
			DGNPointToInt(psInfo, psRangeLow, psCore.raw_data, 52);
			DGNPointToInt(psInfo, psRangeHigh, psCore.raw_data, 64);

			DGNreader.DGNInverseTransformPointToInt(psInfo, psOrigin, psCore.raw_data, 112);
		}

		// Produce a transformation matrix that approximates the requested
		// scaling and rotation.
		if (psInfo.dimension == 2) {
			long[] anTrans = new long[4];
			double cos_a = Math.cos(-dfRotation * Math.PI / 180.0);
			double sin_a = Math.sin(-dfRotation * Math.PI / 180.0);

			anTrans[0] = (long) (cos_a * dfXScale * 214748);
			anTrans[1] = (long) (sin_a * dfYScale * 214748);
			anTrans[2] = (long) (-sin_a * dfXScale * 214748);
			anTrans[3] = (long) (cos_a * dfYScale * 214748);

			BinaryUtils.getBytesME((int) anTrans[0], psCore.raw_data, 68);
			BinaryUtils.getBytesME((int) anTrans[1], psCore.raw_data, 72);
			BinaryUtils.getBytesME((int) anTrans[2], psCore.raw_data, 76);
			BinaryUtils.getBytesME((int) anTrans[3], psCore.raw_data, 80);
		} else {
		}

		// Set the core raw data.
		DGNUpdateElemCoreExtended(hDGN, psCore);

		return psCore;
	}

	/**
	 * Convert a point directly to integer coordinates and write to the indicate
	 * memory location. Intended to be used for the range section of the CELL
	 * HEADER.
	 * 
	 * @param psDGN
	 * @param psPoint
	 * @param pabyTarget
	 * @param offset
	 */
	static void DGNPointToInt(DGNfile psDGN, DGNPoint psPoint, byte[] pabyTarget, int offset) {
		double[] adfCT = new double[3];
		int i;

		adfCT[0] = psPoint.x;
		adfCT[1] = psPoint.y;
		adfCT[2] = psPoint.z;

		for (i = 0; i < psDGN.dimension; i++) {
			int nCTI = (int) Math.max(-2147483647, Math.min(2147483647, adfCT[i]));

			byte[] pabyCTI = new byte[4];
			ByteBuffer.wrap(pabyCTI).order(ByteOrder.LITTLE_ENDIAN).putInt(nCTI);

			pabyTarget[i * 4 + 3 + offset] = pabyCTI[1];
			pabyTarget[i * 4 + 2 + offset] = pabyCTI[0];
			pabyTarget[i * 4 + 1 + offset] = pabyCTI[3];
			pabyTarget[i * 4 + offset] = pabyCTI[2];
		}
	}

	/**
	 * Create cell header from a group of elements.
	 *
	 * The newly created element will still need to be written to file using
	 * DGNWriteElement(). Also the level and other core values will be defaulted.
	 * Use DGNUpdateElemCore() on the element before writing to set these values.
	 *
	 * This function will compute the total length, bounding box, and diagonal range
	 * values from the set of provided elements. Note that the proper diagonal range
	 * values will only be written if 1.0 is used for the x and y scale values, and
	 * 0.0 for the rotation. Use of other values will result in incorrect scaling
	 * handles being presented to the user in Microstation when they select the
	 * element.
	 *
	 * @param hDGN       the file handle on which the element is to be written.
	 * @param pszName
	 * @param nClass     the class value for the cell.
	 * @param panLevels  an array of shorts holding the bit mask of levels in effect
	 *                   for this cell. This array should contain 4 shorts (64
	 *                   bits). This array would normally be passed in as null, and
	 *                   the function will build a mask from the passed list of
	 *                   elements.
	 * @param nNumElems
	 * 
	 * @param papsElems
	 * 
	 * @param psOrigin   the origin of the cell in output file coordinates.
	 * @param dfXScale   the amount of scaling applied in the X dimension in mapping
	 *                   from cell file coordinates to output file coordinates.
	 * @param dfYScale   the amount of scaling applied in the Y dimension in mapping
	 *                   from cell file coordinates to output file coordinates.
	 * @param dfRotation the amount of rotation (degrees counterclockwise) in
	 *                   mapping from cell coordinates to output file coordinates.
	 *
	 * @return the new element (DGNElemCellHeader) or null on failure.
	 */
	public static DGNElem DGNCreateCellHeaderFromGroup(DGNfile hDGN, String pszName, short nClass, byte[] panLevels,
			int nNumElems, DGNElem[] papsElems, DGNPoint psOrigin, double dfXScale, double dfYScale,
			double dfRotation) {
		int nTotalLength;
		int nLevel;
		DGNPoint sMin = null, sMax = null;
		byte[] abyLevelsOccuring = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		DGNfile psInfo = (DGNfile) hDGN;

		DGNreader.DGNLoadTCB(hDGN);

		if (nNumElems < 1 || papsElems == null) {
			System.err.println("Need at least one element to form a cell.");
			return null;
		}

		if (psInfo.dimension == 2)
			nTotalLength = 27;
		else
			nTotalLength = 43;

		// Collect the total size, and bounds.
		nLevel = papsElems[0].level;

		for (int i = 0; i < nNumElems; i++) {
			DGNPoint sThisMin = new DGNPoint(), sThisMax = new DGNPoint();

			nTotalLength += papsElems[i].raw_bytes / 2;

			/* mark as complex */
			papsElems[i].complex = true;
			papsElems[i].raw_data[0] |= 0x80;

			/* establish level */
			nLevel = papsElems[i].level;
			abyLevelsOccuring[nLevel >> 3] |= (0x1 << ((nLevel - 1) & 0x7));

			DGNreader.DGNGetElementExtents(hDGN, papsElems[i], sThisMin, sThisMax);
			if (i == 0) {
				sMin = new DGNPoint(sThisMin);
				sMax = new DGNPoint(sThisMax);
			} else {
				sMin.x = Math.min(sMin.x, sThisMin.x);
				sMin.y = Math.min(sMin.y, sThisMin.y);
				sMin.z = Math.min(sMin.z, sThisMin.z);
				sMax.x = Math.max(sMax.x, sThisMax.x);
				sMax.y = Math.max(sMax.y, sThisMax.y);
				sMax.z = Math.max(sMax.z, sThisMax.z);
			}
		}

		// Create the corresponding cell header.
		if (panLevels == null)
			panLevels = abyLevelsOccuring;

		DGNElem psCH = DGNCreateCellHeaderElem(hDGN, nTotalLength, pszName, nClass, panLevels, sMin, sMax, psOrigin,
				dfXScale, dfYScale, dfRotation);
		DGNWriteBounds((DGNfile) hDGN, psCH, sMin, sMax);

		return psCH;
	}

	/**
	 * Add a database link to element.
	 *
	 * The target element must already have raw_data loaded, and it will be resized
	 * (see DGNResizeElement()) as needed for the new attribute data. Note that the
	 * element is not written to disk immediate. Use DGNWriteElement() for that.
	 *
	 * @param hDGN         the file to which the element corresponds.
	 * @param psElement    the element being updated.
	 * @param nLinkageType link type (DGNLT_*). Usually one of DGNLT_DMRS,
	 *                     DGNLT_INFORMIX, DGNLT_ODBC, DGNLT_ORACLE, DGNLT_RIS,
	 *                     DGNLT_SYBASE, or DGNLT_XBASE.
	 * @param nEntityNum   indicator of the table referenced on target database.
	 * @param nMSLink      indicator of the record referenced on target table.
	 *
	 * @return -1 on failure, or the link index.
	 */
	public static int DGNAddMSLink(DGNfile hDGN, DGNElem psElement, int nLinkageType, int nEntityNum, int nMSLink) {
		byte[] abyLinkage = new byte[32];
		int nLinkageSize;

		if (nLinkageType == DGNlib.DGNLT_DMRS) {
			nLinkageSize = 8;
			abyLinkage[0] = 0x00;
			abyLinkage[1] = 0x00;
			abyLinkage[2] = (byte) (nEntityNum % 256);
			abyLinkage[3] = (byte) (nEntityNum / 256);
			abyLinkage[4] = (byte) (nMSLink % 256);
			abyLinkage[5] = (byte) ((nMSLink / 256) % 256);
			abyLinkage[6] = (byte) (nMSLink / 65536);
			abyLinkage[7] = 0x01;
		} else {
			nLinkageSize = 16;
			abyLinkage[0] = 0x07;
			abyLinkage[1] = 0x10;
			abyLinkage[2] = (byte) (nLinkageType % 256);
			abyLinkage[3] = (byte) (nLinkageType / 256);
			abyLinkage[4] = (byte) (0x81);
			abyLinkage[5] = (byte) (0x0F);
			abyLinkage[6] = (byte) (nEntityNum % 256);
			abyLinkage[7] = (byte) (nEntityNum / 256);
			abyLinkage[8] = (byte) (nMSLink % 256);
			abyLinkage[9] = (byte) ((nMSLink / 256) % 256);
			abyLinkage[10] = (byte) ((nMSLink / 65536) % 256);
			abyLinkage[11] = (byte) (nMSLink / 16777216);
			abyLinkage[12] = 0x00;
			abyLinkage[13] = 0x00;
			abyLinkage[14] = 0x00;
			abyLinkage[15] = 0x00;
		}

		return DGNAddRawAttrLink(hDGN, psElement, nLinkageSize, abyLinkage);
	}

	/**
	 * Add a raw attribute linkage to element.
	 *
	 * Given a raw data buffer, append it to this element as an attribute linkage
	 * without trying to interprete the linkage data.
	 *
	 * The target element must already have raw_data loaded, and it will be resized
	 * (see DGNResizeElement()) as needed for the new attribute data. Note that the
	 * element is not written to disk immediate. Use DGNWriteElement() for that.
	 *
	 * This function will take care of updating the "totlength" field of complex
	 * chain or shape headers to account for the extra attribute space consumed in
	 * the header element.
	 *
	 * @param hDGN            the file to which the element corresponds.
	 * @param psElement       the element being updated.
	 * @param nLinkSize       the size of the linkage in bytes.
	 * @param pabyRawLinkData the raw linkage data (nLinkSize bytes worth).
	 *
	 * @return -1 on failure, or the link index.
	 */

	static int DGNAddRawAttrLink(DGNfile hDGN, DGNElem psElement, int nLinkSize, byte[] pabyRawLinkData) {
		int iLinkage;

		if (nLinkSize % 2 == 1)
			nLinkSize++;

		if (psElement.size + nLinkSize > 768) {
			System.err.printf("Attempt to add %d byte linkage to element exceeds maximum element size.", nLinkSize);
			return -1;
		}

		// Ensure the attribute linkage bit is set.
		psElement.properties |= DGNlib.DGNPF_ATTRIBUTES;

		// Append the attribute linkage to the linkage area.
		psElement.attr_bytes += nLinkSize;
		psElement.attr_data = new byte[psElement.attr_bytes];
		System.arraycopy(pabyRawLinkData, 0, psElement.attr_data, psElement.attr_bytes - nLinkSize, nLinkSize);

		// Grow the raw data, if we have rawdata.
		psElement.raw_bytes += nLinkSize;
		psElement.raw_data = Arrays.copyOf(psElement.raw_data, psElement.raw_bytes);
		System.arraycopy(pabyRawLinkData, 0, psElement.raw_data, psElement.raw_bytes - nLinkSize, nLinkSize);

		// If the element is a shape or chain complex header, then we need to
		// increase the total complex group size appropriately.
		if (psElement.stype == DGNlib.DGNST_COMPLEX_HEADER || psElement.stype == DGNlib.DGNST_TEXT_NODE) {
			// compatible structures
			DGNElemComplexHeader psCT = (DGNElemComplexHeader) psElement;

			psCT.totlength += (nLinkSize / 2);

			psElement.raw_data[36] = (byte) (psCT.totlength % 256);
			psElement.raw_data[37] = (byte) (psCT.totlength / 256);
		}

		// Ensure everything is updated properly, including element length and
		// properties.
		DGNUpdateElemCoreExtended(hDGN, psElement);

		// Figure out what the linkage index is.
		for (iLinkage = 0;; iLinkage++) {
			if (DGNlib.DGNGetLinkage(hDGN, psElement, iLinkage, new int[4]) == null)
				break;
		}

		return iLinkage - 1;
	}

	/**
	 * Add a shape fill attribute linkage.
	 *
	 * The target element must already have raw_data loaded, and it will be resized
	 * (see DGNResizeElement()) as needed for the new attribute data. Note that the
	 * element is not written to disk immediate. Use DGNWriteElement() for that.
	 *
	 * @param hDGN      the file to which the element corresponds.
	 * @param psElement the element being updated.
	 * @param nColor    fill color (color index from palette).
	 *
	 * @return -1 on failure, or the link index.
	 */
	public static int DGNAddShapeFillInfo(DGNfile hDGN, DGNElem psElement, int nColor) {
		byte[] abyFillInfo = { 0x07, 0x10, 0x41, 0x00, 0x02, 0x08, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00 };

		abyFillInfo[8] = (byte) nColor;

		return DGNAddRawAttrLink(hDGN, psElement, 16, abyFillInfo);
	}
}
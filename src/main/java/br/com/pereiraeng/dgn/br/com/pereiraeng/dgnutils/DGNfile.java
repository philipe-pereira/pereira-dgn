package br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.DGNPoint;
import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.elm.DGNElementInfo;

public class DGNfile {
	private RandomAccessFile raf;
	public int next_element_id;

	int nElemBytes;
	byte[] abyElem = new byte[131076];

	boolean got_tcb;
	public int dimension;
	public int options;
	public double scale;
	double origin_x;
	double origin_y;
	double origin_z;

	public boolean index_built;
	public int element_count;
	public int max_element_count;
	public DGNElementInfo[] element_index;

	boolean got_color_table;
	byte[][] color_table = new byte[256][3];

	boolean got_bounds;
	int min_x;
	int min_y;
	int min_z;
	int max_x;
	int max_y;
	int max_z;

	boolean has_spatial_filter;
	boolean sf_converted_to_uor;

	boolean select_complex_group;
	boolean in_complex_group;

	int sf_min_x;
	int sf_min_y;
	int sf_max_x;
	int sf_max_y;

	double sf_min_x_geo;
	double sf_min_y_geo;
	double sf_max_x_geo;
	double sf_max_y_geo;

	public DGNfile(RandomAccessFile raf, int dimension) {
		this.raf = raf;

		next_element_id = 0;

		got_tcb = false;
		scale = 1.0;
		origin_x = 0.0;
		origin_y = 0.0;
		origin_z = 0.0;

		index_built = false;
		element_count = 0;
		element_index = null;

		got_bounds = false;

		this.dimension = dimension;

		has_spatial_filter = false;
		sf_converted_to_uor = false;
		select_complex_group = false;
		in_complex_group = false;
	}

	/**
	 * Set rectangle for which features are desired.
	 *
	 * If a spatial filter is set with this function, DGNReadElement() will only
	 * return spatial elements (elements with a known bounding box) and only
	 * those elements for which this bounding box overlaps the requested region.
	 *
	 * If all four values (dfXMin, dfXMax, dfYMin and dfYMax) are zero, the
	 * spatial filter is disabled. Note that installing a spatial filter won't
	 * reduce the amount of data read from disk. All elements are still scanned,
	 * but the amount of processing work for elements outside the spatial filter
	 * is minimized.
	 *
	 * @param hDGN
	 *            Handle from DGNOpen() for file to update.
	 * @param dfXMin
	 *            minimum x coordinate for extents (georeferenced coordinates).
	 * @param dfYMin
	 *            minimum y coordinate for extents (georeferenced coordinates).
	 * @param dfXMax
	 *            maximum x coordinate for extents (georeferenced coordinates).
	 * @param dfYMax
	 *            maximum y coordinate for extents (georeferenced coordinates).
	 */

	void DGNSetSpatialFilter(double[] XMinXMaxYMinYMax) {
		if (XMinXMaxYMinYMax == null) {
			this.has_spatial_filter = false;
			return;
		}

		this.has_spatial_filter = true;
		this.sf_converted_to_uor = false;

		this.sf_min_x_geo = XMinXMaxYMinYMax[0];
		this.sf_min_y_geo = XMinXMaxYMinYMax[2];
		this.sf_max_x_geo = XMinXMaxYMinYMax[1];
		this.sf_max_y_geo = XMinXMaxYMinYMax[3];

		DGNSpatialFilterToUOR();
	}

	void DGNSpatialFilterToUOR() {
		DGNPoint sMin = new DGNPoint(), sMax = new DGNPoint();

		if (sf_converted_to_uor || !has_spatial_filter || !got_tcb)
			return;

		sMin.x = sf_min_x_geo;
		sMin.y = sf_min_y_geo;
		sMin.z = 0;

		sMax.x = sf_max_x_geo;
		sMax.y = sf_max_y_geo;
		sMax.z = 0;

		DGNreader.DGNInverseTransformPoint(this, sMin);
		DGNreader.DGNInverseTransformPoint(this, sMax);

		sf_min_x = (int) (sMin.x + DGNlib.MAX_INT);
		sf_min_y = (int) (sMin.y + DGNlib.MAX_INT);
		sf_max_x = (int) (sMax.x + DGNlib.MAX_INT);
		sf_max_y = (int) (sMax.y + DGNlib.MAX_INT);

		sf_converted_to_uor = true;
	}

	// =======================================================================

	/**
	 * This function returns the current file position of the stream stream.
	 * 
	 * This function can fail if the stream doesn't support file positioning, or
	 * if the file position can't be represented in a long int, and possibly for
	 * other reasons as well. If a failure occurs, a value of -1 is returned.
	 * 
	 * @return
	 */
	public long tell() {
		try {
			return raf.getFilePointer();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1L;
	}

	/**
	 * Função que faz a leitura de <code>size</code> * <code>blocks</code> bytes
	 * a partir de <code>offSet</code> e os aloca em {@link DGNfile#abyElem}
	 * 
	 * @param offSet
	 * @param size
	 *            <ol>
	 *            <li>1 para bytes;</i>
	 *            <li>2 para shorts;</i>
	 *            <li>4 para int e float;</i>
	 *            <li>8 para long e double.</i>
	 *            </ol>
	 * @param blocks
	 *            número de blocos
	 * @return número de blocos efetivamente lidos
	 */
	public int read(int offSet, int size, int blocks) {
		return read(this.abyElem, offSet, size, blocks);
	}

	public int read(byte[] bs, int size, int blocks) {
		return read(bs, 0, size, blocks);
	}

	public int read(byte[] bs, int offSet, int size, int blocks) {
		try {
			return raf.read(bs, offSet, size * blocks) / size;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * The fseek function is used to change the file position of the stream
	 * stream.
	 * 
	 * This function returns a value of zero if the operation was successful,
	 * and a nonzero value to indicate failure. A successful call also clears
	 * the end-of-file indicator of stream and discards any characters that were
	 * ``pushed back'' by the use of ungetc.
	 * 
	 * fseek either flushes any buffered output before setting the file position
	 * or else remembers it so it will be written later in its proper place in
	 * the file.
	 * 
	 * @param offset
	 * @return
	 */
	public boolean seek(long offset) {
		try {
			raf.seek(offset);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public int write(byte[] bs, int size, int blocks) {
		return write(bs, 0, size, blocks);
	}

	public int write(byte[] bs, int offSet, int size, int blocks) {
		try {
			raf.write(bs, offSet, size * blocks);
			return blocks;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Rewind element reading.
	 *
	 * Rewind the indicated DGN file, so the next element read with
	 * DGNReadElement() will be the first. Does not require indexing like the
	 * more general DGNReadElement() function.
	 *
	 */
	public void rewind() {
		seek(0);
		next_element_id = 0;
		in_complex_group = false;
	}

	public void close() {
		try {
			this.raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open a DGN file.
	 *
	 * The file is opened, and minimally verified to ensure it is a DGN (ISFF)
	 * file. If the file cannot be opened for read access an error with code
	 * CPLE_OpenFailed with be reported via CPLError() and NULL returned. If the
	 * file header does not appear to be a DGN file, an error with code
	 * CPLE_AppDefined will be reported via CPLError(), and NULL returned.
	 *
	 * If successful a handle for further access is returned. This should be
	 * closed with DGNClose() when no longer needed.
	 *
	 * DGNOpen() does not scan the file on open, and should be very fast even
	 * for large files.
	 *
	 * @param pszFilename
	 *            name of file to try opening.
	 * @param bUpdate
	 *            should the file be opened with read+update (r+) mode?
	 *
	 * @return handle to use for further access to file using DGN API, or NULL
	 *         if open fails.
	 */

	public static DGNfile DGNOpen(String pszFilename, boolean bUpdate) {
		RandomAccessFile raf = null;

		// Open the file.
		try {
			raf = new RandomAccessFile(pszFilename, bUpdate ? "rw" : "r");
		} catch (FileNotFoundException e) {
			System.err.printf("Unable to open `%s' for read access.\n", pszFilename);
			return null;
		}

		// Verify the format ... add later.
		byte[] abyHeader = new byte[512];
		try {
			raf.read(abyHeader, 0, 512);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!DGNTestOpen(abyHeader)) {
			System.err.printf("File `%s' does not have expected DGN header.\n", pszFilename);
			try {
				raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		// rewind
		try {
			raf.seek(0);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// -----------------------------------------------------------

		// Create the info structure.
		return new DGNfile(raf, abyHeader[0] == 0xC8 ? 3 : 2);
	}

	/**
	 * Test if header is DGN.
	 *
	 * @param pabyHeader
	 *            block of header data from beginning of file.
	 * @param nByteCount
	 *            number of bytes in pabyHeader.
	 *
	 * @return TRUE if the header appears to be from a DGN file, otherwise
	 *         FALSE.
	 */

	private static boolean DGNTestOpen(byte[] pabyHeader) {
		// Is it a cell library?
		if (pabyHeader[0] == ((byte) 0x08) && pabyHeader[1] == ((byte) 0x05) && pabyHeader[2] == ((byte) 0x17)
				&& pabyHeader[3] == ((byte) 0x00))
			return true;

		// Is it not a regular 2D or 3D file?
		if ((pabyHeader[0] != ((byte) 0x08) && pabyHeader[0] != ((byte) 0xC8)) || pabyHeader[1] != ((byte) 0x09)
				|| pabyHeader[2] != ((byte) 0xFE) || pabyHeader[3] != ((byte) 0x02))
			return false;

		return true;
	}
}
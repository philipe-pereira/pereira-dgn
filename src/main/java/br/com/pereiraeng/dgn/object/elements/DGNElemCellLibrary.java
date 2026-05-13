package br.com.pereiraeng.dgn.object.elements;

public class DGNElemCellLibrary extends DGNElem {

	/** !< Cell type. */
	short celltype;
	/** !< Attribute linkage. */
	short attindx;
	/** !< Cell name */
	public char[] name = new char[7];

	/** !< Number of words in cell definition */
	public int numwords;

	/** !< Display symbol */
	public short dispsymb;

	/** !< Class bitmap */
	public short cclass;
	/** !< Levels used in cell */
	public short[] levels = new short[4];

	/** !< Description */
	public char[] description = new char[28];
}

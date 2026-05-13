package br.com.pereiraeng.dgn.object.elements;

public class DGNElementInfo {
	public byte level; /* !< Element Level: 0-63 */
	public byte type; /* !< Element type (DGNT_*) */
	public byte stype; /* !< Structure type (DGNST_*) */
	public byte flags; /* !< Other flags */
	public long offset; /* !< Offset within file (private) */
}

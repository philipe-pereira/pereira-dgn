package br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj;

public class DGNTagDef {
	/** !< Name of this tag. */
	public String name;
	/** !< Tag index/identifier. */
	public int id;
	/** !< User prompt when requesting value. */
	public String prompt;
	/**
	 * !< Tag type (one of DGNTT_STRING(1), DGNTT_INTEGER(3) or DGNTT_FLOAT(4).
	 */
	public int type;
	/** !< Default tag value */
	public TagValueUnion defaultValue = new TagValueUnion();
}
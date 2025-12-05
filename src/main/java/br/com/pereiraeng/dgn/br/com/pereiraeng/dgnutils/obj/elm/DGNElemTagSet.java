package br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.elm;

import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.DGNTagDef;

public class DGNElemTagSet extends DGNElem {

	/** !< Number of tags in tagList. */
	public	int tagCount;
	/** !< Tag set index. */
	public int tagSet;
	/** !< Tag flags - not too much known. */
	public	int flags;
	/** !< Tag set name. */
	public	String tagSetName;
	/** !< List of tag definitions in this set. */
	public DGNTagDef[] tagList;

}

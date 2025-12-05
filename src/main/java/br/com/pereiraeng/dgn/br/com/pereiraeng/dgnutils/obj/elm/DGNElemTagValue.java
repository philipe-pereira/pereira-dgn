package br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.elm;

import br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.TagValueUnion;

public class DGNElemTagValue extends DGNElem {

	/**
	 * !< Tag type indicator, DGNTT_*
	 */
	public int tagType;

	/**
	 * !< Which tag set does this relate to?
	 */
	public int tagSet;

	/**
	 * !< Tag index within tag set.
	 */
	public int tagIndex;

	/**
	 * !< Length of tag information (text)
	 */
	public int tagLength;

	/**
	 * !< Textual value of tag
	 */
	public TagValueUnion tagValue = new TagValueUnion();
}

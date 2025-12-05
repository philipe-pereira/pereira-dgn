package br.com.pereiraeng.dgn.br.com.pereiraeng.dgnutils.obj.elm;

import java.util.LinkedList;

public class DGNElemSharedCellDefn extends DGNElem {
	/*
	 * !< Total length of cell in words, excluding the first 19 words (header +
	 * totlength field)
	 */
	public int totlength;
	// FIXME: Most of the contents of this element type is currently
	// unknown. Please get in touch with the authors if you have
	// information about how to decode this element.

	public String name;

	public LinkedList<DGNElem> elems;

	public void add(DGNElem d) {
		if (elems == null)
			elems = new LinkedList<>();
		elems.add(d);
	}
}

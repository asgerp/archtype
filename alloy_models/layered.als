module layered


// General model
abstract sig Configuration { elements: set Element }
abstract sig Element { references: set Element }

// Layered style
 sig Layer extends Element {
	layer_no: one Int
 }

// A layer can not reference it self
fact{ all l: Layer | l not in l.references}

// layer numbering must be non zero
fact{all l: Layer | l.layer_no > 0}

// a layer must only reference layers directly above and below
fact{all l:Layer | all refs: l.references | all l_no : refs.layer_no |
	minus[l_no,l.layer_no] = 1  or
	minus[l_no,l.layer_no] = 0 or
	minus[l_no,l.layer_no] = -1
}


pred layered_style [c: Configuration] {
	all l: c.elements & Layer | all refs: l.references |
	all l_no : refs.layer_no |
	minus[l_no,l.layer_no] = 1  or
	minus[l_no,l.layer_no] = 0 or
	minus[l_no,l.layer_no] = -1
	//(refs.layer_no ) = l.layer_no +1

}

run layered_style for 1 Configuration, 6 Layer
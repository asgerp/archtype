module layered


// General model
abstract sig Configuration { elements: set Element }
abstract sig Element { references: set Element }

// Layered style
 sig Layer extends Element {
	layer_no: one Int
 }

fact{ all l: Layer | l not in l.references}

fact{all l: Layer | l.layer_no > 0}

fact{all l:Layer | all refs: l.references | all l_no : refs.layer_no |
	minus[l_no,l.layer_no] = 1  or
	minus[l_no,l.layer_no] = 0 or
	minus[l_no,l.layer_no] = -1
}


//fact {all l: Layer | all refs: l.references | int[refs.layer_no] - 1 = int[l.layer_no] or  int[refs.layer_no] + 1 = int[l.layer_no] }

//fact{all l: Layer | some l.layer_no: Int | int layer_no > 0}

pred layered_style [c: Configuration] {
	all l: c.elements & Layer | all refs: l.references |
	all l_no : refs.layer_no |
	minus[l_no,l.layer_no] = 1  or
	minus[l_no,l.layer_no] = 0 or
	minus[l_no,l.layer_no] = -1
	//(refs.layer_no ) = l.layer_no +1

}

module pipes_and_filters
// General model
abstract sig Configuration { elements: set Element }
abstract sig Element { pipes: set Element }

abstract sig Filter extends Element { }

// all filters should have some pipes
//fact {all f:Filter | some p: some f.pipes | #p >= 1 }

fact { no f :Filter | f in f.^pipes }

pred filters [c: Configuration]{
	all f:c.elements & Filter | some f
}

pred acyclic [c: Configuration] {
	all f :Filter | f not in f.^pipes
}

pred pipes_and_filters [c: Configuration] {
	//	filters[c] //acyclic[c]
}

run pipes_and_filters for 1 Configuration, 8 Filter}





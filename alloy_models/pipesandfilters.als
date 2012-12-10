module pipesandfilters
// General model
abstract sig Configuration {
 	elements: set Filter 
}
abstract sig Filter  {
	pipes: set Filter
}
sig Start extends Filter{} {}

// acyclic graph
//fact { no f :Filter | f in f.^pipes}

//fact {	all f:Filter | all p: f.pipes | #p >= 1 }


pred ac[c:Configuration] {
	no f :c.elements & Filter | f in f.^pipes
}

pred reach[c:Configuration] {
	Filter in Start.*pipes
}

pred pipes_and_filters [c: Configuration] {
	ac[c] 
}



run pipes_and_filters for 1 Configuration, 10 Filter





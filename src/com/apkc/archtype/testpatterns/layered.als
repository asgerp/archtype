module layered


// General model
abstract sig Configuration { elements: set Element }
abstract sig Element { references: set Element }

// Layered style
sig LayerDATA extends Element { }

sig LayerBL extends Element { }

sig LayerPRESENT extends Element { }



pred layered_layerdata_style [c: Configuration] {
	all m: c.elements & LayerDATA | all r: m.references | r not in LayerPRESENT
}
pred layered_layerpresent_style [c: Configuration] {
	all view: c.elements & LayerPRESENT | all ref: view.references | ref not in LayerDATA
}
pred layered_layerbl_style [c: Configuration] {
	
}
pred layered_style [c: Configuration]{
	layered_layerpresent_style[c] layered_layerdata_style[c]
}


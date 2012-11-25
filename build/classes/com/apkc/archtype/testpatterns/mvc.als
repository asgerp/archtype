module mvc
// General model
abstract sig Configuration { elements: set Element }
abstract sig Element { references: set Element }

// MVC Style
abstract sig Model extends Element { }
abstract sig View extends Element { }
abstract sig Controller extends Element { }

pred mvc_model_style [c: Configuration] {
	all m: c.elements & Model | all r: m.references | r not in View	
}
pred mvc_view_style [c: Configuration] {
	all view: c.elements & View | all ref: view.references | ref not in Model
}

pred mvc_style [c: Configuration] {
	mvc_model_style[c] and mvc_view_style[c]
}



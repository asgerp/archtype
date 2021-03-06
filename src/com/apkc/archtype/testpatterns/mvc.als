module mvc
// General model
abstract sig Configuration { elements: set Element }
abstract sig Element { references: set Element }

// MVC Style
some sig Model extends Element { }
some sig View extends Element { }
some sig Controller extends Element { }

pred mvc_model_style [c: Configuration] {
	all m: c.elements & Model | all r: m.references | r not in View
}
pred mvc_view_style [c: Configuration] {
	all view: c.elements & View | all ref: view.references | ref not in Model
}
pred mvc_controller_style [c: Configuration] {
	
}
pred mvc_style [c: Configuration]{
	 mvc_model_style[c] mvc_view_style[c]
}


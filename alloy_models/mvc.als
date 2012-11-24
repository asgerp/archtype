module mvc
// General model
abstract sig Configuration { elements: set Element }
abstract sig Element { references: set Element }

// MVC Style
abstract sig Model extends Element { }
abstract sig View extends Element { }
abstract sig Controller extends Element { }

pred mvc_style [c: Configuration] {
	all m: c.elements & Model | all r: m.references | r not in View	
	all m: c.elements & View | m.references not in Model	
}



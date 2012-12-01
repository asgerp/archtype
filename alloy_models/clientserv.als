module clientserv
abstract sig Configuration { elements: set Element }
abstract sig Element { references: set Element }

// Client Server Style
abstract sig Client extends Element { }
abstract sig Server extends Element { }
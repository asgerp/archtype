module adapter

abstract sig Configuration { elements: set Element }
abstract sig Element { references: set Element }

// adapter style
abstract sig Client extends Element { }
abstract sig Target extends Element { }
abstract sig Adapter extends Element { }
abstract sig Adaptee extends Element { }


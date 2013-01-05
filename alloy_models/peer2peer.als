module peer2peer

// General model
abstract sig Configuration { elements: set Element }
abstract sig Element { references: set Element }

abstract sig Peer extends Element { }

//all peers must be able to reach all peers

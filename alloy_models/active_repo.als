module active_repo

// General model
abstract sig Configuration { elements: set Element }
abstract sig Element { }

// Shared repo style
sig Active_repo extends Element { references: set Element }
sig Client extends Element { references: one Element }

// enforce client <-> repo referencing
pred refs [c: Configuration] { 
		all cl: c.elements & Client, s: c.elements & Active_repo | 
			s in cl.references <=> cl in s.references 
}
// at least one client per repo
pred gt1Client [c: Configuration] {
	all s: c.elements & Active_repo | #s.references > 0
}

// no client <-> client referencing * throws warning
pred noC2C [c: Configuration] { all cl:c.elements & Client | cl.references not in Client } 

// no repo <-> repo referencing * throws warning
pred noR2R [c: Configuration] { all s:c.elements & Active_repo | s.references not in Active_repo }

// main predicates
pred active_repo_active_repo_style [c:Configuration] {
	refs[c] noR2R[c] 
}

pred active_repo_client_style [c: Configuration] {
	gt1Client[c] noC2C[c]
}



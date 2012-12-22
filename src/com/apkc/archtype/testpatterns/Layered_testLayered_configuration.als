open layered
one sig testLayered extends Configuration { } {
	elements = Layer1 + Layer2 + Layer3
}
one sig Layer1 extends LayerDATA { } {
	references = Layer2
}
one sig Layer2 extends LayerBL { } {
	references = Layer1
}
one sig Layer3 extends LayerPRESENT { } {
	references = Layer1 + Layer2
}
assert layer1 {
	layered_layerdata_style[testLayered]
}
assert layer2 {
	layered_layerbl_style[testLayered]
}
assert layer3 {
	layered_layerpresent_style[testLayered]
}
check layer1 for 8
check layer2 for 8
check layer3 for 8

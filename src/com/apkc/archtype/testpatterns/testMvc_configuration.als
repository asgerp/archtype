open mvc
one sig testMvc extends Configuration { } {
	elements = TestController + TestModel
}
one sig TestController extends Controller { } {
	references = TestView + TestModel
}
one sig TestModel extends Model { } {
	references = TestController
}
assert conforms {
	mvc_style[testMvc]
}
check conforms

open mvc
one sig testMvc extends Configuration { } {
	elements = TestController + TestView + TestModel
}
one sig TestController extends Controller { } {
	references = TestView + TestModel + TestController
}
one sig TestView extends View { } {
	references = TestController
}
one sig TestModel extends Model { } {
	references = TestController + TestView
}
assert conforms {
	mvc_style[testMvc]
}
check conforms for 8

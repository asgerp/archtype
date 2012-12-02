open mvc
one sig testMvc extends Configuration { } {
	elements = TestController + TestView + TestModel + TestController3
}
one sig TestController extends Controller { } {
	references = TestView + TestModel
}
one sig TestView extends View { } {
	references = TestController
}
one sig TestModel extends Model { } {
	references = TestController + TestView
}
one sig TestController3 extends Controller { } {
	references = TestView + TestController
}
assert testcontroller {
	mvc_controller_style[testMvc]
}
assert testview {
	mvc_view_style[testMvc]
}
assert testmodel {
	mvc_model_style[testMvc]
}
assert testcontroller3 {
	mvc_controller_style[testMvc]
}
check testcontroller for 8
check testview for 8
check testmodel for 8
check testcontroller3 for 8

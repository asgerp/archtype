/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apkc.archtype.testpatterns;

import com.apkc.archtype.quals.*;
/**
 *
 * @author asger
 */
@Component(
        name="TestController",
        patterns = {@Pattern(name="testMvc",kind = "MVC", role="Controller", references={"TestController","TestController", "TestModel"})}
        )
public class TestController {
    TestController testControlControl;
    TestController testViewControl;
    TestModel testModelControl;

    
    public TestController() {
    }

}

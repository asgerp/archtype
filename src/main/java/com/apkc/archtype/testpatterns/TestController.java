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
        patterns = {@Pattern(name = "MVC", role="Controller"),@Pattern(name="Client/Server", role="Client")}
        )
public class TestController {

}

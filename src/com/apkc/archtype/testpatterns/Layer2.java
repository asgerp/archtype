/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apkc.archtype.testpatterns;

import com.apkc.archtype.quals.Component;
import com.apkc.archtype.quals.Pattern;

/**
 *
 * @author asger
 */
@Component(
        patterns = {@Pattern(name="testLayered", kind = "Layered", role="LayerBL")}
        )
public class Layer2 {
    Layer1 layer1;
    //Layer3 layer3;

    public Layer2() {
    }

    public Layer1 getLayer1() {
        return layer1;
    }

    public void setLayer1(Layer1 layer1, Layer3 l3) {
        this.layer1 = layer1;
    }

    public void getLayer3() {
        //return layer3;
    }

    //public void setLayer3(Layer3 layer3) {
        //this.layer3 = layer3;
    //}

    

}
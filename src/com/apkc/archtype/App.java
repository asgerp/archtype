package com.apkc.archtype;

import com.apkc.archtype.quals.*;
import java.lang.annotation.Annotation;


/**
 * Test annotation and reflection and like stuff
 *
 */
@Component(
        name = "TestAnno",
        patterns = {@Pattern(kind = "MVC",name="tes", role="Model"), @Pattern(name="tes",kind = "ClientServer", role= "client")}
        )
public class App 
{
    public static void main( String[] args ) throws NoSuchFieldException
    {
        // We need to use getDeclaredField here since the field is private.
      Class aClass = App.class;
      Annotation[] anns = aClass.getAnnotations();
      for(Annotation annotation: anns){
          if(annotation instanceof Component){
              Component compo = (Component) annotation;
              System.out.println("name: " + compo.name());
              for(Pattern p : compo.patterns()){
                System.out.println("pattern name: " + p.name());
                System.out.println("pattern role: " + p.role());
              }
          }
      }  
    }
}

package com.apkc.archtype;

import com.apkc.archtype.quals.Component;
import java.lang.annotation.Annotation;

/**
 * Hello world!
 *
 */
@Component(
        name = "TestAnno",
        patterns = {"MVC","Adapter","Factory"}
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
              for(String p : compo.patterns()){
                System.out.println("pattern: " + p);
              }
          }
      }  
    }
}

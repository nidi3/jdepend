package jdepend.framework;

import jdepend.framework.p1.ExampleInnerAnnotation;
import jdepend.framework.p2.ExampleEnum;
import jdepend.framework.p3.ExampleSecondEnum;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static jdepend.framework.p3.ExampleSecondEnum.NO_DEPENDENCIES_ON_ME;

@Retention(RUNTIME)
public @interface ExampleAnnotation {

    Class<?> c1() default Object.class;

    Class<?> c2() default Object.class;

    ExampleInnerAnnotation c3();

    ExampleEnum c4();

    ExampleSecondEnum c5() default NO_DEPENDENCIES_ON_ME;

}

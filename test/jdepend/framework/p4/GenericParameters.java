package jdepend.framework.p4;

import jdepend.framework.p4.p1.Type1;
import jdepend.framework.p4.p10.Type10;
import jdepend.framework.p4.p2.Type2;
import jdepend.framework.p4.p3.Type3;
import jdepend.framework.p4.p4.Type4;
import jdepend.framework.p4.p5.Type5;
import jdepend.framework.p4.p6.Type6;
import jdepend.framework.p4.p7.Type7;
import jdepend.framework.p4.p8.Type8;
import jdepend.framework.p4.p9.Type9;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class GenericParameters<T> {
    private List<Type2> list;
    private List<?> l2 = new ArrayList<Type3>();

    private <E extends Type4 & Type6, X> List<E> method4(Type7 t7, X x) {
        return null;
    }

    private List<? extends Type4> method42() throws Type8 {
        return null;
    }

    private <E extends Type9> void method5(List<? super Type5> param, List<Type1.Type1Sub<String>> sub) throws E {
    }

    private List<Type10[]> method6(List<int[]> a) {
        return null;
    }
}


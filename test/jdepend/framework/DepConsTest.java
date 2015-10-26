package jdepend.framework;

/**
 *
 */
public class DepConsTest {
    public void test(){
        final DepCons dc = DepCons.allowAll();
        final JPack a = dc.addPack("a");
        final JPack b = dc.addPack("b");
//        a.dontDepend(b)
    }
}

package jdepend.framework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class DepCons {
    private final List<JPack> packs = new ArrayList<JPack>();
    private final boolean allowAll;

    private DepCons(boolean allowAll) {
        this.allowAll = allowAll;
    }

    public static DepCons allowAll() {
        return new DepCons(true);
    }

    public static DepCons denyAll() {
        return new DepCons(false);
    }

    public JPack addPack(String pack) {
        final JPack jpack = new JPack(pack, allowAll);
        packs.add(jpack);
        return jpack;
    }

    public JPack addPack(JPack pack) {
        packs.add(pack);
        return pack;
    }

    public DependencyConstraint.MatchResult match(Collection<JavaPackage> testPacks) {
        for (final JPack pack : packs) {
            for (final JavaPackage testPack : testPacks) {
                boolean matched = false;
                if (pack.matches(testPack.getName())) {
                    matched = true;

                }
            }
//            if (!matched && !allowAll) {
//
//            }
        }
        return null;
    }
}
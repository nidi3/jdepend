package jdepend.framework;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class JPack {
    private final String name;
    private final boolean allowAll;
    private final List<JPack> mustDepend = new ArrayList<JPack>();
    private final List<JPack> mayDepend = new ArrayList<JPack>();
    private final List<JPack> dontDepend = new ArrayList<JPack>();

    JPack(String name, boolean allowAll) {
        this.name = name;
        this.allowAll = allowAll;
    }

    public static JPack allowAll(String name) {
        return new JPack(name, true);
    }

    public static JPack denyAll(String name) {
        return new JPack(name, false);
    }

    public JPack mustDepend(JPack pack) {
        mustDepend.add(pack);
        return this;
    }

    public JPack mayDepend(JPack pack) {
        mayDepend.add(pack);
        return this;
    }

    public JPack dontDepend(JPack pack) {
        dontDepend.add(pack);
        return this;
    }

    public boolean matches(String name) {
        return this.name.endsWith("*")
                ? name.startsWith(this.name.substring(0, this.name.length() - 1))
                : name.equals(this.name);
    }
}

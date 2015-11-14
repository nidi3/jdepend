package jdepend.framework.rule;


import jdepend.framework.JavaPackage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class DependencyRules {
    private final List<PackageRule> rules = new ArrayList<PackageRule>();
    private final boolean allowAll;

    private DependencyRules(boolean allowAll) {
        this.allowAll = allowAll;
    }

    public static DependencyRules allowAll() {
        return new DependencyRules(true);
    }

    public static DependencyRules denyAll() {
        return new DependencyRules(false);
    }

    public PackageRule addRule(String pack) {
        final PackageRule rule = new PackageRule(pack, allowAll);
        rules.add(rule);
        return rule;
    }

    public PackageRule addRule(PackageRule pack) {
        rules.add(pack);
        return pack;
    }

    /**
     * Add rules defined by a RuleDefiner class. The following DependencyRules are all equal:
     * <pre>
     * DependencyRules rules1 = DependencyRules.allowAll();
     * PackageRule a = rules1.addRule("com.acme.a.*"));
     * PackageRule b = rules1.addRule("com.acme.sub.b"));
     * a.mustNotDependUpon(b);
     * </pre>
     * ----
     * <pre>
     * class ComAcme implements RuleDefiner{
     *     PackageRule a_, subB;
     *
     *     public defineRules(){
     *         a_.mustNotDependUpon(subB);
     *     }
     * }
     * DependencyRules rules2 = DependencyRules.allowAll().addRules(new ComAcme());
     * </pre>
     * ----
     * <pre>
     * DependencyRules rules3 = DependencyRules.allowAll().addRules("com.acme", new RuleDefiner(){
     *     PackageRule a_, subB;
     *
     *     public defineRules(){
     *         a_.mustNotDependUpon(subB);
     *     }
     * });
     * </pre>
     *
     * @param basePackage
     * @param definer
     * @return
     */
    public DependencyRules withRules(String basePackage, RuleDefiner definer) {
        try {
            for (Field f : definer.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                if (f.getType() == PackageRule.class) {
                    final String name = definer.getClass().isAnonymousClass()
                            ? ""
                            : camelCaseToDotCase(definer.getClass().getSimpleName());
                    final String start = basePackage.length() > 0 && !basePackage.endsWith(".") && name.length() > 0
                            ? basePackage + "." + name
                            : basePackage + name;
                    f.set(definer, addRule(start + (f.getName().equals("self") ? "" : ("." + camelCaseToDotCase(f.getName())))));
                }
            }
            definer.defineRules();
            return this;
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not access field", e);
        }
    }

    public DependencyRules withRules(RuleDefiner... definers) {
        for (final RuleDefiner definer : definers) {
            withRules("", definer);
        }
        return this;
    }

    private static String camelCaseToDotCase(String s) {
        final StringBuilder res = new StringBuilder();
        final boolean dollarMode = s.contains("$");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '_' && i == s.length() - 1) {
                res.append(res.charAt(res.length() - 1) == '.' ? "*" : ".*");
            } else {
                if (dollarMode) {
                    if (c == '$' && i > 0) {
                        res.append(".");
                    } else {
                        res.append(c);
                    }
                } else {
                    if (Character.isUpperCase(c)) {
                        if (i > 0) {
                            res.append(".");
                        }
                        res.append(Character.toLowerCase(c));
                    } else {
                        res.append(c);
                    }
                }
            }
        }
        return res.toString();
    }

    //TODO existing packages without rule
    //TODO detect circles
    public RuleResult analyze(Collection<JavaPackage> testPacks) {
        RuleResult result = new RuleResult();
        for (final PackageRule rule : rules) {
            result.merge(rule.analyze(testPacks));
        }
        result.normalize();
        return result;
    }

}
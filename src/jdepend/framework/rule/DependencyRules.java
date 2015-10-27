package jdepend.framework.rule;


import jdepend.framework.JavaPackage;

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

    public RuleResult analyze(Collection<JavaPackage> testPacks) {
        RuleResult result = new RuleResult();
        for (final PackageRule rule : rules) {
            result.merge(rule.analyze(testPacks));
        }
        result.normalize();
        return result;
    }


}
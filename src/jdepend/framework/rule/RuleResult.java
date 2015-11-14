package jdepend.framework.rule;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class RuleResult {
    final DependencyMap allowed;
    final DependencyMap missing;
    final DependencyMap denied;
    final Set<String> notExisting;
    final Set<String> undefined;

    public RuleResult() {
        this(new DependencyMap(), new DependencyMap(), new DependencyMap(), new HashSet<String>(), new HashSet<String>());
    }

    public RuleResult(DependencyMap allowed, DependencyMap missing, DependencyMap denied, Set<String> notExisting, Set<String> undefined) {
        this.allowed = allowed;
        this.missing = missing;
        this.denied = denied;
        this.notExisting = notExisting;
        this.undefined = undefined;
    }

    public void merge(RuleResult cr) {
        allowed.merge(cr.allowed);
        missing.merge(cr.missing);
        denied.merge(cr.denied);
        notExisting.addAll(cr.notExisting);
        undefined.addAll(cr.undefined);
    }

    // an explicitly allowed dependency is stronger than any denial
    public void normalize() {
        denied.without(allowed);
        allowed.clear();
    }

    public DependencyMap getAllowed() {
        return allowed;
    }

    public DependencyMap getMissing() {
        return missing;
    }

    public DependencyMap getDenied() {
        return denied;
    }

    public Set<String> getNotExisting() {
        return notExisting;
    }

    public Set<String> getUndefined() {
        return undefined;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RuleResult that = (RuleResult) o;

        if (!allowed.equals(that.allowed)) {
            return false;
        }
        if (!missing.equals(that.missing)) {
            return false;
        }
        if (!denied.equals(that.denied)) {
            return false;
        }
        if (!notExisting.equals(that.notExisting)) {
            return false;
        }
        return undefined.equals(that.undefined);

    }

    @Override
    public int hashCode() {
        int result = allowed.hashCode();
        result = 31 * result + missing.hashCode();
        result = 31 * result + denied.hashCode();
        result = 31 * result + notExisting.hashCode();
        result = 31 * result + undefined.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RuleResult{" +
                "allowed=" + allowed +
                ", missing=" + missing +
                ", denied=" + denied +
                ", notExisting=" + notExisting +
                ", undefined=" + undefined +
                '}';
    }
}

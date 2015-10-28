package jdepend.framework.rule;

/**
 *
 */
public class RuleResult {
    final DependencyMap allowed;
    final DependencyMap missing;
    final DependencyMap denied;

    public RuleResult() {
        this(new DependencyMap(), new DependencyMap(), new DependencyMap());
    }

    public RuleResult(DependencyMap allowed, DependencyMap missing, DependencyMap denied) {
        this.allowed = allowed;
        this.missing = missing;
        this.denied = denied;
    }

    public void merge(RuleResult cr) {
        allowed.merge(cr.allowed);
        missing.merge(cr.missing);
        denied.merge(cr.denied);
    }

    // an explicitly allowed dependency is stronger than any denial
    public void normalize(){
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
        return denied.equals(that.denied);

    }

    @Override
    public int hashCode() {
        int result = allowed.hashCode();
        result = 31 * result + missing.hashCode();
        result = 31 * result + denied.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ConstraintResult{" +
                "allowed=" + allowed +
                ", missing=" + missing +
                ", denied=" + denied +
                '}';
    }
}

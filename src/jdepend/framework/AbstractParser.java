package jdepend.framework;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>AbstractParser</code> class is the base class
 * for classes capable of parsing files to create a
 * <code>JavaClass</code> instance.
 *
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public abstract class AbstractParser {

    private List<ParserListener> parseListeners;
    private PackageFilter filter;
    public static boolean DEBUG = false;


    public AbstractParser() {
        this(PackageFilter.all().excludingProperties());
    }

    public AbstractParser(PackageFilter filter) {
        setFilter(filter);
        parseListeners = new ArrayList<ParserListener>();
    }

    public void addParseListener(ParserListener listener) {
        parseListeners.add(listener);
    }

    /**
     * Registered parser listeners are informed that the resulting
     * <code>JavaClass</code> was parsed.
     */
    public abstract JavaClass parse(InputStream is) throws IOException;

    /**
     * Informs registered parser listeners that the specified
     * <code>JavaClass</code> was parsed.
     *
     * @param jClass Parsed Java class.
     */
    protected void onParsedJavaClass(JavaClass jClass) {
        for (ParserListener parseListener : parseListeners) {
            parseListener.onParsedJavaClass(jClass);
        }
    }

    protected PackageFilter getFilter() {
        if (filter == null) {
            setFilter(PackageFilter.all().excludingProperties());
        }
        return filter;
    }

    protected void setFilter(PackageFilter filter) {
        this.filter = filter;
    }

    protected void debug(String message) {
        if (DEBUG) {
            System.err.println(message);
        }
    }
}
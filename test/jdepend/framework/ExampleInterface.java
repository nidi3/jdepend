package jdepend.framework;

import java.math.BigDecimal;

/**
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public interface ExampleInterface {

    void a();

    java.util.Vector b(String[] s, java.text.NumberFormat nf);

    void c(BigDecimal bd, byte[] b) throws java.rmi.RemoteException;

    java.io.File[] d() throws java.io.IOException;

}
package org.eclipse.rse.tests.persistence;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Main class bundling all RSE connection test cases.
 */
public class RSEPersistenceTestSuite {

	/**
	 * Standard Java application main method. Allows to launch the test
	 * suite from outside as part of nightly runs, headless runs or other.
	 * <p><b>Note:</b> Use only <code>junit.textui.TestRunner</code> here as
	 * it is explicitly supposed to output the test output to the shell the
	 * test suite has been launched from.
	 * <p>
	 * @param args The standard Java application command line parameters passed in.
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	/**
	 * Combine all test into a suite and returns the test suite instance.
	 * <p>
	 * <b>Note: This method must be always called <i><code>suite</code></i> ! Otherwise
	 * the JUnit plug-in test launcher will fail to detect this class!</b>
	 * <p>
	 * @return The test suite instance.
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite("RSE Persistence Test Suite"); //$NON-NLS-1$
		// add the single test suites to the overall one here.
		suite.addTestSuite(RSEPersistenceTest.class);
		
		return suite;
	}
}

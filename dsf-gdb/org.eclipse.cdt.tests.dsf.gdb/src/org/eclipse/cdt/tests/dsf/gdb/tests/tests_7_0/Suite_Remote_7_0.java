/*******************************************************************************
 * Copyright (c) 2009 Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ericsson			  - Initial Implementation
 *******************************************************************************/
package org.eclipse.cdt.tests.dsf.gdb.tests.tests_7_0;

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.tests.dsf.gdb.framework.BaseTestCase;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This class is meant to be empty.  It enables us to define
 * the annotations which list all the different JUnit class we
 * want to run.  When creating a new test class, it should be
 * added to the list below.
 * 
 *  This suite is for tests to be run with GDB 7.0
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// We need specific name for the tests of this suite, because of bug https://bugs.eclipse.org/172256
	GDBRemoteTracepointsTest_7_0.class,
	MIRegistersTest_7_0.class,
	MIRunControlTest_7_0.class,
	MIExpressionsTest_7_0.class,
	MIMemoryTest_7_0.class,
	MIBreakpointsTest_7_0.class,
	MIDisassemblyTest_7_0.class,
	GDBProcessesTest_7_0.class
	/* Add your test class here */
})

public class Suite_Remote_7_0 {
	@BeforeClass
    public static void beforeClassMethod() {
		BaseTestCase.setLaunchAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_START_MODE,
				                        IGDBLaunchConfigurationConstants.DEBUGGER_MODE_REMOTE);
	}
}

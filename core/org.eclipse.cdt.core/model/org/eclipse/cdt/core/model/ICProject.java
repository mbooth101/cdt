package org.eclipse.cdt.core.model;

/*
 * (c) Copyright QNX Software Systems Ltd. 2002.
 * All Rights Reserved.
 */

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A C project represents a view of a project resource in terms of C 
 * elements such as , ICContainer, ITranslationUnit ....
 * <code>CCore.create(project)</code>.
 * </p>
 *
 * @see CCore#create(org.eclipse.core.resources.IProject)
 * @see IBuildEntry
 */
public interface ICProject extends ICContainer {

	/**
	 * Returns the <code>ICElement</code> corresponding to the given
	 * path, or <code>null</code> if no such 
	 * <code>ICElement</code> is found.
	 *
	 * @exception CModelException if the given path is <code>null</code>
	 *  or absolute
	 */
	ICElement findElement(IPath path) throws CModelException;

	/**
	 * Return the ArchiveContainer of this Project.
	 */
	IArchiveContainer getArchiveContainer();

	/**
	 * Return the BinaryContainer of this Project.
	 */
	IBinaryContainer getBinaryContainer();

	/**
	 * Return the library references for this project.
	 * 
	 * @return [] ILibraryReference
	 */
	ILibraryReference[] getLibraryReferences() throws CModelException;

	/**
	 * Returns the names of the projects that are directly required by this
	 * project. A project is required if it is in its cpath entries.
	 * <p>
	 * The project names are returned in the order they appear on the cpathentries.
	 *
	 * @return the names of the projects that are directly required by this project
	 * @exception CModelException if this element does not exist or if an
	 *              exception occurs while accessing its corresponding resource
	 */
	String[] getRequiredProjectNames() throws CModelException;

	/**
	 * 
	 * @return IProject
	 */
	IProject getProject();

	/**
	 * Helper method for returning one option value only. Equivalent to <code>(String)this.getOptions(inheritCCoreOptions).get(optionName)</code>
	 * Note that it may answer <code>null</code> if this option does not exist, or if there is no custom value for it.
	 * <p>
	 * For a complete description of the configurable options, see <code>CCorePlugin#getDefaultOptions</code>.
	 * </p>
	 * 
	 * @param optionName the name of an option
	 * @param inheritCCoreOptions - boolean indicating whether CCorePlugin options should be inherited as well
	 * @return the String value of a given option
	 * @see CCorePlugin#getDefaultOptions
	 */
	String getOption(String optionName, boolean inheritCCoreOptions);

	/**
	 * Returns the table of the current custom options for this project. Projects remember their custom options,
	 * in other words, only the options different from the the CCorePlugin global options for the workspace.
	 * A boolean argument allows to directly merge the project options with global ones from <code>CCorePlugin</code>.
	 * <p>
	 * For a complete description of the configurable options, see <code>CCorePlugin#getDefaultOptions</code>.
	 * </p>
	 * 
	 * @param inheritCCoreOptions - boolean indicating whether CCorePlugin options should be inherited as well
	 * @return table of current settings of all options 
	 *   (key type: <code>String</code>; value type: <code>String</code>)
	 * @see CCorePlugin#getDefaultOptions
	 */
	Map getOptions(boolean inheritCCoreOptions);

	/**
	 * Helper method for setting one option value only. Equivalent to <code>Map options = this.getOptions(false); map.put(optionName, optionValue); this.setOptions(map)</code>
	 * <p>
	 * For a complete description of the configurable options, see <code>CCorePlugin#getDefaultOptions</code>.
	 * </p>
	 * 
	 * @param optionName the name of an option
	 * @param optionValue the value of the option to set
	 * @see CCorePlugin#getDefaultOptions
	 */
	void setOption(String optionName, String optionValue);

	/**
	 * Sets the project custom options. All and only the options explicitly included in the given table 
	 * are remembered; all previous option settings are forgotten, including ones not explicitly
	 * mentioned.
	 * <p>
	 * For a complete description of the configurable options, see <code>CCorePlugin#getDefaultOptions</code>.
	 * </p>
	 * 
	 * @param newOptions the new options (key type: <code>String</code>; value type: <code>String</code>),
	 *   or <code>null</code> to flush all custom options (clients will automatically get the global CCorePlugin options).
	 * @see CCorePlugin#getDefaultOptions
	 */
	void setOptions(Map newOptions);

	/**
	 * Returns the list of entries for the project. This corresponds to the exact set
	 * of entries which were assigned using <code>setCPathEntries</code>.
	 * <p>
	 *
	 * @return the list of entries for the project.
	 * @exception CModelException if this element does not exist or if an
	 *              exception occurs while accessing its corresponding resource
	 */
	ICPathEntry[] getCPathEntries() throws CModelException;

	/**
	 * Sets the entries for this project.
	 *
	 * @param entries a list of ICPathEntry[] entries
	 * @param monitor the given progress monitor
	 * @exception CModelException if the entries could not be set. Reasons include:
	 * <ul>
	 * <li> This C/C++ element does not exist (ELEMENT_DOES_NOT_EXIST)</li>
	 * <li> The entries are being modified during resource change event notification (CORE_EXCEPTION)
	 * </ul>
	 */
	void setCPathEntries(ICPathEntry[] entries, IProgressMonitor monitor) throws CModelException;

}

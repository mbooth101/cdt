package org.eclipse.cdt.make.core;
/**********************************************************************
 * Copyright (c) 2002,2003 Rational Software Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: 
 * QNX Software Systems - Initial API and implementation
 * IBM Rational Software - Initial API and implementation
***********************************************************************/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;

public class BuildInfoFactory {

	private static final String PREFIX = MakeCorePlugin.getUniqueIdentifier();

	private static final String BUILD_COMMAND = PREFIX + ".buildCommand";
	private static final String BUILD_LOCATION = PREFIX + ".buildLocation";
	private static final String STOP_ON_ERROR = PREFIX + ".stopOnError";
	private static final String USE_DEFAULT_BUILD_CMD = PREFIX + ".useDefaultBuildCmd";
	private static final String BUILD_TARGET_AUTO = PREFIX + ".autoBuildTarget";
	private static final String BUILD_TARGET_INCREMENTAL = PREFIX + ".incrementalBuildTarget";
	private static final String BUILD_TARGET_FULL = PREFIX + ".fullBuildTarget";
	private static final String BUILD_FULL_ENABLED = PREFIX + ".enableFullBuild";
	private static final String BUILD_INCREMENTAL_ENABLED = PREFIX + ".enabledIncrementalBuild";
	private static final String BUILD_AUTO_ENABLED = PREFIX + ".enableAutoBuild";
	private static final String BUILD_ARGUMENTS = PREFIX + ".buildArguments";

	private abstract static class Store implements IMakeBuilderInfo, IScannerInfo {
		// List of include paths
		protected List pathList;
		protected List symbolList;

		public void setUseDefaultBuildCmd(boolean on) throws CoreException {
			putValue(USE_DEFAULT_BUILD_CMD, new Boolean(on).toString());
		}

		public boolean isDefaultBuildCmd() {
			if (getString(USE_DEFAULT_BUILD_CMD) == null) { // if no property then default to true
				return true;
			}
			return getBoolean(USE_DEFAULT_BUILD_CMD);
		}

		public void setBuildCommand(IPath location) throws CoreException {
			putValue(BUILD_COMMAND, location.toString());
		}

		public IPath getBuildCommand() {
			if (isDefaultBuildCmd()) {
				String command = getBuildParameter("defaultCommand");
				if (command == null) {
					return new Path("make");
				}
				return new Path(command);
			}
			return new Path(getString(BUILD_COMMAND));
		}

		protected String getBuildParameter(String name) {
			IExtension extension =
				Platform.getPluginRegistry().getExtension(
					ResourcesPlugin.PI_RESOURCES,
					ResourcesPlugin.PT_BUILDERS,
					getBuilderID());
			if (extension == null)
				return null;
			IConfigurationElement[] configs = extension.getConfigurationElements();
			if (configs.length == 0)
				return null;
			//The nature exists, or this builder doesn't specify a nature
			IConfigurationElement[] runElement = configs[0].getChildren("run");
			IConfigurationElement[] paramElement = runElement[0].getChildren("parameter");
			for (int i = 0; i < paramElement.length; i++) {
				if (paramElement[i].getAttribute("name").equals(name)) {
					return paramElement[i].getAttribute("value");
				}
			}
			return null;
		}

		protected abstract String getBuilderID();

		public void setBuildLocation(IPath location) throws CoreException {
			putValue(BUILD_LOCATION, location.toString());
		}

		public IPath getBuildLocation() {
			String location = getString(BUILD_LOCATION);
			return new Path(location == null ? "" : location);
		}

		public void setStopOnError(boolean enabled) throws CoreException {
			putValue(STOP_ON_ERROR, new Boolean(enabled).toString());
		}

		public boolean isStopOnError() {
			return getBoolean(STOP_ON_ERROR);
		}

		public void setAutoBuildTarget(String target) throws CoreException {
			putValue(BUILD_TARGET_AUTO, target);
		}

		public String getAutoBuildTarget() {
			return getString(BUILD_TARGET_AUTO);
		}

		public void setIncrementalBuildTarget(String target) throws CoreException {
			putValue(BUILD_TARGET_INCREMENTAL, target);
		}

		public String getIncrementalBuildTarget() {
			return getString(BUILD_TARGET_INCREMENTAL);
		}

		public void setFullBuildTarget(String target) throws CoreException {
			putValue(BUILD_TARGET_FULL, target);
		}

		public String getFullBuildTarget() {
			return getString(BUILD_TARGET_FULL);
		}

		public void setPreprocessorSymbols(String[] symbols) {
			// Clear out any existing symbols and add the new stuff
			getSymbolList().clear();
			getSymbolList().addAll(Arrays.asList(symbols));
		}

		public void setIncludePaths(String[] paths) {
			// Clear the existing list and add the paths
			getPathList().clear();
			getPathList().addAll(Arrays.asList(paths));
		}

		/* (non-Javadoc)
		 * @see org.eclipse.cdt.core.build.managed.IScannerInfo#getIncludePaths()
		 */
		public String[] getIncludePaths() {
			return (String[])getPathList().toArray(new String[getPathList().size()]);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.cdt.core.build.managed.IScannerInfo#getIncludePaths()
		 */
		public Map getDefinedSymbols() {
			// Return the defined symbols for the default configuration
			HashMap symbols = new HashMap();
			String[] symbolList = getPreprocessorSymbols();
			for (int i = 0; i < symbolList.length; ++i) {
				String symbol = symbolList[i];
				if (symbol.length() == 0) {
					continue;
				}
				String key = new String();
				String value = new String();
				int index = symbol.indexOf("=");
				if (index != -1) {
					key = symbol.substring(0, index).trim();
					value = symbol.substring(index + 1).trim();
				} else {
					key = symbol.trim();
				}
				symbols.put(key, value);
			}
			return symbols;
		}

		protected List getPathList() {
			if (pathList == null) {
				pathList = new ArrayList();
			}
			return pathList;
		}

		public String[] getPreprocessorSymbols() {
			return (String[])getSymbolList().toArray(new String[getSymbolList().size()]);
		}

		protected List getSymbolList() {
			if (symbolList == null) {
				symbolList = new ArrayList();
			}
			return symbolList;
		}

		public boolean getBoolean(String property) {
			return Boolean.valueOf(getString(property)).booleanValue();
		}

		public abstract void putValue(String name, String value) throws CoreException;
		public abstract String getString(String property);

		public void setAutoBuildEnable(boolean enabled) throws CoreException {
			putValue(BUILD_AUTO_ENABLED, new Boolean(enabled).toString());
		}

		public boolean isAutoBuildEnable() {
			return getBoolean(BUILD_AUTO_ENABLED);
		}

		public void setIncrementalBuildEnable(boolean enabled) throws CoreException {
			putValue(BUILD_INCREMENTAL_ENABLED, new Boolean(enabled).toString());
		}

		public boolean isIncrementalBuildEnabled() {
			return getBoolean(BUILD_INCREMENTAL_ENABLED);
		}

		public void setFullBuildEnable(boolean enabled) throws CoreException {
			putValue(BUILD_FULL_ENABLED, new Boolean(enabled).toString());
		}

		public boolean isFullBuildEnabled() {
			return getBoolean(BUILD_FULL_ENABLED);
		}

		public String getBuildArguments() {
			return getString(BUILD_ARGUMENTS);
		}

		public void setBuildArguments(String args) throws CoreException {
			putValue(BUILD_ARGUMENTS, args);
		}
	}

	private static class Preference extends Store {
		private Preferences prefs;
		private String builderID;
		private boolean useDefaults;

		public Preference(Preferences prefs, String builderID, boolean useDefaults) {
			this.prefs = prefs;
			this.builderID = builderID;
			this.useDefaults = useDefaults;
		}

		public void putValue(String name, String value) {
			if (useDefaults) {
				prefs.setDefault(name, value);
			} else {
				prefs.setValue(name, value);
			}
		}

		public String getString(String property) {
			if (useDefaults) {
				return prefs.getDefaultString(property);
			}
			return prefs.getString(property);
		}

		protected String getBuilderID() {
			return builderID;
		}
	}

	private static class BuildProperty extends Store {
		private IProject project;
		private String builderID;
		private Map args;

		public BuildProperty(IProject project, String builderID) throws CoreException {
			this.project = project;
			this.builderID = builderID;
			ICommand builder;
			builder = MakeProjectNature.getBuildSpec(project, builderID);
			if (builder == null) {
				throw new CoreException(
					new Status(IStatus.ERROR, MakeCorePlugin.getUniqueIdentifier(), -1, "Missing Builder: " + builderID, null));
			}
			args = builder.getArguments();
		}

		public void putValue(String name, String value) throws CoreException {
			ICommand builder = MakeProjectNature.getBuildSpec(project, builderID);
			args.put(name, value);
			builder.setArguments(args);
			project.setDescription(project.getDescription(), null);
		}

		public String getString(String name) {
			String value = (String)args.get(name);
			return value == null ? "" : value;
		}

		public String getBuilderID() {
			return builderID;
		}
	}

	private static class BuildArguments extends Store {
		private Map args;
		private String builderID;

		public BuildArguments(Map args, String builderID) {
			this.args = args;
			this.builderID = builderID;
		}

		public void putValue(String name, String value) {
			args.put(name, value);
		}

		public String getString(String name) {
			return (String)args.get(name);
		}

		public String getBuilderID() {
			return builderID;
		}
	}

	public static IMakeBuilderInfo create(Preferences prefs, String builderID, boolean useDefaults) {
		return new BuildInfoFactory.Preference(prefs, builderID, useDefaults);
	}

	public static IMakeBuilderInfo create(IProject project, String builderID) throws CoreException {
		return new BuildInfoFactory.BuildProperty(project, builderID);
	}

	public static IMakeBuilderInfo create(Map args, String builderID) {
		return new BuildInfoFactory.BuildArguments(args, builderID);
	}
}

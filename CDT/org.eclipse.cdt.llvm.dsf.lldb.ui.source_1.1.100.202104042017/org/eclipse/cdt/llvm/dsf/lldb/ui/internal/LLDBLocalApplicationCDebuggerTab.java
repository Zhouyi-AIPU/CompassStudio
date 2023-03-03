/*******************************************************************************
 * Copyright (c) 2016 Ericsson.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.cdt.llvm.dsf.lldb.ui.internal;

import java.util.Map;

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.debug.ui.ICDebuggerPage;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.internal.ui.launching.ICDTLaunchHelpContextIds;
import org.eclipse.cdt.dsf.gdb.internal.ui.launching.LocalApplicationCDebuggerTab;
import org.eclipse.cdt.llvm.dsf.lldb.core.ILLDBDebugPreferenceConstants;
import org.eclipse.cdt.llvm.dsf.lldb.core.ILLDBLaunchConfigurationConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

/**
 * NPU LLDB-specific debugger tab that allows us to present a different debugger
 * page.
 */
@SuppressWarnings("restriction")
public class LLDBLocalApplicationCDebuggerTab extends LocalApplicationCDebuggerTab {//CUSTOMIZATION
	
	@Override
	public void createControl(Composite parent) {
		super.fContainer = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		fContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
		fContainer.setLayout(new FillLayout());
		fContainer.setExpandHorizontal(true);
		fContainer.setExpandVertical(true);

		super.fContents = new Composite(fContainer, SWT.NONE);
		setControl(fContainer);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
				ICDTLaunchHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_DEBBUGER_TAB);
		int numberOfColumns = fAttachMode ? 2 : 1;
		GridLayout layout = new GridLayout(numberOfColumns, false);
		fContents.setLayout(layout);
		GridData gd = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		fContents.setLayoutData(gd);

		if (fAttachMode) {
			createDebuggerCombo(fContents);
		}

		createDebuggerGroup(fContents, 2);
		fContainer.setContent(fContents);
	}
	
	

	@Override
	protected void initializeCommonControls(ILaunchConfiguration config) {
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy config) {
		if (getDebuggerId() != null) {
			config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_ID, getDebuggerId());
			ICDebuggerPage dynamicTab = getDynamicTab();
			if (dynamicTab == null) {
				config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_SPECIFIC_ATTRS_MAP,
						(Map<String, String>) null);
			} else {
				dynamicTab.performApply(config);
			}
		}

		if (fAttachMode && fRemoteMode) {
			config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_START_MODE,
					IGDBLaunchConfigurationConstants.DEBUGGER_MODE_REMOTE_ATTACH);
		} else if (fAttachMode) {
			config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_START_MODE,
					ICDTLaunchConfigurationConstants.DEBUGGER_MODE_ATTACH);
		} else if (fRemoteMode) {
			config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_START_MODE,
					IGDBLaunchConfigurationConstants.DEBUGGER_MODE_REMOTE);
		} else if (fCoreMode) {
			config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_START_MODE,
					ICDTLaunchConfigurationConstants.DEBUGGER_MODE_CORE);
		} else {
			config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_START_MODE,
					ICDTLaunchConfigurationConstants.DEBUGGER_MODE_RUN);
		}
	}

	private final static String LOCAL_DEBUGGER_ID = "lldb-mi";

	@Override
	protected void initDebuggerTypes(String selection) {
		if (fAttachMode) {
			setInitializeDefault(selection.isEmpty());

			if (selection.isEmpty()) {
				selection = LOCAL_DEBUGGER_ID;
			}

			loadDebuggerCombo(new String[] { LOCAL_DEBUGGER_ID }, selection);
		} else {
			setDebuggerId(LOCAL_DEBUGGER_ID);
			updateComboFromSelection();
		}
	}

	/*
	 * This flag it to make sure that setDefaults gets called, even if the configuration is initially created for another delegate (GDB) then switched to LLDB.
	 */
	private final static String DEFAULTS_SET = "org.eclipse.cdt.llvm.dsf.lldb.ui.internal.LLDBLocalApplicationCDebuggerTab.DEFAULTS_SET"; //$NON-NLS-1$

	@Override
	public void initializeFrom(ILaunchConfiguration config) {
		try {
			if (config.hasAttribute(DEFAULTS_SET) == false) {
				ILaunchConfigurationWorkingCopy wc;
				wc = config.getWorkingCopy();
				setDefaults(wc);
				wc.doSave();
			}
		} catch (CoreException e) {
		}

		super.initializeFrom(config);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		super.setDefaults(config);
		IPreferenceStore corePreferenceStore = LLDBUIPlugin.getDefault().getCorePreferenceStore();
		config.setAttribute(ILLDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
				corePreferenceStore.getString(ILLDBDebugPreferenceConstants.PREF_DEFAULT_LLDB_COMMAND));
		config.setAttribute(DEFAULTS_SET, true);
	}

	@Override
	protected void loadDynamicDebugArea() {
		Composite dynamicTabHolder = getDynamicTabHolder();
		// Dispose of any current child widgets in the tab holder area
		Control[] children = dynamicTabHolder.getChildren();
		for (int i = 0; i < children.length; i++) {
			children[i].dispose();
		}

		String debuggerId = getIdForCurrentDebugger();
		if (debuggerId == null) {
			setDynamicTab(null);
		} else {
			if (debuggerId.equals(LOCAL_DEBUGGER_ID)) {
				setDynamicTab(new LLDBCDebuggerPage());
			} else {
				assert false : "Unknown debugger id"; 
			}
		}
		setDebuggerId(debuggerId);

		ICDebuggerPage debuggerPage = getDynamicTab();
		if (debuggerPage == null) {
			return;
		}
		// Ask the dynamic UI to create its Control
		debuggerPage.setLaunchConfigurationDialog(getLaunchConfigurationDialog());
		debuggerPage.createControl(dynamicTabHolder);
		debuggerPage.getControl().setVisible(true);
		dynamicTabHolder.layout(true);
		contentsChanged();
	}
}

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

import org.eclipse.cdt.debug.ui.ICDebuggerPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @ClassName: LLDBLocalApplicationOpenCLDebuggerTab
 * 
 * @Description:Customized debug Tab for compass opencl
 * 
 * @author Alan Chen
 */
/*
 * NPU LLDB-specific debugger tab that allows us to present a different debugger
 * page.
 */
@SuppressWarnings("restriction")
public class LLDBLocalApplicationOpenCLDebuggerTab extends LLDBLocalApplicationCDebuggerTab {// CUSTOMIZATION
	
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
				// CUSTOMIZATION customized debug page for compass opencl
				setDynamicTab(new LLDBOpenCLDebuggerPage());
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

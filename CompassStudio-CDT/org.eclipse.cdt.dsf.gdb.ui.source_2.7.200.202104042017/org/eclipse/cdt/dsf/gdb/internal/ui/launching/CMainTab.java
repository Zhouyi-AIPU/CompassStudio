/*******************************************************************************
 * Copyright (c) 2015 Ericsson and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Marc Khouzam (Ericsson) - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.dsf.gdb.internal.ui.launching;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.IBinary;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.launch.internal.ui.LaunchMessages;
import org.eclipse.cdt.launch.ui.CMainTab2;
import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @ClassName: CMainTab
 * 
 * @Description:Automatically add xxx.out file for Compass c debug
 * 
 * @author Alan Chen
 */
/*
 * A launch configuration tab that displays and edits project and main type name launch
 * configuration attributes.
 *
 * @deprecated Replaced with org.eclipse.cdt.launch.ui.CMainTab2
 */
@Deprecated
public class CMainTab extends CMainTab2 {
	protected String progLabel = "Compass C Application:";
	public CMainTab() {
		super();
	}

	public CMainTab(int flags) {
		super(flags);
	}

	// CUSTOMIZATION
	// Default execute debug_binary_file in debug folder
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME, EMPTY_STRING);
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_BUILD_CONFIG_ID, EMPTY_STRING);
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_COREFILE_PATH, EMPTY_STRING);

		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_BUILD_CONFIG_AUTO, true);

		ICElement cElement = null;
		cElement = getContext(config, getPlatform(config));
		if (cElement != null) {
			initializeCProject(cElement, config);
			initializeCustomizationProgramName(cElement, config);
		} else {
			config.setMappedResources(null);
		}
	}

	/**
	 * Default the program name attributes on the workspace base on CProject
	 */
	// CUSTOMIZATION
	protected void initializeCustomizationProgramName(ICElement cElement, ILaunchConfigurationWorkingCopy config) {
		boolean renamed = false;

		if (!(cElement instanceof IBinary)) {
			cElement = cElement.getCProject();
		}

		if (cElement instanceof ICProject) {
			IProject project = cElement.getCProject().getProject();
			String name = project.getName();
			ICProjectDescription projDes = CCorePlugin.getDefault().getProjectDescription(project);
			if (projDes != null) {
				String buildConfigName = projDes.getActiveConfiguration().getName();
				name = name + " " + buildConfigName;
			}
			name = getLaunchConfigurationDialog().generateName(name);
			config.rename(name);
			renamed = true;
		}

		IBinary binary = null;
		if (cElement instanceof ICProject) {
			IBinary[] bins = getBinaryFiles((ICProject) cElement);
			// CUSTOMIZATION automatically add xxx.out file
			if (bins != null && bins.length > 0) {
				binary = bins[0];
			}
		} else if (cElement instanceof IBinary) {
			binary = (IBinary) cElement;
		}

		if (binary != null) {
			String path;
			path = binary.getResource().getProjectRelativePath().toOSString();
			config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, path);
			if (!renamed) {
				String name = binary.getElementName();
				int index = name.lastIndexOf('.');
				if (index > 0) {
					name = name.substring(0, index);
				}
				name = getLaunchConfigurationDialog().generateName(name);
				config.rename(name);
				renamed = true;
			}
		}

		if (!renamed) {
			String name = getLaunchConfigurationDialog().generateName(cElement.getCProject().getElementName());
			config.rename(name);
		}
	}

	@Override
	protected void createExeFileGroup(Composite parent, int colSpan) {
		Composite mainComp = new Composite(parent, SWT.NONE);
		GridLayout mainLayout = new GridLayout();
		mainLayout.marginHeight = 0;
		mainLayout.marginWidth = 0;
		mainComp.setLayout(mainLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = colSpan;
		mainComp.setLayoutData(gd);
		fProgLabel = new Label(mainComp, SWT.NONE);
		fProgLabel.setText(progLabel);
		gd = new GridData();
		fProgLabel.setLayoutData(gd);
		fProgText = new Text(mainComp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fProgText.setLayoutData(gd);
		fProgText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent evt) {
				updateLaunchConfigurationDialog();
			}
		});

		Composite buttonComp = new Composite(mainComp, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttonComp.setLayout(layout);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		buttonComp.setLayoutData(gd);
		buttonComp.setFont(parent.getFont());

		createVariablesButton(buttonComp, LaunchMessages.CMainTab_Variables, fProgText);
		fSearchButton = createPushButton(buttonComp, LaunchMessages.CMainTab_Search, null);
		fSearchButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				handleSearchButtonSelected();
				updateLaunchConfigurationDialog();
			}
		});

		Button browseForBinaryButton;
		browseForBinaryButton = createPushButton(buttonComp, LaunchMessages.Launch_common_Browse_2, null);
		browseForBinaryButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				String text = handleBrowseButtonSelected(LaunchMessages.CMainTab2_Application_Selection);
				if (text != null) {
					fProgText.setText(text);
				}
				updateLaunchConfigurationDialog();
			}
		});
	}

	@Override
	public String getId() {
		// Return the old id as to be backwards compatible
		return "org.eclipse.cdt.dsf.gdb.launch.mainTab";
	}
}

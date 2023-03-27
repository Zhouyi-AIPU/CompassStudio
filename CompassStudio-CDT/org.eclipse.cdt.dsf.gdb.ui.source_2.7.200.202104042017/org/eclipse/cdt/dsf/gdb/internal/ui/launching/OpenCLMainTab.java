package org.eclipse.cdt.dsf.gdb.internal.ui.launching;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.IBinary;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.launch.internal.ui.LaunchMessages;
import org.eclipse.cdt.ui.CElementLabelProvider;
import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.TwoPaneElementSelector;

/**
 * @ClassName: CMainTab
 * 
 * @Description:Automatically add xxx.o file for Compass opencl debug
 * 
 * @author Alan Chen
 */
public class OpenCLMainTab extends CMainTab {
	@Override
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

		if (cElement instanceof ICProject) {
			// CUSTOMIZATION automatically add xxx.o file
			String projectPath = cElement.getResource().getLocation().toOSString();
			String objectFilePath = projectPath + File.separator + "Debug" + File.separator + "src";
			if (!new File(objectFilePath).exists()) {
				objectFilePath = projectPath + File.separator + "Release" + File.separator + "src";
			}
			String fileName = cElement.getElementName() + ".o";
			String filePath = objectFilePath + File.separator + fileName;
			boolean exits = new File(filePath).exists();
			if (exits) {
				String[] sp = filePath.split(projectPath + File.separator);
				config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, sp[1]);
				if (!renamed) {
					String name = fileName;
					int index = name.lastIndexOf('.');
					if (index > 0) {
						name = name.substring(0, index);
					}
					name = getLaunchConfigurationDialog().generateName(name);
					config.rename(name);
					renamed = true;
				}
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
		fProgLabel.setText("Compass OpenCL Application:");
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
	protected void handleSearchButtonSelected() {
		if (getCProject() == null) {
			MessageDialog.openInformation(getShell(), LaunchMessages.CMainTab_Project_required,
					LaunchMessages.CMainTab_Enter_project_before_searching_for_program);

			return;
		}

		ILabelProvider programLabelProvider = new CElementLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof String) {
					StringBuilder name = new StringBuilder();
					name.append(element);
					return name.toString();
				}
				return super.getText(element);
			}

			@Override
			public Image getImage(Object element) {
				if (!(element instanceof ICElement)) {
					return super.getImage(element);
				}
				ICElement celement = (ICElement) element;

				int elementType = celement.getElementType();
				if (elementType == ICElement.C_BINARY) {
					IBinary belement = (IBinary) celement;
					if (belement.isExecutable()) {
						return DebugUITools.getImage(IDebugUIConstants.IMG_ACT_RUN);
					}
				}

				return super.getImage(element);
			}
		};

		ILabelProvider qualifierLabelProvider = new CElementLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof String) {
					StringBuilder name = new StringBuilder();
					name.append("Source");
					name.append(" - ");
					name.append(File.separator + getCProject().getElementName() + File.separator + element);
					return name.toString();
				}
				return super.getText(element);
			}
		};

		TwoPaneElementSelector dialog = new TwoPaneElementSelector(getShell(), programLabelProvider,
				qualifierLabelProvider);
		dialog.setElements(getX2SourceFiles(getCProject()));
		dialog.setMessage(LaunchMessages.CMainTab_Choose_program_to_run);
		dialog.setTitle(LaunchMessages.CMainTab_Program_Selection);
		dialog.setUpperListLabel(LaunchMessages.Launch_common_BinariesColon);
		dialog.setLowerListLabel(LaunchMessages.Launch_common_QualifierColon);
		dialog.setMultipleSelection(false);
		// dialog.set
		if (dialog.open() == Window.OK) {
			String sourceName = (String) dialog.getFirstResult();
			fProgText.setText(sourceName);
		}
	}

	private String[] getX2SourceFiles(final ICProject cproject) {
			if (cproject == null || !cproject.exists()) {
				return null;
			}
			final String[][] ret = new String[1][];
			String elementName = cproject.getElementName();
			String sourcePath = cproject.getLocationURI() + File.separator + "Debug" + File.separator + "src"
					+ File.separator
					+ elementName + ".o";
			sourcePath = sourcePath.replace("file:", "");
			File sourceFile = new File(sourcePath);
			if (sourceFile.exists()) {
				ArrayList<String> list = new ArrayList<String>();
				list.add("Debug" + File.separator + "src" + File.separator + elementName + ".o");
				String[] b = new String[list.size()];
				ret[0] = list.toArray(b);
			}
			return ret[0];
		}
}

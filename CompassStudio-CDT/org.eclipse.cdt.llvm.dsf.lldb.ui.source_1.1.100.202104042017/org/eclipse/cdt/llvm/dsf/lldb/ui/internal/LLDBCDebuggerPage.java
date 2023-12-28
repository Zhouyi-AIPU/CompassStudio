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

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.debug.ui.AbstractCDebuggerPage;
import org.eclipse.cdt.llvm.dsf.lldb.core.ILLDBDebugPreferenceConstants;
import org.eclipse.cdt.llvm.dsf.lldb.core.ILLDBLaunchConfigurationConstants;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cn.com.armchina.ide.tools.FileUtils;
import cn.com.armchina.ide.tools.LaunchUtils.AIPULaunchPathUtils;
import cn.com.armchina.ide.tools.MessageDialog.MyMessageBox;
import cn.com.armchina.ide.tools.consts.GBuildConsts;
import cn.com.armchina.toolchain.core.NPUDBCoreConsts;
import cn.com.armchina.toolchain.core.NPUDBLaunchConfigurationConstants;

/**
 * @ClassName: LLDBCDebuggerPage
 * 
 * @Description:Customized debugger page for compass c
 * 
 * @author Alan Chen
 */
/*
 * A LLDB-specific debugger page that only shows the options currently supported
 * by LLDB and its integration with CDT.
 */
public class LLDBCDebuggerPage extends AbstractCDebuggerPage {

	// CUSTOMIZATION
//	protected Text fLLDBCommandText;

	public Text runirText;
	public Text runweightText;
	public List inputText;
	public Combo runTarget;
	public Combo hardwareRunTarget;
	public Text runOtherCommand;
	public Text gbirText;
	public Text gbweightText;
	public Text gbOtherCommand;
	public CCombo comboPlatform;
	// hardware tab
	public String[] platform_type = { "Simulator", "Hardware" };
	public String connect_addr_default_value = "unspecified-ip-address:unspecified-port-number";
	public StackLayout stackLayout;
	public Composite cfgComposite;
	public Composite simulatorCom;
	public Composite hardwareCom;
	// connectionTab
	public Button btn1;
	public Button btn2;
	public Text addrrsText;
	public Text runcfgText;
	// private Text runnonetcfgText;
	public List fileListCom;
	public Button addBtn;
	public Button deleteBtn;
	public Button copyBtn;
	public Button cfgBtn;
	// private Button cfgnonetBtn;
	// private Label runcfgnonetLabel;
	public Label addrressLabel;
	public Label runcfgLabel;
	public IProject pro;

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(new GridLayout(4, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		// CUSTOMIZATION
		createSingleTecControl(composite);
		createLabel(composite, "Debug Mode:     ");
		comboPlatform = createCustomizedDebugModeCombo(composite);
		GridData layoutData = new GridData();
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.grabExcessHorizontalSpace = false;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = SWT.CENTER;
		layoutData.widthHint = 250;
		layoutData.horizontalSpan = 3;
		comboPlatform.setLayoutData(layoutData);
		comboPlatform.select(0);

		// CUSTOMIZATION remove lldb-mi debugger name in Debug Tab
//		Label lbl = new Label(composite, SWT.LEFT);
//		lbl.setFont(parent.getFont());
//		lbl.setText(Messages.LLDBCDebuggerPage_debugger_command);
//		fLLDBCommandText = new Text(composite, SWT.SINGLE | SWT.BORDER);
//		fLLDBCommandText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,2,1));
//		fLLDBCommandText.addModifyListener(new ModifyListener() {
//			@Override
//			public void modifyText(ModifyEvent evt) {
//				updateLaunchConfigurationDialog();
//			}
//		});
//
//		Button button = createPushButton(composite, Messages.LLDBCDebuggerPage_browse, null);
//		button.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent evt) {
//				handleButtonSelected();
//				updateLaunchConfigurationDialog();
//			}
//
//			private void handleButtonSelected() {
//				FileDialog dialog = new FileDialog(getShell(), SWT.NONE);
//				dialog.setText(Messages.LLDBCDebuggerPage_browse_dialog_title);
//				String lldbCommand = fLLDBCommandText.getText().trim();
//				int lastSeparatorIndex = lldbCommand.lastIndexOf(File.separator);
//				if (lastSeparatorIndex != -1) {
//					String cmd = lldbCommand.substring(0, lastSeparatorIndex);
//					// remove double quotes, since they interfere with
//					// "setFilterPath()" below
//					cmd = cmd.replaceAll("\\\"", ""); //$NON-NLS-1$//$NON-NLS-2$
//					dialog.setFilterPath(cmd);
//				}
//				String res = dialog.open();
//				if (res == null) {
//					return;
//				}
//				// path contains space(s)?
//				if (res.contains(" ")) { //$NON-NLS-1$
//					// surround it in double quotes
//					res = '"' + res + '"';
//				}
//				fLLDBCommandText.setText(res);
//			}
//		});

		// CUSTOMIZATION
		cfgComposite = new Composite(composite, SWT.None);
		cfgComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));
		stackLayout = new StackLayout();
		cfgComposite.setLayout(stackLayout);

		simulatorCom = createSimulatorCfgCompoiste(cfgComposite);
		hardwareCom = createHardwareCfgComposite(cfgComposite);
		stackLayout.topControl = simulatorCom;

		comboPlatform.addSelectionListener(widgetSelectedAdapter(e -> selectLayout()));
		comboPlatform.addListener(SWT.Modify, new ChangeListener());

		setControl(parent);
	}

	public void selectLayout() {
		if (comboPlatform.getText().equals(platform_type[0])) {
			stackLayout.topControl = simulatorCom;
			cfgComposite.layout();

		} else if (comboPlatform.getText().equals(platform_type[1])) {
			stackLayout.topControl = hardwareCom;
			cfgComposite.layout();
		}
	}

	public void createSingleTecControl(Composite composite) {
	}

	// debug operate plugin only can on simulator
	public CCombo createCustomizedDebugModeCombo(Composite composite) {
		return createReadOnlyCCombo(composite, platform_type);
	}

	public Combo createCustomizedTargerCombo(Group group) {
		return createReadOnlyCombo(group, GBuildConsts.simulator_targets_no_x2);
	}

	// CUSTOMIZATION
	// aipurun configuration
	protected Composite createSimulatorCfgCompoiste(Composite composite) {

		Composite com = new Composite(composite, SWT.NONE);
		com.setLayout(new FillLayout());
		Group group = new Group(com, SWT.FILL);
		group.setText("Op.run configuration");
		GridLayout gd = new GridLayout();
		gd.numColumns = 4;
		gd.marginLeft = 5;
		gd.marginRight = 5;
		group.setLayout(gd);

		createLabel(group, "Target:");
		runTarget = createCustomizedTargerCombo(group);
		runTarget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		runTarget.select(0);

		createLabel(group, "IR Path:");
		runirText = createText(group, SWT.BORDER);
		runirText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		Button buttonIrPath = createButton(group, Messages.LLDBCDebuggerPage_browse);
		GridData gd_buttonIrPath = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_buttonIrPath.widthHint = 100;
		buttonIrPath.setLayoutData(gd_buttonIrPath);
		buttonIrPath.addSelectionListener(new BtnSelectionListener(runirText));

		createLabel(group, "Weight Path:");
		runweightText = createText(group, SWT.BORDER);
		runweightText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));

		Button buttonWeightPath = createButton(group, Messages.LLDBCDebuggerPage_browse);
		GridData gd_buttonWeightPath = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
		gd_buttonWeightPath.widthHint = 100;
		buttonWeightPath.setLayoutData(gd_buttonWeightPath);
		buttonWeightPath.addSelectionListener(new BtnSelectionListener(runweightText));

		createSimInputControl(group);

		Label otherCommandsLabel = createLabel(group, "Other Options:");
		otherCommandsLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		runOtherCommand = createText(group, SWT.BORDER);
		runOtherCommand.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		new Label(group, SWT.NONE).setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 4, 3));

		// update button
		runirText.addListener(SWT.Modify, new ChangeListener());
		runweightText.addListener(SWT.Modify, new ChangeListener());
		runTarget.addListener(SWT.Modify, new ChangeListener());
		runOtherCommand.addListener(SWT.Modify, new ChangeListener());

		return com;
	}

	public Combo createHardwareTargetComboControl(Group group) {
		return createReadOnlyCombo(group, GBuildConsts.simulator_targets_no_x2);
	}

	public void createSimInputControl(Group group) {
		Label inputs = createLabel(group, "Inputs:");
		inputs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 3));
		inputText = new List(group, SWT.BORDER | SWT.V_SCROLL);
		GridData gd_buttoninputText = new GridData(SWT.FILL, SWT.FILL, false, false, 2, 3);
		gd_buttoninputText.heightHint = 100;
		inputText.setLayoutData(gd_buttoninputText);

		Composite btns = new Composite(group, SWT.NONE);
		GridLayout btnLayout = new GridLayout(1, false);
		btnLayout.marginHeight = 0;
		btnLayout.marginWidth = 0;
		btns.setLayout(btnLayout);
		btns.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, false, 1, 3));

		Button buttonInputsPath = createButton(btns, "Add File");
		buttonInputsPath.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, false, 1, 1));
		((GridData) buttonInputsPath.getLayoutData()).widthHint = 100;
		buttonInputsPath.addSelectionListener(new BtnListSelectionListener(inputText));
		Button buttonPathDel = createButton(btns, "Delete File");
		buttonPathDel.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, false, 1, 1));
		((GridData) buttonPathDel.getLayoutData()).widthHint = 100;
		buttonPathDel.addSelectionListener(new BtnListDeleteListener());
		Button buttonPathUp = createButton(btns, "Up...");
		buttonPathUp.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, false, 1, 1));
		((GridData) buttonPathUp.getLayoutData()).widthHint = 100;
		buttonPathUp.addSelectionListener(new BtnListLocationUpListener());

//		new Label(group, SWT.NONE);

		inputText.addListener(SWT.Modify, new ChangeListener());
	}

	// aipugb
	protected Composite createHardwareCfgComposite(Composite composite) {
		Composite com = new Composite(composite, SWT.None);
		com.setLayout(new GridLayout(1, false));
		Group group1 = new Group(com, SWT.None);

		group1.setText("Op.run configuration");
		group1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		GridLayout gd = new GridLayout();
		gd.numColumns = 4;
		gd.marginLeft = 5;
		gd.marginRight = 5;
		group1.setLayout(gd);

		createLabel(group1, "Target:");
		hardwareRunTarget = createHardwareTargetComboControl(group1);
		hardwareRunTarget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		hardwareRunTarget.select(0);

		// group1: aipugb cfg
		createLabel(group1, "IR Path:");
		gbirText = createText(group1, SWT.BORDER);
		gbirText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		Button buttonIrPath = createButton(group1, Messages.LLDBCDebuggerPage_browse);
		GridData gd_buttonIrPath = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_buttonIrPath.widthHint = 100;
		buttonIrPath.setLayoutData(gd_buttonIrPath);
		buttonIrPath.addSelectionListener(new BtnSelectionListener(gbirText));

		createLabel(group1, "Weight Path:");
		gbweightText = createText(group1, SWT.BORDER);
		gbweightText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		Button buttonWeightPath = createButton(group1, Messages.LLDBCDebuggerPage_browse);
		GridData gd_buttonWeightPath = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_buttonWeightPath.widthHint = 100;
		buttonWeightPath.setLayoutData(gd_buttonWeightPath);
		buttonWeightPath.addSelectionListener(new BtnSelectionListener(gbweightText));

		Label otherCommandsLabel = createLabel(group1, "Other Options:");
		otherCommandsLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		gbOtherCommand = createText(group1, SWT.BORDER);
		gbOtherCommand.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 3, 1));

		// group2: hardware connection cfg
		Group group2 = new Group(com, SWT.None);
		group2.setText("Remote Target");
		group2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		createConnectionComposite(group2);

		gbirText.addListener(SWT.Modify, new ChangeListener());
		gbweightText.addListener(SWT.Modify, new ChangeListener());
		gbOtherCommand.addListener(SWT.Modify, new ChangeListener());
		hardwareRunTarget.addListener(SWT.Modify, new ChangeListener());

		return com;

	}

	protected void createConnectionComposite(Composite composite) {
		GridLayout gd_group = new GridLayout();
		gd_group.numColumns = 4;
		composite.setLayout(gd_group);

		btn1 = createRadioButton(composite, "Connect with network");
		btn1.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 4, 1));
		btn1.setToolTipText(Messages.NPUDebugOnHardwareWithNetwork_tip);

		createLabel(composite, "    ");
		addrressLabel = createLabel(composite, "Aipugdbserver Connection:");
		addrressLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		addrrsText = createText(composite, SWT.BORDER);
		addrrsText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		new Label(composite, SWT.NONE);
		runcfgLabel = createLabel(composite, "Run Configuration File:");
		runcfgText = createText(composite, SWT.BORDER);
		runcfgText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		cfgBtn = createButton(composite, Messages.LLDBCDebuggerPage_browse);
		cfgBtn.addSelectionListener(new BtnSelectionListener(runcfgText));
		GridData gd_buttoncfgPath = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_buttoncfgPath.widthHint = 100;
		cfgBtn.setLayoutData(gd_buttoncfgPath);

		btn2 = createRadioButton(composite, "Connect with no-network");
		btn2.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 4, 1));
		btn2.setToolTipText(Messages.NPUDebugOnHardwareWithNoNetwork_tip);

		Label hideLabel3 = createLabel(composite, "    ");
		hideLabel3.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 3));
		fileListCom = new List(composite, SWT.BORDER | SWT.V_SCROLL);
		fileListCom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 4));
		addBtn = createButton(composite, "Add File");
		GridData addbtnGd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		addbtnGd.widthHint = 100;
		addBtn.setLayoutData(addbtnGd);

		deleteBtn = createButton(composite, "Delete File");
		deleteBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		copyBtn = createButton(composite, "Copy To");
		copyBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btn1.setSelection(true);
		setConnectionTabControlsStatus(true);

		addBtn.addSelectionListener(widgetSelectedAdapter(e -> addFileAction()));
		deleteBtn.addSelectionListener(widgetSelectedAdapter(e -> deleteFileAction()));
		copyBtn.addSelectionListener(widgetSelectedAdapter(e -> copyFileAction(composite)));
		btn1.addSelectionListener(widgetSelectedAdapter(e -> connectStatusAction()));

		btn1.addListener(SWT.Selection, new ChangeListener());
		btn2.addListener(SWT.Selection, new ChangeListener());
		addrrsText.addListener(SWT.Modify, new ChangeListener());
		runcfgText.addListener(SWT.Modify, new ChangeListener());
		// runnonetcfgText.addListener(SWT.Modify, new ChangeListener());
		fileListCom.addListener(SWT.Modify, new ChangeListener());

	}

	private void connectStatusAction() {
		if (btn1.getSelection()) {
			setConnectionTabControlsStatus(true);
		} else {
			setConnectionTabControlsStatus(false);
		}
	}

	private void copyFileAction(Composite composite) {
		if (fileListCom.getItems().length < 1) {
			MyMessageBox.showMessage("Please add a file first");
			return;
		}
		DirectoryDialog f = new DirectoryDialog(composite.getShell(), SWT.OPEN);
		f.setText("Select direction");

		String dir = f.open();
		boolean transfered = true;
		if (dir != null) {
			for (String filePath : fileListCom.getItems()) {
				File fileIn = new File(filePath);
				if (FileUtils.copyFile(fileIn.getName(), fileIn.getParent(), dir)) {
					System.out.println(fileIn.getName() + "copied succ");
				} else {
					MyMessageBox.alertMessage(fileIn.getName() + "copied failed");
					transfered = false;
					break;
				}

			}
			if (transfered)
				MyMessageBox.showMessage("The files have been copied successfully");

		}
	}

	private void deleteFileAction() {
		int[] selectfiles = fileListCom.getSelectionIndices();
		if (selectfiles.length <= 0) {
			MyMessageBox.showMessage("Please select a file first");
			return;
		}
		fileListCom.remove(selectfiles);
		updateLaunchConfigurationDialog();
	}

	private void addFileAction() {
		FileDialog f = new FileDialog(new Shell(), SWT.OPEN);
		f.setText("Select the file ");
		String fn = f.open();
		if (fn != null && !Arrays.asList(fileListCom.getItems()).contains(fn)) {
			fileListCom.add(fn);
		}
		updateLaunchConfigurationDialog();
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// I'm actually not sure this is needed but it seems this will be called
		// if this delegate is used when first initializing defaults (i.e. GDB
		// is not the delegate for this configuration first)
		IPreferenceStore corePreferenceStore = LLDBUIPlugin.getDefault().getCorePreferenceStore();
		configuration.setAttribute(ILLDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
				corePreferenceStore.getString(ILLDBDebugPreferenceConstants.PREF_DEFAULT_LLDB_COMMAND));
	}// CUSTOMIZATION

	public void initialCustomizationFrom(ILaunchConfiguration configuration) throws CoreException {
		// inputs
		java.util.List<String> inputpath = AIPULaunchPathUtils.getInputBinPath(pro);
		String hardware_debug_cfg = AIPULaunchPathUtils.getDebugRun_cfg_FilePath(pro, 1);
		java.util.List<String> inputfiles = new ArrayList<String>();
		inputfiles = configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_INPUT_PATH, inputpath);
		inputText.removeAll();
		for (String s : inputfiles) {
			inputText.add(s);
		}

		runcfgText.setText(configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_RUN_CFG,
				hardware_debug_cfg));
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		// CUSTOMIZATION remove lldb-mi debugger name in Debug Tab
//		IPreferenceStore preferenceStore = LLDBUIPlugin.getDefault().getCorePreferenceStore();
//		String lldbCommand = getStringAttr(configuration, ILLDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
//				preferenceStore.getString(ILLDBDebugPreferenceConstants.PREF_DEFAULT_LLDB_COMMAND));
//		fLLDBCommandText.setText(lldbCommand);

		// CUSTOMIZATION
		try {
			// get default ir path
			String projectName = configuration.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
			if (projectName.equals(""))
				return;
			pro = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			if (pro.exists()) {
				String irPath = "";
				String weightPath = "";
				String irFolder = AIPULaunchPathUtils.getIRFolderPath(pro); // fix : if ir folder is null, ir and weight path is "";
				if (!irFolder.equals("")) {
					File file = new File(irFolder);
					File[] filelists = file.listFiles();
					for (int i = 0; i < filelists.length; i++) {
						File tmp = filelists[i];
						if (tmp.getName().endsWith(".txt")) {
							irPath = tmp.getAbsolutePath();
						} else if (tmp.getName().endsWith(".bin")) {
							weightPath = tmp.getAbsolutePath();
						}
					}
				}

				initialCustomizationFrom(configuration);

				comboPlatform.select(configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_DEBUG__MODE, 0));
				runirText.setText(
						configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_IR_PATH, irPath));
				runweightText.setText(configuration
						.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_WEIGHT_PATH, weightPath));

				runTarget
						.select(configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_TARGET, 0));
				runOtherCommand.setText(
						configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_OTHERCOMMAND, ""));

				hardwareRunTarget
						.select(configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_TARGET, 0));

				// hardrware
				gbirText.setText(
						configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_IR_PATH, irPath));
				gbweightText.setText(configuration
						.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_WEIGHT_PATH, weightPath));
				gbOtherCommand.setText(
						configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_OTHER_COMMAND, ""));

				addrrsText.setText(configuration.getAttribute(
						NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_CONNECT_ADDR, connect_addr_default_value));

				java.util.List<String> hardwareTranslatefiles = addTranslateFile(pro);
				java.util.List<String> allFiles = new ArrayList<String>();
				allFiles = configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_TRANSLATE_FILES,
						hardwareTranslatefiles);
				fileListCom.removeAll();
				for (String s : allFiles) {
					fileListCom.add(s);
				}
				int connectMode = configuration
						.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_CONNECT_MODE, 0);
				if (connectMode == 0) {
					btn1.setSelection(true);
					btn2.setSelection(false);
					setConnectionTabControlsStatus(true);
				} else if (connectMode == 1) {

					btn1.setSelection(false);
					btn2.setSelection(true);
					setConnectionTabControlsStatus(false);
				}

				if (comboPlatform.getSelectionIndex() == 0) {
					stackLayout.topControl = simulatorCom;
					cfgComposite.layout();
				} else {
					stackLayout.topControl = hardwareCom;
					cfgComposite.layout();
				}

			}

		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public static String getStringAttr(ILaunchConfiguration config, String attributeName, String defaultValue) {
		try {
			return config.getAttribute(attributeName, defaultValue);
		} catch (CoreException e) {
			return defaultValue;
		}
	}

	public void performCustomizationApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(ILLDBLaunchConfigurationConstants.ATTR_DEBUG_NAME, NPUDBCoreConsts.AIPUDBGMI);
		String[] items = inputText.getItems();
		java.util.List<String> inputfiles = Arrays.asList(items);
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_INPUT_PATH, inputfiles);
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// CUSTOMIZATION set debug name default to "aipudbgmi"
		// configuration.setAttribute(ILLDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
		// "aipuodbmi".trim());
		// CUSTOMIZATION
		performCustomizationApply(configuration);
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_DEBUG__MODE,
				comboPlatform.getSelectionIndex());
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_IR_PATH, runirText.getText());
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_WEIGHT_PATH,
				runweightText.getText());

		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_TARGET,
				runTarget.getSelectionIndex());
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_OTHERCOMMAND,
				runOtherCommand.getText());
		// hardware
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_TARGET,
				hardwareRunTarget.getSelectionIndex());

		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_IR_PATH, gbirText.getText());
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_WEIGHT_PATH, gbweightText.getText());
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_OTHER_COMMAND,
				gbOtherCommand.getText());
		if (btn1.getSelection())
			configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_CONNECT_MODE, 0);
		else
			configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_CONNECT_MODE, 1);
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_CONNECT_ADDR, addrrsText.getText());
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_RUN_CFG, runcfgText.getText());
		// configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_RUN_NONET_CFG,
		// runnonetcfgText.getText());
		// translateFiles
//		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_TRANSLATE_FILES,
//				saveTranslateFile(pro, fileListCom.getItems()));
		String[] items = fileListCom.getItems();
		java.util.List<String> files = Arrays.asList(items);
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_TRANSLATE_FILES, files);
	}

	@Override
	public String getName() {
		return Messages.LLDBCDebuggerPage_tab_name;
	}

	public void setConnectionTabControlsStatus(boolean isNetwork) {
		if (isNetwork) {
			fileListCom.setEnabled(false);
			addBtn.setEnabled(false);
			deleteBtn.setEnabled(false);
			copyBtn.setEnabled(false);
			// runcfgnonetLabel.setEnabled(false);
			// runnonetcfgText.setEnabled(false);
			// cfgnonetBtn.setEnabled(false);
			addrrsText.setEnabled(true);
			runcfgText.setEnabled(true);
			cfgBtn.setEnabled(true);
			addrressLabel.setEnabled(true);
			runcfgLabel.setEnabled(true);
		} else {
			fileListCom.setEnabled(true);
			addBtn.setEnabled(true);
			deleteBtn.setEnabled(true);
			copyBtn.setEnabled(true);
			// runcfgnonetLabel.setEnabled(true);
			// runnonetcfgText.setEnabled(true);
			// cfgnonetBtn.setEnabled(true);
			addrrsText.setEnabled(false);
			runcfgText.setEnabled(false);
			cfgBtn.setEnabled(false);
			addrressLabel.setEnabled(false);
			runcfgLabel.setEnabled(false);
		}

	}

	public java.util.List<String> addTranslateFile(IProject pro) {
		java.util.List<String> allfiles = new ArrayList<String>();
		java.util.List<String> inputPath = AIPULaunchPathUtils.getInputBinPath(pro);
		String aipuBinPath = AIPULaunchPathUtils.getHardwareBinPath(pro);
		String runcfgPath = AIPULaunchPathUtils.getDebugRun_cfg_FilePath(pro, 1);
		allfiles.addAll(inputPath);
		if (FileUtils.exist(aipuBinPath))
			allfiles.add(aipuBinPath);
		allfiles.add(runcfgPath);

		return allfiles;
	}
	
	class BtnSelectionListener extends SelectionAdapter {
		private Text text;

		BtnSelectionListener(Text text) {
			this.text = text;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			FileDialog f = new FileDialog(new Shell(), SWT.OPEN);
			f.setText("Select the file");
			String fn = f.open();
			if (fn != null) {
				text.setText(fn);
			}
		}
	}

	class BtnListLocationUpListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			int selectedIndex = inputText.getSelectionIndex();
			if (selectedIndex > 0) {
				String selectedPath = inputText.getItem(selectedIndex);
				String prePath = inputText.getItem(selectedIndex - 1);
				inputText.setItem(selectedIndex - 1, selectedPath);
				inputText.setItem(selectedIndex, prePath);
				inputText.select(selectedIndex - 1);
			}
			updateLaunchConfigurationDialog();
		}
	}

	class BtnListDeleteListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			inputText.remove(inputText.getSelectionIndices());
			updateLaunchConfigurationDialog();
		}
	}

	class BtnListSelectionListener extends SelectionAdapter {
		private List text;

		BtnListSelectionListener(List text) {
			this.text = text;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			FileDialog f = new FileDialog(new Shell(), SWT.OPEN);
			f.setText("Select the file");
			String fn = f.open();
			if (fn != null && !Arrays.asList(text.getItems()).contains(fn)) {
					text.add(fn);
			}
			updateLaunchConfigurationDialog();
		}
	}

	class ChangeListener implements Listener {

		@Override
		public void handleEvent(Event event) {
			updateLaunchConfigurationDialog();
		}

	}
}

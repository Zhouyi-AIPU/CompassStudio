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

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
import cn.com.armchina.toolchain.core.NPUDBLaunchConfigurationConstants;

/**
 * A LLDB-specific debugger page that only shows the options currently supported
 * by LLDB and its integration with CDT.
 */
public class LLDBCDebuggerPage extends AbstractCDebuggerPage {

	// CUSTOMIZATION
//	protected Text fLLDBCommandText;


	private Text runirText;
	private Text runweightText;
	private List inputText;
	private Combo runTarget;
	private Text runOtherCommand;
	private Text gbirText;
	private Text gbweightText;
	private Text gbOtherCommand;
	private CCombo comboPlatform;
	// hardware tab
	private String[] platform_type = { "Simulator", "Hardware" };
	private String connect_addr_default_value = "unspecified-ip-address:unspecified-port-number";
	private StackLayout stackLayout;
	private Composite cfgComposite;
	private Composite simulatorCom;
	private Composite hardwareCom;
	// connectionTab
	private Button btn1;
	private Button btn2;
	private Text addrrsText;
	private Text runcfgText;
	// private Text runnonetcfgText;
	private List fileListCom;
	private Button addBtn;
	private Button deleteBtn;
	private Button copyBtn;
	private Button cfgBtn;
	// private Button cfgnonetBtn;
	// private Label runcfgnonetLabel;
	private Label addrressLabel;
	private Label runcfgLabel;

	private IProject pro;



	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());

		composite.setLayout(new GridLayout(4, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		//CUSTOMIZATION
		Label platform = new Label(composite, SWT.LEFT);
		platform.setText("Debug Mode:");
		comboPlatform = new CCombo(composite, SWT.READ_ONLY | SWT.BORDER);
		GridData layoutData = new GridData();
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.grabExcessHorizontalSpace = false;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = SWT.CENTER;
	    layoutData.widthHint=250;
		comboPlatform.setLayoutData(layoutData);
		comboPlatform.setItems(platform_type);
		comboPlatform.select(0);
		Label hide = new Label(composite, SWT.LEFT);
		hide.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1));

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
		

		//CUSTOMIZATION
		cfgComposite = new Composite(composite, SWT.None);
		cfgComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));
		stackLayout = new StackLayout();
		cfgComposite.setLayout(stackLayout);

		simulatorCom = createSimulatorCfgCompoiste(cfgComposite);
		hardwareCom = createHardwareCfgComposite(cfgComposite);
		stackLayout.topControl = simulatorCom;

		comboPlatform.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (comboPlatform.getText().equals(platform_type[0])) {
					stackLayout.topControl = simulatorCom;
					cfgComposite.layout();


				} else if (comboPlatform.getText().equals(platform_type[1])) {
					stackLayout.topControl = hardwareCom;
					cfgComposite.layout();


				}
			}

		});

		comboPlatform.addListener(SWT.Modify, new ChangeListener());

		setControl(parent);
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

		Label targetLabel = new Label(group, SWT.NONE);
		targetLabel.setText("Target:");
		runTarget = new Combo(group, SWT.READ_ONLY);
		runTarget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		runTarget.setItems(GBuildConsts.simulator_targets_no_x2);
		runTarget.select(0);

		Label irlabel = new Label(group, SWT.NONE);
		irlabel.setText("IR Path:");
		runirText = new Text(group, SWT.BORDER);
		runirText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		Button buttonIrPath = new Button(group, SWT.NONE);
		GridData gd_buttonIrPath = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_buttonIrPath.widthHint = 100;
		buttonIrPath.setLayoutData(gd_buttonIrPath);
		buttonIrPath.setText(Messages.LLDBCDebuggerPage_browse);
		buttonIrPath.addSelectionListener(new BtnSelectionListener(runirText));

		Label weightlabel = new Label(group, SWT.NONE);
		weightlabel.setText("Weight Path:");
		runweightText = new Text(group, SWT.BORDER);
		runweightText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));

		Button buttonWeightPath = new Button(group, SWT.NONE);
		GridData gd_buttonWeightPath = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
		gd_buttonWeightPath.widthHint = 100;
		buttonWeightPath.setLayoutData(gd_buttonWeightPath);
		buttonWeightPath.setText(Messages.LLDBCDebuggerPage_browse);
		buttonWeightPath.addSelectionListener(new BtnSelectionListener(runweightText));

		Label inputsPath = new Label(group, SWT.NONE);
		inputsPath.setText("Inputs:");
		inputText = new List(group, SWT.BORDER | SWT.V_SCROLL);
		GridData gd_buttoninputText = new GridData(SWT.FILL, SWT.FILL, false, false, 2, 2);
		gd_buttoninputText.heightHint = 100;
		inputText.setLayoutData(gd_buttoninputText);

		Button buttonInputsPath = new Button(group, SWT.NONE);
		GridData gd_buttonInputsPath = new GridData(SWT.CENTER, SWT.BOTTOM, false, false, 1, 2);
		gd_buttonInputsPath.widthHint = 100;
		buttonInputsPath.setLayoutData(gd_buttonInputsPath);
		buttonInputsPath.setText(Messages.LLDBCDebuggerPage_browse);
		buttonInputsPath.addSelectionListener(new BtnListSelectionListener(inputText));


		Label placeholder = new Label(group, SWT.NONE);
		Label otherCommandsLabel = new Label(group, SWT.NONE);
		otherCommandsLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		otherCommandsLabel.setText("Other Options:");
		runOtherCommand = new Text(group, SWT.BORDER);
		runOtherCommand.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		Label placeholder1 = new Label(group, SWT.NONE);
		placeholder1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 4, 3));

		// update button
		runirText.addListener(SWT.Modify, new ChangeListener());
		runweightText.addListener(SWT.Modify, new ChangeListener());
		inputText.addListener(SWT.Modify, new ChangeListener());
		runTarget.addListener(SWT.Modify, new ChangeListener());
		runOtherCommand.addListener(SWT.Modify, new ChangeListener());



		return com;


	}

	// aipugb
	protected Composite createHardwareCfgComposite(Composite composite)
	{
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

		// group1: aipugb cfg
		Label irlabel = new Label(group1, SWT.NONE);
		irlabel.setText("IR Path:");
		gbirText = new Text(group1, SWT.BORDER);
		gbirText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		Button buttonIrPath = new Button(group1, SWT.NONE);
		GridData gd_buttonIrPath = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_buttonIrPath.widthHint = 100;
		buttonIrPath.setLayoutData(gd_buttonIrPath);
		buttonIrPath.setText(Messages.LLDBCDebuggerPage_browse);
		buttonIrPath.addSelectionListener(new BtnSelectionListener(gbirText));


		Label weightlabel = new Label(group1, SWT.NONE);
		weightlabel.setText("Weight Path:");
		gbweightText = new Text(group1, SWT.BORDER);
		gbweightText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		Button buttonWeightPath = new Button(group1, SWT.NONE);
		GridData gd_buttonWeightPath = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_buttonWeightPath.widthHint = 100;
		buttonWeightPath.setLayoutData(gd_buttonWeightPath);
		buttonWeightPath.setText(Messages.LLDBCDebuggerPage_browse);
		buttonWeightPath.addSelectionListener(new BtnSelectionListener(gbweightText));

		Label otherCommandsLabel = new Label(group1, SWT.None);
		otherCommandsLabel.setText("Other Options:");
		otherCommandsLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		gbOtherCommand = new Text(group1, SWT.BORDER);
		gbOtherCommand.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 3, 1));

		// group2: hardware connection cfg
		Group group2 = new Group(com, SWT.None);
		group2.setText("Remote Target");
		group2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		createConnectionComposite(group2);

		gbirText.addListener(SWT.Modify, new ChangeListener());
		gbweightText.addListener(SWT.Modify, new ChangeListener());
		gbOtherCommand.addListener(SWT.Modify, new ChangeListener());

		return com;

	}

	protected void createConnectionComposite(Composite composite) {
		GridLayout gd_group = new GridLayout();
		gd_group.numColumns = 4;
		composite.setLayout(gd_group);
		
		btn1 = new Button(composite, SWT.RADIO | SWT.LEFT);
		btn1.setText("Connect with network");
		btn1.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 4, 1));
		btn1.setToolTipText(Messages.NPUDebugOnHardwareWithNetwork_tip);

		Label hideLabel = new Label(composite, SWT.NONE);
		hideLabel.setText("    ");
		addrressLabel = new Label(composite, SWT.NONE);
		addrressLabel.setText("Aipugdbserver Connection:");
		addrressLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		addrrsText = new Text(composite, SWT.BORDER);
		addrrsText.setText(Messages.NPUDebugOnHardware_Ip_port_tip);
		addrrsText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		Label hideLabel2 = new Label(composite, SWT.NONE);
		runcfgLabel = new Label(composite, SWT.None);
		runcfgLabel.setText("Run Configuration File:");
		runcfgText = new Text(composite, SWT.BORDER);
		runcfgText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		cfgBtn = new Button(composite, SWT.NONE);
		cfgBtn.setText(Messages.LLDBCDebuggerPage_browse);
		cfgBtn.addSelectionListener(new BtnSelectionListener(runcfgText));
		GridData gd_buttoncfgPath = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_buttoncfgPath.widthHint = 100;
		cfgBtn.setLayoutData(gd_buttoncfgPath);

		btn2 = new Button(composite, SWT.RADIO | SWT.LEFT);
		btn2.setText("Connect with no-network");
		btn2.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 4, 1));

		btn2.setToolTipText(Messages.NPUDebugOnHardwareWithNoNetwork_tip);

		// Label hideLabel4 = new Label(composite, SWT.NONE);
		// runcfgnonetLabel = new Label(composite, SWT.None);
		// runcfgnonetLabel.setText("Run Configuration File:");
		// runnonetcfgText = new Text(composite, SWT.BORDER);
		// runnonetcfgText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		// cfgnonetBtn = new Button(composite, SWT.NONE);
		// cfgnonetBtn.setText(Messages.LLDBCDebuggerPage_browse);
		// cfgnonetBtn.addSelectionListener(new BtnSelectionListener(runnonetcfgText));
		// GridData gd_buttonnonetcfgPath = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		// gd_buttonnonetcfgPath.widthHint = 100;
		// cfgnonetBtn.setLayoutData(gd_buttonnonetcfgPath);

		Label hideLabel3 = new Label(composite, SWT.NONE);
		hideLabel3.setText("    ");
		hideLabel3.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 3));
		fileListCom = new List(composite, SWT.BORDER);
		fileListCom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 3));
		addBtn = new Button(composite, SWT.None);
		addBtn.setText("Add File");
		GridData addbtnGd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		addbtnGd.widthHint = 100;
		addBtn.setLayoutData(addbtnGd);

		deleteBtn = new Button(composite, SWT.NONE);
		deleteBtn.setText("Delete File");
		deleteBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		copyBtn = new Button(composite, SWT.None);
		copyBtn.setText("Copy To");
		copyBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btn1.setSelection(true);
		setConntectionTabControlsStatus(true);

		addBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				FileDialog f = new FileDialog(new Shell(), SWT.OPEN);
				f.setText("Select the  file ");
				String fn = f.open();
				if (fn != null) {
					fileListCom.add(fn);
				}
			}

		});

		deleteBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int[] selectfiles = fileListCom.getSelectionIndices();
				if (selectfiles.length <= 0) {
					MyMessageBox.showMessage("Please select a file first");
					return;
				}
				fileListCom.remove(selectfiles);
			}

		});
		
		copyBtn.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e) {

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

		});
		
		

		btn1.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btn1.getSelection()) {
					setConntectionTabControlsStatus(true);
				} else {
					setConntectionTabControlsStatus(false);
				}

			}

		});

		btn1.addListener(SWT.Selection, new ChangeListener());
		btn2.addListener(SWT.Selection, new ChangeListener());
		addrrsText.addListener(SWT.Modify, new ChangeListener());
		runcfgText.addListener(SWT.Modify, new ChangeListener());
		// runnonetcfgText.addListener(SWT.Modify, new ChangeListener());
		fileListCom.addListener(SWT.Modify, new ChangeListener());

	}
	

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// I'm actually not sure this is needed but it seems this will be called
		// if this delegate is used when first initializing defaults (i.e. GDB
		// is not the delegate for this configuration first)
		IPreferenceStore corePreferenceStore = LLDBUIPlugin.getDefault().getCorePreferenceStore();
		configuration.setAttribute(ILLDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
				corePreferenceStore.getString(ILLDBDebugPreferenceConstants.PREF_DEFAULT_LLDB_COMMAND));
	}//CUSTOMIZATION

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		// CUSTOMIZATION remove lldb-mi debugger name in Debug Tab
//		IPreferenceStore preferenceStore = LLDBUIPlugin.getDefault().getCorePreferenceStore();
//		String lldbCommand = getStringAttr(configuration, ILLDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
//				preferenceStore.getString(ILLDBDebugPreferenceConstants.PREF_DEFAULT_LLDB_COMMAND));
//		fLLDBCommandText.setText(lldbCommand);
		
		//CUSTOMIZATION
		try {
			// get default ir path
			String projectName = configuration.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
			pro = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			if (pro.exists()) {
				String irFolder = AIPULaunchPathUtils.getIRFolderPath(pro);
				if (irFolder.equals(""))
					return;
				File file = new File(irFolder);
				File[] filelists = file.listFiles();
				String irPath = "";
				String weightPath = "";
				Set<String> inputpath = AIPULaunchPathUtils.getInputBinPath(pro);
				String hardware_debug_cfg =AIPULaunchPathUtils.getDebugRun_cfg_FilePath(pro, 1); 
				for (int i = 0; i < filelists.length; i++) {
					File tmp = filelists[i];
					if (tmp.getName().endsWith(".txt")) {
						irPath = tmp.getAbsolutePath();
					} else if (tmp.getName().endsWith(".bin")) {
							weightPath = tmp.getAbsolutePath();
					}
				}

				comboPlatform.select(configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_DEBUG__MODE, 0));
				runirText.setText(configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_IR_PATH, irPath));
				runweightText.setText(configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_WEIGHT_PATH, weightPath));
				// inputs
				Set<String> inputfiles = new HashSet<String>();
				inputfiles = configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_INPUT_PATH, inputpath);
				inputText.removeAll();
				for (String s : inputfiles) {
					inputText.add(s);
				}

				runTarget.select(configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_TARGET, 0));
				runOtherCommand.setText(configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_OTHERCOMMAND, ""));

				// hardrware
				gbirText.setText(configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_IR_PATH, irPath));
				gbweightText.setText(configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_WEIGHT_PATH, weightPath));
					gbOtherCommand.setText(configuration
						.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_OTHER_COMMAND, ""));

					addrrsText.setText(configuration.getAttribute(
						NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_CONNECT_ADDR, connect_addr_default_value));
					runcfgText.setText(configuration
						.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_RUN_CFG,
									hardware_debug_cfg));
					// runnonetcfgText.setText(configuration.getAttribute(
					// NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_RUN_NONET_CFG, hardware_debug_cfg));

					Set<String> fileSets = new HashSet<String>();
					fileSets = configuration
							.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_TRANSLATE_FILES, fileSets);
					// init fileListCom;
					// String[] t_files = getTranslateFile(files);
					if (fileSets != null) {
						fileListCom.removeAll();
						for (String s : fileSets) {
							fileListCom.add(s);
						}
					}
					int connectMode = configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_CONNECT_MODE, 0);
					if (connectMode == 0)
					{
						btn1.setSelection(true);
						btn2.setSelection(false);
						setConntectionTabControlsStatus(true);
					}
					else if (connectMode == 1)
					{

						btn1.setSelection(false);
						btn2.setSelection(true);
						setConntectionTabControlsStatus(false);
					}

					if (comboPlatform.getSelectionIndex() == 0)
					{
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

	private static String getStringAttr(ILaunchConfiguration config, String attributeName, String defaultValue) {
		try {
			return config.getAttribute(attributeName, defaultValue);
		} catch (CoreException e) {
			return defaultValue;
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// CUSTOMIZATION set debug name default to "aipudbgmi"
		configuration.setAttribute(ILLDBLaunchConfigurationConstants.ATTR_DEBUG_NAME, "aipudbgmi".trim());
		//CUSTOMIZATION
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_DEBUG__MODE, comboPlatform.getSelectionIndex());
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_IR_PATH, runirText.getText());
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_WEIGHT_PATH, runweightText.getText());

		String[] items = inputText.getItems();
		Set<String> inputfiles = new HashSet<>(Arrays.asList(items));
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_INPUT_PATH, inputfiles);
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_TARGET, runTarget.getSelectionIndex());
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_OTHERCOMMAND, runOtherCommand.getText());
		//hardware
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
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_TRANSLATE_FILES,
				saveTranslateFile(pro, fileListCom.getItems()));


	}

	@Override
	public String getName() {
		return Messages.LLDBCDebuggerPage_tab_name;
	}

	private void setConntectionTabControlsStatus(boolean isNetwork) {
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

	private Set<String> saveTranslateFile(IProject pro, String[] files)
	{
        Set<String>  allfiles = new HashSet<String>();        
		Set<String> inputPath = AIPULaunchPathUtils.getInputBinPath(pro);
		String aipuBinPath = AIPULaunchPathUtils.getHardwareBinPath(pro);
		String runcfgPath = AIPULaunchPathUtils.getDebugRun_cfg_FilePath(pro, 1);
		allfiles.addAll(inputPath);
		if (FileUtils.exist(aipuBinPath))
			allfiles.add(aipuBinPath);
	    allfiles.add(runcfgPath);

		for(int i=0; i<files.length; i++)
		{
			allfiles.add(files[i]);
		}
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
			if (fn != null) {
//				String originText = text.getText().trim();
//				HashSet<String> elements;
//				if(!originText.equals(""))
//					elements = new HashSet<String>(Arrays.asList(originText.split(",")));
//				else
//					elements = new HashSet<String>();
//				elements.add(fn);
//				text.setText(String.join(",", elements));
				text.add(fn);

			}
		}
	}

	class ChangeListener implements Listener {

		@Override
		public void handleEvent(Event event) {
			updateLaunchConfigurationDialog();
		}

	}
}

package org.eclipse.cdt.llvm.dsf.lldb.ui.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.llvm.dsf.lldb.core.ILLDBLaunchConfigurationConstants;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import cn.com.armchina.ide.tools.FileUtils;
import cn.com.armchina.ide.tools.LaunchUtils.AIPULaunchPathUtils;
import cn.com.armchina.ide.tools.consts.GBuildConsts;
import cn.com.armchina.toolchain.core.NPUDBCoreConsts;
import cn.com.armchina.toolchain.core.NPUDBLaunchConfigurationConstants;

/**
 * @ClassName: LLDBOpenCLDebuggerPage
 * 
 * @Description: Customized debugger page for compass opencl
 * 
 * @author Alan Chen
 */
public class LLDBOpenCLDebuggerPage extends LLDBCDebuggerPage {

	@Override
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
		runTarget.setItems(GBuildConsts.simulator_targets_x2);
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
		runTarget.addListener(SWT.Modify, new ChangeListener());
		runOtherCommand.addListener(SWT.Modify, new ChangeListener());

		return com;

	}

	@Override
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

		Label targetLabel = new Label(group1, SWT.NONE);
		targetLabel.setText("Target:");
		hardwareRunTarget = new Combo(group1, SWT.READ_ONLY);
		hardwareRunTarget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		hardwareRunTarget.setItems(GBuildConsts.simulator_targets_x2);
		hardwareRunTarget.select(0);

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
		hardwareRunTarget.addListener(SWT.Modify, new ChangeListener());

		return com;

	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		// CUSTOMIZATION remove lldb-mi debugger name in Debug Tab

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
				List<String> inputpath = AIPULaunchPathUtils.getInputBinPath(pro);
				String hardware_debug_cfg = AIPULaunchPathUtils.getDebugRunCfgForX2(pro, 1);
				for (int i = 0; i < filelists.length; i++) {
					File tmp = filelists[i];
					if (tmp.getName().endsWith(".txt")) {
						irPath = tmp.getAbsolutePath();
					} else if (tmp.getName().endsWith(".bin")) {
						weightPath = tmp.getAbsolutePath();
					}
				}

				comboPlatform.select(configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_DEBUG__MODE, 0));
				runirText.setText(
						configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_IR_PATH, irPath));
				runweightText.setText(configuration
						.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_WEIGHT_PATH, weightPath));
				// inputs
				List<String> inputfiles = new ArrayList<String>();
				inputfiles = configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_INPUT_PATH,
						inputpath);

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
				runcfgText.setText(configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_RUN_CFG,
						hardware_debug_cfg));
				// runnonetcfgText.setText(configuration.getAttribute(
				// NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_RUN_NONET_CFG,
				// hardware_debug_cfg));

				Set<String> fileSets = new HashSet<String>();
				fileSets = configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_TRANSLATE_FILES,
						fileSets);
				// init fileListCom;
				// String[] t_files = getTranslateFile(files);
				if (fileSets != null) {
					fileListCom.removeAll();
					for (String s : fileSets) {
						fileListCom.add(s);
					}
				}
				int connectMode = configuration
						.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_CONNECT_MODE, 0);
				if (connectMode == 0) {
					btn1.setSelection(true);
					btn2.setSelection(false);
					setConntectionTabControlsStatus(true);
				} else if (connectMode == 1) {

					btn1.setSelection(false);
					btn2.setSelection(true);
					setConntectionTabControlsStatus(false);
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

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// CUSTOMIZATION set debug name default to "aipudbgmi"
		configuration.setAttribute(ILLDBLaunchConfigurationConstants.ATTR_DEBUG_NAME, NPUDBCoreConsts.AIPUODBMI);
		// CUSTOMIZATION
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
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_TRANSLATE_FILES,
				saveTranslateFile(pro, fileListCom.getItems()));

	}

	// add file lists that need to transfer to hardware for X2
	@Override
	public Set<String> saveTranslateFile(IProject pro, String[] files) {
		Set<String> allfiles = new HashSet<String>();
		List<String> inputPath = AIPULaunchPathUtils.getInputBinPath(pro);
		String aipuBinPath = AIPULaunchPathUtils.getHardwareBinPathForX2(pro);
		String runcfgPath = AIPULaunchPathUtils.getDebugRunCfgForX2(pro, 1);
		allfiles.addAll(inputPath);
		if (FileUtils.exist(aipuBinPath))
			allfiles.add(aipuBinPath);
		allfiles.add(runcfgPath);

		for (int i = 0; i < files.length; i++) {
			allfiles.add(files[i]);
		}
		return allfiles;
	}
}

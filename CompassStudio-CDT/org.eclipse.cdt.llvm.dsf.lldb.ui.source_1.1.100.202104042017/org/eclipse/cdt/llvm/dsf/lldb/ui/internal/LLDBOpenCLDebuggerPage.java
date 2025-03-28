package org.eclipse.cdt.llvm.dsf.lldb.ui.internal;

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.llvm.dsf.lldb.core.ILLDBLaunchConfigurationConstants;
import org.eclipse.cdt.llvm.dsf.lldb.ui.internal.LLDBCDebuggerPage.ChangeListener;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
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
	public Text cfgFileText;
	public Text execFileText;
	public Text coreDumpFileText;

	public String natureID;
//	public String DSL_NATURE_ID = "cn.com.armchina.tvm.dsl.ui.dslnature";
//	public String dslGBuildPath="";
	public Button single_tec;
	public Button single_core;
	public Button layer_debug;
//	public String[] platform_type = { "Simulator", "Hardware","Coredump" };//CUSTOMIZATION FOR COREDUMP

	@Override
	public void createSingleTecControl(Composite composite) {
		GridData layoutData = new GridData();
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.grabExcessHorizontalSpace = false;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = SWT.FILL;
		layoutData.widthHint = 250;
		layoutData.horizontalSpan = 4;
		layoutData.heightHint = 20;
		single_tec = createCheckButton(composite, "Select Target Single Tec Mode");
		single_tec.setToolTipText("It means that only the current TEC will be resumed if you execute the program, while others ignore your execution command.");
		single_tec.setLayoutData(layoutData);
		
		single_core = createCheckButton(composite, "Select Target Single Core Mode");
		single_core.setToolTipText("It means that only the current Core will be resumed if you execute the program, while others ignore your execution command.");
		single_core.setLayoutData(layoutData);
		single_core.addSelectionListener(widgetSelectedAdapter(e -> updateLaunchConfigurationDialog()));
		
		single_tec.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (single_tec.getSelection()==true) {
					single_core.setSelection(true);
				}else {
					single_core.setSelection(false);
				}
				updateLaunchConfigurationDialog();
			}
		});
		
		//CUSTOMIZATION FOR LAYER DEBUG
		//Release for 2024_Q3
		layer_debug = createCheckButton(composite, "Start Multi-Layer Debugging");
		layer_debug.setToolTipText("It will start multi-layer debugging to display subgraph and layer information. It has all the functions of debug such as step into, step over, breakpoint, memory read, etc.");
		layer_debug.setLayoutData(layoutData);
		layer_debug.addSelectionListener(widgetSelectedAdapter(e -> updateLaunchConfigurationDialog()));
	}
	
	@Override
	public Composite createCoreDumpCfgComposite(Composite composite) {//CUSTOMIZATION FOR COREDUMP
		Composite com = new Composite(composite, SWT.NONE);
		com.setLayout(new FillLayout());
		Group group = new Group(com, SWT.FILL);
		group.setText("Coredump.run configuration");
		GridLayout gd = new GridLayout();
		gd.numColumns = 4;
		gd.marginLeft = 5;
		gd.marginRight = 5;
		group.setLayout(gd);
		
		createLabel(group, "Coredump File:");
		coreDumpFileText= createText(group, SWT.BORDER);
		coreDumpFileText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		Button buttonIrPath = createButton(group, Messages.LLDBCDebuggerPage_browse);
		GridData gd_buttonIrPath = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_buttonIrPath.widthHint = 100;
		buttonIrPath.setLayoutData(gd_buttonIrPath);
		buttonIrPath.addSelectionListener(new BtnSelectionListener(coreDumpFileText));

		createLabel(group, "Graph File:");
		execFileText = createText(group, SWT.BORDER);
		execFileText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));

		Button buttonWeightPath = createButton(group, Messages.LLDBCDebuggerPage_browse);
		GridData gd_buttonWeightPath = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
		gd_buttonWeightPath.widthHint = 100;
		buttonWeightPath.setLayoutData(gd_buttonWeightPath);
		buttonWeightPath.addSelectionListener(new BtnSelectionListener(execFileText));
		
		coreDumpFileText.addListener(SWT.Modify, new ChangeListener());
		execFileText.addListener(SWT.Modify, new ChangeListener());
		
		return com;
	}

	@Override
	public void selectLayout() {//CUSTOMIZATION FOR COREDUMP
		if (comboPlatform.getText().equals(platform_type[0])) {
			stackLayout.topControl = simulatorCom;
			cfgComposite.layout();
		} else if (comboPlatform.getText().equals(platform_type[1])) {
			stackLayout.topControl = hardwareCom;
			cfgComposite.layout();
		}else {
			stackLayout.topControl = coreDumpCom;
			cfgComposite.layout();
		}
		updateLaunchConfigurationDialog();
	}
	
	@Override
	public void initializeDebugTypeFrom() {//CUSTOMIZATION FOR COREDUMP
		if (comboPlatform.getSelectionIndex() == 0) {
			stackLayout.topControl = simulatorCom;
			cfgComposite.layout();
		} else if (comboPlatform.getSelectionIndex() == 1){
			stackLayout.topControl = hardwareCom;
			cfgComposite.layout();
		}else {
			stackLayout.topControl = coreDumpCom;
			cfgComposite.layout();
		}
	}

	@Override
	public void createSimInputControl(Group group) {
		createLabel(group, "Run File:");
		cfgFileText = createText(group, SWT.BORDER);
		cfgFileText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));

		Button cfgFilePath = createButton(group, Messages.LLDBCDebuggerPage_browse);
		GridData gd_cfgFilePath = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
		gd_cfgFilePath.widthHint = 100;
		cfgFilePath.setLayoutData(gd_cfgFilePath);
		cfgFilePath.addSelectionListener(new BtnSelectionListener(cfgFileText));

		cfgFileText.addListener(SWT.Modify, new ChangeListener());
	}
	
	//CUSTOMIZATION FOR COREDUMP
	@Override
	public CCombo createCustomizedDebugModeCombo(Composite composite) {
		return createReadOnlyCCombo(composite, platform_type);
	}

	@Override
	public Combo createHardwareTargetComboControl(Group group) {
		return createReadOnlyCombo(group, GBuildConsts.simulator_targets_x2);
	}

	@Override
	public Combo createCustomizedTargerCombo(Group group) {
		return createReadOnlyCombo(group, GBuildConsts.simulator_targets_x2);
	}

	@Override
	public void initialCustomizationFrom(ILaunchConfiguration configuration) throws CoreException {
		// add customized action initial here
		natureID=pro.getDescription().getNatureIds()[0];
		String sim_debug_cfg = "";
		String hardware_debug_cfg = "";
		//CUSTOMIZATION FOR DSL
		//Release for 2024_Q1
//		if (natureID.equals(DSL_NATURE_ID)) {
//			String proGramName = configuration.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, "");
//			if (!proGramName.equals("")) {
//				String path = new File(proGramName).getParent();
//				dslGBuildPath = new File(path).getParent();
//				sim_debug_cfg = dslGBuildPath + File.separator + "opencl_debug.cfg";
//				String dsl_ir=dslGBuildPath + File.separator + "compass_ir.txt";
//				String dsl_weight=dslGBuildPath + File.separator + "compass_ir.bin";
//
//				if (new File(sim_debug_cfg).exists()) {
//					if (configuration
//							.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_CFG_PATH, sim_debug_cfg)
//							.equals("")) {
//						cfgFileText.setText(sim_debug_cfg);
//						runcfgText.setText(sim_debug_cfg);
//						
//						runirText.setText( dsl_ir);
//						runweightText.setText(dsl_weight);
//
//						// hardrware
//						gbirText.setText(dsl_ir);
//						gbweightText.setText(dsl_weight);
//					} else {
//						cfgFileText.setText(configuration.getAttribute(
//								NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_CFG_PATH, sim_debug_cfg));
//						runcfgText.setText(configuration.getAttribute(
//								NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_RUN_CFG, sim_debug_cfg));
//						
//						runirText.setText(configuration
//								.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_IR_PATH, dsl_ir));
//						runweightText.setText(configuration.getAttribute(
//								NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_WEIGHT_PATH, dsl_weight));
//
//						// hardrware
//						gbirText.setText(configuration
//								.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_IR_PATH, dsl_ir));
//						gbweightText.setText(configuration
//								.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_WEIGHT_PATH, dsl_weight));
//					}
//				} else
//					MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Warning",
//							"The opencl debug file does not exist. Please configure \"aipu.tir.BuildManager(is_debug=True)\" in dsl and rerun the dsl script.");
//			}
//		} else {
			sim_debug_cfg = AIPULaunchPathUtils.getDebugRunCfgForX2(pro, 0);
			cfgFileText.setText(configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_CFG_PATH,
					sim_debug_cfg));
			hardware_debug_cfg = AIPULaunchPathUtils.getDebugRunCfgForX2(pro, 1);
			runcfgText.setText(configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_RUN_CFG,
					hardware_debug_cfg));
			if (irPath==null & weightPath==null) {
				runirText.setText(configuration
						.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_IR_PATH, ""));
				runweightText.setText(configuration.getAttribute(
						NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_WEIGHT_PATH, ""));
				// hardrware
				gbirText.setText(configuration
						.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_IR_PATH, ""));
				gbweightText.setText(configuration
						.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_WEIGHT_PATH, ""));
			}else {
				runirText.setText(configuration
						.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_IR_PATH, irPath));
				runweightText.setText(configuration.getAttribute(
						NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_WEIGHT_PATH, weightPath));
				// hardrware
				gbirText.setText(configuration
						.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_IR_PATH, irPath));
				gbweightText.setText(configuration
						.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_WEIGHT_PATH, weightPath));
			}
			runOtherCommand.setText(configuration
					.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_OTHERCOMMAND, ""));

			gbOtherCommand.setText(configuration
					.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_OTHER_COMMAND, ""));
//		}
		single_tec.setSelection(configuration
				.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_TEC_MODE,false));
		single_core.setSelection(configuration
				.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_CORE_MODE,false));
		//Release for 2024_Q3
		layer_debug.setSelection(configuration
				.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_LAYER_DEBUG_MODE,false));
		execFileText.setText(configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_CORE_DUMP_GRAPH_PATH,
				""));
		coreDumpFileText.setText(configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_CORE_DUMP_FILE_PATH,
				""));
//		cfgFileText.setText(configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_CFG_PATH,
//				sim_debug_cfg));
	}

	@Override
	public void performCustomizationApply(ILaunchConfigurationWorkingCopy configuration) {
		// add customized action performApply here
		// CUSTOMIZATION set debug name default to "aipudbgmi"
		configuration.setAttribute(ILLDBLaunchConfigurationConstants.ATTR_DEBUG_NAME, NPUDBCoreConsts.AIPUODBMI);
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_TEC_MODE, single_tec.getSelection());
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_CORE_MODE, single_core.getSelection());
		//Release for 2024_Q3
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_LAYER_DEBUG_MODE, layer_debug.getSelection());
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_CORE_DUMP_GRAPH_PATH,
				execFileText.getText());
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_CORE_DUMP_FILE_PATH,
				coreDumpFileText.getText());
	}

	// set multi-code debug env only for x2_1204mp3 on simulator. debug on hardware
	// can get multi-cores automatically,so it don't need this env variable.
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		super.performApply(configuration);
		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_SIMULATOR_CFG_PATH,
					cfgFileText.getText());
		//Release for 2024_Q1
//		if (natureID.equals(DSL_NATURE_ID)) {
//			String fBinaryName=dslGBuildPath + File.separator + "aipu.bin";
//			configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_DSL_BIN_PATH,
//					fBinaryName);
//		}
		if (comboPlatform.getText().equals(platform_type[0])
				&& runTarget.getText().equals(GBuildConsts.simulator_targets_x2[1])) {
			configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_MULTICORE, true);
		} else {
			configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_MULTICORE, false);
		}
	}

	// add file lists that need to transfer to hardware for X2
	@Override
	public java.util.List<String> addTranslateFile(IProject pro) {
		java.util.List<String> allfiles = new ArrayList<String>();
		String aipuBinPath,runcfgPath;
		List<String> inputPath = null;
		//CUSTOMIZATION FOR DSL
		//Release for 2024_Q1
//		if (natureID.equals(DSL_NATURE_ID)) {
//			if (dslGBuildPath.equals("")) {
//				return allfiles;
//			}
//			inputPath=getDSLInputFile(dslGBuildPath);
//			aipuBinPath =dslGBuildPath+File.separator+"aipu.bin";
//			runcfgPath = dslGBuildPath+File.separator+"opencl_debug.cfg";
//		}else {
			inputPath = AIPULaunchPathUtils.getInputBinPath(pro);
			aipuBinPath = AIPULaunchPathUtils.getHardwareBinPathForX2(pro);
			runcfgPath = AIPULaunchPathUtils.getDebugRunCfgForX2(pro, 1);
//		}
		allfiles.addAll(inputPath);
		if (FileUtils.exist(aipuBinPath))
			allfiles.add(aipuBinPath);
		allfiles.add(runcfgPath);

		return allfiles;
	}
	
	public List<String> getDSLInputFile(String path){
		List<String> inputList = new ArrayList<String>();
		File[] files = new File(path).listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().startsWith("input")) {
				inputList.add(files[i].getAbsolutePath());
			}
		}
		return inputList;
	}
}

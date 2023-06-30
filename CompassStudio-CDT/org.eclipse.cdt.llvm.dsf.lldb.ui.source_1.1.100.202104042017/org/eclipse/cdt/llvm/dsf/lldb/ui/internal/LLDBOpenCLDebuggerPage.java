package org.eclipse.cdt.llvm.dsf.lldb.ui.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.llvm.dsf.lldb.core.ILLDBLaunchConfigurationConstants;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;

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
//	public Button single_tec;

//	@Override
//	public void createSingleTecControl(Composite composite) {
//		GridData layoutData = new GridData();
//		layoutData.horizontalAlignment = SWT.FILL;
//		layoutData.grabExcessHorizontalSpace = false;
//		layoutData.grabExcessVerticalSpace = false;
//		layoutData.verticalAlignment = SWT.FILL;
//		layoutData.widthHint = 250;
//		layoutData.horizontalSpan = 4;
//		layoutData.heightHint = 20;
//		single_tec = createCheckButton(composite, "Select Target Single Tec Mode");
//		single_tec.setLayoutData(layoutData);
//		single_tec.addSelectionListener(widgetSelectedAdapter(e -> updateLaunchConfigurationDialog()));
//	}

	@Override
	public void createSimInputControl(Group group) {
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
		String hardware_debug_cfg = AIPULaunchPathUtils.getDebugRunCfgForX2(pro, 1);
		runcfgText.setText(configuration.getAttribute(NPUDBLaunchConfigurationConstants.ATTR_HARDWARE_RUN_CFG,
				hardware_debug_cfg));
	}

	@Override
	public void performCustomizationApply(ILaunchConfigurationWorkingCopy configuration) {
		// add customized action performApply here
		// CUSTOMIZATION set debug name default to "aipudbgmi"
		configuration.setAttribute(ILLDBLaunchConfigurationConstants.ATTR_DEBUG_NAME, NPUDBCoreConsts.AIPUODBMI);
//		configuration.setAttribute(NPUDBLaunchConfigurationConstants.ATTR_TEC_MODE, single_tec.getSelection());
	}

	// set multi-code debug env only for x2_1204mp3 on simulator. debug on hardware can get multi-cores automatically,so it don't need this env variable.
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		super.performApply(configuration);
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
		List<String> inputPath = AIPULaunchPathUtils.getInputBinPath(pro);
		String aipuBinPath = AIPULaunchPathUtils.getHardwareBinPathForX2(pro);
		String runcfgPath = AIPULaunchPathUtils.getDebugRunCfgForX2(pro, 1);
		allfiles.addAll(inputPath);
		if (FileUtils.exist(aipuBinPath))
			allfiles.add(aipuBinPath);
		allfiles.add(runcfgPath);

		return allfiles;
	}
}

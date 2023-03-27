package org.eclipse.cdt.llvm.dsf.lldb.ui.launchconfigurationgroup;

import org.eclipse.cdt.dsf.gdb.internal.ui.launching.OpenCLMainTab;
import org.eclipse.cdt.llvm.dsf.lldb.ui.internal.LLDBLocalApplicationOpenCLDebuggerTab;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;

/**
 * @ClassName: AipuDbgDebuggerTabsGroup
 * 
 * @Description:Customized debugger tab group for compass opencl
 * 
 * @author Alan Chen
 */
//CUSTOMIZATION
public class AipuODbDebuggerTabsGroup extends AipuDbgDebuggerTabsGroup {
	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] { new OpenCLMainTab(),
				new LLDBLocalApplicationOpenCLDebuggerTab(), new EnvironmentTab(), new CommonTab(),
				new SourceLookupTab() };
		setTabs(tabs);
	}
}

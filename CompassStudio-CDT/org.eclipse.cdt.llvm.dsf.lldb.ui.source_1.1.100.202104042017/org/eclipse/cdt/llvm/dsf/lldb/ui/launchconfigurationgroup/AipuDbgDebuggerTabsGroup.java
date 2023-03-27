package org.eclipse.cdt.llvm.dsf.lldb.ui.launchconfigurationgroup;

import org.eclipse.cdt.dsf.gdb.internal.ui.launching.CMainTab;
import org.eclipse.cdt.llvm.dsf.lldb.ui.internal.LLDBLocalApplicationCDebuggerTab;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;

/**
 * @ClassName: AipuDbgDebuggerTabsGroup
 * 
 * @Description:Customized debugger tab group for compass c
 * 
 * @author Alan Chen
 */
//CUSTOMIZATION
public class AipuDbgDebuggerTabsGroup extends AbstractLaunchConfigurationTabGroup {

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] { new CMainTab(),
				new LLDBLocalApplicationCDebuggerTab(), new EnvironmentTab(), new CommonTab(), new SourceLookupTab()};
		setTabs(tabs);
	}

}

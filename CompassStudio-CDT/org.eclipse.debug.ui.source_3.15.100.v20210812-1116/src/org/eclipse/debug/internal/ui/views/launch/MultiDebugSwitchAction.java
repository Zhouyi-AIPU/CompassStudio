package org.eclipse.debug.internal.ui.views.launch;

import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.actions.DebugCommandAction;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @ClassName: MultiDebugSwitchAction
 * 
 * @Description:Multi debug switch debug view action
 * 
 * @author Alan Chen
 */
//CUSTOMIZATION FOR Multi-Core Debug
public class MultiDebugSwitchAction extends DebugCommandAction {

	public MultiDebugSwitchAction() {
		setActionDefinitionId("org.eclipse.debug.ui.commands.select"); //$NON-NLS-1$
	}

	@Override
	protected Class<IMultiDebugSwitchHandler> getCommandType() {
		return IMultiDebugSwitchHandler.class;
	}

	@Override
	public ImageDescriptor getDisabledImageDescriptor() {
		return DebugPluginImages.getImageDescriptor(IDebugUIConstants.IMG_SWITCH);
	}

	@Override
	public String getHelpContextId() {
		return "org.eclipse.debug.ui.restart_action_context";
	}

	@Override
	public ImageDescriptor getHoverImageDescriptor() {
		return DebugPluginImages.getImageDescriptor(IDebugUIConstants.IMG_SWITCH);
	}

	@Override
	public String getId() {
		return "org.eclipse.debug.ui.actions.Selected";
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return DebugPluginImages.getImageDescriptor(IDebugUIConstants.IMG_SWITCH);
	}

	@Override
	public String getText() {
		return "Switch";
	}

	@Override
	public String getToolTipText() {
		return "Switch to current WorkItem";
	}

}

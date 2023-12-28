package org.eclipse.debug.internal.ui.commands.actions;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.ui.actions.DebugCommandAction;
import org.eclipse.debug.ui.contexts.DebugContextEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

public class ToggleSingleModeAction extends DebugCommandAction {

	// this is a property that store the status of single mode checked or unchecked
	public static String ENABEL_SINGLE_MODE = "cn.com.armchina.toolchain.core.singleMode";

	public ToggleSingleModeAction() {
		setActionDefinitionId("org.eclipse.debug.ui.commands.toggleSingleMode");

	}

	@Override
	protected Class<?> getCommandType() {
		return IToggleSingleModeHandler.class;
	}

	@Override
	public String getHelpContextId() {
		return "cn.com.armchina.toolchain.opencl.debug.ui.commands.toggleSingleMode_context";
	}

	@Override
	public String getId() {
		return "org.eclipse.debug.ui.debugview.popupMenu.toggleSingleMode";
	}

	@Override
	public String getText() {
		return "Disable Single Mode";
	}

	@Override
	public String getToolTipText() {

		return "set single mode false";
	}

	@Override
	public ImageDescriptor getDisabledImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageDescriptor getHoverImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	protected boolean getInitialEnablement() {
		return false;
	}


	@Override
	public int getStyle() {
		return AS_CHECK_BOX;
	}

	@Override
	public void debugContextChanged(DebugContextEvent event) {
		ISelection context = event.getContext();
		if (context.isEmpty()) {
			setEnabled(true);
		} else {
			super.debugContextChanged(event);
		}
	}

	@Override
	public void run() {

		ISelection selection = getContext();
		IStructuredSelection ss = (IStructuredSelection) selection;
		// store the single mode status in launch session.
		ILaunch launch = DebugUIPlugin.getLaunch(ss.getFirstElement());
		if (!isChecked()) {
			launch.setAttribute(ENABEL_SINGLE_MODE, "true");
		} else {
			launch.setAttribute(ENABEL_SINGLE_MODE, "false");
		}
		super.run();
	}


}

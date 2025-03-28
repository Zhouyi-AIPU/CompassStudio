package org.eclipse.debug.internal.ui.commands.actions;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.ui.actions.DebugCommandAction;
import org.eclipse.debug.ui.contexts.DebugContextEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
/**
 * 
 * @ClassName: ToggleSingleTecModeAction.java
 * 
 * @Description: 
 * 
 * @author: alache01
 */
public class ToggleSingleTecModeAction extends DebugCommandAction {

	// this is a property that store the status of single mode checked or unchecked
	public static String ENABEL_SINGLE_TEC_MODE = "cn.com.armchina.toolchain.core.singleTecMode";

	public ToggleSingleTecModeAction() {
		setActionDefinitionId("org.eclipse.debug.ui.commands.toggleSingleTecMode");

	}

	@Override
	protected Class<?> getCommandType() {
		return IToggleSingleTecModeHandler.class;
	}

	@Override
	public String getHelpContextId() {
		return "cn.com.armchina.toolchain.opencl.debug.ui.commands.toggleSingleTecMode_context";
	}

	@Override
	public String getId() {
		return "org.eclipse.debug.ui.debugview.popupMenu.toggleSingleTecMode";
	}

	@Override
	public String getText() {
		return "Target Single Tec Mode";
	}

	@Override
	public String getToolTipText() {

		return "Target single tec mode";
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
		IStructuredSelection ss = (IStructuredSelection) context;
		// store the single mode status in launch session.
		ILaunch launch = DebugUIPlugin.getLaunch(ss.getFirstElement());
		if (launch==null || launch.isTerminated()) {
			setChecked(false);
			setEnabled(false);
		}
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
		if (isChecked()) {
			launch.setAttribute(ENABEL_SINGLE_TEC_MODE, "true");
		} else {
			launch.setAttribute(ENABEL_SINGLE_TEC_MODE, "false");
		}
		super.run();
	}
}

package org.eclipse.debug.internal.ui.commands.actions;

import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.actions.DebugCommandAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

public class RefreshTecCommandAction extends DebugCommandAction {

	public RefreshTecCommandAction() {
		setActionDefinitionId("org.eclipse.debug.ui.commands.refreshTec");
	
	}

	@Override
	public String getHelpContextId() {
		return "cn.com.armchina.toolchain.opencl.debug.ui.commands.refreshTec_context"; //$NON-NLS-1$
	}

	@Override
	public String getId() {
		return "org.eclipse.debug.ui.debugview.popupMenu.refreshTec"; //$NON-NLS-1$
	}

	@Override
	public String getText() {

		return "Refresh";
	}

	@Override
	public String getToolTipText() {

		return "refresh the status of all the cluster, core, tec and workitem";
	}

	@Override
	public ImageDescriptor getDisabledImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageDescriptor getHoverImageDescriptor() {
		return DebugPluginImages.getImageDescriptor(IDebugUIConstants.IMG_REFRESH_TEC);
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return DebugPluginImages.getImageDescriptor(IDebugUIConstants.IMG_REFRESH_TEC);
	}

	@Override
	protected Class<IRefreshTecHandler> getCommandType() {
		return IRefreshTecHandler.class;
	}

	@Override
	public void init(IWorkbenchPart part) {
		super.init(part);
	}

	@Override
	public void init(IWorkbenchWindow window) {

		super.init(window);
	}

}

package org.eclipse.debug.ui.internal.multilayer.actions;

import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.debug.internal.ui.IInternalDebugUIConstants;
import org.eclipse.debug.internal.ui.commands.actions.IMultiLayerStepHandler;
import org.eclipse.debug.ui.actions.DebugCommandAction;
import org.eclipse.jface.resource.ImageDescriptor;
/**
 * 
 * MultiLayerStepCommandAction:layer step command action
 * @author alache01
 *
 */
public class MultiLayerStepCommandAction extends DebugCommandAction {//CUSTOMIZATION FOR LAYER DEBUG

	public MultiLayerStepCommandAction() {//same as plugin.extension of "command id"
		setActionDefinitionId("org.eclipse.debug.ui.commands.LayerStepOver"); 
	}

	@Override
	public String getText() {
		return "LayStep";
	}

	@Override
	public ImageDescriptor getDisabledImageDescriptor() {//TODO:image
		return DebugPluginImages.getImageDescriptor(IInternalDebugUIConstants.IMG_DLCL_STEP_OVER);
	}

	@Override
	public String getHelpContextId() {
		return "org.eclipse.debug.ui.layer_step_over_action_context"; 
	}

	@Override
	public ImageDescriptor getHoverImageDescriptor() {//TODO:image
		return DebugPluginImages.getImageDescriptor(IInternalDebugUIConstants.IMG_ELCL_STEP_OVER);
	}

	@Override
	public String getId() {
		return "org.eclipse.debug.ui.debugview.toolbar.layer.stepOver"; 
	}

	@Override
	public ImageDescriptor getImageDescriptor() {//TODO:image
		return DebugPluginImages.getImageDescriptor(IInternalDebugUIConstants.IMG_ELCL_STEP_OVER);
	}

	@Override
	public String getToolTipText() {
		return "Layer Step";
	}

	@Override
	protected Class<IMultiLayerStepHandler> getCommandType() {
		return IMultiLayerStepHandler.class;
	}

}

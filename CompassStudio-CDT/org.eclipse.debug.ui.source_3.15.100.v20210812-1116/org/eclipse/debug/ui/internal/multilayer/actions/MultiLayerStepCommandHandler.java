package org.eclipse.debug.ui.internal.multilayer.actions;

import org.eclipse.debug.internal.ui.commands.actions.IMultiLayerStepHandler;
import org.eclipse.debug.ui.actions.DebugCommandHandler;
/**
 * MultiLayerStepCommandHandler:layer step command handler
 * @author alache01
 *
 */
public class MultiLayerStepCommandHandler extends DebugCommandHandler {//CUSTOMIZATION FOR LAYER DEBUG

	@Override
	protected Class<IMultiLayerStepHandler> getCommandType() {
		return IMultiLayerStepHandler.class;
	}
}

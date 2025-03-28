package org.eclipse.debug.ui.internal.multilayer.actions;

import org.eclipse.debug.internal.ui.commands.actions.DebugCommandActionDelegate;
import org.eclipse.jface.action.IAction;
/**
 * 
 * MultiLayerStepCommandActionDelegate : layer step action delegate
 * @author alache01
 *
 */
public class MultiLayerStepCommandActionDelegate extends DebugCommandActionDelegate {//CUSTOMIZATION FOR LAYER DEBUG

	public MultiLayerStepCommandActionDelegate() {
		super();
		setAction(new MultiLayerStepCommandAction());
	}

	@Override
	public void init(IAction action) {
		super.init(action);
	}

}
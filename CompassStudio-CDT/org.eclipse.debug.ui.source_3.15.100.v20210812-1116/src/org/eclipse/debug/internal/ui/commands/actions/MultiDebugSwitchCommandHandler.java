package org.eclipse.debug.internal.ui.commands.actions;

import org.eclipse.debug.internal.ui.views.launch.IMultiDebugSwitchHandler;
import org.eclipse.debug.ui.actions.DebugCommandHandler;

/**
 * @ClassName: MultiDebugSwitchCommandHandler
 * 
 * @Description:Multi debug switch debug view action command handler
 * 
 * @author Alan Chen
 */
//CUSTOMIZATION FOR Multi-Core Debug
public class MultiDebugSwitchCommandHandler extends DebugCommandHandler {

	@Override
	protected Class<IMultiDebugSwitchHandler> getCommandType() {
		return IMultiDebugSwitchHandler.class;
	}
}
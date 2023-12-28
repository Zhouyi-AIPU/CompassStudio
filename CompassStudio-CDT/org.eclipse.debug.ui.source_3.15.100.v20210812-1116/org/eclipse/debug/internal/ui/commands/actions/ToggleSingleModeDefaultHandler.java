package org.eclipse.debug.internal.ui.commands.actions;

import org.eclipse.debug.internal.ui.commands.actions.IToggleSingleModeHandler;
import org.eclipse.debug.ui.actions.DebugCommandHandler;

public class ToggleSingleModeDefaultHandler extends DebugCommandHandler {


	@Override
	protected Class<IToggleSingleModeHandler> getCommandType() {
		return IToggleSingleModeHandler.class;
	}

}

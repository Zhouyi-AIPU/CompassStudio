package org.eclipse.debug.internal.ui.commands.actions;

import org.eclipse.debug.ui.actions.DebugCommandHandler;
/**
 * 
 * @ClassName: ToggleSingleCoreModeDefaultHandler.java
 * 
 * @Description: 
 * 
 * @author: alache01
 */
public class ToggleSingleCoreModeDefaultHandler extends DebugCommandHandler {

	@Override
	protected Class<IToggleSingleCoreModeHandler> getCommandType() {
		return IToggleSingleCoreModeHandler.class;
	}

}

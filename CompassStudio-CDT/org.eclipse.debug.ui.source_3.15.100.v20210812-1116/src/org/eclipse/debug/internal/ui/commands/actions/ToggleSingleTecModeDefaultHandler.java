package org.eclipse.debug.internal.ui.commands.actions;

import org.eclipse.debug.ui.actions.DebugCommandHandler;
/**
 * 
 * @ClassName: ToggleSingleTecModeDefaultHandler.java
 * 
 * @Description: 
 * 
 * @author: alache01
 */
public class ToggleSingleTecModeDefaultHandler extends DebugCommandHandler {


	@Override
	protected Class<IToggleSingleTecModeHandler> getCommandType() {
		return IToggleSingleTecModeHandler.class;
	}

}

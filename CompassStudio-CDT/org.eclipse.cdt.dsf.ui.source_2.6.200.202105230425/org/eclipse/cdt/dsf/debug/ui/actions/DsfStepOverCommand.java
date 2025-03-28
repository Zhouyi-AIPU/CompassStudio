/*******************************************************************************
 * Copyright (c) 2006, 2012 Wind River Systems and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Wind River Systems - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.dsf.debug.ui.actions;

import java.util.Map;

import org.eclipse.cdt.debug.core.CDebugUtils;
import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.DsfExecutor;
import org.eclipse.cdt.dsf.concurrent.ImmediateExecutor;
import org.eclipse.cdt.dsf.concurrent.Immutable;
import org.eclipse.cdt.dsf.debug.service.IRunControl.StepType;
import org.eclipse.cdt.dsf.debug.ui.viewmodel.SteppingController;
import org.eclipse.cdt.dsf.internal.ui.DsfUIPlugin;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.commands.IDebugCommandRequest;
import org.eclipse.debug.core.commands.IEnabledStateRequest;
import org.eclipse.debug.core.commands.IStepOverHandler;

import cn.com.armchina.toolchain.core.NPUDBLaunchConfigurationConstants;

/**
 *
 * @since 1.0
 */
@Immutable
public class DsfStepOverCommand implements IStepOverHandler {

	private final DsfExecutor fExecutor;
	private final DsfServicesTracker fTracker;
	private final DsfSteppingModeTarget fSteppingMode;
	private final ILaunch fLaunch;

	public DsfStepOverCommand(DsfSession session, DsfSteppingModeTarget steppingMode,ILaunch launch) {
		fExecutor = session.getExecutor();
		fTracker = new DsfServicesTracker(DsfUIPlugin.getBundleContext(), session.getId());
		fSteppingMode = steppingMode;
		fLaunch=launch;
	}

	public void dispose() {
		fTracker.dispose();
	}

	@Override
	public void canExecute(final IEnabledStateRequest request) {
		Map<String, Object> attributes;//CUSTOMIZATION FOR COREDUMP
		try {
			attributes = fLaunch.getLaunchConfiguration().getAttributes();
			int debugmode = attributes.get(NPUDBLaunchConfigurationConstants.ATTR_LAYER_DEBUG_MODE) == null ? 0:(int) attributes.get(NPUDBLaunchConfigurationConstants.ATTR_DEBUG__MODE);
			if (debugmode == 2) {
				request.setEnabled(false);
				request.done();
				return;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		if (request.getElements().length != 1) {
			request.setEnabled(false);
			request.done();
			return;
		}

		fExecutor.submit(new DsfCommandRunnable(fTracker, request.getElements()[0], request) {
			final StepType stepType = getStepType();

			@Override
			public void doExecute() {
				SteppingController steppingControl = getSteppingController();
				if (steppingControl == null) {
					request.setEnabled(false);
					request.done();
					return;
				}
				steppingControl.canEnqueueStep(getContext(), stepType,
						new DataRequestMonitor<Boolean>(ImmediateExecutor.getInstance(), null) {
							@Override
							protected void handleCompleted() {
								request.setEnabled(isSuccess() && getData());
								request.done();
							}
						});
			}
		});
	}

	@Override
	public boolean execute(final IDebugCommandRequest request) {
		if (request.getElements().length != 1) {
			request.done();
			return false;
		}

		final StepType stepType = getStepType();
		fExecutor.submit(new DsfCommandRunnable(fTracker, request.getElements()[0], request) {
			@Override
			public void doExecute() {
				getSteppingController().enqueueStep(getContext(), stepType);
			}
		});
		return true;
	}

	/**
	 * @return the currently active step type
	 */
	protected final StepType getStepType() {
		boolean instructionSteppingEnabled = fSteppingMode != null && fSteppingMode.isInstructionSteppingEnabled();
		return instructionSteppingEnabled ? StepType.INSTRUCTION_STEP_OVER : StepType.STEP_OVER;
	}

}

/**
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license.txt included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package org.python.pydev.debug.newconsole;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.python.pydev.ast.codecompletion.PyCodeCompletionPreferences;
import org.python.pydev.core.interactive_console.IScriptConsoleViewer;
import org.python.pydev.core.log.Log;
import org.python.pydev.editor.codecompletion.CompletionError;
import org.python.pydev.editor.codecompletion.ConvertCompletionProposals;
import org.python.pydev.editor.codecompletion.PyContentAssistant;
import org.python.pydev.editor.codecompletion.PyContextInformationValidator;
import org.python.pydev.editor.codecompletion.PythonCompletionProcessor;
import org.python.pydev.shared_core.code_completion.ICompletionProposalHandle;
import org.python.pydev.shared_interactive_console.console.IScriptConsoleShell;
import org.python.pydev.shared_ui.content_assist.AbstractCompletionProcessorWithCycling;

/**
 * Gathers completions for the pydev console.
 *
 * @author fabioz
 */
public class PydevConsoleCompletionProcessor extends AbstractCompletionProcessorWithCycling implements
        ICompletionListener {

    /**
     * This is the class that manages the context information (validates it and
     * changes its presentation).
     */
    private PyContextInformationValidator contextInformationValidator;

    private IScriptConsoleShell interpreterShell;
    private String errorMessage = null;
    private int lastActivationCount = -1;

    public PydevConsoleCompletionProcessor(IScriptConsoleShell interpreterShell,
            PyContentAssistant pyContentAssistant) {
        super(pyContentAssistant);
        pyContentAssistant.addCompletionListener(this);
        this.interpreterShell = interpreterShell;

    }

    @Override
    public char[] getContextInformationAutoActivationCharacters() {
        return null;
    }

    @Override
    public char[] getCompletionProposalAutoActivationCharacters() {
        return PythonCompletionProcessor.getStaticCompletionProposalAutoActivationCharacters();
    }

    /**
     * Get the completions (and cycle the completion mode if needed).
     */
    @Override
    public ICompletionProposal[] computeCompletionProposals(ITextViewer v, int offset) {
        //cycle if we're in a new activation for requests (a second ctrl+space or
        //a new request)
        boolean cycleRequest;

        if (lastActivationCount == -1) {
            //new request: don't cycle
            lastActivationCount = this.contentAssistant.lastActivationCount;
            cycleRequest = false;
            updateStatus();
        } else {
            //we already had a request (so, we may cycle or not depending on the activation count)
            cycleRequest = this.contentAssistant.lastActivationCount != lastActivationCount;
        }

        if (cycleRequest) {
            lastActivationCount = this.contentAssistant.lastActivationCount;
            doCycle();
            updateStatus();
        }

        IScriptConsoleViewer viewer = (IScriptConsoleViewer) v;

        try {
            if (!PyCodeCompletionPreferences.useCodeCompletion()) {
                return new ICompletionProposal[0];
            }
            String commandLine = viewer.getCommandLine();
            int cursorPosition = offset - viewer.getCommandLineOffset();

            ICompletionProposalHandle[] completions = interpreterShell.getCompletions(viewer, commandLine,
                    cursorPosition, offset, this.whatToShow);
            return ConvertCompletionProposals.convertHandlesToProposals(completions);
        } catch (Exception e) {
            Log.log(e);
            CompletionError completionError = new CompletionError(e);
            this.errorMessage = completionError.getErrorMessage();
            //Make the error visible to the user!
            return new ICompletionProposal[] { completionError };
        }
    }

    @Override
    public IContextInformation[] computeContextInformation(ITextViewer v, int offset) {
        return null;
    }

    @Override
    public IContextInformationValidator getContextInformationValidator() {
        if (contextInformationValidator == null) {
            contextInformationValidator = new PyContextInformationValidator();
        }

        return contextInformationValidator;
    }

    /**
     * @return an error message that happened while getting the completions
     */
    @Override
    public String getErrorMessage() {
        String msg = errorMessage;
        errorMessage = null;
        return msg;
    }

    @Override
    public void assistSessionEnded(ContentAssistEvent event) {
    }

    @Override
    public void assistSessionStarted(ContentAssistEvent event) {
        this.lastActivationCount = -1;
        //we have to start with templates because it'll start already cycling.
        startCycle();
    }

    @Override
    public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {
    }
}

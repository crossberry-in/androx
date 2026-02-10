package com.androx.app.terminal;

import android.app.Service;

import androidx.annotation.NonNull;

import com.androx.app.TermuxService;
import com.androx.shared.termux.shell.command.runner.terminal.TermuxSession;
import com.androx.shared.termux.terminal.TermuxTerminalSessionClientBase;
import com.androx.terminal.TerminalSession;
import com.androx.terminal.TerminalSessionClient;

/** The {@link TerminalSessionClient} implementation that may require a {@link Service} for its interface methods. */
public class TermuxTerminalSessionServiceClient extends TermuxTerminalSessionClientBase {

    private static final String LOG_TAG = "TermuxTerminalSessionServiceClient";

    private final TermuxService mService;

    public TermuxTerminalSessionServiceClient(TermuxService service) {
        this.mService = service;
    }

    @Override
    public void setTerminalShellPid(@NonNull TerminalSession terminalSession, int pid) {
        TermuxSession termuxSession = mService.getTermuxSessionForTerminalSession(terminalSession);
        if (termuxSession != null)
            termuxSession.getExecutionCommand().mPid = pid;
    }

}

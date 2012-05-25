package com.autocontext.actions;

import com.autocontext.ReactionKind;

public class DisableMicrophoneAction extends BaseNotifyAction {

    public DisableMicrophoneAction() {
        super("Microphone is disabled.", "Disable microphone.");
    }
    @Override
    public ReactionKind getType() {
        return ReactionKind.REACTION_DISABLE_MICROPHONE;
    }
}

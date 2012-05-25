package com.autocontext.actions;

import com.autocontext.*;

public class DisableGPSAction extends BaseNotifyAction {

    public DisableGPSAction() {
        super("GPS is disabled.", "Disable GPS.");
    }
    @Override
    public ReactionKind getType() {
        return ReactionKind.REACTION_DISABLE_GPS;
    }
}

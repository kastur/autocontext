package com.autocontext.actions;

import com.autocontext.*;

public class DisableAccelerometerAction extends BaseNotifyAction {

    public DisableAccelerometerAction() {
        super("Accelerometer is disabled.", "Disable Accelerometer.");
    }
    @Override
    public ReactionKind getType() {
        return ReactionKind.REACTION_DISABLE_ACCELEROMETER;
    }
}

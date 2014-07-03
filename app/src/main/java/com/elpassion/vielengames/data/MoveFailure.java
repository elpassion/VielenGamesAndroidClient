package com.elpassion.vielengames.data;

import com.google.gson.annotations.SerializedName;

import lombok.Value;

@Value
public final class MoveFailure {

    public enum Reason {

        @SerializedName("illegal_move")
        ILLEGAL_MOVE,
        @SerializedName("not_your_turn")
        NOT_YOUR_TURN
    }

    Reason error;
}
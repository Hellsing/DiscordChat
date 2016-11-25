package com.hellzing.discordchat.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public abstract class ToggleableBase
{
    @Getter
    @Expose
    @SerializedName("Enabled")
    protected boolean enabled = true;
}

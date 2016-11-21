package com.hellzing.discordchat.commands;

public interface ICommand
{
    String[] getNames();

    void doCommand(String channel, String[] args);
}

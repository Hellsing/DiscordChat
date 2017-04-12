package com.hellzing.discordchat.commands;

import com.hellzing.discordchat.data.Users;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;

public class LinkUser implements ICommand
{
    @Getter
    private final ChannelType channelType = ChannelType.PRIVATE;
    @Getter
    private final PermissionType permissionType = PermissionType.OWNER;
    @Getter
    private final String[] commandAliases = new String[] { "link" };
    @Getter
    private final String description = "Links a Minecraft user to a Discord ID";

    @Override
    public boolean execute(Member sender, MessageChannel channel, String[] args)
    {
        if (args.length != 3)
        {
            // Tell him he's dumb
            channel.sendMessage("Invalid args length! Expected: 2, actual: " + args.length).queue();
        }
        else
        {
            val minecraftUsername = args[1];
            val discordId = args[2];

            val existed = Users.getInstance().getLinkedUsers().containsKey(minecraftUsername);
            Users.getInstance().getLinkedUsers().put(minecraftUsername, discordId);

            channel.sendMessage("Successfully " + (existed ? "updated " : "added ") + minecraftUsername + "!").queue();

            Users.saveConfig();
        }

        return true;
    }
}

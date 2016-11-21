package com.hellzing.discordchat;

import com.google.common.base.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.*;

public class DCPrivateProps
{
    private static File privateProperties;

    public static boolean setup = false;

    public static void save()
    {
        try
        {
            privateProperties.createNewFile();
            try (Writer out = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(privateProperties)), Charsets.UTF_8))
            {
                out.write("setup=" + Boolean.toString(setup));
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void init(File configDir)
    {
        try
        {
            privateProperties = new File(configDir, "shadowfacts/DiscordChat.private");
            if (!privateProperties.exists())
            {
                save();
            }
            else
            {
                String line = IOUtils.readLines(new FileInputStream(privateProperties)).get(0);
                String[] bits = line.split("=");
                if ("setup".equals(bits[0]))
                {
                    setup = Boolean.parseBoolean(bits[1]);
                }
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}

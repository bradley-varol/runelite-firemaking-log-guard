package com.firemakinglogguard;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class FiremakingLogGuardPluginTest
{
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(FiremakingLogGuardPlugin.class);
        RuneLite.main(args);
    }
}

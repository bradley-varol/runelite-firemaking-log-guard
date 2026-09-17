package com.firemakinglogguard;

import javax.inject.Inject;
import net.runelite.api.AnimationID;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
    name = "Firemaking Log Guard",
    description = "Highlights and protects inventory logs while lighting a fire, preventing accidentally dropping logs",
    tags = {"firemaking", "logs", "inventory"}
)
public class FiremakingLogGuardPlugin extends Plugin
{
    @Inject private Client client;
    @Inject private ItemManager itemManager;
    @Inject private OverlayManager overlayManager;
    private FiremakingLogGuardOverlay overlay;
    private boolean guarding;
    private WorldPoint fireTile;
    private LocalPoint fireLocalPoint;
    private int ticksAfterAnimation;

    @Override
    protected void startUp()
    {
        overlay = new FiremakingLogGuardOverlay(this);
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        clearGuard();
    }

    boolean isLightingFire()
    {
        Player player = client.getLocalPlayer();
        return player != null && (guarding || player.getAnimation() == AnimationID.FIREMAKING);
    }

    private void startGuard(Player player)
    {
        guarding = true;
        fireTile = player.getWorldLocation();
        fireLocalPoint = player.getLocalLocation();
        ticksAfterAnimation = 0;
    }

    private void clearGuard()
    {
        guarding = false;
        fireTile = null;
        fireLocalPoint = null;
        ticksAfterAnimation = 0;
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event)
    {
        Player player = client.getLocalPlayer();
        if (player != null && event.getActor() == player && player.getAnimation() == AnimationID.FIREMAKING)
        {
            startGuard(player);
        }
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        Player player = client.getLocalPlayer();
        if (player == null)
        {
            clearGuard();
            return;
        }

        if (player.getAnimation() == AnimationID.FIREMAKING)
        {
            if (!guarding)
            {
                startGuard(player);
            }
            ticksAfterAnimation = 0;
            return;
        }

        if (!guarding)
        {
            return;
        }

        ticksAfterAnimation++;
        // A failed or interrupted attempt may not include a step.
        if (fireTile != null && fireTile.equals(player.getWorldLocation()) && ticksAfterAnimation >= 2)
        {
            clearGuard();
        }
    }

    @Subscribe
    public void onClientTick(ClientTick event)
    {
        if (!guarding || fireLocalPoint == null)
        {
            return;
        }

        Player player = client.getLocalPlayer();
        if (player == null || player.getAnimation() == AnimationID.FIREMAKING
            || fireTile == null || fireTile.equals(player.getWorldLocation()))
        {
            return;
        }

        LocalPoint current = player.getLocalLocation();
        if (current != null && current.getWorldView() == fireLocalPoint.getWorldView()
            && (Math.abs(current.getX() - fireLocalPoint.getX()) >= 120
                || Math.abs(current.getY() - fireLocalPoint.getY()) >= 120))
        {
            clearGuard();
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() != GameState.LOGGED_IN)
        {
            clearGuard();
        }
    }

    boolean isLog(int itemId)
    {
        if (itemId <= 0)
        {
            return false;
        }

        String name = itemManager.getItemComposition(itemId).getName();
        return name.equalsIgnoreCase("Logs") || name.toLowerCase(java.util.Locale.ROOT).endsWith(" logs");
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        if (!isLightingFire())
        {
            return;
        }

        Widget widget = event.getWidget();
        if (widget == null || widget.getId() >>> 16 != InterfaceID.INVENTORY)
        {
            return;
        }

        // Param0 is the inventory slot for inventory widget actions, including
        // Use and Drop. Read the live slot so we never protect an unrelated item.
        ItemContainer inventory = client.getItemContainer(InventoryID.INV);
        Item item = inventory == null ? null : inventory.getItem(event.getParam0());
        if (item != null && isLog(item.getId()) && !event.getMenuOption().equalsIgnoreCase("Examine"))
        {
            event.consume();
        }
    }
}

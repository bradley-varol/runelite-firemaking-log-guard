package com.firemakinglogguard;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

public class FiremakingLogGuardOverlay extends WidgetItemOverlay
{
    private static final Color RED = new Color(255, 40, 40);
    private final FiremakingLogGuardPlugin plugin;

    FiremakingLogGuardOverlay(FiremakingLogGuardPlugin plugin)
    {
        this.plugin = plugin;
        showOnInterfaces(InterfaceID.INVENTORY);
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
    {
        if (!plugin.isLightingFire() || !plugin.isLog(itemId))
        {
            return;
        }

        Rectangle bounds = widgetItem.getCanvasBounds();
        if (bounds == null)
        {
            return;
        }

        Color oldColor = graphics.getColor();
        Stroke oldStroke = graphics.getStroke();
        graphics.setColor(RED);
        graphics.setStroke(new BasicStroke(2));
        graphics.drawRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
        graphics.setStroke(oldStroke);
        graphics.setColor(oldColor);
    }
}

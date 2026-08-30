package redstonedisorder.mendingtracker;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static redstonedisorder.mendingtracker.ConfigHelper.configOptions;
import static redstonedisorder.mendingtracker.MendingTracker.*;

public class ConfigScreen extends Screen {

    Screen parent;
    protected ConfigScreen(Screen parent) {
        super(Component.literal("Mending Tracker config screen"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        // TODO: Figure out values to actually make the screen look good

        AbstractSliderButton xPosSlider = new AbstractSliderButton(20, 20, 150, 20, Component.literal("GUI element X position: " + configOptions.xPos), (double) configOptions.xPos / 100) {
            @Override
            protected void updateMessage() {
                this.setMessage(Component.literal("GUI element X position: " + Math.round(value * 100)));
            }

            @Override
            protected void applyValue() {
                effectiveXpos = Math.toIntExact(Math.round(value * 100));
            }
        };

        AbstractSliderButton yPosSlider = new AbstractSliderButton(20, 40, 150, 20, Component.literal("GUI element Y position: " + configOptions.yPos), (double) configOptions.yPos / 100) {
            @Override
            protected void updateMessage() {
                this.setMessage(Component.literal("GUI element Y position: " + Math.round(value * 100)));
            }

            @Override
            protected void applyValue() {
                effectiveYpos = Math.toIntExact(Math.round(value * 100));
            }
        };

        addRenderableWidget(xPosSlider);
        addRenderableWidget(yPosSlider);

        Button alignmentButton = Button.builder(Component.literal("Text alignment: " + configOptions.textAlignment.name()), button -> {
            effectiveAlignment = ConfigOptions.Alignment.values()[(effectiveAlignment.ordinal() + 1) % 3];
            button.setMessage(Component.literal("Text alignment: " + effectiveAlignment.name()));
        }).bounds(400, 50, 200, 20).build();

        addRenderableWidget(alignmentButton);

        Button overrideButton = Button.builder(Component.literal("Override MiniHUD: " + configOptions.overrideMinihud), button -> {
            effectiveMinihudOverride = !effectiveMinihudOverride;
            button.setMessage(Component.literal("Override MiniHUD: " + effectiveMinihudOverride));
        }).bounds(400, 100, 200, 20).build();

        if (!FabricLoader.getInstance().isModLoaded("minihud")) {
            overrideButton.active = false;
            overrideButton.setTooltip(Tooltip.create(Component.literal("MiniHUD is not installed, so this option will do nothing.")));
        }

        addRenderableWidget(overrideButton);

        Button saveButton = Button.builder(Component.literal("Save and exit"), button -> {
            configOptions.xPos = effectiveXpos;
            configOptions.yPos = effectiveYpos;
            configOptions.textAlignment = effectiveAlignment;
            configOptions.overrideMinihud = effectiveMinihudOverride;
            boolean success = ConfigHelper.saveConfig(configOptions);
            if (!success) {
                minecraft.getToastManager().addToast(new SystemToast(SystemToast.SystemToastId.PERIODIC_NOTIFICATION, Component.literal("Config error"), Component.literal("The config could not be saved.")));
            }
        }).bounds(100, 100, 200, 20).build();

        addRenderableWidget(saveButton);
    }

    @Override
    public void onClose() {
        effectiveXpos = configOptions.xPos;
        effectiveYpos = configOptions.yPos;
        effectiveAlignment = configOptions.textAlignment;
        effectiveMinihudOverride = configOptions.overrideMinihud;
        Minecraft.getInstance().setScreen(parent);
    }
}

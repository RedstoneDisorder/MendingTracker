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
        AbstractSliderButton xPosSlider = new AbstractSliderButton(width / 8, height / 8, 11 * width / 32, Button.DEFAULT_HEIGHT, Component.literal("GUI element X position: " + configOptions.xPos), (double) configOptions.xPos / 100) {
            @Override
            protected void updateMessage() {
                this.setMessage(Component.literal("GUI element X position: " + Math.round(value * 100)));
            }

            @Override
            protected void applyValue() {
                effectiveXpos = Math.toIntExact(Math.round(value * 100));
            }
        };

        AbstractSliderButton yPosSlider = new AbstractSliderButton(width / 8, height / 4, 11 * width / 32, Button.DEFAULT_HEIGHT, Component.literal("GUI element Y position: " + configOptions.yPos), (double) configOptions.yPos / 100) {
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
        }).bounds(17 * width / 32, height / 8, 11 * width / 32, Button.DEFAULT_HEIGHT).build();

        addRenderableWidget(alignmentButton);

        Button overrideButton = Button.builder(Component.literal("Override MiniHUD: " + configOptions.overrideMinihud), button -> {
            effectiveMinihudOverride = !effectiveMinihudOverride;
            button.setMessage(Component.literal("Override MiniHUD: " + effectiveMinihudOverride));
        }).bounds(17 * width / 32, height / 4, 11 * width / 32, Button.DEFAULT_HEIGHT).build();

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
            onClose();
        }).bounds(5 * width / 16, 3 * height / 8, 3 * width / 8, 20).build();

        addRenderableWidget(saveButton);

        Button discardButton = Button.builder(Component.literal("Exit without saving"), button -> onClose()).bounds(5 * width / 16, 15 * height / 32, 3 * width / 8, 20).build();

        addRenderableWidget(discardButton);
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

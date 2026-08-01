package redstonedisorder.mendingtracker.mixin;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.minihud.Reference;
import fi.dy.masa.minihud.config.InfoToggle;
import fi.dy.masa.minihud.event.RenderHandler;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static redstonedisorder.mendingtracker.MendingTracker.damagedItems;
import static redstonedisorder.mendingtracker.MendingTracker.durabilityNeeded;

@Pseudo
@Mixin(value = RenderHandler.class, remap = false)
public abstract class MiniHudLineRenderer {
    // Also stolen from Mikarific's EID Tracker mod, because
    // I still don't know how to add my own lines to MiniHUD
    @Shadow public abstract void addLine(String text);

    @Inject(method = "addLine(Lfi/dy/masa/minihud/config/InfoToggle;)V", at = @At("TAIL"))
	private void addLine(InfoToggle type, CallbackInfo ci) {
		if (type.name().equals("MENDING_INFO")) this.addLine(StringUtils.translate(Reference.MOD_ID + ".info_line.mending_info", GuiBase.TXT_GREEN, damagedItems.intValue(), GuiBase.TXT_RST, GuiBase.TXT_GREEN, (durabilityNeeded.intValue() + 1) / 2));
	}
}
package redstonedisorder.mendingtracker.mixin;

import fi.dy.masa.minihud.config.InfoToggle;
import fi.dy.masa.minihud.info.InfoLineType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(value = InfoToggle.class, remap = false)
public class MiniHudInfoLine {
	// Stolen from Mikarific's EID Tracker mod, because
	// I don't know how to add my own lines to MiniHUD
	@Shadow @Final @Mutable private static InfoToggle[] $VALUES;

	@Invoker("<init>")
	static InfoToggle invokeInit(String enumName, int enumOrdinal, String name, InfoLineType<?> type, boolean defaultValue, String defaultHotkey) {
		throw new AssertionError();
	}

	@Inject(method = "<clinit>", at = @At(value = "FIELD", target = "Lfi/dy/masa/minihud/config/InfoToggle;$VALUES:[Lfi/dy/masa/minihud/config/InfoToggle;", shift = At.Shift.AFTER))
	private static void addCustomInfo(CallbackInfo ci) {
		List<InfoToggle> infoToggles = new ArrayList<>(Arrays.asList($VALUES));

		infoToggles.add(invokeInit(
				"MENDING_INFO",
				$VALUES.length,
				"infoMending",
				null,
				false,
				""
		));

		$VALUES = infoToggles.toArray(new InfoToggle[0]);
	}
}
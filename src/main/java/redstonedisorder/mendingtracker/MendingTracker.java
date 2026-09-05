package redstonedisorder.mendingtracker;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.debug.DebugEntryCategory;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static redstonedisorder.mendingtracker.ConfigHelper.configOptions;

public class MendingTracker implements ClientModInitializer {
	public static final String MOD_ID = "mendingtracker";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static AtomicInteger damagedItems = new AtomicInteger();
	public static AtomicInteger durabilityNeeded = new AtomicInteger();

	boolean isMinihudLoaded = FabricLoader.getInstance().isModLoaded("minihud");

	public static int effectiveXpos;
	public static int effectiveYpos;
	public static ConfigOptions.Alignment effectiveAlignment = configOptions.textAlignment;
	public static boolean effectiveMinihudOverride = configOptions.overrideMinihud;

	@Override
	public void onInitializeClient() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ConfigHelper.getConfigOptions();

		effectiveXpos = configOptions.xPos;
		effectiveYpos = configOptions.yPos;
		effectiveAlignment = configOptions.textAlignment;
		effectiveMinihudOverride = configOptions.overrideMinihud;

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null) {
                Inventory inventory = client.player.getInventory();
				damagedItems.set(0);
				durabilityNeeded.set(0);

				ArrayList<ItemStack> itemStacks = new ArrayList<>(List.of(inventory.getItem(36), inventory.getItem(37), inventory.getItem(38), inventory.getItem(39), inventory.getItem(40), inventory.getSelectedItem()));
				itemStacks.forEach(itemStack -> {
					if (itemStack.getDamageValue() > 0 && EnchantmentHelper.getItemEnchantmentLevel(client.player.registryAccess().getOrThrow(Enchantments.MENDING), itemStack) > 0) {
						damagedItems.incrementAndGet();
						durabilityNeeded.addAndGet(itemStack.getDamageValue());
					}
				});
			}
		});

		Minecraft client = Minecraft.getInstance();

		HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS,Identifier.fromNamespaceAndPath(MOD_ID, "mending_overlay"), (graphics, tickCounter) -> {

			Component message = Component.literal("Equipment pieces to mend: ").append(Component.literal(String.valueOf(damagedItems)).withStyle(ChatFormatting.GREEN)).append(Component.literal(", XP required: ")).append(Component.literal(String.valueOf((durabilityNeeded.intValue() + 1) / 2)).withStyle(ChatFormatting.GREEN));
			Font font = client.font;

			// HORRENDOUS IF STATEMENT INCOMING, PROGRAMMERS PLEASE CLOSE YOUR EYES!!!

			if (!client.debugEntries.isOverlayVisible() && DebugScreenEntries.allEntries().entrySet().stream().noneMatch(entry -> entry.getValue().category().equals(DebugEntryCategory.SCREEN_TEXT) && client.debugEntries.getStatus(entry.getKey()).equals(DebugScreenEntryStatus.ALWAYS_ON))) {
				int messageWidth = font.width(message);

				int baseXpos = effectiveXpos * client.getWindow().getGuiScaledWidth() / 100;
				int baseYpos = effectiveYpos * client.getWindow().getGuiScaledHeight() / 100;

				graphics.fill(
						effectiveAlignment == ConfigOptions.Alignment.Left ? baseXpos :
								effectiveAlignment == ConfigOptions.Alignment.Center ? baseXpos - messageWidth / 2 - 1 :
										baseXpos - messageWidth - 3,
						baseYpos,
						effectiveAlignment == ConfigOptions.Alignment.Left ? baseXpos + messageWidth + 3 :
								effectiveAlignment == ConfigOptions.Alignment.Center ? baseXpos + messageWidth / 2 + 2 :
										baseXpos,
						baseYpos + font.lineHeight + 3,
						!isMinihudLoaded || effectiveMinihudOverride ? 0x60000000 : 0x00000000
				);

				graphics.text(
						font,
						message,
						effectiveAlignment == ConfigOptions.Alignment.Left ? baseXpos + 2 :
								effectiveAlignment == ConfigOptions.Alignment.Center ? baseXpos - messageWidth / 2 + 1 :
										baseXpos - messageWidth - 1,
						baseYpos + 2,
						!isMinihudLoaded || effectiveMinihudOverride ? 0xFFFFFFFF : 0x00000000
				);
			}
		});

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommands.literal("mendingTracker").executes(context -> {
			client.execute(() -> client.setScreen(new ConfigScreen(null)));
			return 0;
		})));

		LOGGER.info("Mending Tracker initialized!");
	}
}

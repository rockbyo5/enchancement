package moriyashiine.enchancement.common;

import com.google.gson.Gson;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import moriyashiine.enchancement.common.component.entity.DashComponent;
import moriyashiine.enchancement.common.event.*;
import moriyashiine.enchancement.common.packet.SyncFrozenPlayerSlimStatusC2S;
import moriyashiine.enchancement.common.packet.SyncJumpingPacket;
import moriyashiine.enchancement.common.packet.SyncMovingForwardPacket;
import moriyashiine.enchancement.common.registry.ModEnchantments;
import moriyashiine.enchancement.common.registry.ModEntityTypes;
import moriyashiine.enchancement.common.registry.ModSoundEvents;
import moriyashiine.enchancement.common.reloadlisteners.BeheadingReloadListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class Enchancement implements ModInitializer {
	public static final String MOD_ID = "enchancement";

	private static ConfigHolder<ModConfig> config;

	@Override
	public void onInitialize() {
		ServerPlayNetworking.registerGlobalReceiver(SyncFrozenPlayerSlimStatusC2S.ID, SyncFrozenPlayerSlimStatusC2S::receive);
		ServerPlayNetworking.registerGlobalReceiver(SyncMovingForwardPacket.ID, SyncMovingForwardPacket::receive);
		ServerPlayNetworking.registerGlobalReceiver(SyncJumpingPacket.ID, SyncJumpingPacket::receive);
		ModEntityTypes.init();
		ModEnchantments.init();
		ModSoundEvents.init();
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new BeheadingReloadListener(new Gson(), MOD_ID + "_beheading"));
		initEvents();
	}

	public static ModConfig getConfig() {
		if (config == null) {
			AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
			config = AutoConfig.getConfigHolder(ModConfig.class);
		}
		ModConfig modConfig = config.getConfig();
		if (modConfig.allowedEnchantmentIdentifiers == null) {
			modConfig.allowedEnchantmentIdentifiers = modConfig.allowedEnchantments.stream().map(Identifier::new).toList();
		}
		return modConfig;
	}

	private void initEvents() {
		ServerTickEvents.END_SERVER_TICK.register(new AssimilationEvent());
		ServerTickEvents.END_SERVER_TICK.register(new BuffetEvent());
		ServerTickEvents.END_SERVER_TICK.register(server -> DashComponent.tickPacketImmunities());
		UseBlockCallback.EVENT.register(new FireAspectEvent());
		PlayerBlockBreakEvents.BEFORE.register(new ExtractingEvent());
		PlayerBlockBreakEvents.BEFORE.register(new LumberjackEvent());
		UseEntityCallback.EVENT.register(new BuryEvent());
	}
}

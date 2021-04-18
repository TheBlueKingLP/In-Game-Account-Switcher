package ru.vidtu.iasfork.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
	@Accessor("session")
	public void setSession(Session s);
}

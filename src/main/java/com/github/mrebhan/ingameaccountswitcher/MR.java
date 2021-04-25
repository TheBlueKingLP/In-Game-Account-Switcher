package com.github.mrebhan.ingameaccountswitcher;

import com.github.mrebhan.ingameaccountswitcher.tools.Config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import ru.vidtu.iasfork.mixins.MinecraftClientAccessor;

/**
 * @author MRebhan
 */
public class MR {
	public static void init(){
		Config.load();
	}
	public static void setSession(Session s) throws Exception {
		((MinecraftClientAccessor)MinecraftClient.getInstance()).setSession(s);
	}
}
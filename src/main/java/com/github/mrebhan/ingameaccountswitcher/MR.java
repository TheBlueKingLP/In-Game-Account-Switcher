package com.github.mrebhan.ingameaccountswitcher;

import java.lang.reflect.Field;

import com.github.mrebhan.ingameaccountswitcher.tools.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

/**
 * @author MRebhan
 */
public class MR {
	public static void init(){
		Config.load();
	}
	public static void setSession(Session s) throws Exception {
		for (Field f : Minecraft.class.getDeclaredFields()) {
			if (f.getType().equals(Session.class)) {
				f.setAccessible(true);
				f.set(Minecraft.getInstance(), s);
			}
		}
	}
}

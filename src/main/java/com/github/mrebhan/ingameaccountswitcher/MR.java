package com.github.mrebhan.ingameaccountswitcher;

import com.github.mrebhan.ingameaccountswitcher.tools.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import ru.vidtu.iasfork.msauth.MicrosoftAccount;

/**
 * @author MRebhan
 */
public class MR {
	public static void init(){
		Config.load();
		MicrosoftAccount.load(Minecraft.getInstance());
	}
	public static void setSession(User s) {
		Minecraft.getInstance().user = s;
	}
}

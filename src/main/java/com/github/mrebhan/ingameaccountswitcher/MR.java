package com.github.mrebhan.ingameaccountswitcher;

import com.github.mrebhan.ingameaccountswitcher.tools.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import ru.vidtu.iasfork.msauth.MicrosoftAccount;

/**
 * @author MRebhan
 */
public class MR {
	public static void init(){
		Config.load();
		MicrosoftAccount.load(Minecraft.getMinecraft());
	}
	public static void setSession(Session s) {
		Minecraft.getMinecraft().session = s;
	}
}

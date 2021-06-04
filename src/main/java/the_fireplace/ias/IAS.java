package the_fireplace.ias;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

import com.github.mrebhan.ingameaccountswitcher.MR;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import the_fireplace.ias.config.ConfigValues;
import the_fireplace.ias.events.ClientEvents;
import the_fireplace.ias.tools.SkinTools;
import the_fireplace.iasencrypt.Standards;
/**
 * @author The_Fireplace
 */
@Mod(modid="ias", name="In-Game Account Switcher", guiFactory="the_fireplace.ias.config.IASGuiFactory", acceptedMinecraftVersions = "1.7.10")
public class IAS {
	public static Properties config = new Properties();
	public static void syncConfig(boolean save){
		config.setProperty(ConfigValues.CASESENSITIVE_NAME, String.valueOf(ConfigValues.CASESENSITIVE));
		config.setProperty(ConfigValues.ENABLERELOG_NAME, String.valueOf(ConfigValues.ENABLERELOG));
		config.setProperty(ConfigValues.TEXT_POS_NAME + ".x", ConfigValues.TEXT_X);
		config.setProperty(ConfigValues.TEXT_POS_NAME + ".y", ConfigValues.TEXT_Y);
		config.setProperty(ConfigValues.SHOW_ON_MULTIPLAYER_SCREEN_NAME, String.valueOf(ConfigValues.SHOW_ON_MULTIPLAYER_SCREEN));
		if (save) {
			try {
				Minecraft mc = Minecraft.getMinecraft();
				File f = new File(mc.mcDataDir, "config/ias.properties");
				f.getParentFile().mkdirs();
				FileWriter fw = new FileWriter(f);
				config.store(fw, "IAS config");
				fw.close();
			} catch (Throwable t) {
				System.err.println("Unable to save IAS config");
				t.printStackTrace();
			}
		}
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		try {
			Minecraft mc = Minecraft.getMinecraft();
			File f = new File(mc.mcDataDir, "config/ias.properties");
			if (f.exists()) {
				FileReader fr = new FileReader(f);
				config.load(fr);
				fr.close();
			}
			ConfigValues.CASESENSITIVE = Boolean.parseBoolean(config.getProperty(ConfigValues.CASESENSITIVE_NAME, String.valueOf(ConfigValues.CASESENSITIVE_DEFAULT)));
			ConfigValues.ENABLERELOG = Boolean.parseBoolean(config.getProperty(ConfigValues.ENABLERELOG_NAME, String.valueOf(ConfigValues.ENABLERELOG_DEFAULT)));
			ConfigValues.TEXT_X = config.getProperty(ConfigValues.TEXT_POS_NAME + ".x", "");
			ConfigValues.TEXT_Y = config.getProperty(ConfigValues.TEXT_POS_NAME + ".y", "");
			ConfigValues.SHOW_ON_MULTIPLAYER_SCREEN = Boolean.parseBoolean(config.getProperty(ConfigValues.SHOW_ON_MULTIPLAYER_SCREEN_NAME, String.valueOf(ConfigValues.SHOW_ON_MULTIPLAYER_SCREEN_DEFAULT)));
		} catch (Throwable t) {
			System.err.println("Unable to load IAS config");
			t.printStackTrace();
		}
		syncConfig(false);
		try {
			Class.forName("net.minecraft.util.MathHelper");
		} catch (Throwable t) {
			Standards.updateFolder();
		}
	}
	@EventHandler
	public void init(FMLInitializationEvent event){
		MR.init();
		MinecraftForge.EVENT_BUS.register(new ClientEvents());
		Standards.importAccounts();
	}
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		SkinTools.cacheSkins(false);
	}
}

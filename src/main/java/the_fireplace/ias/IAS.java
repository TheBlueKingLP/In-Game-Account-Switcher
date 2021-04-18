package the_fireplace.ias;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

import com.github.mrebhan.ingameaccountswitcher.MR;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import the_fireplace.ias.config.ConfigValues;
import the_fireplace.iasencrypt.Standards;
/**
 * @author The_Fireplace
 */
public class IAS implements ClientModInitializer {
	public static Properties config = new Properties();
	public static boolean CASESENSITIVE_PROPERTY;
	public static boolean ENABLERELOG_PROPERTY;
	public static void syncConfig(boolean save) {
		ConfigValues.CASESENSITIVE = CASESENSITIVE_PROPERTY;
		ConfigValues.ENABLERELOG = ENABLERELOG_PROPERTY;
		config.setProperty(ConfigValues.CASESENSITIVE_NAME, String.valueOf(CASESENSITIVE_PROPERTY));
		config.setProperty(ConfigValues.ENABLERELOG_NAME, String.valueOf(ENABLERELOG_PROPERTY));
		if (save) {
			try {
				MinecraftClient mc = MinecraftClient.getInstance();
				File f = new File(mc.runDirectory, "config/ias.properties");
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
	
	public void onInitializeClient() {
		try {
			MinecraftClient mc = MinecraftClient.getInstance();
			File f = new File(mc.runDirectory, "config/ias.properties");
			if (f.exists()) {
				FileReader fr = new FileReader(f);
				config.load(fr);
				fr.close();
			}
			CASESENSITIVE_PROPERTY = Boolean.parseBoolean(config.getProperty(ConfigValues.CASESENSITIVE_NAME, String.valueOf(ConfigValues.CASESENSITIVE_DEFAULT)));
			ENABLERELOG_PROPERTY = Boolean.parseBoolean(config.getProperty(ConfigValues.ENABLERELOG_NAME, String.valueOf(ConfigValues.ENABLERELOG_DEFAULT)));
		} catch (Throwable t) {
			System.err.println("Unable to load IAS config");
			t.printStackTrace();
		}
		syncConfig(false);
		try {
			Class.forName("net.minecraft.util.math.MathHelper");
		} catch (Throwable t) {
			Standards.updateFolder();
		}
		MR.init();
		Standards.importAccounts();
	}
}

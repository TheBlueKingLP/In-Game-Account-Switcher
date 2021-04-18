package the_fireplace.ias;

import com.github.mrebhan.ingameaccountswitcher.MR;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import ru.vidtu.iasfork.IASConfigScreen;
import the_fireplace.ias.config.ConfigValues;
import the_fireplace.ias.events.ClientEvents;
import the_fireplace.ias.tools.SkinTools;
import the_fireplace.iasencrypt.Standards;
/**
 * @author The_Fireplace
 */
@Mod("ias")
public class IAS {
	public static ForgeConfigSpec config;
	public static ForgeConfigSpec.BooleanValue CASESENSITIVE_PROPERTY;
	public static ForgeConfigSpec.BooleanValue ENABLERELOG_PROPERTY;
	public static void syncConfig(boolean save) {
		ConfigValues.CASESENSITIVE = CASESENSITIVE_PROPERTY.get();
		ConfigValues.ENABLERELOG = ENABLERELOG_PROPERTY.get();
		if (save) config.save();
	}
	
	public IAS() {
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, s) -> new IASConfigScreen(s));
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		CASESENSITIVE_PROPERTY = builder.comment("Should account searches be case sensitive?").define(ConfigValues.CASESENSITIVE_NAME, ConfigValues.CASESENSITIVE_DEFAULT);
		ENABLERELOG_PROPERTY = builder.comment("Enables logging in to the account you are already logged in to.").define(ConfigValues.ENABLERELOG_NAME, ConfigValues.ENABLERELOG_DEFAULT);
		config = builder.build();
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, config);
		syncConfig(false);
		try {
			Class.forName("net.minecraft.util.math.MathHelper");
		} catch (Throwable t) {
			Standards.updateFolder();
		}
		MR.init();
		MinecraftForge.EVENT_BUS.register(new ClientEvents());
		Standards.importAccounts();
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientComplete);
	}
	
	public void onClientComplete(FMLLoadCompleteEvent event) {
		SkinTools.cacheSkins(false);
	}
}

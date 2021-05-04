package the_fireplace.ias.config;

import java.util.HashSet;
import java.util.Set;

import cpw.mods.fml.client.IModGuiFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import ru.vidtu.iasfork.IASConfigScreen;
/**
 *	This is the Gui Factory
 * @author The_Fireplace
 *
 */
public class IASGuiFactory implements IModGuiFactory {
	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement arg0) {
		return null;
	}

	@Override
	public void initialize(Minecraft arg0) {}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return IASConfigScreen.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return new HashSet<>();
	}
}

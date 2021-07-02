package the_fireplace.ias.gui;

import java.util.function.BiFunction;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiPasswordField extends GuiTextField
{
	public GuiPasswordField(int i, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height) {
		super(i, fontrendererObj, x, y, par5Width, par6Height);
		setTextFormatter(new BiFunction<String, Integer, String>() {
			public String apply(String t, Integer u) {
				return StringUtils.repeat('*', t.length());
			}
		});
	}
	
	@Override
	public boolean keyPressed(int key, int oldkey, int mods) {
		return !GuiScreen.isKeyComboCtrlC(key) && !GuiScreen.isKeyComboCtrlX(key) && super.keyPressed(key, oldkey, mods);
	}
}

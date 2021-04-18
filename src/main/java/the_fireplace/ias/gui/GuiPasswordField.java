package the_fireplace.ias.gui;

import java.util.function.BiFunction;

import joptsimple.internal.Strings;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class GuiPasswordField extends TextFieldWidget
{
	public GuiPasswordField(FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height, String s)
	{
		super(fontrendererObj, x, y, par5Width, par6Height, s);
		setTextFormatter(new BiFunction<String, Integer, String>() {
			public String apply(String t, Integer u) {
				return Strings.repeat('*', t.length());
			}
		});
	}
	
	@Override
	public boolean keyPressed(int key, int oldkey, int mods) {
		return !Screen.isCopy(key) && !Screen.isCut(key) && super.keyPressed(key, oldkey, mods);
	}
}

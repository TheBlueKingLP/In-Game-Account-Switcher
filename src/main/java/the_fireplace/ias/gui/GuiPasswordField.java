package the_fireplace.ias.gui;

import java.util.function.BiFunction;

import joptsimple.internal.Strings;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class GuiPasswordField extends TextFieldWidget
{
	public GuiPasswordField(FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height, ITextComponent s)
	{
		super(fontrendererObj, x, y, par5Width, par6Height, s);
		setFormatter(new BiFunction<String, Integer, IReorderingProcessor>() {
			public IReorderingProcessor apply(String t, Integer u) {
				return new StringTextComponent(Strings.repeat('*', t.length())).getVisualOrderText();
			}
		});
	}
	
	@Override
	public boolean keyPressed(int key, int oldkey, int mods) {
		return !Screen.isCopy(key) && !Screen.isCut(key) && super.keyPressed(key, oldkey, mods);
	}
}

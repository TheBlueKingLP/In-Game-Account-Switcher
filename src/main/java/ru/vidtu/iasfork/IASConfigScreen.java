package ru.vidtu.iasfork;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import the_fireplace.ias.IAS;
import the_fireplace.ias.config.ConfigValues;

public class IASConfigScreen extends Screen {
	public final Screen prev;
	public CheckboxButton caseS, relog;
	public IASConfigScreen(Screen prev) {
		super(new StringTextComponent("ias.toml"));
		this.prev = prev;
	}
	
	@Override
	protected void init() {
		addButton(caseS = new CheckboxButton(width / 2 - font.getStringWidth(I18n.format(ConfigValues.CASESENSITIVE_NAME)) / 2 - 24, 40, 20, 20, I18n.format(ConfigValues.CASESENSITIVE_NAME), ConfigValues.CASESENSITIVE));
		addButton(relog = new CheckboxButton(width / 2 - font.getStringWidth(I18n.format(ConfigValues.ENABLERELOG_NAME)) / 2 - 24, 60, 20, 20, I18n.format(ConfigValues.ENABLERELOG_NAME), ConfigValues.ENABLERELOG));
		addButton(new Button(width / 2 - 75, height - 24, 150, 20, I18n.format("gui.done"), btn -> {
			minecraft.displayGuiScreen(prev);
		}));
	}
	
	@Override
	public void removed() {
		IAS.CASESENSITIVE_PROPERTY.set(caseS.func_212942_a());
		IAS.ENABLERELOG_PROPERTY.set(relog.func_212942_a());
		IAS.syncConfig(true);
	}
	
	@Override
	public void render(int mx, int my, float delta) {
		renderBackground();
		drawCenteredString(font, this.title.getFormattedText(), width / 2, 10, -1);
		super.render(mx, my, delta);
	}
}

package ru.vidtu.iasfork;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import the_fireplace.ias.IAS;
import the_fireplace.ias.config.ConfigValues;

public class IASConfigScreen extends Screen {
	public final Screen prev;
	public CheckboxWidget caseS, relog;
	public IASConfigScreen(Screen prev) {
		super(new LiteralText("ias.properties"));
		this.prev = prev;
	}
	
	@Override
	protected void init() {
		addButton(caseS = new CheckboxWidget(width / 2 - font.getStringWidth(I18n.translate(ConfigValues.CASESENSITIVE_NAME)) / 2 - 24, 40, 20, 20, I18n.translate(ConfigValues.CASESENSITIVE_NAME), ConfigValues.CASESENSITIVE));
		addButton(relog = new CheckboxWidget(width / 2 - font.getStringWidth(I18n.translate(ConfigValues.ENABLERELOG_NAME)) / 2 - 24, 60, 20, 20, I18n.translate(ConfigValues.ENABLERELOG_NAME), ConfigValues.ENABLERELOG));
		addButton(new ButtonWidget(width / 2 - 75, height - 24, 150, 20, I18n.translate("gui.done"), btn -> {
			minecraft.openScreen(prev);
		}));
	}
	
	@Override
	public void removed() {
		IAS.CASESENSITIVE_PROPERTY = caseS.isChecked();
		IAS.ENABLERELOG_PROPERTY = relog.isChecked();
		IAS.syncConfig(true);
	}
	
	@Override
	public void render(int mx, int my, float delta) {
		renderBackground();
		drawCenteredString(font, this.title.asFormattedString(), width / 2, 10, -1);
		super.render(mx, my, delta);
	}
}

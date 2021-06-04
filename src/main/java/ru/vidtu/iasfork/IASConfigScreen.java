package ru.vidtu.iasfork;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import the_fireplace.ias.IAS;
import the_fireplace.ias.config.ConfigValues;

public class IASConfigScreen extends Screen {
	public final Screen prev;
	public CheckboxWidget caseS, relog, mpscreen;
	public TextFieldWidget textX, textY;
	public IASConfigScreen(Screen prev) {
		super(new LiteralText("ias.properties"));
		this.prev = prev;
	}
	
	@Override
	protected void init() {
		addButton(caseS = new CheckboxWidget(width / 2 - font.getStringWidth(I18n.translate(ConfigValues.CASESENSITIVE_NAME)) / 2 - 24, 40, 20, 20, I18n.translate(ConfigValues.CASESENSITIVE_NAME), ConfigValues.CASESENSITIVE));
		addButton(relog = new CheckboxWidget(width / 2 - font.getStringWidth(I18n.translate(ConfigValues.ENABLERELOG_NAME)) / 2 - 24, 60, 20, 20, I18n.translate(ConfigValues.ENABLERELOG_NAME), ConfigValues.ENABLERELOG));
		addButton(textX = new TextFieldWidget(font, width / 2 - 100, 90, 98, 20, "X"));
		addButton(textY = new TextFieldWidget(font, width / 2 + 2, 90, 98, 20, "Y"));
		addButton(mpscreen = new CheckboxWidget(width / 2 - font.getStringWidth(I18n.translate(ConfigValues.SHOW_ON_MULTIPLAYER_SCREEN_NAME)) / 2 - 24, 112, 20, 20, I18n.translate(ConfigValues.SHOW_ON_MULTIPLAYER_SCREEN_NAME), ConfigValues.SHOW_ON_MULTIPLAYER_SCREEN));
		addButton(new ButtonWidget(width / 2 - 75, height - 24, 150, 20, I18n.translate("gui.done"), btn -> {
			minecraft.openScreen(prev);
		}));
		if (ConfigValues.TEXT_X != null) textX.setText(ConfigValues.TEXT_X);
		if (ConfigValues.TEXT_Y != null) textY.setText(ConfigValues.TEXT_Y);
	}
	
	@Override
	public void tick() {
		textX.tick();
		textY.tick();
		super.tick();
	}
	
	@Override
	public void removed() {
		ConfigValues.CASESENSITIVE = caseS.isChecked();
		ConfigValues.ENABLERELOG = relog.isChecked();
		ConfigValues.TEXT_X = textX.getText();
		ConfigValues.TEXT_Y = textY.getText();
		ConfigValues.SHOW_ON_MULTIPLAYER_SCREEN = mpscreen.isChecked();
		IAS.syncConfig(true);
	}
	
	@Override
	public void render(int mx, int my, float delta) {
		renderBackground();
		drawCenteredString(font, this.title.asFormattedString(), width / 2, 10, -1);
		drawCenteredString(font, I18n.translate(ConfigValues.TEXT_POS_NAME), width / 2, 80, -1);
		super.render(mx, my, delta);
	}
}

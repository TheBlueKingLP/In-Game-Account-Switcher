package ru.vidtu.iasfork;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import the_fireplace.ias.IAS;
import the_fireplace.ias.config.ConfigValues;

public class IASConfigScreen extends Screen {
	public final Screen prev;
	public CheckboxButton caseS, relog;
	public TextFieldWidget textX, textY;
	public IASConfigScreen(Screen prev) {
		super(new StringTextComponent("ias.properties"));
		this.prev = prev;
	}
	
	@Override
	protected void init() {
		addButton(caseS = new CheckboxButton(width / 2 - font.getStringWidth(I18n.format(ConfigValues.CASESENSITIVE_NAME)) / 2 - 24, 40, 20, 20, I18n.format(ConfigValues.CASESENSITIVE_NAME), ConfigValues.CASESENSITIVE));
		addButton(relog = new CheckboxButton(width / 2 - font.getStringWidth(I18n.format(ConfigValues.ENABLERELOG_NAME)) / 2 - 24, 60, 20, 20, I18n.format(ConfigValues.ENABLERELOG_NAME), ConfigValues.ENABLERELOG));
		addButton(textX = new TextFieldWidget(font, width / 2 - 100, 90, 98, 20, "X"));
		addButton(textY = new TextFieldWidget(font, width / 2 + 2, 90, 98, 20, "Y"));
		addButton(new Button(width / 2 - 75, height - 24, 150, 20, I18n.format("gui.done"), btn -> {
			minecraft.displayGuiScreen(prev);
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
		IAS.syncConfig(true);
	}
	
	@Override
	public void render(int mx, int my, float delta) {
		renderBackground();
		drawCenteredString(font, this.title.getFormattedText(), width / 2, 10, -1);
		drawCenteredString(font, I18n.format(ConfigValues.TEXT_POS_NAME), width / 2, 80, -1);
		super.render(mx, my, delta);
	}
}

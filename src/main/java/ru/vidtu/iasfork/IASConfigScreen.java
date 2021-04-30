package ru.vidtu.iasfork;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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
		addButton(caseS = new CheckboxButton(width / 2 - font.width(new TranslationTextComponent(ConfigValues.CASESENSITIVE_NAME)) / 2 - 24, 40, 20, 20, new TranslationTextComponent(ConfigValues.CASESENSITIVE_NAME), ConfigValues.CASESENSITIVE));
		addButton(relog = new CheckboxButton(width / 2 - font.width(new TranslationTextComponent(ConfigValues.ENABLERELOG_NAME)) / 2 - 24, 60, 20, 20, new TranslationTextComponent(ConfigValues.ENABLERELOG_NAME), ConfigValues.ENABLERELOG));
		addButton(textX = new TextFieldWidget(font, width / 2 - 100, 90, 98, 20, new StringTextComponent("X")));
		addButton(textY = new TextFieldWidget(font, width / 2 + 2, 90, 98, 20, new StringTextComponent("Y")));
		addButton(new Button(width / 2 - 75, height - 24, 150, 20, new TranslationTextComponent("gui.done"), btn -> {
			minecraft.setScreen(prev);
		}));
		if (ConfigValues.TEXT_X != null) textX.setValue(ConfigValues.TEXT_X);
		if (ConfigValues.TEXT_Y != null) textY.setValue(ConfigValues.TEXT_Y);
	}
	
	@Override
	public void tick() {
		textX.tick();
		textY.tick();
		super.tick();
	}
	
	@Override
	public void removed() {
		ConfigValues.CASESENSITIVE = caseS.selected();
		ConfigValues.ENABLERELOG = relog.selected();
		ConfigValues.TEXT_X = textX.getValue();
		ConfigValues.TEXT_Y = textY.getValue();
		IAS.syncConfig(true);
	}
	
	@Override
	public void render(MatrixStack ms, int mx, int my, float delta) {
		renderBackground(ms);
		drawCenteredString(ms, font, this.title, width / 2, 10, -1);
		drawCenteredString(ms, font, new TranslationTextComponent(ConfigValues.TEXT_POS_NAME), width / 2, 80, -1);
		super.render(ms, mx, my, delta);
	}
}

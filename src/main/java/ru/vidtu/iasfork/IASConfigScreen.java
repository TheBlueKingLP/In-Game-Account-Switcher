package ru.vidtu.iasfork;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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
		addButton(caseS = new CheckboxButton(width / 2 - font.width(new TranslationTextComponent(ConfigValues.CASESENSITIVE_NAME)) / 2 - 24, 40, 20, 20, new TranslationTextComponent(ConfigValues.CASESENSITIVE_NAME), ConfigValues.CASESENSITIVE));
		addButton(relog = new CheckboxButton(width / 2 - font.width(new TranslationTextComponent(ConfigValues.ENABLERELOG_NAME)) / 2 - 24, 60, 20, 20, new TranslationTextComponent(ConfigValues.ENABLERELOG_NAME), ConfigValues.ENABLERELOG));
		addButton(new Button(width / 2 - 75, height - 24, 150, 20, new TranslationTextComponent("gui.done"), btn -> {
			minecraft.setScreen(prev);
		}));
	}
	
	@Override
	public void removed() {
		IAS.CASESENSITIVE_PROPERTY.set(caseS.selected());
		IAS.ENABLERELOG_PROPERTY.set(relog.selected());
		IAS.syncConfig(true);
	}
	
	@Override
	public void render(MatrixStack ms, int mx, int my, float delta) {
		renderBackground(ms);
		drawCenteredString(ms, font, this.title, width / 2, 10, -1);
		super.render(ms, mx, my, delta);
	}
}

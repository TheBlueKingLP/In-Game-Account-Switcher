package ru.vidtu.iasfork;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import the_fireplace.ias.IAS;
import the_fireplace.ias.config.ConfigValues;

public class IASConfigScreen extends Screen {
	public final Screen prev;
	public Checkbox caseS, relog, mpscreen;
	public EditBox textX, textY;
	public IASConfigScreen(Screen prev) {
		super(new TextComponent("ias.properties"));
		this.prev = prev;
	}
	
	@Override
	protected void init() { 
		addRenderableWidget(caseS = new Checkbox(width / 2 - font.width(new TranslatableComponent(ConfigValues.CASESENSITIVE_NAME)) / 2 - 24, 40, 20, 20, new TranslatableComponent(ConfigValues.CASESENSITIVE_NAME), ConfigValues.CASESENSITIVE));
		addRenderableWidget(relog = new Checkbox(width / 2 - font.width(new TranslatableComponent(ConfigValues.ENABLERELOG_NAME)) / 2 - 24, 60, 20, 20, new TranslatableComponent(ConfigValues.ENABLERELOG_NAME), ConfigValues.ENABLERELOG));
		addRenderableWidget(textX = new EditBox(font, width / 2 - 100, 90, 98, 20, new TextComponent("X")));
		addRenderableWidget(textY = new EditBox(font, width / 2 + 2, 90, 98, 20, new TextComponent("Y")));
		addRenderableWidget(mpscreen = new Checkbox(width / 2 - font.width(new TranslatableComponent(ConfigValues.SHOW_ON_MULTIPLAYER_SCREEN_NAME)) / 2 - 24, 112, 20, 20, new TranslatableComponent(ConfigValues.SHOW_ON_MULTIPLAYER_SCREEN_NAME), ConfigValues.SHOW_ON_MULTIPLAYER_SCREEN));
		addRenderableWidget(new Button(width / 2 - 75, height - 24, 150, 20, new TranslatableComponent("gui.done"), btn -> {
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
		ConfigValues.SHOW_ON_MULTIPLAYER_SCREEN = mpscreen.selected();
		IAS.syncConfig(true);
	}
	
	@Override
	public void render(PoseStack ms, int mx, int my, float delta) {
		renderBackground(ms);
		drawCenteredString(ms, font, this.title, width / 2, 10, -1);
		drawCenteredString(ms, font, new TranslatableComponent(ConfigValues.TEXT_POS_NAME), width / 2, 80, -1);
		super.render(ms, mx, my, delta);
	}
}

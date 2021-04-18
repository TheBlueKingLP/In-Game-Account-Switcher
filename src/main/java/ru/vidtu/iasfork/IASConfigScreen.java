package ru.vidtu.iasfork;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import the_fireplace.ias.IAS;
import the_fireplace.ias.config.ConfigValues;

public class IASConfigScreen extends GuiScreen {
	public final GuiScreen prev;
	public GuiCheckBox caseS, relog;
	public IASConfigScreen(GuiScreen prev) {
		this.prev = prev;
	}
	
	@Override
	public void initGui() {
		addButton(caseS = new GuiCheckBox(1, width / 2 - fontRenderer.getStringWidth(I18n.format(ConfigValues.CASESENSITIVE_NAME)) / 2 - 7, 40, I18n.format(ConfigValues.CASESENSITIVE_NAME), ConfigValues.CASESENSITIVE));
		addButton(relog = new GuiCheckBox(2, width / 2 - fontRenderer.getStringWidth(I18n.format(ConfigValues.ENABLERELOG_NAME)) / 2 - 7, 60, I18n.format(ConfigValues.ENABLERELOG_NAME), ConfigValues.ENABLERELOG));
		addButton(new GuiButton(0, width / 2 - 75, height - 24, 150, 20, I18n.format("gui.done")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(prev);
			}
		});
	}
	
	@Override
	public void onGuiClosed() {
		IAS.CASESENSITIVE_PROPERTY = caseS.isChecked();
		IAS.ENABLERELOG_PROPERTY = relog.isChecked();
		IAS.syncConfig(true);
	}
	
	@Override
	public void render(int mx, int my, float delta) {
		drawDefaultBackground();
		drawCenteredString(fontRenderer, "ias.properties", width / 2, 10, -1);
		super.render(mx, my, delta);
	}
}

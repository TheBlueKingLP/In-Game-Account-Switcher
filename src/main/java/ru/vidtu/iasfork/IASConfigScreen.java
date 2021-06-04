package ru.vidtu.iasfork;

import cpw.mods.fml.client.config.GuiCheckBox;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import the_fireplace.ias.IAS;
import the_fireplace.ias.config.ConfigValues;

public class IASConfigScreen extends GuiScreen {
	public final GuiScreen prev;
	public GuiCheckBox caseS, relog, mpscreen;
	public GuiTextField textX, textY;
	public IASConfigScreen(GuiScreen prev) {
		this.prev = prev;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		buttonList.add(caseS = new GuiCheckBox(-1, width / 2 - fontRendererObj.getStringWidth(I18n.format(ConfigValues.CASESENSITIVE_NAME)) / 2 - 10, 40, I18n.format(ConfigValues.CASESENSITIVE_NAME), ConfigValues.CASESENSITIVE));
		buttonList.add(relog = new GuiCheckBox(-2, width / 2 - fontRendererObj.getStringWidth(I18n.format(ConfigValues.ENABLERELOG_NAME)) / 2 - 10, 60, I18n.format(ConfigValues.ENABLERELOG_NAME), ConfigValues.ENABLERELOG));
		textX = new GuiTextField(fontRendererObj, width / 2 - 100, 90, 98, 20);
		textY = new GuiTextField(fontRendererObj, width / 2 + 2, 90, 98, 20);
		buttonList.add(mpscreen = new GuiCheckBox(-3, width / 2 - fontRendererObj.getStringWidth(I18n.format(ConfigValues.SHOW_ON_MULTIPLAYER_SCREEN_NAME)) / 2 - 10, 112, I18n.format(ConfigValues.SHOW_ON_MULTIPLAYER_SCREEN_NAME), ConfigValues.SHOW_ON_MULTIPLAYER_SCREEN));
		buttonList.add(new GuiButton(0, width / 2 - 75, height - 24, 150, 20, I18n.format("gui.done")));
		if (ConfigValues.TEXT_X != null) textX.setText(ConfigValues.TEXT_X);
		if (ConfigValues.TEXT_Y != null) textY.setText(ConfigValues.TEXT_Y);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 0) mc.displayGuiScreen(prev);
	}
	
	@Override
	public void onGuiClosed() {
		ConfigValues.CASESENSITIVE = caseS.isChecked();
		ConfigValues.ENABLERELOG = relog.isChecked();
		ConfigValues.TEXT_X = textX.getText();
		ConfigValues.TEXT_Y = textY.getText();
		ConfigValues.SHOW_ON_MULTIPLAYER_SCREEN = mpscreen.isChecked();
		IAS.syncConfig(true);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		textX.textboxKeyTyped(typedChar, keyCode);
		textY.textboxKeyTyped(typedChar, keyCode);
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	public void updateScreen() {
		textX.updateCursorCounter();
		textY.updateCursorCounter();
		super.updateScreen();
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		textX.mouseClicked(mouseX, mouseY, mouseButton);
		textY.mouseClicked(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public void drawScreen(int mx, int my, float delta) {
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, "ias.properties", width / 2, 10, -1);
		drawCenteredString(fontRendererObj, I18n.format(ConfigValues.TEXT_POS_NAME), width / 2, 80, -1);
		textX.drawTextBox();
		textY.drawTextBox();
		super.drawScreen(mx, my, delta);
	}
}

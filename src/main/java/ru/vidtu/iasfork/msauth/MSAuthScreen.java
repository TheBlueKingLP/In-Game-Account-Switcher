package ru.vidtu.iasfork.msauth;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class MSAuthScreen extends Screen {
	public static final String[] symbols = new String[]{"▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃", "_ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄",
			"_ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅", "_ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆", "_ _ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇", "_ _ _ _ _ ▃ ▄ ▅ ▆ ▇ █",
			"_ _ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇", "_ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆", "_ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅", "_ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄",
			"▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃", "▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _", "▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _", "▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _",
			"▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _ _", "█ ▇ ▆ ▅ ▄ ▃ _ _ _ _ _", "▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _ _", "▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _",
			"▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _", "▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _"};
	public Screen prev;
	public List<String> text = new ArrayList<>();
	public boolean endTask = false;
	public int tick;
	public MSAuthScreen(Screen prev) {
		super(new TranslationTextComponent("ias.msauth.title"));
		this.prev = prev;
		AuthSys.start(this);
	}
	
	@Override
	protected void init() {
		addButton(new Button(width / 2 - 50, height - 24, 100, 20, I18n.format("gui.cancel"), btn -> minecraft.displayGuiScreen(prev)));
	}
	
	@Override
	public void tick() {
		tick++;
	}
	
	@Override
	public void render(int mouseX, int mouseY, float delta) {
		renderBackground();
		drawCenteredString(font, this.title.getFormattedText(), width / 2, 10, -1);
		for (int i = 0; i < text.size(); i++) {
			drawCenteredString(font, text.get(i), width / 2, height / 2 + i * 10, -1);
		}
		if (!endTask) drawCenteredString(font, symbols[tick % symbols.length], width / 2, height / 3 * 2, -256);
		super.render(mouseX, mouseY, delta);
	}

	@Override
	public void removed() {
		AuthSys.stop();
		super.removed();
	}
	
	public void setState(String s) {
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.execute(() -> this.text = minecraft.fontRenderer.listFormattedStringToWidth(I18n.format(s), width));
	}
	
	public void error(String error) {
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.execute(() -> {
			this.text = minecraft.fontRenderer.listFormattedStringToWidth(TextFormatting.RED + I18n.format("ias.msauth.error", error), width);
			endTask = true;
		});
	}
}

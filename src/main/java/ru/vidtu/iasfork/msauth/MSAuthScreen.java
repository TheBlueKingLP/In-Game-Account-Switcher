package ru.vidtu.iasfork.msauth;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.IReorderingProcessor;
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
	public List<IReorderingProcessor> text = new ArrayList<>();
	public boolean endTask = false;
	public int tick;
	public MSAuthScreen(Screen prev) {
		super(new TranslationTextComponent("ias.msauth.title"));
		this.prev = prev;
		AuthSys.start(this);
	}
	
	@Override
	protected void init() {
		addButton(new Button(width / 2 - 50, height - 24, 100, 20, new TranslationTextComponent("gui.cancel"), btn -> minecraft.setScreen(prev)));
	}
	
	@Override
	public void tick() {
		tick++;
	}
	
	@Override
	public void render(MatrixStack ms, int mouseX, int mouseY, float delta) {
		renderBackground(ms);
		drawCenteredString(ms, font, this.title, width / 2, 10, -1);
		for (int i = 0; i < text.size(); i++) {
			font.draw(ms, text.get(i), width / 2 - font.width(text.get(i)) / 2, height / 2 + i * 10, -1);
		}
		if (!endTask) drawCenteredString(ms, font, symbols[tick % symbols.length], width / 2, height / 3 * 2, -256);
		super.render(ms, mouseX, mouseY, delta);
	}

	@Override
	public void removed() {
		AuthSys.stop();
		super.removed();
	}
	
	public void setState(String s) {
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.execute(() -> this.text = minecraft.font.split(new TranslationTextComponent(s), width));
	}
	
	public void error(String error) {
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.execute(() -> {
			this.text = minecraft.font.split(new TranslationTextComponent("ias.msauth.error", error).withStyle(TextFormatting.RED), width);
			endTask = true;
		});
	}
}

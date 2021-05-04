package ru.vidtu.iasfork.msauth;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class MSAuthScreen extends Screen {
	public static final String[] symbols = new String[]{"▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃", "_ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄",
			"_ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅", "_ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆", "_ _ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇", "_ _ _ _ _ ▃ ▄ ▅ ▆ ▇ █",
			"_ _ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇", "_ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆", "_ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅", "_ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄",
			"▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃", "▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _", "▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _", "▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _",
			"▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _ _", "█ ▇ ▆ ▅ ▄ ▃ _ _ _ _ _", "▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _ _", "▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _",
			"▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _", "▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _"};
	public Screen prev;
	public List<OrderedText> text = new ArrayList<>();
	public boolean endTask = false;
	public int tick;
	public MSAuthScreen(Screen prev) {
		super(new TranslatableText("ias.msauth.title"));
		this.prev = prev;
		AuthSys.start(this);
	}
	
	@Override
	protected void init() {
		addButton(new ButtonWidget(width / 2 - 50, height - 24, 100, 20, new TranslatableText("gui.cancel"), btn -> client.openScreen(prev)));
	}
	
	@Override
	public void tick() {
		tick++;
	}
	
	@Override
	public void render(MatrixStack ms, int mouseX, int mouseY, float delta) {
		renderBackground(ms);
		drawCenteredText(ms, textRenderer, this.title, width / 2, 10, -1);
		for (int i = 0; i < text.size(); i++) {
			textRenderer.draw(ms, text.get(i), width / 2 - textRenderer.getWidth(text.get(i)) / 2, height / 2 + i * 10, -1);
		}
		if (!endTask) drawCenteredString(ms, textRenderer, symbols[tick % symbols.length], width / 2, height / 3 * 2, -256);
		super.render(ms, mouseX, mouseY, delta);
	}

	@Override
	public void removed() {
		AuthSys.stop();
		super.removed();
	}
	
	public void setState(String s) {
		MinecraftClient minecraft = MinecraftClient.getInstance();
		minecraft.execute(() -> this.text = minecraft.textRenderer.wrapLines(new TranslatableText(s), width));
	}
	
	public void error(String error) {
		MinecraftClient minecraft = MinecraftClient.getInstance();
		minecraft.execute(() -> {
			this.text = minecraft.textRenderer.wrapLines(new TranslatableText("ias.msauth.error", error).formatted(Formatting.RED), width);
			endTask = true;
		});
	}
}

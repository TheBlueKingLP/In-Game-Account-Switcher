package ru.vidtu.iasfork.msauth;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.TextFormat;
import net.minecraft.text.TranslatableText;

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
		super(new TranslatableText("ias.msauth.title"));
		this.prev = prev;
		AuthSys.start(this);
	}
	
	@Override
	protected void init() {
		addButton(new ButtonWidget(width / 2 - 50, height - 24, 100, 20, I18n.translate("gui.cancel"), btn -> minecraft.openScreen(prev)));
	}
	
	@Override
	public void tick() {
		tick++;
	}
	
	@Override
	public void render(int mouseX, int mouseY, float delta) {
		renderBackground();
		drawCenteredString(font, this.title.asFormattedString(), width / 2, 10, -1);
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
		MinecraftClient minecraft = MinecraftClient.getInstance();
		minecraft.execute(() -> this.text = minecraft.textRenderer.wrapStringToWidthAsList(I18n.translate(s), width));
	}
	
	public void error(String error) {
		MinecraftClient minecraft = MinecraftClient.getInstance();
		minecraft.execute(() -> {
			this.text = minecraft.textRenderer.wrapStringToWidthAsList(TextFormat.RED + I18n.translate("ias.msauth.error", error), width);
			endTask = true;
		});
	}
}

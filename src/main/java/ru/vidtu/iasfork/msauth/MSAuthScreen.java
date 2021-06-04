package ru.vidtu.iasfork.msauth;

import java.util.ArrayList;
import java.util.List;

import com.github.mrebhan.ingameaccountswitcher.MR;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.TextFormat;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import ru.vidtu.iasfork.msauth.AuthSys.MicrosoftAuthException;
import the_fireplace.ias.gui.AbstractAccountGui;
import the_fireplace.ias.gui.GuiAccountSelector;
import the_fireplace.iasencrypt.EncryptionTools;

public class MSAuthScreen extends Screen implements MSAuthHandler {
	public static final String[] symbols = new String[]{"▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃", "_ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄",
			"_ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅", "_ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆", "_ _ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇", "_ _ _ _ _ ▃ ▄ ▅ ▆ ▇ █",
			"_ _ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇", "_ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆", "_ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅", "_ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄",
			"▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃", "▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _", "▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _", "▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _",
			"▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _ _", "█ ▇ ▆ ▅ ▄ ▃ _ _ _ _ _", "▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _ _", "▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _",
			"▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _", "▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _"};
	private static final Identifier DEMO_BG = new Identifier("textures/gui/demo_background.png");
	
	public Screen prev;
	public List<String> text = new ArrayList<>();
	public boolean endTask = false;
	public int tick;
	public final boolean add;
	public boolean cancelButton = true;
	
	public MSAuthScreen(Screen prev) {
		super(new TranslatableText("ias.msauth.title"));
		this.prev = prev;
		this.add = true;
		AuthSys.start(this);
	}
	
	public MSAuthScreen(Screen prev, String token, String refresh) {
		super(new TranslatableText("ias.msauth.title"));
		this.prev = prev;
		this.add = false;
		AuthSys.start(token, refresh, this);
	}
	
	@Override
	public void init() {
		addButton(new ButtonWidget(width / 2 - 50, (this.height + 114) / 2, 100, 20, I18n.translate("gui.cancel"), btn -> minecraft.openScreen(prev))).active = cancelButton;
	}
	
	@Override
	public void init(MinecraftClient client, int width, int height) {
		prev.init(client, width, height);
		super.init(client, width, height);
	}
	
	@Override
	public void tick() {
		tick++;
		buttons.get(0).active = cancelButton;
	}
	
	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
	
	@Override
	public void render(int mouseX, int mouseY, float delta) {
		renderBackground();
		
		if (prev != null) prev.render(0, 0, delta);
		fill(0, 0, width, height, Integer.MIN_VALUE);
		
		GlStateManager.color4f(1F, 1F, 1F, 1F);
		this.minecraft.getTextureManager().bindTexture(DEMO_BG);
		this.blit((this.width - 248) / 2, (this.height - 166) / 2, 0, 0, 248, 166);
		
		font.draw(this.title.asFormattedString(), width / 2 - font.getStringWidth(this.title.asFormattedString()) / 2, (this.height - 156) / 2, -16777216);
		for (int i = 0; i < text.size(); i++) {
			font.draw(text.get(i), width / 2 - font.getStringWidth(text.get(i)) / 2, height / 2 + i * 10 - text.size() * 5, 0xFF353535);
		}
		if (!endTask) font.draw(symbols[tick % symbols.length], width / 2 - font.getStringWidth(symbols[tick % symbols.length]) / 2, height - 10, 0xFFFF9900);
		super.render(mouseX, mouseY, delta);
	}

	@Override
	public void removed() {
		AuthSys.stop();
		prev.removed();
		super.removed();
	}
	
	@Override
	public void setState(String s) {
		MinecraftClient mc = MinecraftClient.getInstance();
		mc.execute(() -> this.text = mc.textRenderer.wrapStringToWidthAsList(I18n.translate(s), 240));
	}

	@Override
	public void error(Throwable t) {
		cancelButton = true;
		MinecraftClient mc = MinecraftClient.getInstance();
		mc.execute(() -> {
			endTask = true;
			if (t instanceof MicrosoftAuthException) {
				this.text = mc.textRenderer.wrapStringToWidthAsList(TextFormat.DARK_RED + I18n.translate("ias.msauth.error", t.getMessage()), 240);
			} else {
				this.text = mc.textRenderer.wrapStringToWidthAsList(TextFormat.DARK_RED + I18n.translate("ias.msauth.error", t.toString()), 240);
			}
		});
	}

	@Override
	public void success(String name, String uuid, String token, String refresh) {
		MinecraftClient mc = MinecraftClient.getInstance();
		mc.execute(() -> {
			if (add) {
				MicrosoftAccount.msaccounts.add(new MicrosoftAccount(name, EncryptionTools.encode(token), EncryptionTools.encode(refresh)));
				mc.openScreen(new GuiAccountSelector(prev instanceof AbstractAccountGui?(((AbstractAccountGui)prev).prev instanceof GuiAccountSelector?((GuiAccountSelector)((AbstractAccountGui)prev).prev).prev:((AbstractAccountGui)prev).prev):prev));
			} else {
				MR.setSession(new Session(name, uuid, token, "mojang"));
				mc.openScreen(prev);
			}
		});
	}

	@Override
	public void cancellble(boolean b) {
		this.cancelButton = b;
	}
}

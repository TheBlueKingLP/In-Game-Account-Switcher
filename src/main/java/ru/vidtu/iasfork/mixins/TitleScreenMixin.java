package ru.vidtu.iasfork.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.mrebhan.ingameaccountswitcher.tools.Config;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import ru.vidtu.iasfork.IASMMPos;
import the_fireplace.ias.gui.GuiAccountSelector;
import the_fireplace.ias.gui.GuiButtonWithImage;
import the_fireplace.ias.tools.SkinTools;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
	private static boolean skinsLoaded, modMenu = false;
	protected TitleScreenMixin(Text title) {
		super(title);
	}
	
	@Inject(method = "init", at = @At("TAIL"))
	public void onInit(CallbackInfo ci) {
		if (!skinsLoaded) {
			SkinTools.cacheSkins(false);
			skinsLoaded = true;
		}
		modMenu = FabricLoader.getInstance().isModLoaded("modmenu");
		addButton(new GuiButtonWithImage(width / 2 + 104, height / 4 + 48 + 72 + (modMenu?IASMMPos.buttonOffset():-12), 20, 20, new LiteralText(""), btn -> {
			if (Config.getInstance() == null) {
				Config.load();
			}
			client.openScreen(new GuiAccountSelector());
		}));
	}

	@Inject(method = "render", at = @At("TAIL"))
	public void onRender(MatrixStack ms, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		drawCenteredString(ms, textRenderer, I18n.translate("ias.loggedinas") + " " + client.getSession().getUsername() + ".", width / 2, height / 4 + 48 + 72 + 12 + (modMenu?32:22), 0xFFCC8888);
	}
}

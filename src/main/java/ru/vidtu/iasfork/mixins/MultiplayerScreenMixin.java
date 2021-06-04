package ru.vidtu.iasfork.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.mrebhan.ingameaccountswitcher.tools.Config;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import the_fireplace.ias.config.ConfigValues;
import the_fireplace.ias.gui.GuiAccountSelector;
import the_fireplace.ias.gui.GuiButtonWithImage;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {
	protected MultiplayerScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "render", at = @At("TAIL"))
	public void onRender(int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (minecraft.getSession().getAccessToken().equals("0")) {
			List<String> list = minecraft.textRenderer.wrapStringToWidthAsList(I18n.translate("ias.offlinemode"), width);
			for (int i = 0; i < list.size(); i++) {
				drawCenteredString(minecraft.textRenderer, list.get(i), width / 2, i * 9 + 1, 16737380);
			}
		}
	}
	
	@Inject(method = "init", at = @At("TAIL"))
	public void onInit(CallbackInfo ci) {
		if (ConfigValues.SHOW_ON_MULTIPLAYER_SCREEN) {
			addButton(new GuiButtonWithImage(this.width / 2 + 4 + 76 + 79, height - 28, btn -> {
				if (Config.getInstance() == null) {
					Config.load();
				}
				minecraft.openScreen(new GuiAccountSelector(this));
			}));
		}
	}
}

package ru.vidtu.iasfork.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

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
}

package the_fireplace.ias.events;

import java.util.List;

import com.github.mrebhan.ingameaccountswitcher.tools.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import the_fireplace.ias.gui.GuiAccountSelector;
import the_fireplace.ias.gui.GuiButtonWithImage;

/**
 * @author The_Fireplace
 */
public class ClientEvents {
	@SubscribeEvent
	public void guiEvent(InitGuiEvent.Post event){
		Screen gui = event.getGui();
		if(gui instanceof MainMenuScreen){
			event.addWidget(new GuiButtonWithImage(gui.width / 2 + 104, gui.height / 4 + 48 + 72 - 12, 20, 20, new StringTextComponent(""), btn -> {
				if(Config.getInstance() == null){
					Config.load();
				}
				Minecraft.getInstance().setScreen(new GuiAccountSelector());
			}));
		}
	}
	
	@SubscribeEvent
	public void onTick(GuiScreenEvent.DrawScreenEvent.Post t) {
		Minecraft mc = Minecraft.getInstance();
		Screen screen = mc.screen;
		if (screen instanceof MainMenuScreen) {
			Screen.drawCenteredString(t.getMatrixStack(), mc.font, new TranslationTextComponent("ias.loggedinas").append(" " + Minecraft.getInstance().getUser().getName() + "."), screen.width / 2, screen.height / 4 + 48 + 72 + 12 + 22, 0xFFCC8888);
		} else if(screen instanceof MultiplayerScreen){
			if (Minecraft.getInstance().getUser().getAccessToken().equals("0")) {
				List<IReorderingProcessor> list = mc.font.split(new TranslationTextComponent("ias.offlinemode"), t.getGui().width);
				for (int i = 0; i < list.size(); i++) {
					mc.font.drawShadow(t.getMatrixStack(), list.get(i), (float)(screen.width / 2 - mc.font.width(list.get(i)) / 2), i * 9 + 1, -1);
				}
			}
		}
	}
}

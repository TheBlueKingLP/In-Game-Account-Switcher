package the_fireplace.ias.events;

import java.util.List;

import com.github.mrebhan.ingameaccountswitcher.tools.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
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
			event.addWidget(new GuiButtonWithImage(gui.width / 2 + 104, gui.height / 4 + 48 + 72 - 12, 20, 20, "", btn -> {
				if(Config.getInstance() == null){
					Config.load();
				}
				Minecraft.getInstance().displayGuiScreen(new GuiAccountSelector());
			}));
		}
	}
	
	@SubscribeEvent
	public void onTick(GuiScreenEvent.DrawScreenEvent.Post t) {
		Minecraft mc = Minecraft.getInstance();
		Screen screen = mc.currentScreen;
		if (screen instanceof MainMenuScreen) {
			screen.drawCenteredString(mc.fontRenderer, I18n.format("ias.loggedinas") + " " + Minecraft.getInstance().getSession().getUsername() +".", screen.width / 2, screen.height / 4 + 48 + 72 + 12 + 22, 0xFFCC8888);
		} else if(screen instanceof MultiplayerScreen){
			if (Minecraft.getInstance().getSession().getToken().equals("0")) {
				List<String> list = mc.fontRenderer.listFormattedStringToWidth(I18n.format("ias.offlinemode"), t.getGui().width);
				for (int i = 0; i < list.size(); i++) {
					screen.drawCenteredString(mc.fontRenderer, list.get(i), screen.width / 2, i * 9 + 1, 16737380);
				}
			}
		}
	}
}

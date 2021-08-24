package the_fireplace.ias.events;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.github.mrebhan.ingameaccountswitcher.tools.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import ru.vidtu.iasfork.Expression;
import the_fireplace.ias.config.ConfigValues;
import the_fireplace.ias.gui.GuiAccountSelector;
import the_fireplace.ias.gui.GuiButtonWithImage;

/**
 * @author The_Fireplace
 */
public class ClientEvents {
	private static int textX, textY;
	@SubscribeEvent
	public void guiEvent(InitGuiEvent.Post event){
		Screen gui = event.getGui();
		if(gui instanceof MainMenuScreen){
			try {
				if (StringUtils.isNotBlank(ConfigValues.TEXT_X) && StringUtils.isNotBlank(ConfigValues.TEXT_Y)) {
					textX = (int) new Expression(ConfigValues.TEXT_X.replace("%width%", Integer.toString(event.getGui().width)).replace("%height%", Integer.toString(event.getGui().height))).parse(0);
					textY = (int) new Expression(ConfigValues.TEXT_Y.replace("%width%", Integer.toString(event.getGui().width)).replace("%height%", Integer.toString(event.getGui().height))).parse(0);
				} else {
					textX = event.getGui().width / 2;
					textY = event.getGui().height / 4 + 48 + 72 + 12 + 22;
				}
			} catch (Throwable t) {
				t.printStackTrace();
				textX = event.getGui().width / 2;
				textY = event.getGui().height / 4 + 48 + 72 + 12 + 2;
			}
			event.addWidget(new GuiButtonWithImage(gui.width / 2 + 104, gui.height / 4 + 48 + 72 - 12, btn -> {
				if(Config.getInstance() == null){
					Config.load();
				}
				Minecraft.getInstance().displayGuiScreen(new GuiAccountSelector(event.getGui()));
			}));
		} else if (gui instanceof MultiplayerScreen && ConfigValues.SHOW_ON_MULTIPLAYER_SCREEN) {
			event.addWidget(new GuiButtonWithImage(event.getGui().width / 2 + 4 + 76 + 79, event.getGui().height - 28, btn -> {
				if (Config.getInstance() == null) {
					Config.load();
				}
				Minecraft.getInstance().displayGuiScreen(new GuiAccountSelector(event.getGui()));
			}));
		}
	}
	
	@SubscribeEvent
	public void onTick(GuiScreenEvent.DrawScreenEvent.Post t) {
		Minecraft mc = Minecraft.getInstance();
		Screen screen = mc.currentScreen;
		if (screen instanceof MainMenuScreen) {
			screen.drawCenteredString(mc.fontRenderer, I18n.format("ias.loggedinas") + " " + Minecraft.getInstance().getSession().getUsername() +".", textX, textY, 0xFFCC8888);
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

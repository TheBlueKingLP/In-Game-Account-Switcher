package the_fireplace.ias.events;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.github.mrebhan.ingameaccountswitcher.tools.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
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
		if(gui instanceof TitleScreen){
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
				Minecraft.getInstance().setScreen(new GuiAccountSelector(event.getGui()));
			}));
		} else if (gui instanceof JoinMultiplayerScreen  && ConfigValues.SHOW_ON_MULTIPLAYER_SCREEN) {
			event.addWidget(new GuiButtonWithImage(event.getGui().width / 2 + 4 + 76 + 79, event.getGui().height - 28, btn -> {
				if (Config.getInstance() == null) {
					Config.load();
				}
				Minecraft.getInstance().setScreen(new GuiAccountSelector(event.getGui()));
			}));
		}
	}
	
	@SubscribeEvent
	public void onTick(GuiScreenEvent.DrawScreenEvent.Post t) {
		Minecraft mc = Minecraft.getInstance();
		Screen screen = mc.screen;
		if (screen instanceof TitleScreen) {
			Screen.drawCenteredString(t.getMatrixStack(), mc.font, new TranslatableComponent("ias.loggedinas").append(" " + Minecraft.getInstance().getUser().getName() + "."), textX, textY, 0xFFCC8888);
		} else if(screen instanceof JoinMultiplayerScreen){
			if (Minecraft.getInstance().getUser().getAccessToken().equals("0")) {
				List<FormattedCharSequence> list = mc.font.split(new TranslatableComponent("ias.offlinemode"), t.getGui().width);
				for (int i = 0; i < list.size(); i++) {
					mc.font.drawShadow(t.getMatrixStack(), list.get(i), (float)(screen.width / 2 - mc.font.width(list.get(i)) / 2), i * 9 + 1, -1);
				}
			}
		}
	}
}

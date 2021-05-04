package the_fireplace.ias.events;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.github.mrebhan.ingameaccountswitcher.tools.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
				ScriptEngine engine = new ScriptEngineManager(null).getEngineByName("JavaScript");
				textX = ((Number) engine.eval(ConfigValues.TEXT_X.replace("%width%", Integer.toString(event.getGui().width))
						.replace("%height%", Integer.toString(event.getGui().height)))).intValue();
				textY = ((Number) engine.eval(ConfigValues.TEXT_Y.replace("%width%", Integer.toString(event.getGui().width))
						.replace("%height%", Integer.toString(event.getGui().height)))).intValue();
			} catch (Throwable t) {
				textX = event.getGui().width / 2;
				textY = event.getGui().height / 4 + 48 + 72 + 12 + 22;
			}
			event.addWidget(new GuiButtonWithImage(gui.width / 2 + 104, gui.height / 4 + 48 + 72 - 12, btn -> {
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
			Screen.drawCenteredString(t.getMatrixStack(), mc.font, new TranslationTextComponent("ias.loggedinas").append(" " + Minecraft.getInstance().getUser().getName() + "."), textX, textY, 0xFFCC8888);
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

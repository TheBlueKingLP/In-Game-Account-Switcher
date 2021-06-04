package ru.vidtu.iasfork;

import java.util.function.Function;

import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

public class IASModMenuCompat implements ModMenuApi {
	@Override
	public String getModId() {
		return "ias";
	}
	
	@Override
	public Function<Screen, ? extends Screen> getConfigScreenFactory() {
		return screen -> new IASConfigScreen(screen);
	}

}

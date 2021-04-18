package ru.vidtu.iasfork;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

public class IASModMenuCompat implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return screen -> new IASConfigScreen(screen);
	}

}

package the_fireplace.ias.gui;

import com.github.mrebhan.ingameaccountswitcher.tools.alt.AltDatabase;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import ru.vidtu.iasfork.msauth.MSAuthScreen;
import the_fireplace.ias.account.ExtendedAccountData;

/**
 * The GUI where the alt is added
 * @author The_Fireplace
 * @author evilmidget38
 */
public class GuiAddAccount extends AbstractAccountGui {

	public GuiAddAccount(Screen prev)
	{
		super(prev, new TranslatableComponent("ias.addaccount"));
	}
	
	@Override
	public void init() {
		super.init();
		addRenderableWidget(new Button(width / 2 - 60, height / 3 * 2, 120, 20, new TranslatableComponent("ias.msauth.btn"), btn -> minecraft.setScreen(new MSAuthScreen(this))));
	}

	@Override
	public void complete()
	{
		AltDatabase.getInstance().getAlts().add(new ExtendedAccountData(getUsername(), getPassword(), getUsername()));
	}
}

package the_fireplace.ias.gui;

import org.lwjgl.glfw.GLFW;

import com.github.mrebhan.ingameaccountswitcher.tools.alt.AccountData;
import com.github.mrebhan.ingameaccountswitcher.tools.alt.AltDatabase;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import the_fireplace.iasencrypt.EncryptionTools;

/**
 * @author evilmidget38
 * @author The_Fireplace
 */
public abstract class AbstractAccountGui extends Screen
{
	public final Screen prev;
	private EditBox username;
	private EditBox password;
	private Button complete;
	protected boolean hasUserChanged = false;

	public AbstractAccountGui(Screen prev, Component actionString)
	{
		super(actionString);
		this.prev = prev;
	}
	
	@Override
	protected void init() {
		addRenderableWidget(complete = new Button(this.width / 2 - 152, this.height - 28, 150, 20, this.title, btn -> {
			complete();
			escape();
		}));
		addRenderableWidget(new Button(this.width / 2 + 2, this.height - 28, 150, 20, new TranslatableComponent("gui.cancel"), btn -> escape()));
		addRenderableWidget(username = new EditBox(this.font, this.width / 2 - 100, 60, 200, 20, new TextComponent("")));
		username.setMaxLength(64);
		addRenderableWidget(password = new GuiPasswordField(this.font, this.width / 2 - 100, 90, 200, 20, new TextComponent("")));
		password.setMaxLength(64);
		complete.active = false;
	}
	
	@Override
	public void render(PoseStack ms, int mx, int my, float delta) {
		renderBackground(ms);
		drawCenteredString(ms, font, this.title, this.width / 2, 7, -1);
		drawCenteredString(ms, font, new TranslatableComponent("ias.username"), this.width / 2 - 130, 66, -1);
		drawCenteredString(ms, font, new TranslatableComponent("ias.password"), this.width / 2 - 130, 96, -1);
		super.render(ms, mx, my, delta);
	}
	
	@Override
	public boolean keyPressed(int key, int oldkey, int mods) {
		if (key == GLFW.GLFW_KEY_ESCAPE) {
			escape();
		} else if (key == GLFW.GLFW_KEY_ENTER) {
			if (username.isFocused()) {
				username.setFocus(false);
				password.setFocus(true);
			} else if (password.isFocused() && complete.active) {
				complete();
				escape();
			}
		} else {
			if (username.isFocused()) hasUserChanged = true;
		}
		return super.keyPressed(key, oldkey, mods);
	}
	
	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
	
	@Override
	public void tick() {
		complete.active = canComplete();
		username.tick();
		password.tick();
		super.tick();
	}

	private void escape(){
		minecraft.setScreen(prev);
	}

	public String getUsername()
	{
		return username.getValue();
	}

	public String getPassword()
	{
		return password.getValue();
	}

	public void setUsername(String username)
	{
		this.username.setValue(username);
	}

	public void setPassword(String password)
	{
		this.password.setValue(password);
	}

	protected boolean accountNotInList(){
		for(AccountData data : AltDatabase.getInstance().getAlts())
			if(EncryptionTools.decode(data.user).equals(getUsername()))
				return false;
		return true;
	}

	public boolean canComplete()
	{
		return getUsername().length() > 0 && accountNotInList();
	}

	public abstract void complete();
}

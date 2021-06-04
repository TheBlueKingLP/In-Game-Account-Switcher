package the_fireplace.ias.gui;

import org.lwjgl.glfw.GLFW;

import com.github.mrebhan.ingameaccountswitcher.tools.alt.AccountData;
import com.github.mrebhan.ingameaccountswitcher.tools.alt.AltDatabase;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import the_fireplace.iasencrypt.EncryptionTools;

/**
 * @author evilmidget38
 * @author The_Fireplace
 */
public abstract class AbstractAccountGui extends GuiScreen
{
	public final GuiScreen prev;
	private final String actionString;
	private GuiTextField username;
	private GuiTextField password;
	private GuiButton complete;
	protected boolean hasUserChanged = false;

	public AbstractAccountGui(GuiScreen prev, String actionString) {
		this.prev = prev;
		this.actionString = actionString;
	}
	
	@Override
	public void initGui() {
		addButton(complete = new GuiButton(1, this.width / 2 - 152, this.height - 28, 150, 20, I18n.format(this.actionString)) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				complete();
				escape();
			}
		});
		addButton(new GuiButton(0, this.width / 2 + 2, this.height - 28, 150, 20, I18n.format("gui.cancel")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				escape();
			}
		});
		username = new GuiTextField(-1, this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
		username.setMaxStringLength(64);
		password = new GuiPasswordField(-2, this.fontRenderer, this.width / 2 - 100, 90, 200, 20);
		password.setMaxStringLength(64);
		complete.enabled = false;
	}
	
	@Override
	public boolean mouseClicked(double mx, double my, int btn) {
		username.mouseClicked(mx, my, btn);
		password.mouseClicked(mx, my, btn);
		return super.mouseClicked(mx, my, btn);
	}
	
	@Override
	public void render(int mx, int my, float delta) {
		drawDefaultBackground();
		this.drawCenteredString(fontRenderer, I18n.format(this.actionString), this.width / 2, 7, -1);
		this.drawCenteredString(fontRenderer, I18n.format("ias.username"), this.width / 2 - 130, 66, -1);
		this.drawCenteredString(fontRenderer, I18n.format("ias.password"), this.width / 2 - 130, 96, -1);
		username.drawTextField(mx, my, delta);
		password.drawTextField(mx, my, delta);
		super.render(mx, my, delta);
	}
	
	@Override
	public boolean charTyped(char charT, int key) {
		username.charTyped(charT, key);
		password.charTyped(charT, key);
		return super.charTyped(charT, key);
	}
	
	@Override
	public boolean keyPressed(int key, int oldkey, int mods) {
		if (key == GLFW.GLFW_KEY_ESCAPE) {
			escape();
			return true;
		} else if (key == GLFW.GLFW_KEY_ENTER) {
			if (username.isFocused()) {
				username.setFocused(false);
				password.setFocused(true);
			} else if (password.isFocused() && complete.enabled) {
				complete();
				escape();
			}
		} else {
			if (username.isFocused()) hasUserChanged = true;
		}
		return super.keyPressed(key, oldkey, mods);
	}
	
	@Override
	public void tick() {
		complete.enabled = canComplete();
		username.tick();
		password.tick();
		super.tick();
	}

	private void escape(){
		mc.displayGuiScreen(prev);
	}

	public String getUsername()
	{
		return username.getText();
	}

	public String getPassword()
	{
		return password.getText();
	}

	public void setUsername(String username)
	{
		this.username.setText(username);
	}

	public void setPassword(String password)
	{
		this.password.setText(password);
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

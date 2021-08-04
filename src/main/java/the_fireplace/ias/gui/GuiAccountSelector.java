package the_fireplace.ias.gui;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

import com.github.mrebhan.ingameaccountswitcher.tools.Config;
import com.github.mrebhan.ingameaccountswitcher.tools.Tools;
import com.github.mrebhan.ingameaccountswitcher.tools.alt.AccountData;
import com.github.mrebhan.ingameaccountswitcher.tools.alt.AltDatabase;
import com.github.mrebhan.ingameaccountswitcher.tools.alt.AltManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import ru.vidtu.iasfork.msauth.Account;
import ru.vidtu.iasfork.msauth.MicrosoftAccount;
import the_fireplace.ias.account.AlreadyLoggedInException;
import the_fireplace.ias.account.ExtendedAccountData;
import the_fireplace.ias.config.ConfigValues;
import the_fireplace.ias.tools.HttpTools;
import the_fireplace.ias.tools.JavaTools;
import the_fireplace.ias.tools.SkinTools;
import the_fireplace.iasencrypt.EncryptionTools;
/**
 * The GUI where you can log in to, add, and remove accounts
 * @author The_Fireplace
 */
public class GuiAccountSelector extends Screen {
	public final Screen prev;
	
	private int selectedAccountIndex = 0;
	private int prevIndex = 0;
	private Throwable loginfailed;
	private ArrayList<Account> queriedaccounts = convertData();
	private GuiAccountSelector.List accountsgui;
	// Buttons that can be disabled need to be here
	private ButtonWidget login;
	private ButtonWidget loginoffline;
	private ButtonWidget delete;
	private ButtonWidget edit;
	private ButtonWidget reloadskins;
	// Search
	private String prevQuery = "";
	private TextFieldWidget search;
	
	public GuiAccountSelector(Screen prev) {
		super(new TranslatableText("ias.selectaccount"));
		this.prev = prev;
	}

	@Override
	protected void init() {
		queriedaccounts = convertData();
		accountsgui = new GuiAccountSelector.List(this.minecraft);
	    children.add(accountsgui);
		addButton(reloadskins = new ButtonWidget(2, 2, 120, 20, I18n.translate("ias.reloadskins"), btn -> reloadSkins())); //8
		addButton(new ButtonWidget(this.width / 2 + 4 + 40, this.height - 52, 120, 20, I18n.translate("ias.addaccount"), btn -> add())); //0
		addButton(login = new ButtonWidget(this.width / 2 - 154 - 10, this.height - 52, 120, 20, I18n.translate("ias.login"), btn -> login(selectedAccountIndex))); //1
		addButton(edit = new ButtonWidget(this.width / 2 - 40, this.height - 52, 80, 20, I18n.translate("ias.edit"), btn -> edit())); //7
		addButton(loginoffline = new ButtonWidget(this.width / 2 - 154 - 10, this.height - 28, 110, 20, I18n.translate("ias.login") + " " + I18n.translate("ias.offline"), btn -> logino(selectedAccountIndex))); //2
		addButton(new ButtonWidget(this.width / 2 + 4 + 50, this.height - 28, 110, 20, I18n.translate("gui.cancel"), btn -> escape())); //3
		addButton(delete = new ButtonWidget(this.width / 2 - 50, this.height - 28, 100, 20, I18n.translate("ias.delete"), btn -> delete())); //4
		addButton(search = new TextFieldWidget(this.font, this.width / 2 - 80, 14, 160, 16, I18n.translate("ias.search")));
	    updateButtons();
	    updateShownSkin();
	}
	
	private void updateShownSkin() {
		if (!queriedaccounts.isEmpty()) SkinTools.buildSkin(queriedaccounts.get(selectedAccountIndex).alias());
	}

	@Override
	public void tick() {
		updateButtons();
		if (prevIndex != selectedAccountIndex) {
			prevIndex = selectedAccountIndex;
			updateShownSkin();
		}
		if (!prevQuery.equals(search.getText())) {
			updateQueried();
			prevQuery = search.getText();
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		accountsgui.mouseClicked(mouseX, mouseY, mouseButton);
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void removed() {
		Config.save();
		MicrosoftAccount.save(minecraft);
	}
	
	@Override
	public void render(int mx, int my, float delta) {
		renderBackground();
		accountsgui.render(mx, my, delta);
		this.drawCenteredString(font, this.title.asFormattedString(), this.width / 2, 4, -1);
		if (loginfailed != null) {
			this.drawCenteredString(font, loginfailed.getLocalizedMessage(), this.width / 2, this.height - 62, 16737380);
		}
		super.render(mx, my, delta);
		if (!queriedaccounts.isEmpty()) {
			SkinTools.javDrawSkin(8, height / 2 - 64 - 16, 64, 128);
			Tools.drawBorderedRect(width - 8 - 64, height / 2 - 64 - 16, width - 8, height / 2 + 64 - 16, 2, -5855578, -13421773);
			if (queriedaccounts.get(selectedAccountIndex) instanceof ExtendedAccountData) {
				ExtendedAccountData ead = (ExtendedAccountData) queriedaccounts.get(selectedAccountIndex);
				if (ead.premium != null) {
					if (ead.premium) this.drawString(font, I18n.translate("ias.premium"), width - 8 - 61, height / 2 - 64 - 13, 6618980);
					else this.drawString(font, I18n.translate("ias.notpremium"), width - 8 - 61, height / 2 - 64 - 13, 16737380);
				}
				this.drawString(font, I18n.translate("ias.timesused"), width - 8 - 61, height / 2 - 64 - 15 + 12, -1);
				this.drawString(font, String.valueOf(ead.useCount), width - 8 - 61, height / 2 - 64 - 15 + 21, -1);
				if (ead.useCount > 0) {
					this.drawString(font, I18n.translate("ias.lastused"), width - 8 - 61, height / 2 - 64 - 15 + 30, -1);
					this.drawString(font, JavaTools.getFormattedDate(), width - 8 - 61, height / 2 - 64 - 15 + 39, -1);
				}
			} else {
				this.drawString(font, I18n.translate("ias.premium"), width - 8 - 61, height / 2 - 64 - 13, 6618980);
			}
		}
	}

	/**
	 * Reload Skins
	 */
	private void reloadSkins() {
		Config.save();
		MicrosoftAccount.save(minecraft);
		SkinTools.cacheSkins(true);
		updateShownSkin();
	}

	/**
	 * Leave the gui
	 */
	private void escape() {
		minecraft.openScreen(prev);
	}

	/**
	 * Delete the selected account
	 */
	private void delete() {
		minecraft.openScreen(new ConfirmScreen(b -> {
			if (b) {
				AltDatabase.getInstance().getAlts().remove(getCurrentAsEditable());
				if (this.queriedaccounts.get(selectedAccountIndex) instanceof MicrosoftAccount) MicrosoftAccount.msaccounts.remove(this.queriedaccounts.get(selectedAccountIndex));
				if (selectedAccountIndex > 0) selectedAccountIndex--;
				updateQueried();
				updateButtons();
			}
			minecraft.openScreen(this);
		}, new TranslatableText("ias.delete.title"), new TranslatableText("ias.delete.text", queriedaccounts.get(selectedAccountIndex).alias())));
	}

	/**
	 * Add an account
	 */
	private void add() {
		minecraft.openScreen(new GuiAddAccount(this));
	}

	/**
	 * Login to the account in offline mode, then return to main menu
	 *
	 * @param selected The index of the account to log in to
	 */
	private void logino(int selected) {
		Account data = queriedaccounts.get(selected);
		AltManager.getInstance().setUserOffline(data.alias());
		loginfailed = null;
		ExtendedAccountData current = getCurrentAsEditable();
		if (current != null) {
			current.useCount++;
			current.lastused = JavaTools.getDate();
		}
	}

	/**
	 * Attempt login to the account, then return to main menu if successful
	 *
	 * @param selected The index of the account to log in to
	 */
	private void login(int selected) {
		Account data = queriedaccounts.get(selected);
		loginfailed = data.login();
		if (loginfailed == null) {
			ExtendedAccountData current = getCurrentAsEditable();
			if (current != null) {
				current.premium = true;
				current.useCount++;
				current.lastused = JavaTools.getDate();
			}
		} else if (loginfailed instanceof AlreadyLoggedInException) {
			getCurrentAsEditable().lastused = JavaTools.getDate();
		} else if (HttpTools.ping("http://minecraft.net")) {
			getCurrentAsEditable().premium = false;
		}
	}

	/**
	 * Edits the current account's information
	 */
	private void edit() {
		minecraft.openScreen(new GuiEditAccount(this, selectedAccountIndex));
	}

	private void updateQueried() {
		queriedaccounts = convertData();
		if (!search.getText().trim().isEmpty()) {
			for (int i = 0; i < queriedaccounts.size(); i++) {
				if (!queriedaccounts.get(i).alias().contains(search.getText()) && ConfigValues.CASESENSITIVE) {
					queriedaccounts.remove(i);
					i--;
				} else if (!queriedaccounts.get(i).alias().toLowerCase().contains(search.getText().toLowerCase())
						&& !ConfigValues.CASESENSITIVE) {
					queriedaccounts.remove(i);
					i--;
				}
			}
		}
		if (!queriedaccounts.isEmpty()) {
			while (selectedAccountIndex >= queriedaccounts.size()) {
				selectedAccountIndex--;
			}
		}
	}
	
	@Override
	public boolean keyPressed(int key, int oldkey, int mods) {
		if (key == GLFW.GLFW_KEY_UP && !queriedaccounts.isEmpty()) {
			if (selectedAccountIndex > 0) selectedAccountIndex--;
		} else if (key == GLFW.GLFW_KEY_DOWN && !queriedaccounts.isEmpty()) {
			if (selectedAccountIndex < queriedaccounts.size() - 1) selectedAccountIndex++;
		} else if (key == GLFW.GLFW_KEY_ESCAPE) {
			escape();
		} else if (key == GLFW.GLFW_KEY_DELETE && delete.active) {
			delete();
		} else if (key == GLFW.GLFW_KEY_ENTER && !search.isFocused() && (login.active || loginoffline.active)) {
			if (Screen.hasShiftDown() && loginoffline.active) {
				logino(selectedAccountIndex);
			} else {
				if (login.active) login(selectedAccountIndex);
			}
		} else if (key == GLFW.GLFW_KEY_F5) {
			reloadSkins();
		} else if (search.isFocused()) {
			if (key == GLFW.GLFW_KEY_ENTER) {
				search.setSelected(false); //setSelected()
				return true;
			}
		}
		return super.keyPressed(key, oldkey, mods);
	}
	
	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
	
	@Override
	public boolean charTyped(char charT, int mods) {
		if (charT == '+') {
			add();
		} else if (charT == '/' && edit.active) {
			edit();
		} else if (!search.isFocused() && (charT == 'r' || charT == 'R')) {
			reloadSkins();
		}
		return super.charTyped(charT, mods);
	}

	private ArrayList<Account> convertData() {
		@SuppressWarnings("unchecked")
		ArrayList<AccountData> tmp = (ArrayList<AccountData>) AltDatabase.getInstance().getAlts().clone();
		ArrayList<Account> converted = new ArrayList<>();
		int index = 0;
		for (AccountData data : tmp) {
			if (data instanceof ExtendedAccountData) {
				converted.add((ExtendedAccountData) data);
			} else {
				converted.add(new ExtendedAccountData(EncryptionTools.decode(data.user),
						EncryptionTools.decode(data.pass), data.alias));
				AltDatabase.getInstance().getAlts().set(index, new ExtendedAccountData(
						EncryptionTools.decode(data.user), EncryptionTools.decode(data.pass), data.alias));
			}
			index++;
		}
		converted.addAll(MicrosoftAccount.msaccounts);
		return converted;
	}

	private ArrayList<AccountData> getAccountList() {
		return AltDatabase.getInstance().getAlts();
	}

	private ExtendedAccountData getCurrentAsEditable() {
		for (AccountData dat : getAccountList()) {
			if (dat instanceof ExtendedAccountData) {
				if (((ExtendedAccountData)dat).equals(queriedaccounts.get(selectedAccountIndex))) {
					return (ExtendedAccountData) dat;
				}
			}
		}
		return null;
	}

	private void updateButtons() {
		login.active = !queriedaccounts.isEmpty() && (queriedaccounts.get(selectedAccountIndex) instanceof MicrosoftAccount || !EncryptionTools.decode(((ExtendedAccountData)queriedaccounts.get(selectedAccountIndex)).pass).equals(""));
		loginoffline.active = !queriedaccounts.isEmpty();
		delete.active = !queriedaccounts.isEmpty();
		edit.active = !queriedaccounts.isEmpty() && queriedaccounts.get(selectedAccountIndex) instanceof ExtendedAccountData;
		reloadskins.active = !queriedaccounts.isEmpty();
	}

	class List extends ListWidget {
		public List(MinecraftClient mcIn) {
			super(mcIn, GuiAccountSelector.this.width, GuiAccountSelector.this.height, 32, GuiAccountSelector.this.height - 64, 14);
		}
		
		@Override
		protected int getItemCount() {
			return GuiAccountSelector.this.queriedaccounts.size();
		}

		@Override
		protected boolean isSelectedItem(int i) {
			return i == GuiAccountSelector.this.selectedAccountIndex;
		}

		@Override
		protected void renderBackground() {
			GuiAccountSelector.this.renderBackground();
		}

		@Override
		protected void renderItem(int i1, int i2, int i3, int i4, int i5, int i6, float i7) {
			Account data = queriedaccounts.get(i1);
			String s = data.alias();
			if (StringUtils.isEmpty(s)) {
				s = I18n.translate("ias.alt") + " " + (i1 + 1);
			}
			int color = 16777215;
			if (MinecraftClient.getInstance().getSession().getUsername().equals(data.alias())) {
				color = 0x00FF00;
			}
			GuiAccountSelector.this.drawString(font, s, i2 + 2, i3 + 1, color);
		}
		
		@Override
		public int getItemHeight() {
			return GuiAccountSelector.this.queriedaccounts.size() * 14;
		}
		
		@Override
		protected boolean selectItem(int i, int i2, double mx, double my) {
			GuiAccountSelector.this.selectedAccountIndex = i;
			GuiAccountSelector.this.updateButtons();
			return super.selectItem(i, i2, mx, my);
		}
	}
}

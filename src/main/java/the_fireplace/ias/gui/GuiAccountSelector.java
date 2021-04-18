package the_fireplace.ias.gui;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

import com.github.mrebhan.ingameaccountswitcher.tools.Config;
import com.github.mrebhan.ingameaccountswitcher.tools.Tools;
import com.github.mrebhan.ingameaccountswitcher.tools.alt.AccountData;
import com.github.mrebhan.ingameaccountswitcher.tools.alt.AltDatabase;
import com.github.mrebhan.ingameaccountswitcher.tools.alt.AltManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import ru.vidtu.iasfork.IASConfigScreen;
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
public class GuiAccountSelector extends GuiScreen {
	private int selectedAccountIndex = 0;
	private int prevIndex = 0;
	private Throwable loginfailed;
	private ArrayList<ExtendedAccountData> queriedaccounts = convertData();
	private GuiAccountSelector.List accountsgui;
	// Buttons that can be disabled need to be here
	private GuiButton login;
	private GuiButton loginoffline;
	private GuiButton delete;
	private GuiButton edit;
	private GuiButton reloadskins;
	// Search
	private String prevQuery = "";
	private GuiTextField search;
  
	@Override
	public void initGui() {
		accountsgui = new GuiAccountSelector.List(this.mc);
	    children.add(accountsgui);
		addButton(reloadskins = new GuiButton(8, 2, 2, 120, 20, I18n.format("ias.reloadskins")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				reloadSkins();
			}
		});
		addButton(new GuiButton(0, this.width / 2 + 4 + 40, this.height - 52, 120, 20, I18n.format("ias.addaccount")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				add();
			}
		});
		addButton(login = new GuiButton(1, this.width / 2 - 154 - 10, this.height - 52, 120, 20, I18n.format("ias.login")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				login(selectedAccountIndex);
			}
		});
		addButton(edit = new GuiButton(7, this.width / 2 - 40, this.height - 52, 80, 20, I18n.format("ias.edit")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				edit();
			}
		});
		addButton(loginoffline = new GuiButton(2, this.width / 2 - 154 - 10, this.height - 28, 110, 20, I18n.format("ias.login") + " " + I18n.format("ias.offline")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				logino(selectedAccountIndex);
			}
		});
		addButton(new GuiButton(3, this.width / 2 + 4 + 50, this.height - 28, 110, 20, I18n.format("gui.cancel")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				escape();
			}
		});
		addButton(delete = new GuiButton(4, this.width / 2 - 50, this.height - 28, 100, 20, I18n.format("ias.delete")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				delete();
			}
		});
		search = new GuiTextField(-1, this.fontRenderer, this.width / 2 - 80, 14, 160, 16) {
			@Override
			public void drawTextField(int mouseX, int mouseY, float partialTicks) {
				super.drawTextField(mouseX, mouseY, partialTicks);
				if (getText().isEmpty()) {
					fontRenderer.drawString(I18n.format("ias.search"), this.x, this.y, -1);
				}
			}
		};
		
		//Forge 1.13 doesn't have support for "Config" button in mods list.
		addButton(new GuiButton(999, this.width - 52, 2, 50, 20, "Config") {
			@Override
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(new IASConfigScreen(GuiAccountSelector.this));
			}
		});
		
	    updateButtons();
	    updateShownSkin();
	}
	
	private void updateShownSkin() {
		if (!queriedaccounts.isEmpty()) SkinTools.buildSkin(queriedaccounts.get(selectedAccountIndex).alias);
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
	public void onGuiClosed() {
		Config.save();
	}
	
	@Override
	public void render(int mx, int my, float delta) {
		drawDefaultBackground();
		accountsgui.drawScreen(mx, my, delta);
		this.drawCenteredString(fontRenderer, I18n.format("ias.selectaccount"), this.width / 2, 4, -1);
		if (loginfailed != null) {
			this.drawCenteredString(fontRenderer, loginfailed.getLocalizedMessage(), this.width / 2, this.height - 62, 16737380);
		}
		super.render(mx, my, delta);
		if (!queriedaccounts.isEmpty()) {
			SkinTools.javDrawSkin(8, height / 2 - 64 - 16, 64, 128);
			Tools.drawBorderedRect(width - 8 - 64, height / 2 - 64 - 16, width - 8, height / 2 + 64 - 16, 2, -5855578, -13421773);
			if (queriedaccounts.get(selectedAccountIndex).premium != null) {
				if (queriedaccounts.get(selectedAccountIndex).premium) this.drawString(fontRenderer, I18n.format("ias.premium"), width - 8 - 61, height / 2 - 64 - 13, 6618980);
				else this.drawString(fontRenderer, I18n.format("ias.notpremium"), width - 8 - 61, height / 2 - 64 - 13, 16737380);
			}
			this.drawString(fontRenderer, I18n.format("ias.timesused"), width - 8 - 61, height / 2 - 64 - 15 + 12, -1);
			this.drawString(fontRenderer, String.valueOf(queriedaccounts.get(selectedAccountIndex).useCount), width - 8 - 61, height / 2 - 64 - 15 + 21, -1);
			if (queriedaccounts.get(selectedAccountIndex).useCount > 0) {
				this.drawString(fontRenderer, I18n.format("ias.lastused"), width - 8 - 61, height / 2 - 64 - 15 + 30, -1);
				this.drawString(fontRenderer, JavaTools.getFormattedDate(), width - 8 - 61, height / 2 - 64 - 15 + 39, -1);
			}
		}
	}

	/**
	 * Reload Skins
	 */
	private void reloadSkins() {
		Config.save();
		SkinTools.cacheSkins(true);
		updateShownSkin();
	}

	/**
	 * Leave the gui
	 */
	private void escape() {
		mc.displayGuiScreen(null);
	}

	/**
	 * Delete the selected account
	 */
	private void delete() {
		AltDatabase.getInstance().getAlts().remove(getCurrentAsEditable());
		if (selectedAccountIndex > 0) selectedAccountIndex--;
		updateQueried();
		updateButtons();
	}

	/**
	 * Add an account
	 */
	private void add() {
		mc.displayGuiScreen(new GuiAddAccount());
	}

	/**
	 * Login to the account in offline mode, then return to main menu
	 *
	 * @param selected The index of the account to log in to
	 */
	private void logino(int selected) {
		ExtendedAccountData data = queriedaccounts.get(selected);
		AltManager.getInstance().setUserOffline(data.alias);
		loginfailed = null;
		mc.displayGuiScreen(null);
		ExtendedAccountData current = getCurrentAsEditable();
		current.useCount++;
		current.lastused = JavaTools.getDate();
	}

	/**
	 * Attempt login to the account, then return to main menu if successful
	 *
	 * @param selected The index of the account to log in to
	 */
	private void login(int selected) {
		ExtendedAccountData data = queriedaccounts.get(selected);
		loginfailed = AltManager.getInstance().setUser(data.user, data.pass);
		if (loginfailed == null) {
			mc.displayGuiScreen(null);
			ExtendedAccountData current = getCurrentAsEditable();
			current.premium = true;
			current.useCount++;
			current.lastused = JavaTools.getDate();
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
		mc.displayGuiScreen(new GuiEditAccount(selectedAccountIndex));
	}

	private void updateQueried() {
		queriedaccounts = convertData();
		if (!search.getText().trim().isEmpty()) {
			for (int i = 0; i < queriedaccounts.size(); i++) {
				if (!queriedaccounts.get(i).alias.contains(search.getText()) && ConfigValues.CASESENSITIVE) {
					queriedaccounts.remove(i);
					i--;
				} else if (!queriedaccounts.get(i).alias.toLowerCase().contains(search.getText().toLowerCase())
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
			return true;
		} else if (key == GLFW.GLFW_KEY_DELETE && delete.enabled) {
			delete();
		} else if (key == GLFW.GLFW_KEY_ENTER && !search.isFocused() && (login.enabled || loginoffline.enabled)) {
			if (GuiScreen.isShiftKeyDown() && loginoffline.enabled) {
				logino(selectedAccountIndex);
			} else {
				if (login.enabled) login(selectedAccountIndex);
			}
		} else if (key == GLFW.GLFW_KEY_F5) {
			reloadSkins();
		} else if (search.isFocused()) {
			if (key == GLFW.GLFW_KEY_ENTER) {
				search.setFocused(false);
				return true;
			}
		}
		return super.keyPressed(key, oldkey, mods);
	}
	
	@Override
	public boolean charTyped(char charT, int mods) {
		if (charT == '+') {
			add();
		} else if (charT == '/' && edit.enabled) {
			edit();
		} else if (!search.isFocused() && (charT == 'r' || charT == 'R')) {
			reloadSkins();
		}
		return super.charTyped(charT, mods);
	}

	private ArrayList<ExtendedAccountData> convertData() {
		@SuppressWarnings("unchecked")
		ArrayList<AccountData> tmp = (ArrayList<AccountData>) AltDatabase.getInstance().getAlts().clone();
		ArrayList<ExtendedAccountData> converted = new ArrayList<>();
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
		return converted;
	}

	private ArrayList<AccountData> getAccountList() {
		return AltDatabase.getInstance().getAlts();
	}

	private ExtendedAccountData getCurrentAsEditable() {
		for (AccountData dat : getAccountList()) {
			if (dat instanceof ExtendedAccountData) {
				if (dat.equals(queriedaccounts.get(selectedAccountIndex))) {
					return (ExtendedAccountData) dat;
				}
			}
		}
		return null;
	}

	private void updateButtons() {
		login.enabled = !queriedaccounts.isEmpty() && !EncryptionTools.decode(queriedaccounts.get(selectedAccountIndex).pass).equals("");
		loginoffline.enabled = !queriedaccounts.isEmpty();
		delete.enabled = !queriedaccounts.isEmpty();
		edit.enabled = !queriedaccounts.isEmpty();
		reloadskins.enabled = !AltDatabase.getInstance().getAlts().isEmpty();
	}

	class List extends GuiSlot {
		public List(Minecraft mcIn) {
			super(mcIn, GuiAccountSelector.this.width, GuiAccountSelector.this.height, 32, GuiAccountSelector.this.height - 64, 14);
		}
		
		@Override
		protected int getSize() {
			return GuiAccountSelector.this.queriedaccounts.size();
		}

		@Override
		protected boolean isSelected(int i) {
			return i == GuiAccountSelector.this.selectedAccountIndex;
		}

		@Override
		protected void drawBackground() {
			GuiAccountSelector.this.drawDefaultBackground();
		}

		@Override
		protected void drawSlot(int i1, int i2, int i3, int i4, int i5, int i6, float i7) {
			ExtendedAccountData data = queriedaccounts.get(i1);
			String s = data.alias;
			if (StringUtils.isEmpty(s)) {
				s = I18n.format("ias.alt") + " " + (i1 + 1);
			}
			int color = 16777215;
			if (Minecraft.getInstance().getSession().getUsername().equals(data.alias)) {
				color = 0x00FF00;
			}
			GuiAccountSelector.this.drawString(fontRenderer, s, i2 + 2, i3 + 1, color);
		}
		
		@Override
		protected int getContentHeight() {
			return GuiAccountSelector.this.queriedaccounts.size() * 14;
		}
		
		@Override
		public void setSelectedEntry(int i) {
			GuiAccountSelector.this.selectedAccountIndex = i;
			GuiAccountSelector.this.updateButtons();
		}
	}
}

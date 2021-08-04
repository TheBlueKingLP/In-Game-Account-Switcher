package the_fireplace.ias.gui;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import com.github.mrebhan.ingameaccountswitcher.tools.Config;
import com.github.mrebhan.ingameaccountswitcher.tools.Tools;
import com.github.mrebhan.ingameaccountswitcher.tools.alt.AccountData;
import com.github.mrebhan.ingameaccountswitcher.tools.alt.AltDatabase;
import com.github.mrebhan.ingameaccountswitcher.tools.alt.AltManager;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import ru.vidtu.iasfork.SlotGui;
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
	public Screen prev;
	private int selectedAccountIndex = 0;
	private int prevIndex = 0;
	private Throwable loginfailed;
	private ArrayList<Account> queriedaccounts = convertData();
	private GuiAccountSelector.List accountsgui;
	// Buttons that can be disabled need to be here
	private Button login;
	private Button loginoffline;
	private Button delete;
	private Button edit;
	private Button reloadskins;
	// Search
	private String prevQuery = "";
	private EditBox search;
	
	public GuiAccountSelector(Screen prev) {
		super(new TranslatableComponent("ias.selectaccount"));
		this.prev = prev;
	}
  
	@Override
	protected void init() {
		queriedaccounts = convertData();
		accountsgui = new GuiAccountSelector.List(this.minecraft);
	    addRenderableWidget(accountsgui);
		addRenderableWidget(reloadskins = new Button(2, 2, 120, 20, new TranslatableComponent("ias.reloadskins"), btn -> reloadSkins())); //8
		addRenderableWidget(new Button(this.width / 2 + 4 + 40, this.height - 52, 120, 20, new TranslatableComponent("ias.addaccount"), btn -> add())); //0
		addRenderableWidget(login = new Button(this.width / 2 - 154 - 10, this.height - 52, 120, 20, new TranslatableComponent("ias.login"), btn -> login(selectedAccountIndex))); //1
		addRenderableWidget(edit = new Button(this.width / 2 - 40, this.height - 52, 80, 20, new TranslatableComponent("ias.edit"), btn -> edit())); //7
		addRenderableWidget(loginoffline = new Button(this.width / 2 - 154 - 10, this.height - 28, 110, 20, new TranslatableComponent("ias.login").append(" ").append(new TranslatableComponent("ias.offline")), btn -> logino(selectedAccountIndex))); //2
		addRenderableWidget(new Button(this.width / 2 + 4 + 50, this.height - 28, 110, 20, new TranslatableComponent("gui.cancel"), btn -> escape())); //3
		addRenderableWidget(delete = new Button(this.width / 2 - 50, this.height - 28, 100, 20, new TranslatableComponent("ias.delete"), btn -> delete())); //4
		addRenderableWidget(search = new EditBox(this.font, this.width / 2 - 80, 14, 160, 16, new TranslatableComponent("ias.search")));
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
		if (!prevQuery.equals(search.getValue())) {
			updateQueried();
			prevQuery = search.getValue();
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
	public void render(PoseStack ms, int mx, int my, float delta) {
		renderBackground(ms);
		accountsgui.render(ms, mx, my, delta);
		drawCenteredString(ms, font, this.title, this.width / 2, 4, -1);
		if (loginfailed != null) {
			drawCenteredString(ms, font, loginfailed.getLocalizedMessage(), this.width / 2, this.height - 62, 16737380);
		}
		super.render(ms, mx, my, delta);
		if (!queriedaccounts.isEmpty()) {
			SkinTools.javDrawSkin(ms, 8, height / 2 - 64 - 16, 64, 128);
			Tools.drawBorderedRect(ms, width - 8 - 64, height / 2 - 64 - 16, width - 8, height / 2 + 64 - 16, 2, -5855578, -13421773);
			if (queriedaccounts.get(selectedAccountIndex) instanceof ExtendedAccountData) {
				ExtendedAccountData ead = (ExtendedAccountData) queriedaccounts.get(selectedAccountIndex);
				if (ead.premium != null) {
					if (ead.premium) drawString(ms, font, new TranslatableComponent("ias.premium"), width - 8 - 61, height / 2 - 64 - 13, 6618980);
					else drawString(ms, font, new TranslatableComponent("ias.notpremium"), width - 8 - 61, height / 2 - 64 - 13, 16737380);
				}
				drawString(ms, font, new TranslatableComponent("ias.timesused"), width - 8 - 61, height / 2 - 64 - 15 + 12, -1);
				drawString(ms, font, String.valueOf(ead.useCount), width - 8 - 61, height / 2 - 64 - 15 + 21, -1);
				if (ead.useCount > 0) {
					drawString(ms, font, new TranslatableComponent("ias.lastused"), width - 8 - 61, height / 2 - 64 - 15 + 30, -1);
					drawString(ms, font, JavaTools.getFormattedDate(), width - 8 - 61, height / 2 - 64 - 15 + 39, -1);
				}
			} else {
				drawString(ms, font, new TranslatableComponent("ias.premium"), width - 8 - 61, height / 2 - 64 - 13, 6618980);
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
		minecraft.setScreen(prev);
	}

	/**
	 * Delete the selected account
	 */
	private void delete() {
		minecraft.setScreen(new ConfirmScreen(b -> {
			if (b) {
				AltDatabase.getInstance().getAlts().remove(getCurrentAsEditable());
				if (this.queriedaccounts.get(selectedAccountIndex) instanceof MicrosoftAccount) MicrosoftAccount.msaccounts.remove(this.queriedaccounts.get(selectedAccountIndex));
				if (selectedAccountIndex > 0) selectedAccountIndex--;
				updateQueried();
				updateButtons();
			}
			minecraft.setScreen(this);
		}, new TranslatableComponent("ias.delete.title"), new TranslatableComponent("ias.delete.text", queriedaccounts.get(selectedAccountIndex).alias())));
	}

	/**
	 * Add an account
	 */
	private void add() {
		minecraft.setScreen(new GuiAddAccount(this));
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
		minecraft.setScreen(null);
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
		minecraft.setScreen(new GuiEditAccount(this, selectedAccountIndex));
	}

	private void updateQueried() {
		queriedaccounts = convertData();
		if (!search.getValue().trim().isEmpty()) {
			for (int i = 0; i < queriedaccounts.size(); i++) {
				if (!queriedaccounts.get(i).alias().contains(search.getValue()) && ConfigValues.CASESENSITIVE) {
					queriedaccounts.remove(i);
					i--;
				} else if (!queriedaccounts.get(i).alias().toLowerCase().contains(search.getValue().toLowerCase())
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
				search.setFocus(false);
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

	class List extends SlotGui {
		public List(Minecraft mcIn) {
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
		protected void renderBackground(PoseStack ms) {
			GuiAccountSelector.this.renderBackground(ms);
		}

		@Override
		protected void renderItem(PoseStack ms, int i1, int i2, int i3, int i4, int i5, int i6, float i7) {
			Account data = queriedaccounts.get(i1);
			Component s = new TextComponent(data.alias());
			if (s.getString().isEmpty()) {
				s = new TranslatableComponent("ias.alt").append(" ").append(String.valueOf(i1 + 1));
			}
			int color = 16777215;
			if (Minecraft.getInstance().getUser().getName().equals(data.alias())) {
				color = 0x00FF00;
			}
			drawString(ms, font, s, i2 + 2, i3 + 1, color);
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

package the_fireplace.ias.gui;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;
/**
 * The button with the image on it.
 * @author The_Fireplace
 */
public class GuiButtonWithImage extends ButtonWidget {
	private static final Identifier customButtonTextures = new Identifier("ias:textures/gui/custombutton.png");

	public GuiButtonWithImage(int x, int y, int widthIn,
			int heightIn, String buttonText, PressAction p) {
		super(x, y, widthIn, heightIn, buttonText, p);
	}
	
	@Override
	public void renderButton(int mouseX, int mouseY, float delta) {
		if (this.visible) {
			MinecraftClient mc = MinecraftClient.getInstance();
			TextRenderer fontrenderer = mc.textRenderer;
			mc.getTextureManager().bindTexture(customButtonTextures);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			int k = this.isHovered?2:1;
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(770, 771);
			blit(this.x, this.y, 0, 46 + k * 20, this.width / 2, this.height);
			blit(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
			int l = 14737632;

			if (!this.active)
			{
				l = 10526880;
			}
			else if (this.isHovered)
			{
				l = 16777120;
			}

			this.drawCenteredString(fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, l);
		}
	}
}

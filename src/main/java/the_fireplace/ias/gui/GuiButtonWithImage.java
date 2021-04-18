package the_fireplace.ias.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
/**
 * The button with the image on it.
 * @author The_Fireplace
 */
public class GuiButtonWithImage extends ButtonWidget {
	private static final Identifier customButtonTextures = new Identifier("ias:textures/gui/custombutton.png");

	public GuiButtonWithImage(int x, int y, int widthIn,
			int heightIn, Text buttonText, PressAction p) {
		super(x, y, widthIn, heightIn, buttonText, p);
	}
	
	@Override
	public void renderButton(MatrixStack ms, int mouseX, int mouseY, float delta) {
		if (this.visible) {
			MinecraftClient mc = MinecraftClient.getInstance();
			TextRenderer fontrenderer = mc.textRenderer;
			mc.getTextureManager().bindTexture(customButtonTextures);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			int k = this.hovered?2:1;
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(770, 771);
			drawTexture(ms, this.x, this.y, 0, 46 + k * 20, this.width / 2, this.height);
			drawTexture(ms, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
			int l = 14737632;

			if (!this.active)
			{
				l = 10526880;
			}
			else if (this.hovered)
			{
				l = 16777120;
			}
			drawCenteredText(ms, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, l);
		}
	}
}

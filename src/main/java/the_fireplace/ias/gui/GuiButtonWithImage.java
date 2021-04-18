package the_fireplace.ias.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
/**
 * The button with the image on it.
 * @author The_Fireplace
 */
public class GuiButtonWithImage extends Button {

	private static final ResourceLocation customButtonTextures = new ResourceLocation("ias:textures/gui/custombutton.png");
	public GuiButtonWithImage(int x, int y, int widthIn,
			int heightIn, ITextComponent buttonText, IPressable p) {
		super(x, y, widthIn, heightIn, buttonText, p);
	}
	
	@Override
	public void renderButton(MatrixStack ms, int mouseX, int mouseY, float delta) {
		if (this.visible) {
			Minecraft mc = Minecraft.getInstance();
			FontRenderer fontrenderer = mc.font;
			mc.getTextureManager().bind(customButtonTextures);
			this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			int k = this.isHovered?2:1;
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(770, 771, 1, 0);
			RenderSystem.blendFunc(770, 771);
			blit(ms, this.x, this.y, 0, 46 + k * 20, this.width / 2, this.height);
			blit(ms, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
			int l = 14737632;

			if (!this.active)
			{
				l = 10526880;
			}
			else if (this.isHovered)
			{
				l = 16777120;
			}

			drawCenteredString(ms, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, l);
		}
	}
}

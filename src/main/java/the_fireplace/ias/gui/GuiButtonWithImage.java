package the_fireplace.ias.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
/**
 * The button with the image on it.
 * @author The_Fireplace
 */
public class GuiButtonWithImage extends GuiButton {

	private static final ResourceLocation customButtonTextures = new ResourceLocation("ias:textures/gui/custombutton.png");
	public Runnable r;
	public GuiButtonWithImage(int x, int y, int widthIn, int heightIn, String buttonText, Runnable r) {
		super("1.13isbad".hashCode(), x, y, widthIn, heightIn, buttonText);
		this.r = r;
	}
	
	@Override
	public void onClick(double mouseX, double mouseY) {
		r.run();
	}
	
	@Override
	public void render(int mouseX, int mouseY, float delta) {
		if (this.visible) {
			Minecraft mc = Minecraft.getInstance();
			FontRenderer fontrenderer = mc.fontRenderer;
			mc.getTextureManager().bindTexture(customButtonTextures);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			int k = this.hovered?2:1;
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(770, 771);
			drawTexturedModalRect(this.x, this.y, 0, 46 + k * 20, this.width / 2, this.height);
			drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
			int l = 14737632;

			if (!this.enabled)
			{
				l = 10526880;
			}
			else if (this.hovered)
			{
				l = 16777120;
			}

			this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, l);
		}
	}
}

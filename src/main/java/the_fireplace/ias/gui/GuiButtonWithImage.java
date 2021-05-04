package the_fireplace.ias.gui;

import net.minecraft.client.Minecraft;
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
	public GuiButtonWithImage(int x, int y, Runnable r) {
		super("1.13isbad".hashCode(), x, y, 20, 20, "");
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
			mc.getTextureManager().bindTexture(customButtonTextures);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			int k = getHoverState(hovered);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(770, 771);
			drawTexturedModalRect(this.x, this.y, 0, k * 20, 20, 20);
		}
	}
}

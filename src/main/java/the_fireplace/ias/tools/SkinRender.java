package the_fireplace.ias.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/**
 * Takes care of loading and drawing images to the screen. Adapted from http://www.minecraftforge.net/forum/index.php?topic=11991.0
 * @author dayanto
 * @author The_Fireplace
 */
public class SkinRender
{
	private final File file;
	private NativeImageBackedTexture previewTexture;
	private Identifier resourceLocation;
	private final TextureManager textureManager;

	public SkinRender(TextureManager textureManager, File file)
	{
		this.textureManager = textureManager;
		this.file = file;
	}

	/**
	 * Attempts to load the image. Returns whether it was successful or not.
	 */
	private boolean loadPreview()
	{
		try {
			FileInputStream fis = new FileInputStream(file);
			NativeImage ni = NativeImage.read(fis);
			fis.close();
			previewTexture = new NativeImageBackedTexture(ni);
			resourceLocation = textureManager.registerDynamicTexture("ias", previewTexture);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void drawImage(MatrixStack ms, int xPos, int yPos, int width, int height)
	{
		if(previewTexture == null) {
			boolean successful = loadPreview();
			if(!successful){
				System.out.println("Failure to load preview.");
				return;
			}
		}
		previewTexture.upload();

		textureManager.bindTexture(resourceLocation);
		RenderSystem.color3f(1F, 1F, 1F);
		Screen.drawTexture(ms, xPos, yPos, 0, 0, width, height, 16*4, 32*4);
	}
}
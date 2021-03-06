package universalcoins.render;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import universalcoins.tile.TileVendorFrame;

public class VendorFrameRenderer extends TileEntitySpecialRenderer {

	// The model of your block
	private final ModelVendorFrame model;

	RenderItem renderer = new RenderItem();
    private final RenderBlocks renderBlocks = new RenderBlocks();


	public VendorFrameRenderer() {
		this.model = new ModelVendorFrame();
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
		//TODO change texture based on plank type
		ResourceLocation textures = (new ResourceLocation("textures/blocks/planks_birch.png"));
		Minecraft.getMinecraft().renderEngine.bindTexture(textures);

		// adjust block rotation based on block meta
		int meta = te.blockMetadata;
		if (meta == -1) { // fix for inventory crash on get block meta
			try {
				meta = te.getBlockMetadata();
			} catch (Throwable ex2) {
				// do nothing
			}
		}

		//render block
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
		GL11.glRotatef(meta * 90, 0.0F, 1.0F, 0.0F);
		this.model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		
		//render trade item or block
		ItemStack itemstack = ((TileVendorFrame) te).getSellItem();
		if (itemstack != null) {
			ItemStack visStack = itemstack.copy();
			visStack.stackSize = 1;
			EntityItem entityitem = new EntityItem(null, 0.0D, 0.0D, 0.0D, visStack);
            entityitem.hoverStart = 0.0F;
            GL11.glPushMatrix();
            GL11.glRotatef(180, 1.0F, 0.0F, 0.0F);
            renderer.renderInFrame = true;
            RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, -1.2D, -0.48D, 0F, 0F);
            renderer.renderInFrame = false;
            GL11.glPopMatrix();
        }
		GL11.glPopMatrix();
	}
	
	// Set the lighting stuff, so it changes it's brightness properly.
	private void adjustLightFixture(World world, int i, int j, int k,
			Block block) {
		Tessellator tess = Tessellator.instance;
		float brightness = block.getLightValue(world, i, j, k);
		int skyLight = world.getLightBrightnessForSkyBlocks(i, j, k, 0);
		int modulousModifier = skyLight % 65536;
		int divModifier = skyLight / 65536;
		tess.setColorOpaque_F(brightness, brightness, brightness);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
				(float) modulousModifier, divModifier);
	}
}

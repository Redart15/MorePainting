package redart15.morepainting.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.render.renderer.BlendFactor;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.Shaders;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.core.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;
import redart15.morepainting.core.interfaces.PaintingIndex;

import java.util.ArrayList;
import java.util.List;


@Environment(EnvType.CLIENT)
public class ScreenBetterPaintPicker extends Screen{
	private final Player player;
	private Index centerArtIndex;
	private final List<IndexEntry> indexEntries = new ArrayList<>();
	private int smallestSize;

	private record IndexEntry(Index index, int x, int y, float alpha) {}

	public ScreenBetterPaintPicker(Player player) {
		this.centerArtIndex = Index.getDefaultIndex();
		this.smallestSize = 0;
		this.player = player;
		this.smallestSize = ArtTypes.getSmallest();
	}

	@Override
	public void init() {
		super.init();
		Index index = ((PaintingIndex) player).morepainting$getIndex();
		// if index null, no update needed
		if (index != null) {
			// if nothing is at that index, get next valid picture
			if (ArtTypes.getArt(index) == null) {
				this.centerArtIndex = ArtTypes.getNext(index);
			} else {
				// otherwise update the index
				((PaintingIndex) player).morepainting$setIndex(index);
			}
		}
		this.updateArtEntries();
	}

	private void updateArtEntries() {
		int centerX = this.width / 2;
		int centerY = this.height / 2;
		int scale = getScale(this.width, this.height);
		int offsetX = 32 * scale;
		int artGap = 2;
		this.indexEntries.clear();
		this.indexEntries.add(new IndexEntry(this.centerArtIndex, centerX - offsetX, centerY - ((this.centerArtIndex.getY() * scale) / 2), 1.0f));
		Index currentArtIndex = ArtTypes.getNext(this.centerArtIndex);
		int dy = ((this.centerArtIndex.getY() * scale) / 2 + artGap) + 28;
		while (!currentArtIndex.equals(this.centerArtIndex)) {
			int pCenterY = dy;
			float alpha = 1 - (float) pCenterY / (this.height / 2f);
			this.indexEntries.add(new IndexEntry(currentArtIndex, centerX - offsetX, centerY + dy, alpha * 0.75f));
			dy += ((currentArtIndex.getY() * scale) + artGap);
			currentArtIndex = ArtTypes.getNext(currentArtIndex);
		}

		currentArtIndex = ArtTypes.getPrev(this.centerArtIndex);
		dy = -((this.centerArtIndex.getY() * scale) / 2);
		while (!currentArtIndex.equals(this.centerArtIndex)) {
			dy -= ((currentArtIndex.getY() * scale) + artGap);
			int pCenterY = dy + currentArtIndex.getY() * scale;
			float alpha = 1 - (float) -pCenterY / (this.height / 2f);
			this.indexEntries.add(new IndexEntry(currentArtIndex, centerX - offsetX, centerY + dy, alpha * 0.75f));
			currentArtIndex = ArtTypes.getPrev(currentArtIndex);
		}
	}

	@Override
	public void render(int mx, int my, float partialTick) {
		super.render(mx, my, partialTick);
		float dWheel = Mouse.getDWheel();
		if (dWheel < 0.0F) {
			this.centerArtIndex = ArtTypes.getNext(this.centerArtIndex);
			this.updateArtEntries();
		} else if (dWheel > 0.0F) {
			this.centerArtIndex = ArtTypes.getPrev(this.centerArtIndex);
			this.updateArtEntries();
		}
		this.drawPaintings(this.width, this.height, mx, my);
	}

	@Override
	public void mouseClicked(int mx, int my, int buttonNum) {
		super.mouseClicked(mx, my, buttonNum);
		int scale = this.getScale(this.width, this.height);
		if (buttonNum == 0) {
			for(IndexEntry indexEntry : this.indexEntries) {
				Index index = indexEntry.index();
				if (mx >= indexEntry.x && mx <= indexEntry.x + index.getX() * scale && my >= indexEntry.y && my <= indexEntry.y + index.getY() * scale) {
					((PaintingIndex) player).morepainting$setIndex(index);
					this.mc.displayScreen(null);
					break;
				}
			}
		}

	}

	private void drawPaintings(int screenWidth, int screenHeight, int mouseX, int mouseY) {
		int centerX = screenWidth / 2;
		int centerY = screenHeight / 2;
		int scale = this.getScale(screenWidth, screenHeight);
		int offsetX = 32 * scale;
		ArtTypes.Art art = ArtTypes.getArt(this.centerArtIndex);
		this.drawStringShadow(this.fontRenderer, art.title(), centerX - offsetX, centerY + this.centerArtIndex.getY() / 2 * scale + 4, -1);
		this.drawStringShadow(this.fontRenderer, art.artist(), centerX - offsetX, centerY + this.centerArtIndex.getY() / 2 * scale + 16, 2139062143);
		GLRenderer.pushFrame();
		GLRenderer.enableState(State.BLEND);
		GLRenderer.setBlendFunc(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA);

		for(IndexEntry entry : this.indexEntries) {
			if (entry.alpha <= 0.0F) {
				continue;
			}
			GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			Index index = entry.index();
			if (mouseX > entry.x && mouseX < entry.x + index.getX() * scale && mouseY > entry.y && mouseY < entry.y + index.getY() * scale) {
				GLRenderer.setShader(Shaders.COLOR);
				this.drawRectWidthHeight(entry.x - scale, entry.y - scale, (index.getX() + 2) * scale, (index.getY() + 2) * scale, -1);
				GLRenderer.setShader(Shaders.INTERFACE);
				this.drawPainting(index, entry.x, entry.y, index.getX(), index.getY(), 1.0F);
			} else {
				this.drawPainting(index, entry.x, entry.y, index.getX(), index.getY(), entry.alpha);
			}
		}

		GLRenderer.popFrame();
	}

	private void drawPainting(Index index, int x, int y, int width, int height, float alpha) {
		int scale = this.getScale(this.width, this.height);
		IconCoordinate texture = ArtTypes.getArt(index).getTexture();
		this.drawTexturedIconPainting(x, y, width * scale, height * scale, texture, alpha);
	}

	public void drawTexturedIconPainting(int x, int y, int width, int height, @NotNull IconCoordinate coordinate, float alpha) {
		coordinate.parentAtlas.bind();
		TessellatorGeneral tessellator = GLRenderer.getTessellator();
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, alpha);
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, (double) y + height, this.zLevel, coordinate.getIconUMin(), coordinate.getIconVMax());
		tessellator.addVertexWithUV((double)x + width, (double)y + height, this.zLevel, coordinate.getIconUMax(), coordinate.getIconVMax());
		tessellator.addVertexWithUV((double)x + width, y, this.zLevel, coordinate.getIconUMax(), coordinate.getIconVMin());
		tessellator.addVertexWithUV(x, y, this.zLevel, coordinate.getIconUMin(), coordinate.getIconVMin());
		tessellator.draw();
	}

	public int getScale(int width, int height) {
		return Math.min((int)(height * 0.9F / this.smallestSize), this.mc.resolution.getScale());
	}
}

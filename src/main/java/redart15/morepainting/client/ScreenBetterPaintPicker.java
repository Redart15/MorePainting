package redart15.morepainting.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.core.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import redart15.morepainting.core.interfaces.PaintingIndex;

import java.util.ArrayList;
import java.util.List;


@Environment(EnvType.CLIENT)
public class ScreenBetterPaintPicker extends Screen{
	public static final int ART_GAP = 2;
	private final Player player;
	private Index centerArtIndex;
	private float centerScale;
	private final List<IndexEntry> indexEntries = new ArrayList<>();

	public ScreenBetterPaintPicker(Player player) {
		this.centerArtIndex = Index.getDefaultIndex();
		this.player = player;
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
				this.centerArtIndex = index;
			}
			((PaintingIndex) player).morepainting$setIndex(this.centerArtIndex);
		}
		this.updateArtEntries();
	}

	private void updateArtEntries() {
		// flush list
		this.indexEntries.clear();
		// calc center
		int centerX = this.width / 2;
		int centerY = this.height / 2;
		// center is the first painting in list
		this.centerScale = this.getScaleF(this.centerArtIndex.getX(), this.centerArtIndex.getY());
		int centerWidth = Math.round(this.centerArtIndex.getX() * centerScale);
		int centerHeight = Math.round(this.centerArtIndex.getY() * centerScale);
		int x = centerX - Math.round(centerWidth / 2.0f);
		int y = centerY - Math.round(centerHeight / 2.0f);
		this.indexEntries.add(new IndexEntry(this.centerArtIndex, x, y, 1.0f, centerScale));
		// max iteration to prevent out of memory exceptions
		int maxInterations = ArtTypes.getTotalArtAmount();
		// get next index, forward list
		Index currentArtIndex = ArtTypes.getNext(this.centerArtIndex);
		float dy = ((this.centerArtIndex.getY() * centerScale) / 2f + ART_GAP) + 28;
		while (!currentArtIndex.equals(this.centerArtIndex) && maxInterations-- > 0) {
			float entryScale = this.getScaleF(currentArtIndex.getX(), currentArtIndex.getY());
			float alpha = 1 - Math.round(dy) / (this.height / 2f);
			int cx = centerX - Math.round(currentArtIndex.getX() * entryScale / 2.0f);
			int cy = centerY + Math.round(dy);
			this.indexEntries.add(new IndexEntry(currentArtIndex, cx, cy, alpha * 0.75f, entryScale));
			dy += ((currentArtIndex.getY() * entryScale) + ART_GAP);
			currentArtIndex = ArtTypes.getNext(currentArtIndex);
		}
		// reset
		maxInterations = ArtTypes.getTotalArtAmount();
		// get prev index, reverse list
		currentArtIndex = ArtTypes.getPrev(this.centerArtIndex);
		dy = -((this.centerArtIndex.getY() * centerScale) / 2);
		while (!currentArtIndex.equals(this.centerArtIndex) && maxInterations-- > 0) {
			float entryScale = this.getScaleF(currentArtIndex.getX(), currentArtIndex.getY());
			dy -= ((currentArtIndex.getY() * entryScale) + ART_GAP);
			float alpha = 1 + Math.round(dy + currentArtIndex.getY() * entryScale) / (this.height / 2f);
			int x1 = centerX - Math.round(currentArtIndex.getX() * entryScale / 2.0f);
			int y1 = centerY + Math.round(dy);
			this.indexEntries.add(new IndexEntry(currentArtIndex, x1, y1, alpha * 0.75f, entryScale));
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
		if (buttonNum != 0) {
			return;
		}
		for(IndexEntry indexEntry : this.indexEntries) {
			Index index = indexEntry.index;
			if (mx >= indexEntry.x
				&& mx <= indexEntry.x + index.getX() * indexEntry.scale
				&& my >= indexEntry.y
				&& my <= indexEntry.y + index.getY() * indexEntry.scale
			) {
				((PaintingIndex) player).morepainting$setIndex(index);
				this.mc.displayScreen(null);
				break;
			}
		}

	}

	private void drawPaintings(int screenWidth, int screenHeight, int mouseX, int mouseY) {
		// calc center
		int centerX = screenWidth / 2;
		int centerY = screenHeight / 2;
		// get client side art
		ArtTypes.Art art = ArtTypes.getArt(this.centerArtIndex);
		int zx = centerX - Math.round(this.centerArtIndex.getX() / 2F * this.centerScale);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		int zyTitle = centerY + Math.round(this.centerArtIndex.getY() / 2f * this.centerScale + 4);
		this.drawString(this.font, art.title(), zx, zyTitle, 0xffffffff);
		int zyArtist = centerY + Math.round(this.centerArtIndex.getY() / 2f * this.centerScale + 16);
		this.drawString(this.font, art.artist(), zx, zyArtist, 0x7f7f7f7f);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		for(IndexEntry entry : this.indexEntries) {
			if (entry.alpha <= 0.0F) {
				continue;
			}
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			Index index = entry.index;
			IconCoordinate texture = ArtTypes.getArt(index).getTexture();
			if (mouseX > entry.x && mouseX < entry.x + index.getX() * entry.scale && mouseY > entry.y && mouseY < entry.y + index.getY() * entry.scale) {
				int borderOffset = Math.max(2, Math.round(entry.scale));
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				int boarderWidth = Math.round((index.getX()) * entry.scale) + 2 * borderOffset;
				int boarderHeight = Math.round((index.getY()) * entry.scale) + 2 * borderOffset;
				this.drawRectWidthHeight(entry.x - borderOffset, entry.y - borderOffset, boarderWidth, boarderHeight, 0xffffffff);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				this.drawPainting(entry.x, entry.y, index.getX() * entry.scale, index.getY() * entry.scale, texture, 1.0F);
			} else {
				this.drawPainting(entry.x, entry.y, index.getX() * entry.scale, index.getY() * entry.scale, texture, entry.alpha);
			}
		}
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void drawPainting(int x, int y, float width, float height, @NotNull IconCoordinate coordinate, float alpha) {
		coordinate.parentAtlas.bind();
		Tessellator tessellator = Tessellator.instance;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, y + height, this.zLevel, coordinate.getIconUMin(), coordinate.getIconVMax());
		tessellator.addVertexWithUV(x + width, y + height, this.zLevel, coordinate.getIconUMax(), coordinate.getIconVMax());
		tessellator.addVertexWithUV(x + width, y, this.zLevel, coordinate.getIconUMax(), coordinate.getIconVMin());
		tessellator.addVertexWithUV(x, y, this.zLevel, coordinate.getIconUMin(), coordinate.getIconVMin());
		tessellator.draw();
	}

	private float getScaleF(int artWidth, int artHeight){
		int screenHeight = this.height;
		int scale = this.mc.resolution.getScale();
		int highest = Math.max(artHeight, artWidth);
		return Math.min(screenHeight * 0.5F / highest, scale);
	}

	private static class IndexEntry {
		final Index index;
		final int x;
		final int y;
		final float alpha;
		final float scale;

		public IndexEntry(Index index, int x, int y, float alpha, float scale) {
			this.index = index;
			this.x = x;
			this.y = y;
			this.alpha = alpha;
			this.scale = scale;
		}
	}
}

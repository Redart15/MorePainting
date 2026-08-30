package redart15.morepainting.client;

import com.moandjiezana.toml.Toml;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.client.render.texturepack.TexturePack;
import net.minecraft.client.render.texturepack.TexturePackList;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.enums.ArtType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static redart15.morepainting.MorePainting.LOGGER;

@Environment(EnvType.CLIENT)
public class ArtTypes {
	public static final Art DEFAULT = new Art("Minecraft", "Mojang", "Jeb", "morepainting:art/minecraft");
	private static final TreeMap<Size, List<Art>> SIZE2ART_LIST = new TreeMap<>();
	private static final Map<String, List<DataArt>> PICTURE_WITHOUT_SIZE = new HashMap<>();
	private static final int MIN_WIDTH = 1;
	private static final int MIN_HEIGHT = 1;
	// TODO: fix the 16x16 size
	private static final int MAX_WIDTH = 96;
	private static final int MAX_HEIGHT = 54;
	private static final int PIXEL_PER_BLOCK = 16;

	private record DataArtTypeList(List<DataArt> paintings) {
	}

	private record DataArt(String key, String title, String artist, String texture, Integer width, Integer height) {
		@Override
		public @NotNull String key() {
			return this.key == null ? "???" : this.key;
		}

		@Override
		public @NotNull String title() {
			return this.title == null ? "Missing title" : this.title;
		}

		@Override
		public @NotNull String artist() {
			return this.artist == null ? "Unknown artist" : this.artist;
		}

	}

	public static void reload() {
		ArtTypes.SIZE2ART_LIST.clear();
		for (ArtType artType : ArtType.values) {
			Size size = new Size(artType.sizeX, artType.sizeY);
			Art art = new Art(artType.key, artType.title, artType.artist, artType.texture);
			IconCoordinate texture = TextureRegistry.getTexture(artType.texture);
			ArtTypes.SIZE2ART_LIST.computeIfAbsent(size, key -> new ArrayList<>()).add(art);
		}
		TexturePackList packList = Minecraft.getMinecraft().texturePackList;
		Collection<TexturePack> packs = new ArrayList<>();
		packs.add(packList.getDefaultTexturePack());
		packs.addAll(packList.selectedPacks);
		for (TexturePack pack : packs) {
			for (String namespace : Registries.NAMESPACES) {
				try (InputStream stream = pack.getResourceAsStream("/assets/" + namespace + "/textures/art/painting.toml")) {
					List<DataArt> sizeLessPictures = PICTURE_WITHOUT_SIZE.computeIfAbsent(pack.fileName, key -> new ArrayList<>());
					if (stream == null) {
						continue;
					}
					ArtTypes.loadFromConfigFile(stream, sizeLessPictures);
				} catch (IOException exception) {
					LOGGER.error("Exception while reading paintings in pack '{}'!", pack.fileName, exception);
				} catch (IllegalArgumentException exception) {
					LOGGER.error("Invalid painting in pack '{}': {}", pack.fileName, exception.getMessage());
				}
			}
		}

	}

	private static void loadFromConfigFile(InputStream stream, List<DataArt> sizeLessPictures) throws IOException {
		DataArtTypeList extraList = new Toml().read(stream).to(DataArtTypeList.class);
		if(extraList == null){
			throw new IOException();
		}
		for (DataArt artType : extraList.paintings()) {
			// we need to let the TextureManager know about this texture
			IconCoordinate texture = TextureRegistry.getTexture(artType.texture());
			// once all IconCoordinates are initialized we will sort them manually back in
			if (artType.height() == null || artType.width() == null) {
				sizeLessPictures.add(artType);
				continue;
			}
			int height = artType.height();
			int width = artType.width();
			if (width < MIN_WIDTH || height < MIN_HEIGHT || width > MAX_WIDTH || height > MAX_HEIGHT) {
				throw new IllegalArgumentException(String.format("Invalid dimensions for painting '%s':%dx%d", artType.key(), width, height));
			}
			Size size = new Size(width * PIXEL_PER_BLOCK, height * PIXEL_PER_BLOCK);
			Art art = new Art(artType.key(), artType.title(), artType.artist(), artType.texture());
			ArtTypes.SIZE2ART_LIST.computeIfAbsent(size, key -> new ArrayList<>()).add(art);
		}
	}

	public static void loadPictureWithOutSize() {
		for (Map.Entry<String, List<DataArt>> entry : PICTURE_WITHOUT_SIZE.entrySet()) {
			String packName = entry.getKey();
			for (DataArt artType : entry.getValue()) {
				try {
					IconCoordinate texture = TextureRegistry.getTexture(artType.texture());
					int width = (int) Math.ceil(texture.width / 16.0F);
					int height = (int) Math.ceil(texture.height / 16.0F);
					if (width <= 0 || height <= 0 || width > MAX_WIDTH || height > MAX_HEIGHT) {
						throw new IllegalArgumentException(String.format("Invalid dimensions for painting '%s':%dx%d", artType.key(), width, height));
					}
					Size size = new Size(width * PIXEL_PER_BLOCK, height * PIXEL_PER_BLOCK);
					Art art = new Art(artType.key(), artType.title(), artType.artist(), artType.texture());
					ArtTypes.SIZE2ART_LIST.computeIfAbsent(size, key -> new ArrayList<>()).add(art);
				} catch (IllegalArgumentException exception) {
					LOGGER.error("Failed to load sizeless paintings from pack '{}': {}", packName, exception.getMessage());
				}
			}
		}
		PICTURE_WITHOUT_SIZE.clear();
	}


	public record Art(String key, String title, String artist, String texture) {
		public @NotNull IconCoordinate getTexture() {
			return TextureRegistry.getTexture(this.texture);
		}
	}

	public static @NotNull Index getNext(@NotNull Index artIndex) {
		int index = artIndex.index();
		Size size = artIndex.size();
		List<Art> artList = SIZE2ART_LIST.get(size);
		if (artList != null && index + 1 < artList.size()) {
			return new Index(index + 1, size);
		}
		Map.Entry<Size, List<Art>> next = SIZE2ART_LIST.higherEntry(size);
		while (next != null && next.getValue().isEmpty()) {
			next = SIZE2ART_LIST.higherEntry(next.getKey());
		}
		if (next == null) {
			next = SIZE2ART_LIST.firstEntry();

			while (next != null && next.getValue().isEmpty()) {
				next = SIZE2ART_LIST.higherEntry(next.getKey());
			}
		}
		if (next == null) {
			return new Index(index, size);
		}
		return new Index(0, next.getKey());
	}

	public static @NotNull Index getPrev(@NotNull Index artIndex) {
		int index = artIndex.index();
		if (index > 0) {
			return new Index(index - 1, artIndex.size());
		}
		Map.Entry<Size, List<Art>> prev = SIZE2ART_LIST.lowerEntry(artIndex.size());
		while (prev != null && prev.getValue().isEmpty()) {
			prev = SIZE2ART_LIST.lowerEntry(prev.getKey());
		}
		if (prev == null) {
			prev = SIZE2ART_LIST.lastEntry();
			while (prev != null && prev.getValue().isEmpty()) {
				prev = SIZE2ART_LIST.lowerEntry(prev.getKey());
			}
		}
		if (prev == null) {
			return Index.getDefaultIndex();
		}
		return new Index(prev.getValue().size() - 1, prev.getKey());
	}

	public static Art getArt(@NotNull Index artIndex) {
		Size size = artIndex.size();
		int index = artIndex.index();
		List<Art> artList = SIZE2ART_LIST.get(size);
		if (artList == null || artList.isEmpty()) {
			return DEFAULT;
		}
		Art art = artList.get(index % artList.size());
		if (art == null || art.texture == null) {
			return DEFAULT;
		}
		return art;
	}

	public static int getSmallest() {
		if (SIZE2ART_LIST.isEmpty()) {
			return 0;
		}
		Size size = SIZE2ART_LIST.firstKey();
		if (size == null) {
			return 0;
		}
		return size.secondInt();
	}

	public static int getLargest() {
		if (SIZE2ART_LIST.isEmpty()) {
			return 0;
		}
		Size size = SIZE2ART_LIST.lastKey();
		if (size == null) {
			return 0;
		}
		return size.secondInt();
	}

	public static int getAverage() {
		if (SIZE2ART_LIST.isEmpty()) {
			return 0;
		}
		Size first = SIZE2ART_LIST.firstKey();
		Size last = SIZE2ART_LIST.lastKey();
		if (first == null || last == null) {
			return 0;
		}
		return (int) Math.ceil((first.secondInt() + last.secondInt()) / 2.0);
	}
}

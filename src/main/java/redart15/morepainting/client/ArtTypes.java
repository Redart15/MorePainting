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

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static redart15.morepainting.MorePainting.LOGGER;

@Environment(EnvType.CLIENT)
public class ArtTypes {
	public static final Art DEFAULT = new Art("Kebab", "Kebab med tre pepperoni", "Kristoffer Zetterstrand", "minecraft:art/kebab");
	private static final TreeMap<Size, List<Art>> SIZE2ART_LIST = new TreeMap<>();

	private record DataArtTypeList(List<DataArt> list){}
	private record DataArt(String key, String title, String artist, String texture, int width, int height) {}

	public static void reload(){
		ArtTypes.SIZE2ART_LIST.clear();
		for(ArtType artType : ArtType.values){
			Size size = new Size(artType.sizeX, artType.sizeY);
			Art art = new Art(artType.key, artType.title, artType.artist, artType.texture);
			IconCoordinate texture = TextureRegistry.getTexture(artType.texture);
			ArtTypes.SIZE2ART_LIST.computeIfAbsent(size, key -> new ArrayList<>()).add(art);
		}
		TexturePackList packList = Minecraft.getMinecraft().texturePackList;
		Collection<TexturePack> packs = new ArrayList<>();
		packs.add(packList.getDefaultTexturePack());
		packs.addAll(packList.selectedPacks);
		for(TexturePack pack : packs) {
			for (String namespace : Registries.NAMESPACES) {
				try (InputStream stream = pack.getResourceAsStream("/assets/" + namespace + "/textures/art/painting.toml")) {
					if(stream == null){
						continue;
					}
					DataArtTypeList extraList = new Toml().read(stream).to(DataArtTypeList.class);
					for(DataArt artType : extraList.list()){
						int width = artType.width();
						int height = artType.height();
						if(width <= 0 || height <= 0 || width > 15 || height > 15){ // TODO: fix the 16x16 size
							throw new IllegalArgumentException(String.format("Invalid dimensions for painting '%s':%dx%d", artType.key(),width ,height));
						}
						Size size = new Size(artType.width() * 16, artType.height() * 16);
						Art art = new Art(artType.key(), artType.title(), artType.artist(), artType.texture());
						IconCoordinate texture = TextureRegistry.getTexture(artType.texture);
						ArtTypes.SIZE2ART_LIST.computeIfAbsent(size, key -> new ArrayList<>()).add(art);
					}
				}catch (IOException exception){
					LOGGER.error("Exception while reading paintings in pack '{}'!", pack.packId, exception);
				}catch (IllegalArgumentException exception){
					LOGGER.error("Invalid painting in pack '{}': {}", pack.packId, exception.getMessage());
				}
			}
		}

	}

	public record Art(String key, String title, String artist, String texture) {
		public IconCoordinate getTexture() {
			return TextureRegistry.getTexture(this.texture);
		}
	}

	public static Index getNext(Index artIndex) {
		int index = artIndex.index();
		Size size = artIndex.size();
		List<Art> artList = SIZE2ART_LIST.get(size);
		if (index + 1 < artList.size()) {
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

	public static Index getPrev(Index artIndex) {
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
			return null;
		}
		return new Index(prev.getValue().size() - 1, prev.getKey());
	}

	public static Art getArt(Index artIndex) {
		Size size = artIndex.size();
		int index = artIndex.index();
		List<Art> artList = SIZE2ART_LIST.get(size);
		if(artList == null || artList.isEmpty()){
			return DEFAULT;
		}
		Art art = artList.get(index % artList.size());
		if(art == null || art.texture == null){
			return DEFAULT;
		}
		return art;
	}

	public static int getSmallest(){
		if(SIZE2ART_LIST.isEmpty()){
			return 0;
		}
		Size size = SIZE2ART_LIST.firstKey();
		if(size == null){
			return 0;
		}
		return size.secondInt();
	}
}

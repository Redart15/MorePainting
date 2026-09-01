package redart15.morepainting.client.data;

import net.minecraft.core.lang.I18n;
import org.jetbrains.annotations.NotNull;

public class DataArt {
	private String title;
	private String artist;
	private String texture;
	private DataSize dimensions;
	private Double scaled;

	public DataArt(String title, String artist, String texture, DataSize dataSize, Double scaled){
		this.title = title;
		this.artist = artist;
		this.texture = texture;
		this.dimensions = dataSize;
		this.scaled = scaled;
	}


	public Double scaled() {
		return scaled;
	}

	public DataSize dimensions() {
		return dimensions;
	}

	public String texture() {
		return texture;
	}

	public @NotNull String title() {
		return this.title == null ? I18n.getInstance().translateKey("morepainting.missing.title") : this.title;
	}

	public @NotNull String artist() {
		return this.artist == null ? I18n.getInstance().translateKey("morepainting.missing.artist") : this.artist;
	}

	public DataArt setScaled(Double scaled) {
		this.scaled = scaled;
		return this;
	}

	public DataArt setDimensions(DataSize dimensions) {
		this.dimensions = dimensions;
		return this;
	}

	public DataArt setTexture(String texture) {
		this.texture = texture;
		return this;
	}

	public DataArt setArtist(String artist) {
		this.artist = artist;
		return this;
	}

	public DataArt setTitle(String title) {
		this.title = title;
		return this;
	}

}

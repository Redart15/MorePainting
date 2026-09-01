package redart15.morepainting.client.data;

public class DataSize {
	private Integer width;
	private Integer height;

	public DataSize(Integer width, Integer height){
		this.width = width;
		this.height = height;
	}

	public Integer width() {
		return width;
	}

	public DataSize setWidth(Integer width) {
		this.width = width;
		return this;
	}

	public Integer height() {
		return height;
	}

	public DataSize setHeight(Integer height) {
		this.height = height;
		return this;
	}
}

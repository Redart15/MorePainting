package redart15.morepainting.client.data;

import java.util.List;

public class DataArtTypeList {
	List<DataArt> paintings;

	public DataArtTypeList(List<DataArt> paintings){
		this.paintings = paintings;
	}

	public List<DataArt> paintings() {
		return paintings;
	}

	public DataArtTypeList setPaintings(List<DataArt> paintings) {
		this.paintings = paintings;
		return this;
	}
}

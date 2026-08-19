package redart15.morepainting.client;

import java.util.Objects;

public record Index(int index, Size size){
	public static Index getDefaultIndex() {
		return new Index(0, Size.getSmallest());
	}

	public int getX(){
		return this.size().firstInt();
	}

	public int getY(){
		return this.size().secondInt();
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Index other)) return false;
		return this.index == other.index && this.size.equals(other.size);
	}

	@Override
	public int hashCode() {
		return Objects.hash(index, size);
	}
}

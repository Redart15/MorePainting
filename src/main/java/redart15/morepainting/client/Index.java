package redart15.morepainting.client;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Index{
	private final int index;
	private final Size size;

	public Index(int index, Size size){
		this.index = index;
		this.size = size;
	}

	@Contract(" -> new")
	public static @NotNull Index getDefaultIndex() {
		return new Index(0, Size.getSmallest());
	}

	public int getX(){
		return this.size.firstInt();
	}

	public int getY(){
		return this.size.secondInt();
	}

	public boolean isValid(){
		return this.index() >= 0 && this.size() != null && this.size().isValid();

	}

	public void setInBlockBounds(){
		this.size.setInBlockBounds();

	}

	public Size size(){
		return this.size;
	}

	public int index(){
		return this.index;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Index)) {
			return false;
		}
		Index other = (Index) object;
		return this.index == other.index && this.size.equals(other.size);
	}

	@Override
	public int hashCode() {
		return Objects.hash(index, size);
	}
}

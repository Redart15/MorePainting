package redart15.morepainting.client;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record Index(int index, Size size){

	@Contract(" -> new")
	public static @NotNull Index getDefaultIndex() {
		return new Index(0, Size.getSmallest());
	}

	public int getX(){
		return this.size().firstInt();
	}

	public int getY(){
		return this.size().secondInt();
	}

	public @NotNull Index setX(int first){
		this.size().first(first);
		return this;
	}

	public @NotNull Index setY(int second){
		this.size().second(second);
		return this;
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

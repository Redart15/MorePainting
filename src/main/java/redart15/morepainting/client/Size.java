package redart15.morepainting.client;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import redart15.morepainting.client.data.IntIntPair;

public class Size extends IntIntPair implements Comparable<Size>{
	public static final int MIN_WIDTH = 1;
	public static final int MIN_HEIGHT = 1;
	public static final int MAX_WIDTH = 100;
	public static final int MAX_HEIGHT = 100;
	public static final int PIXEL_PER_BLOCK = 16;

	public Size(int left, int right) {
		super(left, right);
	}

	@Contract(" -> new")
	public static @NotNull Size getSmallest() {
		return new Size(PIXEL_PER_BLOCK * MIN_WIDTH, PIXEL_PER_BLOCK * MIN_HEIGHT);
	}

	@Override
	public int compareTo(@NotNull Size other) {
		int thisSize = this.firstInt() * this.secondInt();
		int otherSize = other.firstInt() * other.secondInt();
		if(thisSize == otherSize){
			if(this.firstInt() == other.firstInt()){
				return Integer.compare(this.secondInt(), other.secondInt());
			}
			return Integer.compare(this.firstInt(), other.firstInt());
		}
		return Integer.compare(thisSize, otherSize);
	}
}

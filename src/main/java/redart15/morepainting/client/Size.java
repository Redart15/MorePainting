package redart15.morepainting.client;

import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Size extends IntIntMutablePair implements Comparable<Size>{
	private static final int MIN_WIDTH = 1;
	private static final int MIN_HEIGHT = 1;
	private static final int MAX_WIDTH = 100;
	private static final int MAX_HEIGHT = 100;
	public static final int PIXEL_PER_BLOCK = 16;

	public Size(int left, int right) {
		super(left, right);
	}

	@Contract(" -> new")
	public static @NotNull Size getSmallest() {
		return new Size(PIXEL_PER_BLOCK * MIN_WIDTH, PIXEL_PER_BLOCK * MIN_HEIGHT);
	}

	public void setInBlockBounds(){
		this.first((int) Math.ceil(this.firstInt() / (double)PIXEL_PER_BLOCK) * PIXEL_PER_BLOCK);
		this.second((int) Math.ceil(this.secondInt() / (double)PIXEL_PER_BLOCK) * PIXEL_PER_BLOCK);
	}

	public boolean isValid(){
		int minHeight = MIN_HEIGHT * PIXEL_PER_BLOCK;
		int maxHeight = MAX_HEIGHT * PIXEL_PER_BLOCK;
		int minWidth = MIN_WIDTH * PIXEL_PER_BLOCK;
		int maxWidth = MAX_WIDTH * PIXEL_PER_BLOCK;
		return this.firstInt() >= minWidth && this.firstInt() <= maxWidth && this.secondInt() >= minHeight && this.secondInt() <= maxHeight;
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

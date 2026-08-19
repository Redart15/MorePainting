package redart15.morepainting.client;

import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Size extends IntIntMutablePair implements Comparable<Size>{
	private static final int SX = 16;
	private static final int SY = 16;

	public Size(int left, int right) {
		super(left, right);
	}

	public static Size getSmallest() {
		return new Size(SX, SY);
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

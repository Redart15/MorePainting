package redart15.morepainting.client.data;

public class IntIntPair {
	private final int first;
	private final int second;

	public IntIntPair(int first, int second) {
		this.first = first;
		this.second = second;
	}

	public IntIntPair of(int first, int second){
		return new IntIntPair(first, second);
	}

	public int firstInt(){
		return this.first;
	}

	public int secondInt(){
		return this.second;
	}
}

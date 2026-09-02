package redart15.morepainting.client.data;

public class IntIntPair {
	private int first;
	private int second;

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


	public IntIntPair first(int first){
		this.first = first;
		return this;
	}

	public IntIntPair second(int second){
		this.second = second;
		return this;
	}
}

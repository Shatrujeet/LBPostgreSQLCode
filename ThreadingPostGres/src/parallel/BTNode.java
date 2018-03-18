package parallel;

public class BTNode<T> {
	public T data;
	public String extra;
	public BTNode<T> right;
	public BTNode<T> left;
	public String name;
	public long duration;
	public long durationSelf;
	
	public BTNode(){
		duration = 0;
		durationSelf = 0;
	}
}

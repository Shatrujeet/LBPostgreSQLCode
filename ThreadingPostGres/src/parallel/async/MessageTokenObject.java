package parallel.async;

import java.util.concurrent.CountDownLatch;

import parallel.BTNode;

public class MessageTokenObject extends TokenObject {
	BTNode<String> node;
	CountDownLatch latch;
	boolean isRoot;
	public long rows;
	
	public MessageTokenObject(CountDownLatch latch, BTNode<String> node, boolean isRoot) {
		this.node = node;
		this.latch = latch;
		this.isRoot = isRoot;
		rows = -1;
	}
	
	public void setCountDownLatch(CountDownLatch latch) {
		this.latch = latch;
	}

}

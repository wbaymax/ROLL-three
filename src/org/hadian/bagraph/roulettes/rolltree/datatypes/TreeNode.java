package org.hadian.bagraph.roulettes.rolltree.datatypes;

public class TreeNode {
	private long weight;
	private boolean root = false;
	private Bucket bucket;
	private TreeNode lchild = null;
	private TreeNode rchild = null;
	private TreeNode thirdchild = null;
	private TreeNode parent = null;
	private int flag = 0;
	
	@Override
	public String toString() {
		String result = "{";
		if(isRoot())
			result += "ROOT, ";
		if(isDataNode())
			result += bucket.toString();
		else{
			if(lchild != null)
				result += " L:" + lchild.toString();
			if(rchild != null)
				result += " R:" + rchild.toString();
			if(thirdchild != null)
				result += "Third:" + thirdchild.toString();
		}
		result += "}";
		return result;
	}
	
	public double getCodeWordLength(long numEdges){
		int depth = 0; 
		double codeLength = getCodeLength(depth);
		double totalWeights = 2 * numEdges;
		return codeLength / totalWeights ;	
	}
	
	private long getCodeLength(int depth) {
		long codeLen = 0;
		if(isDataNode())
			codeLen = depth * this.getBucket().getWeight();
		else{
			if(lchild != null)
				codeLen += lchild.getCodeLength(depth+1);
			if(rchild != null)
				codeLen += rchild.getCodeLength(depth+1);
			if(thirdchild != null)
				codeLen += thirdchild.getCodeLength(depth+1);
		}
		return codeLen;
	}

	public Bucket getBucket() {
		return bucket;
	}
	
	public TreeNode(boolean isRoot) {
		super();
		this.root = isRoot;
	}
	
	public TreeNode(Bucket newBucket) {
		super();
		this.bucket = newBucket;
		this.bucket.correspondingTreeNode = this;
	}
	
	public boolean isDataNode(){
		return (this.bucket != null);
	}
	
	public long getWeight() {
		if(isDataNode())
			return bucket.getWeight();
		return weight;	
	}
	public void setWeight(long weight) {
		if(weight < 0){
			System.err.println("Integer overflow!!!");
			System.exit(1);
		}
		this.weight = weight;
	}
	
	public boolean isRoot() {
		return root;
	}

	public TreeNode getLchild() {
		if(isDataNode()){
			System.err.println("ERROR: access to Lchild of a data node! ");
			return null;
		}
		return lchild;
	}
	
	public void setLchild(TreeNode lchild) {
		if(isDataNode()){
			System.err.println("This is a lchild data node!");
			return;
		}
		this.lchild = lchild;
	}
	
	public TreeNode getRchild() {
		if(isDataNode()){
			System.err.println("ERROR: access to Rchild of a data node! ");//bucket非空
			return null;
		}
		return rchild;
	}
	
	public void setRchild(TreeNode rchild) {
		if(isDataNode()){
			System.err.println("This is a rchild data node!");
			return;
		}
		this.rchild = rchild;
	}
	
//三叉树节点
	public TreeNode getThirdchild(){
		if(isDataNode()){
			System.err.println("ERROR: access to thirdchild of a data node!");
			return null;
		}
		return thirdchild;
	}
	public void setThirdchild(TreeNode thirdchild){
		if(isDataNode()){
			System.err.println("This is a third data node!");
			return;
		}
		this.thirdchild = thirdchild;
	}
//	
	public long getLchildWeight(){
		if(this.getLchild() != null)
			return this.getLchild().getWeight();
		return 0;
	}
	
	public long getRchildWeight(){
		if(this.getRchild() != null)
			return this.getRchild().getWeight();
		return 0;
	}
	
//三叉树节点权值
	public long getThirdchildWeight(){
		if(this.getThirdchild() != null)
			return this.getThirdchild().getWeight();
		return 0;
	}
//	
	
	public TreeNode getParent() {
		return parent;
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
		//System.err.println("TODO: weight of the parent should be updated and propagated!");
	}
	
//设置标记节点孩子个数
	public int getFlag(){
		return flag;
	}
	public void addFlag(){
		this.flag++;
	}
	public void delFlag(){
		this.flag--;
	}
}

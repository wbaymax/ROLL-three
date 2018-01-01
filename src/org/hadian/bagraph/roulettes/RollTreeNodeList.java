package org.hadian.bagraph.roulettes;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

import java.util.Map;
import java.util.Random;

import org.hadian.bagraph.generators.BAGraphGenerator;
import org.hadian.bagraph.roulettes.rolltree.cwlength.HuffmanCodeLenCalculator;
import org.hadian.bagraph.roulettes.rolltree.datatypes.Bucket;
import org.hadian.bagraph.roulettes.rolltree.datatypes.TreeNode;
import org.hadian.bagraph.util.XOrShiftRandomGenerator;

import com.google.common.collect.TreeMultiset;

/**
 * ROLL-tree data structure for generating Barabasi-Albert graphs. See the ROLL paper, section 4.2
 * @author Ali Hadian
 *
 */
public class RollTreeNodeList implements NodesList {
	public long numDeletes, numInserts;
	protected static Random random = new Random();
	
//	System.out.print(random.nextInt());
	protected TreeNode root = new TreeNode(true);
	protected Int2ObjectOpenHashMap<Bucket> buckets = new Int2ObjectOpenHashMap<Bucket>();

	public RollTreeNodeList() {
		System.out.print("class RollTreeNodeList()" + "\n");
		root = new TreeNode(true);
	}
	
	public int addNode_while = 0;
	public int addNode = 0;
	public int removeBucket = 0;
	public int updateTree = 0;
	public int sampleBucket = 0;
	public int connectMRandomNodeToThisNewNode = 0;
	public int updateRouletteWheel = 0;
	
	public void addNode(int nodeId, int nodeDegree) {
		System.out.print("function addNode()" + "\n");
		addNode++;
		TreeNode currentHufNode = root;//从根节点开始遍历
		Bucket bucket = buckets.get(nodeDegree);//选择节点相应度的bucket

		if(bucket != null){
			bucket.addNode(nodeId);
			updateTree(bucket.correspondingTreeNode.getParent());
			return;
		}
		numInserts++;
		//finding position of the nodes
		bucket = new Bucket(nodeDegree);
		bucket.addNode(nodeId);
		buckets.put(bucket.getDegree(), bucket);
		TreeNode newHufNode = new TreeNode(bucket);//初始化桶为树节点
		//node.setRouletteWheelIndex(node.getDegree());
		
		while(true){ //while it is not a leaf
			addNode_while++;
			if(currentHufNode.isDataNode()){
				TreeNode midNode = new TreeNode(false);
				TreeNode parentNode = currentHufNode.getParent();
//原代码
				
				midNode.setParent(parentNode);
				midNode.setLchild(currentHufNode);
				midNode.addFlag();//----------添加一个孩子flag+1
				currentHufNode.setParent(midNode);
				midNode.setRchild(newHufNode);
				midNode.addFlag();//----------添加一个孩子flag+1
				newHufNode.setParent(midNode);
				
//新增插入三叉树节点
				/*
				midNode.setParent(parentNode);
				midNode.setLchild(parentNode.getRchild());
				midNode.addFlag();//--------添加孩子，flag+1
				parentNode.getRchild().setParent(midNode);
				midNode.setRchild(currentHufNode);
				midNode.addFlag();//--------------flag+1
				currentHufNode.setParent(midNode);
				midNode.setThirdchild(newHufNode);
				midNode.addFlag();//--------------flag+1
				newHufNode.setParent(midNode);
				*/
//原代码
				
				if(midNode.getParent().getRchild() == currentHufNode){
					midNode.getParent().setRchild(midNode);
					
				}
				else if(midNode.getParent().getLchild() == currentHufNode){
					midNode.getParent().setLchild(midNode);
					
				}
				else 
					System.err.println("WHERE DOES THIS CurrentNode came from?");
				
//新增Thirdchild
				/*
				if(midNode.getParent().getThirdchild() == currentHufNode){
					midNode.getParent().setThirdchild(midNode);
				
					}
				else if(midNode.getParent().getRchild() == currentHufNode){
					midNode.getParent().setRchild(midNode);
					
					}
				else if(midNode.getParent().getLchild() == currentHufNode){
					midNode.getParent().setLchild(midNode);
					
					}
				else 
					System.err.println("WHERE DOES THIS CurrentNode came from?");
				*/
				updateTree(midNode);
				break;
			}
			if(currentHufNode.getLchild() == null){
				currentHufNode.setLchild(newHufNode);
				currentHufNode.addFlag();//------------flag+1
				newHufNode.setParent(currentHufNode);
				break;
			} 
			if(currentHufNode.getRchild() == null){
				currentHufNode.setRchild(newHufNode);
				currentHufNode.addFlag();//------------flag+1
				newHufNode.setParent(currentHufNode);
				break;
			}
//新增三叉树节点
			/*
			if(currentHufNode.getThirdchild() == null){
				currentHufNode.setThirdchild(newHufNode);
				currentHufNode.addFlag();//------------flag+1
				newHufNode.setParent(currentHufNode);
				break;
			}
			*/
			// else select one of the leafs which is not 
//原代码
			
			if(currentHufNode.getLchildWeight() <= currentHufNode.getRchildWeight())
				currentHufNode = currentHufNode.getLchild();
			else
				currentHufNode = currentHufNode.getRchild();
			
//新增三叉树插入
			/*
			if(currentHufNode.getLchildWeight() <= currentHufNode.getRchildWeight())
				currentHufNode = currentHufNode.getLchild();
			else if(currentHufNode.getRchildWeight() <= currentHufNode.getThirdchildWeight())
				currentHufNode = currentHufNode.getRchild();
			else
				currentHufNode = currentHufNode.getThirdchild();
			*/
		}
		System.out.print("updateTree in addNode"+"\n");
		updateTree(newHufNode);
	}

	public void updateTree(TreeNode node) {
		System.out.print("function updateTree()" + "\n");
		updateTree++;	
//原
		node.setWeight(node.getLchildWeight() + node.getRchildWeight());
		
//新增三叉树	权值	
	//	node.setWeight(node.getLchildWeight() + node.getRchildWeight() + node.getThirdchildWeight());
		
		if(node.getParent() != null){
		//	System.out.print("in updateTree"+"\n");
			updateTree(node.getParent());
		}
	}

	public void updateRouletteWheel(int nodeID, int nodeIndex, int degreeBefore, int degreeAfter) {
		updateRouletteWheel++;
		System.out.print("function updateRouletteWheel()" + "\n");
		Bucket oldBucket = buckets.get(degreeBefore);
		oldBucket.removeNodeAt(nodeIndex);
		updateTree(oldBucket.correspondingTreeNode.getParent());

		if(oldBucket.getSize() == 0)
			removeBucket(oldBucket);

		addNode(nodeID, degreeAfter);
	}

	protected void removeBucket(Bucket oldBucket) {
		System.out.print("function removeBucket()" + "\n");
		removeBucket ++;
		numDeletes++;
		buckets.remove(oldBucket.getDegree());
		TreeNode oldNode = oldBucket.correspondingTreeNode;
		TreeNode father = oldNode.getParent();
		TreeNode sibling = null;
//原代码
		
		if(father.getLchild() == oldNode){
			sibling = father.getRchild();
			if(father.isRoot()){
				father.setLchild(null);
				father.delFlag();
			}
		}else if(father.getRchild() == oldNode){
			sibling = father.getLchild();
			if(father.isRoot()){
				father.setRchild(null);
				father.delFlag();
			}
		}else
			System.out.println("Sibling is a mother fucker."); //It should not happen, just an integrity check.
		
//新增三叉树置空待删除节点，flag-1
		/*
		if(father.getLchild() == oldNode){
			
			sibling = father.getRchild();
			if(father.isRoot()){
				father.setLchild(null);
				father.delFlag();
			}
		}else if(father.getRchild() == oldNode){
			sibling = father.getThirdchild();
			if(father.isRoot()){
				father.setRchild(null);
				father.delFlag();
			}
		}else if(father.getThirdchild() == oldNode){
			sibling = father.getLchild();
			if(father.isRoot()){
				father.setThirdchild(null);
				father.delFlag();
			}
		}else
			System.out.println("Sibling is a mother fucker.");
		*/
		if(!father.isRoot()){
			TreeNode grandFather = father.getParent();
		//	System.out.print("father.getFlag:"+father.getFlag()+"\n");
		//	System.out.print("grandFather.getFlag:"+grandFather.getFlag()+"\n");
//原代码
			
				if (grandFather.getLchild() == father) {
					grandFather.setLchild(sibling);
				} else if (grandFather.getRchild() == father) {
					grandFather.setRchild(sibling);
				}
				sibling.setParent(grandFather);
			
//新增删除桶，father节点仅有一个孩子时按二叉树规则删除，设置flag，每增加孩子flag+1
			/*
			if(father.getFlag() == 1){
				if(grandFather.getLchild() == father){
					grandFather.setLchild(sibling);
				}else if(grandFather.getRchild() == father){
					grandFather.setRchild(sibling);
				}else if(grandFather.getThirdchild() == father){
					grandFather.setThirdchild(sibling);
				}
				sibling.setParent(grandFather);
			}
			*/	
		}
		System.out.print("updateTree in removeBucket"+"\n");
		updateTree(father);
	}


	public int sampleBucket() {
		System.out.print("function sampleBucket()" + "\n");
		sampleBucket++;
		TreeNode sampleNode = root;
		while(!sampleNode.isDataNode()){
			int i = 0;
			System.out.print("while times:"+i+"\n");
			long r = XOrShiftRandomGenerator.randomLong();

			//if( r < (1.0 * sampleNode.getLchildWeight() / (sampleNode.getLchildWeight() + sampleNode.getRchildWeight())))

		//	long sampleWeight = sampleNode.getLchildWeight()+sampleNode.getRchildWeight()+sampleNode.getThirdchildWeight();
			long sampleWeight = sampleNode.getLchildWeight() + sampleNode.getRchildWeight();
//原代码
			
			if( ( r % sampleWeight ) < sampleNode.getLchildWeight())
				sampleNode = sampleNode.getLchild();
			else
				sampleNode = sampleNode.getRchild();
			
//新增三叉树节点
			/*
			if((r % sampleWeight) < sampleNode.getLchildWeight())
				sampleNode = sampleNode.getLchild();
			else if((r % sampleWeight) < sampleNode.getRchildWeight())
				sampleNode = sampleNode.getRchild();
			else
				sampleNode = sampleNode.getThirdchild();
			*/
			BAGraphGenerator.numComparisons++;
			i++;
		}
		return sampleNode.getBucket().getDegree();
	}

	public Map<Integer, Bucket> getBuckets() {
		System.out.print("function getBuckets()" + "\n");
		return buckets;
	}
	
	public void printDegreeDistribution(){
		System.out.print("function printDegreeDistribution()" + "\n");
		for(int deg : buckets.keySet())
			System.out.println(deg + "\t" + buckets.get(deg).getWeight());
	}

	public TreeNode getRoot() {
		System.out.print("function getRoot()" + "\n");
		return root;
	}
	
	public double getHuffmanCodeWordLength(long numEdges){
		System.out.print("function getHuffmanCodeWordLength()" + "\n");
		TreeMultiset<Long> weights = TreeMultiset.create();
		for(int deg : buckets.keySet())
			weights.add(buckets.get(deg).getWeight());
		
		return HuffmanCodeLenCalculator.computeHufmanLen(weights);
	}
	
	@Override
	public void connectMRandomNodeToThisNewNode(int m, int numNodes) {
		connectMRandomNodeToThisNewNode++;
		System.out.print("function connectMRandomNodeToThisNewNode()" + "\n");
		long t = System.nanoTime();
//		System.out.println("Linking nodes to node:" + numNodes);
		IntOpenHashSet allSelectedNodes = new IntOpenHashSet(m);	//m nodes to be selected

		//selecting the node
		for (int mCount=0; mCount<m; mCount++){  //selecting candidateNodes[mCount]
			boolean foundUniqueRandomNode = false;
			while(!foundUniqueRandomNode){
				int randBuckDegree = sampleBucket();
				int selectedNodePositionInBucket = random.nextInt(buckets.get(randBuckDegree).getSize());
				int selectedNodeId = buckets.get(randBuckDegree).getNodeAt(selectedNodePositionInBucket);	
				if(!allSelectedNodes.contains(selectedNodeId)){
					allSelectedNodes.add(selectedNodeId);
					foundUniqueRandomNode = true;
					long t2 = System.nanoTime();
					updateRouletteWheel(selectedNodeId, selectedNodePositionInBucket, randBuckDegree, randBuckDegree+1);
					long moveNodeToHigherBucket_Time = System.nanoTime() - t2;
					BAGraphGenerator.maintenanceTime += moveNodeToHigherBucket_Time;
					t += moveNodeToHigherBucket_Time; //do not count this time in sampling time
				}
			}
		}

		BAGraphGenerator.samplingTime += System.nanoTime() - t;
		t = System.nanoTime();
		//updating weights
		//insert the new node in the wheel (other nodes are inserted to the wheel inside 'removeNodeAndUpdateGraph'
		addNode(numNodes,m);		
		for(int nodeID : allSelectedNodes)
			BAGraphGenerator.writeToGaph(nodeID, numNodes);
		BAGraphGenerator.maintenanceTime += System.nanoTime() - t;

	}

	@Override
	public void createInitNodes(int m) {
		System.out.print("function createInitNodes()" + "\n");
		for(int i=0; i<m; i++){
			addNode(i, 1);
			BAGraphGenerator.writeToGaph( i, BAGraphGenerator.m);
		}
		addNode(m, m);
	}
}

package FPTree;
import java.util.ArrayList;

public class TreeNode implements Comparable<TreeNode>, Cloneable {

	private String name;
	private Integer count;
	private TreeNode parentNode;
	private ArrayList<TreeNode> childTreeNodes;
	
	
	public TreeNode(String nameString, Integer count) {
		super();
		this.name = nameString;
		this.count = count;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public Integer getCount() {
		return count;
	}


	public void setCount(Integer count) {
		this.count = count;
	}


	public TreeNode getParentNode() {
		return parentNode;
	}


	public void setParentNode(TreeNode parentNode) {
		this.parentNode = parentNode;
	}


	public ArrayList<TreeNode> getChildTreeNodes() {
		return childTreeNodes;
	}


	public void setChildTreeNodes(ArrayList<TreeNode> childTreeNodes) {
		this.childTreeNodes = childTreeNodes;
	}


	@Override
	public int compareTo(TreeNode o) {
		// TODO Auto-generated method stub
		//默认为从小到大排序，这里这样写是为了让treeNode按从大到小排列
		return o.getCount().compareTo(this.getCount());  
	}


	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		//由于类的内部有引用，所以要采用深拷贝
		TreeNode node=(TreeNode) super.clone();
		
		if(this.getParentNode() != null){  
            node.setParentNode((TreeNode) this.getParentNode().clone());  
        }  
          
        if(this.getChildTreeNodes() != null){  
            node.setChildTreeNodes((ArrayList<TreeNode>) this.getChildTreeNodes().clone());  
        }  
		return node;
	}

}

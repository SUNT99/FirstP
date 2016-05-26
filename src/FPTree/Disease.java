package FPTree;

import java.util.ArrayList;

public class Disease {

	private int id;
	private String name;
	//第一列用来记录是哪一列的证候 第二列用来记录是该列第几个证候 第三列用来记录  特异度
	private ArrayList<String[][]> primeSymptom=new ArrayList<String[][]>();//用来存储所有的主属性值群
	public Disease(int id,String name){
		this.id=id;
		this.name=name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<String[][]> getPrimeSymptom() {
		return primeSymptom;
	}
	public void setPrimeSymptom(ArrayList<String[][]> primeSymptom) {
		this.primeSymptom = primeSymptom;
	}
	
	
	
	

}

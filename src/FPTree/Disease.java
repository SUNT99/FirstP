package FPTree;

import java.util.ArrayList;

public class Disease {

	private int id;
	private String name;
	//��һ��������¼����һ�е�֤�� �ڶ���������¼�Ǹ��еڼ���֤�� ������������¼  �����
	private ArrayList<String[][]> primeSymptom=new ArrayList<String[][]>();//�����洢���е�������ֵȺ
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

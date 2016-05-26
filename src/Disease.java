
public class Disease {
	private int diseaseId=0;
	private String name;
	private int count;
	private int[][] prime_Attr;//new double[主属性值的个数][2]//用来记录该疾病在每一列中的主属性值的编号，若某一列没有主属性值则设为-1
	                           //每一行用来记录每个主属性值所在的列下标及主属性值的编号
	public int getDiseaseId() {
		return diseaseId;
	}
	public void setDiseaseId(int diseaseId) {
		this.diseaseId = diseaseId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int[][] getPrime_Attr() {
		return prime_Attr;
	}
	public void setPrime_Attr(int[][] prime_Attr) {
		this.prime_Attr = prime_Attr;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public Disease(int id,String name,int count){
		this.diseaseId=id;
		this.name=name;
		this.count=count;
	}
	

}

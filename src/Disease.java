
public class Disease {
	private int diseaseId=0;
	private String name;
	private int count;
	private int[][] prime_Attr;//new double[������ֵ�ĸ���][2]//������¼�ü�����ÿһ���е�������ֵ�ı�ţ���ĳһ��û��������ֵ����Ϊ-1
	                           //ÿһ��������¼ÿ��������ֵ���ڵ����±꼰������ֵ�ı��
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

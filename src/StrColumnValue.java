//������¼ÿһ���ַ����е�ĳһ��ȡֵ����Ϣ
public class StrColumnValue {
	private int id;
	private String name;
	private int count=0;
	private int[] value_dis;//������¼��ֵ��ÿ�������г��ֵĴ���
	private double[][] prime_dis;//new double[2][dis_arr.length]������¼��ֵ���ĸ�������������ֵ�����Ҽ�¼��ֵ�ڸü����г��ֵ������
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
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int[] getValue_dis() {
		return value_dis;
	}
	public void setValue_dis(int[] value_dis) {
		this.value_dis = value_dis;
	}
	public StrColumnValue(int id1,String name1){
		this.id=id1;
		this.name=name1;
	}
	public double[][] getPrime_dis() {
		return prime_dis;
	}
	public void setPrime_dis(double[][] prime_dis) {
		this.prime_dis = prime_dis;
	}
	
}

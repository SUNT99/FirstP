//������¼ÿһ���������Ե�ֵ
public class NumColumnValue {
	private int id;
	private int count=0;//������ֵ���ֵ��ܴ���
	private double left;//������ֵ����߽�
	private double right;//������ֵ���ұ߽�
	private int[] value_dis;//��������ÿһ�������г��ֵĴ���
	private double[][] prime_dis;//new double[2][dis_arr.length]������¼��ֵ���ĸ�������������ֵ�����Ҽ�¼��ֵ�ڸü����г��ֵ������
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public double getLeft() {
		return left;
	}
	public void setLeft(double left) {
		this.left = left;
	}
	public double getRight() {
		return right;
	}
	public void setRight(double right) {
		this.right = right;
	}
	public int[] getValue_dis() {
		return value_dis;
	}
	public void setValue_dis(int[] value_dis) {
		this.value_dis = value_dis;
	}
	public NumColumnValue(int id2){
		this.id=id2;
	}
	public double[][] getPrime_dis() {
		return prime_dis;
	}
	public void setPrime_dis(double[][] prime_dis) {
		this.prime_dis = prime_dis;
	}
	
	
}

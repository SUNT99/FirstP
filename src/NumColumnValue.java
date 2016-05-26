//用来记录每一列数字属性的值
public class NumColumnValue {
	private int id;
	private int count=0;//该属性值出现的总次数
	private double left;//该属性值的左边界
	private double right;//该属性值的右边界
	private int[] value_dis;//该属性在每一个疾病中出现的次数
	private double[][] prime_dis;//new double[2][dis_arr.length]用来记录该值是哪个疾病的主属性值。并且记录该值在该疾病中出现的特异度
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

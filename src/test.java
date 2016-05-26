
public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int count=0;//记录总行数
		// TODO Auto-generated method stub
        PrimeAttr primeAttr=new PrimeAttr();
        
        
        //letter-recognition.data
        primeAttr.pre_treatment("N",4,2,"G:\\KuGou\\b.txt");
       //第一个参数表示有无编号列，第二个参数表示类型属性在第几列,
	   //第三个参数表示成将连续属性分为几段，第四个参数表示数据文件的路径
        primeAttr.prime(0.5,0.8);
      //  System.out.println("进行分类的结果");
        //primeAttr.classifier();
	}

}

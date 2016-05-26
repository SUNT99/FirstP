import java.math.BigDecimal;
import java.text.DecimalFormat;
public class test3 {

public void sishewuru(double num) {
		
		//  System.out.println(Math.floor(num));//È¡Õû  
		  BigDecimal   b=new   BigDecimal(num);     
		     num=b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();     
		    
		  System.out.println(num);  
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		test3 test3=new test3();
		double num = 5.75557;  
		  
		test3.sishewuru(num);
	}

}

package FPTree;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//sample.txt
		//String filePath = "C:\\Users\\tyz\\Desktop\\a.txt";
		//String filePath = "C:\\Users\\tyz\\Desktop\\afterpre\\feiyan.txt";
		//String filePath = "C:\\Users\\tyz\\Desktop\\afterpre\\letter-recognition.txt";
		
		//String filePath = "C:\\Users\\tyz\\Desktop\\afterpre\\segmentation.txt";
		String filePath = "C:\\Users\\kilo\\Desktop\\b.txt";
		
		//String filePath = "C:\\Users\\tyz\\Desktop\\afterpre\\balance-scale.txt";
		
		//String filePath = "C:\\Users\\tyz\\Desktop\\afterpre\\glass.txt";
        //最小支持度阈值  
        int minSupportCount = 2;  
        //最小信任度阀值
        double minCoverage=0.4;
        double minSpecificity=0.90;
        //String[] classificationSet={"E0","E1","E2"};//discret2
        //String[] classificationSet={"J0","J1","J2","J3","J4","J5"};
       // String[] classificationSet={"E0","E1","E2"};//discret2
        String[] classificationSet={"E0","E1","E2"};//iris
        //String[] classificationSet={"T0","T1","T2","T3","T4","T5","T6"};//segmentation
        //String[] classificationSet={"E0","E1","E2"};//balance-scale
      
        
        FPTreeTool tool = new FPTreeTool(filePath, minSupportCount,minCoverage,minSpecificity,classificationSet);  
        tool.startBuildingTree(); 
        tool.classfier();
        
	}

}

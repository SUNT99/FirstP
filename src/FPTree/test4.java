package FPTree;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;  
//匹配汉字、字符串、数字  
public class test4   
{  
    public static void main(String[] args)   
    {  
       /** String regEx1 = "[\\u4e00-\\u9fa5]";  
        String regEx2 = "[a-z||A-Z]";  
        String regEx3 = "[0-9]";  
        String str = "1 2fdAsz我是hhhZ大傻逼";  
        String s1 = matchResult(Pattern.compile(regEx1),str);  
        String s2 = matchResult(Pattern.compile(regEx2),str);  
        String s3 = matchResult(Pattern.compile(regEx3),str);  
        System.out.println(s1+"\n"+s2+"\n"+s3);  **/
    	test4 test4 =new test4();
    	System.out.println(test4.columnIndex("AA2"));
    	double a[][] =new double[4][2];
    	a[0][0]=1;
    	a[1][0]=1;
    	a[2][0]=3;
    	a[3][0]=3;
    	a[0][1]=5;
    	a[1][1]=4;
    	a[2][1]=4;
    	a[3][1]=8;
    	List<Integer> list=test4.MinDouble(a, 4, 1);
    	System.out.println("###########");
    	for (int num:list) {
			System.out.println(num);
		}
    	System.out.println("###########");
    	
    }   
    public List<Integer> MaxDouble(double[][] arr,int rowsize,int columnId){//返回一个链表，因为最大的可能有好几个，所以用链表来记录了最大的点的下标，
		int MaxIndex=0;
		double MaxValue=arr[0][columnId];
		List<Integer> list=new ArrayList<Integer>();
		for (int i = 0; i < rowsize; i++) {
			if(arr[i][columnId]>MaxValue){
				
				MaxValue=arr[i][columnId];
				MaxIndex=i;
			}
		}
		list.add(MaxIndex);
		for (int i = 0; i < rowsize; i++) {
			if (i==MaxIndex) {
				
			}else if (arr[MaxIndex][columnId]==arr[i][columnId]) {
				list.add(i);
			} 
				
			
		}
		return list;
	}  
    public static String matchResult(Pattern p,String str)  
    {  
        StringBuilder sb = new StringBuilder();  
        Matcher m = p.matcher(str);  
        while (m.find())  
        for (int i = 0; i <= m.groupCount(); i++)   
        {  
            sb.append(m.group());     
        }  
        return sb.toString();  
    }  
    public int columnIndex(String string) {
		int index=0;
		String regEx = "[a-z||A-Z]";  //用来提取字母
        String s = matchResult(Pattern.compile(regEx),string);  
         //A   1  B  2
        if (s.length()==1) {
        	index=s.codePointAt(0)-65;
        }else if (s.length()==2){           //AA  27个  AB
			int shang=s.codePointAt(0)-65;  //商是   0+1 
			int yushu=s.codePointAt(1)-65; //余数是  0+1  
			index=26*(shang+1)+yushu+1-1;
		}
        
        	
		
        return index;
	}
    public List<Integer> MinDouble(double[][] arr,int rowsize,int columnId){//返回一个链表，因为最大的可能有好几个，所以用链表来记录了最大的点的下标，
		int MinIndex=0;
		double MinValue=arr[0][columnId];
		List<Integer> list=new ArrayList<Integer>();
		for (int i = 0; i < rowsize; i++) {
			if(arr[i][columnId]<MinValue){
				
				MinValue=arr[i][columnId];
				MinIndex=i;
			}
		}
		list.add(MinIndex);
		for (int i = 0; i < rowsize; i++) {
			if (i==MinIndex) {
				
			}else if (arr[MinIndex][columnId]==arr[i][columnId]) {
				list.add(i);
			} 
				
			
		}
		return list;
	}  
}  
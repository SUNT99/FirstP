import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
/**
 * 该版本研究了主属性值群的挖掘
 */
/**该版本在离散化处理时采用非递归方法，此方法是通过寻找全局最大信息增益的点
 * 并且 在寻找最佳参数时没有采取针对措施，只是每次输入一组参数后根据结果来找出最佳参数**/
/**
 * 此外，该数据在对数据预处理之后，是把处理的结果以二维数组的方式放在了内存里
 * @author tyz
 *打印信息是   每个属性的断点，每个分类的各个主属性值，以及每个分类有几个主属性值。
 */
/**
 * 
（1）若有主属性值，若主属性值个数最多的疾病唯一，那么该疾病就是预测疾病
（2）若有主属性值，但是不仅仅有一个疾病的主属性值个数最多，则比较最大的特异度，若最大的特异度唯一，则该疾病就是预测疾病。
（3）若有主属性值，但是不仅仅有一个疾病的主属性值个数最多，则比较最大的特异度，若最大的特异度不唯一，那么就找出那么就找出与这一行数据距离最小最小的疾病，该疾病即预测疾病。
（4）若无主属性值的话,那么就找出与这一行数据距离最小的疾病，该疾病即预测疾病。


 * @author tyz
 *
 */
//将数字列和属性列都分别进行对象化
//用来对数据进行预处理   对数字属性进行离散化，对字符串属性以及证候进行编号
public class PrimeAttr {
	private String[][] allArr;//将所有的数据读出来放在这个数组里面
	private String[] dis_arr;//用来记录疾病的名字
	private int count=0;//用来记录一共有多少条记录
	private Disease[] diseases;
	private NumColumn[] numColumns; //用来记录数字属性列
	private StrColumn[] strColumns;//用来记录字符串属性列
	private int numcolumnsize;//用来记录数字列的个数
	private int strcolumnsize;//用来记录字符串列的个数
	private int[] numIndexArr;	//用来记录数字列的下标
	private int[] stringIndexArr;//用来记录字符串列的下标
	private int[] dis_count;//用来记录每种疾病出现的个数
	private int columnsize=0;//用来记录一共有多少列   包括疾病列
	private int[] each_Attr_Prime;//用来记录每个疾病有几个主属性值
	private int MaxClassDistribution;//用来记录最大类分布的疾病的下标
	
	/**
	 * @param args  存储疾病数据记录
	 * @return 
	 */
	//把链表变成数组
	public double sishewuru(double num) {//对double数值四舍五入并且保留2位小数
		  BigDecimal   b=new   BigDecimal(num);     
		  num=b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();     
		  return num; 
	}
	public int jueduizhi(int a,int b){
		int c=0;
		if (a>b) {
			c=a-b;
		}else {
			c=b-a;
		}
		return c;	
	}
	public int chafang(int a,int b) {
		int c=0;
		if (a>b) {
			c=a-b;
		}else {
			c=b-a;
		}
		return c*c;
	}	
	public int[] parseListToArrInt(List<Integer> list){
		int[] arr=new int[list.size()];
		int u=0;
		for (int s:list) {
			arr[u]=s;
			u++;
		}
		return arr;
	}
	
	public double[] parseListToArrDouble(List<Double> list){
		double[] arr=new double[list.size()];
		int u=0;
		for (double s:list) {
			arr[u]=s;
			u++;
		}
		return arr;
	}
	public String[] parseListToArrString(List<String> list){
		String[] arr=new String[list.size()];
		int u=0;
		for (String s:list) {
			arr[u]=s;
			u++;
		}
		return arr;
	}
	public double[][] sorting(double[][] target,int columnlength){
    	for (int i = 0; i < columnlength-1; i++) {
			for (int j = 0; j < columnlength-i-1; j++) {
				if(target[j][0]>target[j+1][0]){
					double temp=target[j][0];
					target[j][0]=target[j+1][0];
					target[j+1][0]=temp;
					
					double tempDisease=target[j][1];
					target[j][1]=target[j+1][1];
					target[j+1][1]=tempDisease;
				}
			}
		}
		return target;
    }
	public double[][] sorting2(double[][] target,int rowSize) {
		for (int i = 0; i < rowSize-1; i++) {
			for (int j = 0; j < rowSize-i-1; j++) {
				if(target[j][0]>target[j+1][0]){
					double temp=target[j][0];
					target[j][0]=target[j+1][0];
					target[j+1][0]=temp;
				}
			}
		}
		return target; 
	} 
	public double[] sorting3(double[] target,int length) {
		for (int i = 0; i < length-1; i++) {
			for (int j = 0; j < length-i-1; j++) {
				if(target[j]>target[j+1]){
					double temp=target[j];
					target[j]=target[j+1];
					target[j+1]=temp;
				}
			}
		}
		return target;
		
	}
	public void bianhao(String ID,int disIndex,String filePath){//第一个参数表示有无编号列，第二个参数表示类型属性在第几列
    	/**
    	 * 接下来该读取文件内容了
    	 */
		List<String> disease=new ArrayList<String>();
		List<String> syndrome=new ArrayList<String>();
    	String encoding="GBK";  //编码格式   	
    	//discretization2.txt  iris.data  balance-scale glass.data letter-recognition.data breast-cancer-wisconsin.data wbpc.data wpbc.data wine.data segmentation.data
    	File file=new File(filePath);//打开文件	
        if(file.isFile() && file.exists()){ //判断文件是否存在 
        	
        	try {
				InputStreamReader isr=new InputStreamReader(new FileInputStream(file),encoding);//考虑到编码格式
				BufferedReader br = new BufferedReader(isr);
				//首先先判断哪些属性是数字，哪些属性是字符串
				String lineTxt = br.readLine();
				StringTokenizer st1=new StringTokenizer(lineTxt,",");
				List<Integer> numIndexList=new ArrayList<Integer>();//用来记录数字列的下标
				List<Integer> stringIndexList=new ArrayList<Integer>();//用来记录字符串列的下标
				String[] numFirstRow=null;
			    System.out.println();
				if (ID.equals("Y")) {//如果有编号ID列的话，且在第0列
					columnsize=st1.countTokens()-1;
					numFirstRow=new String[columnsize];
					int x=0;
					while (st1.hasMoreTokens()) {
						if (x==0) {
                           String a=st1.nextToken();
						}else {
							numFirstRow[x-1]=st1.nextToken();				
						}
						x++;
					}	
				}else if (ID.equals("N")) {
					columnsize=st1.countTokens();
					numFirstRow=new String[columnsize];
					int x=0;
					while (st1.hasMoreTokens()) {
						numFirstRow[x]=st1.nextToken();
						x++;		
					}	
				}
				for (int i = 0; i < numFirstRow.length; i++) {
					String value=numFirstRow[i];
						if (i==disIndex) {
						}else if (i<disIndex) {
							try {
					            double b=Double.parseDouble(value);//把是数字的那些属性的列的下标记录下来，加到numIndexList中来
					            numIndexList.add(i);        
					        } catch (Exception e) {
					        	stringIndexList.add(i);//把是字符串的那些属性的列的下标记录下来，加到stringIndexList中来		          
					        }	
						} else if (i>disIndex) {
							try {
					            double b=Double.parseDouble(value);//把是数字的那些属性的列的下标记录下来，加到numIndexList中来
					            numIndexList.add(i-1);     
					        } catch (Exception e) {
					        	stringIndexList.add(i-1);//把是字符串的那些属性的列的下标记录下来，加到stringIndexList中来	          
					        }
						}
				}	
			    numcolumnsize=numIndexList.size();
				if (numcolumnsize!=0) {   
					numIndexArr=parseListToArrInt(numIndexList);
					numColumns=new NumColumn[numcolumnsize];
					int w=0;
					for (int i = 0; i < numIndexArr.length; i++) {
						numColumns[i]=new NumColumn(numIndexArr[i]);
					}
				}
				strcolumnsize=stringIndexList.size();	
				if (strcolumnsize!=0) {
					stringIndexArr=parseListToArrInt(stringIndexList);
					strColumns=new StrColumn[strcolumnsize];
					int v=0;
					for (int i = 0; i < stringIndexArr.length; i++) {
						strColumns[i]=new StrColumn(stringIndexArr[i]);
					}	
				}
				List<String> allList=new ArrayList<String>();//用来记录所有的数据
				while(lineTxt!= null){//读取每一行内容	从第一行开始
					count++;
					StringTokenizer st = new StringTokenizer(lineTxt,",");	
					String[] arr =new String[columnsize];
					int j=0;
					if (ID.equals("Y")) {//如果有编号列的话
						while(st.hasMoreTokens()){
							if (j==0) {
								String aString=st.nextToken();
							}else {
								arr[j-1]=st.nextToken();
								allList.add(arr[j-1]);
								
							}
							j++; 
						}
						if (disease.contains(arr[disIndex])) {//将每一行中的疾病加入到disease链表中		
						}else {
							disease.add(arr[disIndex]);
						}	
						lineTxt=br.readLine();
					}else if (ID.equals("N")) {//无编号列的话
						while(st.hasMoreTokens()){
							arr[j]=st.nextToken();
							allList.add(arr[j]);
							j++;  
						}
						if (disease.contains(arr[disIndex])) {//将每一行中的疾病加入到disease链表中		
						}else {
							disease.add(arr[disIndex]);
						}	
						lineTxt=br.readLine();
					}
				}
			//	System.out.println("记录总数是"+count);
			//	System.out.println("类型总数是"+disease.size());
				 //对疾病进行编号
				dis_arr=parseListToArrString(disease);
				allArr=new String[count][columnsize];//用来存储所有的数据
				dis_count=new int[dis_arr.length];
				int u=0;
				int rowID=0;
				int columnID=0;
				Iterator<String> iterator=allList.iterator();
				for (int i = 0; i < allList.size(); i++) {
					String s=iterator.next();				
					rowID=i/columnsize;
					columnID=i%columnsize;	
					if (columnID<disIndex) {
						allArr[rowID][columnID]=s;
					}else if (columnID==disIndex) {
						for (int j = 0; j < dis_arr.length; j++) {
							if (s.equals(dis_arr[j])) {
								allArr[rowID][columnsize-1]=j+"";
								int h=dis_count[j];
								dis_count[j]=h+1;
								break;
							}
					   }	
					}else if (columnID>disIndex) {
						allArr[rowID][columnID-1]=s;
					} 
				}
				//System.out.println();
				diseases=new Disease[dis_arr.length];
				for (int i = 0; i < dis_count.length; i++) {
					diseases[i]=new Disease(i,dis_arr[i],dis_count[i]);
					System.out.println("第 "+i+" 个分类"+dis_arr[i]+"出现的次数是 "+dis_count[i]);
				}
				MaxClassDistribution=MaxInt(dis_count);
				if (strcolumnsize>0) { //如果有字符串的属性列的话，
					for (int i = 0; i < strColumns.length; i++) {//在每一个字符串属性中s
						HashMap<String, String> strValuehm=new HashMap<String, String>();//用来记录每一列字符串属性列的取值
						int columnId=strColumns[i].getColumnId();				
						for (int k = 0; k < count; k++) {//遍历某一列字符串列的所有行，把出现的名字记录在strValuehm哈希表中
							String strValue=allArr[k][columnId];
							if (strValuehm.containsKey(strValue)) {	
							}else {
								strValuehm.put(strValue, strValue);
							}
						}
						int valueNum=strValuehm.size();
						strColumns[i].setStrColVal(new StrColumnValue[valueNum]);
						Iterator<String> iterator2=strValuehm.keySet().iterator();
						int q=0;
						StrColumnValue[] s=strColumns[i].getStrColVal();//记录该属性i一共有多少个值
						while (iterator2.hasNext()) {//把该字符串列中的名字记录到每一个字符串名字对象中
							String name=iterator2.next();
							s=strColumns[i].getStrColVal();
							s[q]=new StrColumnValue(q, name);
							s[q].setValue_dis(new int[dis_arr.length]);
							s[q].setPrime_dis(new double[2][dis_arr.length]);
							q++;
						}
						for (int k = 0; k < count; k++) {  //遍历该列字符串列的每一行
							for (int k2 = 0; k2 < s.length; k2++) {//在StrColumnValue[] s中去寻找该行出现的属性的值，如果找到了，就将该属性值的次数加一，同时将allArr中该位置的属性值变成该属性值的编号
								String name1=s[k2].getName();
								String name2=allArr[k][columnId];
								if (name1.equals(name2)) {//
									int a=s[k2].getCount();
									s[k2].setCount(a+1);
									allArr[k][columnId]=s[k2].getId()+"";
									//统计该属性值在每一个疾病中出现的总次数
									int dis_index=Integer.parseInt(allArr[k][columnsize-1]);	
									int c=s[k2].getValue_dis()[dis_index];
									s[k2].getValue_dis()[dis_index]=c+1;
								}
							}			
						}
						for (int j = 0; j < s.length; j++) {
							StrColumnValue scv=s[j];
			
						    int[] value_dis=s[j].getValue_dis();
						}
						
						
					}
					
				}			  
				 br.close();
				 isr.close();
				 
			} catch (Exception e) {
				System.out.println("读取文件内容出错");
				e.printStackTrace();
			}	
        }else {
			System.out.println("找不到指定文件");
		}  
        
        
        
	}
	public double Info(double basecount,double[] countArr){
		double info=0;		
		for (int i = 0; i < countArr.length; i++) {
			if (countArr[i]==0) {	
			}else {
				info=info-(countArr[i]/basecount)*(Math.log(countArr[i]/basecount)/Math.log(2));
			}		
		} 
		return info;	
	}
	public double AllValueofRange(double left,double right,double[][] AttrArrInFo){//用来计算在这个属性在这个范围内的取值有多少个
		double count=0;//(114,147)中间的个数
		//计算(114,147)中间的个数
		for (int i = 0; i < AttrArrInFo.length; i++) {
			if ((AttrArrInFo[i][0]>left||AttrArrInFo[i][0]==left)&&(AttrArrInFo[i][0]<right||AttrArrInFo[i][0]==right)) {
				count=count+AttrArrInFo[i][3];
			}
		}
		return count;	
	}
	//找出（114,121）中的各个疾病的个数
	public double[] eachdisCount(double left,double right,double[][] AttrArrInFo,double[] diseaseArr){
		double countArr[]=new double[diseaseArr.length];//用来记录每一个疾病在（114,147）范围内出现的次数
		for (int i = 0; i < diseaseArr.length; i++) {
			for (int j = 0; j <AttrArrInFo.length ; j++) {
				if ((AttrArrInFo[j][0]>left||AttrArrInFo[j][0]==left)&&(AttrArrInFo[j][0]<right||AttrArrInFo[j][0]==right)) {
					countArr[i]=countArr[i]+AttrArrInFo[j][4+2*i+1];
				}
			}
		}
		return countArr;	
	}
	//求最大的信息增益
	public List<Integer> MaxDouble(double[][] arr,int columnsize,int rowId){//返回一个链表，因为最大的可能有好几个，所以用链表来记录了最大的点的下标，
		int MaxIndex=0;
		double MaxValue=arr[rowId][0];
		List<Integer> list=new ArrayList<Integer>();
		for (int i = 0; i < columnsize; i++) {
			if(arr[rowId][i]>MaxValue){
				
				MaxValue=arr[rowId][i];
				MaxIndex=i;
			}
		}
		list.add(MaxIndex);
		for (int i = 0; i < columnsize; i++) {
			if (i==MaxIndex) {
				
			}else if (arr[rowId][MaxIndex]==arr[rowId][i]) {
				list.add(i);
			} 
				
			
		}
		return list;
	}
	public int MaxInt(int[] arr){
		int MaxIndex=0;
		int MaxValue=arr[0];
		for (int i = 0; i < arr.length; i++) {
			if(arr[i]>MaxValue){
				
				MaxValue=arr[i];
				MaxIndex=i;
			}
		}
		return MaxIndex;
	}
	public int MinInfo(double[] breakInfoArr) {
		int MinIndex=0;
		double MinInfo=breakInfoArr[0];
		for (int i = 0; i < breakInfoArr.length; i++) {
			if (breakInfoArr[i]<MinInfo) {
				MinInfo=breakInfoArr[i];
				MinIndex=i;
			}
		}
		return MinIndex;
	}
	public int MaxInfoGain(double[] breakInfoGainArr){
		int MaxIndex=0;
		double MaxGain=breakInfoGainArr[0];
		for (int i = 0; i < breakInfoGainArr.length; i++) {
			if(breakInfoGainArr[i]>MaxGain){
				
				MaxGain=breakInfoGainArr[i];
				MaxIndex=i;
			}
		}
		return MaxIndex;
	}
	//向一个文件输出预处理结果
	public static void exportData(String fileName, String[][] allArr) {
	HashMap< String, String> hm=new HashMap<String, String>();
	try {  
	//打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件  
		FileWriter writer = new FileWriter(fileName, false);  
		
	   	for (int i = 0; i < allArr.length; i++) {
			//writer.write(i+" ");
			for (int j = 0; j < allArr[0].length; j++) {
				int yushu=j%26;
				int shang=j/26;
				if (shang==0) {
					char chars=(char)(j+65);
					writer.write(chars+allArr[i][j]+" ");
				}else {
					char chars1=(char)(shang-1+65);
					//System.out.println(chars1);
					char chars2=(char)(yushu+65);
					//System.out.println(chars2);
					writer.write(chars1+""+chars2+allArr[i][j]+" ");
					if(i==79){
						hm.put(chars1+""+chars2+allArr[i][j], chars1+""+chars2+allArr[i][j]);
						
					}
				}
				
			}
			writer.write("\n");
		  
	   	}
	   	writer.close();
	} catch (IOException e) {  
		e.printStackTrace();  
	} 
	Iterator<String> iterator=hm.keySet().iterator();
	while (iterator.hasNext()) {
		String key=iterator.next();
		String value=hm.get(key);
		System.out.println(value);
		
	}
	
	}

	
	public static void exportDataNozimu(String fileName, String[][] allArr) {
		HashMap< String, String> hm=new HashMap<String, String>();
		try {  
		//打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件  
			FileWriter writer = new FileWriter(fileName, false);  
			
		   /**	for (int i = 0; i < allArr.length; i++) {
				//writer.write(i+" ");
				for (int j = 0; j < allArr[0].length; j++) {
					int yushu=j%26;
					int shang=j/26;
					if (shang==0) {
						char chars=(char)(j+65);
						writer.write(chars+allArr[i][j]+" ");
					}else {
						char chars1=(char)(shang-1+65);
						//System.out.println(chars1);
						char chars2=(char)(yushu+65);
						//System.out.println(chars2);
						writer.write(chars1+""+chars2+allArr[i][j]+" ");
						if(i==79){
							hm.put(chars1+""+chars2+allArr[i][j], chars1+""+chars2+allArr[i][j]);
							
						}
						
						
					}**/
					
			for (int i = 0; i < allArr.length; i++) {
				//writer.write(i+" ");
				for (int j = 0; j < allArr[0].length; j++) {
					
						writer.write(allArr[i][j]+" ");
					
		       }
				writer.write("\n");
		    }
			writer.close();  
		} catch (IOException e) {  
			e.printStackTrace();  
		} 
		Iterator<String> iterator=hm.keySet().iterator();
		while (iterator.hasNext()) {
			String key=iterator.next();
			String value=hm.get(key);
			System.out.println(value);
			
		}
		
		}
	public void discret2(int areascount,double left, double right,double[] breakpointArr,double[][] AttrArrInFo,double[] diseaseArr,List<Double> maxbreakList, List<Double> maxInfoGainList){
		
		double[] limitPoint={left,right};
		maxbreakList.add(left);
		maxbreakList.add(right);
		int areaCount=areascount;//要分成的区域段数
		int breakCount=areaCount-1;//要找的断点个数
		double[] eachAreaInfoArr=null;//用来记录每一段区域的信息
		for (int i = 0; i <breakCount; i++) {//在找断点的每一轮中  比如第3轮，则此时已经找到了2个断点，即找第三个断点的时候
			
			if (i==0) {
				double[] eachDisCount=eachdisCount(left, right, AttrArrInFo, diseaseArr);
				double info=Info(count, eachDisCount);
				eachAreaInfoArr=new double[]{info};
			}
		    int areas_count=limitPoint.length-1;//记录当前已经有几个区域
		    double[] maxbreakpoint=new double[areas_count];//用来记录每个区域内的最大信息增益断点
		    double[][] each_area_left_right_global=new double[areas_count][2];//用来记录所有的区域中找到的最大的信息增益断点的左右全局信息量
			double[] each_area_maxbreak_globalInfo=new double[areas_count];//用来记录每个段的全局信息量
			
			                    //areas_count   便利当前已经有的几个区域，找出每个区域的最大信息增益点并比较全局信息量
			
			int sum=0;//用来记录有几个区域有可能的断点
			for (int j = 0; j < limitPoint.length-1; j++) {//在每一段中找到信息增益最大的点并且记录他们的信息熵，以及全局的信息熵
				double leftLimit=limitPoint[j];
				double rightLimit=limitPoint[j+1]; //
				double basecount=AllValueofRange(leftLimit, rightLimit, AttrArrInFo);//首先找出这一段区域一共有多少个值
				double countArr[]=eachdisCount(leftLimit, rightLimit, AttrArrInFo, diseaseArr);//用来记录每一个疾病在这一段区域范围内出现的次数
				double beginInfo=Info(basecount,countArr);//记录了数据集最初始的信息
				List<Double> brlist=new ArrayList<Double>(); //这一段区域内有哪些可能的断点
				for (int k = 0; k < breakpointArr.length; k++) {
					if (breakpointArr[k]>leftLimit&&breakpointArr[k]<rightLimit) {
						brlist.add(breakpointArr[k]);
					}
				}
				if (brlist.size()==0) {
					each_area_maxbreak_globalInfo[j]=100;
					
					
				}else {
					sum++;
					double[] brArr=parseListToArrDouble(brlist);//用来记录这一段区域有哪些可能的断点
					double[] breakInfoGainArr=new double[brArr.length];  //每个可能断点的信息增量
					double[] breakInfo=new double[brArr.length];//每个可能断点的信息量
					double[][] left_right_globalInfo=new double[brArr.length][2];//用来记录这一段区域内左右全局变量
					for (int k = 0; k < brArr.length; k++) {
						double breakpoint=brArr[k];
				
						double countleft=AllValueofRange(leftLimit, breakpoint, AttrArrInFo);
					
						double[] eachDisCount_left=eachdisCount(leftLimit, breakpoint, AttrArrInFo, diseaseArr);
						
						double Info_left=Info(countleft, eachDisCount_left);
						double global_left_Info=countleft/count*Info_left;
						left_right_globalInfo[k][0]=global_left_Info;
						double countright=AllValueofRange(breakpoint, rightLimit, AttrArrInFo);
						double[] eachDisCount_right=eachdisCount(breakpoint, rightLimit, AttrArrInFo, diseaseArr);
						double Info_right=Info(countright,eachDisCount_right);
						double global_right_Info=countright/count*Info_right;
						left_right_globalInfo[k][1]=global_right_Info;
						
						
						double InfoAfterBreak=0;
						double weightleft=countleft/basecount;
						double weightright=countright/basecount;
						InfoAfterBreak=weightleft*Info_left+weightright*Info_right;
						
						double InfoGain=beginInfo-InfoAfterBreak;
				    	breakInfoGainArr[k]=InfoGain;   //为每个可能断点的信息增益赋值
				    	breakInfo[k]=InfoAfterBreak;//为每个可能断点的信息量赋值
					}
					int MaxIndex=MaxInfoGain(breakInfoGainArr);	//找出信息增益最大的点
					double MaxBreakpoint=brArr[MaxIndex];
				
					
					double MaxInfoGain=breakInfoGainArr[MaxIndex];	
					double MaxBreakpoint_Info=breakInfo[MaxIndex];
			
					
					each_area_left_right_global[j][0]=left_right_globalInfo[MaxIndex][0];
					each_area_left_right_global[j][1]=left_right_globalInfo[MaxIndex][1];
			
				    
					
					
					double global_Info=each_area_left_right_global[j][0]+each_area_left_right_global[j][1];
					for (int k = 0; k < eachAreaInfoArr.length; k++) {
						if (k==j) {
							
						}else {
							global_Info=global_Info+eachAreaInfoArr[k];
						}
					}
					
					each_area_maxbreak_globalInfo[j]=global_Info;
					maxbreakpoint[j]=MaxBreakpoint;
				}
				}
			if (sum==0) {
				return;
			}
			//比较这几段中的几个最大的信息增益，看哪个段中的最大信息增益点能使全局信息量最大	
			int MinglobalIndex=MinInfo(each_area_maxbreak_globalInfo);//用来记录该轮中  全局信息量最大的点
			double MinglobalPoint=maxbreakpoint[MinglobalIndex];
			double MinglobalInfo=each_area_maxbreak_globalInfo[MinglobalIndex];
			maxbreakList.add(MinglobalPoint);
			limitPoint=parseListToArrDouble(maxbreakList);	
			sorting3(limitPoint, limitPoint.length);		
			double[] temp=new double[areas_count+1];
			for (int k = 0; k < temp.length; k++) {
				if(k<MinglobalIndex){
					temp[k]=eachAreaInfoArr[k];
				}else if (k==MinglobalIndex) {
					temp[k]=each_area_left_right_global[MinglobalIndex][0];
				}else if (k==MinglobalIndex+1) {
					temp[k]=each_area_left_right_global[MinglobalIndex][1];
				}else {
					temp[k]=eachAreaInfoArr[k-1];
				}
			}
			eachAreaInfoArr=temp;
		}		
	}
	
	public void discret(int areascount) {
		//编码格式   	
	    if (numcolumnsize!=0) {
	    	numColumns=new NumColumn[numcolumnsize];
		    //把所有的数字属性和疾病属性读出来
		    int row=count;//35行 
		    int column=numIndexArr.length+1;//2列  数字列和疾病列
		    int allcolumn=columnsize;
		    String[][] numValueArr=new String[row][column];//double[35][5]//用来记录数字属性值和疾病值
		    for (int i = 0; i < row; i++) {   //将每一行中数字加入numValueArr中，同时将疾病的编号也加入其中
				for (int j = 0; j < numIndexArr.length; j++) {
					int index=numIndexArr[j];
					numValueArr[i][j]=allArr[i][index];
				}
				numValueArr[i][column-1]=allArr[i][allcolumn-1];
			}
		 
		    for (int i = 0; i < column-1; i++) {//在每一个属性的一列值中  即遍历每一个属性
		    	
		    	double[][] value_dis = new double[count][2];//用来记录每一列属性值与属性值对应的疾病
		    	for (int j = 0; j < count; j++) {
					value_dis[j][0]=Double.parseDouble(numValueArr[j][i]);  //记录该行的第几个数字值
					value_dis[j][1]=Double.parseDouble(numValueArr[j][column-1]);//记录那个疾病
				}	
				sorting(value_dis, count);
				HashMap<Double, Double> AttrNumhm=new HashMap<Double, Double>();//用来记录每一个属性的特异值
				for (int j = 0; j < count; j++) {
					AttrNumhm.put(value_dis[j][0], value_dis[j][0]);
				}
				int AttrCount=AttrNumhm.size();//该属性一共有多少个值
				int columnlength=4+dis_arr.length*2;
				double[][] AttrArrInFo=new double[AttrCount][columnlength];//用来记录属性A的每一个取值点处的信息
				Iterator<Double> iterator=AttrNumhm.keySet().iterator();
				int u=0;
				while (iterator.hasNext()) {//将属性A的各种取值填到AttrArrInFo中的第一列中
					AttrArrInFo[u][0]=iterator.next();
					u++;
				}
				sorting2(AttrArrInFo, AttrCount);//将属性值排序
				for (int j = 0; j < AttrCount; j++) {//遍历AttrArrInFo中的每一行
					HashMap<Double, Double> eachValue_disease=new HashMap<Double, Double>();//用来记录都有哪些疾病在该属性上取了该值
					int allCount=0;//用来记录该属性的该个值一共出现了多少次
					for (int j2 = 0; j2 < count; j2++) {//遍历column中的每一列中属性值
						if (AttrArrInFo[j][0]==value_dis[j2][0]) {
							eachValue_disease.put(value_dis[j2][1],value_dis[j2][1]);
							allCount++;
						}
					}
					AttrArrInFo[j][3]=allCount;
					AttrArrInFo[j][1]=eachValue_disease.size();////对AttrArrInFo[j][]的和第二列进行设置。记录了每种取值有几个疾病
					if (AttrArrInFo[j][1]==1) {//如果该属性中的值114如果只出现了一种疾病，则把该疾病的编号记录到第3列中
						Iterator<Double> iterator3=eachValue_disease.keySet().iterator();
						while (iterator3.hasNext()) {
							AttrArrInFo[j][2]=iterator3.next();
						}
					}else {
						AttrArrInFo[j][2]=-1;
					}
					for (int l = 0; l < dis_arr.length; l++) {
						AttrArrInFo[j][4+2*l]=l;
						int eachDis_Value_Count=0;
						for (int v = 0; v < count; v++) {//统计每一个疾病取该值的次数
							if (value_dis[v][0]==AttrArrInFo[j][0]&&value_dis[v][1]==l) {
								int a=eachDis_Value_Count;
								eachDis_Value_Count=a+1;
							}
						}
						AttrArrInFo[j][4+2*l+1]=eachDis_Value_Count;
					}
				}
				
				List<Double> breakList=new ArrayList<Double>();
				Double breakpoint=0D;
				for (int j = 0; j < AttrCount-1; j++) {//找出所有断点
					if (AttrArrInFo[j][1]!=1||AttrArrInFo[j+1][1]!=1) {
						breakpoint=(AttrArrInFo[j][0]+AttrArrInFo[j+1][0])/2;
						breakList.add(breakpoint);
					}else if(AttrArrInFo[j][1]==1&&AttrArrInFo[j+1][1]==1){
						if (AttrArrInFo[j][2]==AttrArrInFo[j+1][2]) {
							
						}
						else {
							breakpoint=(AttrArrInFo[j][0]+AttrArrInFo[j+1][0])/2;
							breakList.add(breakpoint);
						}
					}
				}
				int breakpointCount=breakList.size();
				double[] breakpointArr=new double[breakpointCount];
				int p=0;
				for (double s:breakList) {
					breakpointArr[p]=s;
					//System.out.println(s);
					p++;
				}			
				double left=AttrArrInFo[0][0];//用来记录该属性的最小取值
				double right=AttrArrInFo[AttrArrInFo.length-1][0];//用来记录该属性的最大取值
				List<Double> maxbreakList=new ArrayList<Double>();
				List<Double> maxInfoGainList=new ArrayList<Double>();
				int breakCount=0;
				double[] diseaseArr=new double[dis_arr.length];
				for (int j = 0; j < diseaseArr.length; j++) {
					diseaseArr[j]=j;
				}
				System.out.println(i+" left "+left);
				System.out.println(i+" right "+right);
				discret2(areascount,left,right,breakpointArr,AttrArrInFo,diseaseArr,maxbreakList,maxInfoGainList);
				
				List<Double> breakPointResult=new ArrayList<Double>();
				double[] temparr=parseListToArrDouble(maxbreakList);
				sorting3(temparr, temparr.length);
				for (int j = 0; j < temparr.length; j++) {
					if (j==0) {
						
					}else if(j==temparr.length-1){
						
					}else {
						breakPointResult.add(temparr[j]);
					}
				}
				
				int Maxbreakcount=breakPointResult.size();
				System.out.println();
				System.out.println("第"+i+"个数值属性的最大信息增益断点的个数： "+Maxbreakcount);
				double[] maxbreakArr=null;
				if (Maxbreakcount!=0) {
					
					maxbreakArr=parseListToArrDouble(breakPointResult);
					System.out.print("断点是：  ");
					for (double s:breakPointResult) {
						System.out.print(s+" ");
					}	
					System.out.println();
					//System.out.println();
				
					for (double s:maxInfoGainList) {
					  // System.out.println(s);	
					}	
				}
				int numcolumnid=numIndexArr[i];
				numColumns[i]=new NumColumn(numcolumnid);//创建该数字列对象，令其编号为i
				numColumns[i].setNumColVal(new NumColumnValue[Maxbreakcount+1]);//创建该数字列对象中的属性值的数组
				NumColumnValue[] numColumnValues=numColumns[i].getNumColVal();
				int rangenum=2+Maxbreakcount;
				double[] range=new double[rangenum];//用来记录该数组的起始点，各个最大信息增益断点，结束点
				for (int j = 0; j < range.length; j++) {
					if (Maxbreakcount!=0) {
						if (j==0) {
							range[j]=AttrArrInFo[0][0];
						}else if (j==range.length-1) {
							range[j]=AttrArrInFo[AttrCount-1][0];
						}else{
							range[j]=maxbreakArr[j-1];
						}
					}else {
						if (j==0) {
							range[j]=AttrArrInFo[0][0];
						}else if (j==range.length-1) {
							range[j]=AttrArrInFo[AttrCount-1][0];
						}
					}		
				}
				for (int j = 0; j <range.length-1 ; j++) {//遍历该数字列对象中的每一个属性值对象，将其左边界和右边界分别进行设置
					numColumnValues[j]=new NumColumnValue(j);
					numColumnValues[j].setLeft(range[j]);
					numColumnValues[j].setRight(range[j+1]);
					numColumnValues[j].setValue_dis(new int[dis_arr.length]);
					
					numColumnValues[j].setPrime_dis(new double[2][dis_arr.length]);
				}
				for (int j = 0; j <count; j++) {//遍历第i个数字列中的每一行，将其数字判断其属于的范围并且用编号代替
					double value=Double.parseDouble(allArr[j][numcolumnid]);
					for (int k = 0; k < range.length-1; k++) {
						if ((value>range[k]||value==range[k])&&(value<range[k+1]||value==range[k+1])) {
							allArr[j][numcolumnid]=k+"";
							int a=numColumnValues[k].getCount();
							numColumnValues[k].setCount(a+1);
							int disid=Integer.parseInt(allArr[j][columnsize-1]);
							int b=numColumnValues[k].getValue_dis()[disid];
							numColumnValues[k].getValue_dis()[disid]=b+1;
						}
					}
				}	
							
		    }
	    }

	}
	
    public void prime(double min_coverage,double min_specificity) {//第一个参数表示编号列的下标，若为-1则表示无编号列   第二个参数表示疾病列的下标
		//System.out.println("------------------------------");
		each_Attr_Prime=new int[dis_arr.length];
		
		for (int i = 0; i < dis_arr.length; i++) {
			double support_D1=dis_count[i];
			double support_NoD1=count-support_D1;
			List<Integer> prime_colu_id=new ArrayList<Integer>();//用来记录该疾病在哪列有主属性值  可以重复
			List<Integer> prime_value_bianhao=new ArrayList<Integer>();//用来记录该疾病的主属性质的编号
			
			if (strcolumnsize!=0) {//如果strcolumnsize不为0的话
				 
				for (int j = 0; j < strcolumnsize; j++) {
					int str_index=stringIndexArr[j];
					StrColumn strColumn=strColumns[j];
					int columnid=strColumn.getColumnId();
					StrColumnValue[] strColumnValues=strColumn.getStrColVal();
					for (int k = 0; k < strColumnValues.length; k++) {
						
					    double support_X1_D1=strColumnValues[k].getValue_dis()[i];
					    double coverage=support_X1_D1/support_D1;
					    double support_X1_NoD1=strColumnValues[k].getCount()-support_X1_D1;
					    double support_NoX1_NoD1=support_NoD1-support_X1_NoD1;
					    double specificity=coverage/(support_X1_NoD1/support_NoD1);
					   // double specificity=support_NoX1_NoD1/support_NoD1;
					    if (coverage>min_coverage && specificity>min_specificity) {
					    	each_Attr_Prime[i]++;
							System.out.println("第"+i+"个分类"+dis_arr[i]+"的主属性值是： 第"+columnid+"列字符串属性值 "+strColumnValues[k].getName()+"其中它中的覆盖率：  "+coverage+" 特异性："+specificity);
							strColumn.getStrColVal()[k].getPrime_dis()[0][i]=1;
							strColumn.getStrColVal()[k].getPrime_dis()[1][i]=specificity;
							prime_colu_id.add(str_index);
							prime_value_bianhao.add(k);
							
						}else {
							strColumn.getStrColVal()[k].getPrime_dis()[0][i]=0;
							strColumn.getStrColVal()[k].getPrime_dis()[1][i]=0;
						}
					}
				}
			}
			if (numcolumnsize!=0) {
				for (int j = 0; j < numcolumnsize; j++) {
					int num_index=numIndexArr[j];
					NumColumn numColumn=numColumns[j];
					int columnid=numColumn.getColumnId();
					NumColumnValue[] numColumnValues=numColumn.getNumColVal();
					for (int k = 0; k < numColumnValues.length; k++) {
					    double support_X1_D1=numColumnValues[k].getValue_dis()[i];
					    double coverage=support_X1_D1/support_D1;
					    double support_X1_NoD1=numColumnValues[k].getCount()-support_X1_D1;
					    double support_NoX1_NoD1=support_NoD1-support_X1_NoD1;
					    double specificity=coverage/(support_X1_NoD1/support_NoD1);
					   // double specificity=support_NoX1_NoD1/support_NoD1;
					    if (coverage>min_coverage && specificity>min_specificity) {
					    	each_Attr_Prime[i]++;
							System.out.println("第"+i+"个分类"+dis_arr[i]+"的主属性值是： 第"+columnid+" 列的第"+k+"个数字属性值 "+" 左边界是 "+numColumnValues[k].getLeft()+" 右边界是："+numColumnValues[k].getRight()+"其中它的覆盖率：  "+coverage+"特异性："+specificity);
							numColumn.getNumColVal()[k].getPrime_dis()[0][i]=1;
							numColumn.getNumColVal()[k].getPrime_dis()[1][i]=specificity;
							prime_colu_id.add(num_index);
							prime_value_bianhao.add(k);
							
					    }else {
					    	numColumn.getNumColVal()[k].getPrime_dis()[0][i]=0;
							numColumn.getNumColVal()[k].getPrime_dis()[1][i]=0;
						}
					}
				}
			}
			Disease disease=diseases[i];
			int prime_num=each_Attr_Prime[i];
			disease.setPrime_Attr(new int[prime_num][2]);
			Iterator< Integer> iterator=prime_colu_id.iterator();
			Iterator<Integer> iterator2=prime_value_bianhao.iterator();
		    
			for (int j = 0; j < prime_num; j++) {
		        disease.getPrime_Attr()[j][0]=iterator.next();
		        disease.getPrime_Attr()[j][1]=iterator2.next();
			}
			//System.out.println();
		}
		
		int  count=0;
		for (int j = 0; j < dis_arr.length; j++) {
			int[][] arr=diseases[j].getPrime_Attr();
			int temp=count;
			count=temp+arr.length;
			//System.out.println("疾病"+dis_arr[j]+"的主属性值个数"+arr.length+" :");
			for (int k = 0; k < arr.length; k++) {
				//System.out.println("第 "+arr[k][0]+" 列，第"+arr[k][1]+" 个值");
			}
			//System.out.println();
			
		}
		//System.out.println(count);
		double average=(double)count/(double)dis_arr.length;
		System.out.println("主属性值平均个数 "+average);
		/**double[][] prime_Dis=numColumns[0].getNumColVal()[0].getPrime_dis();
		
		for (int j = 0; j < dis_arr.length; j++) {
			System.out.println("第"+j+"个疾病"+prime_Dis[0][j]);
			System.out.println(prime_Dis[1][j]);
		}**/
	}
    public void pre_treatment(String ID,int disIndex,int areascount,String filePath){
    	bianhao(ID, disIndex,filePath);
    	discret(areascount);	
    	System.out.println("行数为"+allArr.length+",列数为： "+allArr[0].length);
    	System.out.println();
		for (int i = 0; i < allArr.length; i++) {
			for (int j = 0; j < columnsize; j++) {
				System.out.print(allArr[i][j]+" ");
			}
			System.out.println();
		}
    	String fileName="C:\\Users\\kilo\\Desktop\\b.txt";
    	exportData(fileName,allArr);
    }
    
    
    public void classifier(){  //分类器  用来测试主属性值算法的好坏
    	
    	for (int i = 0; i <allArr.length; i++) {
			for (int j = 0; j < columnsize; j++) {
				System.out.print(allArr[i][j]+":");
			}
			System.out.println();
		}
    	
    	double correct=0;
        double a1=0;
        double a1cor=0;
        double a2=0;
        double a2cor=0;
    	
        double a3=0;
        double a3cor=0;
        double a33=0;
        		
        double a4=0;
        double a4cor=0;
        		
    	for (int i = 0; i <allArr.length ; i++) {//遍历每一行
    		double[][] prime_specificity=new double[3][dis_arr.length];//第一行用来记录该证候有几个主属性值出现了在该行，第二行用来记录该疾病的每个主属性值的特异度总和，第三行用来记录该主属性值最大的特异度。
    		if (strcolumnsize!=0) {
			for (int j = 0; j < strcolumnsize; j++) {//遍历每一个字符串列
				int strIndex=stringIndexArr[j];//获取字符串列的下标
				String s=allArr[i][strIndex];//获取该字符串
				//System.out.println(s);
				int strValbianhao=Integer.parseInt(s);
				StrColumnValue[] strColumnValues=strColumns[j].getStrColVal();
				//System.out.println(strColumns[j].getStrColVal());
				double[][] prime_dis=strColumnValues[strValbianhao].getPrime_dis();
				for (int l = 0; l < dis_arr.length; l++) {
					if (prime_dis[0][l]==1) {//如果该字符串是第l个疾病的主属性值
						prime_specificity[0][l]++;//第l个疾病的主属性值个数加1
						double a=prime_specificity[1][l];
						prime_specificity[1][l]=a+prime_dis[1][l];//统计第l个疾病的主属性值的特异度的和
						if (prime_specificity[2][l]<prime_dis[1][l]) {
							prime_specificity[2][l]=prime_dis[1][l];
						}
					}
				}	
			}
		}
    		if (numcolumnsize!=0) {
				for (int j = 0; j < numcolumnsize; j++) {//遍历每一行数字列
					NumColumn numColumn=numColumns[j];    //numColumns[0]
					int numIndex=numIndexArr[j];//获取数字列的下标    numIndex=0
					String string=allArr[i][numIndex];
					int numValuebianhao=Integer.parseInt(string);//获取该数字值在该列数字离散化后的编号0
					NumColumnValue[] numColumnValues=numColumn.getNumColVal();
					double[][] prime_dis=numColumnValues[numValuebianhao].getPrime_dis();
					for (int l = 0; l < dis_arr.length; l++) {
						if (prime_dis[0][l]==1) {//如果该数值是第l个疾病的主属性值
							prime_specificity[0][l]++;//第l个疾病的主属性值个数加1	
							double a=prime_specificity[1][l];
							prime_specificity[1][l]=a+prime_dis[1][l];//统计第l个疾病的主属性值的特异度的和
							
							if (prime_specificity[2][l]<prime_dis[1][l]) {
								prime_specificity[2][l]=prime_dis[1][l];
							}
							
						}
					}
					
				}
			}
    		List<Integer> list=MaxDouble(prime_specificity, dis_arr.length,0);//主属性值个数最多的疾病
    		int[] dis_has_most_primeAttr=parseListToArrInt(list);
    		
    		
    		if (dis_has_most_primeAttr.length==1) {//(1)若有主属性值，且只有一个疾病的主属性值的个数最多，那么该疾病就是预测疾病
    			a1++;
    			int index=dis_has_most_primeAttr[0];
				System.out.println("第"+i+"行中只有一个疾病的主属性值个数最大,有"+prime_specificity[0][index]+"个主属性值，specificity总和是："+prime_specificity[1][index]+" 疾病预测为 "+dis_arr[index]+" 实际上是 "+dis_arr[Integer.parseInt(allArr[i][columnsize-1])]);
			
				if (dis_arr[index].equals(dis_arr[Integer.parseInt(allArr[i][columnsize-1])])) {
					a1cor++;
					correct++;
					System.out.println(i+" 行 (1)情况 预测正确");
				}else {
					System.err.println(i+" 行 (1)情况 预测错误");
				}
				
			}else {//最大主属性值个数的疾病不唯一,那么首先判断主属性值个数是否是0，若是0，则无主属性值；如果不是0的话，那么比较这几个疾病的最大特异度
				System.out.println();
				int maxspec_dis_Id=dis_has_most_primeAttr[0];
				double maxSpec=prime_specificity[2][maxspec_dis_Id];
				double pri_attr_num=prime_specificity[0][maxspec_dis_Id];
				for (int j = 0; j < dis_has_most_primeAttr.length; j++) {
				//	System.out.println("疾病"+dis_arr[dis_has_most_primeAttr[j]]+" 的主属性值的个数"+pri_attr_num);
				//	System.out.println("最大特异度： "+prime_specificity[2][dis_has_most_primeAttr[j]]);
				}
				
				if (pri_attr_num==0) {//（4）若无主属性值的话
					
					a4++;
					//计算该行中  与每列的主属性值的差距 平均值
					double[] average=new double[dis_arr.length];
				    
					for (int j = 0; j < dis_arr.length; j++) {
						Disease disease=diseases[j];
						int[][] arr=disease.getPrime_Attr();
						int Sum=0;
						for (int k = 0; k < arr.length; k++) {
							int priColId=arr[k][0];
							int priValId=arr[k][1];
							int temp=Sum;
							Sum=temp+chafang(Integer.parseInt(allArr[i][priColId]), priValId);
						}
						average[j]=Math.sqrt((double)Sum);
					}
					for (int j = 0; j < average.length; j++) {
						//System.out.println("疾病"+dis_arr[j]+"差方是： "+average[j]);
					}
					
					
					int minAve_dis_Index=MinInfo(average);
					//Integer.parseInt(   allArr[i][columnsize-1]   )] 
					if (dis_arr[minAve_dis_Index].equals(  dis_arr[Integer.parseInt(allArr[i][columnsize-1])])){
						System.out.println("第"+i+"行没有主属性值，预测正确 "+dis_arr[minAve_dis_Index]);
						for (int j = 0; j < average.length; j++) {
							//System.out.println(average[j]);
						}
						correct++;
						a4cor++;
					}else {
						System.err.println("第"+i+"行没有主属性值，预测错误 预测为"+dis_arr[minAve_dis_Index]+" 实际疾病是 "+dis_arr[Integer.parseInt(allArr[i][columnsize-1])]);
						for (int j = 0; j < average.length; j++) {
							//System.out.println(average[j]);
						}
					}
				
				}else {//
					
					for (int j = 0; j < dis_has_most_primeAttr.length; j++) {
						int index=dis_has_most_primeAttr[j];
						if (maxSpec<prime_specificity[2][index]) {
							maxSpec=prime_specificity[2][index];
							maxspec_dis_Id=index;
						}
					}
					List<Integer> idList=new ArrayList<Integer>();//用来记录拥有最多主属性值个数的疾病的最大特异度
					for (int j = 0; j < dis_has_most_primeAttr.length; j++) {
						int index=dis_has_most_primeAttr[j];
						if (prime_specificity[2][index]==maxspec_dis_Id) {
							idList.add(index);
						}
					}
					int[] idArr=parseListToArrInt(idList);
					if (idArr.length==1) {//（2）若有主属性值，但是不仅仅有一个疾病的主属性值个数最多，则比较最大的特异度，若最大的特异度唯一，则该疾病就是预测疾病。
						a2++;
						int index=idArr[0];
						System.out.println("第"+i+"行有多个疾病的主属性个数最多，但是只有一个疾病的特异度最大  "+" 预测疾病是 "+dis_arr[index]+" 实际上是 "+dis_arr[Integer.parseInt(allArr[i][columnsize-1])]);
					    if (dis_arr[index].equals(dis_arr[Integer.parseInt(allArr[i][columnsize-1])])) {
							a2cor++;
					    	correct++;
							System.out.println(i+" (2) 情况 预测正确");
						}else {
							System.err.println(i+" (2)情况 预测错误");
						}
					}else {//（3）若最大的特异度不唯一，则比较最大特异度之和。最大特异度之和最大的疾病是预测疾病。
						
						a3++;
						//计算该行中  与每列的主属性值的差距 平均值
						double[] average=new double[dis_arr.length];
					    
						for (int j = 0; j < dis_arr.length; j++) {
							Disease disease=diseases[j];
							int[][] arr=disease.getPrime_Attr();
							int Sum=0;
							for (int k = 0; k < arr.length; k++) {
								int priColId=arr[k][0];
								int priValId=arr[k][1];
								int temp=Sum;
								Sum=temp+chafang(Integer.parseInt(allArr[i][priColId]), priValId);
							}
							average[j]=Math.sqrt((double)Sum);
						}
						
						int minAve_dis_Index=MinInfo(average);
						//Integer.parseInt(   allArr[i][columnsize-1]   )] 
						if (dis_arr[minAve_dis_Index].equals(  dis_arr[Integer.parseInt(allArr[i][columnsize-1])])){
							System.out.println("第"+i+"行最大特异度不唯一，预测正确 "+dis_arr[minAve_dis_Index]);
							for (int j = 0; j < average.length; j++) {
								//System.out.println(average[j]);
							}
							correct++;
							a3cor++;
						}else {
							System.err.println("第"+i+"行最大特异度不唯一，预测错误 预测为"+dis_arr[minAve_dis_Index]+" 实际疾病是 "+dis_arr[Integer.parseInt(allArr[i][columnsize-1])]);
							for (int j = 0; j < average.length; j++) {
								//System.out.println(average[j]);
							}
						}
						
						
						
						
					}
					
				}
			}
		}
    	double a=correct/(double)count;
		//System.out.println(dis_arr[MaxClassDistribution]);
    	//System.out.println("correct: "+correct);
		//System.out.println("正确率是 "+a);
    	System.out.println("总数 "+count);
    	System.out.println("整体正确率是 "+sishewuru(a));
    	System.out.println("1的个数 "+a1);
       if (a1!=0) {
    	  System.out.println("1的正确率是 "+sishewuru(a1cor/a1));
	   }
       System.out.println("2的个数 "+a2);
	   if (a2!=0) {
		   System.out.println("2的正确率是 "+sishewuru(a2cor/a2));
	   }
	   System.out.println("3的个数是"+a3);
	 
	   if (a3!=0) {
		   System.out.println("3的正确率是 "+sishewuru(a3cor/a3));
	   }
	   //System.out.println("a33 "+a33);
	   System.out.println("4的个数是"+a4);
	   if (a4!=0) {
		   System.out.println("4的正确率是"+sishewuru(a4cor/a4));
	}
	  // System.out.println("a33: "+a33);
	   
    }
    public NumColumn[] getNumColumns() {
		return numColumns;
	}
	public void setNumColumns(NumColumn[] numColumns) {
		this.numColumns = numColumns;
	}
	public StrColumn[] getStrColumns() {
		return strColumns;
	}
	public void setStrColumns(StrColumn[] strColumns) {
		this.strColumns = strColumns;
	}
	public String[] getDis_arr() {
		return dis_arr;
	}
	public void setDis_arr(String[] dis_arr) {
		this.dis_arr = dis_arr;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public int[] getNumIndexArr() {
		return numIndexArr;
	}
	public void setNumIndexArr(int[] numIndexArr) {
		this.numIndexArr = numIndexArr;
	}
	public int[] getStringIndexArr() {
		return stringIndexArr;
	}
	public void setStringIndexArr(int[] stringIndexArr) {
		this.stringIndexArr = stringIndexArr;
	}
	public String[][] getAllArr() {
		return allArr;
	}
	public void setAllArr(String[][] allArr) {
		this.allArr = allArr;
	}
	public int getNumcolumnsize() {
		return numcolumnsize;
	}
	public void setNumcolumnsize(int numcolumnsize) {
		this.numcolumnsize = numcolumnsize;
	}
	public int getStrcolumnsize() {
		return strcolumnsize;
	}
	public void setStrcolumnsize(int strcolumnsize) {
		this.strcolumnsize = strcolumnsize;
	}
	public int getColumnsize() {
		return columnsize;
	}
	public void setColumnsize(int columnsize) {
		this.columnsize = columnsize;
	}

	public int[] getDis_count() {
		return dis_count;
	}

	public void setDis_count(int[] dis_count) {
		this.dis_count = dis_count;
	}

	public int[] getEach_Attr_Prime() {
		return each_Attr_Prime;
	}

	public void setEach_Attr_Prime(int[] each_Attr_Prime) {
		this.each_Attr_Prime = each_Attr_Prime;
	}


	public int getMaxClassDistribution() {
		return MaxClassDistribution;
	}

	public void setMaxClassDistribution(int maxClassDistribution) {
		MaxClassDistribution = maxClassDistribution;
	}

	public Disease[] getDiseases() {
		return diseases;
	}

	public void setDiseases(Disease[] diseases) {
		this.diseases = diseases;
	}
	
		
}

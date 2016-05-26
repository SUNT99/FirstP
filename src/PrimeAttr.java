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
 * �ð汾�о���������ֵȺ���ھ�
 */
/**�ð汾����ɢ������ʱ���÷ǵݹ鷽�����˷�����ͨ��Ѱ��ȫ�������Ϣ����ĵ�
 * ���� ��Ѱ����Ѳ���ʱû�в�ȡ��Դ�ʩ��ֻ��ÿ������һ���������ݽ�����ҳ���Ѳ���**/
/**
 * ���⣬�������ڶ�����Ԥ����֮���ǰѴ���Ľ���Զ�ά����ķ�ʽ�������ڴ���
 * @author tyz
 *��ӡ��Ϣ��   ÿ�����ԵĶϵ㣬ÿ������ĸ���������ֵ���Լ�ÿ�������м���������ֵ��
 */
/**
 * 
��1������������ֵ����������ֵ�������ļ���Ψһ����ô�ü�������Ԥ�⼲��
��2������������ֵ�����ǲ�������һ��������������ֵ������࣬��Ƚ���������ȣ������������Ψһ����ü�������Ԥ�⼲����
��3������������ֵ�����ǲ�������һ��������������ֵ������࣬��Ƚ���������ȣ�����������Ȳ�Ψһ����ô���ҳ���ô���ҳ�����һ�����ݾ�����С��С�ļ������ü�����Ԥ�⼲����
��4������������ֵ�Ļ�,��ô���ҳ�����һ�����ݾ�����С�ļ������ü�����Ԥ�⼲����


 * @author tyz
 *
 */
//�������к������ж��ֱ���ж���
//���������ݽ���Ԥ����   ���������Խ�����ɢ�������ַ��������Լ�֤����б��
public class PrimeAttr {
	private String[][] allArr;//�����е����ݶ��������������������
	private String[] dis_arr;//������¼����������
	private int count=0;//������¼һ���ж�������¼
	private Disease[] diseases;
	private NumColumn[] numColumns; //������¼����������
	private StrColumn[] strColumns;//������¼�ַ���������
	private int numcolumnsize;//������¼�����еĸ���
	private int strcolumnsize;//������¼�ַ����еĸ���
	private int[] numIndexArr;	//������¼�����е��±�
	private int[] stringIndexArr;//������¼�ַ����е��±�
	private int[] dis_count;//������¼ÿ�ּ������ֵĸ���
	private int columnsize=0;//������¼һ���ж�����   ����������
	private int[] each_Attr_Prime;//������¼ÿ�������м���������ֵ
	private int MaxClassDistribution;//������¼�����ֲ��ļ������±�
	
	/**
	 * @param args  �洢�������ݼ�¼
	 * @return 
	 */
	//������������
	public double sishewuru(double num) {//��double��ֵ�������벢�ұ���2λС��
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
	public void bianhao(String ID,int disIndex,String filePath){//��һ��������ʾ���ޱ���У��ڶ���������ʾ���������ڵڼ���
    	/**
    	 * �������ö�ȡ�ļ�������
    	 */
		List<String> disease=new ArrayList<String>();
		List<String> syndrome=new ArrayList<String>();
    	String encoding="GBK";  //�����ʽ   	
    	//discretization2.txt  iris.data  balance-scale glass.data letter-recognition.data breast-cancer-wisconsin.data wbpc.data wpbc.data wine.data segmentation.data
    	File file=new File(filePath);//���ļ�	
        if(file.isFile() && file.exists()){ //�ж��ļ��Ƿ���� 
        	
        	try {
				InputStreamReader isr=new InputStreamReader(new FileInputStream(file),encoding);//���ǵ������ʽ
				BufferedReader br = new BufferedReader(isr);
				//�������ж���Щ���������֣���Щ�������ַ���
				String lineTxt = br.readLine();
				StringTokenizer st1=new StringTokenizer(lineTxt,",");
				List<Integer> numIndexList=new ArrayList<Integer>();//������¼�����е��±�
				List<Integer> stringIndexList=new ArrayList<Integer>();//������¼�ַ����е��±�
				String[] numFirstRow=null;
			    System.out.println();
				if (ID.equals("Y")) {//����б��ID�еĻ������ڵ�0��
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
					            double b=Double.parseDouble(value);//�������ֵ���Щ���Ե��е��±��¼�������ӵ�numIndexList����
					            numIndexList.add(i);        
					        } catch (Exception e) {
					        	stringIndexList.add(i);//�����ַ�������Щ���Ե��е��±��¼�������ӵ�stringIndexList����		          
					        }	
						} else if (i>disIndex) {
							try {
					            double b=Double.parseDouble(value);//�������ֵ���Щ���Ե��е��±��¼�������ӵ�numIndexList����
					            numIndexList.add(i-1);     
					        } catch (Exception e) {
					        	stringIndexList.add(i-1);//�����ַ�������Щ���Ե��е��±��¼�������ӵ�stringIndexList����	          
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
				List<String> allList=new ArrayList<String>();//������¼���е�����
				while(lineTxt!= null){//��ȡÿһ������	�ӵ�һ�п�ʼ
					count++;
					StringTokenizer st = new StringTokenizer(lineTxt,",");	
					String[] arr =new String[columnsize];
					int j=0;
					if (ID.equals("Y")) {//����б���еĻ�
						while(st.hasMoreTokens()){
							if (j==0) {
								String aString=st.nextToken();
							}else {
								arr[j-1]=st.nextToken();
								allList.add(arr[j-1]);
								
							}
							j++; 
						}
						if (disease.contains(arr[disIndex])) {//��ÿһ���еļ������뵽disease������		
						}else {
							disease.add(arr[disIndex]);
						}	
						lineTxt=br.readLine();
					}else if (ID.equals("N")) {//�ޱ���еĻ�
						while(st.hasMoreTokens()){
							arr[j]=st.nextToken();
							allList.add(arr[j]);
							j++;  
						}
						if (disease.contains(arr[disIndex])) {//��ÿһ���еļ������뵽disease������		
						}else {
							disease.add(arr[disIndex]);
						}	
						lineTxt=br.readLine();
					}
				}
			//	System.out.println("��¼������"+count);
			//	System.out.println("����������"+disease.size());
				 //�Լ������б��
				dis_arr=parseListToArrString(disease);
				allArr=new String[count][columnsize];//�����洢���е�����
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
					System.out.println("�� "+i+" ������"+dis_arr[i]+"���ֵĴ����� "+dis_count[i]);
				}
				MaxClassDistribution=MaxInt(dis_count);
				if (strcolumnsize>0) { //������ַ����������еĻ���
					for (int i = 0; i < strColumns.length; i++) {//��ÿһ���ַ���������s
						HashMap<String, String> strValuehm=new HashMap<String, String>();//������¼ÿһ���ַ��������е�ȡֵ
						int columnId=strColumns[i].getColumnId();				
						for (int k = 0; k < count; k++) {//����ĳһ���ַ����е������У��ѳ��ֵ����ּ�¼��strValuehm��ϣ����
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
						StrColumnValue[] s=strColumns[i].getStrColVal();//��¼������iһ���ж��ٸ�ֵ
						while (iterator2.hasNext()) {//�Ѹ��ַ������е����ּ�¼��ÿһ���ַ������ֶ�����
							String name=iterator2.next();
							s=strColumns[i].getStrColVal();
							s[q]=new StrColumnValue(q, name);
							s[q].setValue_dis(new int[dis_arr.length]);
							s[q].setPrime_dis(new double[2][dis_arr.length]);
							q++;
						}
						for (int k = 0; k < count; k++) {  //���������ַ����е�ÿһ��
							for (int k2 = 0; k2 < s.length; k2++) {//��StrColumnValue[] s��ȥѰ�Ҹ��г��ֵ����Ե�ֵ������ҵ��ˣ��ͽ�������ֵ�Ĵ�����һ��ͬʱ��allArr�и�λ�õ�����ֵ��ɸ�����ֵ�ı��
								String name1=s[k2].getName();
								String name2=allArr[k][columnId];
								if (name1.equals(name2)) {//
									int a=s[k2].getCount();
									s[k2].setCount(a+1);
									allArr[k][columnId]=s[k2].getId()+"";
									//ͳ�Ƹ�����ֵ��ÿһ�������г��ֵ��ܴ���
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
				System.out.println("��ȡ�ļ����ݳ���");
				e.printStackTrace();
			}	
        }else {
			System.out.println("�Ҳ���ָ���ļ�");
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
	public double AllValueofRange(double left,double right,double[][] AttrArrInFo){//������������������������Χ�ڵ�ȡֵ�ж��ٸ�
		double count=0;//(114,147)�м�ĸ���
		//����(114,147)�м�ĸ���
		for (int i = 0; i < AttrArrInFo.length; i++) {
			if ((AttrArrInFo[i][0]>left||AttrArrInFo[i][0]==left)&&(AttrArrInFo[i][0]<right||AttrArrInFo[i][0]==right)) {
				count=count+AttrArrInFo[i][3];
			}
		}
		return count;	
	}
	//�ҳ���114,121���еĸ��������ĸ���
	public double[] eachdisCount(double left,double right,double[][] AttrArrInFo,double[] diseaseArr){
		double countArr[]=new double[diseaseArr.length];//������¼ÿһ�������ڣ�114,147����Χ�ڳ��ֵĴ���
		for (int i = 0; i < diseaseArr.length; i++) {
			for (int j = 0; j <AttrArrInFo.length ; j++) {
				if ((AttrArrInFo[j][0]>left||AttrArrInFo[j][0]==left)&&(AttrArrInFo[j][0]<right||AttrArrInFo[j][0]==right)) {
					countArr[i]=countArr[i]+AttrArrInFo[j][4+2*i+1];
				}
			}
		}
		return countArr;	
	}
	//��������Ϣ����
	public List<Integer> MaxDouble(double[][] arr,int columnsize,int rowId){//����һ��������Ϊ���Ŀ����кü�������������������¼�����ĵ���±꣬
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
	//��һ���ļ����Ԥ������
	public static void exportData(String fileName, String[][] allArr) {
	HashMap< String, String> hm=new HashMap<String, String>();
	try {  
	//��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�  
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
		//��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�  
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
		int areaCount=areascount;//Ҫ�ֳɵ��������
		int breakCount=areaCount-1;//Ҫ�ҵĶϵ����
		double[] eachAreaInfoArr=null;//������¼ÿһ���������Ϣ
		for (int i = 0; i <breakCount; i++) {//���Ҷϵ��ÿһ����  �����3�֣����ʱ�Ѿ��ҵ���2���ϵ㣬���ҵ������ϵ��ʱ��
			
			if (i==0) {
				double[] eachDisCount=eachdisCount(left, right, AttrArrInFo, diseaseArr);
				double info=Info(count, eachDisCount);
				eachAreaInfoArr=new double[]{info};
			}
		    int areas_count=limitPoint.length-1;//��¼��ǰ�Ѿ��м�������
		    double[] maxbreakpoint=new double[areas_count];//������¼ÿ�������ڵ������Ϣ����ϵ�
		    double[][] each_area_left_right_global=new double[areas_count][2];//������¼���е��������ҵ���������Ϣ����ϵ������ȫ����Ϣ��
			double[] each_area_maxbreak_globalInfo=new double[areas_count];//������¼ÿ���ε�ȫ����Ϣ��
			
			                    //areas_count   ������ǰ�Ѿ��еļ��������ҳ�ÿ������������Ϣ����㲢�Ƚ�ȫ����Ϣ��
			
			int sum=0;//������¼�м��������п��ܵĶϵ�
			for (int j = 0; j < limitPoint.length-1; j++) {//��ÿһ�����ҵ���Ϣ�������ĵ㲢�Ҽ�¼���ǵ���Ϣ�أ��Լ�ȫ�ֵ���Ϣ��
				double leftLimit=limitPoint[j];
				double rightLimit=limitPoint[j+1]; //
				double basecount=AllValueofRange(leftLimit, rightLimit, AttrArrInFo);//�����ҳ���һ������һ���ж��ٸ�ֵ
				double countArr[]=eachdisCount(leftLimit, rightLimit, AttrArrInFo, diseaseArr);//������¼ÿһ����������һ������Χ�ڳ��ֵĴ���
				double beginInfo=Info(basecount,countArr);//��¼�����ݼ����ʼ����Ϣ
				List<Double> brlist=new ArrayList<Double>(); //��һ������������Щ���ܵĶϵ�
				for (int k = 0; k < breakpointArr.length; k++) {
					if (breakpointArr[k]>leftLimit&&breakpointArr[k]<rightLimit) {
						brlist.add(breakpointArr[k]);
					}
				}
				if (brlist.size()==0) {
					each_area_maxbreak_globalInfo[j]=100;
					
					
				}else {
					sum++;
					double[] brArr=parseListToArrDouble(brlist);//������¼��һ����������Щ���ܵĶϵ�
					double[] breakInfoGainArr=new double[brArr.length];  //ÿ�����ܶϵ����Ϣ����
					double[] breakInfo=new double[brArr.length];//ÿ�����ܶϵ����Ϣ��
					double[][] left_right_globalInfo=new double[brArr.length][2];//������¼��һ������������ȫ�ֱ���
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
				    	breakInfoGainArr[k]=InfoGain;   //Ϊÿ�����ܶϵ����Ϣ���渳ֵ
				    	breakInfo[k]=InfoAfterBreak;//Ϊÿ�����ܶϵ����Ϣ����ֵ
					}
					int MaxIndex=MaxInfoGain(breakInfoGainArr);	//�ҳ���Ϣ�������ĵ�
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
			//�Ƚ��⼸���еļ���������Ϣ���棬���ĸ����е������Ϣ�������ʹȫ����Ϣ�����	
			int MinglobalIndex=MinInfo(each_area_maxbreak_globalInfo);//������¼������  ȫ����Ϣ�����ĵ�
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
		//�����ʽ   	
	    if (numcolumnsize!=0) {
	    	numColumns=new NumColumn[numcolumnsize];
		    //�����е��������Ժͼ������Զ�����
		    int row=count;//35�� 
		    int column=numIndexArr.length+1;//2��  �����кͼ�����
		    int allcolumn=columnsize;
		    String[][] numValueArr=new String[row][column];//double[35][5]//������¼��������ֵ�ͼ���ֵ
		    for (int i = 0; i < row; i++) {   //��ÿһ�������ּ���numValueArr�У�ͬʱ�������ı��Ҳ��������
				for (int j = 0; j < numIndexArr.length; j++) {
					int index=numIndexArr[j];
					numValueArr[i][j]=allArr[i][index];
				}
				numValueArr[i][column-1]=allArr[i][allcolumn-1];
			}
		 
		    for (int i = 0; i < column-1; i++) {//��ÿһ�����Ե�һ��ֵ��  ������ÿһ������
		    	
		    	double[][] value_dis = new double[count][2];//������¼ÿһ������ֵ������ֵ��Ӧ�ļ���
		    	for (int j = 0; j < count; j++) {
					value_dis[j][0]=Double.parseDouble(numValueArr[j][i]);  //��¼���еĵڼ�������ֵ
					value_dis[j][1]=Double.parseDouble(numValueArr[j][column-1]);//��¼�Ǹ�����
				}	
				sorting(value_dis, count);
				HashMap<Double, Double> AttrNumhm=new HashMap<Double, Double>();//������¼ÿһ�����Ե�����ֵ
				for (int j = 0; j < count; j++) {
					AttrNumhm.put(value_dis[j][0], value_dis[j][0]);
				}
				int AttrCount=AttrNumhm.size();//������һ���ж��ٸ�ֵ
				int columnlength=4+dis_arr.length*2;
				double[][] AttrArrInFo=new double[AttrCount][columnlength];//������¼����A��ÿһ��ȡֵ�㴦����Ϣ
				Iterator<Double> iterator=AttrNumhm.keySet().iterator();
				int u=0;
				while (iterator.hasNext()) {//������A�ĸ���ȡֵ�AttrArrInFo�еĵ�һ����
					AttrArrInFo[u][0]=iterator.next();
					u++;
				}
				sorting2(AttrArrInFo, AttrCount);//������ֵ����
				for (int j = 0; j < AttrCount; j++) {//����AttrArrInFo�е�ÿһ��
					HashMap<Double, Double> eachValue_disease=new HashMap<Double, Double>();//������¼������Щ�����ڸ�������ȡ�˸�ֵ
					int allCount=0;//������¼�����Եĸø�ֵһ�������˶��ٴ�
					for (int j2 = 0; j2 < count; j2++) {//����column�е�ÿһ��������ֵ
						if (AttrArrInFo[j][0]==value_dis[j2][0]) {
							eachValue_disease.put(value_dis[j2][1],value_dis[j2][1]);
							allCount++;
						}
					}
					AttrArrInFo[j][3]=allCount;
					AttrArrInFo[j][1]=eachValue_disease.size();////��AttrArrInFo[j][]�ĺ͵ڶ��н������á���¼��ÿ��ȡֵ�м�������
					if (AttrArrInFo[j][1]==1) {//����������е�ֵ114���ֻ������һ�ּ�������Ѹü����ı�ż�¼����3����
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
						for (int v = 0; v < count; v++) {//ͳ��ÿһ������ȡ��ֵ�Ĵ���
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
				for (int j = 0; j < AttrCount-1; j++) {//�ҳ����жϵ�
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
				double left=AttrArrInFo[0][0];//������¼�����Ե���Сȡֵ
				double right=AttrArrInFo[AttrArrInFo.length-1][0];//������¼�����Ե����ȡֵ
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
				System.out.println("��"+i+"����ֵ���Ե������Ϣ����ϵ�ĸ����� "+Maxbreakcount);
				double[] maxbreakArr=null;
				if (Maxbreakcount!=0) {
					
					maxbreakArr=parseListToArrDouble(breakPointResult);
					System.out.print("�ϵ��ǣ�  ");
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
				numColumns[i]=new NumColumn(numcolumnid);//�����������ж���������Ϊi
				numColumns[i].setNumColVal(new NumColumnValue[Maxbreakcount+1]);//�����������ж����е�����ֵ������
				NumColumnValue[] numColumnValues=numColumns[i].getNumColVal();
				int rangenum=2+Maxbreakcount;
				double[] range=new double[rangenum];//������¼���������ʼ�㣬���������Ϣ����ϵ㣬������
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
				for (int j = 0; j <range.length-1 ; j++) {//�����������ж����е�ÿһ������ֵ���󣬽�����߽���ұ߽�ֱ��������
					numColumnValues[j]=new NumColumnValue(j);
					numColumnValues[j].setLeft(range[j]);
					numColumnValues[j].setRight(range[j+1]);
					numColumnValues[j].setValue_dis(new int[dis_arr.length]);
					
					numColumnValues[j].setPrime_dis(new double[2][dis_arr.length]);
				}
				for (int j = 0; j <count; j++) {//������i���������е�ÿһ�У����������ж������ڵķ�Χ�����ñ�Ŵ���
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
	
    public void prime(double min_coverage,double min_specificity) {//��һ��������ʾ����е��±꣬��Ϊ-1���ʾ�ޱ����   �ڶ���������ʾ�����е��±�
		//System.out.println("------------------------------");
		each_Attr_Prime=new int[dis_arr.length];
		
		for (int i = 0; i < dis_arr.length; i++) {
			double support_D1=dis_count[i];
			double support_NoD1=count-support_D1;
			List<Integer> prime_colu_id=new ArrayList<Integer>();//������¼�ü�����������������ֵ  �����ظ�
			List<Integer> prime_value_bianhao=new ArrayList<Integer>();//������¼�ü������������ʵı��
			
			if (strcolumnsize!=0) {//���strcolumnsize��Ϊ0�Ļ�
				 
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
							System.out.println("��"+i+"������"+dis_arr[i]+"��������ֵ�ǣ� ��"+columnid+"���ַ�������ֵ "+strColumnValues[k].getName()+"�������еĸ����ʣ�  "+coverage+" �����ԣ�"+specificity);
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
							System.out.println("��"+i+"������"+dis_arr[i]+"��������ֵ�ǣ� ��"+columnid+" �еĵ�"+k+"����������ֵ "+" ��߽��� "+numColumnValues[k].getLeft()+" �ұ߽��ǣ�"+numColumnValues[k].getRight()+"�������ĸ����ʣ�  "+coverage+"�����ԣ�"+specificity);
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
			//System.out.println("����"+dis_arr[j]+"��������ֵ����"+arr.length+" :");
			for (int k = 0; k < arr.length; k++) {
				//System.out.println("�� "+arr[k][0]+" �У���"+arr[k][1]+" ��ֵ");
			}
			//System.out.println();
			
		}
		//System.out.println(count);
		double average=(double)count/(double)dis_arr.length;
		System.out.println("������ֵƽ������ "+average);
		/**double[][] prime_Dis=numColumns[0].getNumColVal()[0].getPrime_dis();
		
		for (int j = 0; j < dis_arr.length; j++) {
			System.out.println("��"+j+"������"+prime_Dis[0][j]);
			System.out.println(prime_Dis[1][j]);
		}**/
	}
    public void pre_treatment(String ID,int disIndex,int areascount,String filePath){
    	bianhao(ID, disIndex,filePath);
    	discret(areascount);	
    	System.out.println("����Ϊ"+allArr.length+",����Ϊ�� "+allArr[0].length);
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
    
    
    public void classifier(){  //������  ��������������ֵ�㷨�ĺû�
    	
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
        		
    	for (int i = 0; i <allArr.length ; i++) {//����ÿһ��
    		double[][] prime_specificity=new double[3][dis_arr.length];//��һ��������¼��֤���м���������ֵ�������ڸ��У��ڶ���������¼�ü�����ÿ��������ֵ��������ܺͣ�������������¼��������ֵ��������ȡ�
    		if (strcolumnsize!=0) {
			for (int j = 0; j < strcolumnsize; j++) {//����ÿһ���ַ�����
				int strIndex=stringIndexArr[j];//��ȡ�ַ����е��±�
				String s=allArr[i][strIndex];//��ȡ���ַ���
				//System.out.println(s);
				int strValbianhao=Integer.parseInt(s);
				StrColumnValue[] strColumnValues=strColumns[j].getStrColVal();
				//System.out.println(strColumns[j].getStrColVal());
				double[][] prime_dis=strColumnValues[strValbianhao].getPrime_dis();
				for (int l = 0; l < dis_arr.length; l++) {
					if (prime_dis[0][l]==1) {//������ַ����ǵ�l��������������ֵ
						prime_specificity[0][l]++;//��l��������������ֵ������1
						double a=prime_specificity[1][l];
						prime_specificity[1][l]=a+prime_dis[1][l];//ͳ�Ƶ�l��������������ֵ������ȵĺ�
						if (prime_specificity[2][l]<prime_dis[1][l]) {
							prime_specificity[2][l]=prime_dis[1][l];
						}
					}
				}	
			}
		}
    		if (numcolumnsize!=0) {
				for (int j = 0; j < numcolumnsize; j++) {//����ÿһ��������
					NumColumn numColumn=numColumns[j];    //numColumns[0]
					int numIndex=numIndexArr[j];//��ȡ�����е��±�    numIndex=0
					String string=allArr[i][numIndex];
					int numValuebianhao=Integer.parseInt(string);//��ȡ������ֵ�ڸ���������ɢ����ı��0
					NumColumnValue[] numColumnValues=numColumn.getNumColVal();
					double[][] prime_dis=numColumnValues[numValuebianhao].getPrime_dis();
					for (int l = 0; l < dis_arr.length; l++) {
						if (prime_dis[0][l]==1) {//�������ֵ�ǵ�l��������������ֵ
							prime_specificity[0][l]++;//��l��������������ֵ������1	
							double a=prime_specificity[1][l];
							prime_specificity[1][l]=a+prime_dis[1][l];//ͳ�Ƶ�l��������������ֵ������ȵĺ�
							
							if (prime_specificity[2][l]<prime_dis[1][l]) {
								prime_specificity[2][l]=prime_dis[1][l];
							}
							
						}
					}
					
				}
			}
    		List<Integer> list=MaxDouble(prime_specificity, dis_arr.length,0);//������ֵ�������ļ���
    		int[] dis_has_most_primeAttr=parseListToArrInt(list);
    		
    		
    		if (dis_has_most_primeAttr.length==1) {//(1)����������ֵ����ֻ��һ��������������ֵ�ĸ�����࣬��ô�ü�������Ԥ�⼲��
    			a1++;
    			int index=dis_has_most_primeAttr[0];
				System.out.println("��"+i+"����ֻ��һ��������������ֵ�������,��"+prime_specificity[0][index]+"��������ֵ��specificity�ܺ��ǣ�"+prime_specificity[1][index]+" ����Ԥ��Ϊ "+dis_arr[index]+" ʵ������ "+dis_arr[Integer.parseInt(allArr[i][columnsize-1])]);
			
				if (dis_arr[index].equals(dis_arr[Integer.parseInt(allArr[i][columnsize-1])])) {
					a1cor++;
					correct++;
					System.out.println(i+" �� (1)��� Ԥ����ȷ");
				}else {
					System.err.println(i+" �� (1)��� Ԥ�����");
				}
				
			}else {//���������ֵ�����ļ�����Ψһ,��ô�����ж�������ֵ�����Ƿ���0������0������������ֵ���������0�Ļ�����ô�Ƚ��⼸����������������
				System.out.println();
				int maxspec_dis_Id=dis_has_most_primeAttr[0];
				double maxSpec=prime_specificity[2][maxspec_dis_Id];
				double pri_attr_num=prime_specificity[0][maxspec_dis_Id];
				for (int j = 0; j < dis_has_most_primeAttr.length; j++) {
				//	System.out.println("����"+dis_arr[dis_has_most_primeAttr[j]]+" ��������ֵ�ĸ���"+pri_attr_num);
				//	System.out.println("�������ȣ� "+prime_specificity[2][dis_has_most_primeAttr[j]]);
				}
				
				if (pri_attr_num==0) {//��4������������ֵ�Ļ�
					
					a4++;
					//���������  ��ÿ�е�������ֵ�Ĳ�� ƽ��ֵ
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
						//System.out.println("����"+dis_arr[j]+"��ǣ� "+average[j]);
					}
					
					
					int minAve_dis_Index=MinInfo(average);
					//Integer.parseInt(   allArr[i][columnsize-1]   )] 
					if (dis_arr[minAve_dis_Index].equals(  dis_arr[Integer.parseInt(allArr[i][columnsize-1])])){
						System.out.println("��"+i+"��û��������ֵ��Ԥ����ȷ "+dis_arr[minAve_dis_Index]);
						for (int j = 0; j < average.length; j++) {
							//System.out.println(average[j]);
						}
						correct++;
						a4cor++;
					}else {
						System.err.println("��"+i+"��û��������ֵ��Ԥ����� Ԥ��Ϊ"+dis_arr[minAve_dis_Index]+" ʵ�ʼ����� "+dis_arr[Integer.parseInt(allArr[i][columnsize-1])]);
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
					List<Integer> idList=new ArrayList<Integer>();//������¼ӵ�����������ֵ�����ļ�������������
					for (int j = 0; j < dis_has_most_primeAttr.length; j++) {
						int index=dis_has_most_primeAttr[j];
						if (prime_specificity[2][index]==maxspec_dis_Id) {
							idList.add(index);
						}
					}
					int[] idArr=parseListToArrInt(idList);
					if (idArr.length==1) {//��2������������ֵ�����ǲ�������һ��������������ֵ������࣬��Ƚ���������ȣ������������Ψһ����ü�������Ԥ�⼲����
						a2++;
						int index=idArr[0];
						System.out.println("��"+i+"���ж�������������Ը�����࣬����ֻ��һ����������������  "+" Ԥ�⼲���� "+dis_arr[index]+" ʵ������ "+dis_arr[Integer.parseInt(allArr[i][columnsize-1])]);
					    if (dis_arr[index].equals(dis_arr[Integer.parseInt(allArr[i][columnsize-1])])) {
							a2cor++;
					    	correct++;
							System.out.println(i+" (2) ��� Ԥ����ȷ");
						}else {
							System.err.println(i+" (2)��� Ԥ�����");
						}
					}else {//��3������������Ȳ�Ψһ����Ƚ���������֮�͡���������֮�����ļ�����Ԥ�⼲����
						
						a3++;
						//���������  ��ÿ�е�������ֵ�Ĳ�� ƽ��ֵ
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
							System.out.println("��"+i+"���������Ȳ�Ψһ��Ԥ����ȷ "+dis_arr[minAve_dis_Index]);
							for (int j = 0; j < average.length; j++) {
								//System.out.println(average[j]);
							}
							correct++;
							a3cor++;
						}else {
							System.err.println("��"+i+"���������Ȳ�Ψһ��Ԥ����� Ԥ��Ϊ"+dis_arr[minAve_dis_Index]+" ʵ�ʼ����� "+dis_arr[Integer.parseInt(allArr[i][columnsize-1])]);
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
		//System.out.println("��ȷ���� "+a);
    	System.out.println("���� "+count);
    	System.out.println("������ȷ���� "+sishewuru(a));
    	System.out.println("1�ĸ��� "+a1);
       if (a1!=0) {
    	  System.out.println("1����ȷ���� "+sishewuru(a1cor/a1));
	   }
       System.out.println("2�ĸ��� "+a2);
	   if (a2!=0) {
		   System.out.println("2����ȷ���� "+sishewuru(a2cor/a2));
	   }
	   System.out.println("3�ĸ�����"+a3);
	 
	   if (a3!=0) {
		   System.out.println("3����ȷ���� "+sishewuru(a3cor/a3));
	   }
	   //System.out.println("a33 "+a33);
	   System.out.println("4�ĸ�����"+a4);
	   if (a4!=0) {
		   System.out.println("4����ȷ����"+sishewuru(a4cor/a4));
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

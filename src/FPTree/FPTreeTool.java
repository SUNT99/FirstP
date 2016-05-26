package FPTree;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 主属性值群
 * 
 （1）若有主属性值，若主属性值群个数最多的疾病唯一，那么该疾病就是预测疾病
 （2）若有主属性值，但是不仅仅有一个疾病的主属性值个数最多，则比较最大的特异度，若最大的特异度唯一，则该疾病就是预测疾病。
（3）若有主属性值，但是不仅仅有一个疾病的主属性值个数最多，则比较最大的特异度，若最大的特异度不唯一，那么就找出那么就找出与这一行数据距离最小最小的疾病，该疾病即预测疾病。
（4）若无主属性值的话,那么就找出与这一行数据距离最小的疾病，该疾病即预测疾病。
 * 
 */
public class FPTreeTool {
	// 输入数据文件位置
	private String filePath;
	// 最小支持度阈值
	private int minSupportCount;
	// 所有事物ID记录
	private ArrayList<String[]> totalGoodsID;
	// 各个ID的统计数目映射表项，计数用于排序使用
	private HashMap<String, Integer> itemCountMap;
	//最小信任度阀值
	private double minCoverage;
	//最小特异度
	private double minSpecificity;
	//分类结果集
	private String[] classificationSet;
	private Disease[] diseases;//用来记录所有的证候对象
	private Column[] columns;//用来记录所有的证候列
	
	private int symptom_columnSize;
	private ArrayList<String[]> dataArray;//用来存储文件里的所有数据
	
	public FPTreeTool(String filePath, int minSupportCount,double minCoverage,double minSpecificity,String[] classificationSet) {
		this.filePath = filePath;
		this.minSupportCount = minSupportCount;
		this.minCoverage=minCoverage;
		this.minSpecificity=minSpecificity;
		this.classificationSet=classificationSet;
		this.diseases=new Disease[classificationSet.length];
		for (int i = 0; i < classificationSet.length; i++) {
			 int diseId=tiqushuzi(classificationSet[i]);
			 diseases[diseId]=new Disease(diseId, classificationSet[i]);
		}
//		System.out.println(classificationSet[0]+","+classificationSet[1]);
		readDataFile();
	}

	/**
	 * 从文件中读取数据
	 */
	private void readDataFile() {
		File file = new File(filePath);
		dataArray = new ArrayList<String[]>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str;
			String[] tempArray=null;
			while ((str = in.readLine()) != null) {
				tempArray = str.split(" ");
				dataArray.add(tempArray);
			}
			symptom_columnSize=tempArray.length-2;
			//System.out.println("症状列是 "+symptom_columnSize);
			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}
		//int symptom_columnSize=dataArray.size();
		columns=new Column[symptom_columnSize];
		for (int i = 0; i < columns.length; i++) {
			columns[i]=new Column(i);
		}
		String[] temp;
		int count = 0;
		itemCountMap = new HashMap<String,Integer>();// 各个ID的统计数目映射表项，计数用于排序使用
		totalGoodsID = new ArrayList<String[]>();// 所有事物ID记录
		for (String[] a : dataArray) {//遍历每一行
			//我需要统计一共有多少症状列，对于症状列，我会把他每一列都统计了，然后统计有多少值 每个值编号，名字
			temp = new String[a.length - 1];
			System.arraycopy(a, 1, temp, 0, a.length - 1);
			totalGoodsID.add(temp);//加一条事务ID记录
			for (String s : temp) {
				if (!itemCountMap.containsKey(s)) {
					count = 1;
				} else {
					count = ((int) itemCountMap.get(s));
					// 支持度计数加1
					count++;
				}
				// 更新表项
				itemCountMap.put(s, count);
			}
			for (int i = 0; i < temp.length-1; i++) {//需要将每一列的值加到hm中
				Column column=columns[i];
				HashMap<String, String>hm=column.gethMap();
				hm.put(temp[i], temp[i]);
			}
		}
		for (int i = 0; i < columns.length; i++) {
			//System.out.println("第"+i+"个属性列");
			Column column=columns[i];
			HashMap< String,String > hMap=column.gethMap();
			int valueCount=hMap.keySet().size();
			column.setColumnValues(new ColumnValue[valueCount]);
			//System.out.println("valueCount"+valueCount);
			ColumnValue[] columnValues=column.getColumnValues();//记录所有的值
			Iterator< String> iterator =hMap.keySet().iterator();
			
			while (iterator.hasNext()) {
				String symptomName=iterator.next();
				int valueId=tiqushuzi(symptomName);
				//System.out.println("valueId "+valueId);
				ColumnValue columnValue=new ColumnValue(valueId,symptomName);
				columnValues[valueId]=columnValue;
				
			}
			
			
		}
		for (int i = 0; i < columns.length; i++) {
			Column column=columns[i];
			ColumnValue[] columnValues=column.getColumnValues();
			//System.out.println("第"+i+"属性列有"+columnValues.length+"个值");
			for (int j = 0; j < columnValues.length; j++) {
				//System.out.print(columnValues[j].getValue()+" ");
			}
			//System.out.println();
		}
		
		
	}

	/**
	 * 根据事物记录构造FP树
	 */
	private void buildFPTree(ArrayList<String> suffixPattern,
			ArrayList<ArrayList<TreeNode>> transctionList) {
		// 设置一个空根节点
		TreeNode rootNode = new TreeNode(null, 0);
		int count = 0;
		// 节点是否存在
		boolean isExist = false;
		ArrayList<TreeNode> childNodes;
		ArrayList<TreeNode> pathList;
		// 相同类型节点链表，用于构造的新的FP树
		HashMap<String, ArrayList<TreeNode>> linkedNode = new HashMap<String,ArrayList<TreeNode>>();
		HashMap<String, Integer> countNode = new HashMap<String,Integer>();
		// 根据事物记录，一步步构建FP树
		for (ArrayList<TreeNode> array : transctionList) {
			TreeNode searchedNode;
			pathList = new ArrayList<TreeNode>();
			for (TreeNode node : array) {
				pathList.add(node);
				nodeCounted(node, countNode);
				searchedNode = searchNode(rootNode, pathList);
				childNodes = searchedNode.getChildTreeNodes();

				if (childNodes == null) {
					childNodes = new ArrayList<TreeNode>();
					childNodes.add(node);
					searchedNode.setChildTreeNodes(childNodes);
					node.setParentNode(searchedNode);
					nodeAddToLinkedList(node, linkedNode);
				} else {
					isExist = false;
					for (TreeNode node2 : childNodes) {
						// 如果找到名称相同，则更新支持度计数
						if (node.getName().equals(node2.getName())) {
							count = node2.getCount() + node.getCount();
							node2.setCount(count);
							// 标识已找到节点位置
							isExist = true;
							break;
						}
					}

					if (!isExist) {
						// 如果没有找到，需添加子节点
						childNodes.add(node);
						node.setParentNode(searchedNode);
						nodeAddToLinkedList(node, linkedNode);
					}
				}

			}
		}

		// 如果FP树已经是单条路径，则输出此时的频繁模式
		if (isSinglePath(rootNode)) {
			printFrequentPattern(suffixPattern, rootNode);
		//	System.out.println("-------");
		} else {
			ArrayList<ArrayList<TreeNode>> tList;
			ArrayList<String> sPattern;
			if (suffixPattern == null) {
				sPattern = new ArrayList<String>();
			} else {
				// 进行一个拷贝，避免互相引用的影响
				sPattern = (ArrayList<String>) suffixPattern.clone();
			}

			// 利用节点链表构造新的事务
			for (Map.Entry entry : countNode.entrySet()) {
				// 添加到后缀模式中
				sPattern.add((String) entry.getKey());
				//获取到了条件模式机，作为新的事务
				tList = getTransactionList((String) entry.getKey(), linkedNode);
				
				/*System.out.print("[后缀模式]：{");
				for(String s: sPattern){
					System.out.print(s + ", ");
				}
				System.out.print("}, 此时的条件模式基：");
				for(ArrayList<TreeNode> tnList: tList){
					System.out.print("{");
					for(TreeNode n: tnList){
						System.out.print(n.getName() + ", ");
					}
					System.out.print("}, ");
				}
				System.out.println();*/
				// 递归构造FP树
				buildFPTree(sPattern, tList);
				// 再次移除此项，构造不同的后缀模式，防止对后面造成干扰
				sPattern.remove((String) entry.getKey());
			}
		}
	}

	/**
	 * 将节点加入到同类型节点的链表中
	 * 
	 * @param node
	 *            待加入节点
	 * @param linkedList
	 *            链表图
	 */
	private void nodeAddToLinkedList(TreeNode node,
			HashMap<String, ArrayList<TreeNode>> linkedList) {
		String name = node.getName();
		ArrayList<TreeNode> list;

		if (linkedList.containsKey(name)) {
			list = linkedList.get(name);
			// 将node添加到此队列中
			list.add(node);
		} else {
			list = new ArrayList<TreeNode>();
			list.add(node);
			linkedList.put(name, list);
		}
	}

	/**
	 * 根据链表构造出新的事务
	 * 
	 * @param name
	 *            节点名称
	 * @param linkedList
	 *            链表
	 * @return
	 */
	private ArrayList<ArrayList<TreeNode>> getTransactionList(String name,
			HashMap<String, ArrayList<TreeNode>> linkedList) {
		ArrayList<ArrayList<TreeNode>> tList = new ArrayList<ArrayList<TreeNode>>();
		ArrayList<TreeNode> targetNode = linkedList.get(name);
		ArrayList<TreeNode> singleTansaction;
		TreeNode temp;

		for (TreeNode node : targetNode) {
			singleTansaction = new ArrayList<TreeNode>();

			temp = node;
			while (temp.getParentNode().getName() != null) {
				temp = temp.getParentNode();
				singleTansaction.add(new TreeNode(temp.getName(), 1));
			}

			// 按照支持度计数得反转一下
			Collections.reverse(singleTansaction);

			for (TreeNode node2 : singleTansaction) {
				// 支持度计数调成与模式后缀一样
				node2.setCount(node.getCount());
			}

			if (singleTansaction.size() > 0) {
				tList.add(singleTansaction);
			}
		}

		return tList;
	}

	/**
	 * 节点计数
	 * 
	 * @param node
	 *            待加入节点
	 * @param nodeCount
	 *            计数映射图
	 */
	private void nodeCounted(TreeNode node, HashMap<String, Integer> nodeCount) {
		int count = 0;
		String name = node.getName();

		if (nodeCount.containsKey(name)) {
			count = nodeCount.get(name);
			count++;
		} else {
			count = 1;
		}

		nodeCount.put(name, count);
	}


	/**
	 * 待插入节点的抵达位置节点，从根节点开始向下寻找待插入节点的位置
	 * 
	 * @param root
	 * @param list
	 * @return
	 */
	private TreeNode searchNode(TreeNode node, ArrayList<TreeNode> list) {
		ArrayList<TreeNode> pathList = new ArrayList<TreeNode>();
		TreeNode tempNode = null;
		TreeNode firstNode = list.get(0);
		boolean isExist = false;
		// 重新转一遍，避免出现同一引用
		for (TreeNode node2 : list) {
			pathList.add(node2);
		}

		// 如果没有孩子节点，则直接返回，在此节点下添加子节点
		if (node.getChildTreeNodes() == null) {
			return node;
		}

		for (TreeNode n : node.getChildTreeNodes()) {
			if (n.getName().equals(firstNode.getName()) && list.size() == 1) {
				tempNode = node;
				isExist = true;
				break;
			} else if (n.getName().equals(firstNode.getName())) {
				// 还没有找到最后的位置，继续找
				pathList.remove(firstNode);
				tempNode = searchNode(n, pathList);
				return tempNode;
			}
		}

		// 如果没有找到，则新添加到孩子节点中
		if (!isExist) {
			tempNode = node;
		}

		return tempNode;
	}

	/**
	 * 判断目前构造的FP树是否是单条路径的
	 * 
	 * @param rootNode
	 *            当前FP树的根节点
	 * @return
	 */
	private boolean isSinglePath(TreeNode rootNode) {
		// 默认是单条路径
		boolean isSinglePath = true;
		ArrayList<TreeNode> childList;
		TreeNode node;
		node = rootNode;

		while (node.getChildTreeNodes() != null) {
			childList = node.getChildTreeNodes();
			if (childList.size() == 1) {
				node = childList.get(0);
			} else {
				isSinglePath = false;
				break;
			}
		}

		return isSinglePath;
	}

	/**
	 * 开始构建FP树
	 */
	public void startBuildingTree() {
		ArrayList<TreeNode> singleTransaction;
		ArrayList<ArrayList<TreeNode>> transactionList = new ArrayList<ArrayList<TreeNode>>();
		TreeNode tempNode;
		int count = 0;

		for (String[] idArray : totalGoodsID) {
			singleTransaction = new ArrayList<TreeNode>();
			for (String id : idArray) {
				count = itemCountMap.get(id);
				tempNode = new TreeNode(id, count);
				singleTransaction.add(tempNode);
			}

			// 根据支持度数的多少进行排序
			Collections.sort(singleTransaction);
			for (TreeNode node : singleTransaction) {
				// 支持度计数重新归为1
				node.setCount(1);
			}
			transactionList.add(singleTransaction);
		}

		buildFPTree(null, transactionList);
	}

	/**
	 * 输出此单条路径下的频繁模式
	 * 
	 * @param suffixPattern
	 *            后缀模式
	 * @param rootNode
	 *            单条路径FP树根节点
	 */
	private void printFrequentPattern(ArrayList<String> suffixPattern,
			TreeNode rootNode) {
		int suffixCount=0;
		String[] suffixStrings = new String[suffixPattern.size()]; 
		suffixPattern.toArray(suffixStrings);
		 for (String[] a : totalGoodsID) {  
             if (isStrArrayContain(a, suffixStrings)) {  
                 suffixCount++;  
             }  
         }  
		 if (suffixCount>=minSupportCount){
			 ArrayList<String> idArray = new ArrayList<String>();
				FrequentItem frequentItem;
				String[] fpArray;
				ArrayList<String> fpList;
				TreeNode temp;
				temp = rootNode;
				// 用于输出组合模式
				int length = 0;
				int num = 0;
				int[] binaryArray;

				while (temp.getChildTreeNodes() != null) {
					temp = temp.getChildTreeNodes().get(0);

					// 筛选支持度系数大于最小阈值的值
					if (temp.getCount() >= minSupportCount) {
						idArray.add(temp.getName());
					}
				}

				length = idArray.size();
				num = (int) Math.pow(2, length);
				for (int i = 0; i < num; i++) {
					fpList=new ArrayList<String>();//这里要重新初始化，避免出现重复项集
					binaryArray = new int[length];
					numToBinaryArray(binaryArray, i);

					// 如果后缀模式只有1个，不能输出自身
					if (suffixPattern.size() == 1 && i == 0) {
						continue;
					}
					// 先输出固有的后缀模式
					if (suffixPattern.size() > 1
							|| (suffixPattern.size() == 1 && idArray.size() > 0)) {
						for (String s : suffixPattern) {
							fpList.add(s);
//							System.out.print(s + ", ");
						}
					}
					// 输出路径上的组合模式
					for (int j = 0; j < length; j++) {
						if (binaryArray[j] == 1) {
							fpList.add(idArray.get(j));
//							System.out.print(idArray.get(j) + ", ");
						}
					}
					fpArray=new String[fpList.size()];
					fpList.toArray(fpArray);
					int count = 0;
					 for (String[] a : totalGoodsID) {  
			                if (isStrArrayContain(a, fpArray)) {  
			                    count++;  
			                }  
			            }  
					 //将挖掘到的频繁项集保存
					 frequentItem=new FrequentItem(fpArray, count);
					 //输出频繁项集和支持度
					// System.out.println(frequentItem.toString());
					 //根据得到的频繁项集计算关联规则
					// printAttachRule(frequentItem);
					 //根据得到的频繁项集计算类关联规则
					 printPrimeAssociationRule(frequentItem);
					
			
				}
		 }
		
	}


	
	/**
	 * 计算位于哪一列
	 * @return
	 */
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
	public int tiqushuzi(String string) {
		int index=0;
		String regEx = "[0-9]";  //用来提取字母
        String s = matchResult(Pattern.compile(regEx),string);  
         //A   1  B  2
        index =Integer.parseInt(s);	
        return index;
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
	/** 
     * 根据产生的频繁项集输出........
     *  
     * @param minConf 
     *            最小置信度阈值   张小旭
     */  
   
	public void printPrimeAssociationRule(FrequentItem frequentItem) {  
		
		//System.out.println("prime**************");
		
    	int count1;  //症状发生的次数
    	int count2;//症候发生的次数
    	//frequentItem.getCount()是症状和症候同时发生的次数
        ArrayList<String> childGroup1;  //保存症状信息
        ArrayList<String> childGroup2;  //保存症候信息（分类结果）
        String[] group1;  
        String[] group2;  
        String[] array = frequentItem.getIdArray();
        //array.length
        for (int i=0;i<array.length;i++){
        	childGroup1 = new ArrayList<String>();  
            childGroup2 = new ArrayList<String>();  
            count1 = 0;
            count2 = 0;  
//            childGroup1=(ArrayList<String>) Arrays.asList(array);
            childGroup1.addAll(Arrays.asList(array));
            childGroup1.remove(i);
            childGroup2.add(array[i]);
            
            if (strIsContained(classificationSet, childGroup2.get(0))) {//判断group2里的项集是否是分类结果可以生成类关联规则,接下来计算置信度     	
            	group1 = new String[childGroup1.size()];  
                group2 = new String[childGroup2.size()];  
      
                childGroup1.toArray(group1);  
                childGroup2.toArray(group2);  
      
                for (String[] a : totalGoodsID) {  
                    if (isStrArrayContain(a, group2)) {  
                        count2++;  
                    }  
                    if (isStrArrayContain(a, group1)){
                    	count1++;
                    }
                }  
               // System.out.println("");
                // {A}-->{B}的意思为在A的情况下发生B的概率  
                double coverage=(double)frequentItem.getCount()/(double)count2;
                //group1.length<2||coverage < minCoverage
                //coverage < minCoverage
                if (group1.length<2||coverage < minCoverage) {  
                	
                    // 不符合要求，不是强规则  
//                    System.out.println("由于此规则置信度未达到最小置信度的要求，不是强规则");  
                } else { 
                	  double allcount=totalGoodsID.size();	  
                	  double support_X1X2X3=count1;//症状发生的次数
                	  double support_D1=count2;//证候发生的次数
                	  double support_D1_X1X2X3=frequentItem.getCount();//症状在证候中发生的次数
                	  double support_NOD1=allcount-support_D1;//非D1发生的次数
                	  double support_NOD1_X1X2X3=support_X1X2X3-support_D1_X1X2X3;
                	  double support_NOD1_NOX1X2X3=support_NOD1-support_NOD1_X1X2X3;
                	  double specificity=support_NOD1_NOX1X2X3/support_NOD1;
                	 // System.out.println("specificity "+specificity);
                	  
                	  if (specificity>minSpecificity) {
                		  //System.out.println("**********************");
                		 
                		//  System.out.println("specificity "+specificity);
                		//  System.out.print("{");  
                          for (String s : group2) {  
                              //System.out.print(s + ", ");  
                          }  
                         // System.out.print("}-->");  
                         // System.out.print("{");  
                          for (String s : group1) {  
                             // System.out.print(s + ", ");  
                          } 
                         // System.out.println("}");
                         // System.out.println(MessageFormat.format(  
                      //            "confidence(覆盖率)：{0}/{1}={2}", frequentItem.getCount(), count2, frequentItem.getCount()  
                      //                    * 1.0 / count2));  
//                        //System.out.println("为强类关联规则"); 
                        //  System.out.println("******************************************");
					      //将主属性值群记录到该证候中
                          String disease_name=group2[0];
                          String[][] primeArr=new String[group1.length][2];
                          
                         int disId=tiqushuzi(disease_name);
						 Disease disease=diseases[disId];
							
						//说明是该证候的主属性值群
						ArrayList<String[][]> primeSymptomList=disease.getPrimeSymptom();
						String[][] Prime=new String[group1.length][2];	
						for (int k = 0; k < group1.length; k++) {
							String symptomName=group1[k];///AA
							
							//double symptomColumnIndex=columnIndex(symptomName);//该列的下标
							//double symptomId=tiqushuzi(symptomName);//该列该值的id
							//找到这个
							Prime[k][0]=symptomName;
							
							Prime[k][1]=specificity+"";
						}
						primeSymptomList.add(Prime);
                	  }   
                }
			}
        }
        
        
       
   
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
	public void classfier(){
		//System.out.println("classfier&&&&&&&&&&&&&&&&&&&&&7");
        for (int i = 0; i < diseases.length; i++) {
        	ArrayList<String[][]> primeSymptom=diseases[i].getPrimeSymptom();
        	//System.out.println();
        	System.out.println("第"+i+"个疾病的主属性值群有 "+primeSymptom.size()+" 个");
        	for (int j = 0; j < primeSymptom.size(); j++) {
        		String[][] arr=primeSymptom.get(j);
        		for (int k = 0; k < arr.length; k++) {
					System.out.print(arr[k][0]+" ");
				}
        		System.out.println();
			}
        	System.out.println();
		}
    	//dataArray.size()
    	double error=0;
    	double correct=0;
    	double all=0;
    	double same=0;
    	double Noprime=0;
    	double NoprimeCorrect=0;
    	double NoprimeError=0;
    	double speciCorrect=0;
    	double speciError=0;
    	double sameDis=0;
    	//
		for (int i = 0; i < dataArray.size(); i++) {//遍历每一行
			
			//System.out.println("第"+i+"行  %%%%%%%%%%%%%%%%%%");
			//System.out.println();
			String[] rowArr=dataArray.get(i);
			String readDisease=rowArr[rowArr.length-1];
			String[] symptomArr=new String[rowArr.length-2]; //用来存储每一行的症状
			//System.out.println();
			for (int j = 1; j < rowArr.length-1; j++) {//array[0]第一个数字是下标
				symptomArr[j-1]=rowArr[j];
				//System.out.print(symptomArr[j-1]+" ");
			}
			double [][] eachDisease=new double[diseases.length][3];//第一列记录该疾病有几个主属性值群，第二行记录该疾病的最大特异度，第三列该疾病与该行最小的距离
			for (int j = 0; j < diseases.length; j++) {//遍历每个疾病
				//System.out.println("在第"+j+"个疾病中遍历每个一主属性值群");
				Disease disease=diseases[j];
				ArrayList<String[][]> primeSymptom=disease.getPrimeSymptom();
				int MaxSpecificityID=-1;//先假设第一个主属性值群的特异度最大
				
				double MaxSpecificity=-1;
				
				double Minjuli=10000;//用来记录该疾病与该行疾病的最小距离
				
				
				int count=0;//记录该行中有该疾病几个主属性值群
				
				for (int k = 0; k < primeSymptom.size(); k++) {//遍历每一个主属性值群
					int sumchafang=0;
					
					Boolean boolean1=true;
					//System.out.println();
					String[][] prime=primeSymptom.get(k);
					double specificity=Double.parseDouble(prime[0][1]);
					
					//System.out.println("主属性值群是");
					for (int l = 0; l < prime.length; l++) {
						//System.out.print(prime[l][0]+" ");
					}
					//System.out.println();
					
					for (int l = 0; l < prime.length; l++) {//
						
						String primeString=prime[l][0];
						int columnIndex=columnIndex(primeString);
						String readData=symptomArr[columnIndex];
						//System.out.println("primeString  "+primeString+" readData   "+readData);
						
						if (primeString.equals(readData)) {
							
						}else {
							boolean1=false;
						}	
                        int valueprime=tiqushuzi(primeString);
                        int valueRead=tiqushuzi(readData);	
						int temp=sumchafang;
						sumchafang=temp+chafang(valueprime,valueRead);
					}
					double juli=Math.sqrt((double)sumchafang);	
					if (Minjuli>juli) {
						Minjuli=juli;
						
					}
					
					//System.out.println(boolean1);
					if (boolean1==true) {
						
						count++;
						if (MaxSpecificity<specificity) {
							MaxSpecificity=specificity;
							MaxSpecificityID=k;
						}
						
					}
					
					
				}
				
				eachDisease[j][0]=count;
				eachDisease[j][1]=MaxSpecificity;
				eachDisease[j][2]=Minjuli;
			}
			
			//System.out.println("8888888888888888888888888888");
			
			//System.out.println("eachDisease "+eachDisease.length);
			List<Integer> list=MaxDouble(eachDisease, diseases.length, 0);
			
			
			if (list.size()==1) {//（1）如果有一个疾病出现的主属性值群个数最多,则该疾病是预测疾病
				all++;
				int yuceId=0;
				for (int index:list) {
					yuceId=index;
				}
				String yuce=diseases[yuceId].getName();
				if (readDisease.equals(yuce)) {
					correct++;
				//	System.out.println("第"+i+"行预测正确，疾病是 "+readDisease);
				}else {
					error++;
					//System.out.println("第"+i+"行预测错误，预测疾病是 "+yuce+" 实际疾病是 "+readDisease);
				}
				
			}else if(list.size()==diseases.length){//（4）如果该行无主属性值群，则比较每个疾病与该行数据的距离，与该行数据距离最小的疾病就是预测疾病。
				all++;
				int index=list.get(0);
				double count=eachDisease[index][0];
				if (count==0) {
					
					Noprime++;
					List<Integer> list2=MinDouble(eachDisease, diseases.length, 2);
					if (list2.size()==1) {
						int index2=list2.get(0);
						String yuce=diseases[index2].getName();
						if (readDisease.equals(yuce)) {
							//System.out.println("第"+i+"行无主属性值群 预测正确");
							NoprimeCorrect++;
							correct++;
						}else {
							//System.out.println("预测错误");
							error++;
							NoprimeError++;
						}
					}else {
						
					//	System.out.println("第"+i+"行无主属性值群 有"+list2.size()+"个疾病的距离最近");
						sameDis++;
					}
					
					
					
				}
				
				
				
			}else if(list.size()>1&&list.size()<diseases.length) {//
				all++;
				//same++;
				//System.out.println("第"+i+"行有"+list.size()+"个值");
				List< Integer> listMaxSpe=MaxDouble(eachDisease, diseases.length, 1);
				if (listMaxSpe.size()==1) {//（2）如果有几个疾病拥有的主属性值群个数一样，那么比较最大特异度，最大特异度大的疾病为预测疾病
				   int index=listMaxSpe.get(0);
				   String yuce=diseases[index].getName();
				   if (readDisease.equals(yuce)) {
					//   System.out.println("第"+i+"预测正确，该行最大特异度唯一");
					   correct++;
					   speciCorrect++;
					   
					   
				   }else {
					  error++;
					  speciError++;
					  
				   }
				   	
				}else {//(3)如果有几个疾病拥有的主属性值群个数一样，那么比较最大特异度，若最大特异度不唯一,则比较距离
					List<Integer> list2=MinDouble(eachDisease, diseases.length, 2);
					if (list2.size()==1) {
						int index2=list2.get(0);
						String yuce=diseases[index2].getName();
						if (readDisease.equals(yuce)) {
						//	System.out.println("第"+i+"行特异度不唯一 预测正确");
							NoprimeCorrect++;
							correct++;
						}else {
							//System.out.println("第"+i+"行特异度不唯一 预测错误");
							error++;
							NoprimeError++;
						}
					}else {
						
						//System.out.println("第"+i+"行无主属性值群 有"+list2.size()+"个疾病的距离最近");
						sameDis++;
					}
				}
				
				
				
			}
			
			//	System.out.println("第"+i+"行有两个疾病的主属性值群最多");
				for (int l = 0; l < eachDisease.length; l++) {
					//System.out.println("数量是"+eachDisease[l][0]);
					//System.out.println("最大特异度是"+eachDisease[l][1]);
					//System.out.println("最小距离是"+eachDisease[l][2]);
				}
			
			
			//System.out.println(list.size());
			
			
			
			
			//System.out.println();
			
		}
        double rate=correct/all;
        System.out.println("rate: "+rate);
	   System.out.println("all: "+all);
	   System.out.println("correct: "+correct);
	   //System.out.println("error: "+error);
	 //  System.out.println("same: "+same);
	 //  System.out.println("Noprime "+Noprime);
	 //  System.out.println("NoprimeCorrect "+NoprimeCorrect);
	 //  System.out.println("NoprimeError "+NoprimeError);
	  // System.out.println("speciCorrect "+speciCorrect);
	  // System.out.println("speciError "+speciError);
	  // System.out.println("sameDis "+sameDis);
		
    }

	
	
	/** 
     * 根据产生的频繁项集输出类关联规则    王佳琪
     *  
     * @param minConf 
     *            最小置信度阈值 
     */  
    public void printClassificationAssociationRule(FrequentItem frequentItem) {  
        int count1;  
        ArrayList<String> childGroup1;  
        ArrayList<String> childGroup2;  
        String[] group1;  
        String[] group2;  
        String[] array = frequentItem.getIdArray();
        for (int i=0;i<array.length;i++){
        	childGroup1 = new ArrayList<String>();  
            childGroup2 = new ArrayList<String>();  
            count1 = 0;  
//            childGroup1=(ArrayList<String>) Arrays.asList(array);
            childGroup1.addAll(Arrays.asList(array));
            childGroup1.remove(i);
            childGroup2.add(array[i]);
            if (strIsContained(classificationSet, childGroup2.get(0))) {
//            	可以生成类关联规则,接下来计算置信度
            	group1 = new String[childGroup1.size()];  
                group2 = new String[childGroup2.size()];  
      
                childGroup1.toArray(group1);  
                childGroup2.toArray(group2);  
      
                for (String[] a : totalGoodsID) {  
                    if (isStrArrayContain(a, group1)) {  
                        count1++;  
                    }  
                }  
      
                // {A}-->{B}的意思为在A的情况下发生B的概率  
              
                if (frequentItem.getCount() * 1.0 / count1 < minCoverage) {  
                    // 不符合要求，不是强规则  
//                    System.out.println("由于此规则置信度未达到最小置信度的要求，不是强规则");  
                } else {  
                	  System.out.print("{");  
                      for (String s : group1) {  
                          System.out.print(s + ", ");  
                      }  
                      System.out.print("}-->");  
                      System.out.print("{");  
                      for (String s : group2) {  
                          System.out.print(s + ", ");  
                      }  
                      System.out.print(MessageFormat.format(  
                              "},confidence(置信度)：{0}/{1}={2}", frequentItem.getCount(), count1, frequentItem.getCount()  
                                      * 1.0 / count1));  
                    System.out.println("为强类关联规则");  
                }
			}
        }
   
    }
	
	
	
	/** 
     * 根据产生的频繁项集输出关联规则 
     *  
     * @param minConf 
     *            最小置信度阈值 
     */  
    public void printAttachRule(FrequentItem frequentItem) {  
        int count1 = 0;  
        ArrayList<String> childGroup1;  
        ArrayList<String> childGroup2;  
        String[] group1;  
        String[] group2;  
        // 以最后一个频繁项集做关联规则的输出  
        String[] array = frequentItem.getIdArray();
//        String[] array = resultItem.get(resultItem.size() - 1).getIdArray();  
        // 子集总数，计算的时候除去自身和空集  
        int totalNum = (int) Math.pow(2, array.length);  
//        String[] temp;  
        // 二进制数组，用来代表各个子集  
        int[] binaryArray;  
        // 除去头和尾部  
        for (int i = 1; i < totalNum - 1; i++) {  
            binaryArray = new int[array.length];  
            numToBinaryArray(binaryArray, i);  
  
            childGroup1 = new ArrayList<String>();  
            childGroup2 = new ArrayList<String>();  
            count1 = 0;  
            // 按照二进制位关系取出子集  
            for (int j = 0; j < binaryArray.length; j++) {  
                if (binaryArray[j] == 1) {  
                    childGroup1.add(array[j]);  
                } else {  
                    childGroup2.add(array[j]);  
                }  
            }  
  
            group1 = new String[childGroup1.size()];  
            group2 = new String[childGroup2.size()];  
  
            childGroup1.toArray(group1);  
            childGroup2.toArray(group2);  
  
            for (String[] a : totalGoodsID) {  
                if (isStrArrayContain(a, group1)) {  
                    count1++;  
                }  
            }  
  
            // {A}-->{B}的意思为在A的情况下发生B的概率  
          
            if (frequentItem.getCount() * 1.0 / count1 < minCoverage) {  
                // 不符合要求，不是强规则  
//                System.out.println("由于此规则置信度未达到最小置信度的要求，不是强规则");  
            } else {  
            	  System.out.print("{");  
                  for (String s : group1) {  
                      System.out.print(s + ", ");  
                  }  
                  System.out.print("}-->");  
                  System.out.print("{");  
                  for (String s : group2) {  
                      System.out.print(s + ", ");  
                  }  
                  System.out.print(MessageFormat.format(  
                          "},confidence(置信度)：{0}/{1}={2}", frequentItem.getCount(), count1, frequentItem.getCount()  
                                  * 1.0 / count1));  
                System.out.println("为强规则");  
            }  
        }  
  
    } 

	/**
	 * 数字转为二进制形式
	 * 
	 * @param binaryArray
	 *            转化后的二进制数组形式
	 * @param num
	 *            待转化数字
	 */
	private void numToBinaryArray(int[] binaryArray, int num) {
		int index = 0;
		while (num != 0) {
			binaryArray[index] = num % 2;
			index++;
			num /= 2;
		}
	}
	 /** 
     * 数组array2是否包含于array1中，不需要完全一样 
     *  
     * @param array1 
     * @param array2 
     * @return 
     */  
    private boolean isStrArrayContain(String[] array1, String[] array2) {  
        boolean isContain = true;  
        for (String s2 : array2) {  
            isContain = false;  
            for (String s1 : array1) {  
                // 只要s2字符存在于array1中，这个字符就算包含在array1中  
                if (s2.equals(s1)) {  
                    isContain = true;  
                    break;  
                }  
            }  
  
            // 一旦发现不包含的字符，则array2数组不包含于array1中  
            if (!isContain) {  
                break;  
            }  
        }  
  
        return isContain;  
    }  
    /** 
     * 判断单个字符是否包含在字符数组中 
     *  
     * @param array 
     *            字符数组 
     * @param s 
     *            判断的单字符 
     * @return 
     */  
    private boolean strIsContained(String[] array, String s) {  
        boolean isContained = false;  
  
        for (String str : array) {  
            if (str.equals(s)) {  
                isContained = true;  
                break;  
            }  
        }  
  
        return isContained;  
    }  

}
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
 * ������ֵȺ
 * 
 ��1������������ֵ����������ֵȺ�������ļ���Ψһ����ô�ü�������Ԥ�⼲��
 ��2������������ֵ�����ǲ�������һ��������������ֵ������࣬��Ƚ���������ȣ������������Ψһ����ü�������Ԥ�⼲����
��3������������ֵ�����ǲ�������һ��������������ֵ������࣬��Ƚ���������ȣ�����������Ȳ�Ψһ����ô���ҳ���ô���ҳ�����һ�����ݾ�����С��С�ļ������ü�����Ԥ�⼲����
��4������������ֵ�Ļ�,��ô���ҳ�����һ�����ݾ�����С�ļ������ü�����Ԥ�⼲����
 * 
 */
public class FPTreeTool {
	// ���������ļ�λ��
	private String filePath;
	// ��С֧�ֶ���ֵ
	private int minSupportCount;
	// ��������ID��¼
	private ArrayList<String[]> totalGoodsID;
	// ����ID��ͳ����Ŀӳ����������������ʹ��
	private HashMap<String, Integer> itemCountMap;
	//��С���ζȷ�ֵ
	private double minCoverage;
	//��С�����
	private double minSpecificity;
	//��������
	private String[] classificationSet;
	private Disease[] diseases;//������¼���е�֤�����
	private Column[] columns;//������¼���е�֤����
	
	private int symptom_columnSize;
	private ArrayList<String[]> dataArray;//�����洢�ļ������������
	
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
	 * ���ļ��ж�ȡ����
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
			//System.out.println("֢״���� "+symptom_columnSize);
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
		itemCountMap = new HashMap<String,Integer>();// ����ID��ͳ����Ŀӳ����������������ʹ��
		totalGoodsID = new ArrayList<String[]>();// ��������ID��¼
		for (String[] a : dataArray) {//����ÿһ��
			//����Ҫͳ��һ���ж���֢״�У�����֢״�У��һ����ÿһ�ж�ͳ���ˣ�Ȼ��ͳ���ж���ֵ ÿ��ֵ��ţ�����
			temp = new String[a.length - 1];
			System.arraycopy(a, 1, temp, 0, a.length - 1);
			totalGoodsID.add(temp);//��һ������ID��¼
			for (String s : temp) {
				if (!itemCountMap.containsKey(s)) {
					count = 1;
				} else {
					count = ((int) itemCountMap.get(s));
					// ֧�ֶȼ�����1
					count++;
				}
				// ���±���
				itemCountMap.put(s, count);
			}
			for (int i = 0; i < temp.length-1; i++) {//��Ҫ��ÿһ�е�ֵ�ӵ�hm��
				Column column=columns[i];
				HashMap<String, String>hm=column.gethMap();
				hm.put(temp[i], temp[i]);
			}
		}
		for (int i = 0; i < columns.length; i++) {
			//System.out.println("��"+i+"��������");
			Column column=columns[i];
			HashMap< String,String > hMap=column.gethMap();
			int valueCount=hMap.keySet().size();
			column.setColumnValues(new ColumnValue[valueCount]);
			//System.out.println("valueCount"+valueCount);
			ColumnValue[] columnValues=column.getColumnValues();//��¼���е�ֵ
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
			//System.out.println("��"+i+"��������"+columnValues.length+"��ֵ");
			for (int j = 0; j < columnValues.length; j++) {
				//System.out.print(columnValues[j].getValue()+" ");
			}
			//System.out.println();
		}
		
		
	}

	/**
	 * ���������¼����FP��
	 */
	private void buildFPTree(ArrayList<String> suffixPattern,
			ArrayList<ArrayList<TreeNode>> transctionList) {
		// ����һ���ո��ڵ�
		TreeNode rootNode = new TreeNode(null, 0);
		int count = 0;
		// �ڵ��Ƿ����
		boolean isExist = false;
		ArrayList<TreeNode> childNodes;
		ArrayList<TreeNode> pathList;
		// ��ͬ���ͽڵ��������ڹ�����µ�FP��
		HashMap<String, ArrayList<TreeNode>> linkedNode = new HashMap<String,ArrayList<TreeNode>>();
		HashMap<String, Integer> countNode = new HashMap<String,Integer>();
		// ���������¼��һ��������FP��
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
						// ����ҵ�������ͬ�������֧�ֶȼ���
						if (node.getName().equals(node2.getName())) {
							count = node2.getCount() + node.getCount();
							node2.setCount(count);
							// ��ʶ���ҵ��ڵ�λ��
							isExist = true;
							break;
						}
					}

					if (!isExist) {
						// ���û���ҵ���������ӽڵ�
						childNodes.add(node);
						node.setParentNode(searchedNode);
						nodeAddToLinkedList(node, linkedNode);
					}
				}

			}
		}

		// ���FP���Ѿ��ǵ���·�����������ʱ��Ƶ��ģʽ
		if (isSinglePath(rootNode)) {
			printFrequentPattern(suffixPattern, rootNode);
		//	System.out.println("-------");
		} else {
			ArrayList<ArrayList<TreeNode>> tList;
			ArrayList<String> sPattern;
			if (suffixPattern == null) {
				sPattern = new ArrayList<String>();
			} else {
				// ����һ�����������⻥�����õ�Ӱ��
				sPattern = (ArrayList<String>) suffixPattern.clone();
			}

			// ���ýڵ��������µ�����
			for (Map.Entry entry : countNode.entrySet()) {
				// ��ӵ���׺ģʽ��
				sPattern.add((String) entry.getKey());
				//��ȡ��������ģʽ������Ϊ�µ�����
				tList = getTransactionList((String) entry.getKey(), linkedNode);
				
				/*System.out.print("[��׺ģʽ]��{");
				for(String s: sPattern){
					System.out.print(s + ", ");
				}
				System.out.print("}, ��ʱ������ģʽ����");
				for(ArrayList<TreeNode> tnList: tList){
					System.out.print("{");
					for(TreeNode n: tnList){
						System.out.print(n.getName() + ", ");
					}
					System.out.print("}, ");
				}
				System.out.println();*/
				// �ݹ鹹��FP��
				buildFPTree(sPattern, tList);
				// �ٴ��Ƴ�������첻ͬ�ĺ�׺ģʽ����ֹ�Ժ�����ɸ���
				sPattern.remove((String) entry.getKey());
			}
		}
	}

	/**
	 * ���ڵ���뵽ͬ���ͽڵ��������
	 * 
	 * @param node
	 *            ������ڵ�
	 * @param linkedList
	 *            ����ͼ
	 */
	private void nodeAddToLinkedList(TreeNode node,
			HashMap<String, ArrayList<TreeNode>> linkedList) {
		String name = node.getName();
		ArrayList<TreeNode> list;

		if (linkedList.containsKey(name)) {
			list = linkedList.get(name);
			// ��node��ӵ��˶�����
			list.add(node);
		} else {
			list = new ArrayList<TreeNode>();
			list.add(node);
			linkedList.put(name, list);
		}
	}

	/**
	 * ������������µ�����
	 * 
	 * @param name
	 *            �ڵ�����
	 * @param linkedList
	 *            ����
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

			// ����֧�ֶȼ����÷�תһ��
			Collections.reverse(singleTansaction);

			for (TreeNode node2 : singleTansaction) {
				// ֧�ֶȼ���������ģʽ��׺һ��
				node2.setCount(node.getCount());
			}

			if (singleTansaction.size() > 0) {
				tList.add(singleTansaction);
			}
		}

		return tList;
	}

	/**
	 * �ڵ����
	 * 
	 * @param node
	 *            ������ڵ�
	 * @param nodeCount
	 *            ����ӳ��ͼ
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
	 * ������ڵ�ĵִ�λ�ýڵ㣬�Ӹ��ڵ㿪ʼ����Ѱ�Ҵ�����ڵ��λ��
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
		// ����תһ�飬�������ͬһ����
		for (TreeNode node2 : list) {
			pathList.add(node2);
		}

		// ���û�к��ӽڵ㣬��ֱ�ӷ��أ��ڴ˽ڵ�������ӽڵ�
		if (node.getChildTreeNodes() == null) {
			return node;
		}

		for (TreeNode n : node.getChildTreeNodes()) {
			if (n.getName().equals(firstNode.getName()) && list.size() == 1) {
				tempNode = node;
				isExist = true;
				break;
			} else if (n.getName().equals(firstNode.getName())) {
				// ��û���ҵ�����λ�ã�������
				pathList.remove(firstNode);
				tempNode = searchNode(n, pathList);
				return tempNode;
			}
		}

		// ���û���ҵ���������ӵ����ӽڵ���
		if (!isExist) {
			tempNode = node;
		}

		return tempNode;
	}

	/**
	 * �ж�Ŀǰ�����FP���Ƿ��ǵ���·����
	 * 
	 * @param rootNode
	 *            ��ǰFP���ĸ��ڵ�
	 * @return
	 */
	private boolean isSinglePath(TreeNode rootNode) {
		// Ĭ���ǵ���·��
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
	 * ��ʼ����FP��
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

			// ����֧�ֶ����Ķ��ٽ�������
			Collections.sort(singleTransaction);
			for (TreeNode node : singleTransaction) {
				// ֧�ֶȼ������¹�Ϊ1
				node.setCount(1);
			}
			transactionList.add(singleTransaction);
		}

		buildFPTree(null, transactionList);
	}

	/**
	 * ����˵���·���µ�Ƶ��ģʽ
	 * 
	 * @param suffixPattern
	 *            ��׺ģʽ
	 * @param rootNode
	 *            ����·��FP�����ڵ�
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
				// ����������ģʽ
				int length = 0;
				int num = 0;
				int[] binaryArray;

				while (temp.getChildTreeNodes() != null) {
					temp = temp.getChildTreeNodes().get(0);

					// ɸѡ֧�ֶ�ϵ��������С��ֵ��ֵ
					if (temp.getCount() >= minSupportCount) {
						idArray.add(temp.getName());
					}
				}

				length = idArray.size();
				num = (int) Math.pow(2, length);
				for (int i = 0; i < num; i++) {
					fpList=new ArrayList<String>();//����Ҫ���³�ʼ������������ظ��
					binaryArray = new int[length];
					numToBinaryArray(binaryArray, i);

					// �����׺ģʽֻ��1���������������
					if (suffixPattern.size() == 1 && i == 0) {
						continue;
					}
					// ��������еĺ�׺ģʽ
					if (suffixPattern.size() > 1
							|| (suffixPattern.size() == 1 && idArray.size() > 0)) {
						for (String s : suffixPattern) {
							fpList.add(s);
//							System.out.print(s + ", ");
						}
					}
					// ���·���ϵ����ģʽ
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
					 //���ھ򵽵�Ƶ�������
					 frequentItem=new FrequentItem(fpArray, count);
					 //���Ƶ�����֧�ֶ�
					// System.out.println(frequentItem.toString());
					 //���ݵõ���Ƶ��������������
					// printAttachRule(frequentItem);
					 //���ݵõ���Ƶ����������������
					 printPrimeAssociationRule(frequentItem);
					
			
				}
		 }
		
	}


	
	/**
	 * ����λ����һ��
	 * @return
	 */
	public int columnIndex(String string) {
		int index=0;
		String regEx = "[a-z||A-Z]";  //������ȡ��ĸ
        String s = matchResult(Pattern.compile(regEx),string);  
         //A   1  B  2
        if (s.length()==1) {
        	index=s.codePointAt(0)-65;
        }else if (s.length()==2){           //AA  27��  AB
			int shang=s.codePointAt(0)-65;  //����   0+1 
			int yushu=s.codePointAt(1)-65; //������  0+1  
			index=26*(shang+1)+yushu+1-1;
		}
        
        	
		
        return index;
	}
	public int tiqushuzi(String string) {
		int index=0;
		String regEx = "[0-9]";  //������ȡ��ĸ
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
     * ���ݲ�����Ƶ������........
     *  
     * @param minConf 
     *            ��С���Ŷ���ֵ   ��С��
     */  
   
	public void printPrimeAssociationRule(FrequentItem frequentItem) {  
		
		//System.out.println("prime**************");
		
    	int count1;  //֢״�����Ĵ���
    	int count2;//֢�����Ĵ���
    	//frequentItem.getCount()��֢״��֢��ͬʱ�����Ĵ���
        ArrayList<String> childGroup1;  //����֢״��Ϣ
        ArrayList<String> childGroup2;  //����֢����Ϣ����������
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
            
            if (strIsContained(classificationSet, childGroup2.get(0))) {//�ж�group2�����Ƿ��Ƿ����������������������,�������������Ŷ�     	
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
                // {A}-->{B}����˼Ϊ��A������·���B�ĸ���  
                double coverage=(double)frequentItem.getCount()/(double)count2;
                //group1.length<2||coverage < minCoverage
                //coverage < minCoverage
                if (group1.length<2||coverage < minCoverage) {  
                	
                    // ������Ҫ�󣬲���ǿ����  
//                    System.out.println("���ڴ˹������Ŷ�δ�ﵽ��С���Ŷȵ�Ҫ�󣬲���ǿ����");  
                } else { 
                	  double allcount=totalGoodsID.size();	  
                	  double support_X1X2X3=count1;//֢״�����Ĵ���
                	  double support_D1=count2;//֤�����Ĵ���
                	  double support_D1_X1X2X3=frequentItem.getCount();//֢״��֤���з����Ĵ���
                	  double support_NOD1=allcount-support_D1;//��D1�����Ĵ���
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
                      //            "confidence(������)��{0}/{1}={2}", frequentItem.getCount(), count2, frequentItem.getCount()  
                      //                    * 1.0 / count2));  
//                        //System.out.println("Ϊǿ���������"); 
                        //  System.out.println("******************************************");
					      //��������ֵȺ��¼����֤����
                          String disease_name=group2[0];
                          String[][] primeArr=new String[group1.length][2];
                          
                         int disId=tiqushuzi(disease_name);
						 Disease disease=diseases[disId];
							
						//˵���Ǹ�֤���������ֵȺ
						ArrayList<String[][]> primeSymptomList=disease.getPrimeSymptom();
						String[][] Prime=new String[group1.length][2];	
						for (int k = 0; k < group1.length; k++) {
							String symptomName=group1[k];///AA
							
							//double symptomColumnIndex=columnIndex(symptomName);//���е��±�
							//double symptomId=tiqushuzi(symptomName);//���и�ֵ��id
							//�ҵ����
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
	public List<Integer> MaxDouble(double[][] arr,int rowsize,int columnId){//����һ��������Ϊ���Ŀ����кü�������������������¼�����ĵ���±꣬
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
	 public List<Integer> MinDouble(double[][] arr,int rowsize,int columnId){//����һ��������Ϊ���Ŀ����кü�������������������¼�����ĵ���±꣬
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
        	System.out.println("��"+i+"��������������ֵȺ�� "+primeSymptom.size()+" ��");
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
		for (int i = 0; i < dataArray.size(); i++) {//����ÿһ��
			
			//System.out.println("��"+i+"��  %%%%%%%%%%%%%%%%%%");
			//System.out.println();
			String[] rowArr=dataArray.get(i);
			String readDisease=rowArr[rowArr.length-1];
			String[] symptomArr=new String[rowArr.length-2]; //�����洢ÿһ�е�֢״
			//System.out.println();
			for (int j = 1; j < rowArr.length-1; j++) {//array[0]��һ���������±�
				symptomArr[j-1]=rowArr[j];
				//System.out.print(symptomArr[j-1]+" ");
			}
			double [][] eachDisease=new double[diseases.length][3];//��һ�м�¼�ü����м���������ֵȺ���ڶ��м�¼�ü������������ȣ������иü����������С�ľ���
			for (int j = 0; j < diseases.length; j++) {//����ÿ������
				//System.out.println("�ڵ�"+j+"�������б���ÿ��һ������ֵȺ");
				Disease disease=diseases[j];
				ArrayList<String[][]> primeSymptom=disease.getPrimeSymptom();
				int MaxSpecificityID=-1;//�ȼ����һ��������ֵȺ����������
				
				double MaxSpecificity=-1;
				
				double Minjuli=10000;//������¼�ü�������м�������С����
				
				
				int count=0;//��¼�������иü�������������ֵȺ
				
				for (int k = 0; k < primeSymptom.size(); k++) {//����ÿһ��������ֵȺ
					int sumchafang=0;
					
					Boolean boolean1=true;
					//System.out.println();
					String[][] prime=primeSymptom.get(k);
					double specificity=Double.parseDouble(prime[0][1]);
					
					//System.out.println("������ֵȺ��");
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
			
			
			if (list.size()==1) {//��1�������һ���������ֵ�������ֵȺ�������,��ü�����Ԥ�⼲��
				all++;
				int yuceId=0;
				for (int index:list) {
					yuceId=index;
				}
				String yuce=diseases[yuceId].getName();
				if (readDisease.equals(yuce)) {
					correct++;
				//	System.out.println("��"+i+"��Ԥ����ȷ�������� "+readDisease);
				}else {
					error++;
					//System.out.println("��"+i+"��Ԥ�����Ԥ�⼲���� "+yuce+" ʵ�ʼ����� "+readDisease);
				}
				
			}else if(list.size()==diseases.length){//��4�����������������ֵȺ����Ƚ�ÿ��������������ݵľ��룬��������ݾ�����С�ļ�������Ԥ�⼲����
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
							//System.out.println("��"+i+"����������ֵȺ Ԥ����ȷ");
							NoprimeCorrect++;
							correct++;
						}else {
							//System.out.println("Ԥ�����");
							error++;
							NoprimeError++;
						}
					}else {
						
					//	System.out.println("��"+i+"����������ֵȺ ��"+list2.size()+"�������ľ������");
						sameDis++;
					}
					
					
					
				}
				
				
				
			}else if(list.size()>1&&list.size()<diseases.length) {//
				all++;
				//same++;
				//System.out.println("��"+i+"����"+list.size()+"��ֵ");
				List< Integer> listMaxSpe=MaxDouble(eachDisease, diseases.length, 1);
				if (listMaxSpe.size()==1) {//��2������м�������ӵ�е�������ֵȺ����һ������ô�Ƚ��������ȣ��������ȴ�ļ���ΪԤ�⼲��
				   int index=listMaxSpe.get(0);
				   String yuce=diseases[index].getName();
				   if (readDisease.equals(yuce)) {
					//   System.out.println("��"+i+"Ԥ����ȷ��������������Ψһ");
					   correct++;
					   speciCorrect++;
					   
					   
				   }else {
					  error++;
					  speciError++;
					  
				   }
				   	
				}else {//(3)����м�������ӵ�е�������ֵȺ����һ������ô�Ƚ��������ȣ����������Ȳ�Ψһ,��ȽϾ���
					List<Integer> list2=MinDouble(eachDisease, diseases.length, 2);
					if (list2.size()==1) {
						int index2=list2.get(0);
						String yuce=diseases[index2].getName();
						if (readDisease.equals(yuce)) {
						//	System.out.println("��"+i+"������Ȳ�Ψһ Ԥ����ȷ");
							NoprimeCorrect++;
							correct++;
						}else {
							//System.out.println("��"+i+"������Ȳ�Ψһ Ԥ�����");
							error++;
							NoprimeError++;
						}
					}else {
						
						//System.out.println("��"+i+"����������ֵȺ ��"+list2.size()+"�������ľ������");
						sameDis++;
					}
				}
				
				
				
			}
			
			//	System.out.println("��"+i+"��������������������ֵȺ���");
				for (int l = 0; l < eachDisease.length; l++) {
					//System.out.println("������"+eachDisease[l][0]);
					//System.out.println("����������"+eachDisease[l][1]);
					//System.out.println("��С������"+eachDisease[l][2]);
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
     * ���ݲ�����Ƶ���������������    ������
     *  
     * @param minConf 
     *            ��С���Ŷ���ֵ 
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
//            	�����������������,�������������Ŷ�
            	group1 = new String[childGroup1.size()];  
                group2 = new String[childGroup2.size()];  
      
                childGroup1.toArray(group1);  
                childGroup2.toArray(group2);  
      
                for (String[] a : totalGoodsID) {  
                    if (isStrArrayContain(a, group1)) {  
                        count1++;  
                    }  
                }  
      
                // {A}-->{B}����˼Ϊ��A������·���B�ĸ���  
              
                if (frequentItem.getCount() * 1.0 / count1 < minCoverage) {  
                    // ������Ҫ�󣬲���ǿ����  
//                    System.out.println("���ڴ˹������Ŷ�δ�ﵽ��С���Ŷȵ�Ҫ�󣬲���ǿ����");  
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
                              "},confidence(���Ŷ�)��{0}/{1}={2}", frequentItem.getCount(), count1, frequentItem.getCount()  
                                      * 1.0 / count1));  
                    System.out.println("Ϊǿ���������");  
                }
			}
        }
   
    }
	
	
	
	/** 
     * ���ݲ�����Ƶ�������������� 
     *  
     * @param minConf 
     *            ��С���Ŷ���ֵ 
     */  
    public void printAttachRule(FrequentItem frequentItem) {  
        int count1 = 0;  
        ArrayList<String> childGroup1;  
        ArrayList<String> childGroup2;  
        String[] group1;  
        String[] group2;  
        // �����һ��Ƶ�����������������  
        String[] array = frequentItem.getIdArray();
//        String[] array = resultItem.get(resultItem.size() - 1).getIdArray();  
        // �Ӽ������������ʱ���ȥ����Ϳռ�  
        int totalNum = (int) Math.pow(2, array.length);  
//        String[] temp;  
        // ���������飬������������Ӽ�  
        int[] binaryArray;  
        // ��ȥͷ��β��  
        for (int i = 1; i < totalNum - 1; i++) {  
            binaryArray = new int[array.length];  
            numToBinaryArray(binaryArray, i);  
  
            childGroup1 = new ArrayList<String>();  
            childGroup2 = new ArrayList<String>();  
            count1 = 0;  
            // ���ն�����λ��ϵȡ���Ӽ�  
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
  
            // {A}-->{B}����˼Ϊ��A������·���B�ĸ���  
          
            if (frequentItem.getCount() * 1.0 / count1 < minCoverage) {  
                // ������Ҫ�󣬲���ǿ����  
//                System.out.println("���ڴ˹������Ŷ�δ�ﵽ��С���Ŷȵ�Ҫ�󣬲���ǿ����");  
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
                          "},confidence(���Ŷ�)��{0}/{1}={2}", frequentItem.getCount(), count1, frequentItem.getCount()  
                                  * 1.0 / count1));  
                System.out.println("Ϊǿ����");  
            }  
        }  
  
    } 

	/**
	 * ����תΪ��������ʽ
	 * 
	 * @param binaryArray
	 *            ת����Ķ�����������ʽ
	 * @param num
	 *            ��ת������
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
     * ����array2�Ƿ������array1�У�����Ҫ��ȫһ�� 
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
                // ֻҪs2�ַ�������array1�У�����ַ����������array1��  
                if (s2.equals(s1)) {  
                    isContain = true;  
                    break;  
                }  
            }  
  
            // һ�����ֲ��������ַ�����array2���鲻������array1��  
            if (!isContain) {  
                break;  
            }  
        }  
  
        return isContain;  
    }  
    /** 
     * �жϵ����ַ��Ƿ�������ַ������� 
     *  
     * @param array 
     *            �ַ����� 
     * @param s 
     *            �жϵĵ��ַ� 
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
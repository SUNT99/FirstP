package FPTree;


public class ClassAssociationRule {
	//规则前件
private String[] frequentStrings;
	//分类结果
private String classification;
	//前件项集支持度
private int count1;
	//整个规则的支持度
private int count2;




public ClassAssociationRule(String[] frequentStrings,
			String classification, int count1, int count2) {
		super();
		this.frequentStrings = frequentStrings;
		this.classification = classification;
		this.count1 = count1;
		this.count2 = count2;
	}

//计算置信度confidence
public double getConfidence(){
	return (count2*1.0) / count1;
	
}

public String[] getFrequentStrings() {
	return frequentStrings;
}
public void setFrequentStrings(String[] frequentStrings) {
	this.frequentStrings = frequentStrings;
}
public String getClassification() {
	return classification;
}
public void setClassification(String classification) {
	this.classification = classification;
}
public int getCount1() {
	return count1;
}
public void setCount1(int count1) {
	this.count1 = count1;
}
public int getCount2() {
	return count2;
}
public void setCount2(int count2) {
	this.count2 = count2;
}


}


public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int count=0;//��¼������
		// TODO Auto-generated method stub
        PrimeAttr primeAttr=new PrimeAttr();
        
        
        //letter-recognition.data
        primeAttr.pre_treatment("N",4,2,"G:\\KuGou\\b.txt");
       //��һ��������ʾ���ޱ���У��ڶ���������ʾ���������ڵڼ���,
	   //������������ʾ�ɽ��������Է�Ϊ���Σ����ĸ�������ʾ�����ļ���·��
        primeAttr.prime(0.5,0.8);
      //  System.out.println("���з���Ľ��");
        //primeAttr.classifier();
	}

}

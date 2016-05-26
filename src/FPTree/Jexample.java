package FPTree;
import java.awt.Button;
import java.awt.Container;  
import java.awt.Font;
import java.awt.Point;  
import java.awt.Toolkit;  
import java.awt.event.ActionEvent;  
import java.awt.event.ActionListener;  
import java.io.BufferedReader;
import java.io.File;  
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import javax.swing.JButton;  
import javax.swing.JFileChooser;  
import javax.swing.JFrame;  
import javax.swing.JLabel;  
import javax.swing.JOptionPane;  
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;  
import javax.swing.JTextArea;
import javax.swing.JTextField;  
import javax.swing.UIManager;
/** 
 * ��Ϊ���ܶ���������������ʱ��ʱ��д�˸��򵥵İ��� Դ�����ȥ����Ҫ�Ŀ��Կ���������ʱ���æ���úֲܴ� 2011��1��4��23:22:31 
 *  
 * @author �ᰬ�� QQ 172794299 ���� qiailing.ok@163.com 
 *  
 */ 

public class Jexample implements ActionListener { 
	
    JFrame frame = new JFrame("������ֵ����ϵͳ");// ��ܲ���  
    JTabbedPane tabPane = new JTabbedPane();// ѡ�����  
    Container con = new Container();// 
    
    JLabel label=new JLabel();
    JTextField text1 = new JTextField();// TextField �ļ���Ŀ¼
    JButton button1=new JButton();
    JTextField text2=new JTextField();//������ʾ������coverage
    JLabel label2=new JLabel();//�������ʡ�
    JTextField text3=new JTextField();//������ʾ�����specificity
    JLabel label3=new JLabel();//������ȡ�
    JButton button2=new JButton();//�����ھ�������ֵ����
    
    JLabel label4=new JLabel();//������ʾ�����������ֵ�����
    
    JTextArea textArea=new JTextArea();//������ʾ����ִ�н��
    
    JScrollPane sp=new JScrollPane(textArea);
    
    JFileChooser jfc = new JFileChooser();// �ļ�ѡ����   
    
    
    JLabel label5=new JLabel("support");
    JTextField textField=new JTextField();
    
    Jexample() {  
    	Font f = new Font("����",Font.PLAIN,17);  
    	Font f1=new Font("����",Font.BOLD,25);
    	Font f2=new Font("����",Font.BOLD,17);
    	label.setFont(f1);
    	frame.setFont(f);
    	text1.setFont(f);
    	button1.setFont(f);
    	text2.setFont(f);
    	label2.setFont(f);
    	text3.setFont(f);
    	label3.setFont(f);
    	textArea.setFont(f);
    	button2.setFont(f);
    	label4.setFont(f2);
    	label5.setFont(f);
    	textField.setFont(f);
        jfc.setCurrentDirectory(new File("d://"));// �ļ�ѡ�����ĳ�ʼĿ¼��Ϊd��  
       
        double lx = Toolkit.getDefaultToolkit().getScreenSize().getWidth();  //���ǵõ�����Ļ�Ĵ�С������֮��Ϳ���ȷ�������Ҫ����Ļ��ʲôλ���ˣ�
          
        double ly = Toolkit.getDefaultToolkit().getScreenSize().getHeight();  
        frame.setLocation(new Point((int) (lx / 2)-150 , (int) (ly / 2)-150 ));// �趨���ڳ���λ��  
      
        frame.setSize(580, 600);// �趨���ڴ�С  (width,height)
        frame.setContentPane(tabPane);// ���ò���  
        
     
        label.setBounds(180 , 5, 250 , 30);
        label.setText("������ֵȺ����ϵͳ");
        
        text1.setBounds(20, 45, 200, 30);
        
        button1.setBounds(240, 45, 120, 30); 
        button1.setText("ѡ���ļ�");
        button1.setToolTipText("ѡ���ļ�");  
        button1.addActionListener(this); // ����¼�����  
        
        text2.setBounds(20, 85, 40, 30);
        label2.setBounds(70, 85, 100, 30);
        label2.setText("coverage");
        
        text3.setBounds(190, 85, 40, 30);
        label3.setBounds(240, 85, 100, 30);
        label3.setText("specificity");
        
        
        textField.setBounds(430, 85, 40, 30);
        label5.setBounds(480, 85, 100, 30);
        
        
        
        button2.setBounds(20, 125, 150, 30);
        button2.setText("����ϵͳ");
        button2.addActionListener(this);//����¼�����
        
        
       

       
        
        
        label4.setBounds(190, 165, 200 , 30);
        label4.setText("ϵͳ����������");
      
        
        
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
        sp.setBounds(90, 215, 20 , 280);
        textArea.setBounds(110, 215, 390 , 280);
        
        
        textArea.setLineWrap(true);//ʵ�ֵ����Զ�����
        textArea.setWrapStyleWord(true);  
        textArea.setAutoscrolls(true);
        
        
       // sp.setViewportView(textArea);
        
        con.add(label);
        con.add(text1);
        con.add(button1);  
        con.add(text2);
        con.add(label2);
        con.add(text3);
        con.add(label3);
        con.add(button2);
        con.add(label4);
        con.add(textArea);
        con.add(sp);
        con.add(label5);
        con.add(textField);
        frame.setVisible(true);// ���ڿɼ�  
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// ʹ�ܹرմ��ڣ���������  
        //tabPane.add("1���", con);// ��Ӳ���1  
        tabPane.add("", con);// ��Ӳ���1
        
        
    }  
    /** 
     * ʱ������ķ��� 
     */  
    public void actionPerformed(ActionEvent e) {  
        if (e.getSource().equals(button1)) {  
            jfc.setFileSelectionMode(0);// �趨ֻ��ѡ���ļ�  
            int state = jfc.showOpenDialog(null);// �˾��Ǵ��ļ�ѡ��������Ĵ������  
            if (state == 1) {  
                return;// �����򷵻�  
            } else {  
                File f = jfc.getSelectedFile();// fΪѡ�񵽵��ļ�  
                text1.setText(f.getAbsolutePath());  
            }  
        } 
        if (e.getSource().equals(button2)) {
        	File file=new File("C:\\Users\\kilo\\Desktop\\b.txt");
            PrintStream ps=null;
			try {
				ps = new PrintStream(file);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}  
            System.setOut(ps);  
        	String filePath=text1.getText();
        	int support=Integer.parseInt(textField.getText());
        	double coverage=Double.parseDouble(text2.getText());   	
        	double specificity=Double.parseDouble(text3.getText());   	
        	
        	
            String[] classificationSet={"E0","E1","E2"};//iris
        	//String[] classificationSet={"CW0","CW1","CW2","CW3","CW4","CW5","CW6"};//С������
        	
        	FPTreeTool tool = new FPTreeTool(filePath, support,coverage,specificity,classificationSet);  

        	tool.startBuildingTree(); 
            tool.classfier();
            
          
        	try {
        		BufferedReader br = new BufferedReader(new FileReader(file));
            	String s = null;
                while((s = br.readLine())!=null){//ʹ��readLine������һ�ζ�һ��
                    textArea.append(s+" ");
                    textArea.append("\n");
                    
                }
                br.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}	
		}
    }  
   
    
    
    public static void main(String[] args) {
        new Jexample();  
    }  
} 

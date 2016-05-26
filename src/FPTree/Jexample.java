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
 * 因为看很多朋友在问试用临时抽时间写了个简单的案例 源码放上去，需要的可以看看，由于时间匆忙做得很粗糙 2011年1月4日23:22:31 
 *  
 * @author 漆艾林 QQ 172794299 邮箱 qiailing.ok@163.com 
 *  
 */ 

public class Jexample implements ActionListener { 
	
    JFrame frame = new JFrame("主属性值分析系统");// 框架布局  
    JTabbedPane tabPane = new JTabbedPane();// 选项卡布局  
    Container con = new Container();// 
    
    JLabel label=new JLabel();
    JTextField text1 = new JTextField();// TextField 文件的目录
    JButton button1=new JButton();
    JTextField text2=new JTextField();//用来显示覆盖率coverage
    JLabel label2=new JLabel();//“覆盖率”
    JTextField text3=new JTextField();//用来显示特异度specificity
    JLabel label3=new JLabel();//“特异度”
    JButton button2=new JButton();//用来挖掘主属性值操作
    
    JLabel label4=new JLabel();//用来显示“输出主属性值结果”
    
    JTextArea textArea=new JTextArea();//用来显示程序执行结果
    
    JScrollPane sp=new JScrollPane(textArea);
    
    JFileChooser jfc = new JFileChooser();// 文件选择器   
    
    
    JLabel label5=new JLabel("support");
    JTextField textField=new JTextField();
    
    Jexample() {  
    	Font f = new Font("宋体",Font.PLAIN,17);  
    	Font f1=new Font("黑体",Font.BOLD,25);
    	Font f2=new Font("黑体",Font.BOLD,17);
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
        jfc.setCurrentDirectory(new File("d://"));// 文件选择器的初始目录定为d盘  
       
        double lx = Toolkit.getDefaultToolkit().getScreenSize().getWidth();  //就是得到你屏幕的大小，这样之后就可以确定你程序要在屏幕的什么位置了！
          
        double ly = Toolkit.getDefaultToolkit().getScreenSize().getHeight();  
        frame.setLocation(new Point((int) (lx / 2)-150 , (int) (ly / 2)-150 ));// 设定窗口出现位置  
      
        frame.setSize(580, 600);// 设定窗口大小  (width,height)
        frame.setContentPane(tabPane);// 设置布局  
        
     
        label.setBounds(180 , 5, 250 , 30);
        label.setText("主属性值群分析系统");
        
        text1.setBounds(20, 45, 200, 30);
        
        button1.setBounds(240, 45, 120, 30); 
        button1.setText("选择文件");
        button1.setToolTipText("选择文件");  
        button1.addActionListener(this); // 添加事件处理  
        
        text2.setBounds(20, 85, 40, 30);
        label2.setBounds(70, 85, 100, 30);
        label2.setText("coverage");
        
        text3.setBounds(190, 85, 40, 30);
        label3.setBounds(240, 85, 100, 30);
        label3.setText("specificity");
        
        
        textField.setBounds(430, 85, 40, 30);
        label5.setBounds(480, 85, 100, 30);
        
        
        
        button2.setBounds(20, 125, 150, 30);
        button2.setText("运行系统");
        button2.addActionListener(this);//添加事件处理
        
        
       

       
        
        
        label4.setBounds(190, 165, 200 , 30);
        label4.setText("系统运行输出结果");
      
        
        
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
        sp.setBounds(90, 215, 20 , 280);
        textArea.setBounds(110, 215, 390 , 280);
        
        
        textArea.setLineWrap(true);//实现到边自动换行
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
        frame.setVisible(true);// 窗口可见  
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 使能关闭窗口，结束程序  
        //tabPane.add("1面板", con);// 添加布局1  
        tabPane.add("", con);// 添加布局1
        
        
    }  
    /** 
     * 时间监听的方法 
     */  
    public void actionPerformed(ActionEvent e) {  
        if (e.getSource().equals(button1)) {  
            jfc.setFileSelectionMode(0);// 设定只能选择到文件  
            int state = jfc.showOpenDialog(null);// 此句是打开文件选择器界面的触发语句  
            if (state == 1) {  
                return;// 撤销则返回  
            } else {  
                File f = jfc.getSelectedFile();// f为选择到的文件  
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
        	//String[] classificationSet={"CW0","CW1","CW2","CW3","CW4","CW5","CW6"};//小儿肺炎
        	
        	FPTreeTool tool = new FPTreeTool(filePath, support,coverage,specificity,classificationSet);  

        	tool.startBuildingTree(); 
            tool.classfier();
            
          
        	try {
        		BufferedReader br = new BufferedReader(new FileReader(file));
            	String s = null;
                while((s = br.readLine())!=null){//使用readLine方法，一次读一行
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

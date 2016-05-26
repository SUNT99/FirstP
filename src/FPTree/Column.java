package FPTree;

import java.util.HashMap;

import org.omg.CORBA.portable.ValueBase;

public class Column {
	private int columnIndex;
	private ColumnValue[] columnValues;//记录所有的值
	private HashMap< String,String > hMap=new HashMap<String, String>();
	
	public Column(int columnIndex){
		this.columnIndex=columnIndex;
	}
	
	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public ColumnValue[] getColumnValues() {
		return columnValues;
	}
	public void setColumnValues(ColumnValue[] columnValues) {
		this.columnValues = columnValues;
	}

	public HashMap<String, String> gethMap() {
		return hMap;
	}

	public void sethMap(HashMap<String, String> hMap) {
		this.hMap = hMap;
	}
	
}

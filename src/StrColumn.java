//用来记录字符串列
public class StrColumn {
	private int columnId;
	private StrColumnValue[] strColVal;
	public int getColumnId() {
		return columnId;
	}
	public void setColumnId(int columnId) {
		this.columnId = columnId;
	}
	public StrColumnValue[] getStrColVal() {
		return strColVal;
	}
	public void setStrColVal(StrColumnValue[] strColVal) {
		this.strColVal = strColVal;
	}
	public StrColumn(int id){
		this.columnId=id;
	}
}

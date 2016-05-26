public class NumColumn {
	private int columnId=0;
	private NumColumnValue[] numColVal=null;
	public int getColumnId() {
		return columnId;
	}
	public void setColumnId(int columnId) {
		this.columnId = columnId;
	}
	public NumColumnValue[] getNumColVal() {
		return numColVal;
	}
	public void setNumColVal(NumColumnValue[] numColVal) {
		this.numColVal = numColVal;
	}
	public NumColumn(int id){
		this.columnId=id;
	}
}

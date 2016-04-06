package ssbse;

public class CloneLine {
	private int[] tools;
	
	public CloneLine(int size) {
		tools = new int[size];
		// initialize everything to be '0'
		for (int i=0; i<size; i++)
			tools[i]=0;
	}
	
	public void setTool(int index) {
		tools[index] = 1;
	}
	
	public String print() {
		String output = "";
		for (int i=0; i<tools.length; i++)
			output += tools[i];
		return output;
	}
}

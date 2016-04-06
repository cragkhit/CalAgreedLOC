package ssbse;

public class ClonedLine {
	private int[] tools;
	
	public ClonedLine() {
		tools[0]=0;
		tools[1]=0;
		tools[2]=0;
		tools[3]=0;
	}
	
	public void setTool(int index) {
		tools[index] = 1;
	}
}

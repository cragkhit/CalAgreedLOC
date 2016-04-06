package ssbse;

public class CloneFile {
	private String name;
	private int locSize;
	private CloneLine[] cloneLines;
	private int noOfTools;
	
	public CloneFile(String filename, int size, int noOfTools) {
		this.name = filename;
		this.locSize = size;
		this.noOfTools = noOfTools;
		cloneLines = new CloneLine[size];
		for (int i=0; i<size; i++) {
			cloneLines[i] = new CloneLine(noOfTools);
		}
	}
	
	public int[] getAgreedLinesByTools() {
		int[] agreedLines = new int[noOfTools];
		for (CloneLine cl: cloneLines) {
			for (int i=0; i<noOfTools; i++) {
				
			}
		}
		return agreedLines;
	}

	public int getLocSize() {
		return locSize;
	}

	public void setLocSize(int locSize) {
		this.locSize = locSize;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public CloneLine[] getCloneLines() {
		return cloneLines;
	}

	public void setCloneLines(CloneLine[] cloneLines) {
		this.cloneLines = cloneLines;
	}
	
	public void addCloneLine(int line, int toolIndex) {
		cloneLines[line-1].setTool(toolIndex);
	}
	
	public String print() {
		String output = "";
		for (int i=0; i<cloneLines.length; i++) {
			output += cloneLines[i].print() + ", ";
		}
		return output;
	}
}

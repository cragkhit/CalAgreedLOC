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
		int[] agreedLines = new int[noOfTools*2];
		for (CloneLine cl: cloneLines) {
			int sum = -1;
			for (int i=0; i<noOfTools; i++) {
				sum += cl.getTool(i);
			}
			
			if (sum == 0) { // have only exactly 1 tool reporting
				if (cl.getTool(0) == 1)
					agreedLines[noOfTools]++;
				else if (cl.getTool(1) == 1)
					agreedLines[1 + noOfTools]++;
				else if (cl.getTool(2) == 1)
					agreedLines[2 + noOfTools]++;
				else if (cl.getTool(3) == 1)
					agreedLines[3 + noOfTools]++;
				else
					System.out.println("Wrong tool!");
			}
			
			// found agreed clone line at least one
			if (sum!=-1)
				// increase the number of line having this agreement
				agreedLines[sum]++;
		}
		// System.out.println("1tool, 2tools, 3tools, 4tools, ccfx, deckard, nicad, simian");
		// System.out.println(agreedLines[0] + "," + agreedLines[1] + "," + agreedLines[2] + "," + agreedLines[3] + "," 
		// 				 + agreedLines[4] + "," + agreedLines[5] + "," + agreedLines[6] + "," + agreedLines[7]);
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

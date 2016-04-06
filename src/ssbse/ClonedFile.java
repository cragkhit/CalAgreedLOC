package ssbse;

import java.util.ArrayList;

public class ClonedFile {
	private ArrayList<ClonedLine> cloneLines;
	
	public ClonedFile() {
		cloneLines = new ArrayList<ClonedLine>();
	}

	public ArrayList<ClonedLine> getCloneLines() {
		return cloneLines;
	}

	public void setCloneLines(ArrayList<ClonedLine> cloneLines) {
		this.cloneLines = cloneLines;
	}
	
}

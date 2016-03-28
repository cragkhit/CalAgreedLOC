package ssbse;

public class FragmentPair {
	private Fragment f1;
	private Fragment f2;
	private int[] reportedTools;

	public FragmentPair(Fragment f1, Fragment f2, int toolIndex) {
		this.f1 = f1;
		this.f2 = f2;
		reportedTools = new int[Utilities.NUMBER_OF_TOOLS];
		reportedTools[toolIndex] = 1;
	}
	
	public void addReportedTools(int toolIndex) {
		reportedTools[toolIndex] = 1;
	}
	
	// combine the reported tool
	public void addReportedTools(int[] tools) {
		for (int i=0; i<tools.length; i++) {
			if (tools[i]==1)
				reportedTools[i]=1;
		}
	}

	public Fragment getF1() {
		return f1;
	}

	public void setF1(Fragment f1) {
		this.f1 = f1;
	}

	public Fragment getF2() {
		return f2;
	}

	public void setF2(Fragment f2) {
		this.f2 = f2;
	}

	public boolean isInRange(FragmentPair fp) {
		Fragment frag1 = fp.getF1();
		Fragment frag2 = fp.getF2();
		if (frag1.getFile().equals(f1.getFile()) && frag1.getStartLine() >= f1.getStartLine()
				&& frag1.getEndLine() <= f1.getEndLine() && frag2.getFile().equals(f2.getFile())
				&& frag2.getStartLine() >= f2.getStartLine() && frag2.getEndLine() <= f2.getEndLine()) {
			return true;
		} // reverse the comparison
		else if (frag2.getFile().equals(f1.getFile()) && frag2.getStartLine() >= f1.getStartLine()
				&& frag2.getEndLine() <= f1.getEndLine() && frag1.getFile().equals(f2.getFile())
				&& frag1.getStartLine() >= f2.getStartLine() && frag1.getEndLine() <= f2.getEndLine()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isInRangeAndUpdate(FragmentPair fp) {
		Fragment frag1 = fp.getF1();
		Fragment frag2 = fp.getF2();

		Range thisR1 = new Range(f1.getStartLine(), f1.getEndLine());
		Range thisR2 = new Range(f2.getStartLine(), f2.getEndLine());
		
		Range r1 = new Range(frag1.getStartLine(), frag1.getEndLine());
		Range r2 = new Range(frag2.getStartLine(), frag2.getEndLine());
		
		Range overlapR1r1 = thisR1.isOverlappedWith(r1);
		Range overlapR2r2 = thisR2.isOverlappedWith(r2);
		
		Range overlapR1r2 = thisR1.isOverlappedWith(r2);
		Range overlapR2r1 = thisR2.isOverlappedWith(r1);
		
		// check if the files are the same, and the range has overlap
		if (frag1.getFile().equals(f1.getFile()) 
				&& overlapR1r1 != null && overlapR2r2 != null ) {
			f1.setStartLine(overlapR1r1.getStart());
			f1.setEndLine(overlapR1r1.getEnd());
			f2.setStartLine(overlapR2r2.getStart());
			f2.setEndLine(overlapR2r2.getEnd());
			// update reported tools
			addReportedTools(fp.reportedTools);
			return true;
		} // reverse the comparison
		else if (frag2.getFile().equals(f1.getFile()) 
				&& overlapR1r2 != null && overlapR2r1 != null) {
			f1.setStartLine(overlapR1r2.getStart());
			f1.setEndLine(overlapR1r2.getEnd());
			f2.setStartLine(overlapR2r1.getStart());
			f2.setEndLine(overlapR2r1.getEnd());
			// update reported tools
			addReportedTools(fp.reportedTools);
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() {
		String result = "[";
		for (int i=0; i<reportedTools.length; i++) {
			if (reportedTools[i]==1)
				result +=  Utilities.tools[i] + ",";
			else
				result += "-,";
			
		}
		result += "]";
		result += "|" + f1.getFile() + ": " + f1.getStartLine() + ": " + f1.getEndLine() + "|" + f2.getFile() + ": "
				+ f2.getStartLine() + ": " + f2.getEndLine();
		return result;
	}
	
	public int getAgreedLines() {
		int first = f1.getEndLine() - f1.getStartLine() + 1;
		int second = f2.getEndLine() - f2.getStartLine() + 1;
		// return the maximum one
		if (first > second)
			return first;
		else 
			return second;
	}
	
	public int[] getReportedTools() {
		return reportedTools;
	}
	
	public int getAgreedTools() {
		int agreed = 0;
		for (int i=0; i<reportedTools.length; i++) 
			agreed += reportedTools[i];
		return agreed;
	}
}

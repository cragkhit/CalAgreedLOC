package ssbse;

public class FragmentPair {
	private Fragment f1;
	private Fragment f2;

	public FragmentPair(Fragment f1, Fragment f2) {
		this.f1 = f1;
		this.f2 = f2;
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
		if (frag1.getFile().equals(f1.getFile()) && frag1.getStartLine() >= f1.getStartLine()
				&& frag1.getEndLine() <= f1.getEndLine() && frag2.getFile().equals(f2.getFile())
				&& frag2.getStartLine() >= f2.getStartLine() && frag2.getEndLine() <= f2.getEndLine()) {
			f1.setStartLine(frag1.getStartLine());
			f1.setEndLine(frag1.getEndLine());
			f2.setStartLine(frag2.getStartLine());
			f2.setEndLine(frag2.getEndLine());
			return true;
		} // reverse the comparison
		else if (frag2.getFile().equals(f1.getFile()) && frag2.getStartLine() >= f1.getStartLine()
				&& frag2.getEndLine() <= f1.getEndLine() && frag1.getFile().equals(f2.getFile())
				&& frag1.getStartLine() >= f2.getStartLine() && frag1.getEndLine() <= f2.getEndLine()) {
			f1.setStartLine(frag2.getStartLine());
			f1.setEndLine(frag2.getEndLine());
			f2.setStartLine(frag1.getStartLine());
			f2.setEndLine(frag1.getEndLine());
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() {
		return f1.getFile() + ": " + f1.getStartLine() + ": " + f1.getEndLine() + "|" + f2.getFile() + ": "
				+ f2.getStartLine() + ": " + f2.getEndLine();
	}
}

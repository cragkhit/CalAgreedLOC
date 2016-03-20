package ssbse;

import java.util.ArrayList;

public class FragmentPairList {
	private ArrayList<FragmentPair> fpList;

	public FragmentPairList() {
		super();
		fpList = new ArrayList<FragmentPair>();
	}

	public int size() {
		return fpList.size();
	}

	public FragmentPair get(int index) {
		return fpList.get(index);
	}

	public void addFragmentPair(FragmentPair fp) {
		fpList.add(fp);
	}

	public ArrayList<FragmentPair> getFragmentPairs() {
		return fpList;
	}

	public boolean isInList(FragmentPair fp) {
		// if can be found in any of the pair in the list, return
		for (FragmentPair fragPair : fpList) {
			if (fragPair.isInRange(fp)) {
				return true;
			}
		}
		return false;
	}

	public boolean isInListAndUpdate(FragmentPair fp) {
		// if can be found in any of the pair in the list, return
		for (FragmentPair fragPair : fpList) {
			if (fragPair.isInRangeAndUpdate(fp)) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		String s = "";
		for (FragmentPair fragPair : fpList) {
			s += "\t" + fragPair.toString() + "\n";
		}
		return s;
	}

	public String toString(int agreedToolLimit) {
		String s = "";
		for (FragmentPair fragPair : fpList) {
			if (fragPair.getAgreedTools() >= agreedToolLimit)
				s += "\t" + fragPair.toString() + "\n";
		}
		return s;
	}
}

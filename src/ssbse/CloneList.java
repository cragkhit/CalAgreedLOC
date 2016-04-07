package ssbse;

import java.util.ArrayList;
import java.util.HashMap;

public class CloneList {
	private static HashMap<String, CloneFile> cloneFileHash;
	
	public CloneList() {
		cloneFileHash = new HashMap<String, CloneFile>();
	}
	
	public void addCloneFile(String filename, int size) {
		CloneFile cf = new CloneFile(filename, size, Utilities.NUMBER_OF_TOOLS);
		cloneFileHash.put(filename, cf);
		// System.out.println("Adding: " + filename);
		// System.out.println("Adding: " + cf.print());
	}
	
	public void addCloneLineToList(String filename, int start, int end, int toolIndex) {
		// get the clone file by name, update the clone line
		// System.out.println("file: " + filename);
		CloneFile cf = cloneFileHash.get(filename);
		
		// System.out.println("size: " + cf.getLocSize() + ", start: " + start + ", end: " + end + ", reported by: " + toolIndex);
		for (int i=start; i<=end; i++) {
			cf.addCloneLine(i, toolIndex);
			// System.out.println("adding line: " + i);
		}
		// System.out.println(cf.print());
	}
	
	public ArrayList<CloneFile> getCloneFileList() {
		ArrayList<CloneFile> cloneList = new ArrayList<CloneFile>(cloneFileHash.values());
		return cloneList;
	}
}

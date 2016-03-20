package ssbse;

import java.util.ArrayList;

/***
 * A clone class - can contain many clones 
 * @author cragkhit
 *
 */
public class CloneClass {
	private int id;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CloneClass() {
		this.clones = new ArrayList<Clone>();
	}
	
	private ArrayList<Clone> clones;
	
	public void addClone(Clone c) {
		this.clones.add(c);
	}
	
	public int getSize() {
		return this.clones.size();
	}

	public ArrayList<Clone> getClones() {
		return clones;
	}

	public void setClones(ArrayList<Clone> clones) {
		this.clones = clones;
	}
}

package ssbse;

public class Range {
	private int start, end;

	public Range(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public Range isOverlappedWith(Range r) {
		// CASE 1:
		// |----------------------|
		// |-----------------------|
		// CASE 2:
		// |----------------------|
		// |----------------------|
		// Case 3:
		// |----------------------|
		// |------------|
		// Case 4:
		// |-----------|
		// |-----------------------|

		int finalStart = -1;
		int finalEnd = -1;
		// overlap
		if ((start <= r.end) && (end >= r.start)) {
			// check the start
			if (start < r.start)
				finalStart = r.start;
			else
				finalStart = start;
			// check the end
			if (end < r.end)
				finalEnd = end;
			else
				finalEnd = r.end;
			return new Range(finalStart, finalEnd);
		} else {
			return null;
		}
	}

	public void updateRange(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public String toString() {
		return start + "," + end;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

}

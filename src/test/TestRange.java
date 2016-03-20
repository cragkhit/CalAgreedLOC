package test;

import static org.junit.Assert.*;

import org.junit.Test;

import ssbse.Range;

public class TestRange {

	@Test
	public void testCase1() {
		// CASE 1:
		// |----------------|
		// 			|-----------------------|
		
		Range r1 = new Range(0, 10);
		Range r2 = new Range(5, 15);
		Range finalRange = r1.isOverlappedWith(r2);
		assertEquals(finalRange.getStart(), 5);
		assertEquals(finalRange.getEnd(), 10);
	}
	
	@Test
	public void testCase2() {
		// CASE 2:
		// |----------------------|
		// |----------------------|
		Range r1 = new Range(0, 10);
		Range r2 = new Range(0, 10);
		Range finalRange = r1.isOverlappedWith(r2);
		assertEquals(finalRange.getStart(), 0);
		assertEquals(finalRange.getEnd(), 10);
	}
	
	@Test
	public void testCase3() {
		// Case 3:
		// |----------------------|
		//     |------------|
		Range r1 = new Range(0, 10);
		Range r2 = new Range(2, 5);
		Range finalRange = r1.isOverlappedWith(r2);
		assertEquals(finalRange.getStart(), 2);
		assertEquals(finalRange.getEnd(), 5);
	}
	
	@Test
	public void testCase4() {
		// Case 4:
		//    |-----------|
		// |-----------------------|
		Range r1 = new Range(3, 8);
		Range r2 = new Range(0, 15);
		Range finalRange = r1.isOverlappedWith(r2);
		assertEquals(finalRange.getStart(), 3);
		assertEquals(finalRange.getEnd(), 8);
	}
}

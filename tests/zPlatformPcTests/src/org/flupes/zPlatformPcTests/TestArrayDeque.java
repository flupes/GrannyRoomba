package org.flupes.zPlatformPcTests;

import java.util.ArrayDeque;
import java.util.Queue;

public class TestArrayDeque {

	public static void main(String[] args) {

		ArrayDeque<String> queue = new ArrayDeque<String>(100);
		System.out.println("size after creation: " + queue.size());
		
		queue.push("toto");
		queue.push("tata");
		queue.push("tutu");
		System.out.println("size after insertions: " + queue.size());
		
		while ( !queue.isEmpty() ) {
			System.out.println(queue.pop());
		}
	
		Queue<Integer> q = new ArrayDeque<Integer>(10);
		q.add(1);
		q.add(2);
		q.add(3);
		while ( !q.isEmpty() ) {
			System.out.println(q.remove());
		}
		
	}

}
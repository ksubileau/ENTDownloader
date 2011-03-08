/*
 *  Stack.java
 *      
 *  Copyright 2010 Kévin Subileau. 
 *
 *	This file is part of ENTDownloader.
 *    
 *  ENTDownloader is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  ENTDownloader is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; If not, see <http://www.gnu.org/licenses/>.
 */
package entDownloader.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * The <code>Stack</code> class represents a last-in-first-out (LIFO) stack of
 * objects.<br>
 * <br>
 * The usual <code>push</code> and <code>pop</code> operations are provided, as
 * well as a method to <code>peek</code> at the top item on the stack, a method
 * to test for whether the stack is <code>empty</code>.<br>
 * <br>
 * This implementation uses a LinkedList instance to store the elements
 * of the stack, where the last element of the LinkedList is the top of
 * the <code>Stack</code>.
 * 
 * @param T
 *            the type of elements held in this Stack
 */
class Stack<T> extends java.util.LinkedList<T> implements Iterable<T> {
	private static final long serialVersionUID = 8186550799187818309L;

	/**
	 * Creates an empty Stack.
	 */
	public Stack() {
		super();
	}

	/**
	 * Creates a Stack containing the elements of the specified collection, in
	 * the order they are returned by the collection's iterator.
	 * 
	 * @param c
	 *            the collection whose elements are to be placed into this Stack
	 */
	public Stack(Collection<T> c) {
		super(c);
	}

	/**
	 * Returns true if this stack contains no elements.<br>
	 * <br>
	 * This implementation returns size() == 0.
	 * 
	 * @return true if this stack contains no elements.
	 */
	@Override
	public boolean isEmpty() {
		return (this.size() == 0);
	}

	/**
	 * Retrieves, but does not remove, the head (first element) of this stack.
	 * 
	 * @return the head of this stack, or null if this stack is empty.
	 */
	@Override
	public T peek() {
		return (super.peek());
	}

	/**
	 * Pops an element from this stack. In other words, removes and returns the
	 * first element of this stack.<br>
	 * <br>
	 * This method is equivalent to java.util.LinkedList.removeFirst().
	 * 
	 * @return the element at the front of this stack (which is the top of the
	 *         stack represented by this stack), or null if this stack is empty.
	 */
	@Override
	public T pop() {
		if (this.isEmpty())
			return null;
		return (this.removeFirst());
	}

	/**
	 * Pushes an element onto this list. In other words, inserts the element at
	 * the front of this stack.<br>
	 * <br>
	 * This method is equivalent to java.util.LinkedList.addFirst(T).
	 * 
	 * @param item
	 *            the element to push
	 */
	@Override
	public void push(T item) {
		this.addFirst(item);
	}

	/**
	 * Returns an iterator over the elements in this stack (from top of the
	 * stack to the bottom).<br>
	 * <br>
	 * This implementation merely returns a LinkedList iterator over the stack.
	 * 
	 * @return an iterator over the elements in this stack (from top of the
	 *         stack to the bottom)
	 * @see java.util.AbstractSequentialList#iterator()
	 */
	@Override
	public Iterator<T> iterator() {
		return super.iterator();
	}

	/**
	 * Returns an iterator over the elements in this stack in reverse sequential
	 * order.<br>
	 * <br>
	 * The elements will be returned in order from last (bottom) to first
	 * (last).
	 * 
	 * @return an iterator over the elements in this stack in reverse sequence
	 * @see java.util.LinkedList#descendingIterator()
	 */
	@Override
	public Iterator<T> descendingIterator() {
		return super.descendingIterator();
	}
}

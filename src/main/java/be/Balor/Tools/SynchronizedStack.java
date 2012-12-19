/************************************************************************
 * This file is part of AdminCmd.									
 *																		
 * AdminCmd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by	
 * the Free Software Foundation, either version 3 of the License, or		
 * (at your option) any later version.									
 *																		
 * AdminCmd is distributed in the hope that it will be useful,	
 * but WITHOUT ANY WARRANTY; without even the implied warranty of		
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the			
 * GNU General Public License for more details.							
 *																		
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd.  If not, see <http://www.gnu.org/licenses/>.
 ************************************************************************/
package be.Balor.Tools;

import java.util.Collection;
import java.util.Stack;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class SynchronizedStack<E> extends Stack<E> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6173963896174648554L;
	private final Lock lock = new ReentrantLock(true);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Stack#pop()
	 */
	@Override
	public synchronized E pop() {
		lock.lock();
		try {
			return super.pop();
		} finally {
			lock.unlock();
		}

	}

	@Override
	public E push(final E item) {
		lock.lock();
		try {
			return super.push(item);
		} finally {
			lock.unlock();
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Stack#peek()
	 */
	@Override
	public synchronized E peek() {
		lock.lock();
		try {
			return super.peek();
		} finally {
			lock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Stack#search(java.lang.Object)
	 */
	@Override
	public synchronized int search(final Object o) {
		lock.lock();
		try {
			return super.search(o);
		} finally {
			lock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Stack#empty()
	 */
	@Override
	public boolean empty() {
		lock.lock();
		try {
			return super.empty();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public synchronized boolean add(final E e) {
		lock.lock();
		try {
			return super.add(e);
		} finally {
			lock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Vector#addAll(java.util.Collection)
	 */
	@Override
	public synchronized boolean addAll(final Collection<? extends E> c) {
		lock.lock();
		try {
			return super.addAll(c);
		} finally {
			lock.unlock();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Vector#addAll(int, java.util.Collection)
	 */
	@Override
	public synchronized boolean addAll(final int index,
			final Collection<? extends E> c) {
		lock.lock();
		try {
			return super.addAll(index, c);
		} finally {
			lock.unlock();
		}
	}
}

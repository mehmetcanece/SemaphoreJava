package sync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Test {
	public static void main(String[] args) {
		ExecutorService executorService = Executors.newCachedThreadPool();
		ReadWriteLock RW = new ReadWriteLock();

		executorService.execute(new Writer(RW));
		executorService.execute(new Writer(RW));
		executorService.execute(new Writer(RW));
		executorService.execute(new Writer(RW));

		executorService.execute(new Reader(RW));
		executorService.execute(new Reader(RW));
		executorService.execute(new Reader(RW));
		executorService.execute(new Reader(RW));

	}
}

class ReadWriteLock {
	private Semaphore S = new Semaphore(1);

	public void readLock() {
		try {
			S.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeLock() {
		try {
			S.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void readUnLock() {
		S.release();
	}

	public void writeUnLock() {
		S.release();

	}
}

class Writer implements Runnable {
	private ReadWriteLock RW_lock;

	public Writer(ReadWriteLock rw) {
		RW_lock = rw;
	}

	public void run() {
		while (true) {
			RW_lock.writeLock();

			RW_lock.writeUnLock();

		}
	}

}

class Reader implements Runnable {
	private ReadWriteLock RW_lock;

	public Reader(ReadWriteLock rw) {
		RW_lock = rw;
	}

	public void run() {
		while (true) {
			RW_lock.readLock();

			RW_lock.readUnLock();

		}
	}

}
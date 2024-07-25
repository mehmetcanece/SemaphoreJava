package sync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; // In this part, i imported the ExecutorService and Executors to create a thread pool.
import java.util.concurrent.Semaphore;

public class Test {
  public static void main(String[] args) {
    Semaphore s = new Semaphore(3); // In this part, i created a semaphore with 3 permits, and i assigned it to the
                                    // s variable.
    ExecutorService executorService = Executors.newCachedThreadPool();
    ReadWriteLock RW = new ReadWriteLock(); // In this part, i created a ReadWriteLock object, and i assigned it to the
                                            // RW variable.

    executorService.execute(new Writer(RW));
    executorService.execute(new Writer(RW));
    executorService.execute(new Writer(RW)); // In this part, i created 4 writer threads and 4 reader threads.
    executorService.execute(new Writer(RW));

    executorService.execute(new Reader(RW));
    executorService.execute(new Reader(RW));
    executorService.execute(new Reader(RW));
    executorService.execute(new Reader(RW));

    ReadWriteLock w = new ReadWriteLock(); // In this part, i created a new ReadWriteLock object, and i assigned it to
                                           // the w variable.

  }
}

class ReadWriteLock extends Thread {
  private Semaphore RL = new Semaphore(1);
  private Semaphore WL = new Semaphore(1); // In this part, i created 2 semaphores with 1 permit each, and i assigned
                                           // them to the RL and WL variables.
  private int readCount = 0; // In this part, i created a readCount variable to count the number of readers.

  public void RL() {
    try {
      RL.acquire(); // In this part, i used the acquire() method to acquire the semaphore to make
                    // sure that only one reader can read at a time.
    } catch (InterruptedException e) { // In this part, i used the try-catch to catch the InterruptedException to
                                       // handle the exception.
    }

    ++readCount; // i incremented the readCount variable.

    if (readCount == 1) { // i used the if statement to check if the readCount is equal to 1 or not.
      try {
        WL.acquire(); // In this part, i used the acquire() method to acquire the semaphore to make
                      // sure that no writer can write while a reader is reading.
      } catch (InterruptedException e) { // In this part, i used the try-catch to catch the InterruptedException to
                                         // handle the exception.
      }
    }

    System.out.println("Thread " + Thread.currentThread().getName() + " is reading the document now."); // In this part,
                                                                                                        // i printed the
                                                                                                        // thread name
                                                                                                        // to show that
                                                                                                        // the thread is
                                                                                                        // reading.
    RL.release(); // In this part, i used the release() method to release the semaphore to make
                  // sure that other readers can read.
  }

  public void writeLock() {
    try {
      WL.acquire(); // In this part, i used the acquire() method to acquire the semaphore to make
                    // sure that only one writer can write at a time.
    } catch (InterruptedException e) { // In this part, i used the try-catch to catch the InterruptedException to
                                       // handle the exception.
    }
    System.out.println("Thread " + Thread.currentThread().getName() + " is writing on the document now."); // In this
                                                                                                           // part, i
                                                                                                           // printed
                                                                                                           // the thread
                                                                                                           // name to
                                                                                                           // show that
                                                                                                           // the thread
                                                                                                           // is
                                                                                                           // writing.

  }

  public void readUnLock() throws InterruptedException { // i used the throws InterruptedException to throw the
                                                         // exception to the caller.

    try {
      RL.acquire(); // In this part, i used the acquire() method to acquire the semaphore to make
                    // sure that only one reader can read at a time.
    } catch (InterruptedException e) { // In this part, i used the try-catch to catch the InterruptedException to
                                       // handle the exception.
    }

    --readCount; // i decremented the readCount variable to show that the reader has finished
                 // reading.

    if (readCount == 0) { // i used the if statement to check if the readCount is equal to 0 or not.
      WL.release(); // i used the release() method to release the semaphore to make sure that other
                    // writers can write.
    }

    System.out.println("Thread " + Thread.currentThread().getName() + " has done the reading."); // In this part, i
                                                                                                 // printed the thread
                                                                                                 // name to show that
                                                                                                 // the thread has
                                                                                                 // finished reading.

    RL.release(); // i used the release() method to release the semaphore to make sure that other
                  // readers can read.

  }

  public void writeUnLock() {
    System.out.println("Thread " + Thread.currentThread().getName() + " has done the writing."); // In this part, i
                                                                                                 // printed the thread
                                                                                                 // name to show that
                                                                                                 // the thread has
                                                                                                 // finished writing.
    WL.release(); // i used the release() method to release the semaphore to make sure that other
                  // writers can write.
  }
}

class Writer implements Runnable {
  private ReadWriteLock RW_lock;

  public Writer(ReadWriteLock rw) {
    RW_lock = rw; // i assigned the ReadWriteLock object to the RW_lock variable to make sure that
                  // only one writer can write at a time.
  }

  public void run() {
    while (true) {
      WaitFor.nap(); // i called the nap() method to make the thread sleep.
      RW_lock.writeLock(); // i called the writeLock() method to make sure that only one writer can write
                           // at a time.
      WaitFor.nap();
      RW_lock.writeUnLock(); // i called the writeUnLock() method to make sure that other writers can write.

    }
  }

}

class Reader implements Runnable {
  private ReadWriteLock RW_lock;

  public Reader(ReadWriteLock rw) {
    RW_lock = rw; // i assigned the ReadWriteLock object to the RW_lock variable to make sure that
                  // only one reader can read at a time.
  }

  public void run() {
    while (true) {
      WaitFor.nap();
      RW_lock.RL(); // i called the RL() method to make sure that only one reader can read at a
                    // time.

      try {
        WaitFor.nap(); // i called the nap() method to make the thread sleep.
        RW_lock.readUnLock(); // i called the readUnLock() method to make sure that other readers can read.
      } catch (InterruptedException e) { // In this part, i used the try-catch to catch the InterruptedException to
                                         // handle the exception.
        e.printStackTrace(); // i used the printStackTrace() method to print the stack trace of the
                             // exception.
      }

    }
  }

}

class WaitFor { // In this part, i created a WaitFor class to create a nap() method to make the
                // thread sleep.
  public static void nap() {
    nap(NAP_TIME); // i called the nap() method to make the thread sleep.
  }

  public static void nap(int duration) {
    int sleeptime = (int) (NAP_TIME * Math.random()); // i created a sleeptime variable to make the thread sleep.
    try {
      Thread.sleep(sleeptime * 500); // i used the sleep() method to make the thread sleep, it takes the sleeptime
                                     // variable as a parameter.
    } catch (InterruptedException e) {
    }
  }

  private static final int NAP_TIME = 10; // i created a NAP_TIME variable and assigned it to 10.
}
# 一、经典卖票

## 1、synchronize

```
//定义资源类
class Ticket{
    private static int number = 500;

    public synchronized void sale(){
        if(number > 0){
            System.out.println(Thread.currentThread().getName() + "购买了第"+ number-- +"张票，还剩"+number+"张票！");
        }
    }
}

public class SaleTicket {

    public static void main(String[] args) {
        Ticket ticket = new Ticket();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 500; i++) {
                    ticket.sale();
                }
            }
        }, "小明").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 500; i++) {
                    ticket.sale();
                }
            }
        }, "小李").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 500; i++) {
                    ticket.sale();
                }
            }
        }, "小刚").start();

    }
}

```

## 2、Lock

```
class Ticket{
    private static int number = 500;
    private Lock lock = new ReentrantLock();

    public  void sale(){
        lock.lock();

        try{
            if(number > 0){
                System.out.println(Thread.currentThread().getName() + "购买了第"+ number-- +"张票，还剩"+number+"张票！");
            }
        }catch(Exception e){
           e.printStackTrace();
        }finally{
            lock.unlock();
        }


    }
}
```

## 3、wait和sleep的区别

wait放弃手里的锁睡眠。

sleep一直占着锁睡眠。

## 4、线程的状态

```
 /**
         * Thread state for a thread which has not yet started.
         */
         新建状态、尚未开始运行
        NEW,

        /**
         * Thread state for a runnable thread.  A thread in the runnable
         * state is executing in the Java virtual machine but it may
         * be waiting for other resources from the operating system
         * such as processor.
         */
         在虚拟机里执行了，但是可能会等待操作系统里其他占用资源的程序
        RUNNABLE,

        /**
         * Thread state for a thread blocked waiting for a monitor lock.
         * A thread in the blocked state is waiting for a monitor lock
         * to enter a synchronized block/method or
         * reenter a synchronized block/method after calling
         * {@link Object#wait() Object.wait}.
         */
         阻塞
        BLOCKED,

        /**
         * Thread state for a waiting thread.
         * A thread is in the waiting state due to calling one of the
         * following methods:
         * <ul>
         *   <li>{@link Object#wait() Object.wait} with no timeout</li>
         *   <li>{@link #join() Thread.join} with no timeout</li>
         *   <li>{@link LockSupport#park() LockSupport.park}</li>
         * </ul>
         *
         * <p>A thread in the waiting state is waiting for another thread to
         * perform a particular action.
         *
         * For example, a thread that has called <tt>Object.wait()</tt>
         * on an object is waiting for another thread to call
         * <tt>Object.notify()</tt> or <tt>Object.notifyAll()</tt> on
         * that object. A thread that has called <tt>Thread.join()</tt>
         * is waiting for a specified thread to terminate.
         */
         等待状态，没有时间限制的等待
        WAITING,

        /**
         * Thread state for a waiting thread with a specified waiting time.
         * A thread is in the timed waiting state due to calling one of
         * the following methods with a specified positive waiting time:
         * <ul>
         *   <li>{@link #sleep Thread.sleep}</li>
         *   <li>{@link Object#wait(long) Object.wait} with timeout</li>
         *   <li>{@link #join(long) Thread.join} with timeout</li>
         *   <li>{@link LockSupport#parkNanos LockSupport.parkNanos}</li>
         *   <li>{@link LockSupport#parkUntil LockSupport.parkUntil}</li>
         * </ul>
         */
         有时间的等待，
        TIMED_WAITING,

        /**
         * Thread state for a terminated thread.
         * The thread has completed execution.
         */
        TERMINATED;
```

## 5、lambda改造

```
public class SaleTicket {

    public static void main(String[] args) {
        Ticket ticket = new Ticket();

        new Thread(()-> {for (int i = 0; i < 500; i++) ticket.sale()}, "小明").start();
        new Thread(()-> {for (int i = 0; i < 500; i++) ticket.sale()}, "小李").start();
        new Thread(()-> {for (int i = 0; i < 500; i++) ticket.sale()}, "小刚").start();

    }
}
```

6、生产者消费者

# 二、生产者消费者

## 1、题目

现在两个线程，可以操作初始值为零的一个变量，实现一个线程对该变量+1，一个线程对该变量-1，交替实现，来10轮，变量初始值为零。

生产者如果发现资源为0，则+1，并且通知消费者。

## 2、synchronize

```
class AirConditioner{
    private int number = 0;

    public synchronized void increment() throws InterruptedException {
        if(number != 0){
            wait();
        }
        number++;
        System.out.println(Thread.currentThread().getName()+"\t"+number);
        notifyAll();
    }

    public synchronized void decrement() throws InterruptedException {
        if(number == 0){
            wait();
        }
        number--;
        System.out.println(Thread.currentThread().getName()+"\t"+number);
        notifyAll();
    }
}

public class ThreadWaitNotifyDemo {

    public static void main(String[] args) {
        AirConditioner airConditioner = new AirConditioner();
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                try {
                    airConditioner.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"A").start();

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                try {
                    airConditioner.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "B").start();
    }
}
```

## 3、虚假唤醒

当线程比较多，生产者和消费者不止一个的时候，会产生其他的问题。

```
 new Thread(()->{
            for (int i = 0; i < 10; i++) {
                try {
                    airConditioner.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"A").start();

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                try {
                    airConditioner.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "B").start();


        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                try {
                    airConditioner.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"C").start();

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                try {
                    airConditioner.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "D").start();
```

```
A	1
B	0
C	1
A	2
C	3
B	2
B	1
B	0
C	1
```

分析：

是因为

```
  if(number != 0){
            wait();
  }
```

等待完成后，可能条件就不满足了，没有进行再次判断，所以要把if改成while

```
while (number != 0){
    wait();
}
```

## 4、lock改造

```
class AirCondition{
    private int number = 0;
    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();


    public void increase(){

        lock.lock();

        try{
            while (number != 0){
                condition.await();
            }
            
            number++;
            System.out.println(Thread.currentThread().getName()+" : "+ number);
            condition.signalAll();

        }catch(Exception e){
           e.printStackTrace();
        }finally{
            lock.unlock();
        }
    }


    public void decrease(){
        lock.lock();

        try{
            while (number == 0){
                condition.await();
            }

            number--;
            System.out.println(Thread.currentThread().getName()+" : "+ number);
            condition.signalAll();
        }catch(Exception e){
           e.printStackTrace();
        }finally{
            lock.unlock();
        }

    }
}
```

## 5、多线程顺序执行

多线程顺序执行，例如A线程 打印五次后，b线程打印10次，c线程答应15次。

```
class ShareResource{
    private int number=1;//1 A; 2 B; 3 C
    Lock lock = new ReentrantLock();
    Condition condition1 = lock.newCondition();
    Condition condition2 = lock.newCondition();
    Condition condition3 = lock.newCondition();

    public void print5(){
        lock.lock();

        try{
            if(number != 1){
                condition1.await();
            }

            for (int i = 0; i < 5; i++) {
                System.out.println(i + Thread.currentThread().getName()+" : "+ number);
            }

            //唤醒下一个
            number = 2;
            condition2.signal();
        }catch(Exception e){
           e.printStackTrace();
        }finally{
            lock.unlock();
        }
    }


    public void print10(){
        lock.lock();

        try{
            if(number != 2){
                condition2.await();
            }

            for (int i = 0; i < 10; i++) {
                System.out.println(i + Thread.currentThread().getName()+" : "+ number);
            }

            number = 3;
            condition3.signal();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            lock.unlock();
        }
    }

    public void print15(){
        lock.lock();

        try{
            if(number != 3){
                condition3.await();
            }

            for (int i = 0; i < 15; i++) {
                System.out.println(i + Thread.currentThread().getName()+" : "+ number);
            }

            number = 1;
            condition1.signal();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            lock.unlock();
        }
    }
}

public class ThreadOrderAccess {

    public static void main(String[] args) {

        ShareResource shareResource = new ShareResource();
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                shareResource.print5();
            }
        }, "A").start();
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                shareResource.print10();
            }
        }, "B").start();
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                shareResource.print15();
            }
        }, "C").start();
    }
}

```

# 三、锁对象

## 1、非静态synchronize

即使A线程的方法停了三秒钟，他依然可以先运行。

因为直接加在方法上的synchronize，它的锁就是其对象本身（phone）。

```
class Phone{
    public synchronized void sendEmail(){

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println(Thread.currentThread().getName()+" 发送了一封邮件！");
    }

    public synchronized void sendMsg(){
        System.out.println(Thread.currentThread().getName()+" 发送了一条短信！");
    }
}

public class LockObject {
    public static void main(String[] args) {
        Phone phone = new Phone();

        new Thread(new Runnable() {
            @Override
            public void run() {

                phone.sendEmail();
            }
        }, "A").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                phone.sendMsg();
            }
        }, "B").start();


    }
}

```

结果

```
B 发送了一条短信！
A 发送了一封邮件！
```

如果里面有个新的方法，没有加synchronize

```
class Phone{
    public synchronized void sendEmail(){

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName()+" 发送了一封邮件！");
    }

    public synchronized void sendMsg(){
        System.out.println(Thread.currentThread().getName()+" 发送了一条短信！");
    }
    
    public void hello(){
        System.out.println("hello-----------------");
    }
}
```

运行结果就显而易见了

```
hello-----------------
A 发送了一封邮件！
```

为了验证上述结论，我们可以用不同的Phone对象

```

public class LockObject {
    public static void main(String[] args) {
        Phone phone = new Phone();
        Phone phone1 = new Phone();

        new Thread(new Runnable() {
            @Override
            public void run() {

                phone.sendEmail();
            }
        }, "A").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                phone1.sendMsg();
            }
        }, "B").start();


    }
}
```

结果：

```
B 发送了一条短信！
A 发送了一封邮件！
```



## 2、静态synchronize

接着上述的实验，如果将方法变为静态。

```
class Phone{
    public static synchronized void sendEmail(){

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName()+" 发送了一封邮件！");
    }

    public static synchronized void sendMsg(){
        System.out.println(Thread.currentThread().getName()+" 发送了一条短信！");
    }

}

public class LockObject {
    public static void main(String[] args) {
        Phone phone = new Phone();
        Phone phone1 = new Phone();

        new Thread(new Runnable() {
            @Override
            public void run() {

                phone.sendEmail();
            }
        }, "A").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                phone1.sendMsg();
            }
        }, "B").start();


    }
}

```

其结果又变为：

```
A 发送了一封邮件！
B 发送了一条短信！
```

原因是静态方法的synchronize锁对象就是其字节码对象。

# 四、集合类的不安全

## 1、ArrayList

当多个线程进行添加或者读取的时候，结果是丰富多彩的。

```
public class NotSafeDemo {
    public static void main(String[] args) {
        List<String>list = new ArrayList<>();

        //启动三个线程
        for (int i = 0; i < 3; i++) {
            new Thread(()->{
                list.add(UUID.randomUUID().toString().substring(0,8));
                System.out.println(list);
            },String.valueOf(i)).start();
        }
    }
}
```

### 解决方案

1、换成Vector

但是效率很低，不要用

```
public class NotSafeDemo {
    public static void main(String[] args) {
        List<String>list = new Vector<>();

        //启动三个线程
        for (int i = 0; i < 3; i++) {
            new Thread(()->{
                list.add(UUID.randomUUID().toString().substring(0,8));
                System.out.println(list);
            },String.valueOf(i)).start();
        }
    }
}
```

2、Collection工具包

```
public class NotSafeDemo {
    public static void main(String[] args) {
        List<String>list = Collections.synchronizedList(new ArrayList<>());

        //启动三个线程
        for (int i = 0; i < 3; i++) {
            new Thread(()->{
                list.add(UUID.randomUUID().toString().substring(0,8));
                System.out.println(list);
            },String.valueOf(i)).start();
        }
    }
}

```

3、juc包下面的类

CopyOnWriteArrayList类 读写分离，读操作和写操作的对象不是同一个，维护着两个数组。

```
public class NotSafeDemo {
    public static void main(String[] args) {
        List<String>list = new CopyOnWriteArrayList<>();

        //启动三个线程
        for (int i = 0; i < 3; i++) {
            new Thread(()->{
                list.add(UUID.randomUUID().toString().substring(0,8));
                System.out.println(list);
            },String.valueOf(i)).start();
        }
    }
}

```

CopyOnWriteArrayList中的add方法如下所示，可以看出维护着两个数组对象elements，newElements

```
    public boolean add(E e) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            Object[] newElements = Arrays.copyOf(elements, len + 1);
            newElements[len] = e;
            setArray(newElements);
            return true;
        } finally {
            lock.unlock();
        }
    }
```

## 2、HashSet

同上，也是不安全的

### 解决方案

```
Set<String>set = new CopyOnWriteArraySet<>();
```


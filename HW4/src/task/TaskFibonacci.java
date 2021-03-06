package task;

import space.SpaceImpl;
import system.CilkThread;
import system.Closure;
import system.Continuation;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Optional;

/**
 * Created by hallvard on 4/25/15.
 */
public class TaskFibonacci extends CilkThread {

    private final long START_TIME = System.nanoTime();

    public TaskFibonacci(Closure closure) {
        super(closure);
    }

    /**
     * Decomposes the problem to subtasks that are spawned in space
     * @param c The Continuation of this task
     */
    @Override
    public void decompose(Continuation k) {

        int n = (int) k.argument;
        if(n<2) {
            k.setReturnVal(n);
            sendArgument(k);
        } else {
            String id = spawnNext(new TaskFibonacci(null), k, null, null);
            Continuation c1 = new Continuation(id, 1, n-1)
                        ,c2 = new Continuation(id, 2, n-2);
            spawn(new TaskFibonacci(null), c1);
            spawn(new TaskFibonacci(null), c2);
        }
    }

    /**
     * Compose method.
     */
    @Override
    public void compose() {
        sum((Continuation)closure.getArgument(0), (int)closure.getArgument(1), (int)closure.getArgument(2));
    }

    /**
     * Problem specific implementation of the compose method.
     * <p>The sum method simply sums together the two arguments and requests the thread
     * to send the argument.</p>
     * @param cont  Current Continuation
     * @param arg0  Result-value of the first subtask
     * @param arg1  Result-value of the second (and final) subtask
     */
    private void sum(Continuation cont, int arg0, int arg1) {
        cont.setReturnVal(arg0 + arg1);
        sendArgument(cont);
    }
}

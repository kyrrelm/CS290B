package api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import space.SpaceImpl;
import system.Closure;
import system.Computer;
import system.Continuation;

public interface Space extends Remote
{

    public static int PORT = 1099;
    public static String SERVICE_NAME = "Space";
    public static boolean MULTICORE = true;
    public static int PREFETCH_LIMIT = 5;

    void put(Task task) throws RemoteException, InterruptedException;

    void put(Closure closure)throws RemoteException;

    void putClosureInReady(Closure closure) throws RemoteException;

    Result take() throws RemoteException, InterruptedException;

    void exit() throws RemoteException;

    void register(Computer computer) throws RemoteException;


    void putResult(Result r) throws RemoteException;

    Closure takeReadyClosure() throws  RemoteException;

    void receiveArgument(Continuation k) throws RemoteException;

    void closureDone(String id) throws RemoteException;

    void putAll(Collection<Closure> closures) throws RemoteException;
}
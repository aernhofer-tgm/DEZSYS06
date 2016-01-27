package example.rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class HelloImpl extends UnicastRemoteObject implements Hello
{
    public HelloImpl() throws RemoteException { super(); }

    public String sayHello() { return "Hello world!"; }

    public static void main(String args[])
    {
        try
        {
            LocateRegistry.createRegistry(1099);
            //System.setProperty( "java.rmi.server.hostname", "192.168.1.106" ) ;
            HelloImpl obj = new HelloImpl();
            // Bind this object instance to the name "HelloServer"
            Naming.rebind("HelloServer", obj);
            System.out.print("RMI Server läuft");
        }
        catch (Exception e)
        {
            System.out.println("HelloImpl err: " + e.getMessage());
            e.printStackTrace();
        }
    }
} //rmirgistry in production/dezss08
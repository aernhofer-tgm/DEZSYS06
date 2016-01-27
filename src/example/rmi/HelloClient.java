package example.rmi;

import java.rmi.Naming;

public class HelloClient
{
    public static void main(String arg[])
    {
        String message = "blank";

        // I download server's stubs ==> must set a SecurityManager
        //System.setSecurityManager(new RMISecurityManager());

        //System.setProperty("java.rmi.server.hostname","192.168.48.1");

        try
        {

            System.out.println("Starten des RMI Clients");

            Hello obj = (Hello) Naming.lookup( "//" +
                    "localhost" +
                    "/HelloServer");         //objectname in registry
            System.out.println(obj.sayHello());
        }
        catch (Exception e)
        {
            System.out.println("HelloClient exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
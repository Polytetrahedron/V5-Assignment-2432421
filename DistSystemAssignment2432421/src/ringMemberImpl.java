import java.rmi.*;
import java.net.* ;
 
public class ringMemberImpl extends java.rmi.server.UnicastRemoteObject implements ringMember 
{
    private String	next_id;
    private String  next_host;
    private String	this_id;
    private String  this_host;
    private criticalSection	c;

    /**
     *
     * @param t_node
     * @param t_id
     * @param n_node
     * @param n_id
     * @throws RemoteException
     */
   public ringMemberImpl(String  t_node, String  t_id, String  n_node, String  n_id) throws RemoteException {
      this_host = t_node ;
      this_id = t_id ;
      next_host = n_node ;
      next_id = n_id ;
   }

    //******************************************************************************************

    /**
     * This method is implemented from the rindMember interface and is used in RMI calls to pass the
     * tokens from node to node.
     *
     * @param token The token object
     * @throws RemoteException
     */
   public synchronized void takeToken(Token token) throws RemoteException
   {
       if(!token.checkKillToken())
       {
           c = new criticalSection(this_host, this_id, next_host, next_id, token, token.getCustomFileName());

           c.start();
       }
       else
       {
           c = new criticalSection(token, next_host, next_id, this_id);

           c.start();
       }
   }

    //******************************************************************************************

    /**
     *
     * @param argv
     */
   public static void main(String argv[])
   {
    System.setSecurityManager(new SecurityManager());

    if ((argv.length < 3) || (argv.length > 3))
    {
        System.out.println("Usage: [this ID][next host][next ID]");
        System.out.println("Only " + argv.length + " parameters entered");
        System.exit (1) ;
    }

    String this_id = argv[0];
    String next_host = argv[1];
    String next_id = argv[2];

    try
    {
        InetAddress thisHostAddress = InetAddress.getLocalHost();
        String hostName = thisHostAddress.getHostName();
        System.out.println("This node is: " + hostName);

        ringMember ringHost = new ringMemberImpl(hostName, this_id, next_host, next_id);
        Naming.rebind("//"+hostName+"/"+this_id, ringHost);
        System.out.println(hostName + " bound successfully to registry");

    }catch(Exception e)
    {
        System.out.println("Whoops! something went wrong!");
        e.printStackTrace();

    }
   }
 }
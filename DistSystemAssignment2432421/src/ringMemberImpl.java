import java.rmi.*;
import java.net.* ;

//*******************************************************************
//  Class: ringMemberImpl
//  Desc: This class instantiates a new ringMemberImpl object with
//        cmd arguments
//  @author 2432421
//*******************************************************************
public class ringMemberImpl extends java.rmi.server.UnicastRemoteObject implements ringMember 
{
    //class level variables
    private String	next_id;
    private String  next_host;
    private String	this_id;
    private String  this_host;
    private criticalSection	c;

    /**
     * This is the constructor the instantiates the ringMemberImpl object. This creates
     * an RMI node and many of these can create a ring network.
     *
     * @param t_node The current host
     * @param t_id The ID of the current host
     * @param n_node The nest host
     * @param n_id The ID of the next host
     * @throws RemoteException
     */
   public ringMemberImpl(String  t_node, String  t_id, String  n_node, String  n_id) throws RemoteException
   {
      //Assigning parameters to class level variables
      this_host = t_node ;
      this_id = t_id ;
      next_host = n_node ;
      next_id = n_id ;

   }//end of constructor

    //******************************************************************************************

    /**
     * This method is implemented from the rindMember interface and is used in RMI calls to pass the
     * tokens from node to node.
     *
     * @param token The token object to be passed
     * @throws RemoteException
     */
   public synchronized void takeToken(Token token) throws RemoteException
   {
       if(!token.checkKillToken()) //if the token passed is not a kill token
       {
           //start standard critical section
           c = new criticalSection(this_host, this_id, next_host, next_id, token);

           c.start(); //start thread
       }
       else
       {
           //start cleanup critical section
           c = new criticalSection(token, next_host, next_id, this_id);

           c.start(); //start thread
       }
   }//end of method takeToken

    //******************************************************************************************

    /**
     * This is the main method for this ringMemberImpl class
     * This takes one array that contains arguments passed to it from command line
     *
     * @param argv The arguments passed from command line
     */
   public static void main(String argv[])
   {
    //get arguments from command line and perform basic validation
    if ((argv.length < 3) || (argv.length > 3))
    {
        System.out.println("Usage: [this ID][next host][next ID]");
        System.out.println("Only " + argv.length + " parameters entered");
        System.exit (1) ;
    }

    //assign arguments to variables
    String this_id = argv[0];
    String next_host = argv[1];
    String next_id = argv[2];

    createNode(this_id, next_host, next_id);
   }//end of method main


    //******************************************************************************************

    /**
     * This method was created for the sake of neatness keeping like data together
     * This creates a node using the cmd arguments passed to it
     *
     * @param this_id The ID of the current host
     * @param next_host The next host in the chain
     * @param next_id The ID of the next host
     */
    private static void createNode(String this_id, String next_host, String next_id)
    {

        try
        {
            //attempting to get HostName from system
            InetAddress thisHostAddress = InetAddress.getLocalHost();
            String hostName = thisHostAddress.getHostName(); //storing the hostname
            System.out.println("This node is: " + hostName); //user diagnostic output

            //creating a new remote object
            ringMember ringHost = new ringMemberImpl(hostName, this_id, next_host, next_id);

            //Binding the remote object to RMI registry
            Naming.rebind("//" + hostName + "/" + this_id, ringHost);

            //User diagnostic printout
            System.out.println(hostName + " bound successfully to registry");

        }catch(Exception e)
        {
            System.out.println("Whoops! something went wrong!");
            e.printStackTrace();

        }
    }//end of method createNode

 }//end of class ringMemberImpl
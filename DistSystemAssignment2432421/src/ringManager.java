import java.rmi.*;
import java.io.*;

public class ringManager
{
    /**
     * The constructor for the ringManager object this creates a reference to a remote node
     * and creates a new Token object. It then attempts to pass the token to the node. This
     * will cause the nodes to begin passing the token between them. The manager shuts down
     * after this operation has completed.
     *
     * @param ring_node_host The host at injection point
     * @param ring_node_id The ID of the host at injection point
     * @param customFilename The name of the shared resource
     * @param TTL The time until token expiry (Time To Live)
     * @param extraTImeNode The ID of the node that is to get more processing time
     * @param skipHost The ID of the node designated to skip every second turn
     */
   public ringManager(String ring_node_host, String ring_node_id, String customFilename, int TTL, String extraTImeNode,
                      String skipHost)
   {
       System.setSecurityManager(new SecurityManager()); // Setting the security manager

       clearFile(customFilename); //Clearing/creating a file for use as a shared resource

       //token instantiated with cmd arguments
       Token token = new Token(TTL, extraTImeNode, ring_node_id, skipHost, customFilename);

       sendToInjectionPoint(token, ring_node_host, ring_node_id); //sending the token to the network

       System.out.println("Connecting to Node" + ring_node_host + "/" + ring_node_id); //user diagnostic output
   }//end constructor rindManager


    //******************************************************************************************


    /**
     * This is an overloaded constructor that instantiates a "killManager", this is
     * a ringManager that creates a killToken to send to the network indicating that
     * it is to terminate.
     *
     * @param ring_node_host The host at injection point
     * @param ring_node_id The ID of the host at injection point
     * @param killToken The value of the killToken (This will always be TRUE if this constructor is called)
     */
    public ringManager(String ring_node_host, String ring_node_id, boolean killToken)
    {
        System.setSecurityManager(new SecurityManager()); // Setting the security manager

        Token token = new Token(killToken); //creating a kill token

        sendToInjectionPoint(token, ring_node_host, ring_node_id); //sending token to network

        System.out.println("\nKill token passed to network");
    }


    //******************************************************************************************


    /**
     * This method creates a reference to the remote node and passes a token to it via RMI
     *
     * @param token The token to pass to the node network
     * @param ring_node_host The host at injection point
     * @param ring_node_id The ID of the host at injection point
     */
    private void sendToInjectionPoint(Token token, String ring_node_host, String ring_node_id)
    {
        try
        {
            //creates a remote reference to the node at injection point
            ringMember member_node = (ringMember)Naming.lookup("rmi://" + ring_node_host + "/" + ring_node_id);

            //performs remote call to injection points' takeToken method passing it the token and the name of the file
            member_node.takeToken(token);
        }
        catch(Exception e)
        {
            System.out.println("Node unavailable please try again...");
        }

    }//end method sendToInjectionPoint


    //******************************************************************************************


    /**
     * Main method of ringManager takes in cmd arguments and instantiates the ringManager object
     * using these arguments
     *
     * @param argv Array of arguments passed via command line
     */
   public static void main(String argv[])
   {
       //Host Identifiers
       String ring_host; //host at injection point
       String ring_id; // ID of host at injection point
       String extraTimeHost; //ID of host defined to get extra processing time
       String skipHost; //ID of host defined to skip every second turn

       //Token behaviour parameters
       String customFilename; //custom file name for shared resource
       int TTL; //TTL of token (Time To Live)

       //Kill token definition
       boolean killNetwork; //defines a killToken

       //This performs rudimentary validation on incoming arguments
       if(argv.length == 3 && argv[2].toLowerCase().equals("true")) //3rd argument must == true to create a kill token
       {
           ring_host = argv[0];
           ring_id = argv[1];
           killNetwork = Boolean.parseBoolean(argv[2]);

           ringManager killManager = new ringManager(ring_host, ring_id, killNetwork);
       }
       else if ((argv.length < 6) || (argv.length > 6))
       {
           System.out.println("Usage: [this ID][next host][next ID]");
           System.out.println(argv.length+" parameters entered");
           System.exit (1) ;
       }
       else
       {
           ring_host = argv[0];
           ring_id = argv[1];
           customFilename = argv[2];
           TTL = Integer.parseInt(argv[3]);
           extraTimeHost = argv[4];
           skipHost = argv[5];

           //ringManager object to kick-start the network
           ringManager manager = new ringManager(ring_host, ring_id, customFilename, TTL, extraTimeHost, skipHost);
       }
   }//end method main


    //******************************************************************************************


    /**
     * This methods cleans specified file before circulating it as a shared resource.
     * If is no file matching the name pass into it is found this creates a new file
     * under that name.
     *
     * @param customFilename The name of the shared file
     */
   private void clearFile(String customFilename)
   {
       System.out.println("Clearing " + customFilename + ".txt" ); //user diagnostic output
       try
       {
           //Creates a FileWriter object and creates a file using the custom file name
           FileWriter fw_id = new FileWriter(customFilename + ".txt", false);

           fw_id.close() ; //closing the FileWriter
       }
       catch (java.io.IOException e)
       {
           System.err.println("Exception in xxx clearing file: main: " +e);
       }
   }//end method clearFile
}//end of class ringManager
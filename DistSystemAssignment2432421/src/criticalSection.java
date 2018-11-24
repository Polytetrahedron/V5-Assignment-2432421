import java.io.*;
import java.rmi.*;
import java.util.*;

public class criticalSection extends Thread
{
    private String  this_id;
    private String  this_host;
    private String  next_id;
    private String  next_host;
    private Token token;
    private String customFilename;
    private int timeInCSection = 3000;

    /**
     * Constructor for instantiating the criticalSection
     *
     * @param t_host
     * @param t_id
     * @param n_host
     * @param n_id
     * @param token
     */
    public criticalSection(String t_host, String t_id, String n_host, String n_id, Token token)
    {
        
        this_host =t_host;
        this_id = t_id;
        next_host = n_host;
        next_id = n_id;
        this.token = token;
        this.customFilename = customFilename;
    }


    //******************************************************************************************


    /**
     * Constructor for killing the nodes via critical section
     *
     * @param killToken
     */
    public criticalSection(Token killToken, String next_host, String next_id, String this_id)
    {
        this.next_host = next_host;
        this.next_id = next_id;
        this.token = killToken;
        this.this_id = this_id;
    }


    //******************************************************************************************


    /**
     * This is the thread run method that increments the pass counter and handles the running of
     * the critical section processing as well as passing the token to the next node in the chain.
     * This also deals with the termination of the network by scanning incoming tokens for their type
     * if it is a kill token it handles this in the checkForKillToken method.
     */
    public void run()
    {
        if(!checkForKillToken())
        {
            token.setPassCounter(); //signals to the token it has been passed and increments its counter

            if(!token.setSkips(this_id)) //check to see if the current node needs to skip its processing time
            {
                checkTokenProperties();
                criticalOperation();

                //checkExpiryCirculations(); //remove comment slashes for this implementation
                checkExpiryHops();
            }
            else
            {
                //checkExpiryCirculations(); //remove comment slashes for this implementation
                checkExpiryHops();
            }

        }
    }//end method run


    //******************************************************************************************


    /**
     * This method checks to see if the current token is a kill token.
     * If it is a kill token it maps the current node ID to the network map and passes the token
     * to the next node in the chain. If the node detects that it is in the mapped list
     * then the node terminates itself and passes the token. This pass only happens if the
     * token is not the last node in the network.
     * @return
     */
    private boolean checkForKillToken()
    {
        if(token.checkKillToken())
        {
            if(!token.getVisitedNodes(this_id))
            {
                token.addCurrentNode(this_id); //add the current node to the map
                System.out.println("Passing mapped token");
                passToken(); //pass token down the chain
                return true;
            }
            else
            {
                if(token.checkLastNodeStatus()) //check to see if this node is the last node
                {
                    System.out.println("\nLAST NODE IS CHAIN: Kill token received, terminating node...");
                    System.exit(0); //kill node
                }
                else //if it's not the last node
                {
                    System.out.println("\nKill token received, terminating node...");
                    passToken(); //pass to next node in chain
                    System.exit(0);//kill node
                }
            }
        }
        return false;
    }//end of method checkForKillToken


    //******************************************************************************************


    /**
     * This is an implementation of the Token Expiry TTL in TOTAL network circulations.
     * In order for the token to expire it must pass through the start node for the
     * specified TTL length. This takes significantly longer than the hop based approach.
     */
    private void checkExpiryCirculations()
    {
        //This is TTL implemented via number of TOTAL network circulations
        if(token.getStartNodeID().equals(this_id))
        {
            token.setCirculations(); //increment the total circulations
        }
        if(token.getCirculations() != token.getTTL()) //checking to see if the token should expire
        {
            passToken(); //pass token if there is still life
        }
        else
        {
            System.out.println("Token expired TTL reached: " + token.getTTL());
            token = null; //drop the token
        }
    }//end method checkExpiryCirculations


    //******************************************************************************************


    /**
     * This is the token expiry implemented via number of hops, i.e. the number of times
     * the token is passed from node to node.
     * This behaviour is more similar to a network packet than the one based on number
     * of network circulations.
     * The token will expire after the number of hops matches that of the specified TTL.
     */
    private void checkExpiryHops()
    {
        if(token.getPassCounter() != token.getTTL()) //checking to see if token should expire
        {
            passToken(); //if it still has life then pass the token
        }
        else
        {
            System.out.println("Token expired TTL reached: " + token.getTTL() + " token dropped"); //print to use that the token has been dropped
            token = null; //drop the token
        }
    }//end method checkExpiryHops


    //******************************************************************************************


    /**
     * This checks the data on the received token and modifies the properties
     * of the node as required.
     */
    private void checkTokenProperties()
    {
        if(token.getExtraTimeHost().equals(this_id))
        {
            timeInCSection = token.getExtraTimeGiven();
        }
    }//end method checkToken


    //******************************************************************************************


    /**
     * This is the "processing" code for the critical section. It writes to a file specified via
     * the token, then increments the hop counter. This methods uses a FileWriter to write to the
     * specified file.
     */
    private void criticalOperation()
    {
        try {
            System.out.println("######################################\n");
            System.out.println("Entering critical sections " + this_host + "/" + this_id);
            System.out.println("Writing to file: " + token.getCustomFileName() +".txt" );

            //getting timestamp for data write
            Date timestmp = new Date() ;
            System.out.println("Token counter: " + token.getPassCounter()); //print number of hops
            String timestamp = timestmp.toString() ;

            //file and print writer instantiation
            FileWriter fw_id = new FileWriter(token.getCustomFileName() + ".txt",true); //writing to custom named file
            PrintWriter pw_id = new PrintWriter(fw_id, true) ;

            //data to be written into the file
            pw_id.println ("Record from ring node on host " + this_host + ", host ID " +this_id+ ", is " +timestamp);
            pw_id.close() ;
            fw_id.close() ;
        }
        catch (java.io.IOException e)
        {
            System.out.println("Error writing to file: "+e);
        }
        try
        {
            sleep (timeInCSection);
        }
        catch (java.lang.InterruptedException e)
        {
            System.out.println("sleep failed: "+e);
        }
    }//end method criticalOperation


    //******************************************************************************************


    /**
     * This method contains the code for passing the token to another node in the network
     * Using RMI it gets a reference to the next node in the network and using lookup to invoke
     * that nodes take token method.
     *
     * This method is only called when the current node has completed its critical section or
     * a kill token has been passed to the node
     */
    private void passToken()
    {
        try
        {
            System.out.println("Performing token pass to: " + next_host + "/" + next_id);
            ringMember nextNode = (ringMember)Naming.lookup("rmi://" + next_host + "/" + next_id);
            nextNode.takeToken(token);
            System.out.println("Token released \n");
            System.out.println("#######################################\n");
        }catch(Exception e)
        {
            System.out.println("Connection Issue, could not connect to next host in chain. \n" +
                    "(Are you in cleanup mode?)");
        }
    }//end method passToken

}//end class criticalSection
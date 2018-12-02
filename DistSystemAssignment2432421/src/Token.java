import java.io.Serializable;
import java.util.ArrayList;

//*******************************************************************
//  Class: Token
//  Desc: This class holds all of the Tokens properties for use by
//        the nodes in the network
//  @author 2432421
//*******************************************************************
public class Token implements Serializable
{
    //Token characteristics
    private int passCounter = 0;
    private ArrayList<String> visitedNodes = new ArrayList<>();
    private boolean killNode = false;
    private int circulations;
    private int skips = 0;
    private int extraTimeGiven = 6000;

    //Command line arguments
    private int TTL; //TTL of the token
    private String startNodeID; //this enables the token to detect when it as completed a cycle on the network
    private String extraTimeHost; //ID of host designated for extra time
    private String skipNode; //ID of host skipping every second turn
    private String customFileName; //name of shared resource



    /**
     * This constructor creates a token that can be passed from node to node on the network
     *
     * @param TTL The Time To Live of the token
     * @param extraTimeHost The ID of the host designated for extra time
     * @param startNodeID The node at injection point (this is only used in TTL via circulations)
     * @param skipNode The node designated to pass every second turn
     * @param customFileName The name of the shared resource
     */
    public Token(int TTL, String extraTimeHost, String startNodeID, String skipNode, String customFileName)
    {
        this.TTL = TTL;
        this.extraTimeHost = extraTimeHost;
        this.startNodeID = startNodeID;
        this.skipNode = skipNode;
        this.customFileName = customFileName;

    }//end of constructor Token

    /**
     * This is an overloaded constructor that creates a kill token. This will terminate all nodes on the current network.
     *
     * @param killNode Indicates the type of token it is (kill token) this will always be true if this is created
     */
    public Token(boolean killNode)
    {
        this.killNode = killNode; //sets local killNode variable to true

    }//end of constructor Token (killNode)


    //**************The section below contains the code that enables the skip processing behaviour*********

    /**
     * This allows a host to skip its processing time using the token every second pass
     *
     * @param host the ID of the current host
     * @return true if the host is to skip its processing time false if not
     */
    public boolean setSkips(String host)
    {
        if(skipNode.equals(host))
        {
            if(skips == 1)
            {
                skips = 0;
                System.out.println("Skipping token usage");
                return true;
            }
            skips++;
            return false;
        }
        return false;
    }


    //***************The section below section deals with the behaviour of the kill token******************

    /**
     *  Returns the type of token the node is dealing with.
     *
     * @return True if the token is kill token false if it's not
     */
    public boolean checkKillToken()
    {
        return killNode;
    }


    /**
     * This returns true if the node has already been visited and false if it has not.
     *
     * @param currentNodeID The current node interrogating the token
     * @return true of node is found false if not
     */
    public boolean getVisitedNodes(String currentNodeID)
    {
        if(visitedNodes.contains(currentNodeID))
        {
            visitedNodes.remove(visitedNodes.indexOf(currentNodeID));
            return true;
        }

        return false;
    }


    /**
     * Adds the current node to the node network map
     *
     * @param currentNodeID The ID of the node adding itself to the map
     */
    public void addCurrentNode(String currentNodeID)
    {
        visitedNodes.add(currentNodeID);
    }


    /**
     * Checking to see if the current node is the last node in the sequence
     *
     * @return true if the map is empty false if there are still node in it
     */
    public boolean checkLastNodeStatus()
    {
        return visitedNodes.isEmpty();
    }


    //***The section below contains all of the get and set methods needed to update and interrogate the token******

    /**
     * Returns the name of the shared resource used by the network
     *
     * @return customFileName name of the file
     */
    public String getCustomFileName()
    {
        return customFileName;
    }

    //******************************************************************************************

    /**
     * Returns the Id of the host designated for extra time
     *
     * @return extraTimeHost
     */
    public String getExtraTimeHost()
    {
        return extraTimeHost;
    }

    /**
     * Returns the value of the extra time given (by default this value is 6000)
     *
     * @return extraTimeGiven
     */
    public int getExtraTimeGiven()
    {
        return extraTimeGiven;
    }

    //******************************************************************************************

    /**
     * Returns the node at injection point
     * This is only used in the TTL via circulations
     *
     * @return startNodeID
     */
    public String getStartNodeID()
    {
        return startNodeID;
    }

    //******************************************************************************************

    /**
     * Increments the pass counter whenever a token is passed from node to node
     */
    public void setPassCounter()
    {
        passCounter++;

    }

    /**
     * Returns the total number of passes between nodes
     * This is printed as diagnostic output and also used to calculate the
     * TTL via hops for token expiry.
     *
     * @return passCounter
     */
    public int getPassCounter()
    {
        return passCounter;
    }

    //******************************************************************************************

    /**
     * Returns the Time To Live of the token
     *
     * @return TTL
     */
    public int getTTL()
    {
        return TTL;
    }

    //******************************************************************************************

    /**
     * Increments the number of full circulations of the node network
     */
    public void setCirculations()
    {
        circulations++;
    }

    /**
     * Returns the number of circulations the token has performed
     *
     * @return circulations
     */
    public int getCirculations()
    {
        return circulations;
    }
}

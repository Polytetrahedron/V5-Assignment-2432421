import com.sun.org.apache.xpath.internal.operations.Bool;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class Token implements Serializable
{
    private int passCounter = 0;
    private int TTL;
    private String startNodeID; //this enables the token to detect when it as completed a cycle on the network
    private String extraTimeHost;
    private int extraTimeGiven = 6000; //double the time in critical section
    private int circulations; //this needs implemented properly
    private String skipNode;
    private int skips = 0;
    private boolean killNode = false;
    private String customFileName;
    private ArrayList<String> visitedNodes = new ArrayList<>();


    /**
     *
     * @param TTL
     * @param extraTimeHost
     * @param startNodeID
     * @param skipNode
     * @param customFileName
     */
    public Token(int TTL, String extraTimeHost, String startNodeID, String skipNode, String customFileName)
    {
        this.TTL = TTL;
        this.extraTimeHost = extraTimeHost;
        this.startNodeID = startNodeID;
        this.skipNode = skipNode;
        this.customFileName = customFileName;
    }

    public Token(boolean killNode)
    {
        this.killNode = killNode;
    }


    //******************************************************************************************

    /**
     *
     * @param host
     * @return
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
        System.out.println(skips);
        return false;
    }

    //******************************************************************************************

    public boolean checkKillToken()
    {
        return killNode;
    }


    public boolean getVisitedNodes(String currentNodeID)
    {
        if(visitedNodes.contains(currentNodeID))
        {
            visitedNodes.remove(visitedNodes.indexOf(currentNodeID));
            return true;
        }

        return false;
    }

    public void addCurrentNode(String currentNodeID)
    {
        visitedNodes.add(currentNodeID);
        System.out.println(visitedNodes);
    }

    public boolean checkLastNodeStatus()
    {
        return visitedNodes.isEmpty();
    }



    //******************************************************************************************
    /**
     * Getters and setters
     * @return
     */

    public String getCustomFileName()
    {
        return customFileName;
    }

    //******************************************************************************************

    public String getExtraTimeHost()
    {
        return extraTimeHost;
    }

    public int getExtraTimeGiven()
    {
        return extraTimeGiven;
    }

    //******************************************************************************************

    public String getStartNodeID()
    {
        return startNodeID;
    }

    //******************************************************************************************

    public void setPassCounter()
    {
        passCounter++;

    }
    public int getPassCounter()
    {
        return passCounter;
    }

    //******************************************************************************************

    public int getTTL()
    {
        return TTL;
    }

    //******************************************************************************************

    public void setCirculations()
    {
        circulations++;
    }

    public int getCirculations()
    {
        return circulations;
    }
}

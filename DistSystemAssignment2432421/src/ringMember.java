
//*******************************************************************
//  Interface: ringMember
//  Desc: This is the remote interface that enables the nodes
//        to pass tokens between them.
//  @author 2432421
//*******************************************************************
public interface ringMember extends java.rmi.Remote
{
   void takeToken(Token token) throws java.rmi.RemoteException; //mandatory method for implementation via a class
}

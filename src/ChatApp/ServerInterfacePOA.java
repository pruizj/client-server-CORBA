package ChatApp;


/**
* ChatApp/ServerInterfacePOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from MyInterfaces.idl
* jueves 10 de marzo de 2022 12H40' CET
*/

public abstract class ServerInterfacePOA extends org.omg.PortableServer.Servant
 implements ChatApp.ServerInterfaceOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("connection", new java.lang.Integer (0));
    _methods.put ("newMessages", new java.lang.Integer (1));
    _methods.put ("getMessages", new java.lang.Integer (2));
    _methods.put ("listUsers", new java.lang.Integer (3));
    _methods.put ("listRooms", new java.lang.Integer (4));
    _methods.put ("createNewRooms", new java.lang.Integer (5));
    _methods.put ("joinRoom", new java.lang.Integer (6));
    _methods.put ("leaveRoom", new java.lang.Integer (7));
    _methods.put ("deleteRoom", new java.lang.Integer (8));
    _methods.put ("disconnect", new java.lang.Integer (9));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // ChatApp/ServerInterface/connection
       {
         String userName = in.read_string ();
         String $result = null;
         $result = this.connection (userName);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 1:  // ChatApp/ServerInterface/newMessages
       {
         String roomName = in.read_string ();
         String message = in.read_string ();
         this.newMessages (roomName, message);
         out = $rh.createReply();
         break;
       }

       case 2:  // ChatApp/ServerInterface/getMessages
       {
         String roomName = in.read_string ();
         String $result = null;
         $result = this.getMessages (roomName);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 3:  // ChatApp/ServerInterface/listUsers
       {
         String roomName = in.read_string ();
         String $result = null;
         $result = this.listUsers (roomName);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 4:  // ChatApp/ServerInterface/listRooms
       {
         String $result = null;
         $result = this.listRooms ();
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 5:  // ChatApp/ServerInterface/createNewRooms
       {
         String roomName = in.read_string ();
         String $result = null;
         $result = this.createNewRooms (roomName);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 6:  // ChatApp/ServerInterface/joinRoom
       {
         String roomToJoin = in.read_string ();
         String name = in.read_string ();
         String $result = null;
         $result = this.joinRoom (roomToJoin, name);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 7:  // ChatApp/ServerInterface/leaveRoom
       {
         String roomToLeave = in.read_string ();
         String name = in.read_string ();
         String $result = null;
         $result = this.leaveRoom (roomToLeave, name);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 8:  // ChatApp/ServerInterface/deleteRoom
       {
         String roomName = in.read_string ();
         String $result = null;
         $result = this.deleteRoom (roomName);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 9:  // ChatApp/ServerInterface/disconnect
       {
         String userName = in.read_string ();
         String roomName = in.read_string ();
         this.disconnect (userName, roomName);
         out = $rh.createReply();
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:ChatApp/ServerInterface:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public ServerInterface _this() 
  {
    return ServerInterfaceHelper.narrow(
    super._this_object());
  }

  public ServerInterface _this(org.omg.CORBA.ORB orb) 
  {
    return ServerInterfaceHelper.narrow(
    super._this_object(orb));
  }


} // class ServerInterfacePOA

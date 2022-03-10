import ChatApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

import java.util.*;
import java.util.regex.Pattern;

class ServerInterfaceImpl extends ServerInterfacePOA {

	// lista para almacenar todos los mensajes enviados de una persona registrada
	static List<String> messageLogs = new ArrayList<>();

	// lista para almacenar todos los usuarios con las salas a las que estan conectadas
	static List<String> roomUsers = new ArrayList<>();

	// lista para almecenar los nombres de usuarios
	private static List<String> names = new ArrayList<>();

	// lista para almecenar todos las salas disponibles, y la sala por defecto es la 'general'
	private static List<String> rooms = new ArrayList<String>() {
		{
			add("general");
		}
	};

	// crear un objeto ORB
	private ORB orb;

	// inicializar el objeto ORB
	public void setORB(ORB orb_val) {
		orb = orb_val;
	}

	// metodo de log in
	public String connection(String userName) {

		// crear un StringBuilder para almacenar los mensajes
		StringBuilder sb = new StringBuilder();

		// caso el usuario ya existe
		if (names.contains(userName.toLowerCase())) {
			sb.append("failure");

		// caso el usuario no existe todavia
		} else {
			// añadir el nombre de usuario a los usuarios
			names.add(userName);
			// añadir nombre de usuario y sala por defecto a la lista de sala de usuarios
			roomUsers.add("general " + userName);

			// crear un nuevo mensaje indicando que un nuevo usuario se ha conectado
			// y lo añadimos a la lista mensajes enviados de registro
			String TimeStamp = new java.util.Date().toString();
			String connectedTime = "Connected on " + TimeStamp;
			messageLogs.add("general @" + userName + " " + connectedTime);

			sb.append("joined");

			// obtener todos los mensajes enviados anteriormente a este grupo 
			// y agréguelos a un objeto generador de cadenas (sb)
			for (int i = 0; i < messageLogs.size(); i++) {
				if (messageLogs.get(i).startsWith("general")) {
					sb.append("|" + messageLogs.get(i));
				}
			}
		}

		// devuelve un objeto generador de cadenas (sb) que contiene todos los mensajes
		// anteriores en este grupo separados por este simbolo: '|' 
		return sb.toString();
	}

	// metodo añadir un mensaje: añadimos el nombre de la sala seguido del mensaje
	public void newMessages(String roomName, String message) {
		messageLogs.add(roomName + " " + message);
	}

	// metodo que devuelve el ultimo mensaje de la sala que pasamos por argumento
	public String getMessages(String roomName) {
		String valueToReturn = "";
		
		// coger el ultimo mensaje de la lista de mensajes
		String message = messageLogs.get(messageLogs.size() - 1);

		// si el último mensaje devuelto es para la sala que he pasado,
		// se agrega a la variable 'valueToReturn' y si no 'valueToReturn' será nulo
		if (message.startsWith(roomName)) {
			valueToReturn = message.substring(message.indexOf(" ") + 1);
		}

		return valueToReturn;
	}

	// metodo que devuelve la lista the todos los usuarios conectados
	public String listUsers(String roomName) {

		StringBuilder sb = new StringBuilder();

		// recorrer la lista de nombres y agregar todos los nombres de usuario
		// a un objeto 'StringBuilder (sb)'
		for (String s : names) {
			sb.append(s);
			sb.append(" ");
		}

		return sb.toString();
	}

	// metodo que devuelve la lsita de todas las salas disponibles
	public String listRooms() {
		StringBuilder sb = new StringBuilder();

		// recorrer la lista de salas y agregar todas las salas disponibles
		// a un objeto 'StringBuilder (sb)'
		for (String s : rooms) {
			sb.append(s);
			sb.append(" ");
		}

		return sb.toString();
	}

	// metodo que crea una nueva sala y devuelvo el mensaje de sala creada o la sala ya existe
	public String createNewRooms(String roomName) {
		String response = "";

		// si la sala existe devuelvo mensaje de error y si no,
		// añado la sala a la lista de salas y devuelvo el mensaje de sala creada
		if (rooms.contains(roomName)) {
			response = "exist";
		} else {
			rooms.add(roomName);
			response = "created";
		}

		return response;
	}

	// metodo para unirme a una sala existente, 
	// paso por argumentos la sala a la que me queiro unir y el nombre de usuario
	public String joinRoom(String roomToJoin, String name) {
		StringBuilder response = new StringBuilder();

		// caso la sala no existe devuelvo mensaje de error
		if (!rooms.contains(roomToJoin)) {
			response.append("no-room");

		// caso la sala existe
		} else {
			// añadir la sala y el nombre de usuario a la lista de slas de usuarios
			roomUsers.add(roomToJoin + " " + name);
			// envio el mensaje de usuario se ha unido a la sala
			messageLogs.add(roomToJoin + " " + name + " has joined");
			// devolver mensaje de acierto
			response.append("joined");
			// devolver los mensajes de la sala a la que te unes
			for (int i = 0; i < messageLogs.size(); i++) {
				if (messageLogs.get(i).startsWith(roomToJoin)) {
					response.append("|" + messageLogs.get(i));
				}
			}
		}

		return response.toString();
	}

	// metodo para salirse de la sala
	// paso por argumentos la sala de la que me queiro salir y el nombre de usuario
	public String leaveRoom(String roomToLeave, String name) {
		String response = "";

		// caso la sala no existe devuelvo mensaje de error
		if (!rooms.contains(roomToLeave)) {
			response = "no-room";
		// caso la sala existe pero no estoy unido a ella
		} else if (!roomUsers.contains(roomToLeave + " " + name)) {
			response = "no-user";
		// caso correcto
		} else {
			// elimino el usuario de la sala
			roomUsers.remove(roomToLeave + " " + name);
			// envio el mensaje de usuario ha salido de la sala
			messageLogs.add(roomToLeave + " " + name + " has left");
			// delvolver mensaje de salida de la sala correcta
			response = "leave-success";
		}

		return response;
	}

	// NUEVA FUNCIONALIDAD
	// metodo para eliminar una sala
	public String deleteRoom(String roomName) {
		String response = "";

		// caso la sala existe 
		if (rooms.contains(roomName)) {
			if(roomName.equals("general")){
				response = "general";
			}else{
				// si el usuario esta en la sala lo elimino de ella
				for (String s : names) {
					if (roomUsers.contains(roomName + " " + s)) {
						roomUsers.remove(roomName + " " + s);
					}
				}	
				// elimino la sala de la lista de salas
				rooms.remove(roomName);
				// devuelvo mensaje de sala eliminada
				response = "deleted";
			}

		// caso la sala no existe devuelvo mensaje de error
		} else {
			response = "not exist";
		}

		return response;
	}

	// metodo de log out
	// pasmaos por argumentos el nombre de usuario y la sala a la que esta conectada
	public void disconnect(String userName, String roomName) {
		// eliminar usuario de la lista de usuarios
		names.remove(userName);
		// enviar mensaje indicando que el usuario se ha ido
		messageLogs.add(roomName + " " + userName + " has left");
	}

}

// Servidor
public class CORBAServer {

	public static void main(String args[]) {

		try {
			// crear e inicialiar el ORB
			ORB orb = ORB.init(args, null);

			// obtener una referencia a rootpoa y activar el POAManager
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			// crear el servidor y registrarlo en el ORB
			ServerInterfaceImpl serverInterfaceImpl = new ServerInterfaceImpl();
			serverInterfaceImpl.setORB(orb);

			// obtener la referncia de objeto al servidor
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(serverInterfaceImpl);
			ServerInterface href = ServerInterfaceHelper.narrow(ref);

			// obtener la ruta del naming context
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			// Usar NamingContextExt que es parte de la especificacion Interoperable Naming Service (INS)
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			// vincular la referencia de objeto a Naming
			String name = "ServerInterface";
			NameComponent path[] = ncRef.to_name(name);
			ncRef.rebind(path, href);

			System.out.println("Server running, accepting client connection...");

			// esperar las invocaciones de los clientes
			orb.run();
		}

		catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}
	}
}
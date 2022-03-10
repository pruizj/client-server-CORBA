import ChatApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import java.util.Scanner;
import java.io.*;
import java.util.regex.Pattern;

// Cliente
public class CORBAClient {

    static ServerInterface serverInterfaceImpl;

    public static String lastMessage = "";
    public static String userName = "";
    public static String connectedRoom = "";
    public static String strResponse = "";

    public static void main(String args[]) {

        try {
            // crear e inicializar el ORB
            ORB orb = ORB.init(args, null);

            // obtener la raiz naming context
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

            // Usar NamingContextExt en vez de NamingContext. Es parte de Interoperable naming Service.  
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // resolver el Object Reference en Naming
            String name = "ServerInterface";
            serverInterfaceImpl = ServerInterfaceHelper.narrow(ncRef.resolve_str(name));

            // imprimir un mensaje a la terminal
            System.out.println("Obtained a handle on server object: " + serverInterfaceImpl);

            // crear un buffer para almacenar el flujo de entrada
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            //imprimir en la terminal 
            System.out.println("");
            System.out.print("Enter a name : ");
            String nameInput = br.readLine();
            System.out.println("");

            // obtener la respuesta del servidor web (los mensajes de registro y si el log in es un existo). 
            // se llama al metodo 'connection' para crear el usuario si no existe y añadirlo a la sala por defecto
            String connectionResponse = serverInterfaceImpl.connection(nameInput);
            //Caso de fracaso
            if (connectionResponse.equals("failure")) {
                System.out.println("Please choose a different name");
            //Caso correcto 
            } else {
                // guardamos el nombre de usuario en la variable "userName" 
                // y la respuesta del sevidor web en la variable "strResponse"
                // conectamos al usuario a la sala general 
                connectedRoom = "general";
                strResponse = connectionResponse;
                userName = nameInput;

                //Imprimir el momento de conection en la terminal
                String TimeStamp = new java.util.Date().toString();
                String connectedTime = "Connected on " + TimeStamp;

                System.out.println(connectedTime);
                System.out.println("");

                //obtener todos los mensajes enviados previamente a esta sala e imprímalos en la terminal
                String[] strResponseParts = strResponse.split(Pattern.quote("|"));
                for (int i = 1; i < strResponseParts.length - 1; i++) {
                    String[] strArr = strResponseParts[i].split(" ", 2);
                    System.out.println(strArr[1] + "\n");
                }

                // crear un hilo que seguirá preguntando al servidor si hay un mensaje nuevo para la sala
                // conectada cada 500 milisegundos
                Thread receivingMessages = new Thread(new Runnable() {
                    public void run() {
                        while (true) {
                            String serverResponse = serverInterfaceImpl.getMessages(connectedRoom);
                            if (!(serverResponse.equals(lastMessage)) && !(serverResponse.equals("")) && !(serverResponse.startsWith("@" + userName))) {
                                lastMessage = serverResponse;
                                System.out.println(serverResponse + "\n");
                            }

                            try {
                                Thread.sleep(500);
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        }
                    }
                });

                // iniciar el hilo de recepción de mensajes
                receivingMessages.start();

                while (true) {
                    //Guardar en "input" el flujo de entrada
                    String input = br.readLine();
                    System.out.println("");

                    // si 'input' comienza con 'exit', 
                    if (input.equals("exit")) {
                        // desconectar el cliente llamando al método 'disconect'
                        serverInterfaceImpl.disconnect(userName, connectedRoom);
                        System.exit(0);

                    // si 'input' comienza con la palabra clave '/join'
                    } else if (input.startsWith("/join")) {
                        // obtener el nombre de la sala para unirse
                        String[] inputParts = input.split(Pattern.quote(" "));
                        // caso usuario ya está conectado a esa habitación
                        if (inputParts[1].equalsIgnoreCase(connectedRoom)) {
                            System.out.println("You are already in this room\n");
                        // caso usuario no esta conectado
                        } else {
                            // Llamar al método 'joinRoom' y pasar el nombre de la sala a la que se quiere unir y el nombre de usuario
                            String response = serverInterfaceImpl.joinRoom(inputParts[1], userName);
                            // Obtener la respuesta del método
                            String[] responseParts = response.split(Pattern.quote("|"));

                            // si la respuesta es 'joined'
                            if (response.startsWith("joined")) {

                                //  Dejar el método previamente conectado
                                String leave = serverInterfaceImpl.leaveRoom(connectedRoom, userName);

                                connectedRoom = inputParts[1];
                                System.out.println("You have left " + connectedRoom + " room\n");

                                // Mostrar todos los mensajes anteriores enviados a la nueva sala conectada
                                for (int i = 1; i < responseParts.length - 1; i++) {
                                    String[] arr = responseParts[i].split(" ", 2);
                                    System.out.println(arr[1] + "\n");
                                }
                            // si la respuesta es 'no-room', muestrar el error de sala no encontrada
                            } else if (response.equals("no-room")) {
                                System.out.println(inputParts[1] + " room not found\n");
                            // caso comando no válido
                            } else {
                                System.out.println("Invalid command room\n");
                            }
                        }

                    // si 'input' comienza con la palabra clave '/leave'
                    } else if (input.startsWith("/leave")) {
                        // comprobar la sala de la que se quiere salir
                        String[] inputParts = input.split(Pattern.quote(" "));

                        // si quieres salir de la general no se puede, por lo que se produce un mensaje de error 
                        if (inputParts[1].equals("general")) {
                            System.out.println("You cant leave general room\n");

                        // verificar que la sala exista y que el usuario esté conectado a la sala 
                        // y mostrar el mensaje apropiado (ya sea correcto o fallido)
                        } else {
                            String response = serverInterfaceImpl.leaveRoom(inputParts[1], userName);
                            if (response.equals("no-room")) {
                                System.out.println(inputParts[1] + " room not found\n");
                            } else if (response.equals("no-user")) {
                                System.out.println("You are not in " + inputParts[1] + " room\n");
                            } else if (response.equals("leave-success")) {
                                connectedRoom = "general";
                                System.out.println("You have left " + inputParts[1] + " room\n");
                            } else {
                                System.out.println("Invalid command room\n");
                            }
                        }

                    // si 'input' comienza con la palabra clave '/users'
                    } else if (input.startsWith("/users")) {
                        // obtener la lista de todos los usuarios llamando al método 'listUsers'
                        String returnValue = serverInterfaceImpl.listUsers(connectedRoom);
                        String[] returnValueParts = returnValue.split(Pattern.quote(" "));

                        // mostrar los usuarios por pantalla
                        System.out.println("*** All Users ***\n");
                        for (int i = 0; i < returnValueParts.length; i++) {
                            System.out.println((i + 1) + ". " + returnValueParts[i] + "\n");
                        }
                        System.out.println("*******************\n");

                    // si 'input' comienza con la palabra clave '/rooms'
                    } else if (input.startsWith("/rooms")) {
                        // obtener la lista de todos las salas llamando al método 'listRooms'
                        String returnValue = serverInterfaceImpl.listRooms();
                        String[] returnValueParts = returnValue.split(Pattern.quote(" "));

                        // mostrar las salas por pantalla
                        System.out.println("*** Rooms ***\n");
                        for (int i = 0; i < returnValueParts.length; i++) {
                            System.out.println((i + 1) + ". " + returnValueParts[i] + "\n");
                        }
                        System.out.println("*******************\n");

                    // si 'input' comienza con la palabra clave '/create'
                    } else if (input.startsWith("/create")) {
                        // crear una sala llamando al metodo 'createNewRooms' si la sala no existe
                        String[] inputParts = input.split(Pattern.quote(" "));
                        String response = serverInterfaceImpl.createNewRooms(inputParts[1]);
                        //Respuesta por terminal dependiendo de si existe o no la sala
                        if (response.equals("exist")) {
                            System.out.println(inputParts[1] + " room does not exist\n");
                        } else if (response.equals("created")) {
                            System.out.println(inputParts[1] + " room was created\n");
                        }
 
                    // enviar mensaje a todos los usurios conectados llamando al metodo 'newMessages'
                    // al metodo le pasamos la sala a la que estamos conectada ('connectedRoom'),
                    // el nombre de usuario ('userName') y el mensaje ('input')  
                    } else {
                        serverInterfaceImpl.newMessages(connectedRoom, "@" + userName + ":" + input);
                        
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR : " + e);
            e.printStackTrace(System.out);
        }
    }
}

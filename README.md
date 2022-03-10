## CHAT-CORBA
Esta es una aplicación de chat cliente-servidor implementada con Java y CORBA.

## Funcionamiento 📋
Esta aplicación consiste en un servidor y un cliente.
El servidor permitir a los clientes:

* Log in (nada más se incia el programa)
* Crear salas de chat
* Listar todas las salas existentes
* Unirse a las salas de chat existentes
* Enviar mensajes a salas de chat
* Abandonar una sala de chat
* Eliminar una sala de chat
* Log out (commando exit)

El cliente proporciona una interfaz que permite al usuario realizar las funciones anteriores.

Una vez creadas las salas de chat, almacenan todos los datos (mensajes enviados a la sala) mientras existan.

Cuando un usuario se conecta a una nueva sala, todos los mensajes anteriores de esta sala se muestran al usuario. Además, todos los mensajes enviados por el usuario se muestran a todos los demás clientes conectados a la misma sala con un retraso máximo de 1 segundo. Cuando un usuario sale de una sala, el servidor desconecta al usuario del sistema.

### Pre-requisitos 📦
Se necesita instalar Java y el Kit de Desarrollo de Java (JDK). Además hay que añadir a la variable PATH la ruta de JDK y a la variable PATHCLASS la ruta del directorio principal de nuestro proyecto.

### Compilación 🔧
Desde el directorio raíz del proyecto, abrir la terminal y ejecutar los siguientes comandos:
    
    idlj -td src/ -fall MyInterfaces.idl
    javac src/*.java src/ChatApp/*.java -d bin/
    orbd -ORBInitialPort 1050 -ORBInitialHost localhost

Iniciar el servidor ejecutando el siguiente comando:

    java -cp bin/ CORBAServer -ORBInitialPort 1050 -ORBInitialHost localhost

Después de que el servidor se haya iniciado correctamente, crear varios clientes con el siguiente comando:

    java -cp bin/ CORBAClient -ORBInitialPort 1050 -ORBInitialHost localhost

### Ejecución 🚀
Antes de que un cliente pueda conectarse al servidor web, el cliente debe proporcionar un nombre de usuario al servidor (Log in). Luego, el servidor verifica de que ningún otro cliente conectado esté usando el mismo nombre de usuario. Si el nombre de usuario existe en el servidor, se muestra un mensaje de error que le pide al usuario que elija un nombre de usuario diferente. De lo contrario, el servidor registra al usuario en la sala por defecto 'general' e informa a todos los usuarios conectados a esta sala que se ha conectado un nuevo cliente.

Utilice los clientes creados para interactuar con el servidor realizando varias operaciones con los 
siguientes comandos:

  * **'/create roomName'**
  * **'/rooms'**
  * **'/join roomName'**
  * **'/leave roomName'**
  * **'/users'**
  * **'/delete roomName'**
  * **'exit'**
  * Envíar mensajes a salas de chat simplemente escribiendo un mensaje en el cuadro de texto del cliente y presionando la tecla Intro

## Autor ✒️
**Paula Ruiz Jiménez**
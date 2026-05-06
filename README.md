<h1>AppMensajeria</h1>
</hr>
<h2>Indice</h2>
<p>Resumen</p>
<p>Funcionamiento</p>
</hr>
<h2>Resumen</h2>
</hr>
<p>Este programa se diseño para un proyecto de sostenibilidad por Mario Ocaña Vílchez en 1ºDAM con el objetivo de crear una aplicacción que permitiera enviar mensajes a uno o varios usuarios así como apps conocidas
como whatsapp o telegram y que a la vez fuese eficiente para no tardar mucho en hacer tareas como iniciar sesión para lo cual se implementaron indices en la base de datos para que sea más rapido acceder a la información.</p>
<p>También se implementaron funciones para recargar el chat que esta en uso en caso de que llege un mensaje y funciones para que al agregar un usuario a un grupo no tenga acceso a mensajes previos.</p>
<p>También se opto por guardar todos los mensajes de los chats para que en casos donde alguien use la app para cosas malicioasas no quede impune y los chats no se pueden borrar los usuarios pueden salirse del grupo pero el chat seguira existendo</p>
<hr>
<h2>Funcionamiento</h2>
<h3>Inicio de sesión</h3>
<img width="375" height="188" alt="image" src="https://github.com/user-attachments/assets/54b64fcd-3eb7-445b-b2b0-d097367db3f9" />
<p>El menú de inicio de sesión consta de 4 opciones: </p>
<p><b>1. Inicia sesión </b>: sirve para acceder a la app con una cuenta ya creada</p>
<p><b>2. registrarse</b>: sirve para crear una cuenta</p>
<p><b>3. recuperar cuenta</b>: sirve para iniciar sesión con una cuenta que fue borrada y recuperarla</p>
<p><b>4. salir</b>: sirve para cerrar la app</p>
<p><b>La app tiene un sistema en el que si tu no cierras sesión inicias automaticamente con el usuario usado anteriormente</b></p>
<p><b>La base de datos se ordena por la fecha de ultima conexión por lo que los usuarios que inicien sesión pasaran a encontrarse mas rápido siendo mas eficiente y dejando las cuentas sin uso al final</b></p>
<p></p>
<hr>
<h3>Menú principal</h3>
<img width="333" height="256" alt="image" src="https://github.com/user-attachments/assets/65248fe5-3a7f-4923-93e1-6bd7d14d3e35" />
<p>El menú principal consta de 7 opciónes</p>
<p><b>1. Abrir chats</b>: despliega un menú con todos los chats que tiene el usuario</p>
<img width="309" height="104" alt="image" src="https://github.com/user-attachments/assets/edf98f1d-2919-40fe-ba17-28ac2f41075b" />
<p>Al seleccionar uno se nos habren los mensajes del chat y podemos enviar un mensaje,acceder al menú admin con la palabra reservada admin si tenemos los privilegios de admin o volver al menu de los chats con la palabra reservada salir</p>
<img width="1117" height="133" alt="image" src="https://github.com/user-attachments/assets/8c265f3e-b138-4d58-a64f-3966eb09fcdb" />
<p><b>La app tiene un sistema en el que si estas usando un chat y llega un mensaje se recarga el chat para mostrar el nuevo mensaje</b></p>
<hr>
<h3>Menú admin</h3>
<p>El menú admin tiene 6 opciopciones: </p>
<p><b>1. Cambiar el nombre del grupo</b>: que cambia el nombre del grupo.</p>
<p><b>2. Hacer admin</b>: El cual le da los privilegios de admin a otro miembro del grupo teniendo ahora dos administradores.</p>
<p><b>3. Añadir usuario</b>: que añade otro usuario que no tiene acceso a los mensajes anteriores.</p>
<p><b>4. Quitar admin</b>: que quita los privilegios de admin a otro miembro del grupo que tenia privilegios de admin.</p>
<p><b>5. Expulsar usuario</b>: que expulsa a otro usuario del grupo.</p>
<p><b>6. Salir</b>: para volver al chat.</p>
<img width="354" height="168" alt="image" src="https://github.com/user-attachments/assets/34c5699a-6958-4642-a0b0-c325e3ab5a0c" />

<hr>
<p><b>2. Crear grupo</b></p>
<p>Primero nos pide los usuarios asta que escribamos salir o crear</p>
<p>Si escribimos salir se cancela la operación</p>
<p>Al insertar un usuario el programa lo busca en la base de datos y si lo encuentra lo añade si no el programa dice que no existe</p>
<p>Al darle a crear si hemos introducido usuarios el programa nos pregunta si le queremos poner nombre al grupo</p>
<p>Si le decimos que no el nombre sera el nombre de todos los integrantes salvo el usuario el cual este logueado por lo que el nombre sera diferente para cada usuario del grupo quitando su nombre</p>
<p>Si le decimos que si nos pedira introducir el nombre al grupo</p>
<p>Despues se creara el grupo y se mandar el mensaje de creación con la fecha de creación y el usuario que lo ha creado y nos metera en el chat automaticamente</p>
<img width="1313" height="112" alt="image" src="https://github.com/user-attachments/assets/8250d4c8-cd71-4a16-985a-6e11c0ba4e6e" />
<img width="1083" height="84" alt="image" src="https://github.com/user-attachments/assets/0224f8d6-a0e8-44f4-aa52-cea83512dbf0" />

<p><b>3. Crear chat</b>: nos pregunta el nombre de un usuario y si hay un chat con el nos mete en ese chat y si no crea un chat pero no hay administradores</p>
<img width="863" height="46" alt="image" src="https://github.com/user-attachments/assets/9a58a70d-b7c1-4f59-bebf-6a0e342fa8f9" />
<img width="1131" height="56" alt="image" src="https://github.com/user-attachments/assets/02475ad4-a0ca-46c8-a690-775d2ae589b6" />
<p><b>4. Cambiar datos</b>: abre un submenu que sirve para cambiar el email o la contraseña</p>
<img width="318" height="121" alt="image" src="https://github.com/user-attachments/assets/1614f3c2-2a5a-41f3-a4a2-883515ecf8d7" />
<p><b>5. Borra chat</b>: Borra todos los mensajes de un chat o grupo para el usuario y elimina al usuario de el en caso de ser un grupo</p>
<img width="368" height="125" alt="image" src="https://github.com/user-attachments/assets/77e5e983-ae51-405c-bba6-c48eb274e707" />
<img width="204" height="38" alt="image" src="https://github.com/user-attachments/assets/5c044cae-10d5-4bdc-ad57-28bf278f4994" />
<p><b>6. Borra cuenta</b>: Borra todos los mensajes que le han llegado al usuario y elimina al usuario de todos los chats antes de eliminar la cuenta y volver al menu de inicio</p>
<img width="710" height="287" alt="image" src="https://github.com/user-attachments/assets/9aef6a5b-56c5-4152-a46c-ba49d3181568" />
<p><b>7. Salir</b>: Cierra sesión y vuelve al menú de inicio de sesión</p>
<hr>










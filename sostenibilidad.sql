drop database if exists wats;
create database wats;
use wats;
create table usuariosActivos(
id integer unsigned,
email varchar(40),
clave varchar(40),
ultimaConexion datetime,
constraint PK_usuariosActivos primary key (id),
constraint UK_usuariosActivos_email unique (email),
constraint CK_usuariosActivos_email check (email like '%@gmail.com' or email like '%@yahoo.com')
);
create table usuariosBorrados(
id integer unsigned,
email varchar(40),
clave varchar(40),
ultimaConexion datetime,
constraint PK_usuariosBorrados primary key (id),
constraint CK_usuariosBorrados_email check (email like '%@gmail.com' or email like '%@yahoo.com')
);

create table chats(
id integer unsigned,
nombre varchar(100) null,
ultimoMensaje datetime,
constraint PK_chats primary key (id)
);
create table mensajeChat(
idChat integer unsigned,
idUserEnvia integer unsigned,
texto text,
fechaEnviado datetime,
constraint PK_mensaje primary key (fechaEnviado,idChat,idUserEnvia),
constraint FK_mensaje_idChat foreign key (idChat) references chats(id) on delete cascade on update cascade
);
create table mensajeUsuario(
idChat integer unsigned,
idUserEnvia integer unsigned,
idUserReceptor integer unsigned,
texto text,
fechaEnviado datetime,
constraint PK_mensajeUsuario primary key (fechaEnviado,idChat,idUserReceptor,idUserEnvia),
constraint FK_mensajeUsuario_idChat foreign key (idChat) references chats(id) on delete cascade on update cascade,
constraint FK_mensajeUsuario_idUserReceptor foreign key (idUserReceptor) references usuariosActivos(id) on delete cascade on update cascade
);
create table chatUsuario(
id integer unsigned,
idUser integer unsigned,
constraint PK_chatUsuario primary key (id,idUser),
constraint FK_chatUsuario_id foreign key (id) references chats(id) on delete cascade on update cascade,
constraint FK_chatUsuario_idUser foreign key (idUser) references usuariosActivos(id) on delete cascade on update cascade
);

create table userAdmin(
id integer unsigned,
idUser integer unsigned,
constraint PK_userAdmin primary key (id,idUser),
constraint FK_userAdmin_id foreign key (id) references chats(id) on delete cascade on update cascade,
constraint FK_userAdmin_idUser foreign key (idUser) references usuariosActivos(id) on delete cascade on update cascade
);
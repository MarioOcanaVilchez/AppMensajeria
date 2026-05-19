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

create table usuarioBloqueado(
idUserBloquea integer unsigned,
idUserBloqueado integer unsigned,
constraint PK_usuarioBloqueado primary key (idUserBloquea,idUserBloqueado),
constraint FK_usuarioBloqueado_ids foreign key (idUserBloquea) references usuariosActivos(id) on delete cascade on update cascade,
constraint FK_usuarioBloqueado_ids2 foreign key (idUserBloqueado) references usuariosActivos(id) on delete cascade on update cascade
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
chat boolean,
constraint PK_chats primary key (id)
);
create table mensajeChat(
id bigint,
idChat integer unsigned,
idUserEnvia integer unsigned,
texto text,
fechaEnviado datetime,
constraint PK_mensaje primary key (id),
constraint FK_mensaje_idChat foreign key (idChat) references chats(id) on delete cascade on update cascade
);
create table mensajeUsuario(
idMensaje bigint,
idChat integer unsigned,
idUserEnvia integer unsigned,
idUserReceptor integer unsigned,
texto text,
fechaEnviado datetime,
leido boolean default false,
constraint PK_mensajeUsuario primary key (idMensaje,idUserReceptor),
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
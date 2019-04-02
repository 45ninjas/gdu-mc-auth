-- The users table, it stores data about users.
CREATE TABLE users (
	UUID BINARY(16),
	status varchar(32) default "AUTH_NEW",
	mixerID int unsigned not null,
	minecraftName varchar(255) null,
	mixerName varchar(255) null,
	mixerOAuthCode text null,
	lastLogin timestamp null,
	created timestamp default current_timestamp,

	PRIMARY KEY(UUID),
	UNIQUE(mixerID)
) engine = innodb;

-- The tokens table, it stores data about active tokens.
CREATE TABLE shortcodes (
	UUID BINARY(16),
	code varchar(6) not null,
	handle text not null,
	expires timestamp not null,
	PRIMARY KEY(UUID),
	FOREIGN KEY(UUID)
		REFERENCES users(UUID)
		ON DELETE CASCADE
) engine = innodb;
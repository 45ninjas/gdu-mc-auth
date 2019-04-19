-- The users table, it stores data about users.
CREATE TABLE users (
	UUID BINARY(16),
	mixerID int unsigned null,
	minecraftName varchar(255) null,
	mixerName varchar(255) null,
	lastLogin timestamp null,
	created timestamp default current_timestamp,

	PRIMARY KEY(UUID),
	UNIQUE(mixerID)
) engine = innodb;

-- The shortcodes table, it stores data about active shortcodes.
CREATE TABLE shortcodes (
	UUID BINARY(16),
	shortcode varchar(6) not null,
	handle text not null,
	expires timestamp not null,
	code text null,
	PRIMARY KEY(UUID),
	FOREIGN KEY(UUID)
		REFERENCES users(UUID)
		ON DELETE CASCADE
) engine = innodb;

-- The OAuth tokens table
CREATE TABLE tokens (
	UUID BINARY(16),
	access_token text not null,
	type text not null,
	expires timestamp not null,
	refresh_token text null,
	PRIMARY KEY(UUID),
	FOREIGN KEY(UUID)
		REFERENCES users(UUID)
		ON DELETE CASCADE
) engine = innodb;
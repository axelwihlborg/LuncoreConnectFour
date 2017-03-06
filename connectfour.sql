PRAGMA foreign_keys = off;

DROP TABLE IF EXISTS highscore;

PRAGMA foreign_keys = on;

CREATE TABLE highscore(
	username 	varchar(20),	
	games_won	int not null,
	primary key 	(username)
);

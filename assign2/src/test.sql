DROP TABLE IF EXISTS Player;
CREATE TABLE Player (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    money REAL DEFAULT 40.0,
    current_game INTEGER DEFAULT -1,
    curr_bet REAL DEFAULT 0.0,
    bet_multiplier REAL DEFAULT 1.0
);
DROP TABLE IF EXISTS Game;
CREATE TABLE Game (
    id INTEGER PRIMARY KEY,
    running BOOLEAN DEFAULT TRUE
    -- add other fields related to the game if needed
);
DROP TABLE IF EXISTS Round;
CREATE TABLE Round (
    id INTEGER PRIMARY KEY,
    game_id INTEGER NOT NULL,
    crashed_time TIMESTAMP DEFAULT NULL,-- or DATETIME, depending on your needs
    -- add other fields related to the game if needed
    FOREIGN KEY(game_id) REFERENCES Game(id)
);
INSERT INTO Player (name, password) VALUES ('Alice', 'password1');
INSERT INTO Player (name, password) VALUES ('Bob', 'password2');
INSERT INTO Player (name, password) VALUES ('Charlie', 'password3');

-- Inserting sample games
INSERT INTO Game (running) VALUES (FALSE);
INSERT INTO Game (running) VALUES (FALSE);
INSERT INTO Game (running) VALUES (FALSE);

-- Assigning players to games
UPDATE Player SET current_game = 1 WHERE name = 'Alice';
UPDATE Player SET current_game = 2 WHERE name = 'Bob';
UPDATE Player SET current_game = 3 WHERE name = 'Charlie';
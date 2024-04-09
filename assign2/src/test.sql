DROP TABLE IF EXISTS Player;
CREATE TABLE Player (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    password TEXT NOT NULL,
    money REAL DEFAULT 40.0,
    current_game INTEGER DEFAULT NULL,
    curr_bet REAL DEFAULT 0.0,
    FOREIGN KEY(current_game) REFERENCES Game(id)
);
DROP TABLE IF EXISTS Game;
CREATE TABLE Game (
    id INTEGER PRIMARY KEY,
    crashed_time TIMESTAMP DEFAULT NULL-- or DATETIME, depending on your needs
    -- add other fields related to the game if needed
);

INSERT INTO Player (name, password) VALUES ('Alice', 'password1');
INSERT INTO Player (name, password) VALUES ('Bob', 'password2');
INSERT INTO Player (name, password) VALUES ('Charlie', 'password3');

-- Inserting sample games
INSERT INTO Game (crashed_time) VALUES ('2024-04-09 12:30:00');
INSERT INTO Game (crashed_time) VALUES ('2024-04-09 13:15:00');
INSERT INTO Game (crashed_time) VALUES ('2024-04-09 14:00:00');

-- Assigning players to games
UPDATE Player SET current_game = 1 WHERE name = 'Alice';
UPDATE Player SET current_game = 2 WHERE name = 'Bob';
UPDATE Player SET current_game = 3 WHERE name = 'Charlie';
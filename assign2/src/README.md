# Crash Game

## Server start up
### Linux
```bash
javac Server.java
#Server
java -classpath ".:sqlite-jdbc-3.45.2.0.jar:slf4j-api-2.0.13.jar" Server 8000 1
```
```bash
javac Client.java
#Client
java -cp '.:src' Client localhost 8000
```
### Windows
```bash
javac Server.java Player.java Connections.java Queue.java Game.java Player.java Database.java Pair.java
java -classpath ".;sqlite-jdbc-3.45.2.0.jar;slf4j-api-2.0.13.jar" Server 8000 1
```
```bash
javac Client.java Connections.java
java -cp '.;src' Client localhost 8000
```
### Tester
#### Requirements
- linux
- python
- python library **pexpect**
#### Tester usage
```
tester.py [Number of player simulated] [1 to casual or 2 to ranked]
```
- **warning** pexpect is used with subprocess to simulated multiples clients it doesnt handle well more that 4 clients the client is a little bit heavy
- the players to be used in the tester need to be created with the format playerN passwordN being N the number of the player it will use from player 1 to N players selected in the tester

## Hows does the game work
### Client
- after login the games ask what queue to select

```
1. Normal Queue (Player's rank doesn't matter)
2. Ranked Queue (Player's rank matters)
3. Exit
Enter your choice:
```
- after selecting the queue the game will put you on a waiting list
```
Waiting for game to start
```
- after the game starting it will ask you for a bet value
```
Playing Round
Enter your bet:
```
- and then a maximum multiplier to take out the money
```
Select multiplier: 
```
- when the round begins it will be something like this
```
1.0  Select Y to Bail:
```
- the number on the rigth is a multiplier and it will rise until it crashes if u want to take out the money early you can press Y and it will take the money early

#### I will leave the rest for the presentation


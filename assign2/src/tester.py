
import pexpect
import threading
import random
import time
import sys
players = []
def Client(d, game_mode):
    c = pexpect.spawn("java -cp '.:src' Client localhost 8000")

    c.expect('3. Exit', timeout=120)
    print("username")
    c.sendline('1')
    c.expect('Enter your Existing Username: ', timeout=30)
    #c.sendline('2')
    #c.expect('Enter your new Username: ', timeout=2)
    print("player"+str(d))
    c.sendline("player"+str(d))
    c.expect('Enter your Existing Password: ', timeout=30)
    #c.expect('Enter your new Password: ', timeout=2)
    print("password"+str(d))
    c.sendline("password"+str(d))
    print("ok1")
    c.expect('Login Successful', timeout=60)
    print("ok")
    c.expect(b"\n3. Exit\r\nEnter your choice: ", timeout=1)
    print("game_mode")
    c.sendline(str(game_mode))
    print("game started")
    for i in range(1, 4):
        print("round "+str(i))
        c.expect('Enter your bet: ', timeout=120)
        print("send bet")
        c.sendline("10")
        c.expect('Select multiplier: ', timeout=120)
        print("send multiplier")
        c.sendline("2")
    c.expect('Game Ended', timeout=120)
    print("game ended")
    players.append(c)
    #c.kill(1)
    print('is alive:', c.isalive())
#Client(sys.argv[1], 1)
# Execute functions in parallel (limited by GIL)
def multiTreading(start,users):
    threads = []
    for i in range(start,users):
        d = i + 1  # Numbers from 1 to 30
        #game_mode = random.randint(1,2)  # Replace with actual game mode values
        thread = threading.Thread(target=Client, args=(d, int(sys.argv[2])))
        threads.append(thread)
        thread.start()
        time.sleep(0.5)

    # Wait for all threads to finish (optional)
    for thread in threads:
        thread.join()
    print("All clients finished!")
print('Running')
if(sys.argv[2] != '1' and sys.argv[2] != '2'):
    print("Invalid game mode 1 casual or 2 ranked")
if(int(sys.argv[1]) <= 0):
    print("Invalid number of clients")
multiTreading(0,int(sys.argv[1]))





"""

game_command = "java -cp '.:src' Client localhost 8000"  # Replace if needed
game_process = pexpect.spawn(game_command, searchwindowsize=1024)

# Send simulated user input (replace with your commands)
time.sleep(2)
#game_process.sendline("choice1")

game_output = game_process.expect('', timeout=5)


if game_output:
  print("Connection error detected by client:")
  print(game_output)
else:
  print("Client did not report connection error within timeout.")
#game_process.sendline("choice2")

# Optionally, wait for specific prompts before sending further input
# game_process.expect("Enter your choice: ")
# ...

# Interact with the game and capture output from game_process
# ...
"""
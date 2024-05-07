
import pexpect
import threading
import random
import time
players = []
def Client(d, game_mode):
    c = pexpect.spawn("java -cp '.:src' Client localhost 8000")

    c.expect('3. Exit', timeout=120)
    print('And now for something completely different...')
    c.sendline('1')
    c.expect('Enter your Username: ', timeout=120)
    time.sleep(2)
    print("player"+str(d))
    c.sendline("player"+str(d))
    c.expect('Enter your Password: ', timeout=120)
    print("password"+str(d))
    time.sleep(2)
    c.sendline("password"+str(d))
    c.expect('Login Successful', timeout=120)
    print("ok")
    c.expect(b'Type of game:\r\nChoose the type of queue you want to join\r\n1. Normal Queue\r\n2. Ranked Queue\r\n3. Exit', timeout=120)
    c.sendline(str(game_mode))
    c.expect('Waiting for game to start')
    print((c.before))
    print(c.after, end=' ')
    players.append(c)
    #c.kill(1)
    print('is alive:', c.isalive())
# Execute functions in parallel (limited by GIL)
def multiTreading(start,users):
    threads = []
    for i in range(start,users):
        d = i + 1  # Numbers from 1 to 30
        game_mode = random.randint(1,2)  # Replace with actual game mode values
        thread = threading.Thread(target=Client, args=(d, game_mode))
        threads.append(thread)
        thread.start()
        #time.sleep(0.5)

    # Wait for all threads to finish (optional)
    for thread in threads:
        thread.join()
    print("All clients finished!")
multiTreading(0,4)





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

import pexpect

c = pexpect.spawn("java -cp '.:src' Client localhost 8000")

c.expect('3. Exit')
print('And now for something completely different...')
c.sendline('1')
c.expect('Enter your Username: ')
c.sendline('player2')
c.expect('Enter your Password: ')
c.sendline('password2')
c.expect('Login Successful')
print("ok")
print((c.before))
print(c.after, end=' ')

c.kill(1)
print('is alive:', c.isalive())

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
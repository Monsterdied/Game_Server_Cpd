import time
f = open("time.txt", "w")
print("Test Mult Normal :\ns")
for i in range(600,3001,400):
    New_start_time = time.time()
    matrixc = [[0.0 for _ in range(i)] for _ in range(i)]
    end_time = time.time()
    passedT= end_time-New_start_time
    f.write(str(i)+"  "+str(passedT)+"\n")
f.close()
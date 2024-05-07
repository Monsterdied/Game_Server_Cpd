import time
def NormalMult(matrixA,matrixB,ma,mb):
    matrixc = [[0.0 for _ in range(ma)] for _ in range(ma)]
    for i in range(ma):
        for j in range(mb):
            temp = 0.0
            for k in range(ma):
                temp += matrixA[i][k] * matrixB[k][j]
            matrixc[i][j] = temp
    print("Matrix C:")
    for i in range(10):
        print(matrixc[0][i], end=" ")
    print()
    #for i in range(ma):
    #    for j in range(mb):
    #        print(matrixc[i*ma + j], end=" ")
    #    print()

def LineByLineMult(matrixA,matrixB,ma,mb):
    matrixc = [[0.0 for _ in range(ma)] for _ in range(ma)]
    for i in range(ma):
        for k in range(ma):
            aiK = matrixA[i][k] #cache
            matrixBk = matrixB[k]
            columnCache = matrixc[i]
            for j in range(mb):
                columnCache[j]  +=  aiK* matrixBk[j]
            matrixc[i] = columnCache
    for i in range(10):
        print(matrixc[0][i], end=" ")
    print()
    #for i in range(ma):
    #    for j in range(mb):
    #        print(matrixc[i*ma + j], end=" ")
    #    print()








print("Hello, I am matrix_mult.py")
print("Menu:")
print("1. Multiply two matrices Normal")
print("2. Multiply two matrices line by line")
print("3. Testing Mode")
mode = int(input("Enter the mode: "))
if(mode !=3):
    print("Matrix size lins=cols:")
    n = int(input())
    mb= n
    ma = n
    matrixa =[[1.0 for i in range(ma)] for i in range(ma)]

    matrixb =[[1.0  + i for j in range(ma)] for i in range(ma)]
start_time = time.time()
match mode:
    case 1:
        NormalMult(matrixa, matrixb,ma,mb)
    case 2:
        LineByLineMult(matrixa, matrixb,ma,mb)
    case 3:
        f = open("results.txt", "w")
        f.write("Test Mult Normal :\n")
        for i in range(600,3001,400):
            print("current size:" + str(i))
            matrixa =[[1.0 for a in range(i)] for a in range(i)]
            matrixb =[[1.0  + e for a in range(i)] for e in range(i)]
            New_start_time = time.time()
            NormalMult(matrixa, matrixb,i,i)
            New_end_time = time.time()
            passedT= New_end_time - New_start_time
            print("time elapsed :" ,passedT)
            f.write(str(i)+"  " +str(passedT) + "\n")

            New_start_time = time.time()
            NormalMult(matrixa, matrixb,i,i)
            New_end_time = time.time()
            passedT= New_end_time-New_start_time
            print("time elapsed :" + str(passedT))
            f.write(str(i)+"  "+str(passedT)+"\n")
        f.close()
    case _:
        print("Invalid mode")
end_time = time.time()

print("Execution time: ", end_time-start_time)

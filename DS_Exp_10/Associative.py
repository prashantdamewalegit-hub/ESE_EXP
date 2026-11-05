import numpy as np
# Auto Associative

x=[-1,1,1,1]
w=[
    [0,0,0,0],
    [0,0,0,0],
    [0,0,0,0],
    [0,0,0,0]
]
for i in range(len(x)):
    for j in range(len(x)):
        w[i][j]=x[i]*x[j]

x1=[1,1,1,1]
for i in range(len(w[0])):
    sum=0
    for j in range(len(x1)):
        sum+=x1[j]*w[j][i]

    y=1 if(sum>0) else -1
    print(y,end=" ")


# Hetero Associative
x=[1,1,0,0]

S=[
    [1,0,0,0],
    [1,1,0,0],
    [0,0,0,1],
    [0,0,1,1]
]
St=np.transpose(S)
t=[
    [0,1],
    [0,1],
    [1,0],
    [1,0]
]
W=St @ t
print(np.array(W))
yin=x @ W
for i in range(len(yin)):
    if(yin[i]>0):
        yin[i]=1
    else:
        yin[i]=0
print(np.array(yin))


E = [
    1, 1, 1, 1, -1, -1, 1, 1, 1,
    1, -1, -1, 1, 1, 1
]

t1 = [-1, 1]

F = [
    1, 1, 1, 1, 1, 1, 1, -1, -1, 1,
    -1, -1, 1, -1, -1
]

t2 = [1, 1]
w = [[0 for j in range(2)] for i in range(15)]

for i in range(15):
    for j in range(2):
        w[i][j]+=E[i]*t1[j]


for i in range(15):
    for j in range(2):
        w[i][j]+=F[i]*t2[j]

wt=np.transpose(w)

t=np.array(E) @ np.array(w)

print((t))
E= np.array(t1) @ wt
print(np.array(E))
t=np.array(F) @ np.array(w)
print((t))

F= np.array(t2) @ wt
print(np.array(F))


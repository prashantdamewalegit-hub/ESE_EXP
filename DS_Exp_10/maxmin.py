import numpy as np

A=[
    [0.2,0.5],
    [0.7,0.4]
]
B=[
    [0.6,0.8],
    [0.3,0.9]
]
c=[]
for i in range(2):
    row=[]
    for j in range(2):
        value=[]
        for k in range(2):
            value.append(min(A[i][k],B[k][j]))

        row.append(max(value))
    c.append(row)

print(np.array(c))

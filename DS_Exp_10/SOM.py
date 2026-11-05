x=[0.2,0.4]
w = [
    [0.3, 0.2, 0.1, 0.8, 0.4],
    [0.5, 0.6, 0.7, 0.9, 0.2]
]

alpha=0.2

rows=len(x)
cols=len(w[0])
winner =-1

minDistance=float("inf")

for j in range(cols):
    distance=0
    for i in range(rows):
        distance+=(w[i][j]-x[i])**2
    print("Distance of Cluster", j + 1, ":", round(distance, 4))

    if(minDistance>distance):
        minDistance=distance
        winner=j


for i in range(rows):
    w[i][winner]+=alpha*(x[i]-w[i][winner])


print("\nWinning Unit:", winner + 1)

print("\nUpdated Weight Matrix:")

for row in range(rows):
    for col in range(cols):
        print(w[row][col])
   
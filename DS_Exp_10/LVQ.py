W=[
    [0,0],
    [0,1],
    [1,0],
    [1,0]
]

classLabels=[1,2]

inputs=[
    [0,0,0,1],
    [1,1,0,0],
    [0,1,1,0]
]

target=[2,1,1]
alpha=0.1

features=len(inputs[0])
classes=len(classLabels)
for s in range(len(inputs)):
    winner=0
    minDistance=float("inf")
    for j in range(classes):
        distance=0
        for i in range(features):
            diff=W[i][j]-inputs[s][i]
            distance+=diff*diff
        if(minDistance>distance):
            minDistance=distance
            winner=j
    print("\nPattern:", inputs[s])
    print("Target Class:", target[s])
    print("Winning Class:", classLabels[winner])

    for i in range(features):
        if(classLabels[winner]==target[s]):
            W[i][winner]+=alpha*(inputs[s][i]-W[i][winner])
        else:
            W[i][winner]-=alpha*(inputs[s][i]-W[i][winner])


for row in W:
    print([round(value, 2) for value in row])



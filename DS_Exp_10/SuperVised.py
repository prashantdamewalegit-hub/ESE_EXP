
import math

def sigmoid(x):
    return 1 / (1 + math.exp(-x))


alpha = 0.25

v11 = 0.6
v21 = -0.1
v01 = 0.3

v12 = -0.3
v22 = 0.4
v02 = 0.5

w1 = 0.4
w2 = 0.1
w0 = -0.2

inputs = [
    [0.10, 0.35],
    [0.80, 0.90],
    [0.20, 0.50],
    [0.90, 0.70]
]

targets = [0, 1, 0, 1]

epochs = 100

# Training
for epoch in range(1, epochs + 1):

    for p in range(len(inputs)):

        x1 = inputs[p][0]
        x2 = inputs[p][1]
        t = targets[p]

        # Forward Propagation
        z11 = v01 + v11*x1 + v21*x2
        z1 = sigmoid(z11)

        z22 = v02 + v12*x1 + v22*x2
        z2 = sigmoid(z22)

        yin = w0 + w1*z1 + w2*z2
        y = sigmoid(yin)

        # Backward Propagation
        deltaY = (t-y) * y * (1-y)

        oldW1 = w1
        oldW2 = w2

        w1 = w1 + alpha*deltaY*z1
        w2 = w2 + alpha*deltaY*z2
        w0 = w0 + alpha*deltaY

        deltaZ1 = oldW1 * deltaY * z1 * (1-z1)
        deltaZ2 = oldW2 * deltaY * z2 * (1-z2)

        v11 += alpha * deltaZ1 * x1
        v21 += alpha * deltaZ1 * x2
        v01 += alpha * deltaZ1

        v12 += alpha * deltaZ2 * x1
        v22 += alpha * deltaZ2 * x2
        v02 += alpha * deltaZ2


# Display Predictions
print("Predictions")

for p in range(len(inputs)):

    x1 = inputs[p][0]
    x2 = inputs[p][1]

    # Forward Propagation
    z11 = v01 + v11*x1 + v21*x2
    z1 = sigmoid(z11)

    z22 = v02 + v12*x1 + v22*x2
    z2 = sigmoid(z22)

    yin = w0 + w1*z1 + w2*z2
    y = sigmoid(yin)

    print("\nInput:", inputs[p])
    print("Actual Output:", targets[p])
    print("Predicted Output:", round(y, 4))

    if y > 0.5:
        print("Pass")
    else:
        print("Fail")


# Test a new input
x1 = 0.8
x2 = 0.9

z11 = v01 + v11*x1 + v21*x2
z1 = sigmoid(z11)

z22 = v02 + v12*x1 + v22*x2
z2 = sigmoid(z22)

yin = w0 + w1*z1 + w2*z2
y = sigmoid(yin)

print("\nNew Input Prediction")
print("Input:", x1, x2)
print("Output:", round(y, 4))

if y > 0.5:
    print("Pass")
else:
    print("Fail")

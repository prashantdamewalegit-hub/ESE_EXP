# Single Layer Perceptron

x1 = [-1, -1, 1, 1]
x2 = [-1, 1, -1, 1]
t = [-1, 1, 1, -1]

w1 = 1
w2 = 0
b = 1
alpha = 1

for epoch in range(1, 51):

    oldW1 = w1
    oldW2 = w2
    oldB = b

    print("Epoch", epoch)
    print("x1\tx2\tt\tyin\ty\tw1\tw2\tb")

    for i in range(4):

        yin = x1[i]*w1 + x2[i]*w2 + b

        y = 0

        if yin < 0:
            y = -1

        if yin > 0:
            y = 1

        if y != t[i]:
            w1 += alpha*t[i]*x1[i]
            w2 += alpha*t[i]*x2[i]
            b += alpha*t[i]

        print(x1[i], "\t", x2[i], "\t", t[i], "\t",
              yin, "\t", y, "\t", w1, "\t", w2, "\t", b)

    if oldW1 == w1 and oldW2 == w2 and oldB == b:
        print("\nNo change in weights")
        print("Finished at epoch", epoch)
        break
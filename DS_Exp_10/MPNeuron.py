# McCulloch-Pitts Neuron

def neuron(x1, x2, w1, w2, thre):
    yin = x1*w1 + x2*w2

    if yin >= thre:
        y = 1
    else:
        y = 0

    return y


# AND Gate
def AND(x1, x2):
    y = neuron(x1, x2, 1, 1, 2)
    return y


# XOR Gate
def XOR(x1, x2):
    z1 = neuron(x1, x2, -1, 1, 1)
    z2 = neuron(x1, x2, 1, -1, 1)

    y = neuron(z1, z2, 1, 1, 1)
    return y


# Half Adder
def halfAdder(A, B):
    sum = XOR(A, B)
    carry = AND(A, B)

    print(A, "\t", B, "\t", sum, "\t", carry)


# Full Adder
def fullAdder(A, B, C):
    sum = XOR(XOR(A, B), C)

    carry1 = AND(A, B)
    carry2 = AND(A, C)
    carry3 = AND(C, B)

    carry = 0

    if carry1 + carry2 + carry3 >= 1:
        carry = 1

    print(A, "\t", B, "\t", sum, "\t", carry)


# Main Program

print("AND Gate")
print("x1\tx2\ty")

for i in range(2):
    for j in range(2):
        y = AND(i, j)
        print(i, "\t", j, "\t", y)


print("\nXOR Gate")
print("x1\tx2\ty")

for i in range(2):
    for j in range(2):
        y = XOR(i, j)
        print(i, "\t", j, "\t", y)


print("\nHalf Adder")
print("A\tB\tsum\tcarry")

for i in range(2):
    for j in range(2):
        halfAdder(i, j)


print("\nFull Adder")
print("A\tB\tC\tsum\tcarry")

for i in range(2):
    for j in range(2):
        for k in range(2):
            fullAdder(i, j, k)
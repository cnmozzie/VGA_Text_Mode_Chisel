import numpy as np
n = np.zeros(2304)
font = open("ConsoleFont.raw", "rb")
fo = open("decode_2.txt", "w")
for i in range(16):
    for j in range(2304):
        x = font.read(1)
        if (x[0] == 1):
            n[j] = n[j] + 2**(15-i)

for i in range(256):
    for j in range(9):
        if j < 8:
            x = int(n[i*9+j]).to_bytes(length=2, byteorder='big',signed=False)
            fo.write(x.hex())
    fo.write(" ")
font.close()
fo.close()
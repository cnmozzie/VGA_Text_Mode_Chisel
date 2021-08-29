font = open("ConsoleFont.raw", "rb")
fo = open("decode.txt", "w")
for i in range(16):
    for j in range(2304):
        x = font.read(1)
        if (x[0] == 0):
            fo.write(" ")
        else:
            fo.write("#")
    fo.write("\n")
font.close()
fo.close()
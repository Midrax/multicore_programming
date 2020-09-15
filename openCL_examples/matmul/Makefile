CC = gcc
CFLAGS = -O3 -framework OpenCL
CLUT = ../clut/
OBJ = $(CLUT)clut.o

matmul: matmul.c
	$(CC) $(CFLAGS) -I$(CLUT) matmul.c $(OBJ) -o matmul 

.phony: clean

clean:
	rm -f matmul 

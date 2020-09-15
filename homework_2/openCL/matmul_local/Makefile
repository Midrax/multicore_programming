CC = gcc
CFLAGS = -O3 -framework OpenCL
CLUT = ../clut/
OBJ = $(CLUT)clut.o

matmul_local: matmul_local.c
	$(CC) $(CFLAGS) -I$(CLUT) matmul_local.c $(OBJ) -o matmul_local 

.phony: clean

clean:
	rm -f matmul_local 

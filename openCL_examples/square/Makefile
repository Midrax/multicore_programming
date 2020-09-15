CC = gcc
CFLAGS = -O3 -framework OpenCL

square: square.c
	$(CC) $(CFLAGS) square.c -o square

.phony: clean

clean:
	rm -f square

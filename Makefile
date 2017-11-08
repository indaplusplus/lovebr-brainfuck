CC=clang
CFLAGS=-g `llvm-config --cflags`
LD=clang++
LDFLAGS=`llvm-config --cxxflags --ldflags --libs core executionengine interpreter analysis native bitwriter --system-libs`

all: bfc

bfc.o: bfc.c
	$(CC) $(CFLAGS) -c $<

bfc: bfc.o
	$(LD) $< $(LDFLAGS) -o $@

clean:
	-rm -f sum.o sum sum.bc

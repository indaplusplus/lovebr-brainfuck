## Usage

### Interpreter
```
bfr hello_world.bf <memory_size>
```
`<memory_size>` is the size of the memory in bytes (default: 1024).

### Transpiler (C)
```
bfc hello_world.bf hello_world.c <memory_size>
```
`<memory_size>` is the size of the memory in bytes (default: 1024).

### Compiler (Java Bytecode)
```
java Compiler hello_world.bf <memory_size>
```
`<memory_size>` is the size of the memory in bytes (default: 1024).

## Benchmarks

### Interpreter
```
mandelbrot.bf: 73.042000 seconds
```

### Transpiler (C)
```
mandelbrot.bf: 1.156250 seconds
```

### Compiler (Java Bytecode)
```
mandelbrot.bf: 23.962 seconds
```

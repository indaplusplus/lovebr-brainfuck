#include <stdio.h>
#include <stdlib.h>
#include <time.h>

int main(int argc, char const *argv[]) {
    clock_t start = clock();
    if (argc < 2) {
        printf("ERROR: Too few arguments!\n");
        return 1;
    }
    FILE* fp = fopen(argv[1], "r");
    if (!fp) {
        printf("ERROR: Input file not found!\n");
        return 2;
    }
    fseek(fp, 0, SEEK_END);
    long fsize = ftell(fp);
    rewind(fp);
    char* code = malloc(fsize);
    int* jumps = calloc(fsize, 4);
    fread(code, fsize, 1, fp);
    fclose(fp);
    int mem_size = argc > 2 ? atoi(argv[2]) : 1024;
    int ptr = 0;
    char* memory = calloc(argc > 2 ? atoi(argv[2]) : 1024, 1);
    for(int c_ptr = 0; c_ptr < fsize; ++c_ptr) {
        switch (code[c_ptr]) {
        case '>':
            if(++ptr == mem_size) {
                printf("ERROR: Moving outside memory!\n");
                return 3;
            }
            break;
        case '<':
            if(--ptr == -1) {
                printf("ERROR: Moving outside memory!\n");
                return 3;
            }
            break;
        case '+':
            ++memory[ptr];
            break;
        case '-':
            --memory[ptr];
            break;
        case '.': putchar(memory[ptr]);
            break;
        case ',': memory[ptr]=getchar();
            break;
        case '[':
            if(!memory[ptr]) {
                if (jumps[c_ptr]) {
                    c_ptr = jumps[c_ptr] - 1;
                } else {
                    int c_ptr_start = c_ptr;
                    int open = 1;
                    while (open > 0 && ++c_ptr < fsize) {
                        if (code[c_ptr] == ']') {
                            --open;
                        } else if(code[c_ptr] == '[') {
                            ++open;
                        }
                    }
                    if(open) {
                        printf("ERROR: Brackets does not match!\n");
                        return 4;
                    }
                    jumps[c_ptr_start] = c_ptr + 1;
                    jumps[c_ptr] = c_ptr_start + 1;
                }
            }
            break;
        case ']':
            if(memory[ptr]) {
                if (jumps[c_ptr]) {
                    c_ptr = jumps[c_ptr] - 1;
                } else {
                    int c_ptr_start = c_ptr;
                    int open = 1;
                    while (open > 0 && --c_ptr >= 0) {
                        if (code[c_ptr] == '[') {
                            --open;
                        } else if(code[c_ptr] == ']') {
                            ++open;
                        }
                    }
                    if(open) {
                        printf("ERROR: Brackets does not match!\n");
                        return 4;
                    }
                    jumps[c_ptr_start] = c_ptr + 1;
                    jumps[c_ptr] = c_ptr_start + 1;
                }
            }
            break;
        }
    }
    printf("Execution took %f seconds.\n", (float)(clock() - start) / CLOCKS_PER_SEC);
    free(code);
    free(jumps);
    free(memory);
    return 0;
}

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main(int argc, char const *argv[]) {
    if (argc < 3) {
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
    FILE* fpo = fopen(argv[2], "w");
    if (!fpo) {
        printf("ERROR: Could not write!\n");
        return 3;
    }
    char* code = malloc(fsize);
    fread(code, fsize, 1, fp);
    fclose(fp);
    int mem_size = argc > 3 ? atoi(argv[3]) : 1024;
    fputs("#include <stdio.h>\n#include <stdlib.h>\n#include <time.h>\nint main(int argc, char const *argv[]) {\nclock_t start = clock();\nchar* ptr = calloc(", fpo);
    fprintf(fpo, "%d", mem_size);
    fputs(", 1);\n", fpo);
    int occ;
    for(int c_ptr = 0; c_ptr < fsize; ++c_ptr) {
        switch (code[c_ptr]) {
        case '>':
            occ = 0;
            while (code[c_ptr] == '>') {
                occ++;
                c_ptr++;
            }
            c_ptr--;
            if (occ > 1) {
                fputs("ptr+=", fpo);
                fprintf(fpo, "%d", occ);
                fputs(";\n", fpo);
            } else {
                fputs("++ptr;\n", fpo);
            }
            break;
        case '<':
            occ = 0;
            while (code[c_ptr] == '<') {
                occ++;
                c_ptr++;
            }
            c_ptr--;
            if (occ > 1) {
                fputs("ptr-=", fpo);
                fprintf(fpo, "%d", occ);
                fputs(";\n", fpo);
            } else {
                fputs("--ptr;\n", fpo);
            }
            break;
        case '+':
            occ = 0;
            while (code[c_ptr] == '+') {
                occ++;
                c_ptr++;
            }
            c_ptr--;
            if (occ > 1) {
                fputs("*ptr+=", fpo);
                fprintf(fpo, "%d", occ);
                fputs(";\n", fpo);
            } else {
                fputs("++*ptr;\n", fpo);
            }
            break;
        case '-':
            occ = 0;
            while (code[c_ptr] == '-') {
                occ++;
                c_ptr++;
            }
            c_ptr--;
            if (occ > 1) {
                fputs("*ptr-=", fpo);
                fprintf(fpo, "%d", occ);
                fputs(";\n", fpo);
            } else {
                fputs("--*ptr;\n", fpo);
            }
            break;
        case '.': fputs("putchar(*ptr);\n", fpo);
            break;
        case ',': fputs("*ptr=getchar();\n", fpo);
            break;
        case '[':
            fputs("while(*ptr){\n", fpo);
            break;
        case ']':
            fputs("}\n", fpo);
            break;
        }
    }
    fputs("printf(\"Execution took %f seconds.\\n\", (float)(clock() - start) / CLOCKS_PER_SEC);\nfree(ptr);\nreturn 0;\n}", fpo);
    fclose(fpo);
    free(code);
    return 0;
}

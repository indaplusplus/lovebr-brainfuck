#include <stdio.h>
#include <stdlib.h>

int main(int argc, char const *argv[]) {
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
		fread(code, fsize, 1, fp);
		fclose(fp);
		char* ptr = calloc(argc > 2 ? atoi(argv[2]) : 1000, 1);
		for(int c_ptr = 0; c_ptr < fsize; ++c_ptr) {
				switch (code[c_ptr]) {
				case '>': ++ptr;
						break;
				case '<': --ptr;
						break;
				case '+':
						if(*ptr == 127) {
								printf("ERROR: Invalid byte value!\n");
								return 3;
						}
						++*ptr;
						break;
				case '-':
						if(!*ptr) {
								printf("ERROR: Invalid byte value!\n");
								return 3;
						}
						--*ptr;
						break;
				case '.': putchar(*ptr);
						break;
				case ',': *ptr=getchar();
						break;
				case '[':
						if(!*ptr) {
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
						}
						break;
				case ']':
						if(*ptr) {
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
						}
						break;
				}
		}
		return 0;
}

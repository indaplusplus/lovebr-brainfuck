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
		char* ptr = calloc(argc > 2 ? atoi(argv[2]) : 1000, 1);
		int c;
		while ((c = fgetc(fp)) != EOF) {
				switch (c) {
				case '>': ++ptr;
						break;
				case '<': --ptr;
						break;
				case '+': ++*ptr;
						break;
				case '-': --*ptr;
						break;
				case '.': putchar(*ptr);
						break;
				case ',': *ptr=getchar();
						break;
				case '[':
						break;
				case ']':
						break;
				}
		}
		return 0;
}

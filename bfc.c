#include <inttypes.h>
#include <llvm-c/Core.h>
#include <llvm-c/Analysis.h>
#include <llvm-c/BitWriter.h>
#include <stdio.h>
#include <stdlib.h>

int main(int argc, char const *argv[]) {
    LLVMModuleRef mod = LLVMModuleCreateWithName("main");
    LLVMValueRef main = LLVMAddFunction(mod, "main", LLVMFunctionType(LLVMInt32Type(), (LLVMTypeRef[]) {NULL}, 0, 0));
    LLVMBasicBlockRef entry = LLVMAppendBasicBlock(main, "entry");
    LLVMBuilderRef builder = LLVMCreateBuilder();
    LLVMPositionBuilderAtEnd(builder, entry);
    LLVMValueRef tmp = LLVMBuildAdd(builder, LLVMConstInt(LLVMInt32Type(), 2, 1), LLVMConstInt(LLVMInt32Type(), 2, 1), "tmp");
    LLVMValueRef pc = LLVMAddFunction(mod, "putchar", LLVMFunctionType(LLVMInt32Type(), (LLVMTypeRef[]) {LLVMInt32Type()}, 1, 0));
    LLVMBuildCall(builder, pc, (LLVMValueRef[]) {LLVMConstInt(LLVMInt32Type(), 53, 1)}, 1, "name");
    LLVMBuildRet(builder, LLVMConstInt(LLVMInt32Type(), 0, 1));
    char *error = NULL;
    LLVMVerifyModule(mod, LLVMAbortProcessAction, &error);
    LLVMDisposeMessage(error);
    if (LLVMWriteBitcodeToFile(mod, "bfc.bc") != 0) {
        printf("ERROR: Could not save to file!\n");
    }
    LLVMDisposeBuilder(builder);
}

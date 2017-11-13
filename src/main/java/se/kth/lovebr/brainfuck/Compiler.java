package se.kth.lovebr.brainfuck;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;
import org.apache.bcel.Const;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.CALOAD;
import org.apache.bcel.generic.CASTORE;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.IADD;
import org.apache.bcel.generic.IALOAD;
import org.apache.bcel.generic.IASTORE;
import org.apache.bcel.generic.ICONST;
import org.apache.bcel.generic.IFEQ;
import org.apache.bcel.generic.IFGT;
import org.apache.bcel.generic.IINC;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.ISTORE;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.NEWARRAY;
import org.apache.bcel.generic.POP;
import org.apache.bcel.generic.RETURN;
import org.apache.bcel.generic.Type;

public class Compiler {
  public static void main(String[] args) throws IOException {
    if (args.length < 1) {
      System.out.println("ERROR: Too few arguments!");
      return;
    }
    String baseName = args[0].replaceFirst("\\..*", "");
    ClassGen classGen =
        new ClassGen(baseName, "java.lang.Object", baseName + ".java", Const.ACC_PUBLIC, null);
    ConstantPoolGen constantPoolGen = classGen.getConstantPool();
    InstructionList instructionList = new InstructionList();
    instructionList.append(
        new LDC(constantPoolGen.addInteger(args.length > 1 ? Integer.parseInt(args[1]) : 1024)));
    instructionList.append(new NEWARRAY(BasicType.INT));
    instructionList.append(new ASTORE(0));
    instructionList.append(new ICONST(0));
    instructionList.append(new ISTORE(1));
    String code = new String(Files.readAllBytes(Paths.get(args[0])));
    Instruction out =
        new GETSTATIC(
            constantPoolGen.addFieldref("java.lang.System", "out", "Ljava/io/PrintStream;"));
    Instruction print =
        new INVOKEVIRTUAL(constantPoolGen.addMethodref("java.io.PrintStream", "print", "(C)V"));
    Stack<BranchHandle> braces = new Stack<>();
    for (int i = 0; i < code.length(); i++) {
      switch (code.charAt(i)) {
        case '>':
          instructionList.append(new IINC(1, 1));
          break;
        case '<':
          instructionList.append(new IINC(1, -1));
          break;
        case '+':
          instructionList.append(new ALOAD(0));
          instructionList.append(new ILOAD(1));
          instructionList.append(new ALOAD(0));
          instructionList.append(new ILOAD(1));
          instructionList.append(new IALOAD());
          instructionList.append(new ICONST(1));
          instructionList.append(new IADD());
          instructionList.append(new IASTORE());
          break;
        case '-':
          instructionList.append(new ALOAD(0));
          instructionList.append(new ILOAD(1));
          instructionList.append(new ALOAD(0));
          instructionList.append(new ILOAD(1));
          instructionList.append(new IALOAD());
          instructionList.append(new ICONST(-1));
          instructionList.append(new IADD());
          instructionList.append(new IASTORE());
          break;
        case '.':
          /*instructionList.append(load);
          instructionList.append(locstore);
          instructionList.append(out);
          instructionList.append(locload);
          instructionList.append(print);
          instructionList.append(pop);*/
          break;
        case ',':
          break;
        case '[':
          instructionList.append(new ALOAD(0));
          instructionList.append(new ILOAD(1));
          instructionList.append(new IALOAD());
          braces.push(instructionList.append(new IFEQ(null)));
          break;
        case ']':
          instructionList.append(new ALOAD(0));
          instructionList.append(new ILOAD(1));
          instructionList.append(new IALOAD());
          InstructionHandle end = instructionList.append(new IFGT(braces.peek()));
          braces.pop().setTarget(end);
          break;
        default:
      }
    }
    instructionList.append(new RETURN());
    MethodGen methodGen =
        new MethodGen(
            Const.ACC_PUBLIC | Const.ACC_STATIC,
            Type.VOID,
            new Type[] {new ArrayType(Type.STRING, 1)},
            new String[] {"args"},
            "main",
            baseName,
            instructionList,
            constantPoolGen);
    methodGen.setMaxLocals();
    methodGen.setMaxStack();
    classGen.addMethod(methodGen.getMethod());
    classGen.getJavaClass().dump(baseName + ".class");
  }
}

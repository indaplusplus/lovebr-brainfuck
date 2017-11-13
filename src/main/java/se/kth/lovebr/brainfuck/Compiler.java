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
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.DDIV;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.I2C;
import org.apache.bcel.generic.IADD;
import org.apache.bcel.generic.IALOAD;
import org.apache.bcel.generic.IASTORE;
import org.apache.bcel.generic.ICONST;
import org.apache.bcel.generic.IFGT;
import org.apache.bcel.generic.IINC;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.ISTORE;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.L2D;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.LDC2_W;
import org.apache.bcel.generic.LLOAD;
import org.apache.bcel.generic.LSTORE;
import org.apache.bcel.generic.LSUB;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.NEWARRAY;
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
    Instruction printChar =
        new INVOKEVIRTUAL(constantPoolGen.addMethodref("java.io.PrintStream", "print", "(C)V"));
    final Instruction printString =
        new INVOKEVIRTUAL(
            constantPoolGen.addMethodref(
                "java.io.PrintStream", "println", "(Ljava/lang/String;)V"));
    final Instruction printDouble =
        new INVOKEVIRTUAL(constantPoolGen.addMethodref("java.io.PrintStream", "print", "(D)V"));
    Instruction time =
        new INVOKESTATIC(
            constantPoolGen.addMethodref("java.lang.System", "currentTimeMillis", "()J"));
    instructionList.append(out);
    instructionList.append(time);
    Stack<BranchHandle> braces = new Stack<>();
    for (int i = 0; i < code.length(); i++) {
      switch (code.charAt(i)) {
        case '>':
          int occurrences = 1;
          while (code.charAt(i + 1) == '>') {
            occurrences++;
            i++;
          }
          instructionList.append(new IINC(1, occurrences));
          break;
        case '<':
          occurrences = 1;
          while (code.charAt(i + 1) == '<') {
            occurrences++;
            i++;
          }
          instructionList.append(new IINC(1, -occurrences));
          break;
        case '+':
          occurrences = 1;
          while (code.charAt(i + 1) == '+') {
            occurrences++;
            i++;
          }
          instructionList.append(new ALOAD(0));
          instructionList.append(new ILOAD(1));
          instructionList.append(new ALOAD(0));
          instructionList.append(new ILOAD(1));
          instructionList.append(new IALOAD());
          instructionList.append(new LDC(constantPoolGen.addInteger(occurrences)));
          instructionList.append(new IADD());
          instructionList.append(new IASTORE());
          break;
        case '-':
          occurrences = 1;
          while (code.charAt(i + 1) == '-') {
            occurrences++;
            i++;
          }
          instructionList.append(new ALOAD(0));
          instructionList.append(new ILOAD(1));
          instructionList.append(new ALOAD(0));
          instructionList.append(new ILOAD(1));
          instructionList.append(new IALOAD());
          instructionList.append(new LDC(constantPoolGen.addInteger(-occurrences)));
          instructionList.append(new IADD());
          instructionList.append(new IASTORE());
          break;
        case '.':
          instructionList.append(out);
          instructionList.append(new ALOAD(0));
          instructionList.append(new ILOAD(1));
          instructionList.append(new IALOAD());
          instructionList.append(new I2C());
          instructionList.append(printChar);
          break;
        case ',':
          break;
        case '[':
          braces.push(instructionList.append(new GOTO(null)));
          break;
        case ']':
          final InstructionHandle end = instructionList.append(new ALOAD(0));
          instructionList.append(new ILOAD(1));
          instructionList.append(new IALOAD());
          instructionList.append(new IFGT(braces.peek().getNext()));
          braces.pop().setTarget(end);
          break;
        default:
      }
    }
    instructionList.append(new LSTORE(2));
    instructionList.append(time);
    instructionList.append(new LLOAD(2));
    instructionList.append(new LSUB());
    instructionList.append(new L2D());
    instructionList.append(new LDC2_W(constantPoolGen.addDouble(1000)));
    instructionList.append(new DDIV());
    instructionList.append(out);
    instructionList.append(new LDC(constantPoolGen.addString("Execution took ")));
    instructionList.append(printString);
    instructionList.append(printDouble);
    instructionList.append(out);
    instructionList.append(new LDC(constantPoolGen.addString(" seconds.\n")));
    instructionList.append(printString);
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

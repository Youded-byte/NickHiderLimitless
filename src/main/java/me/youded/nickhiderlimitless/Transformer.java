package me.youded.nickhiderlimitless;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.*;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class Transformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (classfileBuffer == null || classfileBuffer.length == 0) {
            return new byte[0];
        }

        if (!className.startsWith("lunar/")) {
            return classfileBuffer;
        }

        ClassReader cr = new ClassReader(classfileBuffer);
        if (cr.getSuperName().startsWith("lunar/") && cr.getInterfaces().length == 0) {
            ClassNode cn = new ClassNode();

            cr.accept(cn, 0);

            if (cn.methods.size() > 40) {
                for (MethodNode method : cn.methods) {
                    if (method.desc.equals("(Ljava/lang/Character;)Ljava/lang/Character;")) {
                        for (AbstractInsnNode insn : method.instructions) {
                            if (insn.getOpcode() == Opcodes.BIPUSH) {
                                IntInsnNode intInsnNode = (IntInsnNode) insn;
                                if (intInsnNode.operand == 12) {
                                    intInsnNode.operand = 64;
                                }
                            }
                        }
                    }
                }
                ClassWriter cw = new ClassWriter(cr, 0);
                cn.accept(cw);
                return cw.toByteArray();
            }
        }
        return classfileBuffer;
    }

}

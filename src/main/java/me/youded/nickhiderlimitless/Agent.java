package me.youded.nickhiderlimitless;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassWriter;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class Agent {
    public static void premain(String args, Instrumentation inst) {
        inst.addTransformer(new ClassFileTransformer() {

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
                        boolean found = false;
                        for (MethodNode method : cn.methods) {
                            if (method.desc.equals("(Ljava/lang/Character;)Ljava/lang/Character;") && method.access == Opcodes.ACC_PRIVATE + Opcodes.ACC_SYNTHETIC) {
                                System.out.println("Found right function!");
                                method.instructions.clear();
                                method.localVariables.clear();
                                method.exceptions.clear();
                                method.tryCatchBlocks.clear();
                                method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                                method.instructions.add(new InsnNode(Opcodes.ARETURN));
                                ClassWriter cw = new ClassWriter(cr, 0);
                                cn.accept(cw);
                                return cw.toByteArray();
                            }
                        }
                        if (found) {
                            ClassWriter cw = new ClassWriter(cr, 0);
                            cn.accept(cw);
                            return cw.toByteArray();
                        }
                    }
                }
                return classfileBuffer;
            }
        });
    }

    public static void agentmain(Instrumentation inst) {
        premain(null, inst);
    }
}
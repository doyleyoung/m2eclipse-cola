package com.github.bmsantos.m2e.cola.tests.utils;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodCollector extends ClassVisitor {
    private final List<String> methods = new ArrayList<String>();

    public MethodCollector() {
        super(Opcodes.ASM4);
    }

    public MethodCollector(final int api) {
        super(api);
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature,
        final String[] exceptions) {
        methods.add(name);
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    public List<String> getMethods() {
        return methods;
    }
}

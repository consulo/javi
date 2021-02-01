package javi.compiler.spi;

import javi.compiler.internal.com.sun.tools.javac.api.JavacTool;

/**
 * @author VISTALL
 * @since 01/02/2021
 */
public class JaviCompiler extends JavacTool {
    @Override
    public String name() {
        return "javi";
    }
}

package javi.tests;

import javi.api.tools.JavaCompiler;
import javi.compiler.spi.JaviCompiler;

/**
 * @author VISTALL
 * @since 01/02/2021
 */
public class JaviUtil {

    public static JavaCompiler getJaviCompiler() {
        return new JaviCompiler();
    }
}

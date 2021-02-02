package javi.tests;

import javi.api.tools.JavaCompiler;
import javi.compiler.spi.JaviCompiler;

import java.util.ServiceLoader;

/**
 * @author VISTALL
 * @since 01/02/2021
 */
public class JaviUtil {

    public static JavaCompiler getJaviCompiler() {
        return ServiceLoader.load(JavaCompiler.class).findFirst().get();
    }
}

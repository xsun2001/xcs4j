package io.xsun.xcs4j.parser.impl;

import org.junit.Test;

import static org.junit.Assert.*;

public class RegexASTBuilderTest {

    @Test
    public void testBuildAST1() {
        var regex = "a";
        var builder = new RegexASTBuilder(regex);
        var ast = builder.buildAST();
        var ans = new RegexASTNode('a');
        assertEquals(ans, ast);
    }
}
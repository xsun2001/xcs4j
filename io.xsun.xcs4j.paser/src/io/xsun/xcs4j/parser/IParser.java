package io.xsun.xcs4j.parser;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface IParser {
    Stream<Token> parse(String src);
    Stream<Token> parse(Path srcFile);
}

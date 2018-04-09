package io.xsun.xcs4j.parser.impl;

import io.xsun.xcs4j.parser.ParserException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegexAbstractSyntaxTree {

    private static final Logger LOG = LogManager.getLogger(RegexAbstractSyntaxTree.class);

    private final Node root;

    public RegexAbstractSyntaxTree(String regex) {
        LOG.info("Start build AST with regex [%s]", regex);
        root = buildAST(regex);
    }

    private Node buildAST(String regex) {
        LOG.debug("Build the AST node of regex [%s]", regex);
        var nodeList = buildNodeList(regex);

        return null;
    }

    private int findingRightParenthesis(String regex, int left) {
        LOG.debug("Finding matching right parenthesis in regex [%s] from position [%d]", regex, left);
        int leftParenthesis = 0;
        for (int i = left; i < regex.length(); i++) {
            if (regex.charAt(i) == '(') ++leftParenthesis;
            if (regex.charAt(i) == ')') --leftParenthesis;
            if (leftParenthesis == 0) {
                LOG.debug("Found matching right parenthesis in position [%d]", i);
                return i;
            }
        }
        LOG.error("Cannot find matching parenthesis. The regex may have a syntax mistake.");
        throw new ParserException("No matching parenthesis");
    }

    private List<Node> buildNodeList(String regex) {
        LOG.debug("Building nodes list of regex [%s]", regex);
        var nodeList = new ArrayList<Node>();
        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            if (c == '(') {
                LOG.debug("Found left parenthesis in position [%d]", i);
                int right = findingRightParenthesis(regex, i);
                nodeList.add(buildAST(regex.substring(i + 1, right)));
                i = right + 1;
            } else if (c == '*') {
                LOG.debug("Found '*' in position [%d]", i);
                var node = nodeList.remove(nodeList.size() - 1);
                nodeList.add(new Node(Node.NodeType.STAR, node, null));
            } else if (c == '|') {
                LOG.debug("Found '|' in position [%d]", i);
                nodeList.add(new Node(Node.NodeType.OR));
            } else {
                LOG.debug("Found normal char [%c] in position [%d]", c, i);
                nodeList.add(new Node(c));
            }
        }
        LOG.debug("Finished building nodes list of regex [%s]", regex);
        return nodeList;
    }

    private void buildCat(List<Node> nodeList) {

    }

    public static class Node {
        private final NodeType type;
        private char value;
        private Node left, right;

        public Node(NodeType type) {
            this.type = type;
        }

        public Node(char value) {
            type = NodeType.CHAR;
            this.value = value;
        }

        public Node(NodeType type, Node left, Node right) {
            this.type = type;
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Node{");
            sb.append("type=").append(type);
            if (type == NodeType.CHAR) {
                sb.append(", value=").append(value);
            }
            sb.append(", left=").append(left);
            sb.append(", right=").append(right);
            sb.append('}');
            return sb.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return type == node.type &&
                    Objects.equals(left, node.left) &&
                    Objects.equals(right, node.right);
        }

        @Override
        public int hashCode() {

            return Objects.hash(type, left, right);
        }

        public NodeType getType() {

            return type;
        }

        public Node getLeft() {
            return left;
        }

        public void setLeft(Node left) {
            this.left = left;
        }

        public Node getRight() {
            return right;
        }

        public void setRight(Node right) {
            this.right = right;
        }

        public enum NodeType {
            CHAR, EMPTY, CAT, OR, STAR
        }
    }

}

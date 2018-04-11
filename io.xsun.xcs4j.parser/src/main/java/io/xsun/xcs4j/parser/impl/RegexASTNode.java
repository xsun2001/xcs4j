package io.xsun.xcs4j.parser.impl;

import java.util.Objects;

public class RegexASTNode {
    public static final RegexASTNode EMPTY_NODE = new RegexASTNode(NodeType.EMPTY);

    private final NodeType type;
    private char value;
    private RegexASTNode left, right;

    public RegexASTNode(NodeType type) {
        this.type = type;
    }

    public RegexASTNode(char value) {
        type = NodeType.CHAR;
        this.value = value;
    }

    public RegexASTNode(NodeType type, RegexASTNode left, RegexASTNode right) {
        this.type = type;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Node{");
        sb.append("type=").append(type);
        if (type == NodeType.CHAR) {
            sb.append(", value=").append(value).append('}');
        } else if (type == NodeType.STAR) {
            sb.append(", child=").append(left);
        } else if (type != NodeType.EMPTY) {
            sb.append(", left=").append(left);
            sb.append(", right=").append(right);
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegexASTNode node = (RegexASTNode) o;
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

    public RegexASTNode getLeft() {
        return left;
    }

    public void setLeft(RegexASTNode left) {
        this.left = left;
    }

    public RegexASTNode getRight() {
        return right;
    }

    public void setRight(RegexASTNode right) {
        this.right = right;
    }

    public enum NodeType {
        CHAR, EMPTY, CAT, OR, STAR
    }
}

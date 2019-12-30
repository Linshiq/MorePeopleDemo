package com.example.demo.lintCode.solution;

public class Solution85 {
    /*
     * @param root: The root of the binary search tree.
     * @param node: insert this node into the binary search tree
     * @return: The root of the new binary search tree.
     */
    public TreeNode insertNode(TreeNode root, TreeNode node) {
        // write your code here
        if (root == null) {
            return node;
        }
        if (root.right == null) {
            root.right = node;
            return root;
        }
        if (root.left == null) {
            root.left = node;
            return root;
        }

        TreeNode curRoot = root;

        while(curRoot != node){
            if(node.val < curRoot.val){
                if(curRoot.left == null) {
                    curRoot.left = node;
                }
                curRoot = curRoot.left;
            } else {
                if (curRoot.right == null) {
                    curRoot.right = node;
                }
                curRoot = curRoot.right;
            }
        }

        return root;
    }

    public class TreeNode {
        public int val;
        public TreeNode left, right;

        public TreeNode(int val) {
            this.val = val;
            this.left = this.right = null;
        }
    }

    public static void main(String[] args) {
        System.out.println();
    }
}

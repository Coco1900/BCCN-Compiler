package lasalle.tac;

import lasalle.trees.ParsingTree;
import lasalle.trees.ParsingTreeNode;
import lasalle.trees.ParsingTreeNodeIterator;

import java.util.*;

public class SmallTrees {
    Map<String, ParsingTreeNode<String>> smallTreesMap;
    List<String> keywords = Arrays.asList(
            "<ifs>",
            "<while>",
            "<assignment>",
            "<declaration>"
    );
    Stack<ParsingTreeNodeIterator<String>> stack;
    int labelNumber = 1;

    public SmallTrees(){
        smallTreesMap = new LinkedHashMap<String, ParsingTreeNode<String>>();
        stack = new Stack<>();
    }

    public void createSmallTrees(){
        //Iterate through the parsingTree
        ParsingTreeNode<String> parsingTree = ParsingTree.getParsingTree();
        Iterator<ParsingTreeNode<String>> parsingTreeNodeIterator = parsingTree.iterator();
        Iterator<ParsingTreeNode<String>> stackNodeIterator = null;

        //For each node, check if it is in the keyword array
        //If the node does have children, create a small tree
        //Save a new tree in the tree stack
        //Now we know that the next children of the node in the stack will
        //be the same ones as in the parsingTreeNodeIterator
        //Continue iterating
        // -> if the stack node doesn't have more children, remove it from the stack
        // -> if it has more children, and the child is a keyword, change the value of the current child to the next label
        // Create a new tree and add it to stack
        // Update the node-stack iterator to point to the children of newly created small tree

        //Find the fist occurrence of the keyword
        while(parsingTreeNodeIterator.hasNext()) {
            ParsingTreeNode<String> node = parsingTreeNodeIterator.next();
            ParsingTreeNode<String> stackNode = null;
            //Move the iterator of the stackNodeIter
            if(stackNodeIterator != null){
                if(stackNodeIterator.hasNext()) {
                    stackNode = stackNodeIterator.next();
                }else{
                    //If there is no more children in the small tree iterator,
                    // pop the node from the stack and update the iterator
                    if(!stack.isEmpty()){
                        stack.pop();
                        if(!stack.isEmpty()){
                            //If the stack is not empty, get the first iterator that has more children
                            stackNodeIterator = stack.peek();
                            while(!stackNodeIterator.hasNext()){
                                stack.pop();
                                if(!stack.isEmpty()) {
                                    stackNodeIterator = stack.peek();
                                }else{
                                    stackNode = null;
                                    stackNodeIterator = null;
                                    break;
                                }
                            }
                            if(stackNodeIterator != null){
                                stackNode = stackNodeIterator.next();
                            }
                        } else {
                            stackNode = null;
                            stackNodeIterator = null;
                        }
                    }
                }
            }else{
                stackNode = null;
                stackNodeIterator = null;
            }

            //Add nodes to the small Tree if it is not a keyword
            if (isTokenAKeyword(node.data)) {
                if (!node.children.isEmpty()) {
                    ParsingTreeNode<String> newSmallTree = new ParsingTreeNode<>(node.data);
                    //For each child of the node, add them to the newSmallTree
                    Iterator<ParsingTreeNode<String>> childIter = node.children.iterator();
                    while(childIter.hasNext()){
                        newSmallTree.addChild(childIter.next());
                    }
                    //Add the node to small tree map with a label
                    String label = "L"+labelNumber;
                    smallTreesMap.put(label, newSmallTree);
                    //Push the small tree to the stack
                    //Check if there is a parent tree
                    if(stackNode != null){
                        stackNode.updateValue(label);
                        if(!stackNode.children.isEmpty()){
                            ((ParsingTreeNodeIterator<String>) stackNodeIterator).removeChildrenFromStack(stackNode.children);
                            stackNode.children.clear();
                        }
                    }
                    stack.add((ParsingTreeNodeIterator<String>) newSmallTree.iterator());
                    //Update the iterator of the current stack iterator
                    stackNodeIterator = stack.peek();
                    //Move the iterator so the next one is not a parent
                    if(stackNodeIterator.hasNext()){
                        stackNodeIterator.next();
                    }


                    labelNumber++;
                }
            }
        }
    }

    private boolean isTokenAKeyword(String token){
        return keywords.contains(token);
    }

    /*private void createSmallTreesRecursively(ParsingTreeNodeIterator<String> parsingTreeNodeIterator, ParsingTreeNode<String> smallTree) {
        while(parsingTreeNodeIterator.hasNext()){
            ParsingTreeNode<String> node = parsingTreeNodeIterator.next();

            //Add nodes to the small Tree if it is not a keyword
            if(node.data.equals(keywords[0]) || node.data.equals(keywords[1]) || node.data.equals(keywords[2])){
                if(!node.children.isEmpty()) {
                    ParsingTreeNode<String> newSmallTree = new ParsingTreeNode<>(node.data);
                    ParsingTreeNode<String> sibling = findSiblingOfANode(node);
                    stack.push(sibling);
                    labelNumber++;
                    createSmallTreesRecursively(parsingTreeNodeIterator, newSmallTree);
                }
            }else{
                //Populate the smallTree until you find the next node which is on the top of the stack
                //That is a sibling of the current smallTree
                if(!stack.isEmpty()) {
                    if (!node.data.equals(stack.peek().data)) {
                        smallTree.addChild(node.data);
                    } else {
                        //Add the tree to the map
                        smallTreesMap.put("L" + labelNumber, smallTree);
                        stack.pop();
                        //parsingTreeNodeIterator.goBack();
                    }
                }
            }
        }
    }

    private ParsingTreeNode<String> findSiblingOfANode(ParsingTreeNode<String> node){
        //Get children iterator of a parent
        if(!node.isRoot()){
            Iterator<ParsingTreeNode<String>> parentIterator = node.parent.childrenIterator;
            while(parentIterator.hasNext()){
                if(parentIterator.next().data.equals(node.data)){
                    if(parentIterator.hasNext()){
                        return parentIterator.next();
                    }
                }
            }
        }
        return null;
    }*/
}

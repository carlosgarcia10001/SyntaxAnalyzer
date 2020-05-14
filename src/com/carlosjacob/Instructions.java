package com.carlosjacob;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class Instructions {
    private ArrayList<String> instructList;
    Deque<Integer> stack;
    SymbolTable symbols;
    static int instructionCounter = 1;
    public Instructions(SymbolTable symbols){
        instructList = new ArrayList<>();
        stack = new ArrayDeque<>();
        this.symbols = symbols;
    }

    public void performInstruction(InstructionEnum instruction, int value){
        switch(instruction){
            case PUSHI:
                stack.add(value);
                break;
            case PUSHM:
                stack.add((int)symbols.findValue(value));
                break;
            case POPM:
                symbols.insertValue(value,stack.getFirst());
                stack.pop();
                break;
            case JUMPZ:
                break;
            case JUMP:
        }
        insertInstruction(instruction,value);
    }

    public void performInstruction(InstructionEnum instruction){
        int first = stack.getFirst();
        boolean evaluation;
        switch(instruction){
            case STDOUT:
                stack.pop();
            case STDIN:
                break;
            case ADD:
                stack.pop();
                first += stack.getFirst();
                stack.pop();
                stack.add(first);
                break;
            case SUB:
                stack.pop();
                first = stack.getFirst() - first; //2nd item - 1st item
                stack.pop();
                stack.add(first);
            case MUL:
                stack.pop();
                first *= stack.getFirst(); //2nd item - 1st item
                stack.pop();
                stack.add(first);
            case DIV:
                stack.pop();
                first = stack.getFirst()/first; //2nd item - 1st item
                stack.pop();
                stack.add(first);
            case GRT:
                stack.pop();
                evaluation = stack.getFirst() > first;
                stack.pop();
                if(evaluation){
                    stack.add(1);
                }
                else{
                    stack.add(0);
                }
            case LES:
                stack.pop();
                evaluation = stack.getFirst() < first;
                stack.pop();
                if(evaluation){
                    stack.add(1);
                }
                else{
                    stack.add(0);
                }
            case EQU:
                stack.pop();
                evaluation = stack.getFirst() == first;
                stack.pop();
                if(evaluation){
                    stack.add(1);
                }
                else{
                    stack.add(0);
                }
                break;
            case NEQ:
                stack.pop();
                evaluation = stack.getFirst() != first;
                stack.pop();
                if(evaluation){
                    stack.add(1);
                }
                else{
                    stack.add(0);
                }
                break;
            case GEQ:
                stack.pop();
                evaluation = stack.getFirst() >= first;
                stack.pop();
                if(evaluation){
                    stack.add(1);
                }
                else{
                    stack.add(0);
                }
                break;
            case LEQ:
                stack.pop();
                evaluation = stack.getFirst() <= first;
                stack.pop();
                if(evaluation){
                    stack.add(1);
                }
                else{
                    stack.add(0);
                }
                break;
        }
        insertInstruction(instruction);
    }

    public void insertInstruction(InstructionEnum instruction, int value){
        instructList.add(instructionCounter + ". " + instruction.toString() + " " + value);
        instructionCounter++;
    }

    public void insertInstruction(InstructionEnum instruction){
        instructList.add(instructionCounter + ". " + instruction.toString());
        instructionCounter++;
    }

    public ArrayList<String> getInstructList(){
        return instructList;
    }

}
package symbol;

import java.util.*;

/**
 * A class to represent the symbol table which stores the variables that
 * are currently in use and their types, arranged by scope levels
 */

public class SymbolTable {
    private Stack<Map<String, String>> symbolList; // map ID to its type

    public SymbolTable() {
        symbolList = new Stack<>();
        symbolList.push(new HashMap<>());
    }

    
    /** 
     * return the current scope level. For an empty table this is 0.
     * @return int scope level
     */
    public int getLevel() {
        return symbolList.size() - 1;
    }

    /**
     * Open a new scope level. This is done by pushing a new HashMap
     * onto the stack
     */
    public void openScope() {
        symbolList.push(new HashMap<>());
    }

    /**
     * Close a scope level. This will pop the stack, and if the symbol
     * table is empty, i.e we have no scopes, throw an exception. 
     */
    public void closeScope() {
        if (getLevel() == 0) {
            throw new RuntimeException("Cannot close outer scope");
        }
        symbolList.pop();
    }

    
    /** 
     * Reserve a variable identifier name in the current scope level, if not already in use in the current scope.
     * @param id the identifier of the variable to add
     * @param type the type of the variable to add
     * @return boolean true if identifier wasn't present in table (i.e variable in use), otherwise false
     */
    public boolean add(String id, String type) {
        return symbolList.peek().putIfAbsent(id, type) == null;
    }

    
    /** 
     * Checks if a variable identifier is already being used by checking current scope and outer scopes
     * @param id the identifier to check
     * @return boolean true if in use, false if not.
     */
    public boolean inUse(String id) {
        return symbolList.get(getLevel()).keySet().contains(id);   
    }


    
    /** 
     * Find the type of a given identifier, if the identifier is in the table
     * @param id identifier for which we want to find the type
     * @return String the type of the variable, otherwise it returns null if not in use
     */
    public String getType(String id) {
        for (int i = getLevel(); i > -1; i--) {
            if (symbolList.get(i).keySet().contains(id)) {
                return symbolList.get(i).get(id);
            }
            
        }
        return null;
    }
    
    /** 
     * Checks if the table contains a variable identifier of the given type
     * @param id identifier of variable to check
     * @param type type to check
     * @return boolean true if the table contains the variable with the given type, false otherwise
     */
    public boolean contains(String id, String type) {
        for (int i = getLevel(); i > -1; i--) {
            if (symbolList.get(i).keySet().contains(id) && symbolList.get(i).get(id).equals(type)) {
                return true;
            }
            
        }
        return false;
    }

    public boolean varDeclared (String id) {
        for (int i = getLevel(); i > -1; i--) {
            if (symbolList.get(i).keySet().contains(id)) {
                return true;
            }
            
        }
        return false;
    }
}
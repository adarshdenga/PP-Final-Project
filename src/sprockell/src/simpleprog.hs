import Sprockell 
prog :: [Instruction]
prog = [
		Branch regSprID  (Rel 0) 
        , Load (ImmValue 1)  regA 
        , Store regA  (DirAddr 10) 
        , Load (ImmValue 2)  regA 
        , Store regA  (DirAddr 11) 
        , Load (ImmValue 4)  regA 
        , WriteInstr regA  (DirAddr 1) 
        , Jump (Rel 10) 
        , Load (ImmValue 2)  regA 
        , Store regA  (DirAddr 12) 
        , Load (DirAddr 10)  regA 
        , Load (DirAddr 11)  regB 
        , Compute Add regA  regB regA 
        , Load (DirAddr 12)  regB 
        , Compute Sub regA  regB regA 
        , Store regA  (DirAddr 10) 
        , Jump (Abs 1) 
        , Load (ImmValue 16)  regB 
        , WriteInstr regB  (DirAddr 2) 
        , Jump (Rel 6) 
        , Load (DirAddr 10)  regB 
        , Load (DirAddr 11)  regC 
        , Compute Sub regB  regC regB 
        , Store regB  (DirAddr 10) 
        , Jump (Abs 2) 
        , ReadInstr  (DirAddr 0) 
        , Receive  regC 
        , Branch regC  (Rel 2) 
        , Jump (Rel $ negate 1) 
        , EndProg 
		]
main = runWithDebugger (debuggerSimplePrintAndWait myShow) [prog]
main_1 = runWithDebugger (debuggerSimplePrint showLocalMem) [prog]
showLocalMem :: DbgInput -> String
showLocalMem ( _ , systemState ) = show $ localMem $ head $ sprStates systemState
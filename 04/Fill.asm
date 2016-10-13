(START)
@SCREEN
D=A //
@cur_pos //next available location in mem, where we will store SCREEN's loc
M=D //stores SCREEN start location in cur_pos - begins 16384

(KBDLOOP)
@KBD
D=M // D=KBD
@BLACKEN
D;JNE //if D = KBD =/= 0 
@WHITEN
D;JEQ //if no key is pressed, D = KBD = 0
@KBDLOOP
0;JMP //unconditional jump, loops and tries again if neither JUMPS occur

(WHITEN)
@R0
M=0
@FILLS
0;JMP

(BLACKEN)
@R0
M=-1 //temp stores in R0
@FILLS
0;JMP //unconditional jump to FILL

(FILLS)
@R0 //load filler, tells if we blacken or whiten the screen
D=M //D=filler=color

@cur_pos //accessing SCREEN loc that we stored in this variable
A=M 	//we set our A to this; at the beginning it is like writing @SCREEN but using a variable we can 	
		//increment it
M=D 	//changing the value of the actual screen location to color, stored in D

@cur_pos //reset A to cur_pos
D=M+1 //incrementing cur_pos and storing in D; purposes of checking if we can exit (FILL)
@KBD
D=A-D 	//when we have fully traversed the screen, we would have traversed 8192 pixels
		//since our cur_pos is dynamic we can keep this in a loop while KBD (at address 24576)-cur_pos is greater than 0, meaning if our cur_pos is still before the address of KBD this implies we have not filled the entire screen yet

@cur_pos
M=M+1 //actually changing what the current pixel is in the mem loc. cur_pos
A=M

@FILLS
D;JGT	//as soon as D is 0, we exit since we have covered the entire screen

@START
0;JMP //start over

// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input. 
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel. When no key is pressed, the
// program clears the screen, i.e. writes "white" in every pixel.

// Put your code here.

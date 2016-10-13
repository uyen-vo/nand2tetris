// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Div.asm

// Divides R0 by R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

// Put your code here.

@divNum
M = 0 //amount of times subtracted
@R0
D = M // D = R0

//R0 BY R1 implies subtracting R1 from R0 continuously til = 0 or <0

(LOOP)
	@R1
	D = D - M // D=R0-R1
	
	@END
	D;JLT //R0-R1 is less than zero -> exit loop

	@divNum
	M = M + 1 //increment number of division

	@END
	D;JEQ //if we have evenly divided everything -> exit loop

	@LOOP //otherwise we continue subtracting
	0;JMP


(END)
	@divNum
	D=M
	@R2
	M=D //write into R2
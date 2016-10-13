// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Do multiplication of R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

// Put your code here.

@result
M = 0

@1
D=A

@counter
M = D
D = M // D = counter, initialized to 16

(LOOP)
	@temp1
	M = 1 < D
	D=M
	@R1
	D=M&D
	@CB3
	D;JLE //if D <= 0 we skip the adding; else we continue

	@R0
	D=M
	@counter
	D=D<M //R0<<counter
	@result
	M = M + D

(CB3)
@counter
M=M-1
D=M
@LOOP
D;JGE //while counter >= 0

@result
D=M
@R2
M=D

(END)
@END
0;JMP
	

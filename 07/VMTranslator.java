import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;

public class VMTranslator {
	
	private ArrayList<String> lines; //contains all lines at each index
	private String masterString=""; //entire string, returned at the end
	private String fileName;
	private static int count = 0;
	
	//overriding toString() of each enum
	public VMTranslator(){
		lines = new ArrayList<String>();
		fileName = "";
	}
	
	public VMTranslator(String fileName){
		lines = new ArrayList<String>();
		this.fileName = fileName;
	}
	
	private enum MEMACCESS{
		POP {
		      public String toString() { return "@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n"; }
		},
		
		PUSH {
		      public String toString() { return "@SP\nA=M\nM=D\n@SP\nM=M+1\n"; }
		}
		
	}

	private enum ARITHBOOL {
		  ADD {
		      public String toString() { return "@SP\nAM=M-1\nD=M\nA=A-1\nM=D+M\n"; }
		  },
		  
		  SUB {
		      public String toString() { return "@SP\nAM=M-1\nD=M\nA=A-1\nM=M-D\n"; }
		  },
		  
		  NEG {
		      public String toString() { return "@SP\nA=M-1\nM=-M\n"; }
		  },
		  
		  EQ {
		      public String toString() {
		    	  String c = count();
		    	  return "@SP\nAM=M-1\nD=M\nA=A-1\nD=M-D\n@EQ.true." + c +
		    			  "\n\nD;JEQ\n@SP\nA=M-1\nM=0\n@EQ.after." + c + 
		    			  "\n0;JMP\n(EQ.true." + c+ ")\n@SP\nA=M-1\nM=-1\n(EQ.after."
		    			  + c + ")\n";
		      }
		  },
		  
		  GT {
			  public String toString() {
		    	  String c = count();
		    	  return "@SP\nAM=M-1\nD=M\nA=A-1\nD=M-D\n@GT.true." + c +
		    			  "\n\nD;JGT\n@SP\nA=M-1\nM=0\n@GT.after." + c + 
		    			  "\n0;JMP\n(GT.true." + c+ ")\n@SP\nA=M-1\nM=-1\n(GT.after."
		    			  + c + ")\n";
		      }
		  },
		  
		  LT {
			  public String toString() {
		    	  String c = count();
		    	  return "@SP\nAM=M-1\nD=M\nA=A-1\nD=M-D\n@LT.true." + c +
		    			  "\n\nD;JLT\n@SP\nA=M-1\nM=0\n@LT.after." + c + 
		    			  "\n0;JMP\n(LT.true." + c+ ")\n@SP\nA=M-1\nM=-1\n(LT.after."
		    			  + c + ")\n";
		      }
		  },
		  
		  AND {
		      public String toString() { return  "@SP\nAM=M-1\nD=M\nA=A-1\nM=D&M\n"; }
		  },
		  
		  OR {
		      public String toString() { return "@SP\nAM=M-1\nD=M\nA=A-1\nM=D|M\n"; }
		  },
		  
		  NOT {
		      public String toString() { return "@SP\nA=M-1\nM=!M\n"; }
		  }

	}
	
	private static String count(){
		count++;
		return Integer.toString(count);
	}
	
	public void readLines(String line){
		lines.add(line);
	}
	
	public void pop(String[] s){
		switch(s[1]){
		case "argument":{
			masterString += "@ARG\nD=M\n@" + s[2] + "\nD=D+A\n" + MEMACCESS.POP.toString();
		}; break;
		case "local":{
			masterString += "@LCL\nD=M\n@" + s[2] + "\nD=D+A\n" + MEMACCESS.POP.toString();
		}; break;
		case "static":{
			masterString += "@" + fileName + "." + s[2] + "\nD=A\n" + MEMACCESS.POP.toString();
		}; break;
		case "constant":{
			masterString += "@" + s[2] + "\nD=A\n" + MEMACCESS.POP.toString();
		}; break;
		case "this":{
			masterString += "@THIS\nD=M\n@" + s[2] + "\nD=D+A\n" + MEMACCESS.POP.toString();
		}; break;
		case "that":{
			masterString += "@THAT\nD=M\n@" + s[2] + "\nD=D+A\n" + MEMACCESS.POP.toString();
		}; break;
		case "pointer":{
			if (s[2].equals("0"))
			{
				masterString+="@THIS\nD=A\n" + MEMACCESS.POP.toString();
			}
			else{ //s[2] equals 1
				masterString+="@THAT\nD=A\n" + MEMACCESS.POP.toString();
			}
		}; break;
		case "temp":{
			masterString += "@R5\nD=A\n@" + s[2] + "\nA=D+A\nD=M\n" + MEMACCESS.POP.toString();
		}; break;
		default: break;
		}
	}
	
	public void push(String[] s){
		switch(s[1]){
		case "argument":{
			masterString += "@ARG\nD=M\n@" + s[2] + "\nA=D+A\nD=M\n" + MEMACCESS.PUSH.toString();
		}; break;
		case "local":{
			masterString += "@LCL\nD=M\n@" + s[2] + "\nA=D+A\nD=M\n" + MEMACCESS.PUSH.toString();
		}; break;
		case "static":{
			masterString += "@" + fileName + "." + s[2] + "\nD=M\n" + MEMACCESS.PUSH.toString();
		}; break;
		case "constant":{
			masterString += "@" + s[2] + "\nD=A\n" + MEMACCESS.PUSH.toString();
		}; break;
		case "this":{
			masterString += "@THIS\nD=M\n@" + s[2] + "\nA=D+A\nD=M\n" + MEMACCESS.PUSH.toString();
		}; break;
		case "that":{
			masterString += "@THAT\nD=M\n@" + s[2] + "\nA=D+A\nD=M\n" + MEMACCESS.PUSH.toString();
		}; break;
		case "pointer":{
			if (s[2].equals("0"))
			{
				masterString+="@THIS\nD=M\n" + MEMACCESS.PUSH.toString();
			}
			else{ //s[2] equals 1
				masterString+="@THAT\nD=M\n" + MEMACCESS.PUSH.toString();
			}
		}; break;
		case "temp":{
			masterString += "@R5\nD=A\n@" + s[2] + "\nA=D+A\nD=M\n" + MEMACCESS.PUSH.toString();
		}; break;
		default: break;
		}
	}
	
	public void transLines(){
		for(String l : lines){
			String[] splitLine = l.split("\\s+");
			switch(splitLine[0]){
			//	memory access
			case "pop" : {
				pop(splitLine);
			}; break;
			case "push" : {
				push(splitLine);
			}; break;
			// arithmetic/bool
			case "add" : {
				masterString += ARITHBOOL.ADD.toString();
			}; break;
			case "sub" : {
				masterString += ARITHBOOL.SUB.toString();
			}; break;
			case "neg" : {
				masterString += ARITHBOOL.NEG.toString();				
			}; break;
			case "eq" : {
				masterString += ARITHBOOL.EQ.toString();	
			}; break;
			case "gt" : {
				masterString += ARITHBOOL.GT.toString();	
			}; break;
			case "lt" : {
				masterString += ARITHBOOL.LT.toString();
			}; break;
			case "and" : {
				masterString += ARITHBOOL.AND.toString();
			}; break;
			case "or" : {
				masterString += ARITHBOOL.OR.toString();
			}; break;
			case "not" : {
				masterString += ARITHBOOL.NOT.toString();
			}; break;
			// program flow; splitLine[1] is label
			case "label" : {
				masterString += "(" + splitLine[1] + ")\n";
			}; break;
			case "goto" : {
				masterString += "@" + splitLine[1] + "\n0;JMP\n";
			}; break;
			case "if-goto" : {
				masterString += "@SP\nAM=M-1\nD=M\n@" + splitLine[1] + "\nD;JNE\n";				
			}; break;
			default: break; //error handling occurred before already
			}
		}
	}
	
	//Override
	public String toString(){
		return masterString;
	}
	public static void main(String[] args) {
		int i = 0;
		while (i == 0){
			Scanner s = new Scanner(System.in);
			System.out.println("(Type 'z' to quit)\nEnter *.vm file to convert to *.asm: ");
			String name = s.next();
			if(name.equals("z")){ return; }
			VMTranslator vm = new VMTranslator(name);
			String l = null;
			try{
				FileReader f = new FileReader(name+".vm");
				BufferedReader b = new BufferedReader(f);
				while((l = b.readLine()) != null) { //null means end of stream is reached
					if(l.length() > 0) { //ignores empty lines
						if(l.charAt(0) != '/') { //ignores comments
							vm.readLines(l); //we put each line into an array
						}
					}
				}
			} catch(Exception e){
				e.printStackTrace();
			}
			vm.transLines();
			try{
				PrintWriter w = new PrintWriter(name+".asm", "UTF-8");
				w.print(vm.toString());
				w.close();
				
			} catch(FileNotFoundException e){
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return;
		
		
	}
}



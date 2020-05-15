import java.util.*;
import java.io.*; 

public class VMTranslater{

       public static String commandS;


public static class Parser{
public Scanner sc;
public String[] command;
public	  String C_ARITHMETIC = "C_ARITHMETIC";
public	  String C_PUSH = "C_PUSH";
public 	  String C_POP = "C_POP";
public    String C_LABEL = "C_LABEL";
public    String C_GOTO = "C_GOTO";
public    String C_IFGOTO = "C_IFGOTO";
public    String C_FUNCTION = "C_FUNCTION";
public    String C_CALL = "C_CALL";
public    String C_RETURN = "C_RETURN";

 public Parser(File file){
	  
	  try
	  {
    sc = new Scanner(file);
	  } catch(FileNotFoundException ex){
		  
		  System.out.println("File not found");
	  }

	  
	  
 }

 public  Boolean hasMoreCommands(){
	return(sc.hasNextLine());
 }
 public void advance(){
	command = sc.nextLine().split(" ");
	for(int d = 0; d<command.length;d++){
	System.out.println("NEXT D" + command[d]);
	}
 }
 public String commandType(){
//	System.out.println("command check " + command);
	if(command[0].equals("push"))return C_PUSH;
	if(command[0].equals("pop"))return C_POP;
	if(command[0].equals("label"))return C_LABEL;
	if(command[0].equals("if-goto"))return C_IFGOTO;
	if(command[0].equals("goto"))return C_GOTO;
	if(command[0].equals("call"))return C_CALL;
	if(command[0].equals("return"))return C_RETURN;
	if(command[0].equals("function"))return C_FUNCTION;
	return C_ARITHMETIC;
 }
 public String arg1(){
	if(this.commandType() == C_ARITHMETIC)return command[0];
	return command[1];

 }
 public String arg2(){
	return command[2];

 }
}





public static class CodeWriter{
	
	public BufferedWriter writer;
	public String FileName;
	public int counter;
	public int counter2;
	public int counter3;
	public String fName1;
	public CodeWriter(File file){
		try {

			writer = new BufferedWriter(new FileWriter(file));
		
		} catch (IOException e){
			System.out.println(e);
		}
		 counter = 0;
		 counter2 = 1;
		 counter3 = 1;
		 String dir = file.getName();
         String[] tokens = dir.split(".+?/(?=[^/]+$)");
		 FileName = tokens[0].split("\\.")[0];
		 System.out.println("FILE NAME " + FileName);
	}
	
	public void writeArithmetic(String cmdA){
		try {
			
			
          
		if(cmdA.equals("add")){
			 
	     writer.write("@SP");
		 writer.newLine();
         writer.write("M=M-1");
		 writer.newLine();
		 writer.write("A=M");
		 writer.newLine();
		 writer.write("D=M");
		 writer.newLine();
		 writer.write("@SP");
		 writer.newLine();
		 writer.write("M=M-1");
		 writer.newLine();
		 writer.write("A=M");
		 writer.newLine();
		 writer.write("M=D+M");	
         writer.newLine();
         writer.write("@SP");
		 writer.newLine();
         writer.write("M=M+1");	 
         writer.newLine();		 
		 }
		 if(cmdA.equals("sub")){
	     writer.write("@SP");
		 writer.newLine();
         writer.write("M=M-1");
		 writer.newLine();
		 writer.write("A=M");
		 writer.newLine();
		 writer.write("D=M");
		 writer.newLine();
		 writer.write("@SP");
		 writer.newLine();
		 writer.write("M=M-1");
		 writer.newLine();
		 writer.write("A=M");
		 writer.newLine();
		 writer.write("M=M-D");
         writer.newLine();
        writer.write("@SP");
		 writer.newLine();
         writer.write("M=M+1");	 
         writer.newLine();		 
		 }
		 if(cmdA.equals("neg")){
		 writer.write("@SP");
		 writer.newLine();
		 writer.write("A=M-1");
		 writer.newLine();
		 writer.write("D=0\n");
		 writer.write("M=D-M");
         writer.newLine();		 
		 }
		 if(cmdA.equals("and")){
		 writer.write("@SP");
		 writer.newLine();
         writer.write("A=M-1");
		 writer.newLine();
		 writer.write("D=M");
		 writer.newLine();
		 writer.write("@SP");
		 writer.newLine();
		 writer.write("M=M-1");
		 writer.newLine();
		 writer.write("A=M-1");
		 writer.newLine();
		 writer.write("M=M&D");
		 writer.newLine();
		 }
		 if(cmdA.equals("or")){
		 writer.write("@SP");
		 writer.newLine();
         writer.write("A=M-1");
		 writer.newLine();
		 writer.write("D=M");
		 writer.newLine();
		 writer.write("@SP");
		 writer.newLine();
		 writer.write("M=M-1");
		 writer.newLine();
		 writer.write("A=M-1");
		 writer.newLine();
		 writer.write("M=M|D");	
         writer.newLine();		 
		 }
		 if(cmdA.equals("not")){
		 writer.write("@SP");
		 writer.newLine();
         writer.write("M=M-1");
		 writer.newLine();
		 writer.write("@SP");
		 writer.newLine();
		 writer.write("A=M");
		 writer.newLine();
		 writer.write("M=!M");	
         writer.newLine();
         writer.write("@SP\n");
         writer.write("M=M+1\n");		 
		 }
		 
		 if(cmdA.equals("eq") || cmdA.equals("lt") || cmdA.equals("gt")){
		 writer.write("@SP\n");
		 writer.write("M=M-1\n");
		 writer.write("@SP\n");
		 writer.write("A=M\n");
		 writer.write("D=M\n");
		 writer.write("@SP\n");
		 writer.write("M=M-1\n");
		 writer.write("@SP\n");
		 writer.write("A=M\n");
		 writer.write("D=M-D\n");
		 
		 writer.write("@" + "EQ" + counter + "\n");
		 if(cmdA.equals("eq")){		 
         writer.write("D;JEQ\n");
		 } else if(cmdA.equals("lt")){
		 writer.write("D;JLT\n");
         		 
		 }else{
			 writer.write("D;JGT\n");
		 }		 
         writer.write("@SP\n");
         writer.write("A=M\n");	
         writer.write("M=0\n");		 
         writer.write("@" + "ESC" + counter + "\n");
		 writer.write("0;JMP\n");
		 writer.write("(EQ" + counter + ")" + "\n");
		 writer.write("@SP\n");
		 writer.write("A=M\n");
		 writer.write("M=-1\n");
		 writer.write("(ESC" + counter + ")" + "\n");
		 writer.write("@SP\n");
		 writer.write("M=M+1\n");
		 
		 counter++;
		 }
		 
		} catch (IOException e){
			System.out.println(e);
			
		}

	}
	public  void writePushPop (String P, String segment,int index){
		System.out.println("writePushPop " + P + " " + segment + " " + index);
		int z =0;
		try{
		
		String s = "";
		if(segment.equals("local")) s = "LCL";
		if(segment.equals("argument")) s = "ARG";
		if(segment.equals("this")) s = "THIS";
		if(segment.equals("that")) s = "THAT";
		if(segment.equals("temp")) s = "R5";
		if(segment.equals("static")) s = fName1 + "." + index;
		
		if(P.equals( "C_PUSH")){
			
			
			if(segment.equals("constant")){
			writer.write("@" + index + "\n");			
			writer.write("D=A");
			writer.newLine();
			writer.write("@SP");
			writer.newLine();
			writer.write("A=M");
			writer.newLine();
			writer.write("M=D");
			writer.newLine();
			writer.write("@SP");
			writer.newLine();
			writer.write("M=M+1");
			writer.newLine();
			} else if(segment.equals("pointer")){
				if(index == 0)writer.write("@THIS\n");
				if(index == 1)writer.write("@THAT\n");
				
				writer.write("D=M\n");
				writer.write("@SP\n");
				writer.write("A=M\n");
				writer.write("M=D\n");
				writer.write("@SP\n");
				writer.write("M=M+1\n");															
			  }
			     else{
					 
			writer.write("@" + s);
			writer.newLine();
			if(s.equals("R5")){
				while(index > z){
				writer.write("A=A+1" + "\n");
				z++;
			    }				
			}else if(segment.equals("static")){
			}else{
				
					writer.write("A=M");
			writer.newLine();			
				while(index > z){
				writer.write("A=A+1" + "\n");
				z++;
			    }			
			}				
			writer.write("D=M");
			writer.newLine();
            writer.write("@SP");
			writer.newLine();
			writer.write("A=M");
			writer.newLine();
			writer.write("M=D");
			writer.newLine();
			writer.write("@SP");
			writer.newLine();
			writer.write("M=M+1");
			writer.newLine();
			}
		}
		if(P.equals( "C_POP")){
			
				writer.write("@SP");
				writer.newLine();
				writer.write("M=M-1");
				writer.newLine();
				writer.write("A=M");
				writer.newLine();
				writer.write("D=M");
				writer.newLine();
				if(segment.equals("pointer")){
			      if(index == 0)writer.write("@THIS\n");
				  if(index == 1)writer.write("@THAT\n");				
				}else{
				writer.write("@" + s);
				writer.newLine();
				}
			if(s.equals("R5")){
				while(index > z){
				writer.write("A=A+1" + "\n");
				z++;
			    }				
			}else if(segment.equals("pointer")){


				
							
			}
			else if (segment.equals("static")){
			}else{
			writer.write("A=M");
			writer.newLine();			
				while(index > z){
				writer.write("A=A+1" + "\n");
				z++;
			    }	
			}
				writer.write("M=D");
				writer.newLine();
                				
	    }
		
				} catch (IOException e){
			System.out.println(e);
			
		}
	}
	public void writeLabel(String label){
		try{
		writer.write("(" + label + ")\n");
		
				} catch (IOException e){
			System.out.println(e);
			
		}
	}
	public void writeGoto(String label){
		try{
		writer.write("@" + label + "\n");
		writer.write("0;JMP\n");
				} catch (IOException e){
			System.out.println(e);
			
		}
	}
	public void writeIf(String label){
		try{
		writer.write("@SP\n");
		writer.write("M=M-1\n");
		writer.write("A=M\n");
		writer.write("D=M\n");
		writer.write("@" + label + "\n");
		writer.write("D;JNE\n");
				} catch (IOException e){
			System.out.println(e);
			
		}
	}
	public void writeCall(String FName, int argus){
		
	try{
            writer.write("@" + FName + "$ret." + counter2 + "\n");		
			writer.write("D=A\n");
			writer.write("@SP\n");
			writer.write("A=M\n");
			writer.write("M=D\n");
			writer.write("@SP\n");
			writer.write("M=M+1\n");
			
			writer.write("@LCL\n");	
            writer.write("A=M\n");			
			writer.write("D=A\n");
			writer.write("@SP\n");
			writer.write("A=M\n");
			writer.write("M=D\n");
			writer.write("@SP\n");
			writer.write("M=M+1\n");

			writer.write("@ARG\n");	
            writer.write("A=M\n");			
			writer.write("D=A\n");
			writer.write("@SP\n");
			writer.write("A=M\n");
			writer.write("M=D\n");
			writer.write("@SP\n");
			writer.write("M=M+1\n");
			
			writer.write("@THIS\n");	
            writer.write("A=M\n");			
			writer.write("D=A\n");
			writer.write("@SP\n");
			writer.write("A=M\n");
			writer.write("M=D\n");
			writer.write("@SP\n");
			writer.write("M=M+1\n");
			
			writer.write("@THAT\n");	
            writer.write("A=M\n");			
			writer.write("D=A\n");
			writer.write("@SP\n");
			writer.write("A=M\n");
			writer.write("M=D\n");
			writer.write("@SP\n");
			writer.write("M=M+1\n");

            writer.write("@" + (5 + argus)+"\n");
			writer.write("D=A\n");			
			writer.write("@R13\n");
			writer.write("M=D\n");
			writer.write("@SP\n");
			writer.write("A=M-D\n");
			writer.write("D=A\n");
			writer.write("@ARG\n");
			writer.write("M=D\n");
			
			writer.write("@SP\n");
			writer.write("D=M\n");
			writer.write("@LCL\n");
			writer.write("M=D\n");
			
		    writer.write("@" + FName + "\n");
		    writer.write("0;JMP\n");
			writer.write("(" + FName + "$ret." + counter2 + ")\n");
			
			} catch (IOException e){
			System.out.println(e);
			
		}
			
			
			counter2++;
	}
	public void writeFunction(String FName, int vars){
		int popper = 0;
		int i = 0;
		String push = "C_PUSH";
		String constant = "constant";
		try{	
         writer.write("(" + FName + ")\n");
		 while(popper < vars){
			 
			 
			 this.writePushPop(push, constant, i);
			 popper++;
		 }

		
				} catch (IOException e){
			System.out.println(e);
			
		}		
	}
	public void writeReturn(){
		
		try{
		
		
		
		writer.write("@LCL\n");
		writer.write("D=M\n");
		writer.write("@R14\n");
		writer.write("M=D\n");
		
		writer.write("@LCL\n");
		writer.write("A=M\n");
		writer.write("A = A-1\n");
		writer.write("A = A-1\n");
		writer.write("A = A-1\n");
		writer.write("A = A-1\n");
		writer.write("A = A-1\n");
        writer.write("D=M\n");
		
		writer.write("@return\n");
		writer.write("M=D\n");

		

		writer.write("@SP\n");
		writer.write("A=M-1\n");
		writer.write("D=M\n");
		writer.write("@ARG\n");
		writer.write("A=M\n");
		writer.write("M=D\n");	
		
		writer.write("D=A+1\n");
		writer.write("@SP\n");
		writer.write("M=D\n");
		
		writer.write("@R14\n");
		writer.write("M=M-1\n");
		writer.write("A=M\n");
		writer.write("D=M\n");
		writer.write("@THAT\n");
		writer.write("M=D\n");
		
		writer.write("@R14\n");
		writer.write("M=M-1\n");
		writer.write("A=M\n");
		writer.write("D=M\n");
		writer.write("@THIS\n");
		writer.write("M=D\n");
		
		writer.write("@R14\n");
		writer.write("M=M-1\n");
		writer.write("A=M\n");
		writer.write("D=M\n");
		writer.write("@ARG\n");
		writer.write("M=D\n");
		
		writer.write("@R14\n");
		writer.write("M=M-1\n");
		writer.write("A=M\n");
		writer.write("D=M\n");
		writer.write("@LCL\n");
		writer.write("M=D\n");		
		
		writer.write("@return\n");
		writer.write("A=M\n");
        writer.write("0;JMP\n");
		
		} catch (IOException e){
			System.out.println(e);
			
		}
		
	}
	
	
	public  void close(){
		try{
		writer.close();	
				} catch (IOException e){
			System.out.println(e);
			
		}
	}
	public void writeInit(){
			try{
		writer.write("@256\n");
		writer.write("D=A\n");
		writer.write("@SP\n");
		writer.write("M=D\n");
		String s = "Sys.init";
		this.writeCall(s,0);
		
		
				} catch (IOException e){
			System.out.println(e);
			
		}
		
	}
	public void setFileName(String fileName){
		
		fName1 = fileName;
		System.out.println("FILE NAME IS " + fName1);
	}
}    




public static void main(String[] args){

        System.out.println(args[0]);
		File file3 = new File(args[0]);
       FilenameFilter txtFileFilter = new FilenameFilter()
        {
            
            public boolean accept(File dir, String name)
            {
                if(name.endsWith(".vm"))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        };
        File[] files = file3.listFiles(txtFileFilter);
		String[] fileEndArray = args[0].split("\\\\",0);
		String fileEnd = fileEndArray[fileEndArray.length-1];
		System.out.println("FILE END " + fileEnd);
 		File file4 = new File(args[0] + "\\" + fileEnd + ".asm");
       CodeWriter obj2 = new CodeWriter(file4);	
        obj2.writeInit();	   
        for (File file : files)
			
        {

       Parser obj1 = new Parser(file);
		String[] fileEndArray2 = file.getName().split("\\\\",0);
		String fileEnd2 = fileEndArray2[fileEndArray2.length-1];	   
        obj2.setFileName(fileEnd2);
	   while(obj1.hasMoreCommands()){
		   obj1.advance();
		   commandS = obj1.commandType();
		   System.out.println(commandS);
		   if(commandS.equals("C_ARITHMETIC")) obj2.writeArithmetic(obj1.arg1().trim());
		   else if(commandS.equals("C_PUSH") || commandS.equals("C_POP"))obj2.writePushPop(obj1.commandType(),obj1.arg1(), Integer.parseInt(obj1.arg2().trim()));
		   else if(commandS.equals("C_LABEL")) obj2.writeLabel(obj1.arg1());
		   else if (commandS.equals("C_GOTO")) obj2.writeGoto(obj1.arg1());
		   else if (commandS.equals("C_IFGOTO")) obj2.writeIf(obj1.arg1());
		   else if (commandS.equals("C_FUNCTION")) obj2.writeFunction(obj1.arg1(),Integer.parseInt(obj1.arg2().trim()));
		   else if (commandS.equals("C_CALL")) obj2.writeCall(obj1.arg1(),Integer.parseInt(obj1.arg2().trim()));
		   else if (commandS.equals("C_RETURN")) obj2.writeReturn();
	   }

        }
    		
		

		
		

	   obj2.close();
	   



}

}


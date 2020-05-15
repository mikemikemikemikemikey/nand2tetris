import java.util.*;
import java.io.*; 
import java.util.Scanner;

public class Assembler{
	
	public static HashMap<String, String> compu = new HashMap<>();

	public static HashMap<String, String> jump = new HashMap<>();
	public static HashMap<String, String> dest = new HashMap<>();
	public static HashMap<String, String> symbols = new HashMap<>();
    public static int counter = 0;
	public static int nextSpace = 16;
	
	public static String parseFirst(String in){
	String s4 = in.trim();
    String t1 = "(";
	String t2 = "/";
	
	String test;
	char[] ch = s4.toCharArray();	
    if(ch.length > 0){
		if(ch[0] == t2.charAt(0)){
			return null;
		}
		if(ch[0] == t1.charAt(0)){

			s4 = s4.substring(1,s4.length()-1);

			test = symbols.get(s4);
			if(test == null){
				symbols.put(s4,Integer.toString(counter-2));
				counter--;
			}
			
		}
		
	}
	return "something";
	
	}
	
	
    public static String parse(String in){
		in = in.trim();
		ArrayList<String> al = new ArrayList<>();
		String t1 = "@";
		String t2 = ";";
		String t3 = "=";
		String t4 = "/";
		
		char[] ch = in.toCharArray();
		
		if(ch.length > 0){
			if(ch[0] == t4.charAt(0)){
				return null;
			}
		if(ch[0] == t1.charAt(0)){
			if(in.substring(1).matches("\\d+")){
				
			al.add(0,in.substring(1));
			String i = converter(al);
			System.out.println(" numbers " + in);
			return i;
			}
			String test = symbols.get(in.substring(1));
			if(test == null){
			 symbols.put(in.substring(1),Integer.toString(nextSpace));
			 nextSpace++;
			 test = symbols.get(in.substring(1));	
			}

			al.add(0,test);
			String z = converter(al);
			return z;
			
		}
		int c1 = 0;
		String[] splitz = in.split("/");

		char[] ch2 = splitz[0].toCharArray();	
		while(c1 < ch2.length){
		if(	ch2[c1] == t2.charAt(0)){
			
			Scanner scan2 = new Scanner(splitz[0]).useDelimiter("\u003B");
			
			while(scan2.hasNext()){
				
				al.add(scan2.next().trim());
				
				
			}
			scan2.close();
              return comp2(al);	
              			  
		}			
		if(	ch2[c1] == t3.charAt(0)){

			Scanner scan1 = new Scanner(splitz[0]);
			scan1.useDelimiter("\u003d");
			while(scan1.hasNext()){
				
				al.add(scan1.next().trim());

			}
			scan1.close();
			return comp(al);
			
		}
        c1++;		
		}
			
		
		
	}
	return null;
	}
	//public static String cinstruction(ArrayList<String> input){
		
		
	//}
	public static String converter(ArrayList<String> array){
	//	 int n = Integer.parseInt(aray[0]);
	    // String s1 = array[0];
		 int n = new Scanner(array.get(0)).nextInt();
		 String ss = String.format("%16s", Integer.toBinaryString(n)).replaceAll(" ", "0");
		return ss;
	}
	public static String comp(ArrayList<String> array){

		String s3 = "111";



		String comper = compu.get(array.get(1));
  
		String desti = dest.get(array.get(0));
	
		s3 = s3 + comper + desti + "000";		
		return s3;
		
	}
		public static String comp2(ArrayList<String> array){
		
		String s3 = "111";		
			
       	String comper = compu.get(array.get(0));	
		String jumpi = jump.get(array.get(1));	
		
		s3 = s3 + comper + "000" + jumpi;		
		return s3;
		
	}
	
	
	
	




    public static void main(String[] args) {
	    compu.put("0","0101010");
	compu.put("1","0111111");
	compu.put("\u002D1","0111010");
	compu.put("D","0001100");
	compu.put("A","0110000");
	compu.put("M","1110000");
	compu.put("\0021D","0001101");
	compu.put("\u0021A","0110001");
	compu.put("\u0021M","1110001");
	compu.put("\u002DD","0001111");
	compu.put("\u002DA","0110011");
	compu.put("\u002DM","1110011");
	compu.put("D+1","0011111");
	compu.put("A+1","0110111");
	compu.put("M+1","1110111");
	compu.put("D\u002D1","0001110");
	compu.put("A\u002D1","0110010");
	compu.put("M\u002D1","1110010");
	compu.put("D+A","0000010");
	compu.put("D+M","1000010");
	compu.put("D\u002DA","0010011");
	compu.put("D\u002DM","1010011");
	compu.put("A\u002DD","0000111");
	compu.put("M\u002DD","1000111");
	compu.put("D\u0026A","0000000");
	compu.put("D\u0026M","1000000");
	compu.put("D\u007CA","0010101");
	compu.put("D\u007CM","1010101");
      
     	dest.put(null,"000");
	dest.put("M","001");
	dest.put("D","010");
	dest.put("MD","011");
	dest.put("A","100");
	dest.put("AM","101");
	dest.put("AD","110");
	dest.put("AMD","111");
	
	jump.put(null, "000");
	jump.put("JGT","001");
	jump.put("JEQ","010");
	jump.put("JGE","011");
	jump.put("JLT","100");
	jump.put("JNE","101");
	jump.put("JLE","110");
	jump.put("JMP","111");
	
	symbols.put("R0", "0");
	symbols.put("R1", "1");
	symbols.put("R2", "2");
	symbols.put("R3", "3");
	symbols.put("R4", "4");
	symbols.put("R5", "5");
	symbols.put("R6", "6");
	symbols.put("R7", "7");
	symbols.put("R8", "8");
	symbols.put("R9", "9");
	symbols.put("R10", "10");
	symbols.put("R11", "11");
	symbols.put("R12", "12");
	symbols.put("R13", "13");
	symbols.put("R14", "14");
	symbols.put("R15", "15");
	symbols.put("SCREEN", "16384");
	symbols.put("KBD", "24576");
	symbols.put("SP", "0");
	symbols.put("LCL", "1");
	symbols.put("THIS", "3");	
	symbols.put("THAT", "4");
	
    File file = 
      new File("C:\\Users\\M!ke\\Downloads\\nand2tetris\\nand2tetris\\projects\\06\\rect\\Rect.asm"); 
	 try {
		 BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\M!ke\\Downloads\\nand2tetris\\nand2tetris\\projects\\06\\rect\\Rect.hack"));
    
	  try
	  {
    Scanner sc1 = new Scanner(file); 
	String x;
	  while (sc1.hasNextLine()){
		  
      x = parseFirst(sc1.nextLine());
      
	  if(x != null){
		  counter++; 
	  }		 
	  }
	  
	sc1.close();     
	  }
    catch (FileNotFoundException ex)  
    {

     System.out.println("File not found");
    }

    
	  try
	  {
    Scanner sc = new Scanner(file); 
	String s;
	  while (sc.hasNextLine()){
		  
      s = parse(sc.nextLine());
      if(s != null)System.out.println(s);
	  if(s != null){
		  writer.write(s );
		 writer.newLine(); 
	  }		 
	  }
	  
	sc.close();     
	  }
    catch (FileNotFoundException ex)  
    {

     System.out.println("File not found");
    }

writer.close();	
	 }
	catch (IOException e){
		System.out.println(e);
	}
}
}
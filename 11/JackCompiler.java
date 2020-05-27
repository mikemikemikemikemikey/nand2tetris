import java.util.*;
import java.io.*; 
import java.util.regex.*;
import java.util.Map; 
import java.util.HashMap; 

public class JackCompiler {
	
  public static class TokenObject {
	  public String token;
	  public String type;
	  public TokenObject(String t1, String t2){
		 token = t1;
         type = t2;		 
	  }
	  public String getToken(){
		  return token;
	  }
	  public String getType(){
		  return type;
	  }
	  
  }

  public static class JackTokenizer{
  public Scanner sc;
  public String cToken;
  public ArrayList<TokenObject> tokenList;
  public String regularX = "\\w*|[\\.\\(\\)\\{\\}\\<\\>\\=\\+\\*\\-;]|//|/\\*\\*||\"\"";
  public int counter = 0;
  public JackTokenizer(File file){
	  
	  	  try
	  {
    sc = new Scanner(file);
	  } catch(FileNotFoundException ex){
		  
		  System.out.println("File not found");
	  }
	  tokenList = new ArrayList<TokenObject>();
	  
  }
  
  public boolean hasMoreTokens(){
	  return sc.hasNext();
  }
  
  public void advance(){
	  
      cToken = sc.next();

	  if(cToken.equals("//")){
		  if(sc.hasNextLine()){
			 String skip = sc.nextLine();			 		
			  return;
		  }
	  }
      if(cToken.equals("/**")){
		  String x = sc.findWithinHorizon(".*\\*/",1000);
		 
		  return;
	  }
		  
	  
	  if(cToken.matches("(\\w+\\W+.*)|(\\W\\W+)|(\\W+\\w+.*)")){
		 
		 Pattern p1 = Pattern.compile("\\w+");
		 Matcher m1 = p1.matcher(cToken);
		 Pattern p2 = Pattern.compile("\\W+");
		 Matcher m2 = p2.matcher(cToken);
		 
		 
		 
		 if(cToken.matches("(\\w+\\W+.*)")){
         if (m1.find()){ 
         cToken = m1.group(); tokenList.add(new TokenObject(cToken, this.tokenType()));		 
       
           }
		 }
		 
	     while(m2.find()){
			  String multiSymbol = m2.group();
			  
			  int i = 0;
			  while(i < multiSymbol.length()){
				  if(multiSymbol.charAt(i) == 34){
					  String quote = "";
					    if (m1.find()){quote  = quote + m1.group();}
					     quote = quote + sc.findWithinHorizon(".*\"",100); 
						 
					  //System.out.println("quotes " + quote.substring(0, quote.length() - 1));
					  tokenList.add(new TokenObject(quote.substring(0, quote.length() - 1), "STRING_CONSTANT"));
					  return;
				  }
				  cToken = Character.toString(multiSymbol.charAt(i)); 
				  tokenList.add(new TokenObject(cToken, this.tokenType()));
		      //System.out.println(multiSymbol.charAt(i)); 
              i++;			  
			  }
		       
		      if (m1.find()){cToken = m1.group(); tokenList.add(new TokenObject(cToken, this.tokenType()));}
		 }
          	return;
		 
          		 
	  }

	  tokenList.add(new TokenObject(cToken, this.tokenType()));
	  	  

	  
	  counter++;
  }
	  
  public String tokenType(){
  if(cToken.matches("[\\.\\(\\)\\{\\}\\<\\>\\=\\+\\*\\-\\;\\,/\\[\\]]")){
	 return "SYMBOL"; 
    } else if(cToken.matches("class|constructor|function|method|field|int|static|var|char|boolean|void|true|false|null|this|let|do|if|else|while|return")){
	 return "KEYWORD";
	} else if(cToken.matches("\\d+")){
		return "INT_CONSTANT";
	} else if(cToken.matches("\"\\.*\"")){
		return "STRING_CONSTANT";
	} else return "IDENTIFIER";
	
  }
  

}

public static class CompilationEngine{
	
	public ArrayList<TokenObject> tokens;
	private SymbolTable classTable;
	private SymbolTable methodTable;
	private String className;
	public int c1;
	private VMWriter wr;
	private int expArgs;
	private int nParams;
	private int loopNum;
	private int termArgs;
	private int functionVars;
	private int fieldVars;
	private boolean methodChecker;
	private boolean newCheck;
	private String routineName;
    public BufferedWriter writer;
	private boolean isArray;
	public CompilationEngine(ArrayList<TokenObject> ts, File file){
		try {

			writer = new BufferedWriter(new FileWriter(file+"1"));
		
		} catch (IOException e){
			System.out.println(e);
		}	
		tokens = ts;
		c1 = 0;
		classTable = new SymbolTable();
		methodTable = new SymbolTable();
		wr = new VMWriter(file);
		className = "";
		expArgs = 0;
		termArgs = 0;
		nParams = 0;
		loopNum = 0;
		functionVars = 0;
		fieldVars = 0;
	}
	public String statement(){
		String s1 = ("<" + tokens.get(c1).getType().toLowerCase() + "> " + tokens.get(c1).getToken().toLowerCase() + " </" + tokens.get(c1).getType().toLowerCase() + ">\n");
		c1++;
		return s1;
	    
	}
	public void compileClass(){
	
	fieldVars = 0;
	classTable.startSubroutine();	
	
	//writer.write(this.statement());
	c1++;
	className = tokens.get(c1).getToken();
	
	//writer.write(this.statement());
	c1++;
   // writer.write(this.statement());
    c1++;	
	  while(tokens.get(c1).getToken().matches("static|field")){
		  this.compileClassVarDec();
	  }
	  //System.out.println(tokens.get(c1).getToken());
	  while(tokens.get(c1).getToken().matches("constructor|function|method")){
		  this.compileSubroutineDec();
	  }
	//writer.write(this.statement());
	c1++;
	
	//System.out.println("Class Table");
	/*classTable.getMap().forEach((k,v) -> System.out.println("Name = "
                + k + ", Type  = " + v.getType() + ", Kind = " + v.getKind()
				+ ", index = " + v.getIndex())); */
	//writer.write("</class>\n");

	}
	public void compileClassVarDec(){

		
		String name = tokens.get(c1+2).getToken();
		String kind = tokens.get(c1).getToken();
		if(kind.equals("field")){
			kind = "this";
		}
		String type = tokens.get(c1 + 1).getToken();
		classTable.define(name, kind, type);
		if(kind.equals("this")){
			fieldVars++;
		}
		//writer.write(this.statement());
		//writer.write(this.statement());
		//writer.write(this.statement());
		c1 = c1 +3;
		while(tokens.get(c1).getToken().matches(",")){
			//writer.write(this.statement());
			c1++;
		classTable.define(tokens.get(c1).getToken(),kind, type);			
			//writer.write(this.statement());
			c1++;
				if(kind.equals("this")){
			       fieldVars++;
		         }
		}
		//writer.write(this.statement());
		c1++;
	  //  writer.write("</classVariableDec>\n");
	
	}		
		
	public void compileSubroutineDec(){

		System.out.println("<subroutineDec>\n");
    methodTable.startSubroutine();
	functionVars = 0;
	methodChecker = false;
    if(tokens.get(c1).getToken().equals("method")){
	methodChecker = true;
    methodTable.define("this", "argument", this.className);
	functionVars++;
	}
    if(tokens.get(c1).getToken().equals("constructor")){
    wr.writePush("constant", fieldVars);
	wr.writeCall("Memory.alloc", 1);
	wr.writePop("pointer", 0);
	routineName = "new";
	}else{		
	routineName = tokens.get(c1+2).getToken();
	}
	//writer.write(this.statement());
	c1++;
	//writer.write(this.statement());
	c1++;
	//writer.write(this.statement());
	c1++;
	//writer.write(this.statement());
	c1++;
	nParams = 0;
	   this.compileParameterList();
	   
	   //wr.writeFunction(className + "." + routineName, nParams);
	//writer.write(this.statement());
	c1++;
	   this.compileSubroutineBody();
	       /* System.out.println("Method Man Table");
	        methodTable.getMap().forEach((k,v) -> System.out.println("Name = "
                + k + ", Type  = " + v.getType() + ", Kind = " + v.getKind()
				+ ", index = " + v.getIndex())); 	*/   
	   //writer.write("</subroutineDec>\n");
	
	}		

	public void compileParameterList(){

		//writer.write("<parameterList>\n");
	if(tokens.get(c1).getType().matches("KEYWORD|IDENTIFIER")){
		
		methodTable.define(tokens.get(c1+1).getToken(),"argument"
		,tokens.get(c1).getToken());
		nParams++;
		functionVars++;
		//writer.write(this.statement());
		c1++;
		//writer.write(this.statement());
		c1++;
		 while(tokens.get(c1).getToken().matches(",")){
			 nParams++;
			 functionVars++;
			 //writer.write(this.statement());
			 c1++;
	    methodTable.define(tokens.get(c1+1).getToken(),"argument"
		,tokens.get(c1).getToken());
			// writer.write(this.statement());
			 c1++;
		     //writer.write(this.statement());
			 c1++;
		 }
	}
	//writer.write("</parameterList>\n");
			
	}
	public void compileSubroutineBody(){

		//writer.write("<subroutineBody>\n");
	//writer.write(this.statement());
	c1++;
	newCheck = false;
	 while(tokens.get(c1).getToken().matches("var")){
		 compileVarDec();
	 }
	 if(routineName.equals("new")){
	   wr.writeFunction(className + "." + routineName, functionVars);
	   wr.writePush("constant", fieldVars);
	   wr.writeCall("Memory.alloc", 1);
	   wr.writePop("pointer", 0);
     newCheck = true;	   
	 }else if(methodChecker == true){		 
	 wr.writeFunction(className + "." + routineName, functionVars);
	 wr.writePush("argument", 0);
	 wr.writePop("pointer", 0);
	 }else{
	 wr.writeFunction(className + "." + routineName, functionVars);	 
	 }
	 compileStatements();
	 //writer.write(this.statement());
	 c1++;
	//writer.write("</subroutineBody>\n");

	}		
	
	public void compileVarDec(){

		//writer.write("<VarDec>\n");
		String name = tokens.get(c1+2).getToken();
		String kind = "local";
		String type = tokens.get(c1 + 1).getToken();
         methodTable.define(name,kind,type);
         functionVars++;		 
		 //writer.write(this.statement());
		 //writer.write(this.statement());
	     //writer.write(this.statement());
		 c1 = c1 +3;
		 while(tokens.get(c1).getToken().matches(",")){
			//writer.write(this.statement());
			c1++;
			methodTable.define(tokens.get(c1).getToken(),kind,type);
            //writer.write(this.statement());
            c1++;	
           functionVars++;			
		 }
		 //writer.write(this.statement());
		 c1++;
		 //writer.write("</VarDec>\n");
	
	}		
	
	public void compileStatements(){

            //writer.write("<statements>\n");
	while(tokens.get(c1).getToken().matches("let|if|do|while|return")){
		System.out.println("STATEMENTS" + tokens.get(c1).getToken());
		if(tokens.get(c1).getToken().matches("let")){
			this.compileLet();
		}
		
		if(tokens.get(c1).getToken().matches("if")){
			this.compileIf();
		}
		if(tokens.get(c1).getToken().matches("do")){
			this.compileDo();
		}
		if(tokens.get(c1).getToken().matches("while")){
			this.compileWhile();
		}
		if(tokens.get(c1).getToken().matches("return")){
		    this.compileReturn();	
		}
	}
		//writer.write("</statements>\n");

		
	}		
	
	public void compileLet(){
	
		//writer.write("<letStatement>\n");
	//writer.write(this.statement());
	c1++;
	//writer.write(this.statement());
	String letVar = tokens.get(c1).getToken();
	c1++;
	 if(tokens.get(c1).getToken().matches("\\[")){
		if(classTable.getMap().containsKey(letVar)){
		wr.writePush(classTable.getMap().get(letVar).getKind(),
		 classTable.getMap().get(letVar).getIndex());
		}else if(methodTable.getMap().containsKey(letVar)){
		wr.writePush(methodTable.getMap().get(letVar).getKind(),
		 methodTable.getMap().get(letVar).getIndex());
		}
		 //writer.write(this.statement());
		 c1++;
		 this.compileExpression();
		 wr.writeArithmetic("add");		 
		 //writer.write(this.statement());
		 c1++;
		 c1++;
		 this.compileExpression();
		 wr.writePop("temp", 0);
		 wr.writePop("pointer", 1);
		 wr.writePush("temp", 0);
		 wr.writePop("that", 0);
		 c1++;
		
	 }else{
	 
	 //writer.write(this.statement());
	 c1++;
	 this.compileExpression();
	 if(classTable.getMap().containsKey(letVar)){
		 if(classTable.getMap().get(letVar).getKind().equals("static")){
	         wr.writePop("static", classTable.getMap().get(letVar).getIndex());
		 }else{
			 wr.writePop("this", classTable.getMap().get(letVar).getIndex());
		 }
	 }else{
		 if(methodTable.getMap().get(letVar).getKind().equals("local")){
	 	wr.writePop("local", methodTable.getMap().get(letVar).getIndex()); 
		 }else{
			 wr.writePop("argument", methodTable.getMap().get(letVar).getIndex()); 
		 }			 
	 }
	 
	 //writer.write(this.statement());
	 c1++;
	 //writer.write("</letStatement>\n");
	 }
	
	
	}		
	
	public void compileIf(){
	
		//writer.write("<ifStatement>\n");
	//writer.write(this.statement());
	//writer.write(this.statement());
	c1 = c1 +2;
	int ifNum = loopNum;
	String label1 = className + ".if." + ifNum;
	String label2 = className + ".else." + ifNum;
	loopNum++;
	 this.compileExpression();
	 wr.writeArithmetic("not");
	 wr.writeIf(label1);
	//writer.write(this.statement());
	//writer.write(this.statement());
	c1 = c1 +2;
	 this.compileStatements();
	//writer.write(this.statement());
	c1++;
	wr.writeGoto(label2);
	if(tokens.get(c1).getToken().matches("else")){
		wr.writeLabel(label1);
	//writer.write(this.statement());
    //writer.write(this.statement());
	c1 = c1+2;
     this.compileStatements();
    //writer.write(this.statement());
    c1++;	
	wr.writeLabel(label2);
	}else{
		wr.writeLabel(label1);
		wr.writeLabel(label2);
	}

	//writer.write("</ifStatement>\n");
	
	}		
	
	public void compileWhile(){
	
		//writer.write("<whileStatement>\n");
	//writer.write(this.statement());
	//writer.write(this.statement());
	c1 = c1 +2;
	int loopStart = loopNum;
	wr.writeLabel(className+ ".loopStart." + loopStart);
	loopNum++;
	 this.compileExpression();
	 wr.writeArithmetic("not");
	 wr.writeIf(className + ".loopEnd." + loopStart);
	//writer.write(this.statement());
	//writer.write(this.statement());
	c1 = c1 +2;
	 this.compileStatements();
	//writer.write(this.statement());
	c1++;
	wr.writeGoto(className+ ".loopStart." + loopStart);
	wr.writeLabel(className + ".loopEnd." + loopStart);
	//writer.write("</whileStatement>\n");
		
	}		
	
	public void compileDo(){
	
		//writer.write("<doStatement>\n");
		//writer.write(this.statement());
		c1++;
		if(tokens.get(c1+1).getToken().matches("\\(")){
	String thisFunction = tokens.get(c1).getToken();
	//writer.write(this.statement());
	//writer.write(this.statement());
    c1 = c1+2;
    expArgs = 1;
	if(newCheck == true){
		wr.writePush("pointer", 0);
	}else{
    wr.writePush("argument", 0);
	}	
    this.compileExpressionList();
	wr.writeCall(className + "." + thisFunction, expArgs);
	wr.writePop("temp", 0);
    //writer.write(this.statement());
    //writer.write(this.statement());
    c1 = c1 +2;
	
		} else{   
	
	//writer.write(this.statement());
	//writer.write(this.statement());
	//writer.write(this.statement());
    //writer.write(this.statement());
	String methCheck = tokens.get(c1).getToken();
	String fun1 = "";
	expArgs = 0;
	if(classTable.getMap().containsKey(methCheck)){
		fun1 = classTable.getMap().get(methCheck).getType()+
		tokens.get(c1+1).getToken()+tokens.get(c1+2).getToken();
		wr.writePush(classTable.getMap().get(methCheck).getKind(),
		 classTable.getMap().get(methCheck).getIndex());
		 expArgs++;
	}else if(methodTable.getMap().containsKey(methCheck)){
		fun1 = methodTable.getMap().get(methCheck).getType()+
		tokens.get(c1+1).getToken()+tokens.get(c1+2).getToken();
		wr.writePush(methodTable.getMap().get(methCheck).getKind(),
		 methodTable.getMap().get(methCheck).getIndex());
		 expArgs++;
		 
	} else{		
	 fun1 = tokens.get(c1).getToken()+tokens.get(c1+1).getToken()+
      tokens.get(c1+2).getToken();	
	}
	c1 = c1 +4;
	 this.compileExpressionList();
	 wr.writeCall(fun1, expArgs);
	 wr.writePop("temp", 0);
	//writer.write(this.statement());
	c1++;
	//writer.write(this.statement());
	c1++;
	}
	
    //writer.write("</doStatement>\n");	
	
		
	}		
	
	public void compileReturn(){
	
		//writer.write("<return>\n");
	//writer.write(this.statement());
	c1++;
	if(tokens.get(c1).getToken().matches(";")){
		wr.writePush("constant", 0);
		wr.writeReturn();
		//writer.write(this.statement());
		c1++;
	}else{
	this.compileExpression();
	wr.writeReturn();
    //writer.write(this.statement());
    c1++;	
	}
	//writer.write("</return>\n");
		
	}		
	
	public void compileExpression(){

		//writer.write("<expression>\n");
	    this.compileTerm();
		while(tokens.get(c1).getToken().matches("[\\+\\-\\*/\\&\\|\\<\\>\\=]")){
			//writer.write(this.statement());
			String sym = tokens.get(c1).getToken();
			
			c1++;
			this.compileTerm();
			
			if(sym.equals("+")){
				wr.writeArithmetic("add");
			}else if(sym.equals("-")){
				wr.writeArithmetic("sub");
			}else if(sym.equals("*")){
				wr.writeCall("Math.multiply", 2);
			}else if(sym.equals("/")){
				wr.writeCall("Math.divide", 2);
			}else if(sym.equals("<")){
				wr.writeArithmetic("lt");
			}else if(sym.equals(">")){
				wr.writeArithmetic("gt");
			}else if(sym.equals("=")){
				wr.writeArithmetic("eq");
			}else if(sym.equals("&")){
				wr.writeArithmetic("and");
			}else if(sym.equals("|")){
				wr.writeArithmetic("or");
			}
			
		}
		//writer.write("</expression>\n");
	
	}		
	
	public void compileTerm(){
	
		//writer.write("<term>\n");
		System.out.println("TERM " + tokens.get(c1).getToken()); 
		if(tokens.get(c1).getToken().matches("\\-|\\~")){					
			//writer.write(this.statement());
			String fu = tokens.get(c1).getToken();
			c1++;
			int holder = c1;
			this.compileTerm();
			if(tokens.get(holder).getType().equals("INT_CONSTANT")){				
			wr.writeArithmetic("neg");
			}else{
			wr.writeArithmetic("not");
			}				
		}else if(tokens.get(c1).getType().matches("IDENTIFIER")){
			String methodCheck = tokens.get(c1).getToken();
			if(tokens.get(c1+1).getToken().matches("\\[")){
		       if(classTable.getMap().containsKey(methodCheck)){
		          wr.writePush(classTable.getMap().get(methodCheck).getKind(),
		          classTable.getMap().get(methodCheck).getIndex());
		      }else if(methodTable.getMap().containsKey(methodCheck)){
		          wr.writePush(methodTable.getMap().get(methodCheck).getKind(),
		          methodTable.getMap().get(methodCheck).getIndex());
		      }
				//writer.write(this.statement());
				//writer.write(this.statement());
				c1 = c1+2;
				this.compileExpression();
                wr.writeArithmetic("add");
                				
				wr.writePop("pointer", 1);
				wr.writePush("that", 0);
				//writer.write(this.statement());
				c1++;
			}else if(tokens.get(c1+1).getToken().matches("\\.")){
				String fCall = "";
				termArgs = 0;
				System.out.println("METH CHeck " + methodCheck);
				  if(classTable.getMap().containsKey(methodCheck)){
					wr.writePush(classTable.getMap().get(methodCheck).getKind(),
                      	classTable.getMap().get(methodCheck).getIndex());
					 fCall = classTable.getMap().get(methodCheck).getType()+
                        "." + tokens.get(c1+2).getToken();
                       termArgs++;						
				  } else if(methodTable.getMap().containsKey(methodCheck)){
					  wr.writePush(methodTable.getMap().get(methodCheck).getKind(),
                      	methodTable.getMap().get(methodCheck).getIndex());
					 fCall = methodTable.getMap().get(methodCheck).getType()+
                        "." + tokens.get(c1+2).getToken();
                     termArgs++;						
				  } else{
				//writer.write(this.statement());
				//writer.write(this.statement());
				//writer.write(this.statement());
				//writer.write(this.statement());
				System.out.println("MEth 2 " + methodCheck);
			    fCall = tokens.get(c1).getToken() + "." + tokens.get(c1+2).getToken();
				  }
				c1 = c1 +4;
				
				this.compileExpressionList();
				wr.writeCall(fCall, termArgs); 
				//writer.write(this.statement());
				c1++;
			}else{
				  if(classTable.getMap().containsKey(methodCheck)){
					wr.writePush(classTable.getMap().get(methodCheck).getKind(),
                      	classTable.getMap().get(methodCheck).getIndex());					 					
				  } else if(methodTable.getMap().containsKey(methodCheck)){
					  wr.writePush(methodTable.getMap().get(methodCheck).getKind(),
                      	methodTable.getMap().get(methodCheck).getIndex());						
				  }
				//writer.write(this.statement());
				c1++;
			}
				
		}else if(tokens.get(c1).getToken().matches("\\(")){
			//writer.write(this.statement());
			c1++;
			this.compileExpression();
			//writer.write(this.statement());
			c1++;
		} else if(tokens.get(c1).getType().equals("KEYWORD")){
		    if(tokens.get(c1).getToken().equals("true")){
				wr.writePush("constant", 1);
				wr.writeArithmetic("neg");
			}else if(tokens.get(c1).getToken().equals("this")){
				wr.writePush("pointer", 0);
			}else{
				
				wr.writePush("constant", 0);
			}
			c1++;
		}else if(tokens.get(c1).getType().equals("STRING_CONSTANT")){
			String s = tokens.get(c1).getToken();
			wr.writePush("constant", s.length());
			wr.writeCall("String.new", 1);
			wr.writePop("temp", 0);			
			for(int i =0; i< s.length();i++){
		    wr.writePush("temp", 0);
			wr.writePush("constant", s.charAt(i));
            wr.writeCall("String.appendChar", 2); 
            wr.writePop("temp", 0);			
			}
			wr.writePush("temp", 0);
			c1++;
			
		}else{
			//writer.write(this.statement());
			if(tokens.get(c1).getType().equals("INT_CONSTANT")){
			wr.writePush("constant", Integer.valueOf(tokens.get(c1).getToken()));						
			}
			c1++;
		}
		
	  //writer.write("</term>\n");
	
	}		
	
	public void compileExpressionList(){

		//writer.write("<expressionList>\n");
		if(tokens.get(c1).getToken().matches("\\)")){
			//writer.write("</expressionList>\n");
			return;
		}
	 
	  this.compileExpression();
	   expArgs++;
	   termArgs++;
	   while(tokens.get(c1).getToken().matches(",")){
		
		 //writer.write(this.statement());
		 c1++;
         this.compileExpression();
		 expArgs++;
		 termArgs++;
		 
	   }
	   //writer.write("</expressionList>\n");

	}		
	
	public  void close(){
		
		wr.close();
		try{
		writer.close();	
				} catch (IOException e){
			System.out.println(e);
			
		}
	}
	
}


public static class SymbolObject{
	private String kind;
	private String type;
	private int index;
	
	public SymbolObject(String k, String t, int i){
		this.kind = k;
		this.type = t;
		this.index = i;
	}
	public String getKind(){
		return this.kind;
	}
    public String getType(){
		return this.type;
	}
	public int getIndex(){
		return this.index;
	}

	
}


public static class SymbolTable{
	
	private HashMap<String, SymbolObject> table;
	private int stat;
	private int field;
	private int arg;
	private int var;
	
	public SymbolTable(){
		this.table = new HashMap<String, SymbolObject>();
	}
	public HashMap<String, SymbolObject> getMap(){
		return this.table;
	}
	public void startSubroutine(){
		this.table.clear();
		this.stat = 0;
		this.field = 0;
		this.arg = 0;
		this.var = 0;
	}
	public void define(String name, String kind, String type){
		
		int index = this.varCount(kind);
		table.put(name, new SymbolObject(kind,type,index));
	}
	public int varCount(String k1){
		
		if(k1.equals("static")){ 
		   this.stat++;
		   return this.stat - 1;
		}
		if(k1.equals("this")){
		   this.field++;
		   return this.field - 1;
		}
		if(k1.equals("argument")){
			this.arg++;
			return this.arg -1;
		}
		
		this.var++;
		return this.var -1;
		
	}
	public String kindOf(String name){
		return table.get(name).getKind();
	}
	public String typeOf(String name){
		return table.get(name).getType();
	}
	public int indexOf(String name){
		return table.get(name).getIndex();
	}
	
}

public static class VMWriter{
	private BufferedWriter writer;
	
	public VMWriter(File file){
		
		try {

			writer = new BufferedWriter(new FileWriter(file));
		
		} catch (IOException e){
			System.out.println(e);
		}
	}

	public void writePush(String segment, int index){
		try {

		 writer.write("push " + segment + " " + index + "\n");	
		
		} catch (IOException e){
			System.out.println(e);
		}
	}
    public void writePop(String segment, int index){
    	try {
           writer.write("pop " + segment + " " + index + "\n");
			
		
		} catch (IOException e){
			System.out.println(e);
		}
	}
    public void writeArithmetic(String command){
		try {
			
        writer.write(command.toLowerCase() + "\n");
			
		
		} catch (IOException e){
			System.out.println(e);
		}
	}		
	public void writeLabel(String label){
		try {
        writer.write("label " + label + "\n");
			
		
		} catch (IOException e){
			System.out.println(e);
		}
	}
	public void writeGoto(String label){
		try {
         writer.write("goto " + label + "\n");
			
		
		} catch (IOException e){
			System.out.println(e);
		}
	}
	public void writeIf(String label){
		try {
        writer.write("if-goto " + label + "\n");
			
		
		} catch (IOException e){
			System.out.println(e);
		}
	}
	public void writeCall(String name, int args){
		try {
         writer.write("call " + name + " " + args + "\n");
			
		
		} catch (IOException e){
			System.out.println(e);
		}
	}
	public void writeFunction(String name, int n){
		try {
         writer.write("function " + name + " " + n + "\n");
			
		
		} catch (IOException e){
			System.out.println(e);
		}
	}
	public void writeReturn(){
		try {
         writer.write("return\n");
			
		
		} catch (IOException e){
			System.out.println(e);
		}
	}
	public void close(){
		try {

		writer.close();	
		
		} catch (IOException e){
			System.out.println(e);
		}
	}
}



public static void main(String[] args){
		
		File file3 = new File(args[0]);
	    FilenameFilter txtFileFilter = new FilenameFilter()
        {
            
            public boolean accept(File dir, String name)
            {
                if(name.endsWith(".jack"))
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
	
	for(File file : files){
		System.out.println(file.getName());

	JackTokenizer jk = new JackTokenizer(file);
	
    String[] f1 = file.getName().split("\\.");
    
	String f2 = args[0] + "\\" + f1[0] + ".vm";
	
	
	
	File f3 = new File(f2);
	//System.out.println("SAVE Name " + f2);
	
	
	jk.advance();
	while(jk.hasMoreTokens()){
		jk.advance();
	}
	CompilationEngine comp = new CompilationEngine(jk.tokenList, f3);
	comp.compileClass();
	comp.close();
//	for(TokenObject a : jk.tokenList){
	//	System.out.println(a.getToken() + " " + a.getType());
		//}
	}

	
}

}
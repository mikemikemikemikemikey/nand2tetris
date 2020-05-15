import java.util.*;
import java.io.*; 
import java.util.regex.*;

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
		  System.out.println(x);
		  return;
	  }
		  
	  
	  if(cToken.matches("(\\w+\\W+.*)|(\\W\\W+)|(\\W+\\w+.*)")){
		  System.out.println("Token " + cToken);
		 Pattern p1 = Pattern.compile("\\w+");
		 Matcher m1 = p1.matcher(cToken);
		 Pattern p2 = Pattern.compile("\\W+");
		 Matcher m2 = p2.matcher(cToken);
		 
		 
		 
		 if(cToken.matches("(\\w+\\W+.*)")){
         if (m1.find()){ 
         cToken = m1.group(); tokenList.add(new TokenObject(cToken, this.tokenType()));		 
        // System.out.println(m1.group());
           }
		 }
		 
	     while(m2.find()){
			  String multiSymbol = m2.group();
			  System.out.println("MULTI " + multiSymbol);
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
	public int c1;
    public BufferedWriter writer;
	public CompilationEngine(ArrayList<TokenObject> ts, File file){
		try {

			writer = new BufferedWriter(new FileWriter(file));
		
		} catch (IOException e){
			System.out.println(e);
		}	
		tokens = ts;
		c1 = 0;
	}
	public String statement(){
		String s1 = ("<" + tokens.get(c1).getType().toLowerCase() + "> " + tokens.get(c1).getToken().toLowerCase() + " </" + tokens.get(c1).getType().toLowerCase() + ">\n");
		c1++;
		return s1;
	    
	}
	public void compileClass(){
	
	try{
		
	writer.write("<class>\n");
	writer.write(this.statement());
	writer.write(this.statement());
    writer.write(this.statement());	
	  while(tokens.get(c1).getToken().matches("static|field")){
		  this.compileClassVarDec();
	  }
	  System.out.println(tokens.get(c1).getToken());
	  while(tokens.get(c1).getToken().matches("constructor|function|method")){
		  this.compileSubroutineDec();
	  }
	writer.write(this.statement());
	writer.write("</class>\n");
	} catch (IOException e){
			System.out.println(e);
			
		}	
	}
	public void compileClassVarDec(){
	try{
		writer.write("<classVariableDec>\n");
		writer.write(this.statement());
		writer.write(this.statement());
		writer.write(this.statement());
		while(tokens.get(c1).getToken().matches(",")){
			writer.write(this.statement());
			writer.write(this.statement());
		}
		writer.write(this.statement());
	    writer.write("</classVariableDec>\n");
	} catch (IOException e){
			System.out.println(e);
			
		}	
	}		
		
	public void compileSubroutineDec(){
	try{
		writer.write("<subroutineDec>\n");

	writer.write(this.statement());
	writer.write(this.statement());
	writer.write(this.statement());
	writer.write(this.statement());
	   this.compileParameterList();
	writer.write(this.statement());
	   this.compileSubroutineBody();
	   writer.write("</subroutineDec>\n");
	} catch (IOException e){
			System.out.println(e);
			
		}	
	}		

	public void compileParameterList(){
	try{
		writer.write("<parameterList>\n");
	if(tokens.get(c1).getType().matches("KEYWORD|IDENTIFIER")){
		writer.write(this.statement());
		writer.write(this.statement());
		 while(tokens.get(c1).getToken().matches(",")){
			 writer.write(this.statement());
			 writer.write(this.statement());
		     writer.write(this.statement());
		 }
	}
	writer.write("</parameterList>\n");
	} catch (IOException e){
			System.out.println(e);
			
		}			
	}
	public void compileSubroutineBody(){
	try{
		writer.write("<subroutineBody>\n");
	writer.write(this.statement());
	 while(tokens.get(c1).getToken().matches("var")){
		 compileVarDec();
	 }
	 compileStatements();
	 writer.write(this.statement());
	writer.write("</subroutineBody>\n");
	} catch (IOException e){
			System.out.println(e);
			
		}	
	}		
	
	public void compileVarDec(){
	try{
		writer.write("<VarDec>\n");
		 writer.write(this.statement());
		 writer.write(this.statement());
	     writer.write(this.statement());
		 while(tokens.get(c1).getToken().matches(",")){
			writer.write(this.statement());
            writer.write(this.statement());			
		 }
		 writer.write(this.statement());
		 writer.write("</VarDec>\n");
	} catch (IOException e){
			System.out.println(e);
			
		}	
	}		
	
	public void compileStatements(){
		try{
            writer.write("<statements>\n");
	while(tokens.get(c1).getToken().matches("let|if|do|while|return")){
		
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
		writer.write("</statements>\n");
	} catch (IOException e){
			System.out.println(e);
			
		}
		
	}		
	
	public void compileLet(){
	try{
		writer.write("<letStatement>\n");
	writer.write(this.statement());
	writer.write(this.statement());
	 if(tokens.get(c1).getToken().matches("\\[")){
		 writer.write(this.statement());
		  this.compileExpression();
		 writer.write(this.statement());
	 }
	 writer.write(this.statement());
	 this.compileExpression();
	 writer.write(this.statement());
	 writer.write("</letStatement>\n");
	
	
	} catch (IOException e){
			System.out.println(e);
			
		}	
	}		
	
	public void compileIf(){
	try{
		writer.write("<ifStatement>\n");
	writer.write(this.statement());
	writer.write(this.statement());
	 this.compileExpression();
	writer.write(this.statement());
	writer.write(this.statement());
	 this.compileStatements();
	writer.write(this.statement());
	if(tokens.get(c1).getToken().matches("else")){
	writer.write(this.statement());
    writer.write(this.statement());
     this.compileStatements();
    writer.write(this.statement());	 
	}
	writer.write("</ifStatement>\n");
	} catch (IOException e){
			System.out.println(e);
			
		}	
	}		
	
	public void compileWhile(){
	try{
		writer.write("<whileStatement>\n");
	writer.write(this.statement());
	writer.write(this.statement());
	 this.compileExpression();
	writer.write(this.statement());
	writer.write(this.statement());
	 this.compileStatements();
	writer.write(this.statement());
	writer.write("</whileStatement>\n");
	} catch (IOException e){
			System.out.println(e);
			
		}	
	}		
	
	public void compileDo(){
	try{
		writer.write("<doStatement>\n");
		writer.write(this.statement());
		if(tokens.get(c1+1).getToken().matches("\\(")){
	writer.write(this.statement());
	writer.write(this.statement());	
    this.compileExpressionList();
    writer.write(this.statement());
    writer.write(this.statement());	
		} else{   
	
	writer.write(this.statement());
	writer.write(this.statement());
	writer.write(this.statement());
    writer.write(this.statement());	
	 this.compileExpressionList();
	writer.write(this.statement());
	writer.write(this.statement());
	}
	
    writer.write("</doStatement>\n");	
	
	}catch (IOException e){
			System.out.println(e);
			
		}	
	}		
	
	public void compileReturn(){
	try{
		writer.write("<return>\n");
	writer.write(this.statement());
	if(tokens.get(c1).getToken().matches(";")){
		writer.write(this.statement());
	}else{
	this.compileExpression();
    writer.write(this.statement());	
	}
	writer.write("</return>\n");
	} catch (IOException e){
			System.out.println(e);
			
		}	
	}		
	
	public void compileExpression(){
	try{
		writer.write("<expression>\n");
	    this.compileTerm();
		//System.out.println("expression token " + tokens.get(c1).getToken());
		while(tokens.get(c1).getToken().matches("[\\+\\-\\*/\\&\\|\\<\\>\\=]")){
			writer.write(this.statement());
			this.compileTerm();
		}
		writer.write("</expression>\n");
	} catch (IOException e){
			System.out.println(e);
			
		}	
	}		
	
	public void compileTerm(){
	try{
		writer.write("<term>\n");
		if(tokens.get(c1).getToken().matches("\\-|\\~")){
			writer.write(this.statement());
			this.compileTerm();
		}else if(tokens.get(c1).getType().matches("IDENTIFIER")){
			if(tokens.get(c1+1).getToken().matches("\\[")){
				writer.write(this.statement());
				writer.write(this.statement());
				this.compileExpression();
				writer.write(this.statement());
			}else if(tokens.get(c1+1).getToken().matches("\\.")){
				writer.write(this.statement());
				writer.write(this.statement());
				writer.write(this.statement());
				writer.write(this.statement());
				 this.compileExpressionList();
				 writer.write(this.statement());
			}else{
				writer.write(this.statement());
			}
				
		}else if(tokens.get(c1).getToken().matches("\\(")){
			writer.write(this.statement());
			this.compileExpression();
			writer.write(this.statement());
		} else {
			writer.write(this.statement());
		}
		//writer.write(this.statement());
	
	  writer.write("</term>\n");
	} catch (IOException e){
			System.out.println(e);
			
		}	
	}		
	
	public void compileExpressionList(){
	try{
		writer.write("<expressionList>\n");
		if(tokens.get(c1).getToken().matches("\\)")){
			writer.write("</expressionList>\n");
			return;
		}
	  this.compileExpression();
	   while(tokens.get(c1).getToken().matches(",")){
		 writer.write(this.statement());
         this.compileExpression();
		 
	   }
	   writer.write("</expressionList>\n");
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
    String f1[] = file.getName().split("\\.");
	String f2 = f1[0];
	f2 = f2 + "1.xml";
	File f3 = new File(f2);
	System.out.println(f2);
	
	
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
package Driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class CallZ3 {
	static public String CallByString(String s) throws InterruptedException{
		File dir = new File("tmp");
		dir.mkdirs();
		File tmp = new File(dir, "tmp.txt");
		Runtime rt = Runtime.getRuntime();  
        String result = "";
		try {
			tmp.createNewFile();
			WriteStringToFile(tmp,s);
            Process proc = rt.exec(new String[]{"lib/z3","tmp/tmp.txt"});  
            InputStream stderr = proc.getErrorStream();  
            InputStreamReader isr = new InputStreamReader(stderr);  
            BufferedReader br = new BufferedReader(isr);  
            InputStreamReader ir=new InputStreamReader(proc.getInputStream());
            LineNumberReader input = new LineNumberReader (ir);

            String line = null;  
            line = input.readLine();
            if(line.equals("(goals")){
            input.readLine();
            while ((line = input.readLine ()) != null){
            	if(line.equals("  :precision precise :depth 1)")) break;
            	result = result + line + "\n";
            }
            } else {
            	result = line;
            	while ((line = input.readLine ()) != null)result = result + line + "\n";
            }
			tmp.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	static void WriteStringToFile(File f, String s) throws IOException{
		FileWriter fileWriter = new FileWriter(f);
		fileWriter.write(s);
		fileWriter.close();
	}
	public static void main(String args[]) throws Exception {

		CallZ3 z3 = new CallZ3();
		System.out.println(z3.CallByString("haha"));

}
}

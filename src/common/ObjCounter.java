package common;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import com.mathworks.toolbox.javabuilder.MWNumericArray;

import security.EncMat;
import security.EncMatMultThread;
import security.PublicKey;
import server.Coordinator;

public class ObjCounter {
	public static void writeObjToFileV2(Object object, String path) {
		FileOutputStream out = null;
		try {
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.getParentFile().mkdirs();
			file.createNewFile();
			out = new FileOutputStream(file);
			ObjectOutputStream objOut = new ObjectOutputStream(out);
//			Random rnd=new Random();
//			int nn=rnd.nextInt(100000);
//			for (int i = 0; i < nn; i++) {
//				objOut.writeObject("abs");
//			}
			objOut.writeObject(object);
			objOut.flush();
			objOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 
	public static long getFileSize(String filename) {
        File file = new File(filename);
        if (!file.exists() || !file.isFile()) {
            System.out.println("文件不存在");
            return -1;
        }
        return file.length();
    }
	public static long getMsgSize(Object obj){
		long tt=System.currentTimeMillis();
		tt=tt % 10000000L;
		Random rd=new Random();
		int ri=rd.nextInt(10000);
		String fname=("objs/f"+tt)+ri;
		writeObjToFileV2(obj, fname);
		File fl=new File(fname);
		long res=fl.length();
//		fl.delete();
		return res;
	}
	public static Object readObjectFromFile(String path)
    {
        Object temp=null;
        File file =new File(path);
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            ObjectInputStream objIn=new ObjectInputStream(in);
            temp=objIn.readObject();
            objIn.close();
            System.out.println("read object success!");
        } catch (IOException e) {
            System.out.println("read object failed");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return temp;
    }
	public static void main(String[] args) {
		String agentName = "coordinator";
		Coordinator ca = new Coordinator(agentName);
		PublicKey pubKey=ca.getPubKey();
		EncMatMultThread mat=EncMatMultThread.readFromTxt("in/t1.txt",pubKey);
		System.out.println(getMsgSize(mat));
//		writeObjToFileV2(mat, "objs/1.txt");
//		EncMatMultThread mat=(EncMatMultThread) readObjectFromFile("objs/1.txt");
//		System.out.println(mat.getPk().getN());
	}
}
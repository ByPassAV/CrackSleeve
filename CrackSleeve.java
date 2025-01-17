import common.*;
import dns.SleeveSecurity;
import java.io.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class CrackSleeve {

    private static String OriginKey40 ="1be5be52c6255c33558e8a1cb667cb06";
    private static String OriginKey41 ="80e32a742060b884419ba0c171c9aa76";
    private static String OriginKey42 ="b20d487addd4713418f2d5a3ae02a7a0";
    private static String OriginKey43 ="3a4425490f389aeec312bdd758ad2b99";
	private static String OriginKey44 ="5e98194a01c6b48fa582a6a9fcbb92d6";
	private static String OriginKey45 ="f38eb3d1a335b252b58bc2acde81b542";
	
    private static byte[] CustomizeKey = null;

    private String DecDir = "Resource/Decode/sleeve";
    private String EncDir = "Resource/Encode/sleeve";


    public static void main(String[] args) throws IOException {
        if (args.length == 0 || args[0].equals("-h") || args[0].equals("--help")) {
            System.out.println("UseAge: CrackSleeve OPTION [key]");
            System.out.println("Options:");
            System.out.println("\tdecode\t\tDecode sleeve files");
            System.out.println("\tencode\t\tEncode sleeve files");
            System.out.println("\tkey\t\tCustomize key string for encode sleeve files");
            System.exit(0);
        }
        String option = args[0];
//        if (option.toLowerCase().equals("encode"))
//        {
//            if (args.length <= 1){
//                System.out.println("[-] Please enter key.");
//                System.exit(0);
//            }
//            String CustomizeKeyStr = args[1];
//            if (CustomizeKeyStr.length() < 16)
//            {
//                System.out.println("[-] key length must be 16.");
//                System.exit(0);
//            }
//            System.out.println("Init Key: "+CustomizeKeyStr.substring(0,16));
//            CustomizeKey = CustomizeKeyStr.substring(0,16).getBytes();
//        }


        CrackSleeve Cracker = new CrackSleeve();
        // 使用正版key初始化SleeveSecurity中的key
        if (option.equals("decode")){
            CrackSleevedResource.Setup(hex2bytes(OriginKey45));
            Cracker.DecodeFile();
        }else if (option.equals("encode")){
            //CrackSleevedResource.Setup(CustomizeKey);
            CrackSleevedResource.Setup(hex2bytes(OriginKey45));
            Cracker.EncodeFile();
        }
    }

	private static byte[] hex2bytes(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2)
      data[i / 2] = (byte)((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16)); 
    return data;
  }
  
    private void DecodeFile() throws IOException {
        // 文件保存目录
        File saveDir = new File(this.DecDir);
        if (!saveDir.isDirectory())
            saveDir.mkdirs();

        // 获取jar文件中sleeve文件夹下的文件列表
        try {
            String path = this.getClass().getClassLoader().getResource("sleeve").getPath();
            String jarPath = path.substring(5,path.indexOf("!/"));
            Enumeration<JarEntry> jarEnum = new JarFile(new File(jarPath)).entries();
            while (jarEnum.hasMoreElements())
            {
                JarEntry Element = jarEnum.nextElement();
                String FileName = Element.getName();
                if (FileName.indexOf("sleeve")>=0 && !FileName.equals("sleeve/")) {
                    System.out.print("[+] Decoding "+FileName+"......");
                    byte[] decBytes = CrackSleevedResource.DecodeResource(FileName);
                    if (decBytes.length > 0) {
                        System.out.println("Done.");
                        CommonUtils.writeToFile(new File(saveDir,"../"+FileName),decBytes);
                    }
                    else
                        System.out.println("Fail.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void EncodeFile(){
        // 文件保存目录
        File saveDir = new File(this.EncDir);
        if (!saveDir.isDirectory())
            saveDir.mkdirs();

        // 获取解密文件列表
        File decDir = new File(this.DecDir);
        File[] decFiles = decDir.listFiles();
        if (decFiles.length == 0) {
            System.out.println("[-] There's no file to encode, please decode first.");
            System.exit(0);
        }

        for (File file : decFiles){
            String filename = decDir.getPath()+"/"+file.getName();
            System.out.print("[+] Encoding " + file.getName() + "......");
            byte[] encBytes = CrackSleevedResource.EncodeResource(filename);
            if (encBytes.length > 0) {
                System.out.println("Done.");
                CommonUtils.writeToFile(new File(saveDir,file.getName()),encBytes);
            }
            else
                System.out.println("Fail.");
        }
    }
}

class CrackSleevedResource{
    private static CrackSleevedResource singleton;

    private SleeveSecurity data = new SleeveSecurity();

    public static void Setup(byte[] paramArrayOfbyte) {
        singleton = new CrackSleevedResource(paramArrayOfbyte);
        //singleton = new CrackSleevedResource(CommonUtils.readResource("resources/cobaltstrike.auth"));
    }

    public static byte[] DecodeResource(String paramString) {
        return singleton._DecodeResource(paramString);
    }

    public static byte[] EncodeResource(String paramString) {
        return singleton._EncodeResource(paramString);
    }

    private CrackSleevedResource(byte[] paramArrayOfbyte) {
        this.data.registerKey(paramArrayOfbyte);
    }

    private byte[] _DecodeResource(String paramString) {
        byte[] arrayOfByte1 = CommonUtils.readResource(paramString);
        if (arrayOfByte1.length > 0) {
            long l = System.currentTimeMillis();
            return this.data.decrypt(arrayOfByte1);
        }
        byte[] arrayOfByte2 = CommonUtils.readResource(paramString);
        if (arrayOfByte2.length == 0) {
            CommonUtils.print_error("Could not find sleeved resource: " + paramString + " [ERROR]");
        } else {
            CommonUtils.print_stat("Used internal resource: " + paramString);
        }
        return arrayOfByte2;
    }

    private byte[] _EncodeResource(String paramString){
        try {
            File fileResource = new File(paramString);
            InputStream fileStream = new FileInputStream(fileResource);
            if (fileStream != null)
            {
                byte[] fileBytes = CommonUtils.readAll(fileStream);
                if (fileBytes.length > 0)
                {
                    byte[] fileEncBytes = this.data.encrypt(fileBytes);
                    return fileEncBytes;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
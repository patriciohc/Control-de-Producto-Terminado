import com.softlab.liblab.tools.Paths;
import net.sf.jasperreports.engine.JRDefaultScriptlet;

public class Rutas extends JRDefaultScriptlet{
    
    public static String getPathPartePt(String material){
        return Paths.getPathPartePt(material);
    }
    
    public static String getPathParteMp(String material){
        return Paths.getPathParteMp(material);
    }
    
    public static String getPathAnualMp(String material){
        return Paths.getPathAnualMp(material);
    }
    
    public static String getPathAnualPt(String material){
        return Paths.getPathAnualPt(material);
    }

    public static String getPathImage(String image) {
        return Paths.getPathImage(image);
    }
    
    public static String getPathTmp() {
        return Paths.getPathTmp();
    }
    
}

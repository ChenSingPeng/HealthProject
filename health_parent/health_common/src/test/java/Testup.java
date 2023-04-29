import com.singpeng.utils.QiniuUtils;
import org.junit.Test;

public class Testup {
    @Test
    public void testUp(){
        QiniuUtils.upload2Qiniu("C:\\Users\\Administrator\\Desktop\\屏幕截图 2023-04-23 152553.png","test55.png");
    }
}

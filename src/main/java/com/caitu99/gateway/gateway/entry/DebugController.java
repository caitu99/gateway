package com.caitu99.gateway.gateway.entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;


@Controller
public class DebugController {

    private static final Logger logger = LoggerFactory.getLogger(DebugController.class);

    /*@RequestMapping("/debug")
    @ResponseBody
    public String debug(Model model) throws IOException {
        InputStream is = new FileInputStream("/tmp/debug.class");
        byte[] bytes = new byte[is.available()];
        is.read(bytes);
        is.close();
        String result = JavaClassExecutor.execute(bytes);
        HackSystem.clearBuffer();
        ClassModifier cm = new ClassModifier(bytes);
        byte[] modifiedBytes = cm.modifyUTF8Constant("java/lang/System",
                "com/youzan/carmen/utils/debug/HackSystem");
        HotSwapClassLoader loader = new HotSwapClassLoader();
        Class clazz = loader.loadFromBytes(modifiedBytes);
        try {
            Method method = clazz.getMethod("main", new Class[]{String[].class});
            method.invoke(null, new String[]{null});
        } catch (Throwable throwable) {
            throwable.printStackTrace(HackSystem.out);
        }
        return HackSystem.getBufferString();
    }*/

}
